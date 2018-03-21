package org.alien8.drops;

import org.alien8.physics.Position;

/**
 * This class represents a Pickup containing a NoCooldownItem.
 */
public class NoCooldownPickup extends Pickup {
  private static final long serialVersionUID = -4909285725302112826L;

  /**
   * Constructor.
   * 
   * @param position the Position of the Pickup
   */
  public NoCooldownPickup(Position position) {
    super(position, new NoCooldownItem(), Pickup.NO_COOLDOWN);
  }

}
