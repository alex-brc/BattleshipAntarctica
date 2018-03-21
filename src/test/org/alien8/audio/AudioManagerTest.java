package test.org.alien8.audio;

import java.util.concurrent.TimeUnit;

import org.alien8.audio.AudioEvent;
import org.alien8.audio.AudioManager;
import org.alien8.core.ClientModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AudioManagerTest {
	
	static AudioManager am;
	
	@BeforeClass
	public static void setUp() {
		// It needs a ClientModelManager
		ClientModelManager.getInstance()
			.setPlayer(new Ship(new Position(0,0), 0, 0x000000));
		
		// Instantiate it
		am = AudioManager.getInstance();
		// Make it quiet
		am.ambientMuteToggle();
		am.sfxMuteToggle();
		
	}

	@Test
	public void testAmbient() {
		// Unmute
		am.ambientMuteToggle();
		// Play ambient music
		am.startAmbient(0);
		am.startAmbient(1);
		// Mute
		am.ambientMuteToggle();
		// Stop ambient music
		am.stopAmbient(0);
		am.stopAmbient(1);
		
		double initialVolume = am.getAmbientVolumeValue();
		// Increase volume
		am.ambientIncreaseVolume();
		assert(am.getAmbientVolumeValue() - initialVolume > 0.09f); // for rounding errors
		// Push the 1.0 boundary
		for(int i = 0; i < 11; i++)
			am.ambientIncreaseVolume();
		
		initialVolume = am.getAmbientVolumeValue();
		// Decrease volume
		am.ambientDecreaseVolume();
		assert(initialVolume - am.getAmbientVolumeValue()> 0.09f); // for rounding errors
		// Push the 0.0 boundary
		for(int i = 0; i < 11; i++)
			am.ambientDecreaseVolume();		
	}

	@Test
	public void testSfx() {
		// It was muted initially. 
		// See if it unmutes
		assert(am.sfxMuteToggle() == false);
		// See if it mutes 
		assert(am.sfxMuteToggle() == true);
	
		double initialVolume = am.getSfxVolumeValue();
		// Increase volume
		am.sfxIncreaseVolume();
		assert(am.getSfxVolumeValue() - initialVolume > 0.09f); // for rounding errors
		// Push the 1.0 boundary
		for(int i = 0; i < 11; i++)
			am.sfxIncreaseVolume();
		
		initialVolume = am.getSfxVolumeValue();
		// Decrease volume
		am.sfxDecreaseVolume();
		assert(initialVolume - am.getSfxVolumeValue() > 0.09f); // for rounding errors
		// Push the 0.0 boundary
		for(int i = 0; i < 11; i++)
			am.sfxDecreaseVolume();
	}

	@Test
	public void testSoundEvents() throws InterruptedException {
		AudioEvent ae = new AudioEvent(AudioEvent.Type.SHOOT, new Position(0,0));
		// Play 20 shoot sounds
		for(int i = 0; i < 20; i++) 
			am.addEvent(ae);
		am.sfxMuteToggle();
		// Play one more sound
		am.addEvent(ae);
		// Make a sound out of hearing range
		AudioEvent ae2 = new AudioEvent(AudioEvent.Type.SHOOT, 
				new Position(Parameters.MAX_HEARING_DISTANCE, 
						Parameters.MAX_HEARING_DISTANCE));
		// Play it
		am.addEvent(ae2);
		am.sfxMuteToggle();
		
		// Wait for all sounds to finish playing
		TimeUnit.SECONDS.sleep(2);
	}
	
	@AfterClass
	public static void tearDown() {
		// Make sure it's closed
		am.shutDown();
	}

}
