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

  private double mass;
  private double speed;
  private double direction;
  private double length;
  private double width;
  private Position[] obb;

  /**
   * Empty constuctor. Should not be used. Here for technical purposes.
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
    this.position = position;
    this.direction = direction;
    this.speed = speed;
    this.mass = mass;
    initObb();
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
    if (this.serial == -1) {
      this.serial = serial;
    }
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

  public double getLength() {
    return length;
  }

  public void setLength(double length) {
    this.length = length;
  }

  public Position[] getObb() {
    return obb;
  }

  /**
   * Method to initialise the Oriented Bounding Box (OBB) of an Entity, using its Position,
   * direction, length and width.
   */
  public void initObb() {
    this.obb = new Position[4];
    // Get center point of box
    double centerX = position.getX();
    double centerY = position.getY();
    // First, calculate box as if it is facing north
    // Corners are labelled:
    // 0 1
    // 3 2
    obb[0] = new Position(centerX - width / 2, centerY + length / 2);
    obb[1] = new Position(centerX + width / 2, centerY + length / 2);
    obb[2] = new Position(centerX + width / 2, centerY - length / 2);
    obb[3] = new Position(centerX - width / 2, centerY - length / 2);


    // Now rotate box to correct orientation
    rotateObb(direction);
  }

  /**
   * Method to translate the Oriented Bounding Box (OBB) of an Entity by a difference in X and Y.
   * 
   * @param xdiff the amount to translate in the X direction
   * @param ydiff the amount to translate in the Y direction
   */
  public void translateObb(double xdiff, double ydiff) {
    for (Position corner : obb) {
      corner = new Position(corner.getX() + xdiff, corner.getY() + ydiff);
    }
  }

  public void rotateObb(double angle) {
    // Get center point of box
    double centerX = position.getX();
    double centerY = position.getY();
    // We perform the rotation as if it were around the origin (rather than the center of the box),
    // then translate the corner to find its true position
    for (Position corner : obb) {
      // Get current corner points
      double cornerX = corner.getX();
      double cornerY = corner.getY();
      // Translate corner point to origin
      double tempX = cornerX - centerX;
      double tempY = cornerY - centerY;
      // Perform rotation
      double rotatedX = tempX * Math.cos(angle) - tempY * Math.sin(angle);
      double rotatedY = tempX * Math.sin(angle) + tempY * Math.cos(angle);
      // Translate corner back to find true position
      cornerX = rotatedX + centerX;
      cornerY = rotatedY + centerY;
      // Set corner position
      corner = new Position(cornerX, cornerY);
    }
  }
}
