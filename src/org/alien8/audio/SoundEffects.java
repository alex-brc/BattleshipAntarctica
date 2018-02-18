package org.alien8.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum SoundEffects {
	SHIP_SHOOT("src/org/alien8/assets/cannon1.wav");
	
	public String fileName;
	public Clip clip;
	
	SoundEffects(String fileName) {
		this.fileName = fileName;
		try {
	         File file = new File(fileName);
	         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
	         clip = AudioSystem.getClip();
	         clip.open(audioInputStream);
		}
	    catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	    }
	    catch (IOException e) {
	         e.printStackTrace();
	    }
	    catch (LineUnavailableException e) {
	         e.printStackTrace();
	    }
	}
	/**
	 * Pre-loads all the sound files.
	 */
	public static void init() {
		// Calls constructor for all members
		values();
	}
}
