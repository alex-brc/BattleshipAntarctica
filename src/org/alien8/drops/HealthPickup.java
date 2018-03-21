package org.alien8.drops;

import org.alien8.physics.Position;

/**
 * This class represents a Pickup containing a HealthItem.
 */
public class HealthPickup extends Pickup {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   * 
   * @param position the Position of the Pickup
   */
  public HealthPickup(Position position) {
    super(position, new HealthItem(), Pickup.HEALTH);
  }
}
