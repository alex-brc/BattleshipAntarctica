package org.alien8.mapgeneration;

import java.util.Random;
import net.jafama.FastMath;

/**
 * This class implements the Perlin noise algorithm, allowing a Map to be procedurally generated.
 *
 */
public class PerlinNoise {

  /**
   * Array of the 4 unit vectors for gradients.
   */
  static MapVector[] gradientVectors = {new MapVector(1d, 1d), new MapVector(1d, -1d),
      new MapVector(-1d, 1d), new MapVector(-1d, -1d)};

  /**
   * Calculates the vector distance between two given points in x and y.
   * 
   * @param px the x coordinate of the destination point
   * @param py the y coordinate of the destination point
   * @param gx the x coordinate of the source point
   * @param gy the y coordinate of the source point
   * @return a MapVector giving the distance between the two points given
   */
  public static MapVector distance(double px, double py, double gx, double gy) {
    /*
     * Calculates the x distance and y distance given to vectors gx and gy refer to the coordinates
     * of the grid point, px and py refer to the point's coordinates we are calculating the distance
     * to
     */
    double outx = px - gx;
    double outy = py - gy;

    return new MapVector(outx, outy);
  }

  /**
   * Calculates the dot product of two vectors.
   * 
   * @param u the first vector
   * @param v the second vector
   * @return the dot product between the vectors
   */
  public static double dotProduct(MapVector u, MapVector v) {
    double out = (u.getX() * v.getX() + u.getY() * v.getY());
    return out;
  }

  /**
   * Implements the standard Perlin fade function to give smoother noise: 6n^5 - 15n^4 + 10n^3.
   * 
   * @param n n parameter
   * @return the faded value
   */
  public static double fade(double n) {
    return n * n * n * (n * (n * 6 - 15) + 10);
  }

  /**
   * Implements the linear interpolation function: a + w * (b - a).
   * 
   * @param a a parameter
   * @param b b parameter
   * @param w w parameter
   * @return the interpolated value
   */
  public static double linInterpolate(double a, double b, double w) {
    return a + w * (b - a);
  }

  /**
   * Calculates the Perlin value of a given XY coordinate.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @param gradients the four unit vectors
   * @return the Perlin value of the XY coordinate
   */
  public static double perlin(double x, double y, MapVector[][] gradients) {
    /*
     * Notation - The numbers after variables generally refer to the corners on the unit square:
     * E.g. grad00 refers to the gradient vector on the bottom left corner of the square
     */
    double x0 = FastMath.floor(x);
    double y0 = FastMath.floor(y);
    double x1 = FastMath.ceil(x);
    double y1 = FastMath.ceil(y);
    // These are the points coordinates as if it were in the unit square (important for calculation of
    // the distance vector)
    double unitX = x - x0;
    double unitY = y - y0;
    MapVector grad00 = gradients[(int) x0][(int) y0];
    MapVector grad10 = gradients[(int) x1][(int) y0];
    MapVector grad01 = gradients[(int) x0][(int) y1];
    MapVector grad11 = gradients[(int) x1][(int) y1];

    MapVector dist00 = distance(unitX, unitY, 0d, 0d);
    MapVector dist10 = distance(unitX, unitY, 1d, 0d);
    MapVector dist01 = distance(unitX, unitY, 0d, 1d);
    MapVector dist11 = distance(unitX, unitY, 1d, 1d);

    double dot00 = dotProduct(grad00, dist00);
    double dot10 = dotProduct(grad10, dist10);
    double dot01 = dotProduct(grad01, dist01);
    double dot11 = dotProduct(grad11, dist11);

    double fadeX = fade(dist00.getX());
    double fadeY = fade(dist00.getY());

    double linInt0 = linInterpolate(dot00, dot10, fadeX);
    double linInt1 = linInterpolate(dot01, dot11, fadeX);

    double perlinValue = linInterpolate(linInt0, linInt1, fadeY);

    // Want values in the range 0 to 1 instead of -1 to 1 as they are easier to process
    double outValue = (perlinValue + 1) / 2;

    return outValue;
  }

  /**
   * Generates a 2D array of Perlin noise.
   * 
   * @param xPxlSize the x dimension of the pixel size
   * @param yPxlSize the y dimension of the pixel size
   * @param xGridSize the x dimension of the grid size
   * @param yGridSize the y dimension of the grid size/
   * @param seed the random seed used to generate the grid
   * @return the 2D Perlin grid
   */
  public static double[][] generateNoiseGrid(int xPxlSize, int yPxlSize, int xGridSize,
      int yGridSize, long seed) {
    /*
     * PxlSize dimensions are defining the whole picture of noise (as a grid of pixels) GridSize
     * dimensions are defining the grid on top of the noise that has a gradient vector at each point
     * (Changing the grid size will change the density of noise)
     */
    double[][] noiseGrid = new double[xPxlSize][yPxlSize];
    MapVector[][] gradientGrid = new MapVector[xGridSize + 1][yGridSize + 1];
    Random rand = new Random(seed);

    // Defining the grid of gradient vectors (only contains the unit vectors defined at top of the
    // class)
    for (int y = 0; y < yGridSize + 1; y++) {
      for (int x = 0; x < xGridSize + 1; x++) {
        int currentGVector = rand.nextInt(3);
        gradientGrid[x][y] = gradientVectors[currentGVector];
      }
    }
    double xScale = xPxlSize / xGridSize;
    double yScale = yPxlSize / yGridSize;

    for (int y = 0; y < yPxlSize; y++) {
      for (int x = 0; x < xPxlSize; x++) {
        double xCoord = x / (xScale);
        double yCoord = y / (yScale);
        noiseGrid[x][y] = perlin(xCoord, yCoord, gradientGrid);
      }
    }
    return noiseGrid;
  }
}
