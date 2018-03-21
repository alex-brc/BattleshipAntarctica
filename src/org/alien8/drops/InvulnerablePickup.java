package org.alien8.drops;

import org.alien8.physics.Position;

/**
 * This class represents a Pickup containing an InvulnerableItem.
 */
public class InvulnerablePickup extends Pickup {
  private static final long serialVersionUID = -7611529310499072537L;

  /**
   * Constructor.
   * 
   * @param position the Position of the Pickup
   */
  public InvulnerablePickup(Position position) {
    super(position, new InvulnerableItem(), Pickup.INVULNERABLE);
  }

}
