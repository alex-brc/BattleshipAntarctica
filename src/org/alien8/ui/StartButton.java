package org.alien8.ui;

import org.alien8.server.Server;

/**
 * This class represents a Button used to start the game running on a game server. For this to
 * happen, a server must have already been created but it must not already have a game running on
 * it.
 *
 */
public class StartButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public StartButton(int x, int y, int width, int height) {
    super(x, y, width, height, "start");
  }

  /**
   * Starts the game running on a game server.
   */
  @Override
  public void executeAction() {
    Server.getInstance().startSGH();
  }

}
