package test.org.alien8.drops;

import org.alien8.drops.HealthPickup;
import org.alien8.physics.Position;
import org.junit.Test;

public class HealthPickupTest {
	@Test
	public void test() {
		// Make a pickup
		HealthPickup pickup = new HealthPickup(new Position(0,0));
		// Check it
		assert(pickup.getPosition().getX() == 0 && pickup.getPosition().getY() == 0);
	}
}
