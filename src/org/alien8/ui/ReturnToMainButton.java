package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;
import org.alien8.server.Server;

/**
 * This class represents a Button used to return to the main menu.
 *
 */
public class ReturnToMainButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public ReturnToMainButton(int x, int y, int width, int height) {
    super(x, y, width, height, "return");
  }

  /**
   * Returns to the main menu.
   */
  public void executeAction() {
    Client.getInstance().setState(State.MAIN_MENU);
    try {
    if(Server.getInstance().gameRunning())
    	Server.getInstance().stop();
    } catch(NullPointerException e) {
    	// There is no server, that's fine
    }
  }

}
