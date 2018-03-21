package test.org.alien8.physics;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.drops.HealthPickup;
import org.alien8.drops.Mine;
import org.alien8.drops.Torpedo;
import org.alien8.physics.AxisVector;
import org.alien8.physics.Collision;
import org.alien8.physics.MTV;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;
import net.jafama.FastMath;

public class CollisionTest {

  public static Collision cSS;
  public static Collision colMtv;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

  }

  @Test
  public void testShipShipCollision() {
    // Test Collision between two Ships
    Entity s1 = new Ship(new Position(50, 12.5), 0, 0xFF00FF);
    double initialHealth1 = s1.getHealth();
    double speed1 = s1.getSpeed();
    double dir1 = s1.getDirection();
    Entity s2 = new Ship(new Position(12.5, 70), 3 * FastMath.PI / 2, 0xFF00FF);
    double initialHealth2 = s2.getHealth();
    s2.setSpeed(50);
    double speed2 = s2.getSpeed();
    double dir2 = s2.getDirection();

    MTV mtv = new MTV(9, new AxisVector(5, 5));
    Collision c = new Collision(s1, s2, mtv);
    c.resolveCollision();
    double damage1 = speed2 * s2.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER;
    double damage2 = speed1 * s1.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER;
    // Assert ship damage
    assert (s1.getHealth() == initialHealth1 - damage1);
    assert (s2.getHealth() == initialHealth2 - damage2);
    // Assert ship positions
    assert (s1.getPosition().equals(new Position(55, 7.5)));
    assert (s2.getPosition().equals(new Position(7.5, 75)));
    // Assert ship speeds
    assert (s1.getSpeed() == 25);
    assert (s2.getSpeed() == 0);
    // Assert ship directions
    Ship test1 = new Ship(s1.getPosition(), dir1, 0xFF00FF);
    PhysicsManager.rotateEntity(test1,
        ((dir1 - dir2) % 5) * Parameters.COLLISION_ROTATION_MODIFIER);
    Ship test2 = new Ship(s2.getPosition(), dir2, 0xFF00FF);
    PhysicsManager.rotateEntity(test2,
        ((dir2 - dir1) % 5) * Parameters.COLLISION_ROTATION_MODIFIER);
    assert (s1.getDirection() == test1.getDirection());
    assert (s2.getDirection() == test2.getDirection());
  }

  @Test
  public void testShipShipCollisionOneDies() {
    // Test Collision between two Ships where the second one dies
    Entity s1 = new Ship(new Position(50, 12.5), 0, 0xFF00FF);
    double initialHealth1 = s1.getHealth();
    s1.setSpeed(100);
    double speed1 = s1.getSpeed();
    Entity s2 = new Ship(new Position(12.5, 70), 3 * FastMath.PI / 2, 0xFF00FF);
    ((Ship) s2).setHealth(1);
    double initialHealth2 = s2.getHealth();
    double speed2 = s2.getSpeed();

    MTV mtv = new MTV(9, new AxisVector(5, 5));
    Collision c = new Collision(s1, s2, mtv);
    c.resolveCollision();
    double damage1 = speed2 * s2.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER;
    double damage2 = speed1 * s1.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER;
    // Assert ship damage
    assert (s1.getHealth() == initialHealth1 - damage1);
    assert (s2.getHealth() == initialHealth2 - damage2);
    assert (s2.getHealth() <= 0);
    assert (s2.isToBeDeleted());
  }

  @Test
  public void testShipShipCollisionBothDie() {
    // Test Collision between two Ships where both die
    Entity s1 = new Ship(new Position(50, 12.5), 0, 0xFF00FF);
    ((Ship) s1).setHealth(1);
    double initialHealth1 = s1.getHealth();
    s1.setSpeed(100);
    double speed1 = s1.getSpeed();
    Entity s2 = new Ship(new Position(12.5, 70), 3 * FastMath.PI / 2, 0xFF00FF);
    ((Ship) s2).setHealth(1);
    double initialHealth2 = s2.getHealth();
    s2.setSpeed(100);
    double speed2 = s2.getSpeed();

    MTV mtv = new MTV(9, new AxisVector(5, 5));
    Collision c = new Collision(s1, s2, mtv);
    c.resolveCollision();
    double damage1 = speed2 * s2.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER;
    double damage2 = speed1 * s1.getMass() * Parameters.COLLISION_DAMAGE_MODIFIER;
    // Assert ship damage
    assert (s1.getHealth() == initialHealth1 - damage1);
    assert (s1.getHealth() <= 0);
    assert (s1.isToBeDeleted());
    assert (s2.getHealth() == initialHealth2 - damage2);
    assert (s2.getHealth() <= 0);
    assert (s2.isToBeDeleted());
  }

  @Test
  public void testShipOwnBulletCollision() {
    // Test Collision between a Ship and its own Bullet
    Entity s = new Ship(new Position(0, 0), 0, 0);
    s.setSerial(1);
    double initialHealth = s.getHealth();
    Entity b = new Bullet(new Position(0, 0), 0, 0, 1);
    Collision c = new Collision(s, b);
    c.resolveCollision();
    assert (s.getHealth() == initialHealth);
    assert (b.isToBeDeleted() == false);
  }

  @Test
  public void testShipEnemyBulletCollision() {
    // Test Collision between a Ship and an enemy Bullet
    Entity s = new Ship(new Position(0, 0), 0, 0);
    s.setSerial(1);
    double initialHealth = s.getHealth();
    Entity b = new Bullet(new Position(0, 0), 0, 0, 2);
    double bulletDamage = ((Bullet) b).getDamage();
    Collision c = new Collision(s, b);
    c.resolveCollision();
    assert (s.getHealth() == initialHealth - bulletDamage);
    assert (b.isToBeDeleted() == true);
  }

  @Test
  public void testShipPickupCollision() {
    // Test Collision between a Ship and a Pickup
    Entity s = new Ship(new Position(0, 0), 0, 0);
    Entity p = new HealthPickup(new Position(0, 0));
    Collision c = new Collision(s, p);
    c.resolveCollision();
    assert (((Ship) s).hasItem());
    assert (((Ship) s).getItemType() == 0);
    assert (p.isToBeDeleted());
  }

  @Test
  public void testShipOwnMineCollision() {
    // Test Collision between a Ship its own Mine
    Entity s = new Ship(new Position(0, 0), 0, 0);
    s.setSerial(1);
    double initialHealth = s.getHealth();
    Entity m = new Mine(new Position(0, 0), 1);
    Collision c = new Collision(s, m);
    c.resolveCollision();
    assert (s.getHealth() == initialHealth);
    assert (m.isToBeDeleted() == false);
  }

  @Test
  public void testShipEnemyMineCollision() {
    // Test Collision between a Ship and an enemy Mine
    Entity s = new Ship(new Position(0, 0), 0, 0);
    s.setSerial(1);
    double initialHealth = s.getHealth();
    Entity m = new Mine(new Position(0, 0), 2);
    double mineDamage = Parameters.MINE_DAMAGE;
    Collision c = new Collision(s, m);
    c.resolveCollision();
    assert (s.getHealth() == initialHealth - mineDamage);
    assert (m.isToBeDeleted() == true);
  }

  @Test
  public void testShipOwnTorpedoCollision() {
    // Test Collision between a Ship its own Torpedo
    Entity s = new Ship(new Position(0, 0), 0, 0);
    s.setSerial(1);
    double initialHealth = s.getHealth();
    Entity m = new Torpedo(new Position(0, 0), 1, 0);
    Collision c = new Collision(s, m);
    c.resolveCollision();
    assert (s.getHealth() == initialHealth);
    assert (m.isToBeDeleted() == false);
  }

  @Test
  public void testShipEnemyTorpedoCollision() {
    // Test Collision between a Ship and an enemy Torpedo
    Entity s = new Ship(new Position(0, 0), 0, 0);
    s.setSerial(1);
    double initialHealth = s.getHealth();
    Entity m = new Torpedo(new Position(0, 0), 2, 0);
    double damage = Parameters.TORPEDO_DAMAGE;
    Collision c = new Collision(s, m);
    c.resolveCollision();
    assert (s.getHealth() == initialHealth - damage);
    assert (m.isToBeDeleted() == true);
  }

}
