package org.alien8.managers;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;

/**
 * This class implements the model at the core of the game itself. It is
 * responsible for the main loop that updates the game state 60 times a second.
 * It also keeps the record of game entities in a LinkedList<Entity>.
 * <p>
 * 
 * @version 1.0
 */
public class ModelManager {
	
	private int lastSerial = 0;
	
	private static ModelManager instance = new ModelManager();
	private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();

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
		for(Entity ent : entities) {
			if(ent.isToBeDeleted()) {
				entities.remove(ent);
				// Accelerate it's removal
				ent = null;	
				// Skip the rest
				continue;
			}
			if(ent.getSerial() == 1) { // Then it's the player
				// Do movement first
				// Apply forward OR backward force
				if(im.wPressed())
					PhysicsManager.applyForce(ent, Parameters.SHIP_FORWARD_FORCE, ent.getDirection());
				else if(im.sPressed())
					PhysicsManager.applyForce(ent, Parameters.SHIP_BACKWARD_FORCE, PhysicsManager.shiftAngle(ent.getDirection() + Math.PI));
				
				// Apply rotation
				if(im.aPressed())
					PhysicsManager.rotateEntity(ent, false);
				if(im.dPressed())
					PhysicsManager.rotateEntity(ent, true);
				
				// Apply "friction"
				ent.setSpeed(ent.getSpeed()*Parameters.FRICTION);
				
				// Update positions
				PhysicsManager.updatePosition(ent);
			}
			
			PhysicsManager.updatePosition(ent);
			
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
		for(Entity ent : entities) {
			if(ent.getSerial() == serial)
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

}
