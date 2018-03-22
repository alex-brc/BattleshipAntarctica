package test.org.alien8.physics;

import org.alien8.physics.Position;
import org.junit.Test;

public class PositionTest {

    @Test
    public void test() {
        Position p = new Position(0d, 1d);
        assert (p.getX() == 0);
        assert (p.getY() == 1);

        p.setX(2d);
        p.setY(3d);
        assert (p.getX() == 2);
        assert (p.getY() == 3);

        Position p1 = new Position(2d, 3d);
        Position p2 = new Position(-1d, -1d);

        assert (p1.equals(p));
        assert (!p1.equals(p2));
    }
}