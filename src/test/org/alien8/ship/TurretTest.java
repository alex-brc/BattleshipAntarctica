package test.org.alien8.ship;

import static org.junit.Assert.fail;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.ship.Turret;
import org.junit.BeforeClass;
import org.junit.Test;

public class TurretTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Test
  public void testIsOnCooldown() {
    // Turret t = new Turret(new Position(0, 0), 1);
    // t.
    fail("Not yet implemented");
  }

  @Test
  public void testResetCooldown() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetMaxDistance() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetTargetPosition() {
    fail("Not yet implemented");
  }

  @Test
  public void testCharge() {
    // Test charging up
    Turret t = new Turret(new Position(0, 0), 1);
    t.setDistance(0);
    t.charge();
    assert (t.getDistance() == Parameters.CHARGE_INCREMENT);

    // Test overcharging (Turret should shoot at this point)
    t.setDistance(Parameters.TURRET_MAX_DIST);
    System.out.println(t.getDistance());
    t.charge();
    t.charge();
    System.out.println(t.getDistance());
    System.out.println(t.isOnCooldown());
    assert (t.getDistance() == 0);
  }

  @Test
  public void testShoot() {
    fail("Not yet implemented");
  }

}
