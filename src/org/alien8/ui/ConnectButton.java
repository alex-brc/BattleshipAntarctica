package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class ConnectButton extends Button{

	public ConnectButton(int x, int y, int width, int height) {
		super(x, y, width, height, "connect to server");
		
	}
	
	public void executeAction(){
		// Verify the ip
		String ip = Client.getInstance().getMenu().getIP();
		// Check if there's enough addresses 
		int dots = ip.length() - ip.replace(".", "").length();
		if(dots != 3) {
			showMessage("that IP was invalid");
			return;
		} else {
			// Check if all addresses are in 0,255
			String[] tokens = ip.split(".");
			for(String s : tokens) {
				int k = Integer.parseInt(s);
				if(k < 0 || k > 255) {
					showMessage("that IP was invalid");
					return;
				}
			}
		}
		// IP is okay
		boolean connected = Client.getInstance().connect(ip);
		if(!connected) {
			showMessage("  couldn't connect");
			return;
		}
		// Connected, fill up the lobby properly
		Client.getInstance().setState(State.IN_LOBBY);
	}
	
	private void showMessage(String message) {
		Client.getInstance().getMenu().setConnectInfo(message);
	}

}
