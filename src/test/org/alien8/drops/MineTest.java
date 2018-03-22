package test.org.alien8.drops;

import org.alien8.drops.Mine;
import org.alien8.physics.Position;
import org.junit.Test;

public class MineTest extends Object {
	@Test
	public void test() {
		// Make a mine
		Mine mine = new Mine(new Position(0,0), 1);
		// Check it
		assert(mine.getSource() == 1);
		// Test render
		mine.render();
		// Call these empty methods
		mine.dealWithInIce(null);
		mine.dealWithOutOfBounds();
	}
}
