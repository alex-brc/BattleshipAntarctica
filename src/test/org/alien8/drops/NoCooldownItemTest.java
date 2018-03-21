package test.org.alien8.drops;

import org.alien8.drops.Effect;
import org.alien8.drops.NoCooldownItem;
import org.alien8.drops.Pickup;
import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;
import org.junit.Test;

public class NoCooldownItemTest {
	@Test
	public void testUse() {
		// Make 2 items
		NoCooldownItem item = new NoCooldownItem();
		// Check the item
		assert(item.getSprite() == Sprite.item_no_cooldown);
		assert(item.getItemType() == Pickup.NO_COOLDOWN);
		// Make a ship
		Ship ship = new Ship(new Position(0,0), 0, 0);
		// Set a ship for the item
		item.setShip(ship);
		// Use it
		item.use();
		// Check for the effect
		assert(ship.underEffect() && ship.getEffectType() == Effect.NO_COOLDOWN);
	}
}
