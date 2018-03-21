package test.org.alien8.mapgeneration;


import java.util.Random;

import org.alien8.mapgeneration.MapVector;
import org.alien8.mapgeneration.PerlinNoise;
import org.junit.BeforeClass;
import org.junit.Test;

public class PerlinNoiseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testDistance() {
		MapVector v = PerlinNoise.distance(4.2d, 1.2d, 3.2d, 2.4d);
		assert (v.getX() == 1.0);
		assert (v.getY() == -1.2);
	}
	
	@Test
	public void testDotProduct() {
		MapVector v = new MapVector(4.5d, 1.2d);
		MapVector u = new MapVector(3.2d, 2.4d);
		double dp = PerlinNoise.dotProduct(v, u);
		assert (dp == 17.28);
	}
	
	@Test
	public void testFade() {
		double f = PerlinNoise.fade(3.0d);
		assert (f == 513);
	}
	
	@Test
	public void testLinInterpolate() {
		double l = PerlinNoise.linInterpolate(1.2d, 3.4d, 5.0d);
		assert (l == 12.2);
	}
	
	@Test
	public void testPerlin() {
		double x = 0.7;
		double y = 0.4;
		MapVector[][] g = {{new MapVector(1,-1), new MapVector(1,1)},
						   {new MapVector(-1,-1), new MapVector(-1,1)}
						  };
		double p = PerlinNoise.perlin(x, y, g);
		assert (p == 0.450872);
	}
	
	@Test
	public void testGenerateNoiseGrid() {
		double[][] ng = PerlinNoise.generateNoiseGrid(2048,2048,8,8,(new Random()).nextLong());
		/*To test that the noise is not simply random and transitions smoothly we'll check that
		 * adjacent values don't differ by more than 0.1. We also want to make sure that all the
		 * values are between 0 and 1.
		 */
		for (int i = 0; i < 2048; i++) {
			for (int j = 0; j < 2048; j++) {
				if (i != 2047) {
					assert (Math.abs(ng[i+1][j] - ng[i][j]) < 0.1);
				}
				if (j != 2047) {
					assert (Math.abs(ng[i][j+1] - ng[i][j]) < 0.1);
				}
				assert ((ng[i][j] >= 0) && (ng[i][j] <= 1));
			}
		}
	}

}
