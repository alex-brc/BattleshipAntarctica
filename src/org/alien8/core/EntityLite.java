package org.alien8.core;

import java.io.Serializable;

import org.alien8.physics.Position;

public class EntityLite implements Serializable {
	
	private static final long serialVersionUID = -7757472834374226318L;
	
	// For Ship and bullet
	public long serial;
	public int type;
	public Position position;
	public boolean toBeDeleted = false;
	public double direction;
	public double speed;
	
	// For Ship
	public double health;
		
	// For bullet
	public double distance;
	public double travelled;
	public long source;
	
	// For Ship
	public EntityLite(long serial, int type, Position position, boolean toBeDeleted, double direction, double speed, double health) {
		this.serial = serial;
		this.type = type;
		this.position = position;
		this.toBeDeleted = toBeDeleted;
		this.direction = direction;
		this.speed = speed;
		this.health = health;
	}
	
	// For Bullet
	public EntityLite(long serial, int type, Position position, boolean toBeDeleted, double direction, double speed, double distance, double travelled, long source) {
		this.serial = serial;
		this.type = type;
		this.position = position;
		this.toBeDeleted = toBeDeleted;
		this.direction = direction;
		this.speed = speed;
		this.distance = distance;
		this.travelled = travelled;
		this.source = source;
	}
	
}