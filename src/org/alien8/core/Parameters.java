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
	/**
	 * How often the model calls update() on the entities. 
	 */
	public static int TICKS_PER_SECOND = 60;
	/**
	 * How many times a second to update the FPS tracker. Ideally, set to a divisor of e+9, for simplicity.
	 */
	public static int FPS_FREQ = 4;
	public static int N_SECOND = 1000000000;
	public static int MAP_HEIGHT = 2048;
	public static int MAP_WIDTH = 2048;
	/**
	 * Length of the ship in units (the same units we use for the coordinate system)
	 * Currently, this number doesn't mean much
	 */
	public static int SHIP_LENGTH = 100;
	/**
	 * Interdependent stuff. Force required is computed according to how long it
	 * would take to reach top speed
	 */
	public static double SHIP_TOP_SPEED_REACH_TIME = 10; // s
	public static double SHIP_TOP_SPEED_FORWARD = 2; // m/s
	public static double SHIP_TOP_SPEED_BACKWARD = 3; // m/s
	public static double SHIP_MASS = 1000; // kg. This is kinda random
	public static double SHIP_FORWARD_FORCE = SHIP_MASS * SHIP_TOP_SPEED_FORWARD / SHIP_TOP_SPEED_REACH_TIME; // N  
	public static double SHIP_BACKWARD_FORCE = SHIP_MASS * SHIP_TOP_SPEED_BACKWARD / SHIP_TOP_SPEED_REACH_TIME; // N
	public static double SHIP_ROTATION_PER_SEC = Math.PI;
	public static double ROTATION_MODIFIER = 1;
	public static double FRICTION = 0.92;
	public static double BIG_BULLET_MASS = 30;
	public static double SMALL_BULLET_MASS = 10;
	public static double BIG_BULLET_SPEED = 1;
	public static double SMALL_BULLET_SPEED = 1;
	// Bullet cooldowns in miliseconds
	public static int SMALL_BULLET_CD = 500;
	public static int BIG_BULLET_CD = 2000;
	/**
	 * This modifier affects how much distance holding down
	 * a button gives to the turret shot
	 */
	public static double CHARGE_MODIFIER = 100;
}
