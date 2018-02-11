package org.alien8.ship;

import java.io.Serializable;

import org.alien8.core.Entity;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

<<<<<<< HEAD
public class Bullet extends Entity implements Serializable {
	private static final long serialVersionUID = -3644646556942405204L;
	private Position startingPosition;
	private double distance;
	
	public Bullet(Position position, double direction, int bulletType, double distance) {
		this.position = position;
		this.setDirection(direction);
		this.startingPosition = position;
		this.distance = distance;
		
		if(bulletType == Turret.SMALL) {
			this.setSpeed(Parameters.SMALL_BULLET_SPEED);
			this.setMass(Parameters.SMALL_BULLET_MASS);
			
		}
		else if(bulletType == Turret.BIG) {
			this.setSpeed(Parameters.BIG_BULLET_SPEED);
			this.setMass(Parameters.BIG_BULLET_MASS);	
		}
	}
	
	@Override
	public void setPosition(Position position) {
		this.position = position;
		
		// If this distance calculation is too slow, 
		// we can change to a limited life span of 
		// bullets, after which they are deleted 
		if(position.isOutOfBounds() || startingPosition.distanceTo(position) > distance)
			this.delete();
	}
	
	public void render(Renderer r){
		r.drawRect((int)position.getX(), (int)position.getY(), 1, 1, 0xffffff, false);
	}
=======
public abstract class Bullet extends Entity {

  private Position startingPosition;
  private double distance;
  private double damage;
  private double travelled;
  private long source;

  public Bullet(Position position, double direction, double distance, double mass, double width,
      double length, double speed, double damage, long source) {
    super(position, direction, speed, mass, length, width);
    this.distance = distance;
    this.damage = damage;
    this.startingPosition = position;
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

  public abstract void render(Renderer r);

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

  public long getSource() {
    return source;
  }
>>>>>>> f88cc358b04e1e88328d1b191d94b4f27ed9dc86
}
