package org.alien8.drops;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;
import net.jafama.FastMath;

/**
 * This class represents a item pickup that contains an Item and can be picked up by ships.
 *
 */
public abstract class Pickup extends Entity {
  private static final long serialVersionUID = 2171627902685805520L;
  public static final int HEALTH = 0;
  public static final int SPEED = 1;
  public static final int NO_COOLDOWN = 2;
  public static final int INVULNERABLE = 3;
  public static final int MINE = 4;
  public static final int TORPEDO = 5;
  public static final int NUMBER_OF_PICKUPS = 6;

  protected Item item;
  protected Sprite sprite;
  protected int pickupType;

  /**
   * Constructor.
   * 
   * @param position
   * @param item
   * @param pickupType
   */
  public Pickup(Position position, Item item, int pickupType) {
    super(position, 0, 0, 0, Parameters.ITEM_LENGTH, Parameters.ITEM_WIDTH);
    this.item = item;
    this.sprite = Sprite.pickup;
    this.pickupType = pickupType;
  }

  /**
   * @return the type of the Pickup
   */
  public int getPickupType() {
    return pickupType;
  }

  /**
   * Provides the Ship that collected this Pickup with the Item contained within the Pickup. Called
   * when a Ship runs over it.
   */
  public void onPickup(Ship ship) {
    item.setShip(ship);
    ship.giveItem(item);
  }

  @Override
  public void render() {
    Renderer.getInstance().drawSprite((int) position.getX() - sprite.getWidth() / 2,
        (int) position.getY() - sprite.getHeight() / 2, sprite, false);

    if (Parameters.DEBUG_MODE) {
      Renderer r = Renderer.getInstance();
      // Render four corners of bounding box
      for (int i = 0; i < 4; i++) {
        // Color front two points blue
        if (i == 1 || i == 2) {
          r.drawRect((int) this.getObb()[i].getX(), (int) this.getObb()[i].getY(), 4, 4, 0x0000FF,
              false);
        }
        // Color back two points red
        else {
          r.drawRect((int) this.getObb()[i].getX(), (int) this.getObb()[i].getY(), 4, 4, 0xFF0000,
              false);
        }
      }
      r.drawRect((int) this.getPosition().getX(), (int) this.getPosition().getY(), 4, 4, 0x00FFFF,
          false);

      /// Display AABB
      Position pos = getPosition();
      double length = getLength();
      double x = pos.getX();
      double y = pos.getY();

      double dir = PhysicsManager.shiftAngle(getDirection());
      double hypotenuse = length / 2;
      Position max;
      Position min;

      if (dir >= 0 && dir < Math.PI / 2) {
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      } else if (dir >= Math.PI / 2 && dir < Math.PI) {
        dir = Math.PI - dir;
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      } else if (dir >= Math.PI && dir < 3 * Math.PI / 2) {
        dir = (3 * Math.PI / 2) - dir;
        max = new Position(x + hypotenuse * FastMath.sin(dir), y - hypotenuse * FastMath.cos(dir));
        min = new Position(x - hypotenuse * FastMath.sin(dir), y + hypotenuse * FastMath.cos(dir));
      } else {
        dir = (2 * Math.PI) - dir;
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      }

      // // Calculate max and min points
      // Position max = new Position((pos.getX() + 0.5 * length * FastMath.cos(getDirection())),
      // (pos.getY() + 0.5 * length * FastMath.sin(getDirection())));
      // Position min = new Position((pos.getX() - 0.5 * length * FastMath.cos(getDirection())),
      // (pos.getY() - 0.5 * length * FastMath.sin(getDirection())));
      r.drawText("MAX", new Double(max.getX()).intValue(), new Double(max.getY()).intValue(), false,
          FontColor.BLACK);
      r.drawText("MIN", new Double(min.getX()).intValue(), new Double(min.getY()).intValue(), false,
          FontColor.WHITE);

      r.drawRect(new Double(min.getX()).intValue(), new Double(max.getY()).intValue(),
          new Double(max.getX() - min.getX()).intValue(),
          new Double(min.getY() - max.getY()).intValue(), 0x00FF00, false);
    }
  }

  @Override
  public void dealWithOutOfBounds() {
    // Will never be out of bounds
  }

  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    // Will never be in ice
  }
}
