package org.alien8.physics;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;

public class Collision {
  private Entity entity1;
  private Entity entity2;

  public Collision(Entity entity1, Entity entity2) {
    this.entity1 = entity1;
    this.entity2 = entity2;
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
      System.out.println("Two ships have collided!");
      double speed1 = entity1.getSpeed();
      double speed2 = entity2.getSpeed();
      double direction1 = entity1.getDirection();
      double direction2 = entity2.getDirection();

      // Assume for the moment that ships have the same mass
      // This means that we can just swap their speed and direction
      // We multiply speed by the coefficient of restitution to decrease it
      entity1.setSpeed(speed2 * Parameters.RESTITUTION_COEFFICIENT);
      entity1.setDirection(direction2);
      entity2.setSpeed(speed1 * Parameters.RESTITUTION_COEFFICIENT);
      entity2.setDirection(direction1);

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
    System.out.println("A bullet has hit a ship!");

    // Bullet damages ship
    ship.damage(bullet.getDamage());
    // See if ship has been destroyed
    if (ship.getHealth() <= 0) {
      // ship.destroy();
    }
    // Destroy bullet
    // bullet.destroy();

  }
}
