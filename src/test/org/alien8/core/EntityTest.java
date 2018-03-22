package test.org.alien8.core;

import static org.junit.Assert.fail;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;
import net.jafama.FastMath;

public class EntityTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Test
  public void testInitObb() {
    Ship s = new Ship(new Position(50, 12.5), 0, 0x00FF00);
    Position[] obb =
        {new Position(0, 25), new Position(100, 25), new Position(100, 0), new Position(0, 0)};
    assert (s.obbEquals(obb));
  }

  @Test
  public void testTranslateObb() {
    Ship s = new Ship(new Position(50, 12.5), 0, 0x00FF00);
    s.translateObb(100, 50);
    Position[] obb = {new Position(100, 75), new Position(200, 75), new Position(200, 50),
        new Position(100, 50)};
    assert (s.obbEquals(obb));
  }

  @Test
  public void testRotateObb() {
    Ship s = new Ship(new Position(200, 200), 0, 0x00FF00);
    s.rotateObb(FastMath.PI / 2);
    for (int i = 0; i < 4; i++) {
      System.out.println(s.getObb()[i]);
    }
    Position[] obb = {new Position(187.5, 150), new Position(187.5, 250), new Position(212.5, 250),
        new Position(212.5, 150)};
    assert (s.obbEquals(obb));
  }

  @Test
  public void testEqualsEntity() {
    Ship s1 = new Ship(new Position(67, 67), 0, 0xFF4455);
    Ship s2 = new Ship(new Position(67, 67), 0, 0xFF4455);
    assert (s1.equals(s2));
  }

  @Test
  public void testObbEquals() {
    Ship s = new Ship(new Position(200, 200), 0, 0x00FF00);
    Position[] obb = {new Position(150, 212.5), new Position(250, 212.5), new Position(250, 187.5),
        new Position(150, 187.5)};
    assert (s.obbEquals(obb));
  }

  @Test
  public void testIsPlayer() {
    fail("Not yet implemented");
  }

  @Test
  public void testSave() {
    fail("Not yet implemented");
  }

  @Test
  public void testDelete() {
    // Test initial state
    Entity e = new Ship(new Position(0, 0), 0, 0xFF00FF);
    assert (!e.isToBeDeleted());

    // Test deletion
    e.delete();
    assert (e.isToBeDeleted());
  }

  @Test
  public void testClone() {
    fail("Not yet implemented");
  }

  @Test
  public void testDamage() {
    // Damage with zero amount
    Ship s = new Ship(new Position(0, 0), 0, 0xFF4412);
    s.damage(0);
    double health = Parameters.SHIP_HEALTH;
    assert (s.getHealth() == health);

    // Damage with positive amount
    s.damage(50);
    health = 50;
    assert (s.getHealth() == health);

    // Damage with negative amount
    s.damage(-200);
    health = 250;
    assert (s.getHealth() == health);
  }

  @Test
  public void testIsOutOfBounds() {
    // Outside of left edge
    Ship s = new Ship(new Position(-100, 0), 0, 0x123456);
    assert (s.isOutOfBounds());

    // Outside of right edge
    s.setPosition(new Position(Parameters.MAP_WIDTH + 100, 0));
    assert (s.isOutOfBounds());

    // Outside of top edge
    s.setPosition(new Position(0, -100));
    assert (s.isOutOfBounds());

    // Outside of bottom edge
    s.setPosition(new Position(Parameters.MAP_HEIGHT + 100, 0));
    assert (s.isOutOfBounds());

    // Inside map
    s.setPosition(new Position(200, 200));
    assert (!s.isOutOfBounds());
  }

}
