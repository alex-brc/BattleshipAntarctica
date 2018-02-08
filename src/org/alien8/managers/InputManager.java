
package org.alien8.managers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.alien8.physics.Position;

/**
 * This singleton class is a listener to be added to the main window. 
 * It adds all relevant input events to a ConcurrentLinkedQueue<> to be 
 * processed by the model.
 * 
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener {
	private static InputManager instance = new InputManager();
	
	private Position mousePosition = new Position(0,0);
	private boolean lmbPressed = false;
	private boolean rmbPressed = false;
	private boolean wPressed = false;
	private boolean aPressed = false;
	private boolean sPressed = false;
	private boolean dPressed = false;
	private boolean spacePressed = false;

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
	 * @return the latest mouse position, in XY coordinates
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
		
		switch(e.getKeyChar()) {
		case 'w': 
			wPressed = true;
			return;
		case 'a': 
			aPressed = true;
			return;
		case 's': 
			sPressed = true;
			return;
		case 'd': 
			dPressed = true;
			return;
		case ' ': 
			spacePressed = true;
			return;
		case 'W': 
			wPressed = true;
			return;
		case 'A': 
			aPressed = true;
			return;
		case 'S': 
			sPressed = true;
			return;
		case 'D': 
			dPressed = true;
			return;
		default:
			// Not a game control
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyChar()) {
		case 'w': 
			wPressed = false;
			return;
		case 'a': 
			aPressed = false;
			return;
		case 's': 
			sPressed = false;
			return;
		case 'd': 
			dPressed = false;
			return;
		case ' ': 
			spacePressed = false;
			return;
		case 'W': 
			wPressed = false;
			return;
		case 'A': 
			aPressed = false;
			return;
		case 'S': 
			sPressed = false;
			return;
		case 'D': 
			dPressed = false;
			return;
		default:
			// Not a game control
			return;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Not really interesting. Clutters the queue
		
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
