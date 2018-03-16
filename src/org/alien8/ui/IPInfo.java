package org.alien8.ui;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

public class IPInfo {

  private int x, y, width, height;
  private int backCol;
  private String localServerIPStr;
  private String text;

  public IPInfo(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    backCol = 0x000000;
    try {
      localServerIPStr = Inet4Address.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.out.println("Fail to get local server IP address");
    }
    text = "your server IP is " + localServerIPStr;
  }

  public void render(Renderer r) {
    r.drawFilledRect(x, y, width, height, backCol, true);
    r.drawText(text, x, y, true, FontColor.WHITE);
  }

}

