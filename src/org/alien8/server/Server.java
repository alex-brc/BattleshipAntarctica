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
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.items.PlaneDropper;
import org.alien8.physics.Position;
import org.alien8.score.ScoreBoard;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.alien8.util.LogManager;

/*
 * A singleton game server, call Server.getInstance().start() every time a client starts a server
 */
public class Server implements Runnable {

  private static Server instance;
  private Thread thread;
  private InetAddress hostIP = null;
  private InetAddress multiCastIP = null;
  private ServerSocket tcpSocket = null;
  private DatagramSocket udpSocket = null;
  private ModelManager model = ModelManager.getInstance();
  private ConcurrentLinkedQueue<Entity> entities = model.getEntities();
  private ConcurrentHashMap<Player, ClientInputSample> latestCIS =
      new ConcurrentHashMap<Player, ClientInputSample>();
  private ConcurrentHashMap<Ship, AIController> aiMap = new ConcurrentHashMap<Ship, AIController>();
  private ConcurrentHashMap<Ship, Player> playerMap = new ConcurrentHashMap<Ship, Player>();
  private ArrayList<Player> playerList = new ArrayList<Player>();
  private ArrayList<ClientHandler> chList = new ArrayList<ClientHandler>();
  private LinkedList<GameEvent> events = new LinkedList<GameEvent>();
  private Long seed = (new Random()).nextLong();
  private volatile LinkedList<Bullet> bullets = new LinkedList<Bullet>();

  public static void main(String[] args) {
    Server s = Server.getInstance();
    s.start();
  }

  private Server() {

  }

  public static Server getInstance() {
    if (instance == null)
      instance = new Server();
    return instance;
  }

  public void start() {
    this.reset();
    thread = new Thread(this, "Battleship Antarctica Server");
    thread.start();
  }

  public void stop() {
    udpSocket.close();
    try {
      tcpSocket.close(); // Would make tcpSocket.accept() to throw SocketException + all ClientHandlers would throw IOException
    } catch (IOException ioe) {
      System.out.println("Can't close TCP socket!?");
    }
  }

  public void reset() {
    // TODO: reset ModelManager
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
    seed = (new Random()).nextLong();
    bullets = new LinkedList<Bullet>();
  }

  @Override
  public void run() {
    model.makeMap(seed);
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
      while (true) {
        // Receive and process client's packet
        LogManager.getInstance().log("Server", LogManager.Scope.INFO,
            "Waiting for client request...");
        Socket client = tcpSocket.accept();
        InetAddress clientIP = client.getInetAddress();
        ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
        ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
        ClientMessage cr = (ClientMessage) fromClient.readObject();
        processClientMessage(clientIP, cr, toClient, fromClient);
      }

    } catch (SocketException se) {
      // Do nothing, just let this thread stops
    } catch (IOException ioe) {
      LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL,
          "Something wrong with the TCP connection");
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL,
          "Cannot find the class of the received serialized object");
      cnfe.printStackTrace();
    }
    
    System.out.println("Server stopped");
  }

  private void setHostIP() {
    try {
      hostIP = Inet4Address.getLocalHost();
      multiCastIP = InetAddress.getByName("224.0.0.5");
    } catch (UnknownHostException uhe) {
      LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL, "Unknown Host");
      uhe.printStackTrace();
    }
  }

  /*
   * Only initialize the game state, will not start the server game loop
   */
  public void initializeGameState() {
    LogManager.getInstance().log("Server", LogManager.Scope.INFO, "Initialising game state...");

    // Populate bullet pools
    for (int i = 0; i < Parameters.BULLET_POOL_SIZE; i++)
      bullets.add(new Bullet(new Position(0, 0), 0, 0, 0));

    // Initialise ScoreBoard
    // Without a thread, it doesn't listen on input.
    ScoreBoard.getInstance();

    // Initialise AIs
    if (Parameters.AI_ON)
      initializeAIs();

    model.addEntity(new PlaneDropper());

    LogManager.getInstance().log("Server", LogManager.Scope.INFO,
        "Game set up. Waiting for players.");
  }

  private void initializeAIs() {
    // Ai controllers should be put in the
    // ConcurrentHashMap<Ship, AIController> aiMap
    // so the loop has constant time access to the AI controller given the ship
    // also, remember to give them colours

    // test ai
    for (int i = 1; i <= 7; i++) {
      int randColour = (new Random()).nextInt(0xFFFFFF);
      Ship sh = new Ship(getRandomPosition(), 0, randColour);
      AIController ai = new AIController(sh);
      model.addEntity(sh);
      aiMap.put(sh, ai);
    }
  }

  private void processClientMessage(InetAddress clientIP, ClientMessage cr,
      ObjectOutputStream toClient, ObjectInputStream fromClient) {
    if (cr.getType() == 0) { // Connect request
      ClientHandler ch = new ClientHandler(clientIP, cr.getUdpPort(), playerList, entities,
          playerMap, seed, toClient, fromClient);
      chList.add(ch);
      ch.start();
    }

  }

  private boolean isPlayerConnected(InetAddress clientIP, int clientPort) {
    for (Player p : playerList) {
      if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
        return true;
      }
    }
    return false;
  }

  public void disconnectPlayer(InetAddress clientIP, int clientPort) {
    if (isPlayerConnected(clientIP, clientPort)) {
      Player pToBeRemoved = this.getPlayerByIpAndPort(clientIP, clientPort);
      Ship shipToBeRemoved = pToBeRemoved.getShip();
      ClientHandler ch = this.getClientHandlerByIpAndPort(clientIP, clientPort);
      playerList.remove(pToBeRemoved);
      shipToBeRemoved.delete();
      playerMap.remove(shipToBeRemoved);
      latestCIS.remove(pToBeRemoved);
      ScoreBoard.getInstance().remove(pToBeRemoved);
      chList.remove(ch);
      ch.end();
    }
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

  public void addEvent(GameEvent event) {
    events.add(event);
  }

  public GameEvent getNextEvent() {
    if (events.size() == 0)
      return null;
    return events.removeFirst();
  }

  public void startSGH() {
    ServerGameHandler sgh =
        new ServerGameHandler(udpSocket, multiCastIP, entities, latestCIS, playerList);
    sgh.start();
  }

  public Player getPlayerByIpAndPort(InetAddress clientIP, int clientPort) {
    for (Player p : playerList) {
      if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
        return p;
      }
    }
    return null;
  }

  public AIController getAIByShip(Ship ship) {
    return aiMap.get(ship);
  }

  public Player getPlayerByShip(Ship ship) {
    return playerMap.get(ship);
  }

  public ArrayList<ClientHandler> getCHList() {
    return chList;
  }

  public ClientHandler getClientHandlerByIpAndPort(InetAddress clientIP, int clientUdpPort) {
    for (ClientHandler ch : chList) {
      if (ch.getClientIP().equals(clientIP) && ch.getClientUdpPort() == clientUdpPort) {
        return ch;
      }
    }
    return null;
  }

  public Position getRandomPosition() {
    boolean[][] iceGrid = model.getMap().getIceGrid();
    Random r = new Random();
    double randomX = 0;
    double randomY = 0;
    boolean isIcePosition = true;

    // Choose a random position without ice for ship spawning
    while (isIcePosition) {
      randomX = (double) r.nextInt(Parameters.MAP_WIDTH);
      randomY = (double) r.nextInt(Parameters.MAP_HEIGHT);

      if (!iceGrid[(int) randomX][(int) randomY]) {
        isIcePosition = false;
      }
    }
    return new Position(randomX, randomY);
  }

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
