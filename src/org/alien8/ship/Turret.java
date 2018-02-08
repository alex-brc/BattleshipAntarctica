package org.alien8.ship;

import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public class Turret {

  // Type of bullets this turret shoots
  public static final int SMALL = 1;
  public static final int BIG = 2;
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
  private double minDistance;
  private double maxDistance;

  public Turret(Position position, int type) {
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
    if (!this.isOnCooldon() && this.distance <= this.maxDistance)
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
    if (distance == this.minDistance || this.isOnCooldon())
      return;

    if (type == Turret.BIG) {
      ModelManager.getInstance()
          .addEntity(new BigBullet(this.getPosition(), this.getDirection(), distance));
    } else {
      ModelManager.getInstance()
          .addEntity(new SmallBullet(this.getPosition(), this.getDirection(), distance));
    }
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
  public boolean isOnCooldon() {
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

  public void render(Renderer r) {
    if (this.isOnCooldon())
      r.drawRect((int) position.getX(), (int) position.getY(), 4, 4, 0xFF0000, false);
    else
      r.drawRect((int) position.getX(), (int) position.getY(), 4, 4, 0x00FF00, false);
  }

  public Position getScreenPosition() {
    // TODO Auto-generated method stub
    return null;
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
}
