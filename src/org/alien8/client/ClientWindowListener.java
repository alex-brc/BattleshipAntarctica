package org.alien8.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import org.alien8.audio.AudioManager;
import org.alien8.util.LogManager;

/**
 * Window listener for the Jframe in client.
 * 
 */
public class ClientWindowListener implements WindowListener {

  /**
   * Perform a clean exit when the client closes the game window
   */
  @Override
  public void windowClosed(WindowEvent e) {
	  // Disconnect client
	  Launcher.getInstance().getRunningClient().disconnect();
	  // Shutdown audio clips
	  AudioManager.getInstance().shutDown();
	  // Log status
	  System.out.println("System exitted cleanly. Check log for crash information");
	  LogManager.getInstance().log("Shutdown", LogManager.Scope.INFO,
			  "Performed all tasks successfully. Cleanly exit.");
	  System.exit(0);
  }
  
  /**
   * Do ClientShutdownHook
   */
  @Override
  public void windowClosing(WindowEvent e) {
    System.exit(0);

  }

  /**
   * Not used
   */
  @Override
  public void windowOpened(WindowEvent e) {

  }

  /**
   * Not used
   */
  @Override
  public void windowIconified(WindowEvent e) {

  }

  /**
   * Not used
   */
  @Override
  public void windowDeiconified(WindowEvent e) {

  }

  /**
   * Not used
   */
  @Override
  public void windowActivated(WindowEvent e) {

  }

  /**
   * Not used
   */
  @Override
  public void windowDeactivated(WindowEvent e) {

  }

}
