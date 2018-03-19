package org.alien8.audio;

import java.net.URL;

import javafx.scene.media.*;

/**
 * Wrapper for all long sound effects.
 * Implemented using the javafx.media package.
 *
 */
public enum Music {
	AMBIENT("/org/alien8/assets/cannon3.wav");
	
	private MediaPlayer media;
	
	private Music(String fileName) {
		URL url = SoundEffects.class.getResource(fileName);
		media = new MediaPlayer(new Media(url.toString()));
	}
	
	/**
	 * Pre-load all the sound files
	 */
	public static void init() {
		values();
	}
	
	/**
	 * Starts playing this sound
	 */
	public void start() {
		media.setCycleCount(MediaPlayer.INDEFINITE);
		media.play();
	}
	
	public void stop() {
		media.stop();
	}
	
	/**
	 * Sets the volume for this sound.
	 * The valid range is 0.0 to 1.0
	 * 
	 * @param volume volume to set for this sound
	 */
	public void setVolume(double volume) {
		media.setVolume(volume);
	}
	
	/**
	 * Sets the balance for this sound.
	 * The valid range is -1.0 to 1.0
	 * 
	 * @param balance balance to set for this sound
	 */
	public void setBalance(double balance) {
		media.setBalance(balance);
	}
	
	/**
	 * Closes this media player. 
	 */
	public void close() {
		media.dispose();
	}
	
	
}
