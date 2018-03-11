package org.alien8.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.ai.AIController;
import org.alien8.client.ClientInputSample;
import org.alien8.client.InputManager;
import org.alien8.items.HealthPickup;
import org.alien8.items.Pickup;
import org.alien8.items.PlaneDropper;
import org.alien8.mapgeneration.Map;
import org.alien8.physics.Collision;
import org.alien8.physics.CollisionDetector;
import org.alien8.physics.PhysicsManager;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.Bullet;
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

  public void updateServer(ConcurrentHashMap<Player, ClientInputSample> latestCIS) {
    // Loop through all the entities
	//System.out.println(entities.size());
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
      if(ent instanceof Ship) {
    	  // Handle player stuff
    	  Ship ship = (Ship) ent;
    	  ai = Server.getInstance().getAIByShip(ship);
    	  if(ai != null) {
    		  ai.update();
    	  }
    	  else for (Player p : latestCIS.keySet()) {
    		  if (ent == p.getShip()) {
    			  Ship s = (Ship) ent;
    			  ClientInputSample cis = latestCIS.get(p);
    			  InputManager.processInputs(s, cis);
    			  break;
    		  }
    	  }
    	  
      }
      
      // Update the position of the entity
      PhysicsManager.updatePosition(ent, map.getIceGrid());
    }
    ArrayList<Collision> collisions = collisionDetector.checkForCollisions(entities);
    for (Collision c : collisions) {
      // System.out.println("Resolving collision");
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
      this.lastSerial = 0;
    }

    // Add updated entities
    for (EntityLite el : entitiesLite) {
      if (el.entityType == 0) { // Player Ship
        Ship s = new Ship(el.position, el.direction, el.colour);
        s.setSpeed(el.speed);
        s.setHealth(el.health);
        s.getFrontTurret().setDirection(el.frontTurretDirection);
        s.getRearTurret().setDirection(el.rearTurretDirection);
        s.getFrontTurret().setDistance(el.frontTurretCharge);
        s.getRearTurret().setDistance(el.rearTurretCharge);
        
        if (el.toBeDeleted) {
          s.delete();
        }

        if (el.clientIP.equals(
        		clientIP) && 
        		el.clientUdpPort == 
        		clientUdpPort) { // Client's ship
          this.setPlayer(s);
        }

        this.addEntity(s);
        s.setSerial(el.serial);
      } else if (el.entityType == 1) { // AI Ship
        Ship s = new Ship(el.position, el.direction, el.colour);
        s.setSpeed(el.speed);
        s.setHealth(el.health);
        s.getFrontTurret().setDirection(el.frontTurretDirection);
        s.getRearTurret().setDirection(el.rearTurretDirection);

        if (el.toBeDeleted) {
          s.delete();
        }

        this.addEntity(s);
      } else if (el.entityType == 2) { // Bullet
        Bullet b = new Bullet(el.position, el.direction, el.distance, el.source);
        b.setSpeed(el.speed);
        b.setTravelled(el.travelled);

        if (el.toBeDeleted) {
        	b.delete();
        }

        this.addEntity(b);
      } else if(el.entityType == 3) { // Pickup
    	  Pickup p = null;
    	  switch(el.pickupType) {
    	  case Pickup.HEALTH_PICKUP:
    		  p = new HealthPickup(el.position);
    		  break;
    	  }

    	  if(el.toBeDeleted) {
    		  p.delete();
    	  }
    	  
    	  this.addEntity(p);
      } else if(el.entityType == 4) { // Plane
    	  PlaneDropper pd = new PlaneDropper(el.position, el.direction);
    	  System.out.println("got plane at " + pd.getPosition());
    	  if(el.toBeDeleted) {
    		  pd.delete();
    	  }
    	  
    	  this.addEntity(pd);
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
