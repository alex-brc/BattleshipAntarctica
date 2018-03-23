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
  /** Toggles rendering the box. */
  public static final boolean RENDER_BOX = true;
  /** Determines if ice can be collided with or not. */
  public static final boolean ICE_IS_SOLID = true;
  /** The maximum number of players allowed in a match. */
  public static final int MAX_PLAYERS = 8;
  /** The length of a match in seconds. */
  public static final int MATCH_LENGTH = 60 * 3;
  /** The length of time in seconds before a server ends once a game has finished. */
  public static final int TIME_BEFORE_SERVER_END = 10;
  /** Toggles certain features, such as visible bounding boxes. */
  public static final boolean DEBUG_MODE = false;

  /// SERVER PARAMETERS
  /**
   * How many times to attempt connection to server before giving up. Keep in mind the timeout is
   * pretty long itself.
   */
  public static final int NUMBER_CONNECT_ATTEMPTS = 3;
  /** How often the model calls update() on the entities. It's actually a bit lower than that. */
  public static final int TICKS_PER_SECOND = 80;
  /** Port the server runs on. */
  public static final int SERVER_PORT = 4446;
  /** Port for multicasting. */
  public static final int MULTI_CAST_PORT = 4445;
  /** How long the server UDP socket blocks for waiting client's input sample. */
  public static final int SERVER_SOCKET_BLOCK_TIME = 100;
  /** How much nanosecond in 1 second. */
  public static final int N_SECOND = 1000000000;
  /** How much millisecond in 1 second. */
  public static final int M_SECOND = 1000;
  //////////////////////////////////////////////////////////


  /// RENDERER PARAMETERS
  /** Dimension object for the renderer dimensions. */
  public static final Dimension RENDERER_SIZE = new Dimension(800, 600);
  /** The size of a small border of the HUD. */
  public static final int SMALL_BORDER = 16;
  /** The size of a big border of the HUD. */
  public static final int BIG_BORDER = 96;
  /** Width of the minimap on the HUD. */
  public static final int MINIMAP_WIDTH = 64;
  /** Height of the minimap on the HUD. */
  public static final int MINIMAP_HEIGHT = 64;
  /** Affects how the game view moves when the mouse is aimed around the screen. */
  public static final int GAME_PARALLAX_WEIGHT = 15;
  /** Affects how much the game logo moves in menus when the mouse is aimed around the screen. */
  public static final int MENU_PARALLAX_WEIGHT = 100;
  /** Width of the health bar displayed for each player. */
  public static final int HEALTH_BAR_WIDTH = 50;
  /** Height of the health bar displayed for each player. */
  public static final int HEALTH_BAR_HEIGHT = 12;
  //////////////////////////////////////////////////////////


  /// MAP PARAMETERS
  /** Height of the map. */
  public static final int MAP_HEIGHT = 2048;
  /** Width of the map. */
  public static final int MAP_WIDTH = 2048;
  /** Level of deep water on the map. */
  public static final double DEEP_WATER_LEVEL = 0.55d;
  /** Level of shallow water. */
  public static final double SHALLOW_WATER_LEVEL = 0.61d;
  /** Level of thin ice. */
  public static final double THIN_ICE_LEVEL = 0.62d;
  /** Colour of deep water. */
  public static final int DEEP_WATER_COLOR = 0x5555ff;
  /** Colour of shallow water. */
  public static final int SHALLOW_WATER_COLOR = 0x9999FF;
  /** Colour of thin ice. */
  public static final int THIN_ICE_COLOR = 0xEAEAFF;
  /** Colour of thick ice. */
  public static final int THICK_ICE_COLOR = 0xF5FFFA;
  //////////////////////////////////////////////////////////


  /// SHIP PARAMETERS
  /** Length of a Ship. */
  public static final double SHIP_LENGTH = 100;
  /** Width of a Ship. */
  public static final double SHIP_WIDTH = 25;
  /** Maximum health of a Ship. */
  public static final double SHIP_HEALTH = 100;
  /** Time take for a Ship to reach top speed. */
  public static final double SHIP_TOP_SPEED_REACH_TIME = 200;
  /** Top forward speed of a Ship. */
  public static final double SHIP_TOP_SPEED_FORWARD = 2;
  /** Top backward speed of a Ship. */
  public static final double SHIP_TOP_SPEED_BACKWARD = 2;
  /** Mass of a Ship. */
  public static final double SHIP_MASS = 1000;
  /** Force applied to a Ship when moving forwards. */
  public static final double SHIP_FORWARD_FORCE =
      SHIP_MASS * SHIP_TOP_SPEED_FORWARD / SHIP_TOP_SPEED_REACH_TIME;
  /** Force applied to a Ship when moving backwards. */
  public static final double SHIP_BACKWARD_FORCE =
      SHIP_MASS * SHIP_TOP_SPEED_BACKWARD / SHIP_TOP_SPEED_REACH_TIME;
  /** Amount rotated by a Ship in radians per second. */
  public static final double SHIP_ROTATION_PER_SEC = FastMath.PI / 3;
  //////////////////////////////////////////////////////////

  /// AI PARAMETERS
  /** Determines if AI ships spawn on the map in a game. */
  public static final boolean AI_ON = true;
  /** The evenly distributed error for AI aiming in radians */
  public static final double AI_PRECISION = 0.5;
  //////////////////////////////////////////////////////////

  /// BULLET PARAMETERS
  // Bullet parameters. Cooldowns in milliseconds.
  /** Mass of a Bullet. */
  public static final double BULLET_MASS = 10;
  /** Width of a Bullet. */
  public static final double BULLET_WIDTH = 4;
  /** Length of a Bullet. */
  public static final double BULLET_LENGTH = 8;
  /** Speed of a Bullet. */
  public static final double BULLET_SPEED = 8;
  /** Damage dealt by a Bullet. */
  public static final double BULLET_DAMAGE = 10;
  /** Size of the Bullet pool on the server. */
  public static final int BULLET_POOL_SIZE = 50;
  public static final int TURRET_CD = 1000;
  /** Minimum distance a Turret is allowed to fire. */
  public static final int TURRET_MIN_DIST = 0;
  /** Maximum distance a Turret is allowed to fire. */
  public static final int TURRET_MAX_DIST = 400;
  /** Affects how much distance holding down a button gives to the turret shot every tick. */
  public static final double CHARGE_INCREMENT = 4;
  //////////////////////////////////////////////////////////


  /// PHYSICS PARAMETERS
  /** Affects how much damage ships take in collisions. */
  public static final double COLLISION_DAMAGE_MODIFIER = 0.0005;
  /** Affects how much Ships rotate in collisions with each other. */
  public static final double COLLISION_ROTATION_MODIFIER = 0.2;
  /** Affects how much the speed impacts the turning rate. */
  public static final double ROTATION_MODIFIER = 1;
  /** Friction modifier affecting speed loss. */
  public static final double FRICTION = 0.997;
  /** Affects how much speed is lost when Entities collide. */
  public static final double RESTITUTION_COEFFICIENT = 0.5;
  /** Affects how 'bouncy' ice is when collided with. */
  public static final double ICE_BOUNCINESS = 0.005;
  //////////////////////////////////////////////////////////


  /// AUDIO PARAMETERS
  /** Maximum number of "shoot" audio clips running at the same time. */
  public static final int SFX_POOL_SIZE = 3;
  /** Initial volume of sound effects. */
  public static final float INITIAL_VOLUME_SFX = 0.7f;
  /** Initial volume of ambient sound. */
  public static final float INITIAL_VOLUME_AMBIENT = 0.7f;
  /** Maximum distance sound can be heard from. */
  public static final int MAX_HEARING_DISTANCE = 1500;
  //////////////////////////////////////////////////////////


  /// SCORE PARAMETERS
  /** Amount of points awarded for every kill. */
  public static final int SCORE_PER_KILL = 1000;
  public static final float KILL_STREAK_MULTIPLIER = 0.1f;
  public static final float DISTANCE_MULTIPLIER = 0.1f;
  /** Height of scoreboard in pixels. */
  public static final int SCOREBOARD_HEIGHT = 440;
  /** Width of scoreboard in pixels. */
  public static final int SCOREBOARD_WIDTH = 550;
  //////////////////////////////////////////////////////////


  /// ITEMS PARAMETERS
  /** Speed of a cargo plane. */
  public static final double PLANE_SPEED = 3;
  /** Width of a Pickup. */
  public static final double ITEM_WIDTH = 32;
  /** Length (height) of a Pickup. */
  public static final double ITEM_LENGTH = 32;
  /** Amount of health a Ship regains after using a HealthItem. */
  public static final double ITEM_HEALTH_ITEM_VALUE = 25;
  /** Length of time, in seconds, that a SpeedItem lasts. */
  public static final int ITEM_SPEED_ITEM_DURATION = 2;
  /** Affects how fast a Ship travels when using a SpeedItem. */
  public static final int ITEM_SPEED_ITEM_MULTIPLIER = 2; // ship top speed multiplied by this
  /** Length of time, in seconds, that a NoCooldownItem lasts. */
  public static final int ITEM_NO_COOLDOWN_ITEM_DURATION = 5;
  /** Length of time, in seconds, that a InvulnerableItem lasts. */
  public static final int ITEM_INVULNERABLE_ITEM_DURATION = 5;
  /** Width of a Mine. */
  public static final int MINE_WIDTH = 32;
  /** Length (height) of a Mine. */
  public static final int MINE_LENGTH = 32;
  /** Damage dealt to a Ship when colliding with a Mine. */
  public static final int MINE_DAMAGE = 50;
  /** Amount of points gained when an enemy Ship collides with a Player's Mine. */
  public static final int MINE_SCORE = 100;
  /** Length of a Torpedo. */
  public static final int TORPEDO_LENGTH = 32;
  /** Width of a Torpedo. */
  public static final int TORPEDO_WIDTH = 16;
  /** Damage dealt by a Torpedo. */
  public static final int TORPEDO_DAMAGE = 30;
  /** Speed of a Torpedo. */
  public static final int TORPEDO_SPEED = 8;
  /** Amount of points gained when a Player's Torpedo hits another Ship. */
  public static final int TORPEDO_SCORE = 100;
  //////////////////////////////////////////////////////////

  /// TEST PARAMETERS
  /** Amount of acceptable precision loss for double values. */
  public static final double DOUBLE_PRECISION = 0.001;

  //////////////////////////////////////////////////////////

  /// UNUSED PARAMETERS
  /**
   * How many ice pixels must be in a box to be considered an ice entity (%). This is in [0,1]
   */
  public static final double ICE_BOX_DENSITY = 0.7;
  //////////////////////////////////////////////////////////
}
