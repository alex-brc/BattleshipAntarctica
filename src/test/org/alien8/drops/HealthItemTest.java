package test.org.alien8.drops;

import org.alien8.drops.HealthItem;
import org.alien8.drops.Pickup;
import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;
import org.junit.Test;

public class HealthItemTest {
	@Test
	public void testUse() {
		// Make 2 items
		HealthItem healthItem1 = new HealthItem();
		HealthItem healthItem2 = new HealthItem();
		
		// Check the healthItem
		assert(healthItem1.getSprite() == Sprite.item_health);
		assert(healthItem1.getItemType() == Pickup.HEALTH);
		// Make a ship
		Ship ship = new Ship(new Position(0,0), 0, 0);
		// Damage the ship
		ship.setHealth(60);
		// Set a ship for item 1
		healthItem1.setShip(ship);
		// Use it
		healthItem1.use();
		// Assert ship has healed 25 points
		assert(ship.getHealth() == 85);
		// Set a ship for item 2
		healthItem2.setShip(ship);
		// Use it
		healthItem2.use();
		// Assert ship has healed less than 25 points. 
		assert(ship.getHealth() == 100);
	}
}
