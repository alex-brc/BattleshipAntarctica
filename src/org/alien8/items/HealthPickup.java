package org.alien8.items;

import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;

public class HealthPickup extends Pickup {
	private static final long serialVersionUID = 1L;

	public HealthPickup(Position position) {
		super(position, new HealthItem(), Sprite.bullet); // Add sprite for health item
	}

}
