/**
 * 
 */
package test.org.alien8.managers;

import org.alien8.core.Entity;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;
import org.junit.jupiter.api.Test;

/**
 *	Tests the model of the game.
 */
class ModelManagerTest {
	
	ModelManager mm = ModelManager.getInstance();
	
	class TestObject extends Entity {
		public TestObject(Position position) {
			super(position);
		}
		
		public TestObject(double x, double y) {
			super(x,y);
		}

		@Override
		public void update() {
			// do nothing
			
		}

		@Override
		public void render() {
			// do nothing
		}
	}
	
	/**
	 * Test method for {@link org.alien8.managers.ModelManager#getInstance()}.
	 */
	@Test
	void testGetInstance() {
		ModelManager mm2 = ModelManager.getInstance();
		
		// Should return the same instance
		assert(mm.equals(mm2));
	}

	/**
	 * Test method for {@link org.alien8.managers.ModelManager#addEntity(org.alien8.core.Entity)}.
	 */
	@Test
	void testAddEntity() {
		mm.addEntity(new TestObject(0d,0d));
		mm.addEntity(new TestObject(1d,1d));
		mm.addEntity(new TestObject(2d,2d));
		mm.addEntity(new TestObject(3d,3d));
		
		// Check their serials
		assert(mm.getEntity(1).getPosition().getX() == 0);
		assert(mm.getEntity(2).getPosition().getX() == 1);
		assert(mm.getEntity(3).getPosition().getX() == 2);
		assert(mm.getEntity(4).getPosition().getX() == 3);
	}

	/**
	 * Test method for {@link org.alien8.managers.ModelManager#getEntities()}.
	 */
	@Test
	void testGetEntities() {
		mm.addEntity(new TestObject(0d,0d));
		mm.addEntity(new TestObject(1d,1d));
		mm.addEntity(new TestObject(2d,2d));
		mm.addEntity(new TestObject(3d,3d));
		
		assert(mm.getEntities().size() == 4);
	}

}
