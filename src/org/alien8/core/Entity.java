package org.alien8.core;

import java.io.Serializable;

import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

/**
 * This abstract class implements the generic Entity. All things that are part of the game map are
 * instances of classes that implement Entity. Such could be the Player class, the Terrain class,
 * etc.
 *
 */
public abstract class Entity implements Serializable, Cloneable {

  private static final long serialVersionUID = 1635902892337937842L;
  protected Position position;
  protected long serial = -1;
  protected boolean toBeDeleted = false;
  protected Properties properties = new Properties();
  protected Attributes attributes = new Attributes();
  
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
  public Entity(Position position, double direction, double speed, double mass, double length,
      double width) {
	this.position = position;
	this.properties.setDirection(direction);
	this.properties.setSpeed(speed);
	this.properties.setMass(mass);
	this.properties.setLength(length);
	this.properties.setWidth(width);
	
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
	    // Only works once.

  }

  public void delete() {
    this.toBeDeleted = true;
  }

  public boolean isToBeDeleted() {
    return toBeDeleted;
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
    obb[0] = new Position(centerX - this.getLength() / 2, centerY + this.getWidth() / 2);
    obb[1] = new Position(centerX + this.getLength() / 2, centerY + this.getWidth() / 2);
    obb[2] = new Position(centerX + this.getLength() / 2, centerY - this.getWidth() / 2);
    obb[3] = new Position(centerX - this.getLength() / 2, centerY - this.getWidth() / 2);


    // Now rotate box to correct orientation
    rotateObb(this.getDirection());
  }

  /**
   * Method to translate the Oriented Bounding Box (OBB) of an Entity by a difference in X and Y.
   * 
   * @param xdiff the amount to translate in the X direction
   * @param ydiff the amount to translate in the Y direction
   */
  public void translateObb(double xdiff, double ydiff) {
    Position[] result = new Position[4];
    for (int i = 0; i < 4; i++) {
      result[i] = new Position(obb[i].getX() + xdiff, obb[i].getY() + ydiff);
    }
    this.obb = result;
  }

  public void rotateObb(double angle) {
    // Get center point of box
    double centerX = position.getX();
    double centerY = position.getY();

    Position[] newObb = new Position[4];
    int i = 0;
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
      newObb[i] = corner;
      i++;
    }
    obb = newObb;
  }

  public abstract void render(Renderer r);

  public void damage(double damage) {
    this.setHealth(this.getHealth() - damage);
  }
  
  public boolean isPlayer() {
    if (this.getSerial() == 1)
      return true;
    return false;
  }

  public boolean isOutOfBounds() {
    double x = this.getPosition().getX();
    double y = this.getPosition().getY();
    if (x < 0 && x > Parameters.MAP_WIDTH && y < 0 && y > Parameters.MAP_HEIGHT)
      return true;
    return false;
  }

  public abstract void dealWithOutOfBounds();

  public double getHealth() {
	  return this.attributes.getHealth();
  }

  public void setHealth(double health) {
	  this.attributes.setHealth(health);
  }
  
  public double getMass() {
	  return this.properties.getMass();
  }

  public void setMass(double mass) {
	  this.properties.setMass(mass);
  }

  public double getSpeed() {
	  return this.properties.getSpeed();
  }

  public void setSpeed(double speed) {
	  this.properties.setSpeed(speed);
  }

  public double getDirection() {
	  return this.properties.getDirection();
  }

  public void setDirection(double direction) {
	  this.properties.setDirection(direction);
  }

  public double getLength() {
	  return this.properties.getLength();
  }

  public void setLength(double length) {
	  this.properties.setLength(length);
  }

  public double getWidth() {
	  return this.properties.getWidth();
  }

  public void setWidth(double width) {
	  this.properties.setWidth(width);
  }

  public Position[] getObb() {
	  return obb;
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
      return super.clone();
  }
}
