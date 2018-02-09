package org.alien8.ship;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

/**
 * Developer's notes:
 * 
 * All calculations relative to the ship consider the ship "located" at the position right under the
 * center of mass of the ship. Turrets are moved together with it with that consideration in mind.
 * All calculations relative to the ship consider the ship "located" at the position right under the
 * center of mass of the ship. Turrets are moved together with it with that consideration in mind.
 * 
 */
public class Ship extends Entity {
  private Turret frontTurret;
  private Turret rearTurret;
  private Turret midTurret;

  public Ship(Position position, double direction) {
    super(position, direction, 0, Parameters.SHIP_MASS, Parameters.SHIP_LENGTH,
        Parameters.SHIP_WIDTH);

    frontTurret = new Turret(position, Turret.SMALL);
    midTurret = new Turret(position, Turret.BIG);
    rearTurret = new Turret(position, Turret.SMALL);

    setTurretsDirection(new Position(0, 0));
    setTurretsPosition();
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;

    setTurretsPosition();
    // Turrets direction set in model
  }

  /**
   * Sets the direction for all turrets for the new mouse position considering the current ship
   * direction for limiting the movement of turrets
   * 
   * @param mousePosition the latest position of the cursor
   */
  public void setTurretsDirection(Position mousePosition) {
    // For a natural look and feel, the turrets will have a 270* degrees of motion,
    // becoming unable to shoot "through" the ship. The exception is the middle turret
    // which is supposedly mounted above the others and can fire with 360* degrees
    // of motion

    // For s = abs. angle of ship, a = angle of the turret and
    // ra = angle of the turret relative to the ship, ra is
    // ra = a + (pi - s);
    double ra = 0;

    // Front
    double angle = Renderer.getScreenPosition(frontTurret.getPosition()).getAngleTo(mousePosition);
    angle = (-1) * angle + Math.PI / 2;
    ra = angle + (Math.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [pi/4,7*pi/4]
    if (ra > 1.0 * Math.PI / 4 && ra < 7.0 * Math.PI / 4)
    frontTurret.setDirection(angle);

    // Rear
    angle = Renderer.getScreenPosition(rearTurret.getPosition()).getAngleTo(mousePosition);
    angle = (-1) * angle + Math.PI / 2;
    ra = angle + (Math.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [5*pi/4, 3*pi/4]
    if (ra < 3.0 * Math.PI / 4 || ra > 5.0 * Math.PI / 4)
    rearTurret.setDirection(angle);

    // Mid
    angle = Renderer.getScreenPosition(midTurret.getPosition()).getAngleTo(mousePosition);
    angle = PhysicsManager.shiftAngle((-1) * angle + Math.PI / 2);
    // No need to get relative angle.
    // This can go shoot it wants
    midTurret.setDirection(angle);
  }

  /**
   * Sets the position for all turrets considering the ship's position and direction
   * 
   * @param shipPosition the ship's position
   */
  public void setTurretsPosition() {
    // The radius from the ship position to the turret position
    // Chosen to be a fifth of the length of the ship AWAY from
    // the tip of the ship.
    double r = 2 * 0.2 * Parameters.SHIP_LENGTH;

    frontTurret.setPosition(this.getPosition().addPosition(
        new Position(r * Math.cos(this.getDirection()), r * Math.sin(this.getDirection()))));

    rearTurret.setPosition(this.getPosition().addPosition(
        new Position((-r) * Math.cos(this.getDirection()), (-r) * Math.sin(this.getDirection()))));

    midTurret.setPosition(this.getPosition());
  }

  public void render(Renderer r) {
    r.drawRect((int) position.getX(), (int) position.getY(), 10, 20, 0x666666, false);
    frontTurret.render(r);
    rearTurret.render(r);
    midTurret.render(r);
  }

  public double getFrontTurretDirection() {
    return frontTurret.getDirection();
  }

  public double getRearTurretDirection() {
    return rearTurret.getDirection();
  }

  public double getMidTurretDirection() {
    return midTurret.getDirection();
  }

  public Position getFrontTurretPosition() {
    return frontTurret.getPosition();
  }

  public Position getMidTurretPosition() {
    return rearTurret.getPosition();
  }

  public Position getRearTurretPosition() {
    return rearTurret.getPosition();
  }

  public void frontTurretCharge() {
    frontTurret.charge();
  }

  public void midTurretCharge() {
    midTurret.charge();
  }

  public void rearTurretCharge() {
    rearTurret.charge();
  }

  public void frontTurretShoot() {
    frontTurret.shoot();
  }

  public void midTurretShoot() {
    midTurret.shoot();
  }

  public void rearTurretShoot() {
    rearTurret.shoot();
  }
}

