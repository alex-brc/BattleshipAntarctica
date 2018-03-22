package test.org.alien8.ai;


import java.util.Random;
import org.alien8.ai.AIController;
import org.alien8.core.Entity;
import org.alien8.core.ServerModelManager;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.BeforeClass;
import org.junit.Test;

public class AIControllerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testFindCloestTarget() {
		//Server.getInstance().setMaxPlayer(2);
	    //Server.getInstance().start();
	    /*ServerModelManager.getInstance().makeMap((new Random()).nextLong());
	    Ship sai = new Ship(new Position(100, 100), 0, 0xFF00FF);
	    AIController ai = new AIController(sai);
	    Ship s1 = new Ship(new Position(100, 200), 0, 0xFF00FF);
	    Ship s2 = new Ship(new Position(300, 300), 0, 0xFF00FF);
	    ServerModelManager.getInstance().addEntity(sai);
	    ServerModelManager.getInstance().addEntity(s1);
	    ServerModelManager.getInstance().addEntity(s2);
	    
	    ai.findClosestTarget();
	    assert (ai.getTarget().getSerial() == 2);
	    //Server.getInstance().stop();
	    */
	}
	
	/*public void testRayDetect() {
		Server.getInstance().setMaxPlayer(2);
	    Server.getInstance().start();
	    
	    
	    
	    Server.getInstance().stop();
	}
	
	public void testDrawRay() {
		Server.getInstance().setMaxPlayer(2);
	    Server.getInstance().start();
	    
	    
	    
	    Server.getInstance().stop();
	}
	
	public void testWanderNoObsticle() {
		Server.getInstance().setMaxPlayer(2);
	    Server.getInstance().start();
	    
	    
	    
	    Server.getInstance().stop();
	}
	
	public void testWanderObsticleTurnRight() {
		Server.getInstance().setMaxPlayer(2);
	    Server.getInstance().start();
	    
	    
	    
	    Server.getInstance().stop();
	}
	
	public void testWanderObsticleTurnLeft() {
		Server.getInstance().setMaxPlayer(2);
	    Server.getInstance().start();
	    
	    
	    
	    Server.getInstance().stop();
	}*/

}
