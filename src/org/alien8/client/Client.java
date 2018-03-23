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
import org.alien8.core.ClientModelManager;
import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Wreck;
import org.alien8.score.ClientScoreBoard;
import org.alien8.score.Score;
import org.alien8.score.ScoreEvent;
import org.alien8.server.GameEvent;
import org.alien8.server.KillEvent;
import org.alien8.server.Server;
import org.alien8.server.Timer;
import org.alien8.server.TimerEvent;
import org.alien8.ui.CreditsPage;
import org.alien8.ui.Lobby;
import org.alien8.ui.MainMenu;
import org.alien8.ui.NameScreen;
import org.alien8.ui.SettingsMenu;
import org.alien8.ui.SplashScreen;
import org.alien8.util.LogManager;

public class Client implements Runnable {

  // All possible client UI states
  public enum State {
    NAME_SCREEN, MAIN_MENU, SPLASH_SCREEN, IN_GAME, SETTINGS_MENU, IN_LOBBY, CREDITS_SCREEN
  }

  private State state = State.SPLASH_SCREEN;
  private volatile boolean running = false;
  private boolean gameRunning = false;
  private boolean playersCompeting = false;
  private boolean waitingToExit = false;
  private static Client instance;
  private Thread thread;
  private ClientModelManager model;
  private Timer timer;
  private int timeBeforeExiting = 10;
  private int FPS = 0;
  private String clientName = null;
  private InetAddress clientIP = null;
  private InetAddress serverIP = null;
  private InetAddress multiCastIP = null;
  private Integer clientUdpPort = null;
  private Socket tcpSocket = null;
  private DatagramSocket udpSocket = null;
  private MulticastSocket multiCastSocket = null;
  private SplashScreen splash = null;
  private MainMenu menu = null;
  private SettingsMenu settings = null;
  private NameScreen nameScreen = null;
  private Lobby lobby = null;
  private CreditsPage credits = null;
  private ClientTCP clientTCP;
  private byte[] buf = new byte[65536];
  private byte[] receivedByte;
  private byte[] sendingByte;

  /**
   * Private constructor to prevent global instantiation
   */
  private Client() {
    model = ClientModelManager.getInstance();
    nameScreen = new NameScreen();
    splash = new SplashScreen();
    menu = new MainMenu();
    settings = new SettingsMenu();
    lobby = new Lobby();
    credits = new CreditsPage();
  }

  /**
   * Create and return a Client instance the first time being called, only return the instance
   * afterwards
   * 
   * @return A Client instance
   */
  public static Client getInstance() {
    if (instance == null)
      instance = new Client();
    return instance;
  }

  /**
   * Starts the main loop of the game.
   */
  public void start() {
    // Do nothing if the game is already running
    if (running)
      return;
    running = true;

    // Make a timer
    timer = new Timer(0);

    LogManager.getInstance().log("Client", LogManager.Scope.INFO, "Booting client...");
    thread = new Thread(this, "Battleship Antarctica");
    thread.start();
    // Start the loop
  }

