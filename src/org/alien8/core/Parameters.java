package org.alien8.core;

import java.awt.Dimension;
import net.jafama.FastMath;

/**
 * This class holds all the important <code> final</code> parameters for all classes to easily
 * access. If everyone uses these, we could technically just change these parameters, build, run and
 * have the game running at a different speed, maybe projectiles are faster, maybe acceleration from
 * input is more intense, etc. Essentially, this can be viewed as a universal control panel. Change
 * a parameter from here and the change ripples through the code.
 * <p>
 * Taking that into consideration, every tweak-able parameter and important constant should be
 * written in here.
 * 
 */
public class Parameters {

  /// GENERIC GAME PARAMETERS
  public static final boolean RENDER_BOX = true;
  public static final boolean ICE_IS_SOLID = true;
  public static final int MAX_PLAYERS = 16;
  public static final int MATCH_LENGTH = 300; // in seconds
  public static final int TIME_BEFORE_SERVER_END = 10; // in seconds
  public static final boolean AI_ON = true;
  public static final boolean DEBUG_MODE = false;

  /// SERVER PARAMETERS
  /**
   * How many times to attempt connection to server before giving up. Keep in mind the timeout is
   * pretty long itself.
   */
  public static final int NUMBER_CONNECT_ATTEMPTS = 3;
  /**
   * How often the model calls update() on the entities. It's actually a bit lower than that.
   */
  public static final int TICKS_PER_SECOND = 80;
  public static final int SERVER_PORT = 4446;
  public static final int MULTI_CAST_PORT = 4445;
  public static final int SERVER_SOCKET_BLOCK_TIME = 100;
  public static final int LIST_LENGTH_PER_PACKET = 5000;
  public static final int FPS_FREQ = 1;
  public static final int N_SECOND = 1000000000;
  public static final int M_SECOND = 1000;
  //////////////////////////////////////////////////////////


  /// RENDERER PARAMETERS
  /**
   * Dimension object for the renderer dimensions
   */
  public static final Dimension RENDERER_SIZE = new Dimension(800, 600);
  // public static final Dimension VIEWPORT_SIZE = new Dimension(600, 400);
  public static final int SMALL_BORDER = 16;
  public static final int BIG_BORDER = 96;
  public static final int MINIMAP_WIDTH = 64;
  public static final int MINIMAP_HEIGHT = 64;
  public static final int GAME_PARALLAX_WEIGHT = 35;
  public static final int MENU_PARALLAX_WEIGHT = 100;

  //////////////////////////////////////////////////////////


  /// MAP PARAMETERS
  public static final int MAP_HEIGHT = 2048;
  public static final int MAP_WIDTH = 2048;
  public static final double WATER_LEVEL = 0.4d;
  //////////////////////////////////////////////////////////


  /// SHIP PARAMETERS
  public static final double SHIP_LENGTH = 100;
  public static final double SHIP_WIDTH = 25;
  public static final double SHIP_HEALTH = 100;
  public static final double SHIP_TOP_SPEED_REACH_TIME = 200;
  public static final double SHIP_TOP_SPEED_FORWARD = 2;
  public static final double SHIP_TOP_SPEED_BACKWARD = 2;
  public static final double SHIP_MASS = 1000;
  public static final double SHIP_FORWARD_FORCE =
      SHIP_MASS * SHIP_TOP_SPEED_FORWARD / SHIP_TOP_SPEED_REACH_TIME;
  public static final double SHIP_BACKWARD_FORCE =
      SHIP_MASS * SHIP_TOP_SPEED_BACKWARD / SHIP_TOP_SPEED_REACH_TIME;
  public static final double SHIP_ROTATION_PER_SEC = FastMath.PI / 3;
  //////////////////////////////////////////////////////////


