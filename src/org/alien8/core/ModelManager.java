package org.alien8.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.client.ClientInputSample;
import org.alien8.client.InputManager;
import org.alien8.mapgeneration.Map;
import org.alien8.physics.Collision;
import org.alien8.physics.CollisionDetector;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
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
  private Map map = new Map(Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 8, 8);
  private Ship player;

  private ModelManager() {
	  // All setup should be done here, such as support for networking, etc.
	  // Normally this exists only to defeat instantiation

	  entities = new ConcurrentLinkedQueue<Entity>();
	  collisionDetector = new CollisionDetector();
	  map = new Map(Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 8, 8);
	  
	  // List<AABB> aabbs = map.getAABBs();
	  // for (AABB aabb : aabbs) {
	  // entities.add(aabb.getEntity());
    }

  /**
   * A standard getInstance() in accordance with the singleton pattern
   * 
   * @return an instance of the active ModelManager
   */
  public static ModelManager getInstance() {
	if(instance == null)
		instance = new ModelManager();
    return instance;
  }

  /**
   * OBSOLETE
   * Old version, suitable for offline clients. Should not be used
   * 
   * The update() method updates the state of all entities
   */
  public void update() {
    // Loop through all the entities
    for (Entity ent : entities) {
      // Remove the entity if it's marked itself for deletion
      if (ent.isToBeDeleted()) {
    	if(this.getPlayer() == ent) {
    		playerDied();
    		continue;
    	}
        entities.remove(ent);
        // Accelerate it's removal
        ent = null;
        // Skip the rest
        continue;
      }
      // Handle player stuff
      if (ent == this.getPlayer()) {
        Ship player = (Ship) ent;
        // InputManager.processInputs(player);
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
        InputManager.processInputs(player, cis);      }

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
  public void sync(ArrayList<EntityLite> difference) {
	  for (EntityLite el : difference) {
		  if (el.entityType == 0 && el.changeType == 0) { // Modify Ship
	    	  for (Entity e : entities) {
	    		  if (el.serial == e.getSerial()) {
	    			  Ship s = (Ship) e;
	    			  s.setPosition(el.position);
	    	          s.setDirection(el.direction);
	    	          s.setSpeed(el.speed);
	    	          s.setHealth(el.health);
	    	          s.getFrontTurret().setDirection(el.frontTurretDirection);
	    	          s.getMidTurret().setDirection(el.midTurretDirection);
	    	          s.getRearTurret().setDirection(el.rearTurretDirection);
	    	          s.initObb();
	    	          
	    	          if (el.toBeDeleted) {
	    	        	  s.delete();
	    	          }	 
	    	          
	    	          break;
	    		  }
	    	  }
	      }
		  else if (el.entityType == 0 && el.changeType == 1) { // Add Ship
			  Ship s = new Ship(el.position, el.direction);
			  s.setSerial(el.serial);
	          s.setSpeed(el.speed);
	          s.setHealth(el.health);
	          s.getFrontTurret().setDirection(el.frontTurretDirection);
	          s.getMidTurret().setDirection(el.midTurretDirection);
	          s.getRearTurret().setDirection(el.rearTurretDirection);
	          
	          if (el.toBeDeleted) {
	        	  s.delete();
	          }
	          
	          this.addEntity(s);
		  }
		  else if (el.entityType == 1 && el.changeType == 0) { // Modify small bullet
			  for (Entity e : entities) {
	    		  if (el.serial == e.getSerial()) {
	    			  SmallBullet sb = (SmallBullet) e;
	    			  sb.setPosition(el.position);
	    	          sb.setDirection(el.direction);
	    	          sb.setSpeed(el.speed);
	    	          sb.setTravelled(el.travelled);
	    	          sb.initObb();
	    	          
	    	          if (el.toBeDeleted) {
	    	            sb.delete();
	    	          }
	    	          
	    	          break;
	    		  }
	    	  }
		  }
		  else if (el.entityType == 1 && el.changeType == 1) { // Add small bullet
			  SmallBullet sb = new SmallBullet(el.position, el.direction, el.distance, el.source);
		      sb.setSerial(el.serial);
		      sb.setSpeed(el.speed);
		      sb.setTravelled(el.travelled);
		      
		      if (el.toBeDeleted) {
		    	  sb.delete();
		      }
		      
		      this.addEntity(sb);
		  }
		  else if (el.entityType == 2 && el.changeType == 0) { // Modify big bullet
			  for (Entity e : entities) {
	    		  if (el.serial == e.getSerial()) {
					  BigBullet bb = (BigBullet) e;
			          bb.setPosition(el.position);
			          bb.setDirection(el.direction);
			          bb.setSpeed(el.speed);
			          bb.setTravelled(el.travelled);
			          bb.initObb();	
			          
			          if (el.toBeDeleted) {
			        	  bb.delete();
			          }
			          
			          break;
	    		  }
			  }
		  }
		  else if (el.entityType == 2 && el.changeType == 1) { // Add big bullet
			  BigBullet bb = new BigBullet(el.position, el.direction, el.distance, el.source);
		      bb.setSerial(el.serial);
		      bb.setSpeed(el.speed);
		      bb.setTravelled(el.travelled);

		      if (el.toBeDeleted) {
		        bb.delete();
		      }
		      
		      this.addEntity(bb);
		  }
		  else if (el.changeType == 2) { // Remove Entity
			  for (Entity e : entities) {
	    		  if (el.serial == e.getSerial()) {
	    			  entities.remove(e);
	    		  }
	    	  }
		  }
	  }
  }
  
  public void fullSync(ArrayList<EntityLite> entitiesLite) {
	  for (EntityLite el : entitiesLite) {
		  if (el.entityType == 0) {
			  Ship s = new Ship(el.position, el.direction);
			  s.setSerial(el.serial);
	          s.setSpeed(el.speed);
	          s.setHealth(el.health);
	          
	          if (el.toBeDeleted) {
	        	  s.delete();
	          }
	          
	          this.addEntity(s);
		  }
		  else if (el.entityType == 1) {
			  SmallBullet sb = new SmallBullet(el.position, el.direction, el.distance, el.source);
		      sb.setSerial(el.serial);
		      sb.setSpeed(el.speed);
		      sb.setTravelled(el.travelled);
		      
		      if (el.toBeDeleted) {
		    	  sb.delete();
		      }
		      
		      this.addEntity(sb);
		  }
		  else if (el.entityType == 2) {
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
  
  private void playerDied() {
	  System.out.println("Player died! Respawning");
	  Ship playerNew = new Ship(new Position(200,200), 0);
	  this.setPlayer(playerNew);
	  this.addEntity(playerNew);
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
