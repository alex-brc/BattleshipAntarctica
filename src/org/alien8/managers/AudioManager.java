package org.alien8.managers;

import java.util.LinkedList;
import java.util.Random;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import org.alien8.audio.SoundEffects;
import org.alien8.core.Parameters;
import org.alien8.rendering.Renderer;

/**
 * This audio manager is responsible for all game audio, handles
 * sound FX and also volume.
 * 
 * Supports volume control (in 10% steps on a linear scale) for 
 * ambient and SFX separately, as well as separate muting for each 
 * of them.
 * 
 * TODO: Other players' sounds
 * TODO: Change to Thread implementation
 * TODO: Distance-based volume for SFX
 */
public class AudioManager {
	public static final int AIRPLANE_PASS = 0;
	public static final int SFX_SHIP_SHOOT = 1;
	public static final int SFX_ICE_CRASH = 2;
	public static final int SFX_SHIP_CRASH = 3;
	public static final int SFX_PLANE_PASS = 4;
	
	private static AudioManager instance = null;
	private Random rand;
	private Clip ambient;
	
	private float ambientVolumeValue;
	private float sfxVolumeValue;
	private boolean ambientIsMuted = false;
	private boolean sfxIsMuted = false;
	private LinkedList<Clip> shoot1Pool;
	private LinkedList<Clip> shoot2Pool;
	private LinkedList<Clip> shoot3Pool;
	private LinkedList<Clip> iceCrashPool;
	private LinkedList<Clip> shipCrashPool;	
	
	private AudioManager() {
		LogManager.getInstance().log("Audio", LogManager.Scope.INFO, "Loading sound files...");
		try {
			rand = new Random();
			// Pre-load sound files into pools
			SoundEffects.init();
			shoot1Pool = new LinkedList<Clip>();
			shoot2Pool = new LinkedList<Clip>();
			shoot3Pool = new LinkedList<Clip>();
			shipCrashPool = new LinkedList<Clip>();
			iceCrashPool = new LinkedList<Clip>();

			// Pool shoot sound effects
			for(int i = 0; i < Parameters.SFX_POOL_SIZE; i++) {
				shoot1Pool.add(SoundEffects.makeClip(SoundEffects.SHIP_SHOOT_1));
				shoot2Pool.add(SoundEffects.makeClip(SoundEffects.SHIP_SHOOT_2));
				shoot3Pool.add(SoundEffects.makeClip(SoundEffects.SHIP_SHOOT_3));
			}
			sfxVolumeValue = Parameters.INITIAL_VOLUME_SFX;

			// Loads ambient sound
			ambient = SoundEffects.makeClip(SoundEffects.AMBIENT);
			ambientVolumeValue = Parameters.INITIAL_VOLUME_AMBIENT;
		}
		catch(Exception e) {
			LogManager.getInstance().log("Audio", LogManager.Scope.CRITICAL, "Could not load sound files: " + e.getMessage() + ". Exiting.");
			System.exit(-1);
		}
		
	}
	/**
	 * Call to cleanly close everything and prepare for exit.
	 * @return true if cleanly exited, false otherwise
	 */
	public boolean shutDown() {
		try {
			// Close SFX clips
			for(Clip clip : shoot1Pool) {
				clip.close();
			}
			for(Clip clip : shoot2Pool) {
				clip.close();
			}
			for(Clip clip : shoot3Pool) {
				clip.close();
			}
			// Stop and close ambient clips
			ambient.close();
		}
		catch(Exception e) {
			LogManager.getInstance().log("Audio", LogManager.Scope.ERROR, "Audio clips could not be closed: " + e.getMessage());
			return false;
		}
		LogManager.getInstance().log("Audio", LogManager.Scope.INFO, "Audio manager closed cleanly.");
		return true;
	}
	
