package test.org.alien8.drops;

import org.alien8.drops.SpeedPickup;
import org.alien8.physics.Position;
import org.junit.Test;

public class SpeedPickupTest {
	@Test
	public void test() {
		// Make a pickup
		SpeedPickup pickup = new SpeedPickup(new Position(0,0));
		// Check it
		assert(pickup.getPosition().getX() == 0 && pickup.getPosition().getY() == 0);
	}
}
