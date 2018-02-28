package org.alien8.ship;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

/**
 * Developer's notes:
 * 
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
  private int colour;
  private Sprite sprite; // for now

  public Ship(Position position, double direction, int colour) {
    super(position, direction, 0, Parameters.SHIP_MASS, Parameters.SHIP_LENGTH,
        Parameters.SHIP_WIDTH, Parameters.SHIP_HEALTH);
    System.out.println(getHealth());
    this.colour = colour;
    sprite = Sprite.makeShipSprite(colour);

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

    frontTurret.setPosition(this.getPosition().addPosition(
        new Position(r * Math.cos(this.getDirection()), r * Math.sin(this.getDirection()))));

    rearTurret.setPosition(this.getPosition().addPosition(
        new Position((-r) * Math.cos(this.getDirection()), (-r) * Math.sin(this.getDirection()))));

    midTurret.setPosition(this.getPosition());
  }

  public void render() {
    Renderer r = Renderer.getInstance();

    // // Render four corners of bounding box
    // for (int i = 0; i < 4; i++) {
    // // Color front two points blue
    // if (i == 1 || i == 2) {
    // r.drawRect((int) this.getObb()[i].getX(), (int) this.getObb()[i].getY(), 4, 4, 0x0000FF,
    // false);
    // }
    // // Color back two points red
    // else {
    // r.drawRect((int) this.getObb()[i].getX(), (int) this.getObb()[i].getY(), 4, 4, 0xFF0000,
    // false);
    // }
    // }

    // Render turrets
    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - Math.PI / 2));
    r.drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
    frontTurret.render();
    rearTurret.render();
    midTurret.render();
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

  /**
   * Method to stop a ship from going through ice on the map. Simply stops the ship from entering
   * ice.
   */
  public void dealWithInIce(boolean[][] iceGrid) {
    double shipX = getPosition().getX();
    double shipY = getPosition().getY();

    // Checks each corner of the ship
    for (Iterator<Position> iterator = Arrays.asList(getObb()).iterator(); iterator.hasNext();) {
      Position corner = (Position) iterator.next();
      // Rounds the Position (stored as double) to int so we can use it access the map array
      int x = (int) Math.rint(corner.getX());
      int y = (int) Math.rint(corner.getY());

      try {
        // The technique here is to search in all directions (up, down, left, right) to find the
        // minimum distance a ship would have to be moved to push it out of the ice. This should
        // give good performance as the ship will not have strayed too far into the ice, so the
        // minimum distance is the accurate direction to 'push' it out of the ice
        if (iceGrid[x][y]) {
          // System.out.println("Collision with ice");
          int posXdiff = findDiff(iceGrid, x, y, 1, 0);
          // System.out.println("posX done");
          int posYdiff = findDiff(iceGrid, x, y, 0, 1);
          // System.out.println("posY done");
          int negXdiff = findDiff(iceGrid, x, y, -1, 0);
          // System.out.println("negX done");
          int negYdiff = findDiff(iceGrid, x, y, 0, -1);
          // System.out.println("negY done");

          int xdiff;
          int ydiff;


          // Finds minimum x distance
          if (posXdiff >= negXdiff) {
            xdiff = -negXdiff;
          } else {
            xdiff = posXdiff;
          }
          // Finds minimum y distance
          if (posYdiff >= negYdiff) {
            ydiff = -negYdiff;
          } else {
            ydiff = posYdiff;
          }
          // Finds minimum overall distance and adjusts ship Position and bounding box
          if (Math.abs(xdiff) >= Math.abs(ydiff)) {
            setPosition(new Position(shipX, shipY + ydiff));
            translateObb(0, ydiff);
            if (ydiff <= 0) {

            }
            // PhysicsManager.rotateEntity(this, -ydiff * Parameters.ICE_BOUNCINESS);
            // initObb();
          } else {
            setPosition(new Position(shipX + xdiff, shipY));
            translateObb(xdiff, 0);
            // PhysicsManager.rotateEntity(this, -xdiff * Parameters.ICE_BOUNCINESS);
            // initObb();
          }

          // if (getDirection() >= 0 && getDirection() < Math.PI) {
          // PhysicsManager.rotateEntity(this, Math.abs(xdiff) * Parameters.ICE_BOUNCINESS);
          // } else {
          // PhysicsManager.rotateEntity(this, -Math.abs(xdiff) * Parameters.ICE_BOUNCINESS);
          // }

          // damage(properties.getSpeed() * Parameters.COLLISION_DAMAGE_MODIFIER);
          // System.out.println("Health: " + getHealth());

          // Halve the ship's speed for now
          // setSpeed(getSpeed() / 2);
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        // This happens if the entity touches the edge of the map, so we deal with it gracefully
      }
    }
  }

  /**
   * Method to find the distance a Ship is from water in a single direction. Used in the
   * {@code dealWithInIce()} method to find the distance to push a Ship out of ice.
   * 
   * @param iceGrid a 2-dimensional boolean array which represents the map
   * @param x the Position of the Ship in x
   * @param y the Position of the Ship in x
   * @param i the difference amount in x
   * @param j the difference amount in y
   * @return
   */
  private int findDiff(boolean[][] iceGrid, int x, int y, int i, int j) {
    int diff = 0;
    try {
      while (iceGrid[x][y]) {
        diff++;
        x += i;
        y += j;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // This could be caused by attempting to index outside of the map
      // System.out.println("exception");
      return diff;
    }
    // Return the difference anyway
    return diff;
  }

  public Turret getFrontTurret() {
    return this.frontTurret;
  }

  public Turret getMidTurret() {
    return this.midTurret;
  }

  public Turret getRearTurret() {
    return this.rearTurret;
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

  public int getColour() {
    return this.colour;
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

  public boolean equals(Ship s) {
    return this.getSerial() == s.getSerial() && this.getPosition().equals(s.getPosition())
        && this.isToBeDeleted() == s.isToBeDeleted() && this.getMass() == s.getMass()
        && this.getSpeed() == s.getSpeed() && this.getDirection() == s.getDirection()
        && this.getLength() == s.getLength() && this.getWidth() == s.getWidth()
        && this.getHealth() == s.getHealth();
  }

  public String toString() {
    return "Ship " + this.getSerial() + "," + this.getPosition();
  }

}
