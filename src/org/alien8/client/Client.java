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
import java.util.LinkedList;

import org.alien8.audio.AudioEvent;
import org.alien8.audio.AudioManager;
import org.alien8.core.ClientMessage;
import org.alien8.core.ClientModelManager;
import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.rendering.Renderer;
import org.alien8.score.ClientScoreBoard;
import org.alien8.score.Score;
import org.alien8.score.ScoreEvent;
import org.alien8.server.GameEvent;
import org.alien8.server.Server;
import org.alien8.server.Timer;
import org.alien8.server.TimerEvent;
import org.alien8.ui.Lobby;
import org.alien8.ui.MainMenu;
import org.alien8.ui.NameScreen;
import org.alien8.ui.SettingsMenu;
import org.alien8.ui.SplashScreen;
import org.alien8.util.LogManager;

/*
 * A singleton game client, loops and do nothing when not in game (gameRunning == false).
 */
public class Client implements Runnable {
  /**
   * Volatile "running" boolean to avoid internal caching. Thread should stop cleanly when set to
   * false.
   */
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
  private LinkedList<String> opponents = new LinkedList<String>();
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

  public enum State {
	  NAME_SCREEN, MAIN_MENU, SPLASH_SCREEN, IN_GAME, SETTINGS_MENU, IN_LOBBY
  }

  private State state = State.SPLASH_SCREEN;

  private ClientTCP clientTCP;
  private byte[] buf = new byte[65536];
  private byte[] receivedByte;
  private byte[] sendingByte;

  private Client() {
    model = ClientModelManager.getInstance();
    nameScreen = new NameScreen();
    splash = new SplashScreen();
    menu = new MainMenu();
    settings = new SettingsMenu();
    lobby = new Lobby();
  }

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

    while (running) {
      switch (state) {
        case SPLASH_SCREEN:
          Renderer.getInstance().render(splash);
          break;
        case NAME_SCREEN:
          Renderer.getInstance().render(nameScreen);
          break;
        case MAIN_MENU:
          Renderer.getInstance().render(menu);
          break;
        case SETTINGS_MENU:
          Renderer.getInstance().render(settings);
    	  break;
        case IN_LOBBY:
          Renderer.getInstance().render(lobby);
          break;
        case IN_GAME:
          // Play the ambient music
          AudioManager.getInstance().startAmbient();
        	
          long lastTime = getTime();
          long currentTime = 0;
          double catchUp = 0;

          int frameRate = 0;
          long frameTimer = getTime();

          while (gameRunning) {
            if (playersCompeting && !waitingToExit) {
              currentTime = getTime();

              // Get the amount of update()s the model needs to catch up
              //
              // 				  timeNow - timeLastUpdateWasDone    --> time elapsed
              // timeToCatchUp = ----------------------------------
              // 							deltaTPerTick            --> how long a "tick" is
              //
              catchUp +=
                  (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

              // Call update() as many times as needed to compensate before rendering
              while (catchUp >= 1) {
                try {
                  this.sendInputSample();
                  this.receivePacket();
                  this.receivePacket();
                } catch (IOException e) {
                  // Do nothing, if reached here playersCompeting && waitingToExit should be set
                  // false
                }

                catchUp--;
                // Update last time
                lastTime = getTime();
              }
            }
            Renderer.getInstance().render(model);
            frameRate++;

            // Update the FPS timer every FPS_FREQ^-1 seconds
            if (getTime() - frameTimer > Parameters.N_SECOND / Parameters.FPS_FREQ) {
              frameTimer += Parameters.N_SECOND / Parameters.FPS_FREQ;
              FPS = (frameRate * Parameters.FPS_FREQ + FPS) / 2;
              frameRate = 0;
              // System.out.println(FPS);
            }
            if (waitingToExit && !playersCompeting) {
              Renderer.getInstance().render(model);
            }
          }
          AudioManager.getInstance().stopAmbient();
          break;
	default:
		break;
      }
    }
    System.out.println("Client stopped");
  }

