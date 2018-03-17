package org.alien8.items;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;

public class NoCooldownItem extends Item {

	public NoCooldownItem() {
		// Doesn't have a ship at this point
		super(Sprite.item_no_cooldown, Pickup.NO_COOLDOWN_PICKUP);// TODO give it a sprite
	}

  @Override
  public void use() {
    ship.applyEffect(new Effect(System.currentTimeMillis()
        + Parameters.ITEM_NO_COOLDOWN_ITEM_DURATION * Parameters.M_SECOND, Effect.NO_COOLDOWN));
  }

}