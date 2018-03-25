package org.alien8.core;

/**
 * An interface for Ship controllers, such as
 * the Player and AIController.
 */
public interface Controller {
	
	/**
	 * This method is called every tick for every
	 * ship.
	 */
	public void update();
}
