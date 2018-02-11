package org.alien8.core;

/**
 * This class is meant to hold all the important <code> final </code> parameters for all classes to
 * easily access. If everyone uses these, we could technically just change these parameters, build,
 * run and have the game running at a different speed, maybe projectiles are faster, maybe
 * acceleration from input is more intense, etc. Essentially, this can be viewed as a universal
 * control panel. Change a parameter from here and the change ripples through the code.
 * <p>
 * Taking that into consideration, every tweak-able parameter and important constant should be
 * written in here.
 * 
 */
public class Parameters {

  /**
   * How often the model calls update() on the entities. Because of some divisions, it's a bit lower
   * than that. This value gives 58-60 ticks/second, hovering around 59
   */
  public static final int TICKS_PER_SECOND = 63;
  /**
   * Size of map hitboxes
   */
  public static final int MAP_BOX_SIZE = 4;
  /**
   * How many times a second to update the FPS tracker. Ideally, set to a divisor of e+9, for
   * simplicity.
   */
  public static final int INPUT_SAMPLING_RATE = 60;
  public static final int SNAPSHOTS_PER_SECOND = 60;
  public static final int LIST_LENGTH_PER_PACKET = 5000;
  public static final int FPS_FREQ = 4;
  public static final int N_SECOND = 1000000000;
  public static final int MAP_HEIGHT = 2048;
  public static final int MAP_WIDTH = 2048;
  /**
   * Length of the ship in units (the same units we use for the coordinate system) Currently, this
   * number doesn't mean much
   */
  public static final double SHIP_LENGTH = 100;
  public static final double SHIP_WIDTH = 25;
  /**
   * Interdependent stuff. Force required is computed according to how long it would take to reach
   * top speed
   */
  public static final double SHIP_TOP_SPEED_REACH_TIME = 200;
  public static final double SHIP_TOP_SPEED_FORWARD = 2;
  public static final double SHIP_TOP_SPEED_BACKWARD = 2;
  public static final double SHIP_MASS = 1000; // kg. This is kinda random
  public static final double SHIP_FORWARD_FORCE =
      SHIP_MASS * SHIP_TOP_SPEED_FORWARD / SHIP_TOP_SPEED_REACH_TIME; // N
  public static final double SHIP_BACKWARD_FORCE =
      SHIP_MASS * SHIP_TOP_SPEED_BACKWARD / SHIP_TOP_SPEED_REACH_TIME; // N
  public static final double SHIP_ROTATION_PER_SEC = Math.PI / 3;
  /**
   * Affects how much the speed impacts the turning rate.
   */
  public static final double ROTATION_MODIFIER = 1;
  public static final double FRICTION = 0.997;
  // Affects how much speed is lost when Entities collide
  public static final double RESTITUTION_COEFFICIENT = 1;
  /**
   * Bullet parameters
   */
  public static final double SMALL_BULLET_MASS = 10;
  public static final double SMALL_BULLET_WIDTH = 2;
  public static final double SMALL_BULLET_LENGTH = 4;
  public static final double SMALL_BULLET_SPEED = 4;
  public static final double SMALL_BULLET_DAMAGE = 10;
  // Bullet cooldown in milliseconds
  public static final int SMALL_BULLET_CD = 500;
  public static final int SMALL_BULLET_MIN_DIST = 50;
  public static final int SMALL_BULLET_MAX_DIST = 400;

  public static final double BIG_BULLET_MASS = 30;
  public static final double BIG_BULLET_WIDTH = 4;
  public static final double BIG_BULLET_LENGTH = 6;
  public static final double BIG_BULLET_SPEED = 2;
  public static final double BIG_BULLET_DAMAGE = 10;
  public static final int BIG_BULLET_CD = 2000;
  public static final int BIG_BULLET_MIN_DIST = 50;
  public static final int BIG_BULLET_MAX_DIST = 400;

  /**
   * This modifier affects how much distance holding down a button gives to the turret shot every
   * tick
   */
  public static final double CHARGE_INCREMENT = 4;
  /*
   * This modifier affects how much damage ships take in collisions.
   */
  public static final double COLLISION_DAMAGE_MODIFIER = 0.5;
  public static final double WATER_LEVEL = 0.4d;
}
