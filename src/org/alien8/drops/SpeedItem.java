package org.alien8.drops;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;

/**
 * This class represents an Item that can give a Ship a speed boost when used.
 */
public class SpeedItem extends Item {
  /**
   * Constructor.
   */
  public SpeedItem() {
    // Doesn't have a ship at this point
    super(Sprite.item_speed, Pickup.SPEED);
  }

  /**
   * Gives the using Ship a speed boost.
   */
  @Override
  public void use() {
    ship.applyEffect(new Effect(
        System.currentTimeMillis() + Parameters.ITEM_SPEED_ITEM_DURATION * Parameters.M_SECOND,
        Effect.SPEED));
  }
}
