package test.org.alien8.mapgeneration;

import org.alien8.mapgeneration.MapVector;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapVectorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testGetX() {
		MapVector v = new MapVector(1.2d, 4.3d);
		assert (v.getX() == 1.2);
	}
	
	@Test
	public void testGetY() {
		MapVector v = new MapVector(1.2d, 4.3d);
		assert (v.getY() == 4.3);
	}
	
	@Test
	public void testToString() {
		MapVector v = new MapVector(1.2d, 4.3d);
		assert (v.toString().equals("(1.2, 4.3)"));
		
	}

}
