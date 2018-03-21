package test.org.alien8.drops;

import org.alien8.drops.Effect;
import org.junit.Test;

public class EffectTest {
	@Test
	public void testEffect() {
		// Make a new effect
		Effect effect = new Effect(1, Effect.INVULNERABLE);
		// Check it's okay
		assert(effect.getEndTime() == 1);
		assert(effect.getEffectType() == Effect.INVULNERABLE);
	}
}
