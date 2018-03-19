package org.alien8.ui;

import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

/**
 * This class provides a mean of displaying static text to the screen at a position. Essentially,
 * it's a functional wrapper for <code>Renderer.drawText()</code>.
 *
 */
public class InfoBox {

  private int x, y, width, height;
  private int backCol;
  private String text;

  /**
   * Basic constructor for the InfoBox.
   * 
   * @param x the x coordinate of the position to render at
   * @param y the y coordinate of the position to render at
   * @param width the width of the box
   * @param height the height of the box
   */
  public InfoBox(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    backCol = 0x000000;
    this.text = "";
  }

  /**
   * Updates the text of this InfoBox.
   * 
   * @param text the text to update to
   */
  public void updateText(String text) {
    this.text = text;
  }

  /**
   * Renders this box to the screen.
   * 
   * @param r the Renderer instance used to render this box
   */
  public void render(Renderer r) {
    r.drawFilledRect(x, y, width, height, backCol, true);
    r.drawText(text, x, y, true, FontColor.WHITE);
  }

}

