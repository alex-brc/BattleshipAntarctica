package org.alien8.items;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;

public abstract class Pickup extends Entity {
  private static final long serialVersionUID = 2171627902685805520L;
  public static final int HEALTH_PICKUP = 0;
  public static final int SPEED_PICKUP = 1;
  public static final int NO_COOLDOWN_PICKUP = 2;
  public static final int INVULNERABLE_PICKUP = 3;
  public static final int MINE_PICKUP = 4;
  public static final int TORPEDO_PICKUP = 5;
  public static final int NUMBER_OF_PICKUPS = 6;


  protected Item item;
  protected Sprite sprite;
  protected int pickupType;

  public Pickup(Position position, Item item, int pickupType) {
    super(position, 0, 0, 0, Parameters.ITEM_LENGTH, Parameters.ITEM_WIDTH);
    this.item = item;
    this.sprite = Sprite.pickup;
    this.pickupType = pickupType;
  }

  /**
   * Called when item is picked up (i.e. when a player runs over it)
   */
  public void onPickup(Ship ship) {
    item.setShip(ship);
    ship.giveItem(item);
  }

  @Override
  public void render() {
    Renderer.getInstance().drawSprite((int) position.getX() - sprite.getWidth() / 2,
        (int) position.getY() - sprite.getHeight() / 2, sprite, false);
  }

  @Override
  public void dealWithOutOfBounds() {
    // Will never be out of bounds

  }

  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    // Will never be in ice
  }

  public int getPickupType() {
    return pickupType;
  }
}
