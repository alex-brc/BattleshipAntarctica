package org.alien8.items;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

import net.jafama.FastMath;

public class Torpedo extends Entity {
	private static final long serialVersionUID = -799176400440902424L;
	
	private long shipSerial;
	
	public Torpedo(Position position, long shipSerial, double direction) {
		super(position, direction, Parameters.TORPEDO_SPEED, 0, Parameters.TORPEDO_LENGTH, Parameters.TORPEDO_WIDTH);
		this.shipSerial = shipSerial;
	}
	
	public long getSource() {
		return this.shipSerial;
	}
	
	@Override
	public void render() {
		//TODO give this a sprite
		Sprite currentSprite = Sprite.bullet.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
	    Renderer.getInstance().drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
	        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
	}

	@Override
	public void dealWithOutOfBounds() {
		if(this.isOutOfBounds())
			this.delete();
	}

	@Override
	public void dealWithInIce(boolean[][] iceGrid) {
		// Should pass through ice
	}

}
