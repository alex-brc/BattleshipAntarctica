package org.alien8.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.alien8.ai.AIController;
import org.alien8.audio.AudioManager;
import org.alien8.core.ClientRequest;
import org.alien8.core.EntityLite;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.rendering.Renderer;
import org.alien8.score.Score;
import org.alien8.score.ScoreBoard;
import org.alien8.server.AudioEvent;
import org.alien8.server.GameEvent;
import org.alien8.ship.Ship;
import org.alien8.util.LogManager;

public class Client implements Runnable {
  /**
   * Volatile "running" boolean to avoid internal caching. Thread should stop cleanly when set to
   * false.
   */
  private volatile boolean running = false;
  private Thread thread;
  private ModelManager model;
  private AIController aiPlayer;
  private int FPS = 0;
  private InetAddress serverIP = null;
  private InetAddress multiServerIP = null;
  private int clientMultiPort = 4445;
  private Socket tcpSocket = null;
  private DatagramSocket udpSocket = null;
  private MulticastSocket multiReceiver = null;
  private String groupIPStr = "224.0.0.5";
  private String serverIPstr = "192.168.0.15"; // <- change to the ip of the server to test
  private ScoreBoard scoreBoard;

  public Client() {
    model = ModelManager.getInstance();
    scoreBoard = ScoreBoard.getInstance();
  }

  /**
   * Starts the main loop of the game.
   */
  public void start() {
    // Do nothing if the game is already running
    if (running)
      return;
    running = true;

    // Play the ambient music
    AudioManager.getInstance().startAmbient();

    LogManager.getInstance().log("Client", LogManager.Scope.INFO, "Booting client...");
    thread = new Thread(this, "Battleship Antarctica");
    thread.start();
    // Start the loop
  }

