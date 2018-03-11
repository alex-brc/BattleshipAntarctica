package org.alien8.rendering;

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


  public Font() {

  }

  public void render(String text, Renderer r, int x, int y, boolean fixed, FontColor color) {
    for (int i = 0; i < text.length(); i++) {
      char currentChar = text.charAt(i);
      int index = charIndex.indexOf(currentChar);
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

