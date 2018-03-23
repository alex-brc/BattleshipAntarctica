package org.alien8.ship;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.drops.Effect;
import org.alien8.drops.Item;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.server.KillEvent;
import org.alien8.server.Server;

import net.jafama.FastMath;

/**
 * This class represents a ship Entity within the game.
 * <P>
 * All calculations relative to the ship consider the ship "located" at the position right under the
 * center of mass of the ship. Turrets are moved together with it with that consideration in mind.
 * 
 */
public class Ship extends Entity implements Serializable {
  private static final long serialVersionUID = -432334137390727161L;
  private Turret frontTurret;
  private Turret rearTurret;
  private double[] flameSpots;
  private Item item;
  private Effect effect;
  private int colour;
  private Sprite sprite;

  /**
   * Constructor.
   * 
   * @param position the Position of this Ship in XY coordinates
   * @param direction the direction this Ship is travelling in
   * @param colour the colour of this Ship
   */
  public Ship(Position position, double direction, int colour) {
    super(position, direction, 0, Parameters.SHIP_MASS, Parameters.SHIP_LENGTH,
        Parameters.SHIP_WIDTH, Parameters.SHIP_HEALTH);
    this.colour = colour;
    sprite = Sprite.makeShipSprite(colour);

    frontTurret = new Turret(position, this.getSerial());
    rearTurret = new Turret(position, this.getSerial());

    // Chosen to look relatively good
    flameSpots = new double[]{
    		0.1,
    		-0.2,
    		0.17};
    
    setTurretsPosition();
    setTurretsDirection(new Position(0, 0));
  }

  /**
   * @param position the Position to set
   */
  @Override
  public void setPosition(Position position) {
    this.position = position;

    setTurretsPosition();
  }

  /**
   * @param serial the serial ID to set
   */
  @Override
  public void setSerial(long serial) {
    this.serial = serial;
    frontTurret.setShipSerial(serial);
    rearTurret.setShipSerial(serial);
  }

  /**
   * @param speed the speed to set
   */
  @Override
  public void setSpeed(double speed) {
    if (effect != null && effect.getEffectType() == Effect.SPEED) {
      super.setSpeed(Parameters.SHIP_TOP_SPEED_FORWARD * Parameters.ITEM_SPEED_ITEM_MULTIPLIER);
    } else
      super.setSpeed(speed);
  }

  /**
   * @param damage the amount of damage to inflict on the ship
   */
  public void damage(double damage) {
	  if(this.underEffect() && this.getEffectType() == Effect.INVULNERABLE)
		  return;
	  
	  this.setHealth(this.getHealth() - damage);
  }
  
  /**
   * @return the front Turret of this Ship
   */
  public Turret getFrontTurret() {
    return this.frontTurret;
  }

  /**
   * @return the direction the front Turret of this Ship is facing
   */
  public double getFrontTurretDirection() {
    return frontTurret.getDirection();
  }

  /**
   * @return the charge of the front Turret of this Ship
   */
  public double getFrontTurretCharge() {
    return frontTurret.getDistance();
  }

  /**
   * Charges the front Turret of this Ship.
   */
  public void frontTurretCharge() {
    frontTurret.charge();
  }

  /**
   * Shoots the front Turret of this Ship.
   */
  public void frontTurretShoot() {
    frontTurret.shoot();
  }

  /**
   * @return the rear Turret of this Ship
   */
  public Turret getRearTurret() {
    return this.rearTurret;
  }

  /**
   * @return the direction the rear Turret of this Ship is facing
   */
  public double getRearTurretDirection() {
    return rearTurret.getDirection();
  }

  /**
   * @return the charge of the rear Turret of this Ship
   */
  public double getRearTurretCharge() {
    return rearTurret.getDistance();
  }

  /**
   * Charges the rear Turret of this Ship.
   */
  public void rearTurretCharge() {
    rearTurret.charge();
  }

  /**
   * Shoots the rear Turret of this Ship.
   */
  public void rearTurretShoot() {
    rearTurret.shoot();
  }

