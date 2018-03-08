package org.alien8.ship;

import java.io.Serializable;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
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
    super(position, direction, Parameters.BULLET_SPEED, Parameters.BULLET_MASS, Parameters.BULLET_LENGTH, Parameters.BULLET_WIDTH);
    
    this.distance = distance;
    this.damage = Parameters.BULLET_DAMAGE;
    this.travelled = 0;
    this.source = source;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;
    this.travelled += this.getSpeed();
    // If this distance calculation is too slow,
    // we can change to a limited life span of
    // bullets, after which they are deleted
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
    return "Bullet: " + this.travelled + "/" + this.getDistance() + ", " + this.getSerial() + ", " + this.getPosition();
  }

  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
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
  
  @Override
  public void render() {
	    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
	    Renderer.getInstance().drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
	        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
  }
}
