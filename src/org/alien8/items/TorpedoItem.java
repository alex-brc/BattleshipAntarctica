package org.alien8.items;

import org.alien8.core.ServerModelManager;
import org.alien8.rendering.Sprite;

/**
 * This class represents an Item that a Ship to fire a Torpedo when used.
 */
public class TorpedoItem extends Item {
  public TorpedoItem() {
    // Doesn't have a ship at this point
    super(Sprite.item_torpedo, Pickup.TORPEDO_PICKUP);
  }

  /**
   * Shoots a Torpedo.
   */
  @Override
  public void use() {
    ServerModelManager.getInstance()
        .addEntity(new Torpedo(ship.getPosition(), ship.getSerial(), ship.getDirection()));
  }
}
