package test.org.alien8.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntityTest {

  public static Entity e1;
  public static Entity e2;
  public static Entity e3;
  public static Entity eSet;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    e1 = new Ship(new Position(3, 4), 1, 0xFF00FF);
    e2 = new Ship(new Position(3, 4), 1, 0xFF00FF);
    e3 = new Ship(new Position(5, 5), 3, 0x123456);
    eSet = new Ship(new Position(3, 4), 1, 0xFF00FF);
  }

  @Test
  public void testGetPosition() {
    assert (e1.getPosition().equals(new Position(3, 4)));
  }

  @Test
  public void testSetPosition() {
    eSet.setPosition(new Position(10, 10));
    assert (eSet.getPosition().equals(new Position(10, 10)));
  }

  @Test
  public void testGetSerial() {
    assertEquals(e1.getSerial(), -1);
  }

  @Test
  public void testSetSerial() {
    eSet.setSerial(200);
    assertEquals(eSet.getSerial(), 200);
  }

  @Test
  public void testIsToBeDeleted() {
    assertEquals(e1.isToBeDeleted(), false);
  }

  @Test
  public void testGetMass() {
    assertEquals(e1.getMass(), Parameters.SHIP_MASS, Parameters.DOUBLE_PRECISION);
  }

  @Test
  public void testSetMass() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetSpeed() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetSpeed() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetDirection() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetDirection() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetLength() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetLength() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetWidth() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetWidth() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetHealth() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetHealth() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetObb() {
    fail("Not yet implemented");
  }

  @Test
  public void testInitObb() {
    fail("Not yet implemented");
  }

  @Test
  public void testTranslateObb() {
    fail("Not yet implemented");
  }

  @Test
  public void testRotateObb() {
    fail("Not yet implemented");
  }

  @Test
  public void testEqualsEntity() {
    fail("Not yet implemented");
  }

  @Test
  public void testObbEquals() {
    fail("Not yet implemented");
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
    fail("Not yet implemented");
  }

  @Test
  public void testClone() {
    fail("Not yet implemented");
  }

  @Test
  public void testRender() {
    fail("Not yet implemented");
  }

  @Test
  public void testDamage() {
    fail("Not yet implemented");
  }

  @Test
  public void testIsOutOfBounds() {
    fail("Not yet implemented");
  }

  @Test
  public void testDealWithOutOfBounds() {
    fail("Not yet implemented");
  }

  @Test
  public void testDealWithInIce() {
    fail("Not yet implemented");
  }

}
