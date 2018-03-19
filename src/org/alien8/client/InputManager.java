
package org.alien8.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.alien8.audio.AudioManager;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;
import net.jafama.FastMath;

/**
 * This singleton class is a listener to be added to the main window. It adds all relevant input
 * events to a ConcurrentLinkedQueue<> to be processed by the model.
 * 
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener {
  private static InputManager instance = new InputManager();

  // Synced
  private Position mousePosition = new Position(0, 0);
  private boolean lmbPressed = false; // Shoot 1
  private boolean rmbPressed = false; // Shoot 2
  private boolean wPressed = false; // Move forward
  private boolean aPressed = false; // Turn left
  private boolean sPressed = false; // Slow down
  private boolean dPressed = false; // Turn right
  private boolean spacePressed = false; // Use item
  private Position lastLmbClick = new Position(-1, -1); // For clicking menu buttons

  // Not synced - local controls
  private boolean escPressed = false; // Pull up menu
  private boolean shiftPressed = false; // Show scoreboard
  private boolean mPressed = false; // Mute sounds
  private boolean anyPressed = false; // Any key

  private char typed = 0; // most recently typed key

  /**
   * Constructor. Private to prevent global instantiation.
   */
  private InputManager() {
    // Normally this exists only to defeat instantiation
  }

  /**
   * A standard getInstance() in accordance with the singleton pattern.
   * 
   * @return an instance of the active ModelManager
   */
  public static InputManager getInstance() {
    return instance;
  }

  /**
   * Processes any inputs.
   * 
   * @param player the Player to process inputs for
   * @param cis a ClientInputSample containing inputs
   */
  public static void processInputs(Ship player, ClientInputSample cis) {
    // Apply forward OR backward force
    if (cis.wPressed)
      PhysicsManager.applyForce(player, Parameters.SHIP_FORWARD_FORCE, player.getDirection());
    else if (cis.sPressed)
      PhysicsManager.applyForce(player, Parameters.SHIP_BACKWARD_FORCE,
          PhysicsManager.shiftAngle(player.getDirection() + FastMath.PI));

    // Apply rotation
    if (cis.aPressed)
      PhysicsManager.rotateEntity(player,
          (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
    if (cis.dPressed)
      PhysicsManager.rotateEntity(player,
          Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);

    // Apply "friction"
    PhysicsManager.applyFriction(player);

    // Use item
    if (cis.spacePressed)
      player.useItem();

    // Prepare for shooting
    // Orientation
    player.setTurretsDirection(cis.mousePosition);

    if (cis.lmbPressed)
      player.frontTurretCharge();
    else
      player.frontTurretShoot();

    if (cis.rmbPressed)
      player.rearTurretCharge();
    else
      player.rearTurretShoot();
  }


  /**
   * @return true if left mouse button is pressed, false otherwise
   */
  public boolean lmbPressed() {
    return lmbPressed;
  }

  /**
   * @return true if right mouse button is pressed, false otherwise
   */
  public boolean rmbPressed() {
    return rmbPressed;
  }

  /**
   * @return true if W key is pressed, false otherwise
   */
  public boolean wPressed() {
    return wPressed;
  }

  /**
   * @return true if A key is pressed, false otherwise
   */
  public boolean aPressed() {
    return aPressed;
  }

  /**
   * @return true if S key is pressed, false otherwise
   */
  public boolean sPressed() {
    return sPressed;
  }

  /**
   * @return true if D key is pressed, false otherwise
   */
  public boolean dPressed() {
    return dPressed;
  }

  /**
   * @return true if SPACE key is pressed, false otherwise
   */
  public boolean spacePressed() {
    return spacePressed;
  }

  /**
   * @return the position of the last mouse click
   */
  public Position lastLmbClick() {
    return lastLmbClick;
  }

  /**
   * @return true if ESC key is pressed, false otherwise
   */
  public boolean escPressed() {
    return escPressed;
  }

  /**
   * @return true if TAB key is pressed, false otherwise
   */
  public boolean shiftPressed() {
    return shiftPressed;
  }

  /**
   * @return true if any key is pressed, false otherwise
   */
  public boolean anyPressed() {
    return anyPressed;
  }

  /**
   * Sets the correct variable if a key has been pressed.
   */
  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_W:
        wPressed = true;
        break;
      case KeyEvent.VK_A:
        aPressed = true;
        break;
      case KeyEvent.VK_S:
        sPressed = true;
        break;
      case KeyEvent.VK_D:
        dPressed = true;
        break;
      case KeyEvent.VK_SPACE:
        spacePressed = true;
        break;
      // Local controls
      case KeyEvent.VK_ESCAPE:
        escPressed = true;
        break;
      case KeyEvent.VK_SHIFT:
        shiftPressed = true;
        // ScoreBoard.getInstance().notifyShift();
        break;
      default:
        // Not a game control
        break;
    }
    anyPressed = true;
  }

  /**
   * Sets the correct variable if a key is released.
   */
  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_W:
        wPressed = false;
        break;
      case KeyEvent.VK_A:
        aPressed = false;
        break;
      case KeyEvent.VK_S:
        sPressed = false;
        break;
      case KeyEvent.VK_D:
        dPressed = false;
        break;
      case KeyEvent.VK_SPACE:
        spacePressed = false;
        break;
      // Local controls
      case KeyEvent.VK_ESCAPE:
        escPressed = false;
        break;
      case KeyEvent.VK_SHIFT:
        shiftPressed = false;
        break;
      default:
        // Not a game control
        break;
    }
    anyPressed = false;
  }

  /**
   * @return the key which has been pressed in a text field
   */
  public char getKeyTyped() {
    char c = typed;
    typed = 0;
    return c;
  }

  /**
   * Deals with a key being pressed in a text field.
   */
  @Override
  public void keyTyped(KeyEvent e) {
    if (e.getKeyCode() != KeyEvent.VK_ENTER) {
      typed = e.getKeyChar();
    }
    // Mute all sounds with M
    if (e.getKeyCode() == KeyEvent.VK_M) {
      AudioManager.getInstance().ambientMuteToggle();
      AudioManager.getInstance().sfxMuteToggle();
    }
  }

  /**
   * Resets the last click position.
   */
  public void resetLastLmbClick() {
    lastLmbClick.setX(-1);
    lastLmbClick.setY(-1);
  }

  /**
   * @return the latest mouse position, in screen XY coordinates
   */
  public Position mousePosition() {
    return mousePosition;
  }

  /**
   * Updates the mouse position when the mouse is dragged.
   */
  @Override
  public void mouseDragged(MouseEvent e) {
    mousePosition.setX(e.getX());
    mousePosition.setY(e.getY());
  }

  /**
   * Updates the mouse position when the mouse is moved.
   */
  @Override
  public void mouseMoved(MouseEvent e) {
    mousePosition.setX(e.getX());
    mousePosition.setY(e.getY());
  }

  /**
   * Updates the mouse variables when the mouse is clicked.
   */
  @Override
  public void mousePressed(MouseEvent e) {
    switch (e.getButton()) {
      case 1: // LMB
        lmbPressed = true;
        return;
      case 3: // RMB
        rmbPressed = true;
        return;
      default:
        // Not a game control
        return;
    }
  }

  /**
   * Updates the mouse variables when the mouse is released.
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    switch (e.getButton()) {
      case 1: // LMB
        lmbPressed = false;
        return;
      case 3: // RMB
        rmbPressed = false;
        return;
      default:
        // Not a game control
        return;
    }
  }

  /**
   * Updates the position of the last mouse click.
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
      this.lastLmbClick = new Position(e.getPoint().getX(), e.getPoint().getY());
    }
  }

  /**
   * Unused.
   */
  @Override
  public void mouseEntered(MouseEvent e) {
    // Not interesting
  }

  /**
   * Unused.
   */
  @Override
  public void mouseExited(MouseEvent e) {
    // Not interesting
  }
}
