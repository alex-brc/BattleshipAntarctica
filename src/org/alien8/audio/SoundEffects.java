package org.alien8.audio;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * This enum represents all sound effects in the game.
 *
 */
public enum SoundEffects {

	AMBIENT("/org/alien8/assets/ambient_waves.wav"), 
	WIND("/org/alien8/assets/wind.wav"),
	MENU_MUSIC("/org/alien8/assets/12 Shingle Tingle.wav"), 
	INGAME_MUSIC("/org/alien8/assets/04 Shell Shock Shake.wav"), 
	AIRPLANE_PASS("/org/alien8/assets/airplane_pass.wav"), 
	SHIP_SHOOT_1("/org/alien8/assets/cannon1.wav"), 
	SHIP_SHOOT_2("/org/alien8/assets/cannon2.wav"), 
	SHIP_SHOOT_3("/org/alien8/assets/cannon3.wav"),
	SHIP_PICKUP("/org/alien8/assets/pickup.wav"),
	SHIP_HIT("/org/alien8/assets/hit.wav"),
	MINE_EXPLODE("/org/alien8/assets/mine_explosion.wav");

  private String fileName;

  /**
   * Pre-loads all the sound files.
   */
  public static void init() {
    // Calls constructor for all members
    values();
  }

  SoundEffects(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Creates a new Clip from this <code>SoundEffect</code>.
   * 
   * @return the created Clip
   */
  public Clip makeClip() {
    Clip clip = null;
    try {
      URL url = SoundEffects.class.getResource(this.fileName);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
    } catch (Exception e) {
    	e.printStackTrace();
    }
    return clip;
  }
}
