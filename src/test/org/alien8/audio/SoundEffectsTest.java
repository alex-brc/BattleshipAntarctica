package test.org.alien8.audio;

import javax.sound.sampled.Clip;

import org.alien8.audio.SoundEffects;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoundEffectsTest {

	@BeforeClass
	public static void setUp() {
		// Initialise sound effects.
		SoundEffects.init();
	}

	@Test
	public void testMakeClip() {
		Clip clip = null;
		// Make a clip
		clip = SoundEffects.SHIP_SHOOT_1.makeClip();
		// Test it
		clip.start();
		// Close it
		clip.close();
	}
}
