package org.alien8.items;

import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;

public class TorpedoPickup extends Pickup {
	private static final long serialVersionUID = 1L;

	public TorpedoPickup(Position position) {
		super(position, new TorpedoItem(), Sprite.bullet, Pickup.TORPEDO_PICKUP); // Add sprite for torpedo item
	}

}
