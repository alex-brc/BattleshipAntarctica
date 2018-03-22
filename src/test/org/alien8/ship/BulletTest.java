package test.org.alien8.ship;

import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.ship.Bullet;
import org.junit.BeforeClass;
import org.junit.Test;

public class BulletTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Test
  public void testDealWithOutOfBounds() {
    // Outside of left edge
    Bullet b = new Bullet(new Position(-100, 0), 0, 100, 1);
    b.dealWithOutOfBounds();
    assert (b.isToBeDeleted());

    // Outside of right edge
    b = new Bullet(new Position(Parameters.MAP_WIDTH + 100, 0), 0, 100, 1);
    b.dealWithOutOfBounds();
    assert (b.isToBeDeleted());

    // Outside of top edge
    b = new Bullet(new Position(0, -100), 0, 100, 1);
    b.dealWithOutOfBounds();
    assert (b.isToBeDeleted());

    // Outside of bottom edge
    b = new Bullet(new Position(0, Parameters.MAP_HEIGHT + 100), 0, 100, 1);
    b.dealWithOutOfBounds();
    assert (b.isToBeDeleted());

    // Inside map
    b = new Bullet(new Position(200, 200), 0, 100, 1);
    b.dealWithOutOfBounds();
    assert (!b.isToBeDeleted());
  }

  @Test
  public void testDealWithInIce() {
    // Create an ice grid to use for testing
    boolean[][] iceGrid = new boolean[64][64];
    for (int i = 16; i < 48; i++) {
      for (int j = 16; j < 48; j++) {
        iceGrid[i][j] = true;
      }
    }

    // Test being in ice
    Bullet b = new Bullet(new Position(32, 32), 0, 0, 0);
    b.dealWithInIce(iceGrid);
    assert (b.isToBeDeleted());

    // Test being on the edge of the ice
    b = new Bullet(new Position(16, 16), 0, 0, 0);
    b.dealWithInIce(iceGrid);
    assert (b.isToBeDeleted());

    // Test being on the edge of the water
    b = new Bullet(new Position(15, 15), 0, 0, 0);
    b.dealWithInIce(iceGrid);
    assert (!b.isToBeDeleted());

    // Test not being in ice
    b = new Bullet(new Position(0, 0), 0, 0, 0);
    b.dealWithInIce(iceGrid);
    assert (!b.isToBeDeleted());

  }

  @Test
  public void testEqualsBullet() {
    Bullet b1 = new Bullet(new Position(200, 200), 0, 100, 1);
    Bullet b2 = new Bullet(new Position(200, 200), 0, 100, 1);
    assert (b1.equals(b2));
    assert (b2.equals(b1));
  }

  @Test
  public void testToString() {
    Bullet b = new Bullet(new Position(100, 100), 0, 50, 1);
    String value = "Bullet: 0.0/50.0, -1, X: 100.0 Y: 100.0";
    assert (b.toString().equals(value));
  }
}
