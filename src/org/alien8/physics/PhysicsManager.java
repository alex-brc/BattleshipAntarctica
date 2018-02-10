package org.alien8.physics;

import java.util.Arrays;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;

public class PhysicsManager {
  /**
   * Applies a force to an Entity. Must be called for every tick that the force is applied.
   * 
   * @param e The entity the force is being applied to.
   * @param magnitude The magnitude of the force being applied.
   * @param angle The angle at which the force is being applied.
   */
  public static void applyForce(Entity e, double magnitude, double direction) {
    // Calculate acceleration caused by force
    double acceleration = magnitude / e.getMass();
    // Calculate x and y components of Entity's speed
    double speedX = e.getSpeed() * Math.cos(e.getDirection());
    double speedY = e.getSpeed() * Math.sin(e.getDirection());
    // Calculate x and y components of acceleration
    double accelerationX = acceleration * Math.cos(direction);
    double accelerationY = acceleration * Math.sin(direction);
    // Calculate x and y components of new speed
    double newSpeedX = speedX + accelerationX;
    double newSpeedY = speedY + accelerationY;

    // Update the speed and direction of the Entity
    e.setSpeed(Math.sqrt((newSpeedX * newSpeedX) + (newSpeedY * newSpeedY)));
    if(e.getSpeed() > Parameters.SHIP_TOP_SPEED_FORWARD)
    	e.setSpeed(Parameters.SHIP_TOP_SPEED_FORWARD);
    // TODO:this is causing weird stuff. 
    // e.setDirection(shiftAngle(Math.atan(newSpeedY / newSpeedX)));
  }

  /**
   * Updates the position of an Entity. Must be called every tick.
   * 
   * @param e The Entity to be updated.
   */
  public static void updatePosition(Entity e) {
    Position pos = e.getPosition();
    double speed = e.getSpeed();
    double direction = e.getDirection();
    // Calculate difference in x and y
    double xdiff = speed * Math.cos(direction);
    double ydiff = speed * Math.sin(direction);
    // Sets the new position
    e.setPosition(new Position(pos.getX() + xdiff, pos.getY() + ydiff));
    // Update the Oriented Bounding Box 
    e.translateObb(xdiff, ydiff);
  }

  /**
   * Rotates an Entity by an amount proportional to its speed. Must be called every tick that the
   * Entity is being rotated.
   * 
   * @param e The Entity to be rotated.
   * @param clockwise Set to true if the rotation is clockwise, false if anti-clockwise.
   */
  public static void rotateEntity(Entity e, double angle) {
	/** First, squeeze the speed into the [0,pi] interval
	 *
	 * g(x) : [0,SHIP_TOP_SPEED_FORWARD] -> [0,pi] */
	double squeezedSpeed = e.getSpeed() * Math.PI / Parameters.SHIP_TOP_SPEED_FORWARD;
	System.out.println(squeezedSpeed);
	/** 
	 * Then put this speed through the function:
	 *
	 * f : (0,PI) -> [0,1]
	 * f(x) = sin^2(x),	
	 * 
	 * to get a natural rotation modifier. */
	double rotModifier = Math.sin(squeezedSpeed);
	rotModifier *= rotModifier;
	rotModifier += 0.3;
	System.out.println(angle + " " + rotModifier);
	// Then apply this modifier to the angle, with a parametrised weight
	angle *= rotModifier * Parameters.ROTATION_MODIFIER;
	System.out.println(angle);
    // Update the direction of the Entity, but also the bounding box
    e.setDirection(shiftAngle(e.getDirection() + angle));
    e.rotateObb(angle);
  }
  
  /**
   * Shifts the angle in radians to [0,2pi) interval.
   * 
   * @param rads the angle in radians to shift.
   */
  public static double shiftAngle(double rads) {
	  while(rads < 0)
		  rads += 2*Math.PI;
	  while(rads >= 2*Math.PI)
		  rads -= 2*Math.PI;
	  return rads;
  }

  // /**
  // * Calculates the result of a collision between two Entities.
  // *
  // * @param e1 The first Entity.
  // * @param e2 The second Entity.
  // */
  // public static void calculateCollision(Entity e1, Entity e2) {
  // // Still working on this
  // }
}
