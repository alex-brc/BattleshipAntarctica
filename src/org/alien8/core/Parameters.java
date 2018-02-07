package org.alien8.core;

/**
 * This class is meant to hold all the important <code> final </code> parameters
 * for all classes to easily access. If everyone uses these, we could technically just change these parameters,
 * build, run and have the game running at a different speed, maybe projectiles are faster, maybe acceleration 
 * from input is more intense, etc. Essentially, this can be viewed as a universal control panel. Change a 
 * parameter from here and the change ripples through the code. 
 * <p>
 * Taking that into consideration, every tweak-able parameter and important constant should be written in here.
 * 
 */
public class Parameters {
	
	///////////// META AND GAME PARAMS ////////
	/**
	 * How often the model calls update() on the entities. Because of some divisions, it's a bit lower than that.
	 * This value gives 58-61 ticks/second, hovering around 59-60
	 */
	public static int TICKS_PER_SECOND = 60;
	/**
	 * Nanoseconds in a second
	 */
	public static int N_SECOND = 1000000000;
	/**
	 * Size of map hitboxes
	 */
	public static int MAP_BOX_SIZE = 4;
	public static int MAP_HEIGHT = 2048;
	public static int MAP_WIDTH = 2048;
	/**
	 * Must be between 0 and 1. Affects the size and frequency of
	 * the ice patches
	 */
	public static double WATER_LEVEL = 0.4d;
	/**
	 * How many times a second to update the FPS tracker. Ideally, set to a divisor of 1,000,000,000 for optimization.
	 */
	public static int FPS_FREQ = 2;
	
	////////////// SHIP PARAMS ////////////////
	
	/**
	 * Length of the ship in units (the same units we use for the coordinate system)
	 * Currently, this number doesn't mean much
	 */
	public static int SHIP_LENGTH = 100;
	/**
	 * Interdependent stuff. Force required is computed according to how long it
	 * would take to reach top speed
	 */
	public static double SHIP_TOP_SPEED_REACH_TIME = 200;
	public static double SHIP_TOP_SPEED_FORWARD = 2;
	public static double SHIP_TOP_SPEED_BACKWARD = 2;
	public static double SHIP_MASS = 1000; // kg. This is kinda random
	public static double SHIP_FORWARD_FORCE = SHIP_MASS * SHIP_TOP_SPEED_FORWARD / SHIP_TOP_SPEED_REACH_TIME; // N  
	public static double SHIP_BACKWARD_FORCE = SHIP_MASS * SHIP_TOP_SPEED_BACKWARD / SHIP_TOP_SPEED_REACH_TIME; // N
	public static double SHIP_ROTATION_PER_SEC = Math.PI/2;
	/**
	 * Affects how much the speed impacts the turning rate
	 * Lower means turns are tighter at speed. 
	 * 1 means at top speed turning is impossible.
	 */
	public static double ROTATION_MODIFIER = 0.8;
	/**
	 * Applied to ship speeds every tick.
	 */
	public static double FRICTION = 0.997;
	
	//////// BULLET PARAMS //////////
	public static double BIG_BULLET_MASS = 30;
	public static double SMALL_BULLET_MASS = 10;
	public static double BIG_BULLET_SPEED = 2;
	public static double SMALL_BULLET_SPEED = 4;
	public static int SMALL_BULLET_CD = 1000; // Milliseconds
	public static int BIG_BULLET_CD = 3000;   //
	public static int BIG_BULLET_MIN_DIST = 50;
	public static int BIG_BULLET_MAX_DIST = 400;
	public static int SMALL_BULLET_MIN_DIST = 50;
	public static int SMALL_BULLET_MAX_DIST = 400;
	/**
	 * This modifier affects how much distance holding down
	 * a button gives to the turret shot every tick
	 */
	public static double CHARGE_INCREMENT = 4;
}
