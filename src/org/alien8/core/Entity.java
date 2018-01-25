package org.alien8.core;

import org.alien8.core.geometry.Position;

public abstract class Entity {
	private Position position;
	private ID id;

	/**
	 * Basic constructor for an entity
	 * 
	 * @param position the XY coordinates for this entity
	 * @param id the ID of this entity
	 */
	public Entity(Position position, ID id) {
		this.setPosition(position);
		this.setId(id);
	}
	/**
	 * Basic constructor for an entity
	 * 
	 * @param x X coordinate for this entity
	 * @param y Y coordinate for this entity
	 * @param id the ID of this entity
	 */
	public Entity(double x, double y, ID id) {
		this.setPosition(new Position(x,y));
		this.setId(id);
	}

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
	public ID getId() {
		return id;
	}
	
	private void setId(ID id) {
		this.id = id;
	}
}
