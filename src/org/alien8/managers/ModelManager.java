package org.alien8.managers;

import java.util.LinkedList;

import org.alien8.core.Entity;

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
	private LinkedList<Entity> entities = new LinkedList<Entity>();

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
		// Call update for all entities
		for(Entity ent : entities) {
			ent.update();
		}
	}


	/**
	 * Syncs the client with the server
	 */
	protected void sync() {
		// TODO
	}

	/**
	 * Grabs the input and alters the game state accordingly.
	 */
	public void processInput() {
		//TODO
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
	public LinkedList<Entity> getEntities() {
		return entities;
	}
	/**
	 * 
	 * @return the thread this class runs
	 */

}
