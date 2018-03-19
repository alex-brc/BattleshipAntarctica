package org.alien8.ui;

import org.alien8.audio.AudioManager;

/**
 * This class represents a Button which can be used to adjust a type of in-game volume.
 *
 * 
 */
public class VolumeButton extends Button {

  public static final int UP = 1;
  public static final int DOWN = 2;

  private int controlType;
  private int incrementType;

  /**
   * Constructor.
   * 
   * @param controlType the type of volume to adjust (sound effects or ambient sound)
   * @param incrementType the direction to adjust in (up or down)
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public VolumeButton(int controlType, int incrementType, int x, int y, int width, int height) {
    super(x, y, width, height, (incrementType == UP) ? "up" : "down");
    this.controlType = controlType;
    this.incrementType = incrementType;
  }

  /**
   * Adjusts the volume of the appropriate sound type in the correct direction.
   */
  public void executeAction() {
    if (incrementType == UP && controlType == AudioManager.SFX) {
      AudioManager.getInstance().sfxIncreaseVolume();
    } else if (incrementType == UP && controlType == AudioManager.AMBIENT) {
      AudioManager.getInstance().ambientIncreaseVolume();;
    } else if (incrementType == DOWN && controlType == AudioManager.SFX) {
      AudioManager.getInstance().sfxDecreaseVolume();
    } else if (incrementType == DOWN && controlType == AudioManager.AMBIENT) {
      AudioManager.getInstance().ambientDecreaseVolume();
    }
  }

}
