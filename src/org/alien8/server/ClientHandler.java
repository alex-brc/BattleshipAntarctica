package org.alien8.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.core.ClientMessage;
import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.core.ServerModelManager;
import org.alien8.core.ServerMessage;
import org.alien8.physics.Position;
import org.alien8.score.Score;
import org.alien8.score.ServerScoreBoard;
import org.alien8.score.ScoreEvent;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.alien8.util.LogManager;

public class ClientHandler extends Thread {

  private InetAddress clientIP;
  private int clientUdpPort;
  private ArrayList<Player> playerList;
  private ConcurrentLinkedQueue<Entity> entities;
  private ConcurrentHashMap<Ship, Player> playerMap;
  private Long mapSeed;
  private ObjectOutputStream toClient;
  private ObjectInputStream fromClient;
  private String playerName;
  private ServerModelManager model = ServerModelManager.getInstance();
  private volatile boolean run = true;

  /**
   * Constructor
   * 
   * @param clientIP Client's IP address
   * @param clientUdpPort Client's UDP port number
   * @param playerList List of player
   * @param entities List of entity
   * @param playerMap Ship to Player table
   * @param mapSeed Map seed
   * @param toClient Output stream for writing data to client
   * @param fromClient Input stream for reading data from client
   * @param playerName Player's name
   */
  public ClientHandler(InetAddress clientIP, int clientUdpPort, ArrayList<Player> playerList,
      ConcurrentLinkedQueue<Entity> entities, ConcurrentHashMap<Ship, Player> playerMap,
      Long mapSeed, ObjectOutputStream toClient, ObjectInputStream fromClient, String playerName) {
    this.clientIP = clientIP;
    this.clientUdpPort = clientUdpPort;
    this.playerList = playerList;
    this.entities = entities;
    this.playerMap = playerMap;
    this.mapSeed = mapSeed;
    this.toClient = toClient;
    this.fromClient = fromClient;
    this.playerName = playerName;
  }

  // Stop the thread
  public void end() {
    run = false;
  }

  // Send end game message to client
  public void sendEndGameMessage() {
    try {
      toClient.writeObject(new ServerMessage(0));
    } catch (IOException ioe) {
      // Do nothing
    }
  }

  // Send the time before exiting the match to client
  public void sendTimeBeforeExiting(int timeBeforeExiting) {
    try {
      toClient.writeObject(new ServerMessage(1, timeBeforeExiting));
    } catch (IOException ioe) {
      // Do nothing
    }
  }

  // Send server stopped message to client
  public void sendServerStoppedMessage() {
    try {
      toClient.writeObject(new ServerMessage(2));
    } catch (IOException ioe) {
      // Do nothing
    }
  }


  // Send start game message to client
  public void sendStartGame() {
    try {
      toClient.writeObject(new ServerMessage(3));
    } catch (IOException ioe) {
      // Do nothing
    }
  }

  /**
   * Run loop for communicating with client
   */
  @Override
  public void run() {
    Position randPos = Server.getInstance().getRandomPosition();

    // Setup client's ship
    int randColour = (new Random()).nextInt(0xFFFFFF);
    Ship s = new Ship(new Position(randPos.getX(), randPos.getY()), 0, randColour);
    model.addEntity(s);

    // Setup client's player info
    Player p = new Player(playerName, clientIP, clientUdpPort, s);
    playerMap.put(s, p);
    ServerScoreBoard.getInstance().add(p);

    this.sendMapSeed(p, s);
    this.sendGameState(p, s);
    this.waitForReadyMessage(p, s);

    // Keep reading from client to track the connection status
    while (run) {
      try {
        fromClient.readObject();
      } catch (ClassNotFoundException cnfe) {
        // Won't happen
        Server.getInstance().disconnectPlayer(clientIP, clientUdpPort);
      } catch (IOException ioe) {
        Server.getInstance().disconnectPlayer(clientIP, clientUdpPort);
      }
    }

    System.out.println("Client Handler (" + clientIP + ", " + clientUdpPort + ") stopped");
  }

  /**
   * Send the map seed to client
   * 
   * @param p The Player object of the client
   * @param s The Ship object of the client
   */
  private void sendMapSeed(Player p, Ship s) {
    try {
      toClient.writeObject(mapSeed);
    } catch (IOException ioe) {
      this.disconnectClient(p, s);
    }
  }

  /**
   * Send a full snapshot of game state to client
   * 
   * @param p The Player object of the client
   * @param s The Ship object of the client
   */
  private void sendGameState(Player p, Ship s) {
    ArrayList<EntityLite> entsLite = this.calculateEntitiesLite(entities);
    LinkedList<ScoreEvent> initialScores = new LinkedList<ScoreEvent>();
    for (Score score : ServerScoreBoard.getInstance().getScores())
      initialScores.add(score.exportToEvent());
    try {
      toClient.writeObject(entsLite);
      toClient.writeObject(initialScores);
    } catch (IOException ioe) {
      this.disconnectClient(p, s);
    }
  }

  /*
   * Create a compressed set of entities (ArrayList of EntityLite) from the original set of entities
   */
  private ArrayList<EntityLite> calculateEntitiesLite(ConcurrentLinkedQueue<Entity> ents) {
    ArrayList<EntityLite> EntitiesLite = new ArrayList<EntityLite>();

    for (Entity e : ents) {
      if (e instanceof Ship) {
        Ship s = (Ship) e;
        Player p = Server.getInstance().getPlayerByShip(s);
        if (p != null) { // Player ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 0, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getRearTurretDirection(), s.getFrontTurretCharge(), s.getRearTurretCharge(),
              s.getColour(), s.getItemType(), s.getEffectType(), p.getIP(), p.getPort()));
        } else { // AI ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 1, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getRearTurretDirection(), s.getEffectType(), s.getColour()));
        }

      } else if (e instanceof Bullet) {
        Bullet b = (Bullet) e;
        EntitiesLite.add(new EntityLite(b.getSerial(), 2, b.getPosition(), b.isToBeDeleted(),
            b.getDirection(), b.getSpeed(), b.getDistance(), b.getTravelled(), b.getSource()));
      }
    }

    return EntitiesLite;
  }

  /**
   * Wait for the ready message from client
   * 
   * @param p The Player object of the client
   * @param s The Ship object of the client
   */
  private void waitForReadyMessage(Player p, Ship s) {
    // Add the player to the playerList when the player is ready
    try {
      ClientMessage msg = (ClientMessage) fromClient.readObject();
      if (msg.getType() == 1) // Ready message
        playerList.add(p);
      else
        this.disconnectClient(p, s);
    } catch (ClassNotFoundException cnfe) {
      this.disconnectClient(p, s);
    } catch (IOException ioe) {
      this.disconnectClient(p, s);
    }
  }

  /**
   * Disconnect the client if things goes wrong before the client is added to the playerList
   * 
   * @param p The Player object of the client
   * @param s The Ship object of the client
   */
  private void disconnectClient(Player p, Ship s) {
    s.delete();
    playerMap.remove(s);
    ServerScoreBoard.getInstance().remove(p);
    Server.getInstance().getCHList().remove(this);
    this.end();
  }

  /**
   * Get the client's IP address
   * 
   * @return The client's IP address
   */
  public InetAddress getClientIP() {
    return this.clientIP;
  }

  /**
   * Get the client's UDP port number
   * 
   * @return The client's UDP port number
   */
  public int getClientUdpPort() {
    return this.clientUdpPort;
  }

}
