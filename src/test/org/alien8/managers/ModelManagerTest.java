/**
 * 
 */
package test.org.alien8.managers;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.TimeUnit;

import org.alien8.core.Entity;
import org.alien8.core.Type;
import org.alien8.core.geometry.Position;
import org.alien8.managers.ModelManager;
import org.junit.jupiter.api.Test;

/**
 *	Tests the model of the game.
 */
class ModelManagerTest {
	
	ModelManager mm = ModelManager.getInstance();
	
	class TestObject extends Entity {
		public TestObject(Position position, Type id) {
			super(position, id);
		}
		
		public TestObject(double x, double y, Type id) {
			super(x,y,id);
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
	 * Test method for {@link org.alien8.managers.ModelManager#pause()}. 
	 * Checks if the game loop breaks out if this is called.
	 */
	@Test
	void testPause() {
		// Should be alive
		mm.start();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(mm.getThread().isAlive());
		
		// Should die here
		mm.pause();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(!mm.getThread().isAlive());
		
		try {
			mm.getThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.alien8.managers.ModelManager#addEntity(org.alien8.core.Entity)}.
	 */
	@Test
	void testAddEntity() {
		mm.addEntity(new TestObject(0d,0d,Type.PLAYER));
		mm.addEntity(new TestObject(1d,1d,Type.TERRAIN));
		mm.addEntity(new TestObject(2d,2d,Type.AI));
		mm.addEntity(new TestObject(3d,3d,Type.PROJECTILE));
		
		// Check their serials
		assert(mm.getEntity(1).getPosition().getX() == 0);
		assert(mm.getEntity(2).getPosition().getX() == 1);
		assert(mm.getEntity(3).getPosition().getX() == 2);
		assert(mm.getEntity(4).getPosition().getX() == 3);
	}

	/**
	 * Test method for {@link org.alien8.managers.ModelManager#getFPS()}.
	 */
	@Test
	void testGetFPS() {
		mm.start();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// If the fps is > 0 it's okay.
		int fpsInfo = mm.getFPS();
		assert(fpsInfo > 0);
	}

	/**
	 * Test method for {@link org.alien8.managers.ModelManager#getEntities()}.
	 */
	@Test
	void testGetEntities() {
		mm.addEntity(new TestObject(0d,0d,Type.PLAYER));
		mm.addEntity(new TestObject(1d,1d,Type.TERRAIN));
		mm.addEntity(new TestObject(2d,2d,Type.AI));
		mm.addEntity(new TestObject(3d,3d,Type.PROJECTILE));
		
		assert(mm.getEntities().size() == 4);
	}

}
