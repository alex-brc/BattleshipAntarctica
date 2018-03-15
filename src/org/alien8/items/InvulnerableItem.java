package org.alien8.items;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;

public class InvulnerableItem extends Item {

	public InvulnerableItem() {
		// Doesn't have a ship at this point
		super(Sprite.item_invulnerable, Pickup.INVULNERABLE_PICKUP);// TODO give it a sprite
	}

	@Override
	public void use() {
		ship.applyEffect(new Effect(
				System.currentTimeMillis()
				+ Parameters.ITEM_INVULNERABLE_ITEM_DURATION * Parameters.M_SECOND,
				Effect.INVULNERABLE));
	}

}
