package org.alien8.drops;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;

/**
 * This class represents an Item that can make a Ship invulnerable when used.
 *
 */
public class InvulnerableItem extends Item {

  /**
   * Constructor.
   */
  public InvulnerableItem() {
    // Doesn't have a ship at this point
    super(Sprite.item_invulnerable, Pickup.INVULNERABLE);
  }

  /**
   * Makes the Ship invulnerable.
   */
  @Override
  public void use() {
    ship.applyEffect(new Effect(
        System.currentTimeMillis()
            + Parameters.ITEM_INVULNERABLE_ITEM_DURATION * Parameters.M_SECOND,
        Effect.INVULNERABLE));
  }
}
