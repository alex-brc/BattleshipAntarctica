package org.alien8.items;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;

public abstract class Item extends Entity {
	private static final long serialVersionUID = 3008244926512742730L;
	
	protected Sprite sprite;

	public Item(Position position, Sprite sprite) {
		super(position, 0, 0, 0, Parameters.ITEM_LENGTH, Parameters.ITEM_WIDTH);
		this.sprite = sprite;
	}
	
	/**
	 * The use method is called on the item when the client "uses" it, 
	 * i.e. in our current environment, presses spacebar
	 */
	public abstract void use(Ship ship);
	
	/**
	 * Called the moment it's picked up.
	 */
	public abstract void onPickup();
	
	@Override
	public void render() {
		Renderer.getInstance().drawSprite((int) position.getX() - sprite.getWidth() / 2,
		        (int) position.getY() - sprite.getHeight() / 2, sprite, false);
	}
	
	@Override
	public void dealWithOutOfBounds() {
		// Will never be out of bounds	
	}

	@Override
	public void dealWithInIce(boolean[][] iceGrid) {
		// Will never be in ice
	}
}
