package org.alien8.items;

import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;

public abstract class Item {
	protected Ship ship;
	protected Sprite sprite;
	protected int itemType;

	public Item(Sprite sprite, int itemType) {
		this.sprite = sprite;
	}
	
	/**
	 * The use method is called on the item when the client "uses" it, 
	 * i.e. in our current environment, presses space bar.
	 */
	public abstract void use();
	
	public void setShip(Ship ship) {
		this.ship = ship;
	}

	public int getItemType() {
		// TODO Auto-generated method stub
		return itemType;
	}
}
