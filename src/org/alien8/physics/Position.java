package org.alien8.physics;

import java.io.Serializable;

import org.alien8.core.Parameters;

/**
 * Basic position class to pack together the X and Y coordinates for an entity.
 *
 */
public class Position implements Serializable{
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
	/**
	 * Verifies if two positions in the XY plane are equal.
	 * @param position the position to compare this position to
	 * @return true if the two positions are equal, false otherwise
	 */
	public boolean equals(Position position) {
		if(this.getX() == position.getX() && this.getY() == position.getY())
			return true;
		return false;
	}
	/**
	 * Computes the angle between the X axis and the line formed by
	 * the point at this position and the point at the given position
	 * @param position the second point that determines the line
	 * @return the required angle in radians, anti-clockwise from the positive X axis, is in [0,2pi) 
	 */
	public double getAngleTo(Position position) {
		return Math.atan2(						// Intentionally left out intermediary variables for speed
				position.getX() - this.getX(),  // B(x) - A(x)  
				position.getY() - this.getY()); // B(y) - A(y) 
	}
	
	/**
	 * Simply adds the X's and Y's of the two position to 
	 * @param position the second point to add to this one
	 * @return a new position resulting from the two
	 */
	public Position addPosition(Position position) {
		return new Position(this.getX() + position.getX(), this.getY() + position.getY());
	}
	
	public double distanceTo(Position position) {
		return (Math.sqrt(
					Math.pow(
						(position.getX()-this.getX()), 
						2) + 
					Math.pow(
						(position.getY()-this.getY()), 
						2)
					)
				);
	}

	public boolean isOutOfBounds() {
		double x = this.getX();
		double y = this.getY();
		if(x < 0 || x > Parameters.MAP_WIDTH || y < 0 || y > Parameters.MAP_HEIGHT)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "X: " + this.getX() + " Y: " + this.getY();
	}
}
