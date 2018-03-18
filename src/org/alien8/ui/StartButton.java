package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;
import org.alien8.server.Server;

public class StartButton extends Button {

	public StartButton(int x, int y, int width, int height) {
		super(x, y, width, height, "start");
	}

	@Override
	public void executeAction() {
		Server.getInstance().startSGH();
		Client.getInstance().setState(State.IN_GAME);
	}

}
