package org.alien8.core;

import java.io.Serializable;

/**
 * Packages all the physical parameters in one neat object.
 */
public class Properties implements Serializable {
  private static final long serialVersionUID = 3959568633929095110L;
  public double mass;
  public double speed;
  public double direction;
  public double length;
  public double width;
  private double health;

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

  public double getHealth() {
    return health;
  }

  public void setLength(double length) {
    this.length = length;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public void setHealth(double health) {
    this.health = health;
  }
}
