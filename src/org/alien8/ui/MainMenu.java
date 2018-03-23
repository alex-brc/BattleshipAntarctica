package org.alien8.ui;

import org.alien8.rendering.Renderer;

/**
 * This class represents the main menu for the game. It presents different options to the player,
 * such as single player, multiplayer and settings.
 *
 */
public class MainMenu implements Page {

  private SinglePlayerButton spBtn;
  private CreateServerButton csBtn;
  private TextBox ipField;
  private ConnectButton conBtn;
  private InfoBox conInfo;
  private SettingButton setBtn;
  private ExitButton exBtn;
  private InteractiveLogo logo;
  private CreditsButton credsBtn;

  /**
   * Constructor.
   */
  public MainMenu() {
    int infoWidth = (12 * 16) + (15 * 16) + 4; // Prepend text * font size + IP length * font size +
                                               // padding
    int fieldWidth = (18 * 16) + 4; // IP length * font size + padding
    int btnWidth = (18 * 16) + 4; // Max button text length * font size + padding
    int height = 40; // Font size + padding;
    int vPad = 9; // Spacing between buttons = r.getWidth() / vPad
    int vOffset = 140; // Pixels to offset from the top of the window

    Renderer r = Renderer.getInstance();
    // Make buttons
    spBtn = new SinglePlayerButton(r.getWidth() / 2 - btnWidth / 2, vOffset + r.getHeight() / vPad,
        btnWidth, height);
    csBtn = new CreateServerButton(r.getWidth() / 2 - btnWidth / 2,
        vOffset + (r.getHeight() / vPad) * 2, btnWidth, height);
    ipField = new TextBox(r.getWidth() / 2 - fieldWidth / 2,
        vOffset + (r.getHeight() / vPad) * 3 - height / 2 + 2, fieldWidth, 18, 15);
    conInfo = new InfoBox(r.getWidth() / 2 - fieldWidth / 2 - 4,
        vOffset + (r.getHeight() / vPad) * 3 + height + 4, infoWidth, height);
    conBtn = new ConnectButton(r.getWidth() / 2 - btnWidth / 2,
        vOffset + (r.getHeight() / vPad) * 3, btnWidth, height);
    setBtn = new SettingButton(r.getWidth() / 2 - btnWidth / 2,
            vOffset + (r.getHeight() / vPad) * 4, btnWidth, height);
    credsBtn = new CreditsButton(r.getWidth() / 2 - btnWidth / 2,
            vOffset + (r.getHeight() / vPad) * 5, btnWidth, height);
    exBtn = new ExitButton(r.getWidth() / 2 - btnWidth / 2, vOffset + (r.getHeight() / vPad) * 6,
        btnWidth, height);
    

    // Make the logo
    logo = new InteractiveLogo(r.getWidth() / 2, 100);

  }

  public void render(Renderer r) {
    spBtn.render(r);
    csBtn.render(r);
    ipField.render(r);
    conBtn.render(r);
    conInfo.render(r);
    setBtn.render(r);
    credsBtn.render(r);
    exBtn.render(r);

    logo.render();
  }

  /**
   * @return the IP address entered into the IP field
   */
  public String getIP() {
    return ipField.getInput();
  }

  /**
   * Sets information used to connect to a server.
   * 
   * @param info the String information to set
   */
  public void setConnectInfo(String info) {
    conInfo.updateText(info);
  }

}
