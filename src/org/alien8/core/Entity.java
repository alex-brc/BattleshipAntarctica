package org.alien8.core;

import org.alien8.core.geometry.Position;

public abstract class Entity {
	private Position position;
	private ID id;
	private boolean rendered;

	/**
	 * Basic constructor for an entity
	 * 
	 * @param position the XY coordinates for this entity
	 * @param id the ID of this entity
	 */
	public Entity(Position position, ID id, boolean rendered) {
		this.setPosition(position);
		this.setId(id);
		this.setRendered(rendered);
	}
	/**
	 * Basic constructor for an entity
	 * 
	 * @param x X coordinate for this entity
	 * @param y Y coordinate for this entity
	 * @param id the ID of this entity
	 */
	public Entity(double x, double y, ID id, boolean rendered) {
		this.setPosition(new Position(x,y));
		this.setId(id);
		this.setRendered(rendered);
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
	protected void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * @return the id of this entity
	 */
	public ID getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id the id of this entity
	 */
	private void setId(ID id) {
		this.id = id;
	}

	/**
	 * @return true if the entity will be rendered, false otherwise
	 */
	public boolean isRendered() {
		return rendered;
	}

	/**
	 * @param rendered true if the entity will be rendered, false otherwise
	 */
	protected void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	
}
