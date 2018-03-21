package org.alien8.mapgeneration;

/**
 * This class represents a vector in x and y, used for map generation.
 *
 */
public class MapVector {
  // Needed a very simple implementation of a vector for the purpose vectors in the Perlin Noise
  // algorithm
  protected double xValue;
  protected double yValue;

  /**
   * Default constructor. Assigns zero values to x and y.
   */
  public MapVector() {
    xValue = 0d;
    yValue = 0d;
  }

  /**
   * Constructor.
   * 
   * @param x the x magnitude of this vector
   * @param y the y magnitude of this vector
   */
  public MapVector(double x, double y) {
    xValue = x;
    yValue = y;
  }

  /**
   * @return the x magnitude of this vector
   */
  public double getX() {
    return xValue;
  }

  /**
   * @return the y magnitude of this vector
   */
  public double getY() {
    return yValue;
  }

  /**
   * @return a String representation of this vector
   */
  public String toString() {
    return ("(" + Double.toString(xValue) + ", " + Double.toString(yValue) + ")");
  }
}
