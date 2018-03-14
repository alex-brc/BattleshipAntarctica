package org.alien8.items;

import org.alien8.physics.Position;

public class TorpedoPickup extends Pickup {
  private static final long serialVersionUID = 1L;

  public TorpedoPickup(Position position) {
    super(position, new TorpedoItem(), Pickup.TORPEDO_PICKUP); // Add sprite for
                                                               // torpedo item
  }

}
