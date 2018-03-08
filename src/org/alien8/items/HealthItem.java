package org.alien8.items;

import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;

public class HealthItem extends Item {
	private static final long serialVersionUID = -2433703415597645678L;
	
	public HealthItem(Position position) {
		super(position, null); //TODO give it Sprite.health_item
	}
	
	/**
	 * This item heals the ship by Parameters.ITEM_HEALTH_ITEM_VALUE
	 */
	@Override
	public void use(Ship ship) {
		if(ship.getHealth() > Parameters.SHIP_HEALTH - Parameters.ITEM_HEALTH_ITEM_VALUE)
			ship.setHealth(Parameters.SHIP_HEALTH);
		else
			ship.setHealth(ship.getHealth() + Parameters.ITEM_HEALTH_ITEM_VALUE);
	}

	@Override
	public void onPickup() {
		// TODO Auto-generated method stub
		
	}
}
