package org.alien8.ui;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class CreateServerButton extends Button {

  public CreateServerButton(int x, int y, int width, int height) {
    super(x, y, width, height, "create a server");

  }

  public void executeAction() {
    Client.getInstance().createServer(8);
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
