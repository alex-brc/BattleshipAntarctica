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
	public static int N_SECOND = 1000000; 
}
