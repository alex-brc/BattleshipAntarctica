package org.alien8.items;

import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;

public class InvulnerablePickup extends Pickup {
	private static final long serialVersionUID = -7611529310499072537L;

	public InvulnerablePickup(Position position) {
		super(position, new InvulnerableItem(), Sprite.bullet, Pickup.INVULNERABLE_PICKUP);
	}

}
