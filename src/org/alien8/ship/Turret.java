package org.alien8.ship;

import java.io.Serializable;

import org.alien8.client.InputManager;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.server.AudioEvent;
import org.alien8.server.Server;

import net.jafama.FastMath;

public class Turret implements Serializable {

  private static final long serialVersionUID = -7308366899275446394L;
  // Type of bullets this turret shoots
  public static final int SMALL = 1;
  public static final int BIG = 2;
  // The parent ship of this turret
  private Ship ship;
  // Position will be handled by Ship class
  private Position position;
  // Orientation in radians
  private double direction;
  // Last time it shot in nanoseconds
  private long lastShot;
  // Type of bullets this turret shoots
  private int type;
  // Cooldown of this turret
  private long cooldown;
  // Charged distance of this turret
  private double distance;
  private final double minDistance;
  private final double maxDistance;

  private Sprite sprite = Sprite.turret; // for now

  public Turret(Position position, int type, Ship ship) {
    this.ship = ship;
    this.position = position;
    this.direction = 0;
    this.type = type;
    this.cooldown = (type == Turret.BIG) ? Parameters.BIG_BULLET_CD : Parameters.SMALL_BULLET_CD;
    this.lastShot = System.currentTimeMillis() - cooldown;
    this.minDistance =
        (type == Turret.BIG) ? Parameters.BIG_BULLET_MIN_DIST : Parameters.SMALL_BULLET_MIN_DIST;
    this.maxDistance =
        (type == Turret.BIG) ? Parameters.BIG_BULLET_MAX_DIST : Parameters.SMALL_BULLET_MAX_DIST;
    this.distance = this.minDistance;
  }

  /**
   * Charge the distance for every tick the button is pressed. Only start charging if it's not on
   * cooldown. If it reached max charge, shoot.
   */
  public void charge() {
    if (!this.isOnCooldown() && this.distance <= this.maxDistance)
      this.distance += Parameters.CHARGE_INCREMENT;
    else
      this.shoot();

    this.distance++;
  }

  /**
   * Shoots a bullet of the given type in the direction the turret is facing.
   * 
   * @param type
   */
  public void shoot() {
    if (distance == this.minDistance || this.isOnCooldown())
      return;

    if (type == Turret.BIG) {
      ModelManager.getInstance().addEntity(new BigBullet(this.getPosition(), this.getDirection(),
          distance, this.getShip().getSerial()));
    } else {
      ModelManager.getInstance().addEntity(new SmallBullet(this.getPosition(), this.getDirection(),
          distance, this.getShip().getSerial()));
    }

    Server.addEvent(new AudioEvent(AudioEvent.Type.SHOOT, this.getPosition()));
    this.startCooldown();
    this.distance = this.minDistance;
  }

  /**
   * @return the direction of the turret
   */
  public double getDirection() {
    return direction;
  }

  /**
   * @return the time of the last shot
   */

  public boolean isOnCooldown() {
    if (System.currentTimeMillis() - this.lastShot < this.cooldown)
      return true;
    return false;
  }

  /**
   * Puts the turret on cooldown.
   */
  private void startCooldown() {
    this.lastShot = System.currentTimeMillis();
  }

  /**
   * Gets the remaining cooldown
   * 
   * @return the remaining cooldown time in milliseconds
   */
  public long getCooldown() {
    long result = (lastShot + cooldown) - System.currentTimeMillis();
    if (result < 0)
      return 0;
    return result;
  }
  
  public Position getTargetPosition() {
	  Position result = new Position(0,0);
	  
	  result.setX(this.getPosition().getX() + FastMath.cos(this.getDirection()) * this.getDistance());
	  result.setY(this.getPosition().getY() + FastMath.sin(this.getDirection()) * this.getDistance());
	  
	  return result;
  }

  public void render() {
    Renderer r = Renderer.getInstance();

    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
    r.drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
    
    if(distance != minDistance) {
    	Position pos = getTargetPosition();
    	r.drawRect((int) pos.getX(), (int) pos.getY(), 6, 6, 0xFF0000, false);
    }
  }
  
  /**
   * @param direction the direction to set
   */
  public void setDirection(double direction) {
    this.direction = direction;
  }

  /**
   * @return the position
   */
  public Position getPosition() {
    return position;
  }

  /**
   * @param position the position to set
   */
  public void setPosition(Position position) {
    this.position = position;
  }

  protected Ship getShip() {
    return ship;
  }

  public double getDistance() {
	return distance;
  }
}
