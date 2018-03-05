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
  private InetAddress clientIP = null;
  private InetAddress serverIP = null;
  private InetAddress multiCastIP = null;
  private Integer clientUdpPort = null;
  private Integer serverPort = 4446;
  private int multiCastPort = 4445;
  private Socket tcpSocket = null;
  private DatagramSocket udpSocket = null;
  private MulticastSocket multiCastSocket = null;
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
        //System.out.println(FPS);
      }
      if (getTime() - tickTimer > Parameters.N_SECOND) {
        tickTimer += Parameters.N_SECOND;
        //System.out.println(tickRate);
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
    if (clientIP == null && serverIP == null && multiCastIP == null && clientUdpPort == null
        && tcpSocket == null && udpSocket == null && multiCastSocket == null) {
      try {
        clientIP = InetAddress.getLocalHost();
        serverIP = InetAddress.getByName(serverIPStr);
        tcpSocket = new Socket(serverIP, serverPort);
        udpSocket = new DatagramSocket();
        clientUdpPort = udpSocket.getLocalPort();
        // eventSocket = new DatagramSocket();

        // Serialize a ClientRequest Object into byte array
        ClientRequest connectRequest = new ClientRequest(0, udpSocket.getLocalPort());
        ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(tcpSocket.getInputStream());
        toServer.writeObject(connectRequest);

        // Receive map seed from server
        Long seed = getMapSeed(fromServer);
        model.makeMap(seed);
        
        // Receive the initial game state from server
        ArrayList<EntityLite> entsLite = receiveGameStateTCP(fromServer);
        model.sync(entsLite, clientIP, clientUdpPort);

        // Set up multicast socket for receiving game states from server
        multiCastIP = InetAddress.getByName("224.0.0.5");
        multiCastSocket = new MulticastSocket(multiCastPort);
        multiCastSocket.joinGroup(multiCastIP);
        
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

      multiCastSocket.receive(eventPacket);
      byte[] eventBytes = eventPacket.getData();

      // Deserialize the event data into object
      ByteArrayInputStream byteIn = new ByteArrayInputStream(eventBytes);
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      GameEvent event = (GameEvent) objIn.readObject();

      // Send audio events to AudioManager
      if (event != null) {
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

      // Receive a entsLite packet and obtain the byte data
      multiCastSocket.receive(packet);
      byte[] differenceByte = packet.getData();

      // Deserialize the entsLite byte data into object
      ByteArrayInputStream byteIn = new ByteArrayInputStream(differenceByte);
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      ArrayList<EntityLite> entsLite = (ArrayList<EntityLite>) objIn.readObject();

      // Sync the game state with server
      ModelManager.getInstance().sync(entsLite, clientIP, clientUdpPort);

    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }

  private ArrayList<EntityLite> receiveGameStateTCP(ObjectInputStream fromServer) {
    ArrayList<EntityLite> gameState = null;

    try {
      gameState = (ArrayList<EntityLite>) fromServer.readObject();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }

    return gameState;
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
   * Should be called when the client clicks the 'Exit' button from the in-game menu.
   */
  public void disconnect() {
    if (clientIP != null && serverIP != null && multiCastIP != null && clientUdpPort != null
        && tcpSocket != null && udpSocket != null && multiCastSocket != null) {
      try {
        // Serialize a ClientRequest Object into byte array
        ClientRequest disconnectRequest = new ClientRequest(1, udpSocket.getLocalPort());
        ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());

        // Send the disconnect request
        toServer.writeObject(disconnectRequest);

        // Reset every networking related field
        clientIP = null;
        serverIP = null;
        multiCastIP = null;
        clientUdpPort = null;
        tcpSocket = null;
        udpSocket = null;
        multiCastSocket = null;
      } catch (IOException e) {
        LogManager.getInstance().log("Client", LogManager.Scope.ERROR,
            "Something went wrong disconnecting client. " + e.toString());
      }
    }
  }

}
