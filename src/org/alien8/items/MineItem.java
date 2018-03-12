package org.alien8.items;

import org.alien8.rendering.Sprite;

public class MineItem extends Item {
	public MineItem() {
		// Doesn't have a ship at this point
		super(Sprite.bullet); //TODO give it Sprite.health_item
	}
	
	/**
	 * This item drops a mine that blows up when another ship hits it
	 */
	@Override
	public void use() {
		System.out.println("SUCCESS MINE (19:MineItem)");
	}
}
