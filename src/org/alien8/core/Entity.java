package org.alien8.core;

import org.alien8.core.geometry.Position;
/**
 * This abstract class implements the generic Entity. All things that are part
 * of the game map are instances of classes that implement Entity. Such could 
 * be the Player class, the Terrain class, etc.
 *
 */
public abstract class Entity {
	private Position position;
	private Type type;
	private long serial = -1;

	/**
	 * Basic constructor for an entity
	 * 
	 * @param position the XY coordinates for this entity
	 * @param id the ID of this entity. The ID determines the type of the entity
	 */
	public Entity(Position position, Type type) {
		this.setPosition(position);
		this.setId(type);
	}
	/**
	 * Basic constructor for an entity
	 * 
	 * @param x X coordinate for this entity
	 * @param y Y coordinate for this entity
	 * @param id the ID of this entity. The ID determines the type of the entity
	 */
	public Entity(double x, double y, Type type) {
		this.setPosition(new Position(x,y));
		this.setId(type);
	}
	
	/**
	 * This method is called in the game loop. It must implement the behaviour
	 * of the entity on an update step (if it needs to).
	 */
	public abstract void update();
	
	/**
	 * This method is called in the game loop. It should use the renderer to be
	 * displayed on screen (if it is the case)
	 */
	public abstract void render();

	/**
	 * @return the position in XY coordinates
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * @param position the position to set, in XY coordinates
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * @return the id of this entity
	 */
	public Type getType() {
		return type;
	}
	
	private void setId(Type type) {
		this.type = type;
	}
	
	
	public long getSerial() {
		return serial;
	}
	
	public void setSerial(long serial) {
		if(this.serial == -1)
			this.serial = serial;
		// Else, do nothing. Only works once.
	}
}
