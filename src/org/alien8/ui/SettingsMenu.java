package org.alien8.ui;

import org.alien8.audio.AudioManager;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

/**
 * This class represents a settings menu where the player can change game options.
 *
 */
public class SettingsMenu implements Page {

  private MuteButton muteSfxBtn = null;
  private MuteButton muteAmbBtn = null;
  private VolumeButton sfxVolUpBtn = null;
  private VolumeButton ambVolUpBtn = null;
  private VolumeButton sfxVolDownBtn = null;
  private VolumeButton ambVolDownBtn = null;
  private ReturnToMainButton returnBtn = null;
  private InfoBox sfxInfo = null;
  private InfoBox ambInfo = null;
  private InteractiveLogo logo = null;

  private int btnWidth = (18 * 16) / 3 + 4; // Max button text length * font size + padding
  private int height = 40; // Font size + padding;
  private int vPad = 5; // Spacing between buttons = r.getWidth() / vPad
  private int vOffset = 50; // Pixels to offset from the top of the window

  /**
   * Constructor.
   */
  public SettingsMenu() {
    btnWidth = (18 * 16) / 3 + 4; // Max button text length * font size + padding
    height = 40; // Font size + padding;
    vPad = 9; // Spacing between buttons = r.getWidth() / vPad
    vOffset = 160; // Pixels to offset from the top of the window

    Renderer r = Renderer.getInstance();
    muteSfxBtn =
        new MuteButton(AudioManager.SFX, r.getWidth() / 2 + Sprite.health_bar.getWidth() / 2 + 8,
            vOffset + (r.getHeight() / vPad) * 2, 16 * 3, height);
    sfxVolUpBtn = new VolumeButton(AudioManager.SFX, VolumeButton.UP,
        r.getWidth() / 2 - btnWidth - 5, vOffset + r.getHeight() / vPad, btnWidth, height);
    sfxVolDownBtn = new VolumeButton(AudioManager.SFX, VolumeButton.DOWN, r.getWidth() / 2 + 5,
        vOffset + r.getHeight() / vPad, btnWidth, height);
    muteAmbBtn = new MuteButton(AudioManager.AMBIENT,
        r.getWidth() / 2 + Sprite.health_bar.getWidth() / 2 + 8, vOffset + r.getHeight() / vPad * 4,
        16 * 3, height);
    ambVolUpBtn = new VolumeButton(AudioManager.AMBIENT, VolumeButton.UP,
        r.getWidth() / 2 - btnWidth - 5, vOffset + r.getHeight() / vPad * 3, btnWidth, height);
    ambVolDownBtn = new VolumeButton(AudioManager.AMBIENT, VolumeButton.DOWN, r.getWidth() / 2 + 5,
        vOffset + r.getHeight() / vPad * 3, btnWidth, height);
    returnBtn = new ReturnToMainButton(r.getWidth() / 2 - btnWidth,
        vOffset + r.getHeight() / vPad * 5, 2 * btnWidth, height);
    sfxInfo = new InfoBox(r.getWidth() / 2 - Sprite.health_bar.getWidth() / 2 - 16 * 3 - 8,
        vOffset + r.getHeight() / vPad * 2 + 10, 16 * 4, height);
    sfxInfo.updateText("sfx");
    ambInfo = new InfoBox(r.getWidth() / 2 - Sprite.health_bar.getWidth() / 2 - 16 * 7 - 8,
        vOffset + r.getHeight() / vPad * 4 + 10, 16 * 8, height);
    ambInfo.updateText("ambient");

    // Make the logo
    logo = new InteractiveLogo(r.getWidth() / 2, 100);
  }

  public void render(Renderer r) {
    ambInfo.render(r);
    sfxInfo.render(r);
    muteSfxBtn.render(r);
    muteAmbBtn.render(r);
    sfxVolUpBtn.render(r);
    ambVolUpBtn.render(r);
    sfxVolDownBtn.render(r);
    ambVolDownBtn.render(r);
    returnBtn.render(r);

    logo.render();

    // Render volume bars
    // SFX volume
    r.drawBar(Sprite.health_bar, AudioManager.getInstance().getSfxVolumeValue(), 1,
        r.getWidth() / 2 - Sprite.health_bar.getWidth() / 2, vOffset + r.getHeight() / vPad * 2,
        true);
    // Ambient volume
    r.drawBar(Sprite.health_bar, AudioManager.getInstance().getAmbientVolumeValue(), 1,
        r.getWidth() / 2 - Sprite.health_bar.getWidth() / 2, vOffset + r.getHeight() / vPad * 4,
        true);


  }

}
