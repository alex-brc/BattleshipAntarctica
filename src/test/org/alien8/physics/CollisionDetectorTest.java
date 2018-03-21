package test.org.alien8.physics;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.core.Entity;
import org.alien8.physics.Collision;
import org.alien8.physics.CollisionDetector;
import org.alien8.physics.Position;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;
import net.jafama.FastMath;

public class CollisionDetectorTest {

  public static ConcurrentLinkedQueue<Entity> entities;
  static CollisionDetector detector;
  static Entity s1;
  static Entity s2;
  static Entity s3;
  static Entity b1;
  static Entity b2;
  static Collision cs1s2;
  static Collision cs1s3;
  static Collision cs1b1;
  static Collision cs1b2;
  static Collision cs2b2;
  static Collision cs2s3;
  static long lastSerial;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    detector = new CollisionDetector();
    lastSerial = 0;
    // Ships
    s1 = new Ship(new Position(50, 12.5), 0, 0xFF00FF);
    s2 = new Ship(new Position(250, 12.5), 0, 0xFF00FF);
    s3 = new Ship(new Position(12.5, 70), 3 * FastMath.PI / 2, 0xFF00FF);
    // Bullets
    b1 = new Bullet(new Position(100, 25), 5 * FastMath.PI / 4, 100, s2.getSerial());
    b2 = new Bullet(new Position(200, 25), 3 * FastMath.PI / 4, 100, s2.getSerial());
    // Entity queue
    entities = new ConcurrentLinkedQueue<Entity>();
    addEntity(s1);
    addEntity(s2);
    addEntity(s3);
    addEntity(b1);
    addEntity(b2);
    System.out.println(entities);
    // Collisions
    cs1s2 = new Collision(s1, s2);
    cs1s3 = new Collision(s1, s3);
    cs1b1 = new Collision(s1, b1);
    cs1b2 = new Collision(s1, b2);
    cs2b2 = new Collision(s2, b2);
    cs2s3 = new Collision(s2, s3);
  }

  @Test
  public void testFindCollisions() {
    ArrayList<Collision> collisions = detector.findCollisions(entities);
    System.out.println(collisions);
    // assert (!s2.equals(s1));
    assert (collisionInList(cs1s3, collisions));
    assert (collisionInList(cs1b1, collisions));
    assert (collisionInList(cs2b2, collisions));
  }

  private static boolean addEntity(Entity entity) {
    // Give it a serial number
    lastSerial++;
    entity.setSerial(lastSerial);
    return entities.add(entity);
  }


  private boolean collisionInList(Collision col, ArrayList<Collision> collisions) {
    for (Collision c : collisions) {
      if (c.equals(col)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void testVerifyCollision() {
    /// Colliding objects
    // s1 and s3 collide
    assert (detector.verifyCollision(cs1s3) != null);
    // b1 and s1 collide
    assert (detector.verifyCollision(cs1b1) != null);

    /// Non-colliding objects
    // s1 and s2 don't collide
    assert (detector.verifyCollision(cs1s2) == null);
    // s2 and s3 don't collide
    assert (detector.verifyCollision(cs2s3) == null);
    // s1 and b2 don't collide
    assert (detector.verifyCollision(cs1b2) == null);
  }
}
