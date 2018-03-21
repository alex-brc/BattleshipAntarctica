package org.alien8.drops;

import org.alien8.physics.Position;

/**
 * This class represents a Pickup containing a MineItem.
 */
public class MinePickup extends Pickup {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   * 
   * @param position the Position of the Pickup
   */
  public MinePickup(Position position) {
    super(position, new MineItem(), Pickup.MINE);
  }

}