  public void stop() {
    running = false;
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * The main loop of the game. A common way to implement it. This implementation basically allows
   * the renderer to do it's job separately from the update() method. If a certain computer tends to
   * be slower on the render() side, then it can perform more fixed time updates in between frames
   * to compensate. Faster computers wouldn't see any improvement.
   */
  @Override
  public void run() {
    // Game loop goes here
	
    long lastTime = getTime();
    long currentTime = 0;
    double catchUp = 0;

    int frameRate = 0;
    long frameTimer = getTime();
    int tickRate = 0;
    long tickTimer = getTime();

    while (running) {
      currentTime = getTime();

      // Get the amount of update()s the model needs to catch up
      //
      // timeNow - timeLastUpdateWasDone -->
      // timeToCatchUp = ----------------------------------
      // deltaTPerTick --> how long a "tick" is
      //
      catchUp += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

      // Call update() as many times as needed to compensate before rendering
      while (catchUp >= 1) {
        this.sendInputSample();
        this.receiveAndUpdate();
        this.receiveEvents();
        tickRate++;
        catchUp--;
        // Update last time
        lastTime = getTime();
      }

      // Call the renderer
      // aiPlayer.update();
      Renderer.getInstance().render(model);
      frameRate++;

      // Update the FPS timer every FPS_FREQ^-1 seconds
      if (getTime() - frameTimer > Parameters.N_SECOND / Parameters.FPS_FREQ) {
        frameTimer += Parameters.N_SECOND / Parameters.FPS_FREQ;
        FPS = frameRate * Parameters.FPS_FREQ;
        frameRate = 0;
        System.out.println(FPS);
      }
      if (getTime() - tickTimer > Parameters.N_SECOND) {
        tickTimer += Parameters.N_SECOND;
        System.out.println(tickRate);
        tickRate = 0;
      }
    }
    System.out.println("stopped");
  }

  /**
   * Getter for the latest FPS estimation.
   * 
   * @return
   */
  public int getFPS() {
    return FPS;
  }

  /**
   * Pauses the game.
   */
  public void pause() {
    running = false;
  }

  /**
   * Gets current time in nanoseconds from the JVM
   * 
   * @return current time in nanoseconds
   */
  private long getTime() {
    return System.nanoTime();
  }

  /**
   * Should be called when the client clicks the 'Connect' button after entering an server IP.
   */
  public boolean connect(String serverIPStr) {
    if (tcpSocket == null && serverIP == null) {
      this.serverIPstr = serverIPStr;
      try {
        serverIP = InetAddress.getByName(serverIPStr);
        tcpSocket = new Socket(serverIP, 4446);
        udpSocket = new DatagramSocket();
        // eventSocket = new DatagramSocket();

        // Serialize a ClientRequest Object into byte array
        ClientRequest connectRequest = new ClientRequest(0, udpSocket.getLocalPort());
        ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(tcpSocket.getInputStream());
        toServer.writeObject(connectRequest);

        // Wait for a full snapshot of current game state from server
        ArrayList<EntityLite> initialEntitiesLite = getFullSnapshot(fromServer);
        Long seed = getMapSeed(fromServer);
        model.makeMap(seed);

        // Full sync the game state
        model.fullSync(initialEntitiesLite);

        // Client's Ship is stored at the end of the synced entities queue
        Object[] entitiesArr = model.getEntities().toArray();
        model.setPlayer((Ship) entitiesArr[entitiesArr.length - 1]);

        // set up multicast socket client receiver
        multiServerIP = InetAddress.getByName(groupIPStr);
        multiReceiver = new MulticastSocket(clientMultiPort);
        multiReceiver.joinGroup(multiServerIP);

      } catch (BindException e) {
        LogManager.getInstance().log("Client", LogManager.Scope.CRITICAL,
            "Could not bind to any port. Firewalls?\n" + e.toString());
        return false;
      } catch (SocketException e) {
        LogManager.getInstance().log("Client", LogManager.Scope.CRITICAL,
            "A socket exception occured.\n" + e.toString());
        return false;
      } catch (UnknownHostException e) {
        LogManager.getInstance().log("Client", LogManager.Scope.CRITICAL,
            "Unknown host. Check host details.\n" + e.toString());
        return false;
      } catch (IOException e) {
        LogManager.getInstance().log("Client", LogManager.Scope.CRITICAL,
            "IO exception.\n" + e.toString());
        return false;
      }
      LogManager.getInstance().log("Client", LogManager.Scope.INFO,
          "Client succesfully connected to the server at " + serverIPStr);
      return true;
    }
    System.out.println("The client is already connected.");
    LogManager.getInstance().log("Client", LogManager.Scope.WARNING,
        "Connection attempted while already connected. ???");
    return false;
  }

  private void sendInputSample() {
    try {
      // Serialize the input sample object into byte array
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
      ClientInputSample cis = new ClientInputSample();
      objOut.writeObject(cis);
      byte[] clientInputSampleByte = byteOut.toByteArray();

      // Create a packet for holding the input sample byte data
      DatagramPacket packet =
          new DatagramPacket(clientInputSampleByte, clientInputSampleByte.length, serverIP, 4446);

      // Send the client input sample packet to the server
      udpSocket.send(packet);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void receiveEvents() {
	  try {
		  // Create a packet for receiving event packet
		  byte[] buf = new byte[65536];
		  DatagramPacket eventPacket = new DatagramPacket(buf, buf.length);

		  multiReceiver.receive(eventPacket);
		  byte[] eventBytes = eventPacket.getData();

		  // Deserialize the event data into object
		  ByteArrayInputStream byteIn = new ByteArrayInputStream(eventBytes);
		  ObjectInputStream objIn = new ObjectInputStream(byteIn);
		  GameEvent event = (GameEvent) objIn.readObject();

		  // Send audio events to AudioManager
		  if (event != null) {
			  System.out.println(event.toString());
			  if (event instanceof AudioEvent)
				  AudioManager.getInstance().addEvent((AudioEvent) event);
			  else if (event instanceof Score)
				  ScoreBoard.getInstance().update((Score) event);
		  }
	  } catch (IOException ioe) {
		  ioe.printStackTrace();
	  } catch (ClassNotFoundException cnfe) {
		  cnfe.printStackTrace();
	  }
  }

  /*
   * Receive the game state difference from the server and sync the game state with the server
   */
  public void receiveAndUpdate() {
    try {
      // Create a packet for receiving difference packet
      byte[] buf = new byte[65536];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);

      // Receive a difference packet and obtain the byte data
      multiReceiver.receive(packet);
      byte[] differenceByte = packet.getData();

      // Deserialize the difference byte data into object
      ByteArrayInputStream byteIn = new ByteArrayInputStream(differenceByte);
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      ArrayList<EntityLite> difference = (ArrayList<EntityLite>) objIn.readObject();

      // Sync the game state with server
      ModelManager.getInstance().sync(difference);
      
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }

  private ArrayList<EntityLite> getFullSnapshot(ObjectInputStream fromServer) {
    ArrayList<EntityLite> fullSnapShot = null;

    try {
      fullSnapShot = (ArrayList<EntityLite>) fromServer.readObject();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }

    return fullSnapShot;
  }

  private Long getMapSeed(ObjectInputStream fromServer) {
    Long seed = null;
    try {
      seed = (Long) fromServer.readObject();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }

    return seed;
  }

  /**
   * Should be called when the client clicks the 'Exit' button from the in-game menu. TODO
   */
  public void disconnect() {
    if (tcpSocket != null && serverIP != null) {
      try {
        // Serialize a FALSE Boolean object (representing disconnect request) into byte array
        Boolean disconnectRequest = new Boolean(false);
        ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());

        // Send the disconnect request
        toServer.writeObject(disconnectRequest);

        // Reset the socket, serverIP and client-side threads after disconnecting
        tcpSocket = null;
        udpSocket = null;
        multiReceiver = null;
        serverIP = null;
      } catch (IOException e) {
        LogManager.getInstance().log("Client", LogManager.Scope.ERROR,
            "Something went wrong disconnecting client. " + e.toString());
      }
    }
  }

}
