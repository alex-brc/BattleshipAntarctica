package org.alien8.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import org.alien8.audio.AudioManager;
import org.alien8.score.ScoreBoard;
import org.alien8.util.LogManager;

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
	  // Kill scoreboard listener
	  ScoreBoard.getInstance().killListener();
	  // Log status
	  System.out.println("System exitted cleanly. Check log for crash information");
	  LogManager.getInstance().log("Shutdown", LogManager.Scope.INFO,
			  "Performed all tasks successfully. Cleanly exit.");
	  System.exit(0);
  }

  @Override
  public void windowOpened(WindowEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowClosing(WindowEvent e) {
	// Do ClientShutdownHook
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
