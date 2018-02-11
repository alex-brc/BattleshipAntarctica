package org.alien8.physics;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;

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
    // Ship-Ship collision
    if ((entity1 instanceof Ship) & (entity2 instanceof Ship)) {
      // System.out.println("Two ships have collided!");
      double speed1 = entity1.getSpeed();
      double speed2 = entity2.getSpeed();
      double direction1 = entity1.getDirection();
      double direction2 = entity2.getDirection();

      // Move ships apart according to the Minimum Translation Vector
      // System.out.println("Cur" + entity1.getPosition());
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
      PhysicsManager.rotateEntity(entity1, (direction1 - direction2) % 5);
      // entity1.setDirection(direction2);
      entity2.setSpeed(speed1 * Parameters.RESTITUTION_COEFFICIENT);
      PhysicsManager.rotateEntity(entity2, (direction2 - direction1) % 5);
      // entity2.setDirection(direction1);

      // Each ship takes damage proportional to the momentum of the other ship
      entity1.damage(speed2 * entity2.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER);
      entity2.damage(speed1 * entity1.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER);
    }
    // Bullet-Bullet collision - ignore

    // Ship-Bullet collision
    else if ((entity1 instanceof Ship) & (entity2 instanceof Bullet)) {
      resolveBulletShipCollision((Bullet) entity2, (Ship) entity1);
    }
    // Bullet-Ship collision
    else if ((entity1 instanceof Bullet) & (entity2 instanceof Ship)) {
      resolveBulletShipCollision((Bullet) entity1, (Ship) entity2);
    }
  }

  private void resolveBulletShipCollision(Bullet bullet, Ship ship) {
    if (bullet.getSource() != ship.getSerial()) {
      System.out.println("A bullet has hit a ship!");
      // Bullet damages ship
      ship.damage(bullet.getDamage());
      // See if ship has been destroyed
      if (ship.getHealth() <= 0) {
        ship.delete();
      }
      // Destroy bullet
      bullet.delete();
    }
  }
}
