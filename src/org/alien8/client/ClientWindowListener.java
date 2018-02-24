package org.alien8.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
/**
 * Window listener for the Jframe in client.
 * 
 */
public class ClientWindowListener implements WindowListener {
	
	@Override
	public void windowClosed(WindowEvent e) {
		// Disconnect client
		Launcher.getInstance().getRunningClient().disconnect();
		// Do other stuff maybe
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
