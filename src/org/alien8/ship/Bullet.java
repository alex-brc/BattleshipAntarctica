package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

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

  public void render(Renderer r) {
    r.drawRect((int) position.getX(), (int) position.getY(), 1, 1, 0xffffff, false);
  }

  public double getDamage() {
    return damage;
  }
}
