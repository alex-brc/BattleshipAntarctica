package org.alien8.physics;

/**
 * This class represents an axis, stored as a vector in x and y.
 *
 */
public class AxisVector {
  private double x;
  private double y;

  /**
   * Constructor.
   * 
   * @param x the x magnitude of the axis
   * @param y the y magnitude of the axis
   */
  public AxisVector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the x magnitude of the axis.
   * 
   * @return the x magnitude of the axis
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y magnitude of the axis.
   * 
   * @return the y magnitude of the axis
   */
  public double getY() {
    return y;
  }
}
