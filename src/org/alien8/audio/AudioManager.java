package org.alien8.audio;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.util.LogManager;

/**
 * This audio manager is responsible for all game audio, handles sound FX and also volume.
 * 
 * Supports volume control (in 10% steps on a linear scale) for ambient and SFX separately, as well
 * as separate muting for each of them.
 * 
 */
public class AudioManager implements Runnable {
  public static final int AIRPLANE_PASS = 0;
  public static final int SFX_SHIP_SHOOT = 1;
  public static final int SFX_ICE_CRASH = 2;
  public static final int SFX_SHIP_CRASH = 3;
  public static final int SFX_PLANE_PASS = 4;
  public double RANGE;
  public double RANGE_MIN;
  public double RANGE_MAX;

  private static AudioManager instance = null;
  private volatile boolean running;
  private Random rand;
  private Clip ambient;
  private ConcurrentLinkedQueue<AudioEvent> audioEvents;

  private double ambientVolumeValue;
  private double sfxVolumeValue;
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
      for (int i = 0; i < Parameters.SFX_POOL_SIZE; i++) {
        shoot1Pool.add(SoundEffects.makeClip(SoundEffects.SHIP_SHOOT_1));
        shoot2Pool.add(SoundEffects.makeClip(SoundEffects.SHIP_SHOOT_2));
        shoot3Pool.add(SoundEffects.makeClip(SoundEffects.SHIP_SHOOT_3));
      }
      sfxVolumeValue = Parameters.INITIAL_VOLUME_SFX;

      // Loads ambient sound
      ambient = SoundEffects.makeClip(SoundEffects.AMBIENT);
      ambientVolumeValue = Parameters.INITIAL_VOLUME_AMBIENT;

      // Initialise event queue
      audioEvents = new ConcurrentLinkedQueue<AudioEvent>();
      
      // Get gain control range
      FloatControl gainControl = (FloatControl) shoot1Pool.get(1).getControl(FloatControl.Type.MASTER_GAIN);
      RANGE = gainControl.getMaximum() - gainControl.getMinimum();
      RANGE_MIN = gainControl.getMinimum();
      RANGE_MAX = gainControl.getMaximum();
      
