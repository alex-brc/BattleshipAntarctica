package org.alien8.mapgeneration;

public class MapVector{
	
	protected double xValue;
	protected double yValue;
	
	public MapVector(){
		xValue = 0d;
		yValue = 0d;
	}
	public MapVector(double x, double y){
		xValue = x;
		yValue = y;
	}
	
	public double getX(){
		return xValue;
	}
	
	public double getY(){
		return yValue;
	}
	
	public String toString(){
		return ("(" + Double.toString(xValue) + ", " + Double.toString(yValue) + ")");
	}
}