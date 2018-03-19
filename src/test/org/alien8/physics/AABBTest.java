package test.org.alien8.physics;

import org.alien8.core.Entity;
import org.alien8.physics.AABB;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;

public class AABBTest {

  private static AABB aabb;

  @BeforeClass
  public static void setUp() {
    Position min = new Position(3, 4);
    Position max = new Position(12, 20);
    Entity ent = new Ship(new Position(5, 5), 1, 0xFF00FF);
    aabb = new AABB(min, max, ent);
  }

  @Test
  public void testGetMin() {
    Position pos = new Position(3, 4);
    // assertEquals(aabb.getMin().getX(), pos.getX(), Parameters.DOUBLE_PRECISION);
    // assertEquals(aabb.getMin().getY(), pos.getY(), Parameters.DOUBLE_PRECISION);
    assert (aabb.getMin().equals(pos));
  }

  @Test
  public void testGetMax() {
    Position pos = new Position(12, 20);
    // assertEquals(aabb.getMax().getX(), pos.getX(), Parameters.DOUBLE_PRECISION);
    // assertEquals(aabb.getMax().getY(), pos.getY(), Parameters.DOUBLE_PRECISION);
    assert (aabb.getMax().equals(pos));
  }

  @Test
  public void testGetEntity() {
    Entity ent = new Ship(new Position(5, 5), 1, 0xFF00FF);
    assert (aabb.getEntity().equals(ent));

    // assertEquals(aabb.getEntity().getClass(), ent.getClass());
    // assertEquals(aabb.getEntity().getDirection(), ent.getDirection(),
    // Parameters.DOUBLE_PRECISION);
    // assertEquals(aabb.getEntity().getHealth(), ent.getHealth(), Parameters.DOUBLE_PRECISION);
    // assertEquals(aabb.getEntity().getLength(), ent.getLength(), Parameters.DOUBLE_PRECISION);
    // assertEquals(aabb.getEntity().getMass(), ent.getMass(), Parameters.DOUBLE_PRECISION);
    // // assertEquals(aabb.getEntity().getObb(), ent.getObb());
    // assert (aabb.getEntity().getPosition().equals(ent.getPosition()));
    // assertEquals(aabb.getEntity().getSerial(), ent.getSerial());
    // assertEquals(aabb.getEntity().getSpeed(), ent.getSpeed(), Parameters.DOUBLE_PRECISION);
    // assertEquals(aabb.getEntity().getWidth(), ent.getWidth(), Parameters.DOUBLE_PRECISION);
  }

}
