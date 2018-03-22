package org.alien8.physics;

import org.alien8.audio.AudioEvent;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.drops.Mine;
import org.alien8.drops.Pickup;
import org.alien8.drops.Torpedo;
import org.alien8.score.ServerScoreBoard;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;

/**
 * This class represents a collision between two Entities in the game. It contains a Minimum
 * Translation Vector (MTV), which may be needed to 'push apart' two intersecting Entities, if this
 * is required.
 *
 */
public class Collision {
  private Entity entity1;
  private Entity entity2;
  private MTV mtv;

  /**
   * Constructor.
   * 
   * @param entity1 the first Entity involved in the Collision
   * @param entity2 the second Entity involved in the Collision
   */
  public Collision(Entity entity1, Entity entity2) {
    this.entity1 = entity1;
    this.entity2 = entity2;
  }

  /**
   * Constructor.
   * 
   * @param entity1 the first Entity involved in the Collision
   * @param entity2 the second Entity involved in the Collision
   * @param mtv the Minimum Translation Vector (MTV)
   */
  public Collision(Entity entity1, Entity entity2, MTV mtv) {
    this.entity1 = entity1;
    this.entity2 = entity2;
    this.mtv = mtv;
  }

  /**
   * Returns the first Entity involved in the Collision.
   * 
   * @return the first Entity involved in the Collision
   */
  public Entity getEntity1() {
    return entity1;
  }

  /**
   * Returns the second Entity involved in the Collision.
   * 
   * @return the second Entity involved in the Collision
   */
  public Entity getEntity2() {
    return entity2;
  }

  /**
   * Checks for equality between this Collision and another.
   * 
   * @param c the other Collision
   * @return {@code true} if the Collisions are equal, {@code false} if not
   */
  public boolean equals(Collision c) {
    if ((entity1.equals(c.getEntity1()) && entity2.equals(c.getEntity2()))
        || (entity1.equals(c.getEntity2()) && entity2.equals(c.getEntity1()))) {
      return true;
    } else {
      return false;
    }
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
    // Collision between a Ship and a Pickup
    else if ((entity1 instanceof Ship) & (entity2 instanceof Pickup)) {
      resolveShipPickupCollision((Ship) entity1, (Pickup) entity2);
    } else if ((entity1 instanceof Pickup) & (entity2 instanceof Ship)) {
      resolveShipPickupCollision((Ship) entity2, (Pickup) entity1);
    }
    // Collision between a Ship and a Mine
    else if ((entity1 instanceof Ship) & (entity2 instanceof Mine)) {
      resolveShipMineCollision((Ship) entity1, (Mine) entity2);
    } else if ((entity1 instanceof Mine) & (entity2 instanceof Ship)) {
      resolveShipMineCollision((Ship) entity2, (Mine) entity1);
    }
    // Collision between a Ship and a Torpedo
    else if ((entity1 instanceof Ship) & (entity2 instanceof Torpedo)) {
      resolveShipTorpedoCollision((Ship) entity1, (Torpedo) entity2);
    } else if ((entity1 instanceof Torpedo) & (entity2 instanceof Ship)) {
      resolveShipTorpedoCollision((Ship) entity2, (Torpedo) entity1);
    }
  }

  /**
   * Resolves a Collision between a Bullet and a Ship.
   * 
   * @param bullet the Bullet involved in the Collision
   * @param ship the Ship involved in the Collision
   */
  private void resolveBulletShipCollision(Bullet bullet, Ship ship) {
    // This allows us to ignore cases where a ship shoots itself
    if (bullet.getSource() == ship.getSerial())
      return;
    
    // Send audio event for bullet hit
    Server.getInstance().addEvent(new AudioEvent(AudioEvent.Type.HIT, ship.getPosition()));
    
    System.out.println("B: " + bullet.getSource() + ", S: " + ship.getSerial());
    // Bullet damages ship
    ship.damage(bullet.getDamage());
    // Award score to the bullet owner
    Player shooter = Server.getInstance().getPlayer(bullet.getSource());
    // If it's AI, no points
    if (shooter != null)
      ServerScoreBoard.getInstance().giveHit(shooter, bullet);
    // See if ship has been destroyed
    if (new Double(ship.getHealth()).intValue() <= 0) {
      System.out.println("A ship died!");
      ServerScoreBoard.getInstance().kill(ship);
      ship.kill();
      // Award score to the killer
      // If it's AI, no points
      if (shooter != null)
        ServerScoreBoard.getInstance().giveKill(shooter);
    }
    // Destroy bullet
    bullet.delete();
  }