      // Start event listener thread
      running = true;
      (new Thread(this, "AudioManager")).start();
    } catch (Exception e) {
      LogManager.getInstance().log("Audio", LogManager.Scope.CRITICAL,
          "Could not load sound files: " + e.getMessage() + ". Exiting.");
      System.exit(-1);
    }

  }

  @Override
  public void run() {
    while (running) {
      AudioEvent event = audioEvents.poll();
      if (event != null)
        this.handleEvent(event);
    }
  }

  /**
   * Call to cleanly close everything and prepare for exit.
   * 
   * @return true if cleanly exited, false otherwise
   */
  public boolean shutDown() {
    // Stop run()
    this.running = false;
    // Kill daemons
    try {
      // Close SFX clips
      for (Clip clip : shoot1Pool) {
        clip.close();
      }
      for (Clip clip : shoot2Pool) {
        clip.close();
      }
      for (Clip clip : shoot3Pool) {
        clip.close();
      }
      // Stop and close ambient clips
      ambient.close();
    } catch (Exception e) {
      LogManager.getInstance().log("Audio", LogManager.Scope.ERROR,
          "Audio clips could not be closed: " + e.getMessage());
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
    if (instance == null)
      instance = new AudioManager();
    return instance;
  }

  /**
   * Plays the specified type of sound effect
   * 
   * @param type the type of sounds to play, i.e. AudioManager.SFX_SHIP_SHOOT will shoot one of 3
   *        shooting sounds at random
   */
  public void playSound(int type, Position position) {
    double dist = ModelManager.getInstance().getPlayer().getPosition().distanceTo(position);

    // Only play it if it's in hearing range
    if (dist > Parameters.MAX_HEARING_DISTANCE)
      return;

    double modifier = distanceVolumeFunction(dist);

    if (type == SFX_SHIP_SHOOT) {
      int k = rand.nextInt(3);
      switch (k) {
        case 0:
          playSFX(shoot1Pool, modifier);
          return;
        case 1:
          playSFX(shoot2Pool, modifier);
          return;
        case 2:
          playSFX(shoot3Pool, modifier);
          return;
      }
    }
  }

  private void playSFX(LinkedList<Clip> pool, double modifier) {
    // Get first clip, play it regardless of it's state
    Clip clip = pool.removeFirst();
    if (!sfxIsMuted)
      setVolume(clip, sfxVolumeValue * modifier);
    else
      setVolume(clip, 0.0f);

    if (clip.isRunning()) {
      clip.stop();
      clip.flush();
    }
    clip.setFramePosition(0);
    clip.start();

    // Add that clip to the end of the pool
    pool.addLast(clip);
  }

  // Render controls. TODO
  public void render() {

  }

  /**
   * Mutes or unmutes the ambient sounds
   * 
   * @return returns the boolean representing the muted state of the ambient sounds after the
   *         operation (true if it muted, false otherwise).
   */
  public boolean ambientMuteToggle() {
    if (!ambientIsMuted) {
      setVolume(ambient, 0.0f);
      ambientIsMuted = true;
      return true;
    }

    setVolume(ambient, ambientVolumeValue);
    ambientIsMuted = false;
    return false;
  }

  /**
   * Ambient volume goes in steps of 0.1 from 0.0f to 1.0f
   */
  public void ambientDecreaseVolume() {
    if (ambientVolumeValue != 0.0f) {
      ambientVolumeValue -= 0.1f;
      setVolume(ambient, ambientVolumeValue);
    }
  }

  /**
   * Ambient volume goes in steps of 0.1 from 0.0f to 1.0f
   */
  public void ambientIncreaseVolume() {
    if (ambientVolumeValue != 1.0f) {
      ambientVolumeValue += 0.1f;
      setVolume(ambient, ambientVolumeValue);
    }
  }

  /**
   * Mutes or unmutes sound effects
   * 
   * @return returns the boolean representing the muted state of the sfx after the operation (true
   *         if it muted, false otherwise).
   */
  public boolean sfxMuteToggle() {
    if (!sfxIsMuted) {
      sfxIsMuted = true;
      return true;
    }

    sfxIsMuted = false;
    return false;
  }

  /**
   * Sfx volume goes in steps of 0.1 from 0.0f to 1.0f
   */
  public void sfxDecreaseVolume() {
    if (sfxVolumeValue != 0.0f)
      sfxVolumeValue -= 0.1f;
  }

  /**
   * Sfx volume goes in steps of 0.1 from 0.0f to 1.0f
   */
  public void sfxIncreaseVolume() {
    if (sfxVolumeValue != 1.0f)
      sfxVolumeValue += 0.1f;
  }

  /**
   * Assigns a value for the gain in decibels from a linear scale input
   * 
   * @param clip the clip to set the volume for
   * @param volume the new volume for the clip (0 to 1)
   */
  private void setVolume(Clip clip, double volume) {
    if (volume < 0.0f || volume > 1.0f)
      return;
    
    float gain = (float) (RANGE * volume + RANGE_MIN);
    ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(gain);
    
  }

  public void addEvent(AudioEvent event) {
    audioEvents.add(event);
  }

  /**
   * Handles events from the audio event queue
   * 
   * @param event
   */
  private void handleEvent(AudioEvent event) {
    if (event.getType() == AudioEvent.Type.SHOOT)
      playSound(SFX_SHIP_SHOOT, event.getPosition());
    else if (event.getType() == AudioEvent.Type.SHIP_CRASH)
      playSound(SFX_SHIP_CRASH, event.getPosition());
    else if (event.getType() == AudioEvent.Type.ICE_CRASH)
      playSound(SFX_ICE_CRASH, event.getPosition());
  }

  /**
   * This function takes the distance to the sound source and returns the modifier value for volume,
   * making farther sounds quieter, and closer ones louder. Right now, the function is:
   * 
   * f(distance) = 1 - distance / MAX_HEARING_DISTANCE f : [0, MAX_HEARINGDISTANCE] -> [0, 1];
   * 
   * @param distance
   * @return the modifier for volume (between 0 and 1)
   */
  private double distanceVolumeFunction(double distance) {
    return (1 - (distance / Parameters.MAX_HEARING_DISTANCE));
  }
}
