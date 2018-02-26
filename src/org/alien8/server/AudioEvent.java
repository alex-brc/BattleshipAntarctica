package org.alien8.server;

import java.io.Serializable;

import org.alien8.physics.Position;

public class AudioEvent implements Serializable{

	private static final long serialVersionUID = 8550834400277582403L;

	public enum Type {
		SHOOT,ICE_CRASH,SHIP_CRASH;
	}
	private Type type;
	private Position position;
	
	public AudioEvent(Type type, Position position) {
		this.type = type;
		this.position = position;
	}

	public Type getType() {
		return type;
	}

	public Position getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return type.name() + " " + position.toString();
	}
}
