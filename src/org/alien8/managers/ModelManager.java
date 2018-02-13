package org.alien8.managers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.client.ClientInputSample;
import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.mapgeneration.Map;
import org.alien8.physics.AABB;
import org.alien8.physics.Collision;
import org.alien8.physics.CollisionDetector;
import org.alien8.physics.PhysicsManager;
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
  private static ModelManager instance = new ModelManager();
  private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();
  private CollisionDetector collisionDetector = new CollisionDetector();
  private Map map = new Map(Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 8, 8);
  private Ship player;

  private ModelManager() {
    // All setup should be done here, such as support for networking, etc.
    // Normally this exists only to defeat instantiation
    List<AABB> aabbs = map.getAABBs();
    for (AABB aabb : aabbs) {
      entities.add(aabb.getEntity());
    }
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
   * Server version of update()
   */
  public void updateServer(InetAddress clientIP, ClientInputSample cis) {
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
      if (ent == Server.getPlayerByIp(clientIP).getShip()) {
        Ship player = (Ship) ent;

        // Apply forward OR backward force
        if (cis.wPressed)
          PhysicsManager.applyForce(player, Parameters.SHIP_FORWARD_FORCE, player.getDirection());
        else if (cis.sPressed)
          PhysicsManager.applyForce(player, Parameters.SHIP_BACKWARD_FORCE,
              PhysicsManager.shiftAngle(player.getDirection() + Math.PI));

        // Apply rotation
        if (cis.aPressed)
          PhysicsManager.rotateEntity(player,
              (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
        if (cis.dPressed)
          PhysicsManager.rotateEntity(player,
              Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);

        // Apply "friction"
        PhysicsManager.applyFriction(player);

        // Prepare for shooting
        // Orientation
        player.setTurretsDirection(cis.mousePosition);

        if (cis.lmbPressed)
          player.frontTurretCharge();
        else
          player.frontTurretShoot();

        if (cis.rmbPressed)
          player.rearTurretCharge();
        else
          player.rearTurretShoot();

        if (cis.spacePressed)
          player.midTurretCharge();
        else
          player.midTurretShoot();
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
   * Create an entity from a compressed entity and add it to the set of entities
   */
  public void addEntityByLite(EntityLite el) {
    if (el.type == 0) { // Ship
      Ship s = new Ship(el.position, el.direction);
      s.setSerial(el.serial);
      s.setSpeed(el.speed);
      s.setHealth(el.health);

      if (el.toBeDeleted) {
        s.delete();
      }

      this.addEntity(s);
    } else if (el.type == 1) { // Small bullet
      SmallBullet sb = new SmallBullet(el.position, el.direction, el.distance, el.source);
      sb.setSerial(el.serial);
      sb.setSpeed(el.speed);
      sb.setTravelled(el.travelled);

      if (el.toBeDeleted) {
        sb.delete();
      }

      this.addEntity(sb);
    } else if (el.type == 2) { // Big bullet
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

  /**
   * Syncs the client with the server
   */
  public void sync(ArrayList<EntityLite> difference) {
    for (EntityLite es : difference) {
      boolean isNewEntity = true;

      for (Entity e : entities) {
        if (es.serial == e.getSerial() && e instanceof Ship && es.type == 0) { // Ship
          Ship s = (Ship) e;
          s.setPosition(es.position);
          s.setDirection(es.direction);
          s.setSpeed(es.speed);
          s.setHealth(es.health);
          s.initObb();

          if (es.toBeDeleted) {
            s.delete();
          }

          isNewEntity = false;
        } else if (es.serial == e.getSerial() && e instanceof SmallBullet && es.type == 1) { // SmallBullet
          SmallBullet sb = (SmallBullet) e;
          sb.setPosition(es.position);
          sb.setDirection(es.direction);
          sb.setSpeed(es.speed);
          sb.setTravelled(es.travelled);
          sb.initObb();

          if (es.toBeDeleted) {
            sb.delete();
          }

          isNewEntity = false;
        } else if (es.serial == e.getSerial() && e instanceof BigBullet && es.type == 2) { // BigBullet
          BigBullet bb = (BigBullet) e;
          bb.setPosition(es.position);
          bb.setDirection(es.direction);
          bb.setSpeed(es.speed);
          bb.setTravelled(es.travelled);
          bb.initObb();

          if (es.toBeDeleted) {
            bb.delete();
          }

          isNewEntity = false;
        }
      }

      if (isNewEntity) {
        addEntityByLite(es);
      }
    }
  }

  public void fullSync(ArrayList<EntityLite> entitiesLite) {
    for (EntityLite es : entitiesLite) {
      addEntityByLite(es);
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
   * Getter for the entity list.
   * 
   * @return the entity list as a LinkedList<Entity>
   */
  public ConcurrentLinkedQueue<Entity> getEntities() {
    return entities;
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

  public void setMap(Map m) {
    map = m;
  }
}
