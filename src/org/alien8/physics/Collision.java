package org.alien8.physics;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.mapgeneration.Ice;
import org.alien8.score.ScoreBoard;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import net.jafama.FastMath;

public class Collision {
  private Entity entity1;
  private Entity entity2;
  private MTV mtv;

  public Collision(Entity entity1, Entity entity2) {
    this.entity1 = entity1;
    this.entity2 = entity2;
  }

  public Collision(Entity entity1, Entity entity2, MTV mtv) {
    this.entity1 = entity1;
    this.entity2 = entity2;
    this.mtv = mtv;
  }

  public Entity getEntity1() {
    return entity1;
  }

  public Entity getEntity2() {
    return entity2;
  }

  /**
   * Method to resolve a Collision. Calculates things like damage, velocity and direction
   * post-collision.
   */
  public void resolveCollision() {
    // Collision between two Ships
    if ((entity1 instanceof Ship) & (entity2 instanceof Ship)) {
      resolveShipShipCollision((Ship) entity1, (Ship) entity2);
    }
    // Collision between a Bullet and a Ship
    else if ((entity1 instanceof Ship) & (entity2 instanceof Bullet)) {
      resolveBulletShipCollision((Bullet) entity2, (Ship) entity1);
    } else if ((entity1 instanceof Bullet) & (entity2 instanceof Ship)) {
      resolveBulletShipCollision((Bullet) entity1, (Ship) entity2);
    }
    // // Collision between a Ship and Ice
    // else if ((entity1 instanceof Ship) & (entity2 instanceof Ice)) {
    // resolveShipIceCollision((Ship) entity1, (Ice) entity2);
    // } else if ((entity1 instanceof Ice) & (entity2 instanceof Ship)) {
    // resolveShipIceCollision((Ship) entity2, (Ice) entity1);
    // }
    // // Collision between a Bullet and Ice
    // else if ((entity1 instanceof Bullet) & (entity2 instanceof Ice)) {
    // resolveBulletIceCollision((Bullet) entity1, (Ice) entity2);
    // } else if ((entity1 instanceof Ice) & (entity2 instanceof Bullet)) {
    // resolveBulletIceCollision((Bullet) entity2, (Ice) entity1);
    // }
  }

  private void resolveShipShipCollision(Ship ship1, Ship ship2) {
    System.out.println("ship hit ship!");
    double speed1 = entity1.getSpeed();
    double speed2 = entity2.getSpeed();
    double direction1 = entity1.getDirection();
    double direction2 = entity2.getDirection();

    // Move ships apart according to the Minimum Translation Vector
    // mod 10 allows distance to be scaled down so that objects don't fly away from each other as
    // much
    double mtvX = mtv.getDistance() * mtv.getAxis().getX() % 10;
    double mtvY = mtv.getDistance() * mtv.getAxis().getY() % 10;

    Position pos1 = entity1.getPosition();
    Position pos2 = entity2.getPosition();

    double xdiff1 = 0, xdiff2 = 0, ydiff1 = 0, ydiff2 = 0;

    if (pos1.getX() > pos2.getX()) {
      xdiff1 = mtvX;
      xdiff2 = -mtvX;
    } else {
      xdiff1 = -mtvX;
      xdiff2 = mtvX;
    }

    if (pos1.getY() > pos2.getY()) {
      ydiff1 = mtvY;
      ydiff2 = -mtvY;

    } else {
      ydiff1 = -mtvX;
      ydiff2 = mtvX;
    }

    entity1.setPosition(new Position(pos1.getX() + xdiff1, pos1.getY() + ydiff1));
    entity1.translateObb(xdiff1, ydiff1);
    // System.out.println("New " + entity1.getPosition());
    entity2.setPosition(new Position(pos2.getX() + xdiff2, pos2.getY() + ydiff2));
    entity2.translateObb(xdiff2, ydiff2);


    // Position newPos1 =
    // new Position(entity1.getPosition().getX() + mtvX, entity1.getPosition().getY() + mtvY);
    // entity1.setPosition(newPos1);
    // entity1.translateObb(mtvX, mtvY);
    // System.out.println("New " + entity1.getPosition());
    // Position newPos2 =
    // new Position(entity2.getPosition().getX() - mtvX, entity2.getPosition().getY() - mtvY);
    // entity2.setPosition(newPos2);
    // entity2.translateObb(-mtvX, -mtvY);

    // Assume for the moment that ships have the same mass
    // This means that we can just swap their speed and direction
    // We multiply speed by the coefficient of restitution to decrease it
    entity1.setSpeed(speed2 * Parameters.RESTITUTION_COEFFICIENT);
    PhysicsManager.rotateEntity(entity1,
        ((direction1 - direction2) % 5) * Parameters.COLLISION_ROTATION_MODIFIER);
    // entity1.setDirection(direction2);
    entity2.setSpeed(speed1 * Parameters.RESTITUTION_COEFFICIENT);
    PhysicsManager.rotateEntity(entity2,
        ((direction2 - direction1) % 5) * Parameters.COLLISION_ROTATION_MODIFIER);
    // entity2.setDirection(direction1);

    // Each ship takes damage proportional to the momentum of the other ship
    entity1.damage(speed2 * entity2.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER);
    entity2.damage(speed1 * entity1.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER);
    // Delete ships if dead
    if (new Double(entity1.getHealth()).intValue() <= 0) {
      System.out.println("A ship died!");
      entity1.delete();
    }
    if (new Double(entity2.getHealth()).intValue() <= 0) {
      System.out.println("A ship died!");
      entity2.delete();
    }
  }

  private void resolveBulletShipCollision(Bullet bullet, Ship ship) {
    // This allows us to ignore cases where a ship shoots itself
    //System.out.println("B: " + bullet.getSource() + ", S: " + ship.getSerial());
    if (bullet.getSource() != ship.getSerial()) {
      System.out.println("B: " + bullet.getSource() + ", S: " + ship.getSerial());
      // Bullet damages ship
      ship.damage(bullet.getDamage());
      // Award score to the bullet owner
      Player shooter = Server.getInstance().getPlayer(bullet);
      // If it's AI, no points
      if (shooter != null)
        ScoreBoard.getInstance().giveHit(shooter, bullet);
      // See if ship has been destroyed
      if (new Double(ship.getHealth()).intValue() <= 0) {
        System.out.println("A ship died!");
        ship.delete();
        // Award score to the killer
        // If it's AI, no points
        if (shooter != null)
          ScoreBoard.getInstance().giveKill(shooter);
      }
      // Destroy bullet
      bullet.delete();
    }
  }

  private void resolveShipIceCollision(Ship ship, Ice ice) {
    System.out.println("ship hit ice");
    ship.setDirection(FastMath.PI + ship.getDirection());
  }

  private void resolveBulletIceCollision(Bullet bullet, Ice ice) {
    System.out.println("bullet hit ice");
    bullet.delete();
  }
}
