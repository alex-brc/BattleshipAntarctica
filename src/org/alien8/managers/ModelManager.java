package org.alien8.managers;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.mapgeneration.Map;
import org.alien8.physics.Collision;
import org.alien8.physics.CollisionDetector;
import org.alien8.physics.PhysicsManager;
import org.alien8.ship.Ship;


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
  private static ModelManager instance = new ModelManager();
  private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();
  private CollisionDetector collisionDetector = new CollisionDetector();
  private Map map = new Map(Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 8, 8);
  private Ship player;

  private ModelManager() {
    // All setup should be done here, such as support for networking, etc.
    // Normally this exists only to defeat instantiation
  }

  /**
   * A standard getInstance() in accordance with the singleton pattern
   * 
   * @return an instance of the active ModelManager
   */
  public static ModelManager getInstance() {
    return instance;
  }

  /**
   * The update() method updates the state of all entities
   */
  public void update() {
    // Loop through all the entities
	for (Entity ent : entities) {
	  // Remove the entity if it's marked itself for deletion
      if (ent.isToBeDeleted()) {
        entities.remove(ent);
        // Accelerate it's removal
        ent = null;
        // Skip the rest
        continue;
      }
      // Handle player stuff
      if (ent == this.getPlayer()) {
    	Ship player = (Ship) ent;
    	InputManager.getInstance().processInputs(player);
      }
      
      // Update the position of the entity
      PhysicsManager.updatePosition(ent);
    }
    ArrayList<Collision> collisions =
        (ArrayList<Collision>) collisionDetector.checkForCollisions(entities);
    for (Collision c : collisions) {
      // System.out.println("Collision");
      c.resolveCollision();
    }
  }

  /**
   * Syncs the client with the server
   */
  protected void sync() {
    // TODO
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
   * @param serial the serial number of the entity
   * @return the entity with the specified serial number if it exists, null otherwise
   */
  public Entity getEntity(int serial) {
    for (Entity ent : entities) {
      if (ent.getSerial() == serial)
        return ent;
    }
    return null;
  }

  /**
   * Getter for the entity list.
   * 
   * @return the entity list as a LinkedList<Entity>
   */
  public ConcurrentLinkedQueue<Entity> getEntities() {
    return entities;
  }

  /**
   * Setter for the entity list (Used when client updates the game state according to a full
   * snapshot received)
   */
  public void setEntities(ConcurrentLinkedQueue<Entity> entities) {
    this.entities = entities;
  }
  
  /**
   * Sets the designated ship player.
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
}
