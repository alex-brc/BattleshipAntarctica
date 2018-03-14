package org.alien8.items;

import org.alien8.core.Parameters;
import org.alien8.rendering.Sprite;
import org.alien8.util.LogManager;

public class HealthItem extends Item {
  public HealthItem() {
    // Doesn't have a ship at this point
    super(Sprite.item_health);
  }

  /**
   * This item heals the ship by Parameters.ITEM_HEALTH_ITEM_VALUE
   */
  @Override
  public void use() {
    if (ship == null) {
      LogManager.getInstance().log("HealthItem", LogManager.Scope.ERROR,
          "Something is using an item belongig to a null ship. ");
      return;
    }
    if (ship.getHealth() > Parameters.SHIP_HEALTH - Parameters.ITEM_HEALTH_ITEM_VALUE)
      ship.setHealth(Parameters.SHIP_HEALTH);
    else
      ship.setHealth(ship.getHealth() + Parameters.ITEM_HEALTH_ITEM_VALUE);
  }
}
