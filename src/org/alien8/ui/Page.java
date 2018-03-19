package org.alien8.ui;

import org.alien8.rendering.Renderer;

/**
 * This interface denotes any implementing class as a UI page (e.g. a menu, splash screen etc.).
 *
 */
public interface Page {
  /**
   * Renders this page to the screen.
   * 
   * @param r the Renderer instance used to render this page
   */
  public abstract void render(Renderer r);
}
