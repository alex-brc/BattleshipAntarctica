package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;

/**
 * Developer's notes:
 * 
 * All calculations relative to the ship consider the 
 * ship "located" at the position right under the center 
 * of mass of the ship. Turrets are moved together with it
 * with that consideration in mind. 
 * 
 */
public class Ship extends Entity {

	private Turret frontTurret;
	private Turret rearTurret;
	private Turret midTurret;
		
	public Ship(Position position, double direction) {
		super(position, direction, 0, Parameters.SHIP_MASS);
	
		frontTurret = new Turret(position);
		midTurret = new Turret(position);
		rearTurret = new Turret(position);
		
		setTurretsDirection(new Position(0,0), direction);
		setTurretsPosition(position, direction);
		
	}
	
	@Override
	public void setPosition(Position position) {
		this.position = position;
		
		setTurretsPosition(position, this.getDirection());
	}
	
	/**
	 * Sets the direction for all turrets for the new mouse position
	 * considering the current ship direction for limiting the movement
	 * of turrets
	 * @param mousePosition the latest position of the cursor
	 */
	public void setTurretsDirection(Position mousePosition, double direction) {
		// For a natural look and feel, the turrets will have a 270* degrees of motion,
		// becoming unable to shoot "through" the ship. The exception is the middle turret
		// which is supposedly mounted above the others and can fire with 360* degrees 
		// of motion

		// For s = abs. angle of ship, a = angle of the turret and 
		// ra = angle of the turret relative to the ship, ra is
		//         ra = a + (pi - s);
		double ra = 0;
		
		// Front
		double angle = frontTurret
				.getPosition()
				.getAngleTo(mousePosition);
		
		ra = angle + (Math.PI - this.getDirection());
		// Range of motion: [7pi/4, 5pi/4]
		if( ra < 5.0 * Math.PI / 4 || ra > 7.0 * Math.PI / 4)
			frontTurret.setDirection(angle);

		// Rear
		angle = rearTurret
				.getPosition()
				.getAngleTo(mousePosition);
		
		ra = angle + (Math.PI - this.getDirection());
		// Range of motion: [3pip/4,pi/4]
		if( ra < 1.0 * Math.PI / 4 || ra > 3.0 * Math.PI / 4)
			rearTurret.setDirection(angle);

		// Mid
		angle = frontTurret
				.getPosition()
				.getAngleTo(mousePosition);
		// No need to get relative angle. 
		// This can go shoot it wants
		midTurret.setDirection(angle);	
	}
	
	/**
	 * Sets the position for all turrets considering the ship's position
	 * and direction
	 * @param shipPosition the ship's position
	 * @param direction the ship's direction
	 */
	public void setTurretsPosition(Position shipPosition, double direction) {
		// The radius from the ship position to the turret position
		// Chosen to be a fifth of the length of the ship AWAY from
		// the tip of the ship.
		double r = 2*0.2*Parameters.SHIP_LENGTH;
		
		frontTurret
			.setPosition(shipPosition.addPosition(new Position(
					r*Math.cos(direction),
					r*Math.sin(direction))));
		
		rearTurret
			.setPosition(shipPosition.addPosition(new Position(
					(-r)*Math.cos(direction),
					(-r)*Math.sin(direction))));
		
		midTurret.setPosition(shipPosition);
	}
	
	public double getFrontTurretDirection() {
		return frontTurret.getDirection();
	}

	public double getRearTurretDirection() {
		return rearTurret.getDirection();
	}

	public double getMidTurretDirection() {
		return midTurret.getDirection();
	}
	
	public Position getFrontTurretPosition() {
		return frontTurret.getPosition();
	}
	
	public Position getMidTurretPosition() {
		return rearTurret.getPosition();
	}
	
	public Position getRearTurretPosition() {
		return rearTurret.getPosition();
	}
}

