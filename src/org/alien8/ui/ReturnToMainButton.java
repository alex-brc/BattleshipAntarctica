package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class ReturnToMainButton extends Button{

	public ReturnToMainButton(int x, int y, int width, int height) {
		super(x, y, width, height, "return");
	}
	
	public void executeAction(){
		Client.getInstance().setState(State.MAIN_MENU);
	}

}
