package test.org.alien8.drops;

import org.alien8.drops.MinePickup;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.Test;

public class MinePickupTest {

	@Test
	public void test() {
		// Make a pickup
		MinePickup pickup = new MinePickup(new Position(0,0));
		// Make a ship
		Ship ship = new Ship(new Position(0,0),0,0);
		// Check it
		assert()
		// Make it pick it up
		pickup.onPickup(ship);
		
	}

}