  /**
   * Sets the position for all turrets considering the ship's Position and direction.
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

  /**
   * Sets the direction for all turrets for the new mouse position considering the current ship
   * direction for limiting the movement of turrets.
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

  /**
   * Sets the direction for all turrets for the target Position considering the current ship
   * direction for limiting the movement of turrets.
   * 
   * @param targetPosition the latest position of the cursor
   */
  public void setTurretsDirectionAI(Position targetPosition) {
    // Had to make this to allow the AI ships to aim at positions
    double ra = 0;
    
    Random rand = new Random();
    
    // Front
    double angle = frontTurret.getPosition().getAngleTo(targetPosition);
    angle += rand.nextDouble() * Parameters.AI_PRECISION - Parameters.AI_PRECISION / 2;
    angle = (-1) * angle + FastMath.PI / 2;
    ra = angle + (FastMath.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [pi/4,7*pi/4]
    if (ra > 1.0 * FastMath.PI / 4 && ra < 7.0 * FastMath.PI / 4)
      frontTurret.setDirection(angle);

    // Rear
    angle = rearTurret.getPosition().getAngleTo(targetPosition);
    angle += rand.nextDouble() * Parameters.AI_PRECISION - Parameters.AI_PRECISION / 2;
    angle = (-1) * angle + FastMath.PI / 2;
    ra = angle + (FastMath.PI - this.getDirection());
    ra = PhysicsManager.shiftAngle(ra);
    // Range of motion: [5*pi/4, 3*pi/4]
    if (ra < 3.0 * FastMath.PI / 4 || ra > 5.0 * FastMath.PI / 4)
      rearTurret.setDirection(angle);
  }

  /**
   * @return the Item this Ship is currently carrying
   */
  public Item getItem() {
    return item;
  }

  /**
   * @return the type of the Item this Ship is currently carrying
   */
  public int getItemType() {
    if (item != null)
      return this.item.getItemType();
    return -1;
  }

  /**
   * @param item the Item to give to this Ship
   */
  public void giveItem(Item item) {
    if (this.item == null) {
      this.item = item;
    }
  }

  /**
   * Uses the Item this Ship is currently carrying.
   */
  public void useItem() {
    if (item != null) {
      this.item.use();
      item = null;
    }
  }

  /**
   * @return {@code true} if this Ship is carrying an Item, {@code false} if not
   */
  public boolean hasItem() {
    if (item == null)
      return false;
    return true;
  }

  /**
   * @return {@code true} if this Ship is under an Effect, {@code false} if not
   */
  public boolean underEffect() {
    if (effect == null)
      return false;
    return true;
  }

  /**
   * @return the type of the Effect this Ship is currently under
   */
  public int getEffectType() {
    if (effect != null)
      return this.effect.getEffectType();
    return -1;
  }

  /**
   * @param effect the Effect to apply to this Ship
   */
  public void applyEffect(Effect effect) {
    this.effect = effect;
  }

  /**
   * Checks if any active effects of this Ship need updating, and alters them if necessary. Must be
   * called every tick.
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

  /**
   * @return the colour value of this Ship
   */
  public int getColour() {
    return this.colour;
  }

  /**
   * Checks for equality between this Ship and another.
   * 
   * @param s the other Ship
   * @return {@code true} if the Ships are equal, {@code false} if not
   */
  public boolean equals(Ship s) {
    return this.getSerial() == s.getSerial() && this.getPosition().equals(s.getPosition())
        && this.isToBeDeleted() == s.isToBeDeleted() && this.getMass() == s.getMass()
        && this.getSpeed() == s.getSpeed() && this.getDirection() == s.getDirection()
        && this.getLength() == s.getLength() && this.getWidth() == s.getWidth()
        && this.getHealth() == s.getHealth();
  }

  /**
   * @return a String representation of this Ship
   */
  public String toString() {
    return "Ship " + this.getSerial() + "," + this.getPosition();
  }

  /**
   * Renders this Ship to the screen.
   */
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
    
    // Render turrets
    frontTurret.render();
    rearTurret.render();

    // Display health
    r.drawRect((int) (position.getX() - Parameters.HEALTH_BAR_WIDTH/2), 
    		(int) (position.getY() + Parameters.SHIP_WIDTH/2 + 5), 
    		Parameters.HEALTH_BAR_WIDTH, Parameters.HEALTH_BAR_HEIGHT,
    		0x000000, false);
    r.drawFilledRect((int) position.getX() - Parameters.HEALTH_BAR_WIDTH/2 + 1, 
    		(int) (position.getY() + Parameters.SHIP_WIDTH/2 + 5) + 1, 
    		(int) ((Parameters.HEALTH_BAR_WIDTH - 1) * this.getHealth() / Parameters.SHIP_HEALTH), 
    		Parameters.HEALTH_BAR_HEIGHT - 1,
    		getHealthColor(this.getHealth()), 
    		false);
    
    // Draw damage level
    int damageLevel = this.getDamageLevel(this.getHealth());
    double p = 0;
    Position pos = new Position(0,0);
    for(int i = 0; i < damageLevel; i++) {
      p = 2 * Parameters.SHIP_LENGTH * flameSpots[i];
      pos = this.getPosition()
              .addPosition(new Position(FastMath.floor(p * FastMath.cos(this.getDirection())),
                      FastMath.floor(p * FastMath.sin(this.getDirection()))));
  	  Renderer.getInstance().drawSprite(
  			  (int) pos.getX() - Sprite.fires[i].getWidth() + 3,
  			  (int) pos.getY() - Sprite.fires[i].getHeight() + 3,
  			  Sprite.fires[i], false);
    }
    
    /* frontTurret.setPosition(this.getPosition()
            .addPosition(new Position(FastMath.floor(r * FastMath.cos(this.getDirection())),
                FastMath.floor(r * FastMath.sin(this.getDirection()))))); */
    
    // Draw effects
    if (effect != null) {
      Sprite sp = null;
      switch (effect.getEffectType()) {
        case Effect.NO_COOLDOWN:
          sp = Sprite.item_no_cooldown;
          break;
        case Effect.SPEED:
          sp = Sprite.effect_speed;
          sp = sp.rotateSprite(this.getDirection() * -1 + Math.PI / 2);
          break;
        case Effect.INVULNERABLE:
          sp = Sprite.effect_invulnerable;
          sp = sp.rotateSprite(this.getDirection() * -1 + Math.PI / 2);
          break;
      }

      Renderer.getInstance().drawSprite((int) this.getPosition().getX() - sp.getWidth() / 2,
          (int) this.getPosition().getY() - sp.getHeight() / 2, sp, false);
    }
  }
  
