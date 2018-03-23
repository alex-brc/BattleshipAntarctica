package org.alien8.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.ai.AIController;
import org.alien8.client.ClientInputSample;
import org.alien8.core.ClientMessage;
import org.alien8.core.Entity;
import org.alien8.core.ServerModelManager;
import org.alien8.drops.PlaneDropper;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.score.ServerScoreBoard;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.alien8.util.LogManager;

public class Server implements Runnable {

  private static Server instance;
  private Thread thread;
  private InetAddress hostIP = null;
  private InetAddress multiCastIP = null;
  private ServerSocket tcpSocket = null;
  private DatagramSocket udpSocket = null;
  private ServerModelManager model = ServerModelManager.getInstance();
  private ConcurrentLinkedQueue<Entity> entities = model.getEntities();
  private ConcurrentHashMap<Player, ClientInputSample> latestCIS =
      new ConcurrentHashMap<Player, ClientInputSample>();
  private ConcurrentHashMap<Ship, AIController> aiMap = new ConcurrentHashMap<Ship, AIController>();
  private ConcurrentHashMap<Ship, Player> playerMap = new ConcurrentHashMap<Ship, Player>();
  private ArrayList<Player> playerList = new ArrayList<Player>();
  private ArrayList<ClientHandler> chList = new ArrayList<ClientHandler>();
  private LinkedList<GameEvent> events = new LinkedList<GameEvent>();
  private ServerGameHandler sgh = null;
  private Long seed = (new Random()).nextLong();
  private ServerScoreBoard scoreboard = ServerScoreBoard.getInstance();
  private Integer maxPlayer;
  private volatile LinkedList<Bullet> bullets = new LinkedList<Bullet>();

  /**
   * Private constructor to prevent global instantiation
   */
  private Server() {

  }

  /**
   * Create and return a server instance the first time being called, only return the instance
   * afterwards
   * 
   * @return A Server instance
   */
  public static Server getInstance() {
    if (instance == null)
      instance = new Server();
    return instance;
  }

  /**
   * Start the server thread
   */
  public void start() {
    // Reset all game state / network related fields before starting
    this.reset();
    thread = new Thread(this, "Battleship Antarctica Server");
    thread.start();
  }

  /**
   * Stop the server thread
   */
  public void stop() {
    udpSocket.close();
    try {
      /**
       * Would make tcpSocket.accept() to throw SocketException + all ClientHandlers would throw
       * IOException
       */
      tcpSocket.close();

    } catch (IOException ioe) {
      System.out.println("Can't close TCP socket");
    }
  }

  /**
   * Set the maximum number of players for the game server
   * 
   * @param max Maximum number of players
   */
  public void setMaxPlayer(int max) {
    this.maxPlayer = max;
  }

  /**
   * Reset all game state / network related fields
   */
  public void reset() {
    model.reset();
    thread = null;
    hostIP = null;
    multiCastIP = null;
    tcpSocket = null;
    udpSocket = null;
    entities = model.getEntities();
    latestCIS = new ConcurrentHashMap<Player, ClientInputSample>();
    aiMap = new ConcurrentHashMap<Ship, AIController>();
    playerMap = new ConcurrentHashMap<Ship, Player>();
    playerList = new ArrayList<Player>();
    chList = new ArrayList<ClientHandler>();
    events = new LinkedList<GameEvent>();
    sgh = null;
    seed = (new Random()).nextLong();
    scoreboard.reset();
    bullets = new LinkedList<Bullet>();
  }

