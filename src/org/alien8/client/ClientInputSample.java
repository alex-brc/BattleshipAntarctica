package org.alien8.client;

import java.io.Serializable;

import org.alien8.physics.Position;
import org.alien8.Managers.InputManager;

public class ClientInputSample implements Serializable {
	private static final long serialVersionUID = 8460850087080341787L;
	
	public Position mousePosition = null;
	public boolean lmbPressed = false;
	public boolean rmbPressed = false;
	public boolean wPressed = false;
	public boolean aPressed = false;
	public boolean sPressed = false;
	public boolean dPressed = false;
	public boolean spacePressed = false;

	
	public ClientInputSample() {
		mousePosition = InputManager.getInstance().mousePosition();
		rmbPressed = InputManager.getInstance().rmbPressed();
		wPressed = InputManager.getInstance().wPressed();
		aPressed = InputManager.getInstance().aPressed();
		sPressed = InputManager.getInstance().sPressed();
		dPressed = InputManager.getInstance().dPressed();
		spacePressed = InputManager.getInstance().spacePressed();
	}
}