  /**
   * Determines how many flame sprites to draw on the ship
   * 
   * @param health the health of the ship
   * @return the number of flame sprites to draw on the ship
   */
  private int getDamageLevel(double health) {
	  if(health <= 20)
		  return 3;
	  else if(health <= 60)
		  return 2;
	  else if(health <= 80)
		  return 1;
	  else 
		  return 0;
  }
  
  /**
   * Determines the colour of the health bar
   * 
   * @param health the current health of the ship
   * @return a hex color value for the ship health bar
   */
  private int getHealthColor(double health) {
	  if(health <= 20)
		  return 0xFF0000;
	  else if(health <= 60)
		  return 0xFFA500;
	  else 
		  return 0x00C000;
  }
  
  /**
   * Kills this ship. 
   * First will show it being destroyed then
   * it will call delete() on itself.
   */
  public void kill() {
	Server.getInstance().addEvent(new KillEvent(this));
	this.delete();
  }

  /**
   * Prevents this Ship from moving outside of the bounds of the map. The Ship will go as far as the
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
   * Prevents this Ship from going through ice on the map.
   */
  public void dealWithInIce(boolean[][] iceGrid) {
    if (Parameters.ICE_IS_SOLID) {
      double shipX = getPosition().getX();
      double shipY = getPosition().getY();

      int i = 0;
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
            double minDistance = 0;
            if (FastMath.abs(xdiff) >= FastMath.abs(ydiff)) {
              minDistance = ydiff;
              setPosition(new Position(shipX, shipY + ydiff));
              translateObb(0, ydiff);
              PhysicsManager.rotateEntity(this, -ydiff * Parameters.ICE_BOUNCINESS);
              initObb();
            } else {
              minDistance = xdiff;
              setPosition(new Position(shipX + xdiff, shipY));
              translateObb(xdiff, 0);
              PhysicsManager.rotateEntity(this, -xdiff * Parameters.ICE_BOUNCINESS);
              initObb();
            }

            double rotateAmount = minDistance * Parameters.ICE_BOUNCINESS;
            if (i == 0 || i == 3) {
              PhysicsManager.rotateEntity(this, rotateAmount);
              initObb();
            } else {
              // In this case, i == 1 or i == 2
              PhysicsManager.rotateEntity(this, -rotateAmount);
            }

            i++;
          }
        } catch (ArrayIndexOutOfBoundsException e) {
          // This happens if the entity touches the edge of the map, so we deal with it gracefully
        }
      }
    }
  }

  /**
   * Finds the distance this Ship is from water in a single direction. Used in the
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

}