  public Timer getTimer() {
    return this.timer;
  }
  
  public MainMenu getMenu() {
    return this.menu;
  }

  public NameScreen getNameScreen() {
	return this.nameScreen;
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

  public void waitToExit() {
    waitingToExit = true;
    playersCompeting = false;
    udpSocket.close();
    multiCastSocket.close();
  }

  public boolean isWaitingToExit() {
    return waitingToExit;
  }
  
  public void createServer(int maxPlayer) {
    Server.getInstance().setMaxPlayer(maxPlayer);
    Server.getInstance().start();
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
        ClientMessage ready = new ClientMessage(1, udpSocket.getLocalPort());
        toServer.writeObject(ready);

        // Set up multicast socket for receiving game states from server
        multiCastIP = InetAddress.getByName("224.0.0.5");
        multiCastSocket = new MulticastSocket(Parameters.MULTI_CAST_PORT);
        multiCastSocket.joinGroup(multiCastIP);

        clientTCP = new ClientTCP(fromServer);
        clientTCP.start();
        gameRunning = true;
        playersCompeting = true;

      } catch (BindException e) {
        LogManager.getInstance().log("Client", LogManager.Scope.CRITICAL,
            "Could not bind to any port. Firewalls?\n" + e.toString());
        return false;
      } catch (SocketException e) {
        System.out.println("Server " + serverIPStr + " didn't response");
        this.disconnect();
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
      this.receiveEvents((GameEvent) receivedObject);
  }

  private void receiveEvents(GameEvent event) {
    // Send audio events to AudioManager
    if (event != null) {
      if (event instanceof AudioEvent)
        AudioManager.getInstance().addEvent((AudioEvent) event);
      else if (event instanceof ScoreEvent) {
        ClientScoreBoard.getInstance().update((new Score((ScoreEvent) event)));
      } else if (event instanceof TimerEvent) {
        timer = new Timer((TimerEvent) event);
      }
    }
  }

  /*
   * Receive the game state from the server and sync the game state with the server (UDP)
   */
  private void updateGameState(ArrayList<EntityLite> entsLite) {
    if (entsLite != null)
      // Sync the game state with server
      ClientModelManager.getInstance().sync(entsLite, clientIP, clientUdpPort);
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

    for (ScoreEvent score : scores)
      ClientScoreBoard.getInstance().update(new Score(score));

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

  public void setTimeBeforeExiting(int t) {
    this.timeBeforeExiting = t;
  }

  public int getTimeBeforeExiting() {
    return this.timeBeforeExiting;
  }

  /**
   * Should be called when the client clicks the 'Exit' button from the in-game menu or closes the
   * game window
   */
  public void disconnect() {
    try {
      if (clientTCP != null)
        clientTCP.end();
      gameRunning = false;
      playersCompeting = false;
      waitingToExit = false;
      this.setState(State.MAIN_MENU);
      if (tcpSocket != null)
        tcpSocket.close();
      if (udpSocket != null)
        udpSocket.close();
      if (multiCastSocket != null)
        multiCastSocket.close();

      // Reset relevant field
      model.reset();
      ClientScoreBoard.getInstance().reset();
      timer = null;
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
    } catch (IOException e) {
      // Trying to close a closed socket, it's ok just proceed
    }
  }

  public void setState(State s) {
    state = s;
  }

  /**
   * @return the client's name
   */
  public String getClientName() {
	  return clientName;
  }

  /**
   * @param clientName the client name to set
   */
  public void setClientName(String clientName) {
	  this.clientName = clientName;
	  this.opponents.add(clientName);
  }

  /**
   * @return the opponents' names
   */
  public LinkedList<String> getOpponents() {
	  return opponents;
  }
  
  public Lobby getLobby() {
	return lobby;
  }

}
