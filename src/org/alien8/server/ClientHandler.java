package org.alien8.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.score.ScoreBoard;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;
import org.alien8.util.LogManager;

public class ClientHandler extends Thread {

  private InetAddress clientIP;
  private int clientUdpPort;
  private ArrayList<Player> playerList;
  private ConcurrentLinkedQueue<Entity> entities;
  private ConcurrentHashMap<Ship, Player> playerMap;
  private Long mapSeed;
  private ObjectOutputStream toClient;
  private ModelManager model = ModelManager.getInstance();

  public ClientHandler(InetAddress clientIP, int clientUdpPort, ArrayList<Player> playerList,
      ConcurrentLinkedQueue<Entity> entities, ConcurrentHashMap<Ship, Player> playerMap,
      Long mapSeed, ObjectOutputStream toClient) {
    this.clientIP = clientIP;
    this.clientUdpPort = clientUdpPort;
    this.playerList = playerList;
    this.entities = entities;
    this.playerMap = playerMap;
    this.mapSeed = mapSeed;
    this.toClient = toClient;
  }

  public void run() {
    Position randPos = Server.getRandomPosition();
    
    // TODO: ADD NAMES TO PLAYERS
    int k = (new Random()).nextInt(1000);
    String name = "RAND_NAME_" + k;
    
    // Setup client's ship
    int randColour = (new Random()).nextInt(0xFFFFFF);
    Ship s = new Ship(new Position(randPos.getX(), randPos.getY()), 0, randColour);
    model.addEntity(s);

    // Setup client's player info
    Player p = new Player(name, clientIP, clientUdpPort, s);
    playerMap.put(s, p);
    ScoreBoard.getInstance().add(p);
    playerList.add(p);

    // Start the server game loop if it is the first client connecting
    if (playerList.size() == 1) {
      Server.startSMCS();
    }
    
    sendMapSeed(clientIP, toClient, mapSeed);
    sendGameState(toClient);
  }

  private void sendMapSeed(InetAddress clientIP, ObjectOutputStream toClient, Long seed) {
    try {
      toClient.writeObject(seed);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
          "Could not send map seed to client. " + ioe.toString());
      ioe.printStackTrace();
    }
  }

  /*
   * Create a compressed set of entities (game state) from the original set of entities
   */
  private ArrayList<EntityLite> calculateEntitiesLite(ConcurrentLinkedQueue<Entity> ents) {
    ArrayList<EntityLite> EntitiesLite = new ArrayList<EntityLite>();

    for (Entity e : ents) {
      if (e instanceof Ship) {
        Ship s = (Ship) e;
        Player p = Server.getPlayerByShip(s);
        if (p != null) { // Player ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 0, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getMidTurretDirection(), s.getRearTurretDirection(), s.getColour(), p.getIP(),
              p.getPort()));
        } else { // AI ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 1, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getMidTurretDirection(), s.getRearTurretDirection(), s.getColour()));
        }

      } else if (e instanceof SmallBullet) {
        SmallBullet sb = (SmallBullet) e;
        EntitiesLite.add(new EntityLite(sb.getSerial(), 2, sb.getPosition(), sb.isToBeDeleted(),
            sb.getDirection(), sb.getSpeed(), sb.getDistance(), sb.getTravelled(), sb.getSource()));
      } else if (e instanceof BigBullet) {
        BigBullet bb = (BigBullet) e;
        EntitiesLite.add(new EntityLite(bb.getSerial(), 3, bb.getPosition(), bb.isToBeDeleted(),
            bb.getDirection(), bb.getSpeed(), bb.getDistance(), bb.getTravelled(), bb.getSource()));
      }
    }

    return EntitiesLite;
  }

  private void sendGameState(ObjectOutputStream toClient) {
    ArrayList<EntityLite> entsLite = this.calculateEntitiesLite(entities);
    try {
      toClient.writeObject(entsLite);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
          "Could not send entsLite to client. " + ioe.toString());
      ioe.printStackTrace();
    }
  }

  public InetAddress getClientIP() {
    return this.clientIP;
  }

  public int getClientUdpPort() {
    return this.clientUdpPort;
  }
}
