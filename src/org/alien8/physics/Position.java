package org.alien8.physics;

import java.io.Serializable;
import org.alien8.core.Parameters;
import net.jafama.FastMath;

/**
 * Basic position class to pack together the X and Y coordinates for an entity.
 *
 */
public class Position implements Serializable {
  private static final long serialVersionUID = 2186201837660951453L;
  private double x;
  private double y;

  /**
   * Basic constructor for the Position class.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   */
  public Position(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * @return the X coordinate
   */
  public double getX() {
    return x;
  }

  /**
   * @param x the X coordinate to set
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * @return the Y coordinate
   */
  public double getY() {
    return y;
  }

  /**
   * @param y the Y coordinate to set
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Verifies if two positions in the XY plane are equal.
   * 
   * @param position the position to compare this position to
   * @return true if the two positions are equal, false otherwise
   */
  public boolean equals(Position position) {
    if ((int) this.getX() == (int) position.getX() && (int) this.getY() == (int) position.getY())
      return true;
    return false;
  }

  @Override
  public String toString() {
    return "X: " + this.getX() + " Y: " + this.getY();
  }

  /**
   * Simply adds the X's and Y's of the two position to
   * 
   * @param position the second point to add to this one
   * @return a new position resulting from the two
   */
  public Position addPosition(Position position) {
    return new Position(this.getX() + position.getX(), this.getY() + position.getY());
  }

  /**
   * Computes the angle between the X axis and the line formed by the point at this position and the
   * point at the given position
   * 
   * @param position the second point that determines the line
   * @return the angle in radians in [0,2pi)
   */
  public double getAngleTo(Position position) {
    return FastMath.atan2( // Intentionally left out intermediary variables for speed
        position.getX() - this.getX(), // B(x) - A(x)
        position.getY() - this.getY()); // B(y) - A(y)
  }

  /**
   * Gives the pythagorean distance from this position to the giver position
   * 
   * @param position the position to compute the distance to
   * @return distance in units between the points
   */
  public double distanceTo(Position position) {
    return (FastMath.hypot(position.getX() - this.getX(), position.getY() - this.getY()));
  }

  /**
   * Checks if this position if outside the boundaries of the map
   * 
   * @return true if it is out of bounds, false otherwise
   */
  public boolean isOutOfBounds() {
    double x = this.getX();
    double y = this.getY();
    if (x < 0 || x > Parameters.MAP_WIDTH || y < 0 || y > Parameters.MAP_HEIGHT)
      return true;
    return false;
  }

  /**
   * Verifies if two positions are approximately equal
   * 
   * @param position to check against
   * @param margin the margin that would be acceptable to call the position approximate
   * @return true if they are approximately equal, false otherwise
   */
  public boolean approximately(Position position, double margin) {
    if (FastMath.abs(this.getX() - position.getX()) > margin)
      return false;

    if (FastMath.abs(this.getY() - position.getY()) > margin)
      return false;

    return true;
  }
}
