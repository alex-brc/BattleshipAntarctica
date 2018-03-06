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
import org.alien8.core.ClientRequest;
import org.alien8.core.Entity;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.score.ScoreBoard;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;
import org.alien8.util.LogManager;

public class Server {

  private static InetAddress hostIP = null;
  private static InetAddress multiCastIP = null;
  private static ServerSocket tcpSocket = null;
  private static DatagramSocket udpSocket = null;
  private static ModelManager model = ModelManager.getInstance();
  private static ConcurrentLinkedQueue<Entity> entities = model.getEntities();
  private static ConcurrentHashMap<Player, ClientInputSample> latestCIS =
      new ConcurrentHashMap<Player, ClientInputSample>();
  private static ConcurrentHashMap<Ship, AIController> aiMap =
      new ConcurrentHashMap<Ship, AIController>();
  private static ConcurrentHashMap<Ship, Player> playerMap = new ConcurrentHashMap<Ship, Player>();
  private static ArrayList<Player> playerList = new ArrayList<Player>();
  private static ArrayList<ClientHandler> chList = new ArrayList<ClientHandler>();
  private static LinkedList<GameEvent> events = new LinkedList<GameEvent>();
  private static int serverPort = 4446;
  private static int multiCastPort = 4445;
  private static Long seed = (new Random()).nextLong();
  private static volatile LinkedList<BigBullet> bigBullets = new LinkedList<BigBullet>();
  private static volatile LinkedList<SmallBullet> smallBullets = new LinkedList<SmallBullet>();
  private static volatile boolean run = true;

