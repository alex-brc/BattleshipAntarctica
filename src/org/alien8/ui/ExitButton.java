package org.alien8.ui;

/**
 * This class represents a Button used to exit the game.
 *
 */
public class ExitButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public ExitButton(int x, int y, int width, int height) {
    super(x, y, width, height, "exit");
  }

  /**
   * Exits the game.
   */
  public void executeAction() {
    System.exit(0);
  }

}
