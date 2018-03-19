package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

/**
 * This class represents a Button used to connect to a game server.
 *
 */
public class ConnectButton extends Button {

  /**
   * Constructor.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param width the width of this Button in pixels
   * @param height the height of this Button in pixels
   */
  public ConnectButton(int x, int y, int width, int height) {
    super(x, y, width, height, "connect to server");
  }

  /**
   * Attempts to connect to a game server.
   */
  public void executeAction() {
    // Verify the ip
    String ip = Client.getInstance().getMenu().getIP();
    // Check if there's enough addresses
    int dots = ip.length() - ip.replace(".", "").length();
    if (dots != 3) {
      showMessage("that IP was invalid");
      return;
    } else {
      // Check if all addresses are in 0,255
      String[] tokens = ip.split(".");
      for (String s : tokens) {
        int k = Integer.parseInt(s);
        if (k < 0 || k > 255) {
          showMessage("that IP was invalid");
          return;
        }
      }
    }
    // IP is okay
    boolean connected = Client.getInstance().connect(ip);
    if (!connected) {
      showMessage("  couldn't connect");
      return;
    }
    // Connected, fill up the lobby properly
    Client.getInstance().setState(State.IN_LOBBY);
  }

  /**
   * Shows a message to the player.
   * 
   * @param message the message to show
   */
  private void showMessage(String message) {
    Client.getInstance().getMenu().setConnectInfo(message);
  }

}
