package test.org.alien8.drops;

import org.alien8.drops.Effect;
import org.alien8.drops.Pickup;
import org.alien8.drops.SpeedItem;
import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;
import org.junit.Test;

public class SpeedItemTest {
	@Test
	public void testUse() {
		// Make 2 items
		SpeedItem item = new SpeedItem();
		// Check the item
		assert(item.getSprite() == Sprite.item_speed);
		assert(item.getItemType() == Pickup.SPEED);
		// Make a ship
		Ship ship = new Ship(new Position(0,0), 0, 0);
		// Set a ship for the item
		item.setShip(ship);
		// Use it
		item.use();
		// Check ship's effect
		assert(ship.underEffect() && ship.getEffectType() == Effect.SPEED);
	}

}
