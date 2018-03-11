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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.alien8.audio.AudioEvent;
import org.alien8.audio.AudioManager;
import org.alien8.core.ClientMessage;
import org.alien8.core.EntityLite;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.rendering.Renderer;
import org.alien8.score.Score;
import org.alien8.score.ScoreBoard;
import org.alien8.score.ScoreEvent;
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
  private int FPS = 0;
  private int TICKS = 0;
  private InetAddress clientIP = null;
  private InetAddress serverIP = null;
  private InetAddress multiCastIP = null;
  private Integer clientUdpPort = null;
  private Socket tcpSocket = null;
  private DatagramSocket udpSocket = null;
  private MulticastSocket multiCastSocket = null;
  private byte[] buf = new byte[65536];
  private byte[] receivedByte;
  private byte[] sendingByte;

  public Client() {
    model = ModelManager.getInstance();
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
        Renderer.getInstance().render(model);
        
        tickRate++;
        catchUp--;
        // Update last time
        lastTime = getTime();
      }

      // Call the renderer
      frameRate++;

      // Update the FPS timer every FPS_FREQ^-1 seconds
      if (getTime() - frameTimer > Parameters.N_SECOND / Parameters.FPS_FREQ) {
        frameTimer += Parameters.N_SECOND / Parameters.FPS_FREQ;
        FPS = (frameRate * Parameters.FPS_FREQ + FPS) / 2;
        frameRate = 0;
        System.out.println("FPS: " + FPS);
      }
      if (getTime() - tickTimer > Parameters.N_SECOND) {
        tickTimer += Parameters.N_SECOND;
        TICKS = (TICKS + tickRate) / 2;
        System.out.println("Ticks: " + TICKS);
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
        tcpSocket = new Socket(serverIP, Parameters.SERVER_PORT);
        udpSocket = new DatagramSocket();
        clientUdpPort = udpSocket.getLocalPort();

        // Serialize a ClientMessage (connect) Object into byte array
        ClientMessage connectRequest = new ClientMessage(0, udpSocket.getLocalPort());
        ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(tcpSocket.getInputStream());
        toServer.writeObject(connectRequest);

        // Receive map seed from server
        Long seed = this.getMapSeed(fromServer);
        model.makeMap(seed);

        // Receive the initial game state from server
        ArrayList<EntityLite> entsLite = this.receiveGameStateTCP(fromServer);
        model.sync(entsLite, clientIP, clientUdpPort);

        // Serialize a ClientMessage (ready) Object into byte array
        ClientMessage ready = new ClientMessage(2, udpSocket.getLocalPort());
        toServer.writeObject(ready);

        // Set up multicast socket for receiving game states from server
        multiCastIP = InetAddress.getByName("224.0.0.5");
        multiCastSocket = new MulticastSocket(Parameters.MULTI_CAST_PORT);
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
      sendingByte = byteOut.toByteArray();

      // Create a packet for holding the input sample byte data
      DatagramPacket packet = new DatagramPacket(sendingByte, sendingByte.length, serverIP, Parameters.SERVER_PORT);

      // Send the client input sample packet to the server
      udpSocket.send(packet);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void receiveEvents() {
    try {
      // Create a packet for receiving event packet
      DatagramPacket eventPacket = new DatagramPacket(buf, buf.length);

      multiCastSocket.receive(eventPacket);
      receivedByte = eventPacket.getData();

      // Deserialize the event data into object
      ByteArrayInputStream byteIn = new ByteArrayInputStream(receivedByte);
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      GameEvent event = null;
      try {
        event = (GameEvent) objIn.readObject();
      } catch (ClassCastException e) {
        // Desync'd. Drop packet, move on
        LogManager.getInstance().log("Client", LogManager.Scope.ERROR,
            "Desync'd socket receive. Tried to cast ArrayList to GameEvent");
      }

      // Send audio events to AudioManager
      if (event != null) {
        if (event instanceof AudioEvent)
          AudioManager.getInstance().addEvent((AudioEvent) event);
        else if (event instanceof ScoreEvent) {
          ScoreBoard.getInstance().update((new Score((ScoreEvent) event)));
        }
      }
    } catch (SocketTimeoutException ste) {
      // Do nothing, just proceed
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }

  /*
   * Receive the game state from the server and sync the game state with the server (UDP)
   */
  private void receiveAndUpdate() {
    try {
      // Create a packet for receiving entsLite packet
      DatagramPacket packet = new DatagramPacket(buf, buf.length);

      // Receive a entsLite packet and obtain the byte data
      multiCastSocket.receive(packet);
      receivedByte = packet.getData();

      // Deserialize the entsLite byte data into object
      ByteArrayInputStream byteIn = new ByteArrayInputStream(receivedByte);
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      ArrayList<EntityLite> entsLite = null;
      try {
    	  entsLite = (ArrayList<EntityLite>) objIn.readObject();
      } catch (ClassCastException e) {
          // Desync'd. Drop packet, move on
          LogManager.getInstance().log("Client", LogManager.Scope.ERROR,
              "Desync'd socket receive. Tried to cast GameEvent to ArrayList");
        }
      if (entsLite != null)
        // Sync the game state with server
        ModelManager.getInstance().sync(entsLite, clientIP, clientUdpPort);

    } catch (SocketTimeoutException ste) {
      // Do nothing, just proceed
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }

  /*
   * Receive the game state from the server through TCP connection
   */
  private ArrayList<EntityLite> receiveGameStateTCP(ObjectInputStream fromServer) {
    ArrayList<EntityLite> entsLite = null;
    LinkedList<ScoreEvent> scores = null;

    try {
      entsLite = (ArrayList<EntityLite>) fromServer.readObject();
      scores = (LinkedList<ScoreEvent>) fromServer.readObject();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
    
    for(ScoreEvent score : scores)
    	ScoreBoard.getInstance().update(new Score(score));
    
    return entsLite;
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
   * Should be called when the client clicks the 'Exit' button from the in-game menu or closes the game window
   */
  public void disconnect() {
    if (clientIP != null && serverIP != null && multiCastIP != null && clientUdpPort != null
        && tcpSocket != null && udpSocket != null && multiCastSocket != null) {
      try {
        // Serialize a ClientRequest Object into byte array
        ClientMessage disconnectRequest = new ClientMessage(1, udpSocket.getLocalPort());
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

  // public boolean[][] getMapIceGrid() {
  // return model.getMap().getIceGrid();
  // }

}
