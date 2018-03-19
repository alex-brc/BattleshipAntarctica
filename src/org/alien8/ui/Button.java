package org.alien8.ui;

import org.alien8.client.InputManager;
import org.alien8.physics.Position;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

/**
 * This class represents an abstract UI button that can be clicked to perform some action.
 *
 */
public abstract class Button {

  private int x, y, width, height;
  private int col, hCol;
  private String text;

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   * @param text the text displayed on this Button
   */
  public Button(int x, int y, int width, int height, String text) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.text = text;
    col = 0x000000;
    hCol = 0xAAAAAA;
  }

  /**
   * Changes the text of this button.
   * 
   * @param text the new text to display
   */
  protected void changeText(String text) {
    this.text = text;
  }

  /**
   * Renders this Button to the screen.
   * 
   * @param r the Renderer instance used to render this Button
   */
  public void render(Renderer r) {
    Position p = InputManager.getInstance().mousePosition();
    int mX = (int) p.getX();
    int mY = (int) p.getY();
    Position l = InputManager.getInstance().lastLmbClick();
    int lX = (int) l.getX();
    int lY = (int) l.getY();

    if (mX >= x && mY >= y && mX <= x + width && mY <= y + height) {
      r.drawFilledRect(x, y, width, height, hCol, true);
      if (lX >= x && lY >= y && lX <= x + width && lY <= y + height) {
        InputManager.getInstance().resetLastLmbClick();
        executeAction();
      }
    } else {
      r.drawRect(x, y, width, height, hCol, true);
    }
    r.drawText(text, x + width / 2 - (text.length() * 16) / 2, y + (height - 16) / 2, true,
        FontColor.WHITE);
  }

  /**
   * Performs the action of this Button.
   */
  public abstract void executeAction();
}
