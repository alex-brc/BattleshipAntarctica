package org.alien8.util;

import org.alien8.audio.AudioManager;
import org.alien8.client.Launcher;

/**
 * This shutdown hook handles cleanly exiting after
 * a System.exit() or unexpected closing of the client.
 */
public class ClientShutdownHook extends Thread {
	@Override
	public void run() {
		// Disconnect client
		Launcher.getInstance().getRunningClient().disconnect();
		// Shutdown audio clips
		AudioManager.getInstance().shutDown();
		// Log status
		System.out.println("System exitted succesfully. Check log for crash information");
		LogManager.getInstance().log("ClientShutdownHook", LogManager.Scope.INFO, "Hook performed all tasks succesfully. Cleanly exit.");
	}
}
