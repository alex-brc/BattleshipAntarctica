package org.alien8.core;

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
import org.alien8.ship.Ship;


/**
 * This class implements the model at the core of the game itself. It is responsible for the main
 * loop that updates the game state 60 times a second. It also keeps the record of game entities in
 * a LinkedList<Entity>.
 * <p>
 * 
 * @version 1.0
 */
public class ServerModelManager {

  private long lastSerial = 0;
  private static ServerModelManager instance;
  private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();
  private CollisionDetector collisionDetector = new CollisionDetector();
  private Map map;

  private ServerModelManager() {
    // Prevent global instantiation
  }

  /**
   * A standard getInstance() in accordance with the singleton pattern
   * 
   * @return an instance of the active ModelManager
   */
  public static ServerModelManager getInstance() {
    if (instance == null)
      instance = new ServerModelManager();
    return instance;
  }

  public void reset() {
    lastSerial = 0;
    entities = new ConcurrentLinkedQueue<Entity>();
    collisionDetector = new CollisionDetector();
    map = null;
  }

  public void makeMap(long seed) {
    map = new Map(Parameters.MAP_HEIGHT, Parameters.MAP_WIDTH, 8, 8, seed);
  }

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
        if (ai != null) {
          ai.update();
        } else
          for (Player p : latestCIS.keySet()) {
            if (ent == p.getShip()) {
              Ship s = (Ship) ent;
              s.updateEffect();
              ClientInputSample cis = latestCIS.get(p);
              InputManager.processInputs(s, cis);
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

  public Map getMap() {
    return this.map;
  }

  public void setMap(Map m) {
    map = m;
  }

  public int countShips() {
    int counter = 0;
    for (Entity ent : entities)
      if (ent instanceof Ship)
        counter++;
    return counter;
  }
}
