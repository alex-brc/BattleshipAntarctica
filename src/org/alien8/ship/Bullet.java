package org.alien8.ship;

import java.io.Serializable;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import net.jafama.FastMath;

public class Bullet extends Entity implements Serializable {

  private static final long serialVersionUID = -4758229490654529751L;
  protected Sprite sprite = Sprite.bullet;
  private double distance;
  private double damage;
  private double travelled;
  private long source;

  public Bullet(Position position, double direction, double distance, long source) {
    super(position, direction, Parameters.BULLET_SPEED, Parameters.BULLET_MASS,
        Parameters.BULLET_LENGTH, Parameters.BULLET_WIDTH);

    this.distance = distance;
    this.damage = Parameters.BULLET_DAMAGE;
    this.travelled = 0;
    this.source = source;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;
    this.travelled += this.getSpeed();
    // If this distance calculation is too slow, we can change to a limited life span of bullets,
    // after which they are deleted
    if (this.getPosition().isOutOfBounds() || this.travelled > this.distance)
      this.delete();
  }

  public double getDamage() {
    return damage;
  }

  /**
   * This method should delete a bullet if it goes out of the bounds of the map. It isn't too
   * crucial as bullets automatically delete after they have travelled their distance.
   */
  public void dealWithOutOfBounds() {
    if (this.isOutOfBounds()) {
      this.delete();
    }
  }

  public void setSource(long source) {
    this.source = source;
  }

  public long getSource() {
    return source;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double getDistance() {
    return distance;
  }

  public double getTravelled() {
    return travelled;
  }

  public void setTravelled(double travelled) {
    this.travelled = travelled;
  }

  public boolean equals(Bullet b) {
    return this.getSerial() == b.getSerial() && this.getPosition().equals(b.getPosition())
        && this.isToBeDeleted() == b.isToBeDeleted() && this.getMass() == b.getMass()
        && this.getSpeed() == b.getSpeed() && this.getDirection() == b.getDirection()
        && this.getLength() == b.getLength() && this.getWidth() == b.getWidth()
        && this.getDistance() == b.getDistance() && this.getTravelled() == b.getTravelled();
  }

  public String toString() {
    return "Bullet: " + this.travelled + "/" + this.getDistance() + ", " + this.getSerial() + ", "
        + this.getPosition();
  }

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
}
