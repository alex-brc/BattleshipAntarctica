package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.InputManager;
import org.alien8.client.Client.State;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

public class TextBox {

  private int x, y, width, height, charLimit;
  private int backCol, bordCol;
  private String text;

  public TextBox(int x, int y, int width, int height, int charLimit) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.charLimit = charLimit;
    backCol = 0x000000;
    bordCol = 0x888888;
    text = "";
  }

  public void render(Renderer r) {
    r.drawFilledRect(x, y, width, height, backCol, true);
    r.drawRect(x, y, width, height, bordCol, true);
    r.drawText(text, x + width/2 - text.length()*16 / 2, y, true, FontColor.WHITE);
    char c = InputManager.getInstance().getKeyTyped();
    if ( ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.') && text.length() < charLimit) {
      text += c;
    } else if (c == 8 && text.length() > 0) { // code for backspace
      text = text.substring(0, text.length() - 1);
    } else if (c == 1 && Client.getInstance().getState() == State.NAME_SCREEN) {
    	Client.getInstance().setClientName(text);
    	Client.getInstance().setState(State.MAIN_MENU);
    }
  }

  public String getInput() {
    return text;
  }

}
