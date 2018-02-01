
package org.alien8.managers;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This singleton class is a listener to be added to the main window. 
 * It adds all relevant input events to a ConcurrentLinkedQueue<> to be 
 * processed by the model.
 * 
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener {
	private static InputManager instance = new InputManager();
	protected ConcurrentLinkedQueue<InputEvent> inputs = new ConcurrentLinkedQueue<InputEvent>();

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
	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		inputs.add(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		inputs.add(e);	
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		inputs.add(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		inputs.add(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		inputs.add(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		inputs.add(e);
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