  public static void main(String[] args) {
    model.makeMap(seed);
    try {
      setHostIP();
      tcpSocket = new ServerSocket(serverPort, 50, hostIP);
      udpSocket = new DatagramSocket(serverPort, hostIP);
      System.out.println("TCP socket Port: " + tcpSocket.getLocalPort());
      System.out.println("TCP socket IP: " + tcpSocket.getInetAddress());
      System.out.println("UDP socket Port: " + udpSocket.getLocalPort());
      System.out.println("UDP socket IP: " + udpSocket.getLocalAddress());
      initializeGameState();

      // Process clients' connect/disconnect request
      while (run) {
        // Receive and process client's packet
        LogManager.getInstance().log("Server", LogManager.Scope.INFO,
            "Waiting for client request...");
        Socket client = tcpSocket.accept();
        InetAddress clientIP = client.getInetAddress();
        ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
        ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
        ClientRequest cr = (ClientRequest) fromClient.readObject();
        processClientRequest(clientIP, cr, toClient);
      }

      tcpSocket.close();
      udpSocket.close();
    } catch (SocketException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }

  private static void setHostIP() {
    try {
      hostIP = Inet4Address.getLocalHost();
      multiCastIP = InetAddress.getByName("224.0.0.5");
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  /*
   * Only initialize the game state, will not start the server game loop
   */
  public static void initializeGameState() {
    LogManager.getInstance().log("Server", LogManager.Scope.INFO, "Initialising game state...");

    // Populate bullet pools
    for(int i = 0; i < Parameters.SMALL_BULLET_POOL_SIZE; i++) {
    	smallBullets.add(new SmallBullet(new Position(0,0), 0, 0, 0));
    }
    for(int i = 0; i < Parameters.BIG_BULLET_POOL_SIZE; i++) {
    	bigBullets.add(new BigBullet(new Position(0,0), 0, 0, 0));
    }
    
    // Initialise ScoreBoard
    // Without a thread, it doesn't listen on input.
    ScoreBoard.getInstance();

    initializeAIs();

    LogManager.getInstance().log("Server", LogManager.Scope.INFO,
        "Game set up. Waiting for players.");
  }

  private static void initializeAIs() {
    // Ai controllers should be put in the
    // ConcurrentHashMap<Ship, AIController> aiMap
    // so the loop has constant time access to the AI controller given the ship
    // also, remember to give them colours

    // test ai
	for(int i = 1; i <= 7; i++) {
		Ship sh = new Ship(getRandomPosition(), 0, 0xFFFFFF);
	    AIController ai = new AIController(sh);
	    model.addEntity(sh);
	    aiMap.put(sh, ai);
	}
    

  }

  private static void processClientRequest(InetAddress clientIP, ClientRequest cr,
      ObjectOutputStream toClient) {
    if (cr.getType() == 0) { // Connect request
      ClientHandler ch = new ClientHandler(clientIP, cr.getUdpPort(), playerList, entities,
          playerMap, seed, toClient);
      chList.add(ch);
      ch.start();
    } else if (cr.getType() == 1) { // Disconnect Request
      disconnectPlayer(clientIP, cr.getUdpPort());
    }
  }

  private static boolean isPlayerConnected(InetAddress clientIP, int clientPort) {
    for (Player p : playerList) {
      if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
        return true;
      }
    }
    return false;
  }

  public static void disconnectPlayer(InetAddress clientIP, int clientPort) {
    if (isPlayerConnected(clientIP, clientPort)) {
      for (Player p : playerList) {
        if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
          // Remove player from the PlayerList
          // Do not do entities.remove(), just have the ship marked for deletion.
          // model.getEntities().remove(p.getShip());
          p.getShip().delete();
          latestCIS.remove(p);
          playerList.remove(p);
          chList.remove(getClientHandlerByIpAndPort(clientIP, clientPort));
          // Remove player from scoreboard
          ScoreBoard.getInstance().remove(p);
        }
      }
    }
  }

  /**
   * Gets player by bullet. Used in awarding score.
   * 
   * @param bullet the bullet belonging to the player
   * @return the player who owns the bullet, null if it's AI
   */
  public static Player getPlayer(Bullet bullet) {
    for (Player p : playerList)
      if (p.getShip().getSerial() == bullet.getSource())
        return p;
    return null;
  }

  public static void addEvent(GameEvent event) {
    events.add(event);
  }

  public static GameEvent getNextEvent() {
    if (events.size() == 0)
      return null;
    return events.removeFirst();
  }

  public static void startSMCS() {
    ServerMulticastSender smcs = new ServerMulticastSender(udpSocket, multiCastPort, multiCastIP,
        entities, latestCIS, playerList);
    smcs.start();
  }

  public static Player getPlayerByIpAndPort(InetAddress clientIP, int clientPort) {
    for (Player p : playerList) {
      if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
        return p;
      }
    }
    return null;
  }

  public static AIController getAIByShip(Ship ship) {
    return aiMap.get(ship);
  }

  public static Player getPlayerByShip(Ship ship) {
    return playerMap.get(ship);
  }

  public static ClientHandler getClientHandlerByIpAndPort(InetAddress clientIP, int clientUdpPort) {
    for (ClientHandler ch : chList) {
      if (ch.getClientIP().equals(clientIP) && ch.getClientUdpPort() == clientUdpPort) {
        return ch;
      }
    }
    return null;
  }

public static Position getRandomPosition() {
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
	return new Position(randomX,randomY);
}

public static BigBullet getBigBullet(Position position, double direction, double distance, long serial) {
	
	// Take one from the top
	BigBullet b = bigBullets.pollFirst();
	// Modify it
	b.setPosition(position);
	b.setDirection(direction);
	b.setDistance(distance);
	b.setSource(serial);
	b.save();
	// Add it to the end before passing it to the caller
	bigBullets.addLast(b);
	System.out.println("Summoned " + b);
	return b;
}

public static SmallBullet getSmallBullet(Position position, double direction, double distance, long serial) {
	// Take one from the top
	SmallBullet b = smallBullets.pollFirst();
	// Modify it
	b.setDirection(direction);
	b.setDistance(distance);
	b.setPosition(position);
	b.setSource(serial);
	b.save();
	// Add it to the end before passing it to the caller
	smallBullets.addLast(b);
	return b;
}

}
