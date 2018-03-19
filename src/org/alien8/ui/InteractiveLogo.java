package org.alien8.ui;

import org.alien8.client.InputManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

/**
 * This class represents an interactive game logo.
 *
 */
public class InteractiveLogo {
  public static final int WEIGHT = 100;

  int x, y;

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   */
  public InteractiveLogo(int x, int y) {
    this.x = x - Sprite.logo.getWidth() / 2;
    this.y = y - Sprite.logo.getHeight() / 2;
  }

  /**
   * @return the Position of this logo
   */
  private Position getLogoPosition() {
    Position mousePos = InputManager.getInstance().mousePosition();
    return new Position(
        x + (mousePos.getX() - Renderer.getInstance().getWidth() / 2)
            / Parameters.MENU_PARALLAX_WEIGHT,
        y + (mousePos.getY() - Renderer.getInstance().getHeight() / 2)
            / Parameters.MENU_PARALLAX_WEIGHT);
  }

  /**
   * Renders this logo to the screen.
   */
  public void render() {
    Position pos = getLogoPosition();
    Renderer.getInstance().drawSprite((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()),
        Sprite.logo, true);
  }
}
