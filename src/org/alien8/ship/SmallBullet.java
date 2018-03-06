package org.alien8.ship;

import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

import net.jafama.FastMath;

public class SmallBullet extends Bullet {
  protected Sprite sprite = Sprite.bullet;

  public SmallBullet(Position position, double direction, double distance, long sourceSerial) {
    super(position, direction, distance, Parameters.SMALL_BULLET_MASS,
        Parameters.SMALL_BULLET_WIDTH, Parameters.SMALL_BULLET_LENGTH,
        Parameters.SMALL_BULLET_SPEED, Parameters.SMALL_BULLET_DAMAGE, sourceSerial);
  }

  @Override
  public void render() {
    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
    Renderer.getInstance().drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
  }
}
