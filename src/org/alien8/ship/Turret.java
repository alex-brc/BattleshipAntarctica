package org.alien8.ship;

import org.alien8.physics.Position;

public class Turret {
	// Position will be handled by Ship class
	private Position position;
	// Orientation in radians
	private double direction;

	public Turret(Position position) {
		this.position = position;
	}
	
	/**
	 * @return the direction of the turret
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(double direction) {
		this.direction = direction;
	}

	/**
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

}
