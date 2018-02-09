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

  private int lastSerial = 0;
  private static ModelManager instance = new ModelManager();
  private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();
  private CollisionDetector collisionDetector = new CollisionDetector();
  private Map map = new Map(Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 8, 8);


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
    InputManager im = InputManager.getInstance();
    // System.out.println(entities.size());
    for (Entity ent : entities) {
      if (ent.isToBeDeleted()) {
        entities.remove(ent);
        // Accelerate it's removal
        ent = null;
        // Skip the rest
        continue;
      }
      if (ent.getSerial() == 1) { // Then it's the player

        // Do movement first
        // Apply forward OR backward force
        if (im.wPressed())
          PhysicsManager.applyForce(ent, Parameters.SHIP_FORWARD_FORCE, ent.getDirection());
        else if (im.sPressed())
          PhysicsManager.applyForce(ent, Parameters.SHIP_BACKWARD_FORCE,
              PhysicsManager.shiftAngle(ent.getDirection() + Math.PI));
        // System.out.println(ent.getSpeed());
        // Apply rotation
        if (im.aPressed())
          PhysicsManager.rotateEntity(ent,
              (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
        if (im.dPressed())
          PhysicsManager.rotateEntity(ent,
              Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);

        // Apply "friction"
        ent.setSpeed(ent.getSpeed() * Parameters.FRICTION);

        // Update positions
        PhysicsManager.updatePosition(ent);

        // Prepare for shootings
        Ship sh = (Ship) ent;

        // Orientation
        sh.setTurretsDirection(im.mousePosition());

        if (im.lmbPressed())
          sh.frontTurretCharge();
        else
          sh.frontTurretShoot();

        if (im.rmbPressed())
          sh.rearTurretCharge();
        else
          sh.rearTurretShoot();

        if (im.spacePressed())
          sh.midTurretCharge();
        else
          sh.midTurretShoot();
      }

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

  public Entity getPlayer() {
    return getEntity(1); // change implementation later
  }
}