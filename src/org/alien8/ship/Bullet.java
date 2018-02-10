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
  
  private Sprite sprite = Sprite.bullet;

  public Bullet(Position position, double direction, double distance, double mass, double width,
      double length, double speed, double damage) {
    super(position, direction, speed, mass, length, width);
    this.distance = distance;
    this.damage = damage;
    this.startingPosition = position;
    this.travelled = 0;
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
    r.drawSprite((int) position.getX(), (int) position.getY(), sprite, false);
  }

  public double getDamage() {
    return damage;
  }
}
