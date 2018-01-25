package test.org.alien8.core;

import org.alien8.core.Entity;
import org.alien8.core.ID;
import org.alien8.core.geometry.Position;
import org.junit.jupiter.api.Test;

class EntityTest {

	@Test
	void test() {
		// Extend the Entity class
		class TestClass extends Entity{

			public TestClass(double x, double y, ID id) {
				super(x, y, id);
			}
			
			public TestClass(Position position, ID id) {
				super(position, id);
			}
		}
		
		TestClass testerA = new TestClass(0d,1d,ID.AI);
		TestClass testerB = new TestClass(0d,1d,ID.PLAYER);
		TestClass testerC = new TestClass(0d,1d,ID.TERRAIN);
		TestClass testerD = new TestClass(new Position(0d,1d),ID.PROJECTILE);
		
		testerB.setPosition(new Position(2d,3d));
		
		assert(testerA.getPosition().getX() == 0d);
		assert(testerA.getPosition().getY() == 1d);
		assert(testerB.getPosition().getX() == 2d);
		assert(testerB.getPosition().getY() == 3d);
		assert(testerD.getPosition().getX() == 0d);
		assert(testerD.getPosition().getY() == 1d);
		
		assert(testerA.getId() == ID.AI);
		assert(testerB.getId() == ID.PLAYER);
		assert(testerC.getId() == ID.TERRAIN);
		assert(testerD.getId() == ID.PROJECTILE);	
	}

}
