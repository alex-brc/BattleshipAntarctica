package org.alien8.drops;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;

/**
 * This class represents an Item that can temporarily eliminate a Ship's cooldown between firing
 * shots.
 */
public class NoCooldownItem extends Item {

  /**
   * Constructor.
   */
  public NoCooldownItem() {
    // Doesn't have a ship at this point
    super(Sprite.item_no_cooldown, Pickup.NO_COOLDOWN);
  }

  /**
   * Eliminates the cooldown between firing shots.
   */
  @Override
  public void use() {
    ship.applyEffect(new Effect(System.currentTimeMillis()
        + Parameters.ITEM_NO_COOLDOWN_ITEM_DURATION * Parameters.M_SECOND, Effect.NO_COOLDOWN));
  }

}
