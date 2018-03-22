package org.alien8.ui;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.alien8.client.Client;
import org.alien8.client.Client.State;
import org.alien8.core.Parameters;

/**
 * This class represents a Button used to create a game server.
 *
 */
public class CreateServerButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public CreateServerButton(int x, int y, int width, int height) {
    super(x, y, width, height, "create a server");
  }

  /**
   * Attempts to create a game server.
   */
  public void executeAction() {
    Client.getInstance().createServer(Parameters.MAX_PLAYERS);
    Client.getInstance().getLobby().setHost();
    Client.getInstance().setState(State.IN_LOBBY);
    String localServerIPStr = null;
    try {
      localServerIPStr = Inet4Address.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.out.println("fail to get local server IP address");
    }
    if (localServerIPStr != null)
      Client.getInstance().connect(localServerIPStr);
  }

}
