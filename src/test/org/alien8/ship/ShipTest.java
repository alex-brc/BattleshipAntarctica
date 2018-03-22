package test.org.alien8.ship;

import static org.junit.Assert.fail;
import org.alien8.core.Parameters;
import org.alien8.drops.Effect;
import org.alien8.drops.HealthItem;
import org.alien8.drops.InvulnerableItem;
import org.alien8.drops.Item;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;
import net.jafama.FastMath;

public class ShipTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Test
  public void testRender() {
    fail("Not yet implemented");
  }

  @Test
  public void testDealWithOutOfBounds() {
    // Test inside the map
    Ship s = new Ship(new Position(200, 200), 0, 0xFF00FF);
    s.dealWithOutOfBounds();
    assert (!s.isOutOfBounds());
    assert (s.getPosition().equals(new Position(200, 200)));

    // Test all corners outside the map
    s = new Ship(new Position(-1000, -1000), 0, 0xFF00FF);
    assert (s.isOutOfBounds());
    s.dealWithOutOfBounds();
    assert (!s.isOutOfBounds());
    assert (s.getPosition().equals(new Position(50, 12.5)));

    // Test one corner outside the map
    s = new Ship(new Position(10, 100), FastMath.PI / 4, 0xFF00FF);
    // We don't bother testing isOutOfBounds beforehand, as that uses the centre of the Ship so the
    // Ship is not out of bounds according to that method
    s.dealWithOutOfBounds();
    assert (!s.isOutOfBounds());
    assert (s.getPosition().equals(new Position(44.19417382415922, 100)));

    // Test off the left edge of the map
    s = new Ship(new Position(0, 100), 0, 0xFF00FF);
    s.dealWithOutOfBounds();
    assert (s.getPosition().equals(new Position(50, 100)));

    // Test off the right edge of the map
    s = new Ship(new Position(Parameters.MAP_WIDTH, 100), 0, 0xFF00FF);
    s.dealWithOutOfBounds();
    assert (s.getPosition().equals(new Position(Parameters.MAP_WIDTH - 50, 100)));

    // Test off the top edge of the map
    s = new Ship(new Position(100, -100), 0, 0xFF00FF);
    s.dealWithOutOfBounds();
    assert (s.getPosition().equals(new Position(100, 12.5)));

    // Test off the bottom edge of the map
    s = new Ship(new Position(100, Parameters.MAP_HEIGHT), 0, 0xFF00FF);
    s.dealWithOutOfBounds();
    assert (s.getPosition().equals(new Position(100, Parameters.MAP_HEIGHT - 12.5)));

  }

  @Test
  public void testDealWithInIce() {
    // Create an ice grid to use for testing
    boolean[][] iceGrid = new boolean[1000][1000];
    for (int i = 500; i < 600; i++) {
      for (int j = 500; j < 600; j++) {
        iceGrid[i][j] = true;
      }
    }

    // Test being in ice
    Ship s = new Ship(new Position(500, 500), 0, 0xFF00FF);
    s.dealWithInIce(iceGrid);
    assert (s.getPosition().equals(new Position(500, 487)));

    // Test being on the edge of the ice
    s = new Ship(new Position(450, 550), 0, 0xFF00FF);
    s.dealWithInIce(iceGrid);
    assert (s.getPosition().equals(new Position(449, 550)));

    // Test being on the edge of the water
    s = new Ship(new Position(449, 550), 0, 0xFF00FF);
    s.dealWithInIce(iceGrid);
    assert (s.getPosition().equals(new Position(449, 550)));

    // Test not being in ice
    s = new Ship(new Position(100, 100), 0, 0xFF00FF);
    s.dealWithInIce(iceGrid);
    assert (s.getPosition().equals(new Position(100, 100)));

    // Test being outside of the map
    // Position remains unchanged
    s = new Ship(new Position(-999, -999), 0, 0xFF00FF);
    s.dealWithInIce(iceGrid);
    assert (s.getPosition().equals(new Position(-999, -999)));
  }

  @Test
  public void testFrontTurretCharge() {
    fail("Not yet implemented");
  }

  @Test
  public void testFrontTurretShoot() {
    fail("Not yet implemented");
  }

  @Test
  public void testRearTurretCharge() {
    fail("Not yet implemented");
  }

  @Test
  public void testRearTurretShoot() {
    fail("Not yet implemented");
  }

  @Test
  public void testGiveItem() {
    // Test giving Ship an Item when it doesn't have one
    Ship s = new Ship(new Position(100, 100), 0, 0xFF00FF);
    assert (!s.hasItem());
    Item item = new HealthItem();
    s.giveItem(item);
    assert (s.hasItem());
    assert (s.getItemType() == 0);
    assert (s.getItem() == item);

    // Test giving Ship an Item when it already has one
    s.giveItem(new InvulnerableItem());
    assert (s.hasItem());
    assert (s.getItemType() == 0);
  }

  @Test
  public void testUseItem() {
    // Test using an Item when Ship has one
    Ship s = new Ship(new Position(0, 0), 0, 0xFF00FF);
    s.giveItem(new HealthItem());
    s.useItem();
    assert (!s.hasItem());

    // Test using an Item when Ship doesn't have one
    s.useItem();
    assert (!s.hasItem());
  }

  @Test
  public void testUnderEffect() {
    // Test default (no Effect)
    Ship s = new Ship(new Position(100, 100), 0, 0xFF00FF);
    assert (!s.underEffect());

    // Test adding Effect
    s.applyEffect(new Effect(0, 0));
    assert (s.underEffect());

    // Test Effect expiring
    s.updateEffect();
    assert (!s.underEffect());
  }

  @Test
  public void testApplyEffect() {
    // Test default (no Effect)
    Ship s = new Ship(new Position(100, 100), 0, 0xFF00FF);
    assert (!s.underEffect());

    // Test adding Effect
    s.applyEffect(new Effect(0, 0));
    assert (s.underEffect());

    // Test Effect expiring
    s.updateEffect();
    assert (!s.underEffect());
  }

  @Test
  public void testUpdateEffect() {
    // Test default (no Effect)
    Ship s = new Ship(new Position(100, 100), 0, 0xFF00FF);
    s.applyEffect(new Effect(0, 0));
    assert (s.underEffect());

    // Test updating short Effect
    s.updateEffect();
    assert (!s.underEffect());

    // Test applying longer Effect
    s.applyEffect(new Effect(20, 0));
    assert (s.underEffect());
    try {
      Thread.sleep(10);
      s.updateEffect();
      System.out.println(s.underEffect());
      assert (s.underEffect());
      Thread.sleep(10);
      s.updateEffect();
      assert (!s.underEffect());
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testEqualsShip() {
    // Test equality
    Ship s1 = new Ship(new Position(100, 100), 0, 0xFF00FF);
    Ship s2 = new Ship(new Position(100, 100), 0, 0xFF00FF);
    assert (s1.equals(s2));
    assert (s2.equals(s1));

    // Test inequality
    Ship s3 = new Ship(new Position(2000, 2000), 340, 0xFF00FF);
    assert (!s1.equals(s3));
    assert (!s3.equals(s1));
  }

  @Test
  public void testToString() {
    Ship s = new Ship(new Position(100, 100), 0, 0xFF00FF);
    s.setSerial(20);
    String value = "Ship 20,X: 100.0 Y: 100.0";
    assert (s.toString().equals(value));
  }

}
