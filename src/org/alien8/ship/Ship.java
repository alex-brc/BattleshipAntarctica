package org.alien8.ship;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.items.Effect;
import org.alien8.items.Item;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import net.jafama.FastMath;

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
  private Item item;
  private Effect effect;
  private int colour;
  private Sprite sprite;


  public Ship(Position position, double direction, int colour) {
    super(position, direction, 0, Parameters.SHIP_MASS, Parameters.SHIP_LENGTH,
        Parameters.SHIP_WIDTH, Parameters.SHIP_HEALTH);
    this.colour = colour;
    sprite = Sprite.makeShipSprite(colour);

    frontTurret = new Turret(position, this.getSerial());
    rearTurret = new Turret(position, this.getSerial());

    setTurretsPosition();
    setTurretsDirection(new Position(0, 0));
  }

  /**
   * Called every tick to see if we need to alter any active effect
   */
  public void updateEffect() {
    if (effect != null) {
      if (effect.getEndTime() < System.currentTimeMillis())
        effect = null;
      else if (effect.getEffectType() == Effect.NO_COOLDOWN) {
        this.getFrontTurret().resetCooldown();
        this.getRearTurret().resetCooldown();
      }
    }
  }

  @Override
  public void setSpeed(double speed) {
    if (effect != null && effect.getEffectType() == Effect.SPEED) {
      super.setSpeed(Parameters.SHIP_TOP_SPEED_FORWARD * Parameters.ITEM_SPEED_ITEM_MULTIPLIER);
    } else
      super.setSpeed(speed);
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;

    setTurretsPosition();
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
    double angle = Renderer.getInstance().getScreenPosition(frontTurret.getPosition())
        .getAngleTo(mousePosition);
    angle = (-1) * angle + FastMath.PI / 2;
    ra = angle + (FastMath.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [pi/4,7*pi/4]
    if (ra > 1.0 * FastMath.PI / 4 && ra < 7.0 * FastMath.PI / 4)
      frontTurret.setDirection(angle);

    // Rear
    angle = Renderer.getInstance().getScreenPosition(rearTurret.getPosition())
        .getAngleTo(mousePosition);
    angle = (-1) * angle + FastMath.PI / 2;
    ra = angle + (FastMath.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [5*pi/4, 3*pi/4]
    if (ra < 3.0 * FastMath.PI / 4 || ra > 5.0 * FastMath.PI / 4)
      rearTurret.setDirection(angle);
  }

  public void setTurretsDirectionAI(Position mousePosition) {
    // Had to make this to allow the AI ships to aim at positions
    double ra = 0;

    // Front
    double angle = Renderer.getInstance().getScreenPositionAI(frontTurret.getPosition())
        .getAngleTo(mousePosition);
    angle = (-1) * angle + FastMath.PI / 2;
    ra = angle + (FastMath.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [pi/4,7*pi/4]
    if (ra > 1.0 * FastMath.PI / 4 && ra < 7.0 * FastMath.PI / 4)
      frontTurret.setDirection(angle);

    // Rear
    angle = Renderer.getInstance().getScreenPositionAI(rearTurret.getPosition())
        .getAngleTo(mousePosition);
    angle = (-1) * angle + FastMath.PI / 2;
    ra = angle + (FastMath.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [5*pi/4, 3*pi/4]
    if (ra < 3.0 * FastMath.PI / 4 || ra > 5.0 * FastMath.PI / 4)
      rearTurret.setDirection(angle);
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

    frontTurret.setPosition(this.getPosition()
        .addPosition(new Position(FastMath.floor(r * FastMath.cos(this.getDirection())),
            FastMath.floor(r * FastMath.sin(this.getDirection())))));

    rearTurret.setPosition(this.getPosition()
        .addPosition(new Position(FastMath.floor((-r) * FastMath.cos(this.getDirection())),
            FastMath.floor((-r) * FastMath.sin(this.getDirection())))));

  }

  @Override
  public void setSerial(long serial) {
    this.serial = serial;
    frontTurret.setShipSerial(serial);
    rearTurret.setShipSerial(serial);
  }

  public void render() {
    Renderer r = Renderer.getInstance();

    if (Parameters.DEBUG_MODE) {
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
      r.drawRect((int) this.getPosition().getX(), (int) this.getPosition().getY(), 4, 4, 0x00FFFF,
          false);

      /// Display AABB
      Position pos = getPosition();
      double length = getLength();
      double x = pos.getX();
      double y = pos.getY();

      double dir = PhysicsManager.shiftAngle(getDirection());
      double hypotenuse = length / 2;
      Position max;
      Position min;

      if (dir >= 0 && dir < Math.PI / 2) {
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      } else if (dir >= Math.PI / 2 && dir < Math.PI) {
        dir = Math.PI - dir;
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      } else if (dir >= Math.PI && dir < 3 * Math.PI / 2) {
        dir = (3 * Math.PI / 2) - dir;
        max = new Position(x + hypotenuse * FastMath.sin(dir), y - hypotenuse * FastMath.cos(dir));
        min = new Position(x - hypotenuse * FastMath.sin(dir), y + hypotenuse * FastMath.cos(dir));
      } else {
        dir = (2 * Math.PI) - dir;
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      }

      // // Calculate max and min points
      // Position max = new Position((pos.getX() + 0.5 * length * FastMath.cos(getDirection())),
      // (pos.getY() + 0.5 * length * FastMath.sin(getDirection())));
      // Position min = new Position((pos.getX() - 0.5 * length * FastMath.cos(getDirection())),
      // (pos.getY() - 0.5 * length * FastMath.sin(getDirection())));
      r.drawText("MAX", new Double(max.getX()).intValue(), new Double(max.getY()).intValue(), false,
          FontColor.BLACK);
      r.drawText("MIN", new Double(min.getX()).intValue(), new Double(min.getY()).intValue(), false,
          FontColor.WHITE);

      r.drawRect(new Double(min.getX()).intValue(), new Double(max.getY()).intValue(),
          new Double(max.getX() - min.getX()).intValue(),
          new Double(min.getY() - max.getY()).intValue(), 0x00FF00, false);
    }


    // Render ship sprite
    Sprite currentSprite = sprite.rotateSprite(-(this.getDirection() - FastMath.PI / 2));
    r.drawSprite((int) position.getX() - currentSprite.getWidth() / 2,
        (int) position.getY() - currentSprite.getHeight() / 2, currentSprite, false);
    // Display health
    r.drawText(new Integer(new Double(getHealth()).intValue()).toString(),
        new Double(getPosition().getX()).intValue() + 20,
        new Double(getPosition().getY()).intValue(), false, FontColor.BLACK);
    // Render turrets
    frontTurret.render();
    rearTurret.render();
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

    // // Handle rotation of the ship
    // if (xdiff != 0) {
    // if (getDirection())
    // }
    //
    //
    // PhysicsManager.rotateEntity(this, xdiff * Parameters.OUT_OF_BOUNDS_BOUNCINESS);
    // PhysicsManager.rotateEntity(this, ydiff * Parameters.OUT_OF_BOUNDS_BOUNCINESS);
  }

  /**
   * Method to stop a ship from going through ice on the map. Simply stops the ship from entering
   * ice.
   */
  public void dealWithInIce(boolean[][] iceGrid) {
    if (Parameters.ICE_IS_SOLID) {
      double shipX = getPosition().getX();
      double shipY = getPosition().getY();

      // Checks each corner of the ship
      for (Iterator<Position> iterator = Arrays.asList(getObb()).iterator(); iterator.hasNext();) {
        Position corner = (Position) iterator.next();
        // Rounds the Position (stored as double) to int so we can use it access the map array
        int x = (int) FastMath.rint(corner.getX());
        int y = (int) FastMath.rint(corner.getY());

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
            if (FastMath.abs(xdiff) >= FastMath.abs(ydiff)) {
              setPosition(new Position(shipX, shipY + ydiff));
              translateObb(0, ydiff);
              if (ydiff <= 0) {

              }
              PhysicsManager.rotateEntity(this, -ydiff * Parameters.ICE_BOUNCINESS);
              initObb();
            } else {
              setPosition(new Position(shipX + xdiff, shipY));
              translateObb(xdiff, 0);
              PhysicsManager.rotateEntity(this, -xdiff * Parameters.ICE_BOUNCINESS);
              initObb();
            }

            if (getDirection() >= 0 && getDirection() < FastMath.PI) {
              PhysicsManager.rotateEntity(this, FastMath.abs(xdiff) * Parameters.ICE_BOUNCINESS);
            } else {
              PhysicsManager.rotateEntity(this, -FastMath.abs(xdiff) * Parameters.ICE_BOUNCINESS);
            }

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

  public Turret getRearTurret() {
    return this.rearTurret;
  }

  public double getFrontTurretDirection() {
    return frontTurret.getDirection();
  }

  public double getRearTurretDirection() {
    return rearTurret.getDirection();
  }

  public double getFrontTurretCharge() {
    return frontTurret.getDistance();
  }

  public double getRearTurretCharge() {
    return rearTurret.getDistance();
  }

  public int getColour() {
    return this.colour;
  }

  public void frontTurretCharge() {
    frontTurret.charge();
  }

  public void rearTurretCharge() {
    rearTurret.charge();
  }

  public void frontTurretShoot() {
    frontTurret.shoot();
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

  public void giveItem(Item item) {
    if (this.item == null) {
      this.item = item;
      System.out.println("Picked up a " + item.getClass());
    }
  }

  public void useItem() {
    if (item != null) {
      this.item.use();
      item = null;
    }
  }

  public boolean hasItem() {
    if (item == null)
      return false;
    return true;
  }

  public void applyEffect(Effect effect) {
    this.effect = effect;
  }

  public int getEffectType() {
    return this.effect.getEffectType();
  }

  public boolean underEffect() {
    if (effect == null)
      return false;
    return true;
  }

  public Item getItem() {
    return item;
  }
}

