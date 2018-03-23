package test.org.alien8.audio;

import org.alien8.audio.AudioEvent;
import org.alien8.physics.Position;
import org.junit.Test;

public class AudioEventTest {
	
	@Test
	public void test() {
		AudioEvent ae = null;
		for(AudioEvent.Type t : AudioEvent.Type.values()) {
			ae = new AudioEvent(t,new Position(0,0));
		}
		
		assert(ae.getPosition().getX() == 0);
		assert(ae.getPosition().getY() == 0);
		
		assert(ae.getType() == AudioEvent.Type.MINE_EXPLODE);
		System.out.println(ae.toString());
		assert(ae.toString().equals("SHIP_CRASH X: 0.0 Y: 0.0"));
	}

}
