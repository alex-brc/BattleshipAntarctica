package org.alien8.ship;

import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;

public class Turret {
	// Position will be handled by Ship class
	private Position position;
	// Orientation in radians
	private double direction;
	// Last time it shot in nanoseconds
	private long lastShot;

	public Turret(Position position) {
		this.position = position;
		this.direction = 0;
		this.lastShot = 0;
	}
	
	/**
	 * Shoots a bullet of the given type in the direction the turret is facing.
	 * @param type
	 */
	public void shoot(int type) {
		int cd = 0;
		if(type == 2)
			cd = Parameters.BIG_BULLET_CD;
		if(type == 1)
			cd = Parameters.SMALL_BULLET_CD;
		
		// Current time
		long t = System.currentTimeMillis();
		
		if(lastShot < t - cd) {
			ModelManager.getInstance()
				.addEntity(new Bullet(
						this.getPosition(),
						this.getDirection(),
						type));
			lastShot = t;
		}
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

	/**
	 * @return the time of the last shot
	 */
	public double getLastShot() {
		return lastShot;
	}

}
