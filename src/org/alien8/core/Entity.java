package org.alien8.core;

import org.alien8.physics.Position;

/**
 * This abstract class implements the generic Entity. All things that are part of the game map are
 * instances of classes that implement Entity. Such could be the Player class, the Terrain class,
 * etc.
 *
 */
public abstract class Entity {
  protected Position position;
  protected long serial = -1;

  protected double mass;
  protected double speed;
  protected double direction;

  /**
   * Empty constuctor. Should not be used.
   * Here for technical purposes. 
   */
  protected Entity() {
	  
  }
  /**
   * Basic constructor for an entity
   * 
   * @param position the XY coordinates for this entity
   * @param id the ID of this entity. The ID determines the type of the entity
   */
  public Entity(Position position, double direction, double speed, double mass) {
    this.setPosition(position);
    this.setDirection(direction);
    this.setSpeed(speed);
    this.setMass(mass);
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
  public void setPosition(Position position) {
    this.position = position;
  }

  public long getSerial() {
    return serial;
  }

  public void setSerial(long serial) {
    if (this.serial == -1)
      this.serial = serial;
    // Else, do nothing. Only works once.
  }

  public double getMass() {
    return mass;
  }

  public void setMass(double mass) {
    this.mass = mass;
  }

  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public double getDirection() {
    return direction;
  }

  public void setDirection(double direction) {
    this.direction = direction;
  }
}
