package test.org.alien8.mapgeneration;


import java.util.Random;
import org.alien8.core.Parameters;
import org.alien8.mapgeneration.Map;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testMakeMap() {
		Map m = new Map(2048,2048,8,8,(new Random()).nextLong());
		boolean[][] ig = m.getIceGrid();
		double[][] ng = m.getNoiseGrid();
		for (int i = 0; i < 2048; i++) {
			for (int j = 0; j < 2048; j++) {
				assert (ig[i][j] == (ng[i][j] >= Parameters.THIN_ICE_LEVEL));
			}
		}
	} 

}
