package test.org.alien8.drops;

import org.alien8.drops.MinePickup;
import org.alien8.physics.Position;
import org.junit.Test;

public class MinePickupTest {
	@Test
	public void test() {
		// Make a pickup
		MinePickup pickup = new MinePickup(new Position(0,0));
		// Check it
		assert(pickup.getPosition().getX() == 0 && pickup.getPosition().getY() == 0);
	}
}
