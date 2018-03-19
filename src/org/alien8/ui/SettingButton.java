package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

/**
 * This class represents a Button used to navigate to the settings menu.
 *
 */
public class SettingButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public SettingButton(int x, int y, int width, int height) {
    super(x, y, width, height, "settings");
  }

  /**
   * Navigates to the settings menu.
   */
  public void executeAction() {
    Client.getInstance().setState(State.SETTINGS_MENU);
  }

}
