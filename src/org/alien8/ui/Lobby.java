package org.alien8.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

/**
 * This class represents a server lobby screen which appears when hosting/connecting to a game
 * server.
 *
 */
public class Lobby implements Page {
  private boolean isHost = false;
  private StartButton startBtn;
  private InteractiveLogo logo;
  private String ip;

  /**
   * Constructor.
   */
  public Lobby() {
    logo = new InteractiveLogo(Renderer.getInstance().getWidth() / 2, 100);
    startBtn = new StartButton(360, 530, 80, 30);
    try {
      ip = "server ip: " + InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the player to be the server's host.
   */
  public void setHost() {
    this.isHost = true;

  }

  /**
   * Sets the player to be a connecting player (not the server's host).
   */
  public void setNotHost() {
    this.isHost = false;

  }

  public void render(Renderer renderer) {
    if (isHost) {
      startBtn.render(renderer);
      renderer.drawText(ip, renderer.getWidth() / 2 - ip.length() * 8, 180, true, FontColor.WHITE);
    } else {
      renderer.drawText("the host can start the game",
          renderer.getWidth() / 2 - "the host can start the game".length() * 8, 530, true,
          FontColor.WHITE);
      renderer.drawText("waiting for players",
          renderer.getWidth() / 2 - "waiting for players".length() * 8, 180, true, FontColor.WHITE);
    }
    logo.render();
    renderer.drawSprite(renderer.getWidth() / 2 - Sprite.controls.getWidth() / 2,
        renderer.getHeight() / 2 - Sprite.controls.getHeight() / 2 + 60, Sprite.controls, true);

  }
}
