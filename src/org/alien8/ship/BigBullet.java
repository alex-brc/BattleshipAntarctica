package org.alien8.ship;

import java.io.Serializable;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

public class BigBullet extends Bullet implements Serializable {
  private static final long serialVersionUID = -8445751045321379981L;
  protected Sprite sprite = Sprite.bullet;
  
  public BigBullet(Position position, double direction, double distance, long serial) {
    super(position, direction, distance, Parameters.BIG_BULLET_MASS, Parameters.BIG_BULLET_WIDTH,
        Parameters.BIG_BULLET_LENGTH, Parameters.BIG_BULLET_SPEED, Parameters.BIG_BULLET_DAMAGE, 
        serial);
  }

  @Override
  public void render(Renderer r) {
    r.drawSprite((int) position.getX(), (int) position.getY(), sprite, false);
  }
}