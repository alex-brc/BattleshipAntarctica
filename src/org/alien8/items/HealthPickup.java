package org.alien8.items;

import org.alien8.physics.Position;

public class HealthPickup extends Pickup {
  private static final long serialVersionUID = 1L;

  public HealthPickup(Position position) {
    super(position, new HealthItem(), Pickup.HEALTH_PICKUP); // Add sprite for
                                                             // health item
  }

}
