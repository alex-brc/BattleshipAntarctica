package org.alien8.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.ai.AIController;
import org.alien8.client.ClientInputSample;
import org.alien8.client.InputManager;
import org.alien8.mapgeneration.Map;
import org.alien8.physics.Collision;
import org.alien8.physics.CollisionDetector;
import org.alien8.physics.PhysicsManager;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;


/**
 * This class implements the model at the core of the game itself. It is responsible for the main
 * loop that updates the game state 60 times a second. It also keeps the record of game entities in
 * a LinkedList<Entity>.
 * <p>
 * 
 * @version 1.0
 */
public class ModelManager {

  private long lastSerial = 0;
  private static ModelManager instance;
  private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();
  private CollisionDetector collisionDetector = new CollisionDetector();
  private Map map;
  private Ship player;

  private ModelManager() {
    // Normally this exists only to defeat instantiation

    entities = new ConcurrentLinkedQueue<Entity>();
    collisionDetector = new CollisionDetector();
  }

  /**
   * A standard getInstance() in accordance with the singleton pattern
   * 
   * @return an instance of the active ModelManager
   */
  public static ModelManager getInstance() {
    if (instance == null)
      instance = new ModelManager();
    return instance;
  }

  public void makeMap(long seed) {
    map = new Map(Parameters.MAP_HEIGHT, Parameters.MAP_WIDTH, 8, 8, seed);
  }

  /**
   * Server update(). Loops through all the entities and updates the game state
   */
  public void updateServer(ConcurrentHashMap<Player, ClientInputSample> latestCIS) {
    // Loop through all the entities
    System.out.println(entities.size());
    AIController ai = null;
    Player pl = null;
    Ship sh = null;
    ClientInputSample cis = null;
    for (Entity ent : entities) {
      // Remove the entity if it's marked itself for deletion
      if (ent.isToBeDeleted()) {
        entities.remove(ent);
        // Skip the rest
        continue;
      }
      if (ent instanceof Ship) {
        sh = (Ship) ent;
        ai = Server.getAIByShip(sh);
        pl = Server.getPlayerByShip(sh);

        if (ai != null) {
          ai.update();
        } else if (pl != null) {
          cis = latestCIS.get(pl);
          InputManager.processInputs(sh, cis);
        }
      }

      // Update the position of the entity
      PhysicsManager.updatePosition(ent, map.getIceGrid());
    }
    ArrayList<Collision> collisions =
        (ArrayList<Collision>) collisionDetector.checkForCollisions(entities);
    for (Collision c : collisions) {
      // System.out.println("Collision");
      c.resolveCollision();
    }
  }


  /**
   * Sync the client with the server
   */
  public void sync(ArrayList<EntityLite> entitiesLite, InetAddress clientIP,
      Integer clientUdpPort) {
    // Remove all entities
    for (Entity e : entities) {
      entities.remove(e);
    }

    // Add updated entities
    for (EntityLite el : entitiesLite) {
      if (el.entityType == 0) { // Ship
        Ship s = new Ship(el.position, el.direction, el.colour);
        s.setSerial(el.serial);
        s.setSpeed(el.speed);
        s.setHealth(el.health);

        if (el.toBeDeleted) {
          s.delete();
        }

        if (el.clientIP.equals(clientIP) && el.clientUdpPort == clientUdpPort) { // Client's ship
          this.setPlayer(s);
        }

        this.addEntity(s);
      } else if (el.entityType == 1) { // SmallBullet
        SmallBullet sb = new SmallBullet(el.position, el.direction, el.distance, el.source);
        sb.setSerial(el.serial);
        sb.setSpeed(el.speed);
        sb.setTravelled(el.travelled);

        if (el.toBeDeleted) {
          sb.delete();
        }

        this.addEntity(sb);
      } else if (el.entityType == 2) { // BigBullet
        BigBullet bb = new BigBullet(el.position, el.direction, el.distance, el.source);
        bb.setSerial(el.serial);
        bb.setSpeed(el.speed);
        bb.setTravelled(el.travelled);

        if (el.toBeDeleted) {
          bb.delete();
        }

        this.addEntity(bb);
      }
    }
  }

  /**
   * Creates a serial number for it then adds the Entity to the entity list
   * 
   * @param entity the Entity to add to the list
   * @return true if the Entity was added successfully, false otherwise
   */
  public boolean addEntity(Entity entity) {
    // Give it a serial number
    lastSerial++;
    entity.setSerial(lastSerial);

    return entities.add(entity);
  }

  /**
   * @return the entity list as a LinkedList<Entity>
   */
  public ConcurrentLinkedQueue<Entity> getEntities() {
    return entities;
  }

  /**
   * Sets the designated ship as player.
   * 
   * @param player ship to set as the player
   */
  public void setPlayer(Ship player) {
    this.player = player;
  }

  public Ship getPlayer() {
    return player;
  }

  public Map getMap() {
    return this.map;
  }

  public void setMap(Map m) {
    map = m;
  }
}
