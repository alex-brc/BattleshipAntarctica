package org.alien8.ui;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.alien8.client.Client;
import org.alien8.client.Client.State;
import org.alien8.server.Server;

public class SinglePlayerButton extends Button {

  public SinglePlayerButton(int x, int y, int width, int height) {
    super(x, y, width, height, "single-player");
  }

  public void executeAction() {
    Client.getInstance().createServer(1);
    Client.getInstance().setState(State.IN_GAME);
    String localServerIPStr = null;
    try {
      localServerIPStr = Inet4Address.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.out.println("Fail to get local server IP address");
    }
    if (localServerIPStr != null)
      Client.getInstance().connect(localServerIPStr);
    
    // Game start
	Server.getInstance().startSGH();
  }
}
