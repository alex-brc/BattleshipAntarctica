package org.alien8.core;

import java.io.Serializable;
import org.alien8.physics.Position;

import net.jafama.FastMath;

/**
 * This abstract class implements the generic Entity. All things that are part of the game map are
 * instances of classes that implement Entity. Such could be the Player class, the Terrain class,
 * etc.d
 *
 */
public abstract class Entity implements Serializable, Cloneable {

  private static final long serialVersionUID = 1635902892337937842L;
  protected Position position;
  protected long serial = -1;
  protected boolean toBeDeleted = false;
  // protected Properties properties = new Properties();
  // protected Attributes attributes = new Attributes();

  private double mass;
  private double speed;
  private double direction;
  private double length;
  private double width;
  private double health;
  private Position[] obb;

  /**
   * Basic constructor for an Entity.
   * 
   * @param position the XY coordinates for this entity
   * @param id the ID of this entity. The ID determines the type of the entity
   */
  public Entity(Position position, double direction, double speed, double mass, double length,
      double width) {
    this.position = position;
    this.direction = direction;
    this.speed = speed;
    this.mass = mass;
    this.length = length;
    this.width = width;

    initObb();
  }

  public Entity(Position position, double direction, double speed, double mass, double length,
      double width, double health) {
    this.position = position;
    this.direction = direction;
    this.speed = speed;
    this.mass = mass;
    this.length = length;
    this.width = width;
    this.health = health;

    initObb();
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
      double rotatedX = tempX * FastMath.cos(angle) - tempY * FastMath.sin(angle);
      double rotatedY = tempX * FastMath.sin(angle) + tempY * FastMath.cos(angle);
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

  public abstract void render();

  public void damage(double damage) {
    // Dying handled externally
    health -= damage;
  }

  public boolean isPlayer() {
    if (this.getSerial() == 1)
      return true;
    return false;
  }

  public boolean isOutOfBounds() {
    double x = this.getPosition().getX();
    double y = this.getPosition().getY();
    if (x < 0 || x > Parameters.MAP_WIDTH || y < 0 || y > Parameters.MAP_HEIGHT)
      return true;
    return false;
  }

  public abstract void dealWithOutOfBounds();

  public abstract void dealWithInIce(boolean[][] iceGrid);

  public double getHealth() {
    // return this.properties.getHealth();
    return health;
  }

  public void setHealth(double health) {
    // this.properties.setHealth(health);
    this.health = health;
  }

  public double getMass() {
    // return this.properties.getMass();
    return mass;
  }

  public void setMass(double mass) {
    // this.properties.setMass(mass);
    this.mass = mass;
  }

  public double getSpeed() {
    // return this.properties.getSpeed();
    return speed;
  }

  public void setSpeed(double speed) {
    // this.properties.setSpeed(speed);
    this.speed = speed;
  }

  public double getDirection() {
    // return this.properties.getDirection();
    return direction;
  }

  public void setDirection(double direction) {
    // this.properties.setDirection(direction);
    this.direction = direction;
  }

  public double getLength() {
    // return this.properties.getLength();
    return length;
  }

  public void setLength(double length) {
    // this.properties.setLength(length);
    this.length = length;
  }

  public double getWidth() {
    // return this.properties.getWidth();
    return width;
  }

  public void setWidth(double width) {
    // this.properties.setWidth(width);
    this.width = width;
  }

  public Position[] getObb() {
    return obb;
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
	this.serial = serial;
  }

  public void save() {
	  this.toBeDeleted = false;
  }
  
  public void delete() {
    this.toBeDeleted = true;
  }

  public boolean isToBeDeleted() {
    return toBeDeleted;
  }

  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
