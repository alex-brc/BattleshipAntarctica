package org.alien8.managers;

import org.alien8.audio.SoundEffects;

/**
 * This audio manager ensures a background song is always playing, handles
 * sound FX and also volume.  
 * 
 */
public class AudioManager {
	public String musicFile = "org/alien8/assets/bg1.wav";
	public static AudioManager instance;
	
	private AudioManager() {
		// Pre-load sound files
		SoundEffects.init();
	}
	
	public static AudioManager getInstance() {
		instance = new AudioManager();
		return instance;
	}
	
	public void startMusic() {
		//TODO
	}
	
	public void pauseMusic() {
		//TODO
	}
	
	public void stopMusic() {
		//TODO
	}
	
	/**
	 * Plays the specified sound effect
	 * @param sfx the SoundEffect to play
	 */
	public void playFX(SoundEffects sfx) {
		if(sfx.clip.isRunning())
			sfx.clip.stop();
		sfx.clip.setFramePosition(0); // rewind to the beginning
        sfx.clip.start();
	}
	
}
