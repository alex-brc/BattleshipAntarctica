package test.org.alien8.core.geometry;

import org.alien8.core.geometry.Position;
import org.junit.jupiter.api.Test;

class PositionTest {

	@Test
	void test() {
		Position p = new Position(0d,1d);
		assert(p.getX() == 0);
		assert(p.getY() == 1);
		
		p.setX(2d);
		p.setY(3d);
		assert(p.getX() == 2);
		assert(p.getY() == 3);
	}

}
