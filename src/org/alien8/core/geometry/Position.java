package org.alien8.core.geometry;
/**
 * Basic position class to pack together the X and Y coordinates for an entity.
 * 
 * @author Alexandru Bracau	
 *
 */
public class Position {
	private double x;
	private double y;
	
	/**
	 * Basic constructor for the Position class. 
	 * 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the X coordinate to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the Y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the Y coordinate to set
	 */
	public void setY(double y) {
		this.y = y;
	}
}