	/**
	 * Starts the ambient sounds. 
	 */
	public void startAmbient() {
		setVolume(ambient, ambientVolumeValue);
		ambient.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public static AudioManager getInstance() {
		if(instance == null)
			instance = new AudioManager();
		return instance;
	}
	
	/**
	 * Plays the specified type of sound effect
	 * @param type the type of sounds to play, i.e. AudioManager.SFX_SHIP_SHOOT will shoot one of 3 shooting sounds at random
	 */
	public void playSound(int type) {
		if(type == SFX_SHIP_SHOOT) {
			int k = rand.nextInt(3);
			switch(k) {
			case 0: playSFX(shoot1Pool);
				return;
			case 1: playSFX(shoot2Pool);
				return;
			case 2: playSFX(shoot3Pool);
				return;
			}
		}
	}
	
	private void playSFX(LinkedList<Clip> shootPool) {
		// Get first clip, play it regardless of it's state
		Clip clip = shootPool.removeFirst();
		if(!sfxIsMuted)
			setVolume(clip, sfxVolumeValue);
		else
			setVolume(clip, 0.0f);
		
		if(clip.isRunning()) {
			clip.stop();
			clip.flush();
		}
		clip.setFramePosition(0);
		clip.start();
		
		// Add that clip to the end of the pool
		shootPool.addLast(clip);
	}
	
	// Render controls. TODO
	public void render(Renderer r) {
		
	}
	/**
	 * Mutes or unmutes the ambient sounds
	 * 
	 * @return returns the boolean representing the muted state of the ambient sounds after the operation (true if it muted, false otherwise).
	 */
	public boolean ambientMuteToggle() {
		if(!ambientIsMuted) {
			setVolume(ambient, 0.0f);
			ambientIsMuted = true;
			return true;
		}
		
		setVolume(ambient, ambientVolumeValue);
		ambientIsMuted = false;
		return false;
	}
	
	/**
	 *  Ambient volume goes in steps of 0.1 from 0.0f to 1.0f
	 */
	public void ambientDecreaseVolume() {
		if(ambientVolumeValue != 0.0f) {
			ambientVolumeValue -= 0.1f;
		    setVolume(ambient, ambientVolumeValue);
		}
	}
	
	/**
	 *  Ambient volume goes in steps of 0.1 from 0.0f to 1.0f
	 */
	public void ambientIncreaseVolume() {
		if(ambientVolumeValue != 1.0f) {
			ambientVolumeValue += 0.1f;	
			setVolume(ambient, ambientVolumeValue);
		}
	}
	
	/**
	 * Mutes or unmutes sound effects
	 * 
	 * @return returns the boolean representing the muted state of the sfx after the operation (true if it muted, false otherwise).
	 */
	public boolean sfxMuteToggle() {
		if(!sfxIsMuted) {
			sfxIsMuted = true;
			return true;
		}
		
		sfxIsMuted = false;
		return false;
	}
	/**
	 *  Sfx volume goes in steps of 0.1 from 0.0f to 1.0f
	 */
	public void sfxDecreaseVolume() {
		if(sfxVolumeValue != 0.0f)
			sfxVolumeValue -= 0.1f;
	}
	
	/**
	 *  Sfx volume goes in steps of 0.1 from 0.0f to 1.0f
	 */
	public void sfxIncreaseVolume() {
		if(sfxVolumeValue != 1.0f)
			sfxVolumeValue += 0.1f;
	}
	
	/**
	 * Computes a linear volume scale from the gain in decibels and returns the equivalent value
	 * 
	 * @param clip the clip to get the volume from
	 * @return a linear scale value for the volume (0 to 1)
	 */
	private float getVolume(Clip clip) {
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    return (float) Math.pow(10f, gainControl.getValue() / 20f);
	}
	
	/**
	 * Assigns a value for the gain in decibels from a linear scale input 
	 * 
	 * @param clip the clip to set the volume for
	 * @param volume the new volume for the clip (0 to 1)
	 */
	private void setVolume(Clip clip, float volume) {
		if (volume < 0.0f || volume > 1.0f)
	        return;
		
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    gainControl.setValue(20f * (float) Math.log10(volume));
	}
}
