package org.alien8.ui;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class CreateServerButton extends Button {

  public CreateServerButton(int x, int y, int width, int height) {
    super(x, y, width, height, "Create a server");

  }

  public void executeAction() {
    Client.getInstance().createServer(16);
    Client.getInstance().setState(State.IN_GAME);
    String localServerIPStr = null;
    try {
      localServerIPStr = Inet4Address.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.out.println("Fail to get local server IP address");
    }
    if (localServerIPStr != null)
      Client.getInstance().connect(localServerIPStr);
  }

}
