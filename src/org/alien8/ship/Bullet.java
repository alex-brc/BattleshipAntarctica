package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public abstract class Bullet extends Entity {

  private Position startingPosition;
  private double distance;
  private double damage;

  public Bullet(Position position, double direction, double distance, double mass, double width,
      double length, double speed, double damage) {
    super(position, direction, speed, mass, length, width);
    this.distance = distance;
    this.damage = damage;
    this.startingPosition = position;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;

    // If this distance calculation is too slow,
    // we can change to a limited life span of
    // bullets, after which they are deleted
    if (this.isOutOfBounds() || startingPosition.distanceTo(position) > distance)
      this.delete();
  }

  public void render(Renderer r) {
    r.drawRect((int) position.getX(), (int) position.getY(), 1, 1, 0xffffff, false);
  }

  public double getDamage() {
    return damage;
  }
}
