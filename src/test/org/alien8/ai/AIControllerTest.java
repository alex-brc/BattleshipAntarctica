package test.org.alien8.ai;

import org.alien8.ai.AIController;
import org.alien8.core.ServerModelManager;
import org.alien8.drops.HealthItem;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.Test;

public class AIControllerTest {
	@Test
	public void testAI() {
		ServerModelManager.getInstance().makeMap(0);
		Ship ship = new Ship(new Position(0,0),0,0);
		ship.giveItem(new HealthItem());
		AIController ai = new AIController(ship);
		ship = new Ship(new Position(50,50),0,0);
		ServerModelManager.getInstance().addEntity(ship);
		
		ai.update();
	}

}
