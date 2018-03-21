package org.alien8.drops;

import org.alien8.core.ServerModelManager;
import org.alien8.rendering.Sprite;

/**
 * This class represents an Item that can drop a Mine behind a Ship when used.
 */
public class MineItem extends Item {
  /**
   * Constructor.
   */
  public MineItem() {
    // Doesn't have a ship at this point
    super(Sprite.item_mine, Pickup.MINE);
  }

  /**
   * Drops a mine that blows up when another Ship hits it.
   */
  @Override
  public void use() {
    ServerModelManager.getInstance().addEntity(new Mine(ship.getPosition(), ship.getSerial()));
  }
}
