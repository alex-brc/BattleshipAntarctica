package org.alien8.drops;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import net.jafama.FastMath;

/**
 * This class represents a Torpedo that flies across the map and damages a Ship that it hits.
 *
 */
public class Torpedo extends Entity {
  private static final long serialVersionUID = -799176400440902424L;

  private long shipSerial;

  /**
   * Constructor.
   * 
   * @param position the Position of this Torpedo in XY coordinates
   * @param shipSerial the serial ID of the Ship that fired this Torpedo
   * @param direction the direction this Torpedo is travelling in
   */
  public Torpedo(Position position, long shipSerial, double direction) {
    super(position, direction, Parameters.TORPEDO_SPEED, 0, Parameters.TORPEDO_LENGTH,
        Parameters.TORPEDO_WIDTH);
    this.shipSerial = shipSerial;
  }

  /**
   * @return the serial ID of the Ship that fired this Torpedo
   */
  public long getSource() {
    return this.shipSerial;
  }

  @Override
  public void render() {
    // TODO give this a sprite
    Sprite currentSprite = Sprite.torpedo.rotateSprite(-(this.getDirection() + FastMath.PI));
    Renderer.getInstance().drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
  }

  @Override
  public void dealWithOutOfBounds() {
    if (this.isOutOfBounds())
      this.delete();
  }

  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    // Should pass through ice
  }
}
