package org.alien8.ship;

import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;

public class Turret {
	// Type of bullets this turret shoots
	public static final int SMALL = 1;
	public static final int BIG = 2;
	// Position will be handled by Ship class
	private Position position;
	// Orientation in radians
	private double direction;
	// Last time it shot in nanoseconds
	private long lastShot;
	// Type of bullets this turret shoots
	private int type;
	// Cooldown of this turret
	private long cooldown;
	// Charged distance of this turret
	private double distance;

	public Turret(Position position, int type) {
		this.position = position;
		this.direction = 0;
		this.lastShot = System.currentTimeMillis();
		this.type = type;
		this.cooldown = (type == Turret.BIG) ? Parameters.BIG_BULLET_CD : Parameters.SMALL_BULLET_CD; 
	}
	
	public void charge() {
		this.distance++;
	}
	
	/**
	 * Shoots a bullet of the given type in the direction the turret is facing.
	 * @param type
	 */
	public void shoot() {
		if(distance == 0)
			return;
		
		ModelManager.getInstance()
		.addEntity(new Bullet(
				this.getPosition(),
				this.getDirection(),
				type,
				distance*Parameters.CHARGE_MODIFIER));
		
		this.startCooldown();
		this.distance = 0;
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
	public boolean isOnCooldon() {
		if(System.currentTimeMillis() - this.lastShot < this.cooldown)
			return true;
		return false;
	}
	
	private void startCooldown() {
		this.lastShot = System.currentTimeMillis();
	}

}
