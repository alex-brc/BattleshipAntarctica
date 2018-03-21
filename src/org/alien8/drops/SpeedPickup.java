package org.alien8.drops;

import org.alien8.physics.Position;

/**
 * This class represents a Pickup containing a SpeedItem.
 */
public class SpeedPickup extends Pickup {
  private static final long serialVersionUID = -209294293040575331L;

  /**
   * Constructor.
   * 
   * @param position the Position of the Pickup
   */
  public SpeedPickup(Position position) {
    super(position, new SpeedItem(), Pickup.SPEED);
  }

}
