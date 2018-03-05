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
  private int numberOfPlayer;
  private ObjectOutputStream toClient;
  private ModelManager model = ModelManager.getInstance();

  public ClientHandler(InetAddress clientIP, int clientUdpPort, ArrayList<Player> playerList,
      ConcurrentLinkedQueue<Entity> entities, ConcurrentHashMap<Ship, Player> playerMap,
      Long mapSeed, int numberOfPlayer, ObjectOutputStream toClient) {
    this.clientIP = clientIP;
    this.clientUdpPort = clientUdpPort;
    this.playerList = playerList;
    this.entities = entities;
    this.playerMap = playerMap;
    this.mapSeed = mapSeed;
    this.numberOfPlayer = numberOfPlayer;
    this.toClient = toClient;
  }

  public void run() {
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

    // Setup client's ship
    int randColour = (new Random()).nextInt(0xFFFFFF);
    Ship s = new Ship(new Position(randomX, randomY), 0, randColour);
    model.addEntity(s);

    // Send the map seed to the client
    sendMapSeed(clientIP, toClient, mapSeed);

    // TODO: ADD NAMES TO PLAYERS
    int k = (new Random()).nextInt(1000);
    String name = "RAND_NAME_" + k;
    Player p = new Player(name, clientIP, clientUdpPort, s);

    playerMap.put(s, p);
    ScoreBoard.getInstance().add(p);
    playerList.add(p);

    while (playerList.size() != numberOfPlayer) {
      // Wait until the required number of player has connected
    }

    sendGameState(toClient);
    sendGameStartEvent(toClient);
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
        if (p != null) { // It is a player's ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 0, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getMidTurretDirection(), s.getRearTurretDirection(), s.getColour(), p.getIP(),
              p.getPort()));
        } else {
          // call EntityLite constructor for AI ship
        }

      } else if (e instanceof SmallBullet) {
        SmallBullet sb = (SmallBullet) e;
        EntitiesLite.add(new EntityLite(sb.getSerial(), 1, sb.getPosition(), sb.isToBeDeleted(),
            sb.getDirection(), sb.getSpeed(), sb.getDistance(), sb.getTravelled(), sb.getSource()));
      } else if (e instanceof BigBullet) {
        BigBullet bb = (BigBullet) e;
        EntitiesLite.add(new EntityLite(bb.getSerial(), 2, bb.getPosition(), bb.isToBeDeleted(),
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

  private void sendGameStartEvent(ObjectOutputStream toClient) {
    try {
      GameStartEvent gse = new GameStartEvent();
      toClient.writeObject(gse);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ClientHandler", LogManager.Scope.CRITICAL,
          "Could not send game start notificaiton to client. " + ioe.toString());
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
