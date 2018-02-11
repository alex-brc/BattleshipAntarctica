package org.alien8.core;

public class Attributes {
	public String name;
	public double health;
	public int colour;
	// Others in the future
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getHealth() {
		return health;
	}
	public void setHealth(double health) {
		this.health = health;
	}
	public int getColour() {
		return colour;
	}
	// Colour in hex
	public void setColour(int colour) {
		this.colour = colour;
	}
}
