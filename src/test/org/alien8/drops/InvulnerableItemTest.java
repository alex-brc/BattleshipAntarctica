package test.org.alien8.drops;

import org.alien8.drops.Effect;
import org.alien8.drops.InvulnerableItem;
import org.alien8.drops.Pickup;
import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;
import org.junit.Test;

public class InvulnerableItemTest {
	@Test
	public void testUse() {
		// Make 2 items
		InvulnerableItem item = new InvulnerableItem();
		// Check the item
		assert(item.getSprite() == Sprite.item_invulnerable);
		assert(item.getItemType() == Pickup.INVULNERABLE);
		// Make a ship
		Ship ship = new Ship(new Position(0,0), 0, 0);
		// Set a ship for the item
		item.setShip(ship);
		// Use it
		item.use();
		// Try to damage it
		double shipHealth = ship.getHealth();
		ship.damage(50);
		// Check if damaged
		assert(shipHealth == ship.getHealth());
		// Check ship's effect
		assert(ship.underEffect() && ship.getEffectType() == Effect.INVULNERABLE);
	}
}
