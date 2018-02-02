package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.physics.Position;

public class Turret {
	
	// Orientation in radians
	private double direction;

	public Turret(double direction) {
		this.direction = direction;
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

}
