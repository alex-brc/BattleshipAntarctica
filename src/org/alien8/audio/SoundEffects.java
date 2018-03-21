package org.alien8.audio;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This enum represents all sound effects in the game.
 *
 */
public enum SoundEffects {

  AMBIENT("/org/alien8/assets/ambient_waves.wav"), WIND("/org/alien8/assets/wind.wav"), MENU_MUSIC(
      "/org/alien8/assets/12 Shingle Tingle.wav"), INGAME_MUSIC(
          "/org/alien8/assets/04 Shell Shock Shake.wav"), AIRPLANE_PASS(
              "/org/alien8/assets/airplane_pass.wav"), SHIP_SHOOT_1(
                  "/org/alien8/assets/cannon1.wav"), SHIP_SHOOT_2(
                      "/org/alien8/assets/cannon2.wav"), SHIP_SHOOT_3(
                          "/org/alien8/assets/cannon3.wav");

  public String fileName;

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
   * Creates a Clip from a given sound effect.
   * 
   * @param sfx the sound effect to create a Clip from
   * @return the created Clip
   */
  public static Clip makeClip(SoundEffects sfx) {
    Clip clip = null;
    try {
      URL url = SoundEffects.class.getResource(sfx.fileName);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
    return clip;
  }
}
