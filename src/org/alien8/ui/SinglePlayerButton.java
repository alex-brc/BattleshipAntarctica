package org.alien8.ui;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.alien8.client.Client;
import org.alien8.server.Server;

/**
 * This class represents a Button used to start a single player game.
 *
 */
public class SinglePlayerButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public SinglePlayerButton(int x, int y, int width, int height) {
    super(x, y, width, height, "single-player");
  }

  /**
   * Starts a single player game.
   */
  public void executeAction() {
    Client.getInstance().createServer(1);
    String localServerIPStr = null;
    try {
      localServerIPStr = Inet4Address.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.out.println("Fail to get local server IP address");
    }
    if (localServerIPStr != null)
      Client.getInstance().connect(localServerIPStr);

    Client.getInstance().getLobby().setHost();
    Client.getInstance().setState(Client.State.IN_LOBBY);
  }
}
