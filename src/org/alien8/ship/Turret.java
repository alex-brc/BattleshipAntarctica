package org.alien8.ship;

import java.io.Serializable;
import org.alien8.audio.AudioEvent;
import org.alien8.core.Parameters;
import org.alien8.core.ServerModelManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.server.Server;
import net.jafama.FastMath;

/**
 * This class represents a turret Entity within the game.
 *
 */
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

  /**
   * Constructor.
   * 
   * @param position the Position of this Turret in XY coordinates
   * @param shipSerial the serial ID of the Ship this Turret belongs to
   */
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
   * @return the position of this Turret in XY coordinates
   */
  public Position getPosition() {
    return position;
  }

  /**
   * @param position the Position to set
   */
  public void setPosition(Position position) {
    this.position = position;
  }

  /**
   * @return the direction of this Turret
   */
  public double getDirection() {
    return direction;
  }

  /**
   * @param direction the direction to set
   */
  public void setDirection(double direction) {
    this.direction = direction;
  }

  /**
   * @return the serial ID of the Ship this Turret belongs to
   */
  protected long getShipSerial() {
    return shipSerial;
  }

  /**
   * @param shipSerial the serial ID to set
   */
  public void setShipSerial(long shipSerial) {
    this.shipSerial = shipSerial;
  }

  /**
   * @return {@code true} if this Turret is on cooldown, {@code false} if not
   */
  public boolean isOnCooldown() {
    if (System.currentTimeMillis() - this.lastShot < this.cooldown)
      return true;
    return false;
  }

  /**
   * @return the remaining cooldown time in milliseconds of this Turret
   */
  public long getCooldown() {
    long result = (lastShot + cooldown) - System.currentTimeMillis();
    if (result < 0)
      return 0;
    return result;
  }

  /**
   * Puts this Turret on cooldown.
   */
  private void startCooldown() {
    this.lastShot = System.currentTimeMillis();
  }

  /**
   * Resets this Turret's cooldown.
   */
  public void resetCooldown() {
    this.lastShot = System.currentTimeMillis() - cooldown;
  }

  /**
   * @return the distance of the shot
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
   * @return the maximum distance of the shot
   */
  public double getMaxDistance() {
    return maxDistance;
  }

  /**
   * @return the target Position of the Bullet this Turret will fire
   */
  public Position getTargetPosition() {
    Position result = new Position(0, 0);

    result.setX(this.getPosition().getX() + FastMath.cos(this.getDirection()) * this.getDistance());
    result.setY(this.getPosition().getY() + FastMath.sin(this.getDirection()) * this.getDistance());

    return result;
  }

  /**
   * Charges a shot for this Turret. The distance increases for every tick the button is pressed.
   * Only starts charging if it's not on cooldown. Shoots if it has reached maximum charge.
   */
  public void charge() {
    if (!this.isOnCooldown() && this.distance <= this.maxDistance)
      this.distance += Parameters.CHARGE_INCREMENT;
    else
      this.shoot();
  }

  /**
   * Shoots a Bullet in the direction the turret is facing.
   */
  public void shoot() {
    if (distance == this.minDistance || this.isOnCooldown())
      return;
    
    ServerModelManager.getInstance().addEntity(Server.getInstance().getBullet(this.getPosition(),
        this.getDirection(), distance, this.getShipSerial()));

    Server.getInstance().addEvent(new AudioEvent(AudioEvent.Type.SHOOT, this.getPosition()));
    this.startCooldown();
    this.distance = this.minDistance;
  }

  /**
   * Renders this Turret to the screen.
   */
  public void render() {
    Renderer r = Renderer.getInstance();

    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
    r.drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);

    if (Parameters.DEBUG_MODE) {
      if (this.isOnCooldown())
        r.drawRect((int) position.getX(), (int) position.getY(), 4, 4, 0xFF0000, false);
      else
        r.drawRect((int) position.getX(), (int) position.getY(), 4, 4, 0x00FF00, false);
    }

    if (distance != minDistance) {
      Position pos = getTargetPosition();
      Renderer.getInstance().drawSprite((int) pos.getX() - Sprite.crosshair.getWidth(),
          (int) pos.getY() - Sprite.crosshair.getHeight(), Sprite.crosshair, false);
    }
  }
}
