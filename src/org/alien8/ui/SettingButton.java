package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class SettingButton extends Button{

	public SettingButton(int x, int y, int width, int height) {
		super(x, y, width, height, "settings");
	}
	
	public void executeAction(){
		Client.getInstance().setState(State.SETTINGS_MENU);
	}

}
