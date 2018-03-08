package org.alien8.ship;

import java.io.Serializable;
import org.alien8.audio.AudioEvent;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.server.Server;
import net.jafama.FastMath;

public class Turret implements Serializable {

  private static final long serialVersionUID = -7308366899275446394L;
  // The parent ship of this turret serial
  private long shipSerial;
  // Position will be handled by Ship class
  private Position position;
  // Orientation in radians
  private double direction;
  // Last time it shot in nanoseconds
  private long lastShot;
  // Cooldown of this turret
  private long cooldown;
  // Charged distance of this turret
  private double distance;
  private final double minDistance;
  private final double maxDistance;

  private Sprite sprite = Sprite.turret;

  public Turret(Position position, long shipSerial) {
    this.shipSerial = shipSerial;
    this.position = position;
    this.direction = 0;
    this.cooldown = Parameters.TURRET_CD;
    this.lastShot = System.currentTimeMillis() - cooldown;
    this.minDistance = Parameters.TURRET_MIN_DIST;
    this.maxDistance = Parameters.TURRET_MAX_DIST;
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
  }

  /**
   * Shoots a bullet of the given type in the direction the turret is facing.
   * 
   * @param type
   */
  public void shoot() {
    if (distance == this.minDistance || this.isOnCooldown())
      return;

    ModelManager.getInstance().addEntity(Server.getBullet(this.getPosition(), this.getDirection(),
    		distance, this.getShipSerial()));

    Server.getInstance().addEvent(new AudioEvent(AudioEvent.Type.SHOOT, this.getPosition()));
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

  /**
   * Gets the distance of the shot that the turret will fire.
   * 
   * @return the distance of the shot
   */
  public double getDistance() {
    return distance;
  }

  /**
   * Gets the maximum distance of the shot that the turret will fire.
   * 
   * @return the maximum distance of the shot
   */
  public double getMaxDistance() {
    return maxDistance;
  }

  public void render() {
    Renderer r = Renderer.getInstance();

    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
    r.drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);

    // if (this.isOnCooldown())
    // r.drawRect((int) position.getX(), (int) position.getY(), 4, 4, 0xFF0000, false);
    // else
    // r.drawRect((int) position.getX(), (int) position.getY(), 4, 4, 0x00FF00, false);

    if (distance != minDistance) {
      Position pos = getTargetPosition();
      Renderer.getInstance().drawRect((int) pos.getX(), (int) pos.getY(), 6, 6, 0xFF0000, false);
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

  protected long getShipSerial() {
    return shipSerial;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public void setShipSerial(long shipSerial) {
    this.shipSerial = shipSerial;
  }
}
