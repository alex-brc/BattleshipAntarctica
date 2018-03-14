package org.alien8.items;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;

public class SpeedItem extends Item {
	public SpeedItem() {
		// Doesn't have a ship at this point
	    super(Sprite.item_speed, Pickup.SPEED_PICKUP); //TODO give it Sprite.health_item
	}
	
	@Override
	public void use() {
		ship.applyEffect(new Effect(System.currentTimeMillis() + Parameters.ITEM_SPEED_ITEM_DURATION * Parameters.M_SECOND, Effect.SPEED));
	}
}
