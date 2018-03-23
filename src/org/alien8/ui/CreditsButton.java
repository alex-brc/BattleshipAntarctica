package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class CreditsButton extends Button {

	public CreditsButton(int x, int y, int width, int height) {
		super(x, y, width, height, "credits");
	}

	/**
	 * Sets the client state to show the credits screen
	 */
	@Override
	public void executeAction() {
		Client.getInstance().setState(State.CREDITS_SCREEN);
	}

}
