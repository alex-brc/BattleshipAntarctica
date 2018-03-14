package org.alien8.items;

import org.alien8.physics.Position;

public class SpeedPickup extends Pickup {
  private static final long serialVersionUID = -209294293040575331L;

  public SpeedPickup(Position position) {
    super(position, new SpeedItem(), Pickup.SPEED_PICKUP);
  }

}
