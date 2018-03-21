package org.alien8.client;

import java.io.Serializable;
import org.alien8.physics.Position;

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
  public boolean escPressed = false;

  /**
   * Constructor
   */
  public ClientInputSample() {
    mousePosition = InputManager.getInstance().mousePosition();
    lmbPressed = InputManager.getInstance().lmbPressed();
    rmbPressed = InputManager.getInstance().rmbPressed();
    wPressed = InputManager.getInstance().wPressed();
    aPressed = InputManager.getInstance().aPressed();
    sPressed = InputManager.getInstance().sPressed();
    dPressed = InputManager.getInstance().dPressed();
    spacePressed = InputManager.getInstance().spacePressed();
    escPressed = InputManager.getInstance().escPressed();
  }

  /**
   * String representation of the client input sample
   */
  public String toString() {
    return mousePosition.getX() + "," + mousePosition.getY() + "," + lmbPressed + "," + rmbPressed
        + "," + wPressed + "," + aPressed + "," + sPressed + "," + dPressed + "," + spacePressed;
  }

}
