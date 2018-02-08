package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public class Bullet extends Entity {
  private Position startingPosition;
  private double distance;
  private double damage;

  public Bullet(Position position, double direction, int bulletType, double distance) {
    // THIS NEEDS SORTING OUT AS THE ENTITY CONSTRUCTORS NEED DECIDING
    super(position, direction, Parameters.SMALL_BULLET_SPEED, Parameters.SMALL_BULLET_MASS,
        Parameters.SMALL_BULLET_LENGTH, Parameters.SMALL_BULLET_WIDTH);
    // this.position = position;
    // this.setDirection(direction);
    this.startingPosition = position;
    this.distance = distance;
    // AGAIN, SORT THIS OUT FOR BIG BULLETS
    this.damage = Parameters.SMALL_BULLET_DAMAGE;

    if (bulletType == Turret.SMALL) {
      super.setSpeed(Parameters.SMALL_BULLET_SPEED);
      super.setMass(Parameters.SMALL_BULLET_MASS);
      // super.setLength(Parameters.SMALL_BULLET_LENGTH);
      // super.setWidth(Parameters.SMALL_BULLET_WIDTH);
    } else if (bulletType == Turret.BIG) {
      super.setSpeed(Parameters.BIG_BULLET_SPEED);
      super.setMass(Parameters.BIG_BULLET_MASS);
      // super.setLength(Parameters.BIG_BULLET_LENGTH);
      // super.setWidth(Parameters.BIG_BULLET_WIDTH);
    }
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