  /// BULLET PARAMETERS
  /**
   * Bullet parameters. Cooldowns in milliseconds.
   */
  public static final double BULLET_MASS = 10;
  public static final double BULLET_WIDTH = 4;
  public static final double BULLET_LENGTH = 8;
  public static final double BULLET_SPEED = 4;
  public static final double BULLET_DAMAGE = 10;
  public static final int BULLET_POOL_SIZE = 50;
  public static final int TURRET_CD = 1000;
  public static final int TURRET_MIN_DIST = 0;
  public static final int TURRET_MAX_DIST = 400;
  /**
   * This modifier affects how much distance holding down a button gives to the turret shot every
   * tick
   */
  public static final double CHARGE_INCREMENT = 4;
  //////////////////////////////////////////////////////////


  /// PHYSICS PARAMETERS
  /**
   * Affects how much damage ships take in collisions.
   */
  public static final double COLLISION_DAMAGE_MODIFIER = 0.01;
  public static final double COLLISION_ROTATION_MODIFIER = 0.2;
  /**
   * Affects how much the speed impacts the turning rate.
   */
  public static final double ROTATION_MODIFIER = 1;
  public static final double FRICTION = 0.997;
  /**
   * Affects how much speed is lost when Entities collide
   */
  public static final double RESTITUTION_COEFFICIENT = 0.5;
  /**
   * Affects how 'bouncy' ice is when collided with.
   */
  public static final double ICE_BOUNCINESS = 0.005;
  public static final double OUT_OF_BOUNDS_PUSHBACK = 10;
  public static final double OUT_OF_BOUNDS_BOUNCINESS = 0.02;
  //////////////////////////////////////////////////////////


  /// AUDIO PARAMETERS
  /**
   * Maximum number of "shoot" audio clips running at the same time
   */
  public static final int SFX_POOL_SIZE = 5;
  public static final float INITIAL_VOLUME_SFX = 0.8f;
  public static final float INITIAL_VOLUME_AMBIENT = 0.8f;
  public static final int MAX_HEARING_DISTANCE = 1500;
  //////////////////////////////////////////////////////////


  /// SCORE PARAMETERS
  public static final int SCORE_PER_KILL = 1000;
  public static final float KILL_STREAK_MULTIPLIER = 0.1f;
  public static final float DISTANCE_MULTIPLIER = 0.1f;
  public static final int SCOREBOARD_HEIGHT = 450;
  public static final int SCOREBOARD_WIDTH = 550;
  //////////////////////////////////////////////////////////


  /// ITEMS PARAMETERS
  public static final double PLANE_SPEED = 3;
  public static final double ITEM_WIDTH = 32;
  public static final double ITEM_LENGTH = 32;
  public static final double ITEM_HEALTH_ITEM_VALUE = 25;
  public static final int ITEM_SPEED_ITEM_DURATION = 2; // in seconds
  public static final int ITEM_SPEED_ITEM_MULTIPLIER = 2; // ship top speed multiplied by this
  public static final int ITEM_NO_COOLDOWN_ITEM_DURATION = 5; // in seconds
  public static final int ITEM_INVULNERABLE_ITEM_DURATION = 5; // in seconds
  public static final int MINE_WIDTH = 32;
  public static final int MINE_LENGTH = 32;
  public static final int MINE_DAMAGE = 50;
  public static final int MINE_SCORE = 100;
  public static final int TORPEDO_LENGTH = 32;
  public static final int TORPEDO_WIDTH = 16;
  public static final int TORPEDO_DAMAGE = 30;
  public static final int TORPEDO_SPEED = 5;
  public static final int TORPEDO_SCORE = 100;
  //////////////////////////////////////////////////////////

  /// TEST PARAMETERS
  public static final double DOUBLE_PRECISION = 0.001;

  //////////////////////////////////////////////////////////

  /// UNUSED PARAMETERS
  /**
   * How many ice pixels must be in a box to be considered an ice entity (%). This is in [0,1]
   */
  public static final double ICE_BOX_DENSITY = 0.7;
  //////////////////////////////////////////////////////////
}
