package test.org.alien8.drops;

import org.alien8.drops.MinePickup;
import org.alien8.drops.Pickup;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.Test;

public class PickupTest {
	@Test
	public void test() {
		// Test using any pickup that extends it.
		Pickup pickup = new MinePickup(new Position(0,0));
		// Check it
		assert(pickup.getPickupType() == Pickup.MINE);
		// Give it to a ship
		Ship ship = new Ship(new Position(0,0),0,0);
		pickup.onPickup(ship);
		// Test the rendering
		pickup.render();
		// Call these empty methods
		pickup.dealWithInIce(null);
		pickup.dealWithOutOfBounds();
	}
}