  /**
   * The server main loop for accepting connection request
   */
  @Override
  public void run() {
    this.initializeGameState();
    this.setHostIP();
    try {
      tcpSocket = new ServerSocket(Parameters.SERVER_PORT, 50, hostIP);
      udpSocket = new DatagramSocket(Parameters.SERVER_PORT, hostIP);
      udpSocket.setSoTimeout(Parameters.SERVER_SOCKET_BLOCK_TIME);
      System.out.println("TCP socket Port: " + tcpSocket.getLocalPort());
      System.out.println("TCP socket IP: " + tcpSocket.getInetAddress());
      System.out.println("UDP socket Port: " + udpSocket.getLocalPort());
      System.out.println("UDP socket IP: " + udpSocket.getLocalAddress());

      // Process clients' connect/disconnect request
      for (int i = 0; i < maxPlayer; i++) {
        // Receive and process client's packet
        LogManager.getInstance().log("Server", LogManager.Scope.INFO,
            "Waiting for client request...");
        System.out.println("accepting..");
        Socket client = tcpSocket.accept();
        InetAddress clientIP = client.getInetAddress();
        ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
        ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
        ClientMessage cr = (ClientMessage) fromClient.readObject();
        if (sgh == null)
          processClientMessage(clientIP, cr, toClient, fromClient);
        else
          break;
      }

    } catch (SocketException se) {
      // Do nothing, just let this thread stops
    } catch (IOException ioe) {
      LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL,
          "Something wrong with the TCP connection");
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL,
          "Cannot find the class ClientMessage");
      cnfe.printStackTrace();
    }

    System.out.println("Server no longer accept client connection");
  }
  
  /**
   * @return true if the game is running, false otherwise
   */
  public boolean gameRunning() {
	  return sgh.running();
  }
  
  /**
   * Set the game server IP and the multi cast IP
   */
  private void setHostIP() {
    try {
      hostIP = Inet4Address.getLocalHost();
      multiCastIP = InetAddress.getByName("224.0.0.5");
    } catch (UnknownHostException uhe) {
      LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL, "Unknown Host");
      uhe.printStackTrace();
    }
  }

  /**
   * Initialize the game state, would not start the game loop
   */
  private void initializeGameState() {
    LogManager.getInstance().log("Server", LogManager.Scope.INFO, "Initialising game state...");

    // Make the map
    model.makeMap(seed);

    // Populate bullet pools
    for (int i = 0; i < Parameters.BULLET_POOL_SIZE; i++)
      bullets.add(new Bullet(new Position(0, 0), 0, 0, 0));

    // Initialise AIs
    if (Parameters.AI_ON)
      initializeAIs();

    // Add a plane dropper
    model.addEntity(new PlaneDropper());

    LogManager.getInstance().log("Server", LogManager.Scope.INFO,
        "Game set up. Waiting for players.");
  }

  /**
   * Initialize the AIs
   */
  private void initializeAIs() {
    for (int i = 1; i <= Parameters.MAX_PLAYERS; i++) {
      int randColour = (new Random()).nextInt(0xFFFFFF);
      Ship sh = new Ship(getRandomPosition(), 0, randColour);
      AIController ai = new AIController(sh);
      model.addEntity(sh);
      aiMap.put(sh, ai);
    }
  }
  
  /**
   * Get a position on the map randomly that doesn't have ice for ship spawning
   * 
   * @return A position on the map
   */
  public Position getRandomPosition() {
    boolean[][] iceGrid = model.getMap().getIceGrid();
    Random r = new Random();
    int randomX = 0;
    int randomY = 0;
    boolean appropriatePosition = false;

    while (!appropriatePosition) {
      boolean collideWithIce = false;
      int minX = (int) Parameters.SHIP_LENGTH;
      int minY = (int) Parameters.SHIP_WIDTH;
      int maxX = Parameters.MAP_WIDTH - (int) Parameters.SHIP_LENGTH;
      int maxY = Parameters.MAP_HEIGHT - (int) Parameters.SHIP_WIDTH;
      randomX = r.nextInt((maxX - minX) + 1) + minX;
      randomY = r.nextInt((maxY - minY) + 1) + minY;
      int xPosStart = randomX - (int) Parameters.SHIP_LENGTH;
      int xPosEnd = randomX + (int) Parameters.SHIP_LENGTH;
      int yPosStart = randomY - (int) Parameters.SHIP_WIDTH;
      int yPosEnd = randomY + (int) Parameters.SHIP_WIDTH;

      outerloop: for (int x = xPosStart; x < xPosEnd; x++) {
        for (int y = yPosStart; y < yPosEnd; y++) {
          if (iceGrid[x][y]) {
            collideWithIce = true;
            break outerloop;
          }
        }
      }

      if (!collideWithIce) {
        appropriatePosition = true;
      }
    }

    return new Position(randomX, randomY);
  }

  /**
   * Process client's message
   * 
   * @param clientIP Client's IP address
   * @param cr Message from client
   * @param toClient Output stream to write data to client
   * @param fromClient Input stream to read data from client
   */
  private void processClientMessage(InetAddress clientIP, ClientMessage cr,
      ObjectOutputStream toClient, ObjectInputStream fromClient) {
    if (cr.getType() == 0) { // Connect request
      ClientHandler ch = new ClientHandler(clientIP, cr.getUdpPort(), playerList, entities,
          playerMap, seed, toClient, fromClient, cr.getPlayerName());
      chList.add(ch);
      ch.start();
    }

  }
  
  /**
   * Start the game state handler
   */
  public void startSGH() {
    sgh = new ServerGameHandler(udpSocket, multiCastIP, entities, latestCIS, playerList);
    for (ClientHandler ch : chList) {
      ch.sendStartGame();
    }
    sgh.start();
  }

  /**
   * Disconnect the player from the server
   * 
   * @param clientIP Client's IP address
   * @param clientPort Client's UDP port number
   */
  public void disconnectPlayer(InetAddress clientIP, int clientPort) {
    if (isPlayerConnected(clientIP, clientPort)) {
      Player pToBeRemoved = this.getPlayerByIpAndPort(clientIP, clientPort);
      Ship shipToBeRemoved = pToBeRemoved.getShip();
      ClientHandler ch = this.getClientHandlerByIpAndPort(clientIP, clientPort);
      playerList.remove(pToBeRemoved);
      shipToBeRemoved.delete();
      playerMap.remove(shipToBeRemoved);
      latestCIS.remove(pToBeRemoved);
      ServerScoreBoard.getInstance().remove(pToBeRemoved);
      chList.remove(ch);
      ch.end();
      System.out.println("Client disconnected");
    }
  }

  /**
   * Check if the player is connected
   * 
   * @param clientIP Client's IP address
   * @param clientPort Client's UDP port number
   * @return
   */
  private boolean isPlayerConnected(InetAddress clientIP, int clientPort) {
    for (Player p : playerList) {
      if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets player by bullet. Used in awarding score.
   * 
   * @param l the bullet belonging to the player
   * @return the player who owns the bullet, null if it's AI
   */
  public Player getPlayer(long l) {
    for (Player p : playerList)
      if (p.getShip().getSerial() == l)
        return p;
    return null;
  }

  /**
   * Add a game event
   * 
   * @param event The GameEvent object
   */
  public void addEvent(GameEvent event) {
    events.add(event);
  }

  /**
   * Get the next game event
   * 
   * @return The GameEvent object
   */
  public GameEvent getNextEvent() {
    if (events.size() == 0)
      return null;
    return events.removeFirst();
  }

  /**
   * Get the corresponding player object using client's IP address and UDP port number
   * 
   * @param clientIP Client's IP address
   * @param clientPort Client's UDP port number
   * @return The player object
   */
  public Player getPlayerByIpAndPort(InetAddress clientIP, int clientPort) {
    for (Player p : playerList) {
      if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
        return p;
      }
    }
    return null;
  }

  /**
   * Get the AI controller for a ship
   * 
   * @param ship The ship object
   * @return The AI controller
   */
  public AIController getAIByShip(Ship ship) {
    return aiMap.get(ship);
  }

  /**
   * Get the corresponding player object using its ship
   * 
   * @param ship The ship object
   * @return The player object
   */
  public Player getPlayerByShip(Ship ship) {
    return playerMap.get(ship);
  }

  /**
   * Get the list of ClientHandler
   * 
   * @return A list of ClientHandler
   */
  public ArrayList<ClientHandler> getCHList() {
    return chList;
  }

  /**
   * Get the corresponding ClientHandler using client's IP address and UDP port number
   * 
   * @param clientIP Client's IP address
   * @param clientPort Client's UDP port number
   * @return A ClientHandler object
   */
  public ClientHandler getClientHandlerByIpAndPort(InetAddress clientIP, int clientUdpPort) {
    for (ClientHandler ch : chList) {
      if (ch.getClientIP().equals(clientIP) && ch.getClientUdpPort() == clientUdpPort) {
        return ch;
      }
    }
    return null;
  }

  /**
   * Get a bullet from the pool
   * @param position Position of the bullet
   * @param direction Direction of the bullet
   * @param distance Distance that will travel for the bullet
   * @param serial Serial number of the bullet
   * @return A bullet
   */
  public Bullet getBullet(Position position, double direction, double distance, long serial) {

    // Take one from the top
    Bullet b = bullets.pollFirst();
    // Modify it
    b.setPosition(position);
    b.setDirection(direction);
    b.initObb();
    b.setDistance(distance);
    b.setTravelled(0);
    b.setSource(serial);
    b.save();
    // Add it to the end before passing it to the caller
    bullets.addLast(b);
    return b;
  }

}
