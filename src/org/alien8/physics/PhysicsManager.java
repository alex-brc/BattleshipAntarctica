package org.alien8.physics;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import net.jafama.FastMath;

/**
 * This class contains methods related to physics operations that are applied in the game world.
 *
 */
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
    double speedX = e.getSpeed() * FastMath.cos(e.getDirection());
    double speedY = e.getSpeed() * FastMath.sin(e.getDirection());
    // Calculate x and y components of acceleration
    double accelerationX = acceleration * FastMath.cos(direction);
    double accelerationY = acceleration * FastMath.sin(direction);
    // Calculate x and y components of new speed
    double newSpeedX = speedX + accelerationX;
    double newSpeedY = speedY + accelerationY;

    // Update the speed and direction of the Entity
    e.setSpeed(FastMath.sqrt((newSpeedX * newSpeedX) + (newSpeedY * newSpeedY)));
    if (e.getSpeed() > Parameters.SHIP_TOP_SPEED_FORWARD)
      e.setSpeed(Parameters.SHIP_TOP_SPEED_FORWARD);
    // TODO:this is causing weird stuff.
    // e.setDirection(shiftAngle(FastMath.atan(newSpeedY / newSpeedX)));
  }

  /**
   * Slows the entity down by a constant scaling factor. Less complex than dealing with forces.
   * 
   * @param e the entity to apply friction to
   */
  public static void applyFriction(Entity e) {
    e.setSpeed(e.getSpeed() * Parameters.FRICTION);
    // Makes friction less CPU-intensive sometimes
    if (e.getSpeed() <= 0.001d) {
      e.setSpeed(0);
    }
  }

  /**
   * Updates the position of an Entity. Must be called every tick.
   * 
   * @param e The Entity to be updated.
   */
  public static void updatePosition(Entity e, boolean[][] iceGrid) {
    Position pos = e.getPosition();
    double speed = e.getSpeed();
    double direction = e.getDirection();
    // Calculate difference in x and y
    double xdiff = speed * FastMath.cos(direction);
    double ydiff = speed * FastMath.sin(direction);
    // Sets the new position
    e.setPosition(new Position(pos.getX() + xdiff, pos.getY() + ydiff));

    // Update the Oriented Bounding Box
    e.translateObb(xdiff, ydiff);
    // Deal with terrain collision
    e.dealWithInIce(iceGrid);
    // Push the player back inside the map if they are out of bounds
    e.dealWithOutOfBounds();
  }

  /**
   * Rotates an Entity by an amount proportional to its speed. Must be called every tick that the
   * Entity is being rotated.
   * 
   * @param e The Entity to be rotated.
   * @param clockwise Set to true if the rotation is clockwise, false if anti-clockwise.
   */
  public static void rotateEntity(Entity e, double angle) {
    /**
     * First, squeeze the speed into the [0,4pi/5] interval 4pi/5 since we 
     * want the ship at top speed to be able to turn slowly
     *
     * g(x) : [0,SHIP_TOP_SPEED_FORWARD] -> [0,4pi/5]
     */
    double squeezedSpeed = e.getSpeed() * (4 * FastMath.PI / 5) / Parameters.SHIP_TOP_SPEED_FORWARD;
    /**
     * Then put this speed through the function:
     *
     * f : (0,PI) -> [0,1.5] f(x) = sin^2(x) + 0.5,
     *
     */
    double rotModifier = FastMath.pow(FastMath.sin(squeezedSpeed), 2) + 0.5;
    // Then apply this modifier to the angle, with a parametrised weight
    angle *= rotModifier * Parameters.ROTATION_MODIFIER;

    // Update the direction of the Entity, but also the bounding box
    e.setDirection(shiftAngle(e.getDirection() + angle));
    e.rotateObb(angle);
  }

  /**
   * Shifts the angle in radians to [0,2pi] interval.
   * 
   * @param rads the angle in radians to shift.
   */
  public static double shiftAngle(double rads) {
    while (rads < 0)
      rads += 2 * FastMath.PI;
    while (rads >= 2 * FastMath.PI)
      rads -= 2 * FastMath.PI;
    return rads;
  }
}
