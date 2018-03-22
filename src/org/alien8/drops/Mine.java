package org.alien8.drops;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

/**
 * This class represents a mine which detonates if a Ship crashes into it, dealing damage.
 *
 */
public class Mine extends Entity {
  private static final long serialVersionUID = -799176400440902424L;
  protected Sprite sprite;
  private long shipSerial;

  /**
   * Constructor.
   * 
   * @param position the Position of the Mine in XY coordinates
   * @param shipSerial the serial ID of the Ship that dropped the Mine
   */
  public Mine(Position position, long shipSerial) {
    super(position, 0, 0, 0, Parameters.MINE_LENGTH, Parameters.MINE_WIDTH);
    this.sprite = Sprite.mine;
    this.shipSerial = shipSerial;
  }

  /**
   * @return the serial ID of the Ship that dropped this Mine
   */
  public long getSource() {
    return this.shipSerial;
  }

  @Override
  public void render() {
    // TODO give this a sprite
    Renderer.getInstance().drawSprite((int) position.getX() - sprite.getWidth() / 2,
        (int) position.getY() - sprite.getHeight() / 2, sprite, false);
  }

  /**
   * This object would never be out of bounds, as it is
   * created at some player's position
   */
  @Override
  public void dealWithOutOfBounds() {
    // Will never be
  }
  /**
   * This object would never be in ice, as it is
   * created at some player's position
   */
  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    // Will never be
  }

}
