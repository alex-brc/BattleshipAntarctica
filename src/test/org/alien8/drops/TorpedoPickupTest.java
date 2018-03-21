package test.org.alien8.drops;

import org.alien8.drops.TorpedoPickup;
import org.alien8.physics.Position;
import org.junit.Test;

public class TorpedoPickupTest {
	@Test
	public void test() {
		// Make a pickup
		TorpedoPickup pickup = new TorpedoPickup(new Position(0,0));
		// Check it
		assert(pickup.getPosition().getX() == 0 && pickup.getPosition().getY() == 0);
	}
}
