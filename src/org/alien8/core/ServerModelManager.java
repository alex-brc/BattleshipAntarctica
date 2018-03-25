package org.alien8.core;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.ai.AIController;
import org.alien8.client.ClientInputSample;
import org.alien8.mapgeneration.Map;
import org.alien8.physics.Collision;
import org.alien8.physics.CollisionDetector;
import org.alien8.physics.PhysicsManager;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.Ship;


/**
 * This class implements the model at the core of the game itself. It keeps the record of game
 * entities in a ConcurrentLinkedQueue<Entity>.
 */
public class ServerModelManager {

  private long lastSerial = 0;
  private static ServerModelManager instance;
  private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();
  private CollisionDetector collisionDetector = new CollisionDetector();
  private Map map;

  /**
   * Private constructor to prevent global instantiation
   */
  private ServerModelManager() {
    
  }

  /**
   * A standard getInstance() in accordance with the singleton pattern.
   * Create and return a ServerModelManager instance the first time being called,
   * only return the instance afterwards
   * 
   * @return A ServerModelManager instance
   */
  public static ServerModelManager getInstance() {
    if (instance == null)
      instance = new ServerModelManager();
    return instance;
  }

  /**
   * Reset game state related fields
   */
  public void reset() {
    lastSerial = 0;
    entities = new ConcurrentLinkedQueue<Entity>();
    collisionDetector = new CollisionDetector();
    map = null;
  }

  /**
   * Make a map from a random seed
   * @param seed A random long
   */
  public void makeMap(long seed) {
    map = new Map(Parameters.MAP_HEIGHT, Parameters.MAP_WIDTH, 8, 8, seed);
  }

  /**
   * Update the server game state using the latest client input sample table
   * @param latestCIS An hashmap storing the latest client input sample
   */
  public void updateServer(ConcurrentHashMap<Player, ClientInputSample> latestCIS) {
    // Loop through all the entities
    // System.out.println(entities.size());
    AIController ai = null;
    for (Entity ent : entities) {
      // Remove the entity if it's marked itself for deletion
      if (ent.isToBeDeleted()) {
        entities.remove(ent);
        // Accelerate it's removal
        ent = null;
        // Skip the rest
        continue;
      }
      if (ent instanceof Ship) {
        // Handle player stuff
        Ship ship = (Ship) ent;
        ai = Server.getInstance().getAIByShip(ship);
        if (ai != null) { // It's an AIController
          ai.update();
        } else // It's a Player
          for (Player p : latestCIS.keySet()) {
            if (ent == p.getShip()) {
              p.setClientInputSample(latestCIS.get(p));
              p.update();
              break;
            }
          }
      }

      // Update the position of the entity
      PhysicsManager.updatePosition(ent, map.getIceGrid());
    }
    ArrayList<Collision> collisions = collisionDetector.findCollisions(entities);
    for (Collision c : collisions) {
      // System.out.println("Resolving collision");
      c.resolveCollision();
    }

  }

  /**
   * Creates a serial number for it then adds the Entity to the entity queue
   * 
   * @param entity the Entity to add to the queue
   * @return true if the Entity was added successfully, false otherwise
   */
  public boolean addEntity(Entity entity) {
    // Give it a serial number
    lastSerial++;
    entity.setSerial(lastSerial);

    return entities.add(entity);
  }

  /**
   * Get the current Entity queue
   * @return An Entity queue
   */
  public ConcurrentLinkedQueue<Entity> getEntities() {
    return entities;
  }

  /**
   * Get the map
   * @return The map
   */
  public Map getMap() {
    return this.map;
  }

  /**
   * Count the number of ships in the entity queue
   * @return The number of ships in the entity queue
   */
  public int countShips() {
    int counter = 0;
    for (Entity ent : entities)
      if (ent instanceof Ship)
        counter++;
    return counter;
  }
}
