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
  
  public void end() {
    run = false;
  }
  
  public void sendEndGameMessage() {
    try {
      toClient.writeObject(new ServerMessage(0));
    } catch (IOException ioe) {
      // Do nothing
    }
  }
  
  public void sendTimeBeforeExiting(int timeBeforeExiting) {
    try {
      toClient.writeObject(new ServerMessage(1, timeBeforeExiting));
    } catch (IOException ioe) {
      // Do nothing
    }
  }
  
  public void sendServerStoppedMessage() {
    try {
      toClient.writeObject(new ServerMessage(2));
    } catch (IOException ioe) {
      // Do nothing
    }
  }
  

  public void sendStartGame() {
	  try {
		  toClient.writeObject(new ServerMessage(3));
	  } catch (IOException ioe) {
		  // Do nothing
	  }
  }

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

    // Keep reading from client to track the connection status (Disconnection is catched by exception)
    while (run) {
      try {
        fromClient.readObject();
      } catch (ClassNotFoundException cnfe) {
        LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
            "Class of serialized object cannot be found." + cnfe.toString());
        cnfe.printStackTrace();
        Server.getInstance().disconnectPlayer(clientIP, clientUdpPort);
      } catch (IOException ioe) { // Client disconnected / Server closing the input stream when game ends
        Server.getInstance().disconnectPlayer(clientIP, clientUdpPort);
      }
    }
    
    System.out.println("Client Handler (" + clientIP + ", " + clientUdpPort + ") stopped");
  }

  private void sendMapSeed(Player p, Ship s) {
    try {
      toClient.writeObject(mapSeed);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
          "Could not send map seed to client. " + ioe.toString());
      ioe.printStackTrace();
      this.disconnectClient(p, s);
    }
    LogManager.getInstance().log("ClientHandler", LogManager.Scope.INFO, "Sent seed to client. ");
  }

  /*
   * Create a compressed set of entities (game state) from the original set of entities
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
              s.getColour(), s.getItemType(), s.getEffectType(), 
              p.getIP(), p.getPort()));
        } else { // AI ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 1, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getRearTurretDirection(), s.getColour()));
        }

      } else if (e instanceof Bullet) {
        Bullet b = (Bullet) e;
        EntitiesLite.add(new EntityLite(b.getSerial(), 2, b.getPosition(), b.isToBeDeleted(),
            b.getDirection(), b.getSpeed(), b.getDistance(), b.getTravelled(), b.getSource()));
      }
    }

    return EntitiesLite;
  }

  private void sendGameState(Player p, Ship s) {
    ArrayList<EntityLite> entsLite = this.calculateEntitiesLite(entities);
    LinkedList<ScoreEvent> initialScores = new LinkedList<ScoreEvent>();
    for(Score score : ServerScoreBoard.getInstance().getScores())
    	initialScores.add(score.exportToEvent());
    try {
      toClient.writeObject(entsLite);
      toClient.writeObject(initialScores);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
          "Could not send entsLite to client. " + ioe.toString());
      ioe.printStackTrace();
      this.disconnectClient(p, s);
    }
    LogManager.getInstance().log("ClientHandler", LogManager.Scope.INFO,
        "Sent entLites to client. ");
  }

  private void waitForReadyMessage(Player p, Ship s) {
    // Add the player to the playerList when the player is ready
    try {
      ClientMessage msg = (ClientMessage) fromClient.readObject();
      if (msg.getType() == 1) // Ready message
        playerList.add(p);
      else
        this.disconnectClient(p, s);
    } catch (ClassNotFoundException cnfe) {
      LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
          "Class of serialized object cannot be found." + cnfe.toString());
      cnfe.printStackTrace();
      this.disconnectClient(p, s);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
          "Something is wrong when reading client's ready message" + ioe.toString());
      ioe.printStackTrace();
      this.disconnectClient(p, s);
    }
  }

  /*
   * Use this only if things goes wrong before the client is added to the playerList
   */
  private void disconnectClient(Player p, Ship s) {
    s.delete();
    playerMap.remove(s);
    ServerScoreBoard.getInstance().remove(p);
    Server.getInstance().getCHList().remove(this);
    this.end();
  }

  public InetAddress getClientIP() {
    return this.clientIP;
  }

  public int getClientUdpPort() {
    return this.clientUdpPort;
  }
  
}
