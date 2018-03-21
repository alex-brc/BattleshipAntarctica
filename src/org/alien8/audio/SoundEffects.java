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
  SHIP_SHOOT_1("/org/alien8/assets/cannon1.wav"), 
  SHIP_SHOOT_2("/org/alien8/assets/cannon2.wav"), 
  SHIP_SHOOT_3("/org/alien8/assets/cannon3.wav");

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
