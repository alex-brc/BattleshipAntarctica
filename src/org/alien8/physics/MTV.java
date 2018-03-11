package org.alien8.physics;

public class MTV {
  private double distance;
  private AxisVector axis;

  public MTV(double distance, AxisVector axis) {
    this.distance = distance;
    this.setAxis(axis);
  }

  public double getDistance() {
    return distance;
  }

  public AxisVector getAxis() {
    return axis;
  }

  public void setAxis(AxisVector axis) {
    this.axis = axis;
  }
}
