package org.alien8.ship;

import org.alien8.core.Parameters;
import org.alien8.physics.Position;

public class BigBullet extends Bullet {

  public BigBullet(Position position, double direction, double distance, long serial) {
    super(position, direction, distance, Parameters.BIG_BULLET_MASS, Parameters.BIG_BULLET_WIDTH,
        Parameters.BIG_BULLET_LENGTH, Parameters.BIG_BULLET_SPEED, Parameters.BIG_BULLET_DAMAGE, 
        serial);
  }
}
