package org.alien8.ui;

import org.alien8.rendering.Renderer;

/**
 * This class represents a screen where the player can enter their name into a box.
 *
 */
public class NameScreen implements Page {

  private InfoBox conInfo;
  private TextBox nameBox;
  private NameButton nameBtn;

  /**
   * Constructor.
   */
  public NameScreen() {
    int fontSize = 16;
    int boxWidth = fontSize * 8;
    int vPad = 8;
    int vOffset = 100;

    Renderer r = Renderer.getInstance();
    conInfo = new InfoBox(r.getWidth() / 2 - boxWidth / 2 - 16 * 4 + 8,
        vOffset + r.getHeight() / vPad, boxWidth, fontSize + 2);
    conInfo.updateText("enter your name");
    nameBox = new TextBox(r.getWidth() / 2 - boxWidth / 2,
        vOffset + (r.getHeight() / vPad) * 2 - fontSize / 2, boxWidth, fontSize + 2, 8);
    nameBtn = new NameButton(r.getWidth() / 2 - boxWidth / 2, vOffset + (r.getHeight() / vPad) * 3,
        boxWidth, 2 * fontSize + 2);

  }

  @Override
  public void render(Renderer r) {
    conInfo.render(r);
    nameBox.render(r);
    nameBtn.render(r);
  }

  /**
   * @return the text entered into the name box
   */
  public String getContent() {
    return nameBox.getInput();
  }

}