  /**
   * Resolves a Collision between two Ships.
   * 
   * @param ship1 the first Ship involved in the Collision
   * @param ship2 the second Ship involved in the Collision
   */
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
    entity2.setPosition(new Position(pos2.getX() + xdiff2, pos2.getY() + ydiff2));
    entity2.translateObb(xdiff2, ydiff2);

    // Adjust their speeds and directions
    entity1.setSpeed(speed2 * Parameters.RESTITUTION_COEFFICIENT);
    PhysicsManager.rotateEntity(entity1,
        ((direction1 - direction2) % 5) * Parameters.COLLISION_ROTATION_MODIFIER);
    entity2.setSpeed(speed1 * Parameters.RESTITUTION_COEFFICIENT);
    PhysicsManager.rotateEntity(entity2,
        ((direction2 - direction1) % 5) * Parameters.COLLISION_ROTATION_MODIFIER);

    // Each ship takes damage proportional to the momentum of the other ship
    entity1.damage(speed2 * entity2.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER);
    entity2.damage(speed1 * entity1.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER);
    // Delete ships if dead
    if (new Double(entity1.getHealth()).intValue() <= 0) {
      System.out.println("A ship died!");
      ServerScoreBoard.getInstance().kill(ship1);
      ship1.kill();
    }
    if (new Double(entity2.getHealth()).intValue() <= 0) {
      System.out.println("A ship died!");
      ServerScoreBoard.getInstance().kill(ship2);
      ship2.kill();
    }
  }

  /**
   * Resolves a Collision between a Ship and a Pickup.
   * 
   * @param ship the Ship involved in the Collision
   * @param pickup the Pickup involved in the Collision
   */
  private void resolveShipPickupCollision(Ship ship, Pickup pickup) {
	// Give item if it doesn't have one already 
	if (!ship.hasItem()) {
      pickup.onPickup(ship);
    }
	
	// Send pickup audio event
    Server.getInstance().addEvent(new AudioEvent(AudioEvent.Type.PICKUP, ship.getPosition()));
    pickup.delete();
  }

  /**
   * Resolves a Collision between a Ship and a Mine.
   * 
   * @param ship the Ship involved in the Collision
   * @param mine the Mine involved in the Collision
   */
  private void resolveShipMineCollision(Ship ship, Mine mine) {
    if (ship.getSerial() == mine.getSource())
      return;

    // Send audio event for mine explosion
    Server.getInstance().addEvent(new AudioEvent(AudioEvent.Type.MINE_EXPLODE, ship.getPosition()));
    
    // Mine damages ship
    ship.damage(Parameters.MINE_DAMAGE);
    // Award score to the mine owner
    Player deployer = Server.getInstance().getPlayer(mine.getSource());
    // If it's AI, no points
    if (deployer != null)
      ServerScoreBoard.getInstance().giveScore(deployer, Parameters.MINE_SCORE);
    // See if ship has been destroyed
    if (new Double(ship.getHealth()).intValue() <= 0) {
      System.out.println("A ship died!");
      ServerScoreBoard.getInstance().kill(ship);
      ship.kill();
      // Award score to the killer
      // If it's AI, no points
      if (deployer != null)
        ServerScoreBoard.getInstance().giveKill(deployer);
    }
    // Destroy mine
    mine.delete();

  }

  /**
   * Resolves a Collision between a Ship and a Torpedo.
   * 
   * @param ship the Ship involved in the Collision
   * @param torpedo the Torpedo involved in the Collision
   */
  private void resolveShipTorpedoCollision(Ship ship, Torpedo torpedo) {
    if (ship.getSerial() == torpedo.getSource())
      return;
    
    // Send audio event for torpedo hit
    Server.getInstance().addEvent(new AudioEvent(AudioEvent.Type.HIT, ship.getPosition()));
    
    // Torpedo damages ship
    ship.damage(Parameters.TORPEDO_DAMAGE);
    // Award score to the mine owner
    Player deployer = Server.getInstance().getPlayer(torpedo.getSource());
    // If it's AI, no points
    if (deployer != null)
      ServerScoreBoard.getInstance().giveScore(deployer, Parameters.TORPEDO_SCORE);
    // See if ship has been destroyed
    if (new Double(ship.getHealth()).intValue() <= 0) {
      System.out.println("A ship died!");
      ServerScoreBoard.getInstance().kill(ship);
      ship.kill();
      // Award score to the killer
      // If it's AI, no points
      if (deployer != null)
        ServerScoreBoard.getInstance().giveKill(deployer);
    }
    // Destroy torpedo
    torpedo.delete();
  }
}
