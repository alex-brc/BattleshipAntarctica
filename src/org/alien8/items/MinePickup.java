package org.alien8.items;

import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;

public class MinePickup extends Pickup {
	private static final long serialVersionUID = 1L;

	public MinePickup(Position position) {
		super(position, new MineItem(), Sprite.bullet, Pickup.MINE_PICKUP); // Add sprite for health item
	}

}
