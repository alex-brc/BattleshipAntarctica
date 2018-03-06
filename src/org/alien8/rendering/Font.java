package org.alien8.rendering;

public class Font {

  private static Sprite font = new Sprite("/org/alien8/assets/fontBig.png");
  private static Sprite[] characters = Sprite.split(font, 16);

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


  public Font() {

  }

  public void render(String text, Renderer r, int x, int y, boolean fixed) {
    for (int i = 0; i < text.length(); i++) {
      char currentChar = text.charAt(i);
      int index = charIndex.indexOf(currentChar);
      r.drawSprite(x + i * 16, y, characters[index], fixed);
    }
  }
}

