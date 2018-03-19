package org.alien8.ui;

import org.alien8.audio.AudioManager;

/**
 * This class represents a Button used to mute game volume.
 *
 */
public class MuteButton extends Button {

  private int controlType;

  /**
   * Constructor.
   * 
   * @param controlType the type of game volume to mute (sound effects or ambient)
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public MuteButton(int controlType, int x, int y, int width, int height) {
    super(x, y, width, height, "m");
    this.controlType = controlType;
  }

  /**
   * Mutes the game volume of the type this Button represents.
   */
  public void executeAction() {
    if (controlType == AudioManager.SFX) {
      if (AudioManager.getInstance().sfxMuteToggle())
        this.changeText("um");
      else
        this.changeText("m");
    } else if (controlType == AudioManager.AMBIENT) {
      if (AudioManager.getInstance().ambientMuteToggle())
        this.changeText("um");
      else
        this.changeText("m");
    }
  }

}
