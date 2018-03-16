package org.alien8.ui;

import org.alien8.client.Client.State;
import org.alien8.client.Client;

public class ConnectButton extends Button{

	public ConnectButton(int x, int y, int width, int height, IPField ip) {
		super(x, y, width, height, "Connect to server");
		
	}
	
	public void executeAction(){
		Client.getInstance().setState(State.IN_GAME);
		Client.getInstance().connect(Client.getInstance().getMenu().getIP());
	}

}
