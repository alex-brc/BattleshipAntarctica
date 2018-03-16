package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class NameButton extends Button {

	public NameButton(int x, int y, int width, int height) {
		super(x, y, width, height, "next");
	}

	@Override
	public void executeAction() {
		Client.getInstance().setState(State.MAIN_MENU);
		Client.getInstance().setClientName(Client.getInstance().getNameScreen().getContent());
	}

}
