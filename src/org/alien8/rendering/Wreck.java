package org.alien8.rendering;

import org.alien8.physics.Position;
import org.alien8.server.KillEvent;
import org.alien8.ship.Ship;

import net.jafama.FastMath;

public class Wreck {
	private Sprite sprite;
	private Sprite wreckageSprite;
	private Position position;
	private double direction;
	
	public Wreck(KillEvent killEvent) {
		this.position = killEvent.getPosition();
		this.direction = killEvent.getDirection();
		this.sprite = Sprite.pasteSprites(Sprite.makeShipSprite(killEvent.getColour()), Sprite.ship_wreckage)
						.rotateSprite(-(killEvent.getDirection() - FastMath.PI / 2));
	}
	
	/**
	 * @return the Position of this wreckage
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * @return the sprite of this wreckage
	 */
	public Sprite getSprite() {
		return sprite;
	}
	
	/**
	 * 
	 * @return the direction of this wreckage
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * @return the wreckageSprite of this wreckage
	 */
	public Sprite getWreckageSprite() {
		return wreckageSprite;
	}
}
