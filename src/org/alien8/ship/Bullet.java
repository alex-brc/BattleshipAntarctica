package org.alien8.ship;

import java.io.Serializable;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import net.jafama.FastMath;

/**
 * This class represents a bullet Entity in the game.
 *
 */
public class Bullet extends Entity implements Serializable {

  private static final long serialVersionUID = -4758229490654529751L;
  protected Sprite sprite = Sprite.bullet;
  private double distance;
  private double damage;
  private double travelled;
  private long source;

  /**
   * Constructor.
   * 
   * @param position the Position of this Bullet in XY coordinates
   * @param direction the direction this Bullet is travelling in
   * @param distance the distance this Bullet will travel
   * @param source the serial ID of the Ship that fired this Bullet
   */
  public Bullet(Position position, double direction, double distance, long source) {
    super(position, direction, Parameters.BULLET_SPEED, Parameters.BULLET_MASS,
        Parameters.BULLET_LENGTH, Parameters.BULLET_WIDTH);

    this.distance = distance;
    this.damage = Parameters.BULLET_DAMAGE;
    this.travelled = 0;
    this.source = source;
  }

  /**
   * @param position the Position to set
   */
  @Override
  public void setPosition(Position position) {
    this.position = position;
    this.travelled += this.getSpeed();
    // If this distance calculation is too slow, we can change to a limited life span of bullets,
    // after which they are deleted
    if (this.getPosition().isOutOfBounds() || this.travelled > this.distance)
      this.delete();
  }

  /**
   * @return the distance this Bullet will travel
   */
  public double getDistance() {
    return distance;
  }

  /**
   * @param distance the distance to set
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * @return the damage this Bullet deals when colliding with a Ship
   */
  public double getDamage() {
    return damage;
  }

  /**
   * @return the distance this Bullet has travelled
   */
  public double getTravelled() {
    return travelled;
  }

  /**
   * @param travelled the distance to set
   */
  public void setTravelled(double travelled) {
    this.travelled = travelled;
  }

  /**
   * @return the serial ID of the Ship that fired this Bullet
   */
  public long getSource() {
    return source;
  }

  /**
   * @param source the serial ID to set
   */
  public void setSource(long source) {
    this.source = source;
  }

  /**
   * Checks for equality between this Bullet and another.
   * 
   * @param b the other Bullet
   * @return {@code true} if the Bullets are equal, {@code false} if not
   */
  public boolean equals(Bullet b) {
    return this.getSerial() == b.getSerial() && this.getPosition().equals(b.getPosition())
        && this.isToBeDeleted() == b.isToBeDeleted() && this.getMass() == b.getMass()
        && this.getSpeed() == b.getSpeed() && this.getDirection() == b.getDirection()
        && this.getLength() == b.getLength() && this.getWidth() == b.getWidth()
        && this.getDistance() == b.getDistance() && this.getTravelled() == b.getTravelled();
  }

  /**
   * @return a String representation of this Bullet
   */
  public String toString() {
    return "Bullet: " + this.travelled + "/" + this.getDistance() + ", " + this.getSerial() + ", "
        + this.getPosition();
  }

  /**
   * Renders this Bullet to the screen.
   */
  @Override
  public void render() {

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
    }

    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
    Renderer.getInstance().drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
  }

  /**
   * Checks if this Bullet is out of the bounds of the map, and deletes it if it is.
   */
  public void dealWithOutOfBounds() {
    if (this.isOutOfBounds()) {
      this.delete();
    }
  }


  /**
   * Checks if this Bullet is within ice, and deletes it if it is.
   */
  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    if (Parameters.ICE_IS_SOLID) {
      int x = (int) FastMath.rint(getPosition().getX());
      int y = (int) FastMath.rint(getPosition().getY());
      try {
        if (iceGrid[x][y] == true) {
          this.delete();
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        // This happens if the entity touches the edge of the map
      }
    }
  }
}
