package org.alien8.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.alien8.audio.AudioManager;
import org.alien8.score.ScoreBoard;
/**
 * Window listener for the Jframe in client.
 * 
 */
public class ClientWindowListener implements WindowListener {
	
	@Override
	public void windowClosed(WindowEvent e) {
		// Disconnect client
		Launcher.getInstance().getRunningClient().disconnect();
		// Shutdown audio clips
		AudioManager.getInstance().shutDown();
		// Tear down ScoreBoard
		ScoreBoard.getInstance().killListener();
		// Do other stuff maybe
		
		// Log status
		System.out.println("System exit in a controlled fashion. Check log for crash information");
		System.exit(0);
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// Disconnect client
		Launcher.getInstance().getRunningClient().disconnect();
		// Shutdown audio clips
		AudioManager.getInstance().shutDown();
		// Tear down ScoreBoard
		ScoreBoard.getInstance().killListener();
		// Do other stuff maybe
		
		// Log status
		System.out.println("System exit in a controlled fashion. Check log for crash information");
		System.exit(0);
		
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
