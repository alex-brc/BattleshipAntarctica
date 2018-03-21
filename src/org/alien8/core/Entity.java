package org.alien8.core;

import java.io.Serializable;
import org.alien8.physics.Position;
import net.jafama.FastMath;

/**
 * This abstract class implements the generic Entity. All objects in the game which move around and
 * have some sort of behaviour are instances of classes that implement Entity.
 *
 */
public abstract class Entity implements Serializable, Cloneable {

  private static final long serialVersionUID = 1635902892337937842L;
  protected Position position;
  protected long serial = -1;
  protected boolean toBeDeleted = false;
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
   * @param position the XY coordinates for the Entity
   * @param direction the direction the Entity is facing, in radians with 0 representing an Entity
   *        pointing to the right
   * @param speed the speed with which an Entity travels
   * @param mass the mass of the Entity
   * @param length the length of the Entity
   * @param width the width of the Entity
   */
  public Entity(Position position, double direction, double speed, double mass, double length,
      double width) {
    this.position = position;
    this.direction = direction;
    this.speed = speed;
    this.mass = mass;
    this.length = length;
    this.width = width;
    // Initialise the bounding box
    initObb();
  }

  /**
   * Constructor for an Entity that includes health.
   * 
   * @param position the XY coordinates for the Entity
   * @param direction the direction the Entity is facing, in radians with 0 representing an Entity
   *        pointing to the right
   * @param speed the speed with which an Entity travels
   * @param mass the mass of the Entity
   * @param length the length of the Entity
   * @param width the width of the Entity
   * @param health the initial health of the Entity
   */
  public Entity(Position position, double direction, double speed, double mass, double length,
      double width, double health) {
    this.position = position;
    this.direction = direction;
    this.speed = speed;
    this.mass = mass;
    this.length = length;
    this.width = width;
    this.health = health;
    // Initialise the bounding box
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

  /**
   * @return the unique serial ID of the Entity.
   */
  public long getSerial() {
    return serial;
  }

  /**
   * @param serial the serial to set
   */
  public void setSerial(long serial) {
    this.serial = serial;
  }

  /**
   * @return a boolean giving {@code true} if the Entity is to be deleted, {@code false} if not
   */
  public boolean isToBeDeleted() {
    return toBeDeleted;
  }

  /**
   * @return the mass of the Entity
   */
  public double getMass() {
    return mass;
  }

  /**
   * @param mass the mass to set
   */
  public void setMass(double mass) {
    this.mass = mass;
  }

  /**
   * @return the speed of the Entity
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * @param speed the speed to set
   */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * @return the direction of the Entity
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
   * @return the length of the Entity
   */
  public double getLength() {
    return length;
  }

  /**
   * @param length the length to set
   */
  public void setLength(double length) {
    this.length = length;
  }

  /**
   * @return the width of the Entity
   */
  public double getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * @return the health of the Entity
   */
  public double getHealth() {
    return health;
  }

  /**
   * @param health the health to set
   */
  public void setHealth(double health) {
    this.health = health;
  }

  /**
   * @return the Oriented Bounding Box (OBB) of the entity
   */
  public Position[] getObb() {
    return obb;
  }

  /**
   * Initialises the Oriented Bounding Box (OBB) of the Entity, using its Position, direction,
   * length and width.
   */
  public void initObb() {
    this.obb = new Position[4];
    // Get center point of box
    double centerX = position.getX();
    double centerY = position.getY();
    // First, calculate box as if it is facing north
    // Corners are labelled:
    // 3 2
    // 0 1
    obb[0] = new Position(centerX - this.getLength() / 2, centerY + this.getWidth() / 2);
    obb[1] = new Position(centerX + this.getLength() / 2, centerY + this.getWidth() / 2);
    obb[2] = new Position(centerX + this.getLength() / 2, centerY - this.getWidth() / 2);
    obb[3] = new Position(centerX - this.getLength() / 2, centerY - this.getWidth() / 2);

    // Now rotate box to correct orientation
    rotateObb(this.getDirection());
  }

  /**
   * Translates the Oriented Bounding Box (OBB) of an Entity by a difference in X and Y.
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

  /**
   * Rotates the Oriented Bounding Box (OBB) of an Entity by an angle.
   * 
   * @param angle the angle to rotate by
   */
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

  /**
   * Checks for equality between this Entity and another.
   * 
   * @param e the other Entity
   * @return {@code true} if the Entities are equal, {@code false} if not
   */
  public boolean equals(Entity e) {
    if (position.equals(e.position) && serial == e.serial && toBeDeleted == e.toBeDeleted
        && mass == e.mass && speed == e.speed && direction == e.direction && length == e.length
        && width == e.width && health == e.health && obbEquals(e.obb)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks for equality between this Entity's Oriented Bounding Box (OBB), and that of another
   * Entity's.
   * 
   * @param obb2 the other OBB
   * @return {@code true} if the OBB's are equal, {@code false} if not
   */
  public boolean obbEquals(Position[] obb2) {
    for (int i = 0; i < 4; i++) {
      if (!obb[i].equals(obb2[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return {@code true} if the Entity is the player, {@code false} if not
   */
  public boolean isPlayer() {
    if (this.getSerial() == 1)
      return true;
    return false;
  }

  /**
   * Makes sure the Entity is not deleted.
   */
  public void save() {
    this.toBeDeleted = false;
  }

  /**
   * Deletes the Entity.
   */
  public void delete() {
    this.toBeDeleted = true;
  }

  /**
   * Creates and returns a copy of this object.
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Renders the Entity to the screen. Abstract as each class that extends Entity will implement it
   * differently.
   */
  public abstract void render();

  /**
   * Damages the Entity by the specified amount, removing the amount from the Entity's health.
   * 
   * @param damage the amount of damage to be dealt
   */
  public void damage(double damage) {
    // Dying handled externally
    health -= damage;
  }

  /**
   * Checks if the Entity is out of the bounds of the map.
   * 
   * @return {@code true} if the Entity is outside the map, {@code false} if the Entity is within
   *         the map
   */
  public boolean isOutOfBounds() {
    double x = this.getPosition().getX();
    double y = this.getPosition().getY();
    if (x < 0 || x > Parameters.MAP_WIDTH || y < 0 || y > Parameters.MAP_HEIGHT)
      return true;
    return false;
  }

  /**
   * Checks if the Entity is outside the bounds of the map, and deals with this appropriately.
   */
  public abstract void dealWithOutOfBounds();

  /**
   * Checks if the Entity is inside ice on the map, and deals with this appropriately.
   * 
   * @param iceGrid a 2D array of booleans where {@code true } represents ice, and {@code false}
   *        represents water
   */
  public abstract void dealWithInIce(boolean[][] iceGrid);
}
