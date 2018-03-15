package org.alien8.ui;

import org.alien8.client.Client.State;
import org.alien8.client.Launcher;

public class ConnectButton extends Button{

	public ConnectButton(int x, int y, int width, int height, IPField ip) {
		super(x, y, width, height, "Connect to server");
		
	}
	
	public void executeAction(){
		Launcher.getInstance().getRunningClient().setState(State.IN_GAME);
	}

}
