package org.alien8.ship;

import java.io.Serializable;
import org.alien8.core.Entity;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public abstract class Bullet extends Entity implements Serializable {

  private static final long serialVersionUID = -4758229490654529751L;
  private double distance;
  private double damage;
  private double travelled;
  private long source;

  public Bullet(Position position, double direction, double distance, double mass, double width,
      double length, double speed, double damage, long source) {
    super(position, direction, speed, mass, length, width);
    this.distance = distance;
    this.damage = damage;
    this.travelled = 0;
    this.source = source;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;
    this.travelled += this.getSpeed();
    // If this distance calculation is too slow,
    // we can change to a limited life span of
    // bullets, after which they are deleted
    if (this.getPosition().isOutOfBounds() || this.travelled > this.distance)
      this.delete();
  }

  public abstract void render(Renderer r);

  public double getDamage() {
    return damage;
  }

  /**
   * This method should delete a bullet if it goes out of the bounds of the map. It isn't too
   * crucial as bullets automatically delete after they have travelled their distance.
   */
  public void dealWithOutOfBounds() {
    if (this.isOutOfBounds()) {
      this.delete();
    }
  }

  public long getSource() {
    return source;
  }

  public double getDistance() {
    return distance;
  }

  public double getTravelled() {
    return travelled;
  }

  public void setTravelled(double travelled) {
    this.travelled = travelled;
  }

  public boolean equals(Bullet b) {
    return this.getSerial() == b.getSerial() && this.getPosition().equals(b.getPosition())
        && this.isToBeDeleted() == b.isToBeDeleted() && this.getMass() == b.getMass()
        && this.getSpeed() == b.getSpeed() && this.getDirection() == b.getDirection()
        && this.getLength() == b.getLength() && this.getWidth() == b.getWidth()
        && this.getDistance() == b.getDistance() && this.getTravelled() == b.getTravelled();
  }

  public String toString() {
    return "Bullet " + this.getSerial() + "," + this.getPosition();
  }

  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    int x = (int) Math.rint(getPosition().getX());
    int y = (int) Math.rint(getPosition().getY());
    try {
      if (iceGrid[x][y] == true) {
        this.delete();
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // This happens if the entity touches the edge of the map
    }
  }
}
