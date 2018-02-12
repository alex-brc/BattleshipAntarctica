package org.alien8.ship;

import java.io.Serializable;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

/**
 * Developer's notes:

 * 
 * All calculations relative to the ship consider the ship "located" at the position right under the
 * center of mass of the ship. Turrets are moved together with it with that consideration in mind.
 * 
 */
public class Ship extends Entity implements Serializable {
  private static final long serialVersionUID = -432334137390727161L;
  private Turret frontTurret;
  private Turret rearTurret;
  private Turret midTurret;
  
  private Sprite sprite = Sprite.ship_green; //for now

  public Ship(Position position, double direction) {
    super(position, direction, 0, Parameters.SHIP_MASS, Parameters.SHIP_LENGTH,
        Parameters.SHIP_WIDTH);

    frontTurret = new Turret(position, Turret.SMALL, this);
    midTurret = new Turret(position, Turret.BIG, this);
    rearTurret = new Turret(position, Turret.SMALL, this);

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

    frontTurret
    .setPosition(
    		this.getPosition()
    		.addPosition(
        new Position(r * Math.cos(this.getDirection()), r * Math.sin(this.getDirection()))));

    rearTurret.setPosition(this.getPosition().addPosition(
        new Position((-r) * Math.cos(this.getDirection()), (-r) * Math.sin(this.getDirection()))));

    midTurret.setPosition(this.getPosition());
  }

  public void render(Renderer r) {
    // r.drawRect((int) position.getX(), (int) position.getY(), 10, 20, 0x666666, false);
    // r.drawRect((int) this.getObb()[0].getX(), (int) this.getObb()[0].getY(), (int)
    // this.getLength(),
    // (int) this.getWidth(), 0xFF0000, false);
	
    r.drawSprite((int) this.getObb()[0].getX(),(int) this.getObb()[0].getY(), sprite, false);
	  
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

    // Render turrets
    
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

  /**
   * Method to stop a ship from going out of the bounds of the map. The ship will go as far as the
   * border of the map and not be able to travel further.
   */
  public void dealWithOutOfBounds() {
    double xdiff = 0;
    double ydiff = 0;
    for (Position corner : this.getObb()) {
      // See if corner is out of bounds in the x-direction
      // Assume it is not so large that it will be off both sides of the map at once
      if (corner.getX() > Parameters.MAP_WIDTH) {
        if (corner.getX() - Parameters.MAP_WIDTH > xdiff) {
          xdiff = corner.getX() - Parameters.MAP_WIDTH;
        }
      } else if (corner.getX() < 0) {
        if (corner.getX() < xdiff) {
          xdiff = corner.getX();
        }
      }

      // Do the same for the y-direction
      if (corner.getY() > Parameters.MAP_HEIGHT) {
        if (corner.getY() - Parameters.MAP_HEIGHT > ydiff) {
          ydiff = corner.getY() - Parameters.MAP_HEIGHT;
        }
      } else if (corner.getY() < 0) {
        if (corner.getY() < ydiff) {
          ydiff = corner.getY();
        }
      }
    }
    // Update ship's position and bounding box
    this.setPosition(
        new Position(this.getPosition().getX() - xdiff, this.getPosition().getY() - ydiff));
    this.translateObb(-xdiff, -ydiff);
  }
  
  public boolean equals(Ship s) {
	  return this.getSerial() == s.getSerial() && this.getPosition().equals(s.getPosition()) && this.isToBeDeleted() == s.isToBeDeleted() && 
			 this.getMass() == s.getMass() && this.getSpeed() == s.getSpeed() && this.getDirection() == s.getDirection() && 
			 this.getLength() == s.getLength() && this.getWidth() == s.getWidth() && this.getHealth() == s.getHealth();
  }
  
  public String toString() {
	  return "Ship " + this.getSerial() + "," + this.getPosition();
  }
}
