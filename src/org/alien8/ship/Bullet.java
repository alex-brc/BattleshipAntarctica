package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

public abstract class Bullet extends Entity {

  private Position startingPosition;
  private double distance;
  private double damage;
  private double travelled;
  private long source;

  public Bullet(Position position, double direction, double distance, double mass, double width,
      double length, double speed, double damage, long source) {
    super(position, direction, speed, mass, length, width);
    this.distance = distance;
    this.damage = damage;
    this.startingPosition = position;
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
}
