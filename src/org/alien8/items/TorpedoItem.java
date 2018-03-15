package org.alien8.items;

import org.alien8.core.ModelManager;
import org.alien8.rendering.Sprite;

public class TorpedoItem extends Item {
	public TorpedoItem() {
		// Doesn't have a ship at this point
		super(Sprite.item_torpedo, Pickup.SPEED_PICKUP); //TODO give it Sprite.health_item
	}
	
	/**
	 * This item drops a mine that blows up when another ship hits it
	 */
	@Override
	public void use() {
		ModelManager.getInstance().addEntity(new Torpedo(ship.getPosition(), ship.getSerial(), ship.getDirection()));
	}
}
