package org.alien8.physics;

import org.alien8.core.Entity;

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
    e.setDirection(Math.atan(newSpeedY / newSpeedX));
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
    // Calculate the new x and y positions
    double x = pos.getX() + speed * Math.cos(direction);
    double y = pos.getY() + speed * Math.sin(direction);
    // Sets the new position
    e.setPosition(new Position(x, y));
  }

  /**
   * Rotates an Entity by an amount proportional to its speed. Must be called every tick that the
   * Entity is being rotated.
   * 
   * @param e The Entity to be rotated.
   * @param clockwise Set to true if the rotation is clockwise, false if anti-clockwise.
   */
  public static void rotateEntity(Entity e, boolean clockwise) {
    // This modifier can be tweaked during testing
    double modifier = 0.2;
    if (clockwise) {
      e.setDirection(e.getDirection() + modifier * e.getSpeed());
    } else {
      e.setDirection(e.getDirection() - modifier * e.getSpeed());
    }
  }

  /**
   * Calculates the result of a collision between two Entities.
   * 
   * @param e1 The first Entity.
   * @param e2 The second Entity.
   */
  public static void calculateCollision(Entity e1, Entity e2) {
    // Still working on this
  }
}
