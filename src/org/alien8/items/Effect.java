package org.alien8.items;

public class Effect {
	public static int SPEED = 0;
	public static int NO_COOLDOWN = 1;
	public static int INVULNERABLE = 2;
	
    // When this effect expires, in nanoseconds
	private long endTime;
	private int effectType;
	
	public Effect(long endTime, int effectType) {
		this.endTime = endTime;
		this.effectType = effectType;
	}
	
	public int getEffectType() {
		return this.effectType;
	}
	
	public long getEndTime() {
		return this.endTime;
	}
}
