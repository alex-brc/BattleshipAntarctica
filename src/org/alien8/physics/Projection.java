package org.alien8.physics;

/**
 * This class represents a projection of a bounding box onto an axis. It is defined by a maximum
 * value and a minimum value which represent the farthest possible points of the projection along
 * the axis.
 *
 */
public class Projection {
  private double min;
  private double max;

  /**
   * Constructor.
   * 
   * @param min the minimum value
   * @param max the maximum value
   */
  public Projection(double min, double max) {
    this.min = min;
    this.max = max;
  }

  /**
   * Returns the minimum value of the Projection.
   * 
   * @return the minimum value of the Projection
   */
  public double getMin() {
    return min;
  }

  /**
   * Returns the maximum value of the Projection.
   * 
   * @return the maximum value of the Projection
   */
  public double getMax() {
    return max;
  }
}
