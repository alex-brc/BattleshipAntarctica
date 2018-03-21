package test.org.alien8.physics;

import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;
import net.jafama.FastMath;

public class PhysicsManagerTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Test
  public void testApplyForce() {
    // Check if speed increases
    Ship s = new Ship(new Position(0, 0), 0, 0xFF00FF);
    s.setSpeed(0.5);
    PhysicsManager.applyForce(s, 1000, 0);
    assert (s.getSpeed() == 1.5);
    // Apply a huge force and check that speed is capped
    PhysicsManager.applyForce(s, 9999999, 0);
    assert (s.getSpeed() == Parameters.SHIP_TOP_SPEED_FORWARD);
  }

  @Test
  public void testApplyFriction() {
    // Apply friction to Ship at speed
    Ship s = new Ship(new Position(50, 50), 0, 0xFF00FF);
    s.setSpeed(1);
    PhysicsManager.applyFriction(s);
    assert (s.getSpeed() == 1 * Parameters.FRICTION);

    // Apply friction to Ship at very low speed
    s.setSpeed(0.000001);
    PhysicsManager.applyFriction(s);
    assert (s.getSpeed() == 0);

    // Apply friction to Ship at borderline speed
    s.setSpeed(0.001);
    PhysicsManager.applyFriction(s);
    assert (s.getSpeed() == 0);

    // Apply friction to Ship at negative speed
    s.setSpeed(-1000);
    PhysicsManager.applyFriction(s);
    assert (s.getSpeed() == 0);
  }

  @Test
  public void testUpdatePosition() {
    // Test stationary
    Ship s = new Ship(new Position(50, 50), 0, 0xFF00FF);
    s.setSpeed(0);
    Position[] obb = s.getObb();
    PhysicsManager.updatePosition(s, new boolean[1][1]);
    assert (s.getPosition().equals(new Position(50, 50)));
    assert (s.obbEquals(obb));
    // Test moving
    s = new Ship(new Position(50, 50), 0, 0xFF00FF);
    s.setSpeed(20);
    obb = s.getObb();
    PhysicsManager.updatePosition(s, new boolean[1][1]);
    Ship test = new Ship(new Position(50, 50), 0, 0xFF00FF);
    test.initObb();
    test.translateObb(20, 0);
    assert (s.getPosition().equals(new Position(70, 50)));
    assert (s.obbEquals(test.getObb()));
  }

  @Test
  public void testRotateEntity() {
    // Clockwise rotate stationary Ship
    Ship s = new Ship(new Position(0, 0), 0, 0xFF00FF);
    PhysicsManager.rotateEntity(s, FastMath.PI / 2);
    double angle = FastMath.PI / 2 * 0.5 * Parameters.ROTATION_MODIFIER;
    Ship test = new Ship(new Position(0, 0), 0, 0xFF00FF);
    test.initObb();
    test.rotateObb(angle);
    assert (s.getDirection() == angle);
    assert (s.obbEquals(test.getObb()));

    // Anticlockwise rotate stationary Ship
    s = new Ship(new Position(0, 0), 0, 0xFF00FF);
    PhysicsManager.rotateEntity(s, -FastMath.PI / 2);
    angle = -FastMath.PI / 2 * 0.5 * Parameters.ROTATION_MODIFIER;
    angle = PhysicsManager.shiftAngle(angle);
    test = new Ship(new Position(0, 0), 0, 0xFF00FF);
    test.initObb();
    test.rotateObb(angle);
    assert (s.getDirection() == angle);
    assert (s.obbEquals(test.getObb()));

    // Clockwise rotate moving Ship
    s = new Ship(new Position(0, 0), 0, 0xFF00FF);
    s.setSpeed(1);
    double squeezedSpeed = s.getSpeed() * (4 * FastMath.PI / 5) / Parameters.SHIP_TOP_SPEED_FORWARD;
    double rotModifier = FastMath.pow(FastMath.sin(squeezedSpeed), 2) + 0.5;
    PhysicsManager.rotateEntity(s, FastMath.PI / 2);
    angle = FastMath.PI / 2 * rotModifier * Parameters.ROTATION_MODIFIER;
    test = new Ship(new Position(0, 0), 0, 0xFF00FF);
    test.initObb();
    test.rotateObb(angle);
    assert (s.getDirection() == angle);
    assert (s.obbEquals(test.getObb()));

    // Anticlockwise rotate moving Ship
    s = new Ship(new Position(0, 0), 0, 0xFF00FF);
    s.setSpeed(1);
    squeezedSpeed = s.getSpeed() * (4 * FastMath.PI / 5) / Parameters.SHIP_TOP_SPEED_FORWARD;
    rotModifier = FastMath.pow(FastMath.sin(squeezedSpeed), 2) + 0.5;
    PhysicsManager.rotateEntity(s, -FastMath.PI / 2);
    angle = -FastMath.PI / 2 * rotModifier * Parameters.ROTATION_MODIFIER;
    angle = PhysicsManager.shiftAngle(angle);
    test = new Ship(new Position(0, 0), 0, 0xFF00FF);
    test.initObb();
    test.rotateObb(angle);
    assert (s.getDirection() == angle);
    assert (s.obbEquals(test.getObb()));
  }

  @Test
  public void testShiftAngle() {
    // Check angle less than 0
    double angle = -2 * FastMath.PI;
    angle = PhysicsManager.shiftAngle(angle);
    assert (angle >= 0 && angle < 2 * FastMath.PI);
    assert (angle == 0);

    // Check angle in range 0 - 2pi
    angle = FastMath.PI;
    angle = PhysicsManager.shiftAngle(angle);
    assert (angle >= 0 && angle < 2 * FastMath.PI);
    assert (angle == FastMath.PI);

    // Check angle greater than 2pi
    angle = 7 * FastMath.PI;
    angle = PhysicsManager.shiftAngle(angle);
    assert (angle >= 0 && angle < 2 * FastMath.PI);
    assert (angle == FastMath.PI);
  }

}
