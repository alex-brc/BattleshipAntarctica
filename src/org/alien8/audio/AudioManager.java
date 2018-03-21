package org.alien8.audio;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import org.alien8.core.ClientModelManager;
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
  public static final int SFX = -99;
  public static final int AMBIENT = -98;

  public static final int SFX_SHIP_SHOOT = 1;
  public double RANGE;
  public double RANGE_MIN;
  public double RANGE_MAX;

  private static AudioManager instance = null;
  private volatile boolean running;
  private Random rand;
  private Clip ambientMenu;
  private Clip ambientInGame;
  private ConcurrentLinkedQueue<AudioEvent> audioEvents;

  private double ambientVolumeValue;
  private double sfxVolumeValue;
  private boolean ambientIsMuted = false;
  private boolean sfxIsMuted = false;
  private LinkedList<Clip> shoot1Pool;
  private LinkedList<Clip> shoot2Pool;
  private LinkedList<Clip> shoot3Pool;

  /**
   * Constructor. Private to prevent instantiation.
   */
  private AudioManager() {
    LogManager.getInstance().log("Audio", LogManager.Scope.INFO, "Loading sound files...");
    try {
      rand = new Random();
      // Pre-load sound files into pools
      SoundEffects.init();
      shoot1Pool = new LinkedList<Clip>();
      shoot2Pool = new LinkedList<Clip>();
      shoot3Pool = new LinkedList<Clip>();

      // Pool shoot sound effects
      for (int i = 0; i < Parameters.SFX_POOL_SIZE; i++) {
        shoot1Pool.add(SoundEffects.SHIP_SHOOT_1.makeClip());
        shoot2Pool.add(SoundEffects.SHIP_SHOOT_2.makeClip());
        shoot3Pool.add(SoundEffects.SHIP_SHOOT_3.makeClip());
      }
      sfxVolumeValue = Parameters.INITIAL_VOLUME_SFX;

      // Loads ambient sound
      ambientMenu = SoundEffects.MENU_MUSIC.makeClip();
      ambientInGame = SoundEffects.INGAME_MUSIC.makeClip();
      ambientVolumeValue = Parameters.INITIAL_VOLUME_AMBIENT;

      // Initialise event queue
      audioEvents = new ConcurrentLinkedQueue<AudioEvent>();

      // Get gain control range
      FloatControl gainControl =
          (FloatControl) shoot1Pool.get(1).getControl(FloatControl.Type.MASTER_GAIN);
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

  /**
   * @return the singleton instance of this AudioManager class
   */
  public static AudioManager getInstance() {
    if (instance == null)
      instance = new AudioManager();
    return instance;
  }

  /**
   * Starts the AudioManager thread.
   */
  @Override
  public void run() {
    while (running) {
      AudioEvent event = audioEvents.poll();
      if (event != null)
        this.handleEvent(event);
    }
  }

  /**
   * @return the volume of ambient sound
   */
  public double getAmbientVolumeValue() {
    return ambientVolumeValue;
  }

  /**
   * Starts the ambient sound.
   */
  public void startAmbient(int type) {
    switch (type) {
      case 0:
        setVolume(ambientMenu, ambientVolumeValue);
        ambientMenu.loop(Clip.LOOP_CONTINUOUSLY);
        break;
      case 1:
        setVolume(ambientInGame, ambientVolumeValue);
        ambientInGame.loop(Clip.LOOP_CONTINUOUSLY);
        break;
      default:
        break;
    }
  }

  /**
   * Stops the ambient sound.
   */
  public void stopAmbient(int type) {
    switch (type) {
      case 0:
        ambientMenu.stop();
        ambientMenu.flush();
        ambientMenu.setFramePosition(0);
        break;
      case 1:
        ambientInGame.stop();
        ambientInGame.flush();
        ambientInGame.setFramePosition(0);
        break;
      default:
        break;
    }

  }

  /**
   * Mutes or unmutes the ambient sound.
   * 
   * @return returns the boolean representing the muted state of the ambient sounds after the
   *         operation ({@code true} if muted, {@code false} otherwise).
   */
  public boolean ambientMuteToggle() {
    if (!ambientIsMuted) {
      setVolume(ambientMenu, 0.0f);
      setVolume(ambientInGame, 0.0f);
      ambientIsMuted = true;
      return true;
    }

    setVolume(ambientMenu, ambientVolumeValue);
    setVolume(ambientInGame, ambientVolumeValue);
    ambientIsMuted = false;
    return false;
  }

  /**
   * Increases the volume of ambient sound, going in steps of 0.1 from 0.0f to 1.0f.
   */
  public void ambientIncreaseVolume() {
    ambientVolumeValue += 0.1f;
    if (ambientVolumeValue > 1)
      ambientVolumeValue = 1f;
    setVolume(ambientMenu, ambientVolumeValue);
    setVolume(ambientInGame, ambientVolumeValue);
  }

  /**
   * Decreases the volume of ambient sound, going in steps of 0.1 from 0.0f to 1.0f.
   */
  public void ambientDecreaseVolume() {
    ambientVolumeValue -= 0.1f;
    if (ambientVolumeValue < 0)
      ambientVolumeValue = 0f;
    setVolume(ambientMenu, ambientVolumeValue);
    setVolume(ambientInGame, ambientVolumeValue);
  }

  /**
   * @return the volume of sound effects
   */
  public double getSfxVolumeValue() {
    return sfxVolumeValue;
  }

  /**
   * Mutes or unmutes sound effects.
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
   * Increases the volume of sound effects, going in steps of 0.1 from 0.0f to 1.0f.
   */
  public void sfxIncreaseVolume() {
    sfxVolumeValue += 0.1f;
    if (sfxVolumeValue > 1)
      sfxVolumeValue = 1f;
  }

  /**
   * Decreases the volume of sound effects, going in steps of 0.1 from 0.0f to 1.0f.
   */
  public void sfxDecreaseVolume() {
    sfxVolumeValue -= 0.1f;
    if (sfxVolumeValue < 0)
      sfxVolumeValue = 0f;
  }

  /**
   * Assigns a value for the gain in decibels from a linear scale input.
   * 
   * @param clip the clip to set the volume for
   * @param volume the new volume for the clip (0 to 1)
   */
  private void setVolume(Clip clip, double volume) {
    float gain = (float) (RANGE * volume + RANGE_MIN);
    ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(gain);
  }

  /**
   * Takes the distance to the sound source and returns the modifier value for volume, making
   * farther sounds quieter, and closer ones louder. Right now, the function is:
   * 
   * f(distance) = 1 - distance / MAX_HEARING_DISTANCE f : [0, MAX_HEARINGDISTANCE] -> [0, 1];
   * 
   * @param distance the distance of the sound source to the listener
   * @return the modifier for volume (between 0 and 1)
   */
  private double distanceVolumeFunction(double distance) {
    return (1 - (distance / Parameters.MAX_HEARING_DISTANCE));
  }

  /**
   * Cleanly closes everything and prepares for exit.
   * 
   * @return {@code true} if cleanly exited, {@code false} otherwise
   */
  public boolean shutDown() {
    // Stop run()
    this.running = false;
    // Kill daemons
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
    ambientMenu.close();
    ambientInGame.close();

    LogManager.getInstance().log("Audio", LogManager.Scope.INFO, "Audio manager closed cleanly.");
    return true;
  }

  /**
   * Plays the specified type of sound effect.
   * 
   * @param type the type of sounds to play, i.e. AudioManager.SFX_SHIP_SHOOT will shoot one of 3
   *        shooting sounds at random
   */
  private void playSound(int type, Position position) {
    double dist = ClientModelManager.getInstance().getPlayer().getPosition().distanceTo(position);

    // Only play it if it's in hearing range
    if (dist > Parameters.MAX_HEARING_DISTANCE)
      return;

    double modifier = distanceVolumeFunction(dist);

    if (type == SFX_SHIP_SHOOT) {
      int k = rand.nextInt(1000);
      switch (k%3) {
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

  /**
   * Plays a sound effect from the pool.
   * 
   * @param pool the pool of sound effects to play from
   * @param modifier a modifier to affect the clip volume based on distance
   */
  private void playSFX(LinkedList<Clip> pool, double modifier) {
    // Get first clip, play it regardless of it's state
    Clip clip = pool.removeFirst();
    if (!sfxIsMuted)
      setVolume(clip, sfxVolumeValue * modifier);
    else
      setVolume(clip, 0.0f);
    
    clip.setFramePosition(0);
    clip.start();

    // Add that clip to the end of the pool
    pool.addLast(clip);
  }

  /**
   * Adds an AudioEvent to a ConcurrentLinkedQueue.
   * 
   * @param event the AudioEvent to add
   */
  public void addEvent(AudioEvent event) {
    audioEvents.add(event);
  }

  /**
   * Handles an event from the audio event queue.
   * 
   * @param event the AudioEvent to handle
   */
  private void handleEvent(AudioEvent event) {
    if (event.getType() == AudioEvent.Type.SHOOT)
      playSound(SFX_SHIP_SHOOT, event.getPosition());
  }

}
