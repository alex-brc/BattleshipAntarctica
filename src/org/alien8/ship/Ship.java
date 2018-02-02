package org.alien8.ship;

import org.alien8.core.Entity;

public class Ship extends Entity {

	private Turret frontTurret;
	private Turret rearTurret;
	private Turret midTurret;
	
	public Ship(double x, double y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	
	public double getFrontTurret() {
		return frontTurret.getDirection();
	}

	public double getRearTurret() {
		return rearTurret.getDirection();
	}

	public double getMidTurret() {
		return midTurret.getDirection();
	}
	
	public void setFrontTurret(double direction) {
		this.frontTurret.setDirection(direction);
	}
	
	public void setRearTurret(double direction) {
		this.rearTurret.setDirection(direction);
	}
	
	public void setMidTurret(double direction) {
		this.midTurret.setDirection(direction);
	}
}

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
