package test.org.alien8.core;

import org.alien8.core.Entity;
import org.alien8.core.Type;
import org.alien8.core.geometry.Position;
import org.junit.jupiter.api.Test;

class EntityTest {

	@Test
	void test() {
		// Extend the Entity class
		class TestClass extends Entity{

			public TestClass(double x, double y, Type id) {
				super(x, y, id);
			}
			
			public TestClass(Position position, Type id) {
				super(position, id);
			}
			
			public void update() {
				this.setPosition(new Position(-1d,-1d));
			}
			
			public void render() {
				
			}
		}
		
		TestClass testerA = new TestClass(0d,1d,Type.AI);
		TestClass testerB = new TestClass(0d,1d,Type.PLAYER);
		TestClass testerC = new TestClass(0d,1d,Type.TERRAIN);
		TestClass testerD = new TestClass(new Position(0d,1d),Type.PROJECTILE);
		
		testerB.setPosition(new Position(2d,3d));
		
		assert(testerA.getPosition().getX() == 0d);
		assert(testerA.getPosition().getY() == 1d);
		assert(testerB.getPosition().getX() == 2d);
		assert(testerB.getPosition().getY() == 3d);
		assert(testerD.getPosition().getX() == 0d);
		assert(testerD.getPosition().getY() == 1d);
		
		assert(testerA.getType() == Type.AI);
		assert(testerB.getType() == Type.PLAYER);
		assert(testerC.getType() == Type.TERRAIN);
		assert(testerD.getType() == Type.PROJECTILE);
		
		testerA.update();
		assert(testerA.getPosition().getX() == -1d);
		assert(testerA.getPosition().getY() == -1d);
		
	}

}
