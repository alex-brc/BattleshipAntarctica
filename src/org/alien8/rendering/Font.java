package org.alien8.rendering;

/**
 * This class represents text characters that can be rendered in the game.
 *
 */
public class Font {
  private static Sprite fontBlack = new Sprite("/org/alien8/assets/fontBigBlack.png");
  private static Sprite fontWhite = new Sprite("/org/alien8/assets/fontBigWhite.png");
  private static Sprite[] charactersBlack = Sprite.split(fontBlack, 16);
  private static Sprite[] charactersWhite = Sprite.split(fontWhite, 16);

  public static Font defaultFont = new Font();

  public static String charIndex = //
      "ABCDEFGHIJKLM" + //
          "NOPQRSTUVWXYZ" + //
          "abcdefghijklm" + //
          "nopqrstuvwxyz" + //
          "1234567890-=[" + //
          "];\'#\\,.¬!\"£$%" + //
          "^&*()_+{}:@~|" + //
          "<>?` ";

  /**
   * Empty constructor.
   */
  public Font() {
    // Nothing
  }

  /**
   * Renders text onto the screen.
   * 
   * @param text the text to render
   * @param r the Renderer object to use
   * @param x the x position of the left edge of the text
   * @param y the y position of the top edge of the text
   * @param fixed fixed {@code true} if the rectangle is at a fixed screen position (for UI
   *        elements), {@code false} if the text moves relative to the position of the player
   * @param color the color of the text, black or white
   */
  public void render(String text, Renderer r, int x, int y, boolean fixed, FontColor color) {
    for (int i = 0; i < text.length(); i++) {
      char currentChar = text.charAt(i);
      int index = charIndex.indexOf(currentChar);
      if(index == -1)
    	  return;
      switch (color) {
        case BLACK:
          r.drawSprite(x + i * 16, y, charactersBlack[index], fixed);
          break;
        case WHITE:
          r.drawSprite(x + i * 16, y, charactersWhite[index], fixed);
          break;
      }
    }
  }
}

