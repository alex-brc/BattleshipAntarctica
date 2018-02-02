package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;

public class Bullet extends Entity {
	public static final int SMALL = 1;
	public static final int BIG = 2;
	
	
	public Bullet(Position position, double direction, int bulletType) throws Exception {
		if(bulletType == SMALL) {
			this.setPosition(position);
			this.setDirection(direction);
			this.setSpeed(Parameters.SMALL_BULLET_SPEED);
			this.setMass(Parameters.SMALL_BULLET_MASS);
		}
		else if(bulletType == BIG) {
			this.setPosition(position);
			this.setDirection(direction);
			this.setSpeed(Parameters.BIG_BULLET_SPEED);
			this.setMass(Parameters.BIG_BULLET_MASS);	
		}
		else
			throw new Exception("Bullet type not \"BIG\" or \"SMALL\"");
	}
	
	
}
