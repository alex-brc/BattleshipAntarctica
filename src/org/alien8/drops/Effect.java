package org.alien8.drops;

/**
 * This class represents a status effect that an Entity is currently under (e.g. speed boost,
 * invulnerable etc.).
 *
 */
public class Effect {
  public static final int SPEED = 0;
  public static final int NO_COOLDOWN = 1;
  public static final int INVULNERABLE = 2;

  // When this effect expires, in nanoseconds
  private long endTime;
  private int effectType;

  /**
   * Constructor.
   * 
   * @param endTime the time at which the effect ends
   * @param effectType the type of effect to apply
   */
  public Effect(long endTime, int effectType) {
    this.endTime = endTime;
    this.effectType = effectType;
  }

  /**
   * @return the time at which the effect ends
   */
  public long getEndTime() {
    return this.endTime;
  }

  /**
   * @return the type of effect
   */
  public int getEffectType() {
    return this.effectType;
  }
}
