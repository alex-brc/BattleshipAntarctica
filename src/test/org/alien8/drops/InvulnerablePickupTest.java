package test.org.alien8.drops;

import org.alien8.drops.InvulnerablePickup;
import org.alien8.physics.Position;
import org.junit.Test;

public class InvulnerablePickupTest {
	@Test
	public void test() {
		// Make a pickup
		InvulnerablePickup pickup = new InvulnerablePickup(new Position(0,0));
		// Check it
		assert(pickup.getPosition().getX() == 0 && pickup.getPosition().getY() == 0);
	}

}
