package org.alien8.physics;

/**
 * This class represents the Minimum Translation Vector (MTV) between two objects. If they overlap,
 * it can be used to identify the minimum distance the object's would have to be moved so that they
 * were no longer overlapping. This idea is useful in collision detection.
 *
 */
public class MTV {
  private double distance;
  private AxisVector axis;

  /**
   * Constructor.
   * 
   * @param distance the length of the vector
   * @param axis the direction in which the vector occurs
   */
  public MTV(double distance, AxisVector axis) {
    this.distance = distance;
    this.setAxis(axis);
  }

  /**
   * Returns the length of the vector.
   * 
   * @return the length of the vector
   */
  public double getDistance() {
    return distance;
  }

  /**
   * Returns the axis the vector occurs in.
   * 
   * @return the axis the vector occurs in
   */
  public AxisVector getAxis() {
    return axis;
  }

  /**
   * Sets the axis to the specified AxisVector.
   * 
   * @param axis the AxisVector to set
   */
  public void setAxis(AxisVector axis) {
    this.axis = axis;
  }
}
