package org.alien8.items;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

public class Mine extends Entity {
	private static final long serialVersionUID = -799176400440902424L;
	
	private long shipSerial;
	
	public Mine(Position position, long shipSerial) {
		super(position, 0, 0, 0, Parameters.MINE_LENGTH, Parameters.MINE_WIDTH);
		this.shipSerial = shipSerial;
	}
	
	public long getSource() {
		return this.shipSerial;
	}
	
	@Override
	public void render() {
		//TODO give this a sprite
		Renderer.getInstance().drawSprite((int) position.getX()
				- Sprite.bullet.getWidth() / 2,
		        (int) position.getY() - Sprite.bullet.getHeight() / 2, Sprite.bullet, false);
	}

	@Override
	public void dealWithOutOfBounds() {
		// Will never be
	}

	@Override
	public void dealWithInIce(boolean[][] iceGrid) {
		// Will never be
	}

}
