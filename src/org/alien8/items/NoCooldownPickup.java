package org.alien8.items;

import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;

public class NoCooldownPickup extends Pickup {
	private static final long serialVersionUID = -4909285725302112826L;

	public NoCooldownPickup(Position position) {
		super(position, new NoCooldownItem(), Sprite.bullet, Pickup.NO_COOLDOWN_PICKUP);
	}

}
