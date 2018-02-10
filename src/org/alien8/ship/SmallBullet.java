package org.alien8.ship;

import org.alien8.core.Parameters;
import org.alien8.physics.Position;

public class SmallBullet extends Bullet {

  public SmallBullet(Position position, double direction, double distance, long serial) {
    super(position, direction, distance, Parameters.SMALL_BULLET_MASS,
        Parameters.SMALL_BULLET_WIDTH, Parameters.SMALL_BULLET_LENGTH,
        Parameters.SMALL_BULLET_SPEED, Parameters.SMALL_BULLET_DAMAGE, 
        serial);
  }
}
