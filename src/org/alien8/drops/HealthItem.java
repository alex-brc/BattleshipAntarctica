package org.alien8.drops;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;

/**
 * This class represents an Item that can heal a Ship when used.
 */
public class HealthItem extends Item {
  /**
   * Constructor.
   */
  public HealthItem() {
    // Doesn't have a ship at this point
    super(Sprite.item_health, Pickup.HEALTH);
  }

  /**
   * Heals the Ship by Parameters.ITEM_HEALTH_ITEM_VALUE.
   */
  @Override
  public void use() {
    if (ship != null) {
    	if (ship.getHealth() > Parameters.SHIP_HEALTH - Parameters.ITEM_HEALTH_ITEM_VALUE)
    		ship.setHealth(Parameters.SHIP_HEALTH);
    	else
    		ship.setHealth(ship.getHealth() + Parameters.ITEM_HEALTH_ITEM_VALUE);
    	}
    }
}
