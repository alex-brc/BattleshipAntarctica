package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public class Bullet extends Entity {
	private Position startingPosition;
	private double distance;
	
	public Bullet(Position position, double direction, int bulletType, double distance) {
		this.position = position;
		this.setDirection(direction);
		this.startingPosition = position;
		this.distance = distance;
		
		if(bulletType == Turret.SMALL) {
			this.setSpeed(Parameters.SMALL_BULLET_SPEED);
			this.setMass(Parameters.SMALL_BULLET_MASS);
			
		}
		else if(bulletType == Turret.BIG) {
			this.setSpeed(Parameters.BIG_BULLET_SPEED);
			this.setMass(Parameters.BIG_BULLET_MASS);	
		}
		
		initObb();
	}
	
	@Override
	public void setPosition(Position position) {
		this.position = position;
		
		// If this distance calculation is too slow, 
		// we can change to a limited life span of 
		// bullets, after which they are deleted 
		if(position.isOutOfBounds() || startingPosition.distanceTo(position) > distance)
			this.delete();
	}
	
	public void render(Renderer r){
		r.drawRect((int)position.getX(), (int)position.getY(), 1, 1, 0xffffff, false);
	}
}
