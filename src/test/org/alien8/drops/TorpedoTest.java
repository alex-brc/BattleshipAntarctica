package test.org.alien8.drops;

import org.alien8.drops.Torpedo;
import org.alien8.physics.Position;
import org.junit.Test;

public class TorpedoTest {
	@Test
	public void test() {
		// Make two torpedos
		Torpedo torpedo = new Torpedo(new Position(-1,-1), 1, 0);
		Torpedo torpedo2 = new Torpedo(new Position(1,1), 1, 0);
		// Check it
		assert(torpedo.getSource() == 1);
		assert(torpedo.getDirection() == 0);
		// Test render
		torpedo.render();
		// Test out of bounds
		torpedo.dealWithOutOfBounds();
		torpedo2.dealWithOutOfBounds();
		// Check their states
		assert(torpedo.isToBeDeleted());
		assert(!torpedo2.isToBeDeleted());
		// Call this empty method
		torpedo.dealWithInIce(null);
	}
}
