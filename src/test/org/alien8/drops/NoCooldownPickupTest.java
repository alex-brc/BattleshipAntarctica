package test.org.alien8.drops;

import org.alien8.drops.NoCooldownPickup;
import org.alien8.physics.Position;
import org.junit.Test;

public class NoCooldownPickupTest {
	@Test
	public void test() {
		// Make a pickup
		NoCooldownPickup pickup = new NoCooldownPickup(new Position(0,0));
		// Check it
		assert(pickup.getPosition().getX() == 0 && pickup.getPosition().getY() == 0);
	}
}
