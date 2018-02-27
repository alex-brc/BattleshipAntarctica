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

  private Sprite sprite = Sprite.ship_green; // for now

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

    frontTurret.setPosition(this.getPosition().addPosition(
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
    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - Math.PI / 2));
    r.drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
    frontTurret.render(r);
    rearTurret.render(r);
    midTurret.render(r);
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

  public void dealWithInIce(boolean[][] iceGrid) {
    double direction = getDirection();
    double shipX = getPosition().getX();
    double shipY = getPosition().getY();

    // System.out.println("Ship " + getPosition());
    Position[] obb = this.getObb();

    for (Iterator<Position> iterator = Arrays.asList(getObb()).iterator(); iterator.hasNext();) {
      // for (Position corner: getObb()) {
      Position corner = (Position) iterator.next();
      // System.out.println("Position: " + corner);
      int x = (int) Math.rint(corner.getX());
      int y = (int) Math.rint(corner.getY());
      // System.out.println("Rounded X: " + x + "Y: " + y);

      try {
        if (iceGrid[x][y]) {
          System.out.println("Collision with ice");
          int posXdiff = findDiff(iceGrid, x, y, 1, 0);
          int posYdiff = findDiff(iceGrid, x, y, 0, 1);
          int negXdiff = findDiff(iceGrid, x, y, -1, 0);
          int negYdiff = findDiff(iceGrid, x, y, 0, -1);

          // System.out.println("posx = " + posXdiff);
          // System.out.println("negx = " + negXdiff);

          int xdiff;
          int ydiff;

          if (posXdiff >= negXdiff) {
            xdiff = -negXdiff;
          } else {
            xdiff = posXdiff;
          }

          if (posYdiff >= negYdiff) {
            ydiff = -negYdiff;
          } else {
            ydiff = posYdiff;
          }

          if (Math.abs(xdiff) >= Math.abs(ydiff)) {
            setPosition(new Position(shipX, shipY + ydiff));
            translateObb(0, ydiff);
            setSpeed(getSpeed() / 2);
          } else {
            setPosition(new Position(shipX + xdiff, shipY));
            translateObb(xdiff, 0);
            setSpeed(getSpeed() / 2);
          }

          // obb = this.getObb();

          // break;

          // while (iceGrid[x++][y++] == true) {
          // xdiff++;
          // ydiff++;
          // }
          // setPosition(new Position(shipX + xdiff, shipY + ydiff));
          // translateObb(xdiff, ydiff);
          /*
           * if (direction >= 0 && direction < Math.PI / 4) { setPosition(new Position(shipX, shipY
           * + 1)); System.out.println("1"); this.translateObb(0, 1); } else if (direction >=
           * Math.PI / 4 && direction < Math.PI / 2) { setPosition(new Position(shipX - 1, shipY));
           * System.out.println("2"); this.translateObb(-1, 0); } else if (direction >= Math.PI / 2
           * && direction < 3 * Math.PI / 4) { setPosition(new Position(shipX - 1, shipY));
           * System.out.println("3"); this.translateObb(-1, 0); } else if (direction >= 3 * Math.PI
           * / 4 && direction < Math.PI) { setPosition(new Position(shipX, shipY - 1));
           * System.out.println("4"); this.translateObb(0, -1); } else if (direction >= Math.PI &&
           * direction < 5 * Math.PI / 4) { setPosition(new Position(shipX, shipY - 1));
           * System.out.println("5"); this.translateObb(0, -1); } else if (direction >= 5 * Math.PI
           * / 4 && direction < 3 * Math.PI / 2) { setPosition(new Position(shipX + 1, shipY));
           * System.out.println("6"); this.translateObb(1, 0); } else if (direction >= 3 * Math.PI /
           * 2 && direction < 7 * Math.PI / 4) { setPosition(new Position(shipX + 1, shipY));
           * System.out.println("7"); this.translateObb(1, 0); } else if (direction >= 7 * Math.PI /
           * 4 && direction < Math.PI) { setPosition(new Position(shipX, shipY + 1));
           * System.out.println("8"); this.translateObb(0, 1); }
           */
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        // This happens if the entity touches the edge of the map
      }
    }
  }

  /**
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
    while (iceGrid[x][y]) {
      diff++;
      x += i;
      y += j;
      // System.out.println(x);
    }
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