  /**
   * The main loop of the client's UI
   */
  @Override
  public void run() {
    while (running) {
      switch (state) {
        case SPLASH_SCREEN:
          Renderer.getInstance().render(splash);
          break;
        case NAME_SCREEN:
          Renderer.getInstance().render(nameScreen);
          break;
        case MAIN_MENU:
          AudioManager.getInstance().startAmbient(0);
          AudioManager.getInstance().stopAmbient(1);
          Renderer.getInstance().render(menu);
          break;
        case SETTINGS_MENU:
          Renderer.getInstance().render(settings);
          break;
        case IN_LOBBY:
          Renderer.getInstance().render(lobby);
          break;
        case CREDITS_SCREEN:
          Renderer.getInstance().render(credits);
          break;
        /**
         * The main loop of the game. A common way to implement it. This implementation basically
         * allows the renderer to do it's job separately from the update() method. If a certain
         * computer tends to be slower on the render() side, then it can perform more fixed time
         * updates in between frames to compensate. Faster computers wouldn't see any improvement.
         */
        case IN_GAME:
          AudioManager.getInstance().stopAmbient(0);
          // Play the ambient music
          AudioManager.getInstance().startAmbient(1);

          // Setup up variables for calculating time elapsed and ticks
          long lastTime = getTime();
          long currentTime = 0;
          double catchUp = 0;

          try {
            while (gameRunning) {
              // The part is executed only when the match is ongoing
              if (playersCompeting && !waitingToExit) {
                // Get the current time
                currentTime = getTime();

                // Get the amount of update()s the model needs to catch up
                catchUp +=
                    (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

                // Call update() as many times as needed to compensate before rendering
                while (catchUp >= 1) {
                  this.sendInputSample();
                  this.receivePacket();
                  this.receivePacket();
                  catchUp--;

                  // Update last time
                  lastTime = getTime();
                }
              }

              // Render the game world as often as possible
              Renderer.getInstance().render(model);
            }

            // Stop the ambient music
            AudioManager.getInstance().stopAmbient(0);
            AudioManager.getInstance().stopAmbient(1);

          } catch (IOException ioe) {
            // Do nothing just proceed and end the game loop
          }
          break;
        default:
          break;
      }
    }
  }

  /**
   * Send an input sample to the game server
   * 
   * @throws IOException if UDP socket is closed or other I/O errors
   */
  private void sendInputSample() throws IOException {
    try {
      // Serialize the input sample object into byte array
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
      ClientInputSample cis = new ClientInputSample();
      objOut.writeObject(cis);
      sendingByte = byteOut.toByteArray();

      // Create a packet for holding the input sample byte data
      DatagramPacket packet =
          new DatagramPacket(sendingByte, sendingByte.length, serverIP, Parameters.SERVER_PORT);

      // Send the client input sample packet to the server
      udpSocket.send(packet);
    } catch (IOException ioe) { // UdpSocket closed
      throw ioe;
    }
  }

  /**
   * Receive a packet from the game server
   * 
   * @throws IOException if UDP socket is closed or other I/O errors
   */
  private void receivePacket() throws IOException {
    Object receivedObject = null;
    try {
      // Create a packet for receiving entsLite packet
      DatagramPacket packet = new DatagramPacket(buf, buf.length);

      // Receive a entsLite packet and obtain the byte data
      multiCastSocket.receive(packet);
      receivedByte = packet.getData();

      // Deserialize the entsLite byte data into object
      ByteArrayInputStream byteIn = new ByteArrayInputStream(receivedByte);
      ObjectInputStream objIn = new ObjectInputStream(byteIn);
      receivedObject = objIn.readObject();
    } catch (IOException ioe) { // multiCastsocket closed
      throw ioe;
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
    if (receivedObject == null)
      return;
    if (receivedObject instanceof ArrayList<?>)
      this.updateGameState((ArrayList<EntityLite>) receivedObject);
    else if (receivedObject instanceof GameEvent)
      this.updateEvent((GameEvent) receivedObject);
  }

  /**
   * Update the game event
   * 
   * @param event The game event object
   */
  private void updateEvent(GameEvent event) {
    // Send audio events to AudioManager
    if (event != null) {
      if (event instanceof AudioEvent)
        AudioManager.getInstance().addEvent((AudioEvent) event);
      else if (event instanceof ScoreEvent) {
        ClientScoreBoard.getInstance().update((new Score((ScoreEvent) event)));
      } else if (event instanceof TimerEvent) {
        timer = new Timer((TimerEvent) event);
      } else if (event instanceof KillEvent) {
    	Renderer.getInstance().addWreck((KillEvent) event);
      }
    }
  }

  /**
   * Update the game state
   * 
   * @param entsLite An arraylist of EntityLite (A full snapshot of game state)
   */
  private void updateGameState(ArrayList<EntityLite> entsLite) {
    if (entsLite != null)
      // Sync the game state with server
      ClientModelManager.getInstance().sync(entsLite, clientIP, clientUdpPort);
  }

  /**
   * Create a game server
   * 
   * @param maxPlayer Maximum number of player of the game server
   */
  public void createServer(int maxPlayer) {
    Server.getInstance().setMaxPlayer(maxPlayer);
    Server.getInstance().start();
  }

  /**
   * Do a connection attempt to the specified game server
   * 
   * @param serverIPStr The IP address of the game server
   * @return true if the connection is successful, false otherwise
   */
  public boolean connect(String serverIPStr) {
    if (clientIP == null && serverIP == null && multiCastIP == null && clientUdpPort == null
        && tcpSocket == null && udpSocket == null && multiCastSocket == null) {
      try {
        clientIP = InetAddress.getLocalHost();
        serverIP = InetAddress.getByName(serverIPStr);
        
        tcpSocket = new Socket(serverIP, Parameters.SERVER_PORT);
        tcpSocket.setSoTimeout(2000);
        udpSocket = new DatagramSocket();
        clientUdpPort = udpSocket.getLocalPort();

        // Serialize a ClientMessage (connect) Object into byte array
        ClientMessage connectRequest = new ClientMessage(0, udpSocket.getLocalPort(), clientName);
        ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(tcpSocket.getInputStream());
        toServer.writeObject(connectRequest);

        // Receive map seed from server
        Long seed = this.getMapSeed(fromServer);
        model.makeMap(seed);

        // Receive the initial game state from server\
        ArrayList<EntityLite> entsLite = this.receiveGameStateTCP(fromServer);
        model.sync(entsLite, clientIP, clientUdpPort);

        // Serialize a ClientMessage (ready) Object into byte array
        ClientMessage ready = new ClientMessage(1, udpSocket.getLocalPort(), clientName);
        toServer.writeObject(ready);

        // Set up multicast socket for receiving game states from server
        multiCastIP = InetAddress.getByName("224.0.0.5");
        multiCastSocket = new MulticastSocket(Parameters.MULTI_CAST_PORT);
        multiCastSocket.joinGroup(multiCastIP);

        tcpSocket.setSoTimeout(0);
        clientTCP = new ClientTCP(fromServer);
        clientTCP.start();
        gameRunning = true;
        playersCompeting = true;
        System.out.println("ok");

      } catch (SocketTimeoutException e) {
        this.disconnect();
        return false;
      } catch (BindException e) {
        System.out.println("Could not bind to " + serverIPStr);
        this.disconnect();
        return false;
      } catch (SocketException e) {
        System.out.println("Server " + serverIPStr + " didn't response");
        this.disconnect();
        return false;
      } catch (UnknownHostException e) {
        System.out.println("Unknown host " + serverIPStr);
        this.disconnect();
        return false;
      } catch (IOException e) {
        System.out.println("I/O error " + serverIPStr);
        this.disconnect();
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

  /**
   * Receive a full snapshot of game state from server through a TCP channel
   * 
   * @param fromServer Input stream for reading data from server
   * @return An arraylist of EntityLite (A full snapshot of game state)
   * @throws SocketTimeoutException if the client didn't receive the game state within a specified
   *         time
   * @throws IOException if connection with server is broken or other I/O errors
   */
  private ArrayList<EntityLite> receiveGameStateTCP(ObjectInputStream fromServer)
      throws SocketTimeoutException, IOException {
    ArrayList<EntityLite> entsLite = null;
    LinkedList<ScoreEvent> scores = null;

    try {
      entsLite = (ArrayList<EntityLite>) fromServer.readObject();
      scores = (LinkedList<ScoreEvent>) fromServer.readObject();
    } catch (SocketTimeoutException ste) {
      throw ste;
    } catch (IOException ioe) {
      throw ioe;
    } catch (ClassNotFoundException cnfe) {
      LogManager.getInstance().log("Client", LogManager.Scope.CRITICAL,
          "Cannot find the class ArrayList<EntityLite>");
      cnfe.printStackTrace();
    }

    for (ScoreEvent score : scores)
      ClientScoreBoard.getInstance().update(new Score(score));

    return entsLite;
  }

  /**
   * 
   * @param fromServer Input stream for reading data from server
   * @return The map seed (A random long)
   * @throws SocketTimeoutException if the client didn't receive the game state within a specified
   *         time
   * @throws IOException if connection with server is broken or other I/O errors
   */
  private Long getMapSeed(ObjectInputStream fromServer) throws SocketTimeoutException, IOException {
    Long seed = null;
    try {
      seed = (Long) fromServer.readObject();
    } catch (SocketTimeoutException ste) {
      throw ste;
    } catch (IOException ioe) {
      throw ioe;
    } catch (ClassNotFoundException cnfe) {
      LogManager.getInstance().log("Client", LogManager.Scope.CRITICAL,
          "Cannot find the class Long");
      cnfe.printStackTrace();
    }

    return seed;
  }

  /**
   * Disconnect the client, reset game state / network related fields
   */
  public void disconnect() {
    try {
      // Stop the client TCP thread
      if (clientTCP != null)
        clientTCP.end();

      // Client not in game anymore
      gameRunning = false;
      playersCompeting = false;
      waitingToExit = false;

      // Set client's state to MAIN_MENU to allow renderer to render the main menu
      this.setState(State.MAIN_MENU);

      // Close all sockets
      if (tcpSocket != null)
        tcpSocket.close();
      if (udpSocket != null)
        udpSocket.close();
      if (multiCastSocket != null)
        multiCastSocket.close();

      // Reset relevant field
      model.reset();
      ClientScoreBoard.getInstance().reset();
      Renderer.getInstance().wrecks = new LinkedList<Wreck>();
      timer = null;
      timeBeforeExiting = 10;
      FPS = 0;
      clientIP = null;
      serverIP = null;
      multiCastIP = null;
      clientUdpPort = null;
      tcpSocket = null;
      udpSocket = null;
      multiCastSocket = null;
      clientTCP = null;
      buf = new byte[65536];
      receivedByte = null;
      sendingByte = null;
      lobby.setNotHost();
    } catch (IOException e) {
      // Trying to close a closed socket, it's ok just proceed
    }
  }

  /**
   * Make the client to be in a state of "waiting to exit the match"
   */
  public void waitToExit() {
    waitingToExit = true;
    playersCompeting = false;
    udpSocket.close();
    multiCastSocket.close();
  }

  /**
   * Check if the client is waiting to exit the match
   * 
   * @return true if the client is waiting to exit the match, false otherwise
   */
  public boolean isWaitingToExit() {
    return waitingToExit;
  }
  
  /**
   * Set the time remaining before exiting the match
   * @param t The time remaining before exiting the match
   */
  public void setTimeBeforeExiting(int t) {
    this.timeBeforeExiting = t;
  }

  /**
   * Get the time remaining before exiting the match
   * @param t The time remaining before exiting the match
   */
  public int getTimeBeforeExiting() {
    return this.timeBeforeExiting;
  }

  /**
   * Gets the match timer
   * 
   * @return The timer
   */
  public Timer getTimer() {
    return this.timer;
  }

  /**
   * Gets the main menu
   * 
   * @return The main menu
   */
  public MainMenu getMenu() {
    return this.menu;
  }

  /**
   * Gets the name screen
   * 
   * @return The name screen
   */
  public NameScreen getNameScreen() {
    return this.nameScreen;
  }

  /**
   * Get the lobby
   * 
   * @return The lobby
   */
  public Lobby getLobby() {
    return lobby;
  }

  /**
   * Gets the client's name
   * 
   * @return The client's name
   */
  public String getClientName() {
    return clientName;
  }

  /**
   * Set the client's name
   * 
   * @param clientName The client name to set
   */
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  /**
   * Set the client's state
   * 
   * @param s The state to set
   */
  public void setState(State s) {
    state = s;
  }

  /**
   * Get the client's state
   * 
   * @return Client's state
   */
  public State getState() {
    return this.state;
  }

  /**
   * Get the latest FPS estimation
   * 
   * @return The latest FPS estimation
   */
  public int getFPS() {
    return FPS;
  }

  /**
   * Gets current time in nanoseconds from the JVM
   * 
   * @return current time in nanoseconds
   */
  private long getTime() {
    return System.nanoTime();
  }

}
