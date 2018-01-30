package org.alien8.managers;

import java.util.LinkedList;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;

/**
 * This class implements the model at the core of the game itself. It is
 * responsible for the main loop that updates the game state 60 times a second.
 * It also keeps the record of game entities in a LinkedList<Entity>.
 * <p>
 * 
 * @version 1.0
 */
public class ModelManager implements Runnable {;
	
	/**
	 *  Volatile "running" boolean to avoid internal caching. 
	 *	Thread should stop when set to false.
	 */
	private volatile boolean running = false;
	private int lastSerial = 0;
	
	private static ModelManager instance = new ModelManager();
	private LinkedList<Entity> entities = new LinkedList<Entity>();
	private Thread thread;
	
	private int FPS = 0;

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
	 * Starts the main loop of the game.
	 */
	public synchronized void start() {
		// Do nothing if the game is already running
		if (running)
			return;
		running = true;
		
		thread = new Thread(this);
		thread.start();
		// Start the loop
	}

	/**
	 * The main loop of the game. A common way to implement it. 
	 * This implementation
	 * basically allows the renderer to do it's job separately from the update()
	 * method. If a certain computer tends to be slower on the render() side, then
	 * it can perform more fixed time updates in between frames to compensate.
	 * Faster computers wouldn't see any improvement.
	 */
	public void run() {
		long lastTime = getTime();
		long currentTime = 0;
		double catchUp = 0;

		int frameRate = 0;
		long frameTimer = getTime();

		while (running) {
			// Process user input. Alter the game state appropriately.
			processInput();
			
			currentTime = getTime();

			// Get the amount of update()s the model needs to catch up
			catchUp += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);
			// Update lastTime
			lastTime = currentTime;

			// Call update() as many times as needed to compensate before rendering
			while (catchUp >= 1) {
				update();
				catchUp--;
			}

			// Call the renderer
			render();
			frameRate++;

			// Update the FPS timer every FPS_FREQ^-1 seconds
			if (getTime() - frameTimer > Parameters.N_SECOND / Parameters.FPS_FREQ) {
				frameTimer += Parameters.N_SECOND / Parameters.FPS_FREQ;
				FPS = frameRate * Parameters.FPS_FREQ;
				frameRate = 0;
			}
		}
		System.out.println("stopped");
	}

	/**
	 * The update() method updates the state of all entities
	 */
	private void update() {
		// Call update for all entities
		for(Entity ent : entities) {
			ent.update();
		}
	}

	/**
	 * The render() method renders all entities to the screen in their current state
	 */
	private void render() {
		// Call render for all entities
		for(Entity ent : entities) {
			ent.render();
		}
	}

	/**
	 * Might pause the game. I don't know if this works yet.
	 */
	public void pause() {
		running = false;
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
	protected void processInput() {
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
	 * Getter for the latest FPS estimation.
	 * 
	 * @return
	 */
	public int getFPS() {
		return FPS;
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
	public Thread getThread() {
		return thread;
	}

	
	/**
	 * Gets current time in nanoseconds from the JVM
	 * @return current time in nanoseconds
	 */
	private long getTime() {
		return System.nanoTime();
	}

}
