package test.org.alien8.drops;

import org.alien8.core.Entity;
import org.alien8.core.ServerModelManager;
import org.alien8.drops.Pickup;
import org.alien8.drops.PlaneDropper;
import org.alien8.physics.Position;
import org.junit.Before;
import org.junit.Test;

public class PlaneDropperTest {
	
	PlaneDropper plane;
	
	@Before
	public void setUp() throws Exception {
		ServerModelManager.getInstance().makeMap(1);
		plane = new PlaneDropper();
	}

	@Test
	public void testSetPosition() {
		// Test it works
		plane.setPosition(new Position(0,0));
		assert(plane.getPosition().getX() == 0 &&
				plane.getPosition().getY() == 0);
		// Test to check it doesn't drop packet
		plane.setPosition(new Position(0,0));
		boolean ok = false;
		for(Entity ents : ServerModelManager.getInstance().getEntities())
			if(ents instanceof Pickup)
				ok = true;
		assert(ok == false);
		// Test to check it drops packet
		plane.setPosition(plane.getPacketPosition());
		for(Entity ents : ServerModelManager.getInstance().getEntities())
			if(ents instanceof Pickup)
				ok = true;
		assert(ok == true);
		// Test that a few more times
		for(int i = 0; i < 10; i++)
			plane.setPosition(plane.getPacketPosition());
	}

	@Test
	public void testRender() {
		// Run it
		plane.render();
	}

	@Test
	public void testDealWithOutOfBounds() {
		// Check that it is deleted
		plane.setPosition(new Position(-1,1));
		plane.dealWithOutOfBounds();
		assert(plane.isToBeDeleted());
	}

	@Test
	public void testDealWithInIce() {
		// Nohing happens 
		plane.dealWithInIce(null);
	}

	@Test
	public void testPlaneDropperPositionDouble() {
		plane = new PlaneDropper(new Position(0,0), 0);
	}

}
