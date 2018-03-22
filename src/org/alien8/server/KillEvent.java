package org.alien8.server;

import org.alien8.physics.Position;
import org.alien8.ship.Ship;

public class KillEvent extends GameEvent {
	private static final long serialVersionUID = 1L;
	
	private Position position;
	private int colour;
	private double direction;
	
	public KillEvent(Ship ship) {
		this.position = ship.getPosition();
		this.colour = ship.getColour();
		this.direction = ship.getDirection();
	}

	public Position getPosition() {
		return position;
	}

	public int getColour() {
		return colour;
	}

	public double getDirection() {
		return direction;
	}
}
