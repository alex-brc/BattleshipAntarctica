package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

/**
 * This class represents a Button used to set the player's name.
 *
 */
public class NameButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public NameButton(int x, int y, int width, int height) {
    super(x, y, width, height, "next");
  }

  /**
   * Sets the player's name to the one which has been typed into the name box, and proceeds to the
   * main menu.
   */
  @Override
  public void executeAction() {
    Client.getInstance().setState(State.MAIN_MENU);
    Client.getInstance().setClientName(Client.getInstance().getNameScreen().getContent());
  }

}
