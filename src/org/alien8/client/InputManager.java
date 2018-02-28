package org.alien8.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.alien8.client.ClientInputSample;
import org.alien8.audio.AudioManager;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.physics.Position;
import org.alien8.score.ScoreBoard;
import org.alien8.ship.Ship;

/**
 * This singleton class is a listener to be added to the main window. 
 * It adds all relevant input events to a ConcurrentLinkedQueue<> to be 
 * processed by the model.
 * 
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener {
	private static InputManager instance = new InputManager();
	
	// Synced
	private Position mousePosition = new Position(0,0);
	private boolean lmbPressed = false; // Shoot 1
	private boolean rmbPressed = false; // Shoot 2
	private boolean wPressed = false; // Move forward
	private boolean aPressed = false; // Turn left
	private boolean sPressed = false; // Slow down
	private boolean dPressed = false; // Turn right
	private boolean spacePressed = false; // Shoot 3

	// Not synced - local controls
	private boolean escPressed = false; // Pull up menu
	private boolean shiftPressed = false; // Show scoreboard
	private boolean mPressed = false; // Mute sounds
	
	private InputManager() {
		// Normally this exists only to defeat instantiation
	}
	
	/**
	 * A standard getInstance() in accordance with the singleton pattern
	 * 
	 * @return an instance of the active ModelManager
	 */
	public static InputManager getInstance() {
		return instance;
	}
	
	public static void processInputs(Ship player, ClientInputSample cis) {
        // Apply forward OR backward force
        if (cis.wPressed)
          PhysicsManager.applyForce(player, Parameters.SHIP_FORWARD_FORCE, player.getDirection());
        else if (cis.sPressed)
          PhysicsManager.applyForce(player, Parameters.SHIP_BACKWARD_FORCE,
              PhysicsManager.shiftAngle(player.getDirection() + Math.PI));

        // Apply rotation
        if (cis.aPressed)
          PhysicsManager.rotateEntity(player,
              (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
        if (cis.dPressed)
          PhysicsManager.rotateEntity(player,
              Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);

        // Apply "friction"
        PhysicsManager.applyFriction(player);

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

        if (cis.spacePressed)
          player.midTurretCharge();
        else
          player.midTurretShoot();

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
	 * @return the latest mouse position, in screen XY coordinates
	 */
	public Position mousePosition() {
		return mousePosition;
	}
	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePosition.setX(e.getX());
		mousePosition.setY(e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition.setX(e.getX());
		mousePosition.setY(e.getY());	
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
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

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
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

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W: 
			wPressed = true;
			return;
		case KeyEvent.VK_A: 
			aPressed = true;
			return;
		case KeyEvent.VK_S: 
			sPressed = true;
			return;
		case KeyEvent.VK_D: 
			dPressed = true;
			return;
		case KeyEvent.VK_SPACE: 
			spacePressed = true;
			return;
		// Local controls
		case KeyEvent.VK_ESCAPE: 
			escPressed = true;
			return;
		case KeyEvent.VK_SHIFT:
			shiftPressed = true;
			ScoreBoard.getInstance().notifyShift();
			return;
		default:
			// Not a game control
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W: 
			wPressed = false;
			return;
		case KeyEvent.VK_A: 
			aPressed = false;
			return;
		case KeyEvent.VK_S: 
			sPressed = false;
			return;
		case KeyEvent.VK_D: 
			dPressed = false;
			return;
		case KeyEvent.VK_SPACE: 
			spacePressed = false;
			return;
		// Local controls
		case KeyEvent.VK_ESCAPE: 
			escPressed = false;
			return;
		case KeyEvent.VK_SHIFT:
			shiftPressed = false;
			return;
		default:
			// Not a game control
			return;
		}		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Not interesting
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// Not interesting
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Not interesting
		
	}

	@Override
	public void keyTyped(KeyEvent e) {	
		// Not interesting
	}
}
