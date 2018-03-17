package org.alien8.items;

import org.alien8.physics.Position;

/**
 * This class represents a Pickup containing a TorpedoItem.
 */
public class TorpedoPickup extends Pickup {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   * 
   * @param position the Position of the Pickup
   */
  public TorpedoPickup(Position position) {
    super(position, new TorpedoItem(), Pickup.TORPEDO_PICKUP);
  }

}
