package org.alien8.score;

import org.alien8.core.Parameters;
import org.alien8.server.GameEvent;
import org.alien8.server.Player;
import org.alien8.ship.Bullet;

public class Score extends GameEvent implements Comparable<Score>{

	private static final long serialVersionUID = -1613827319434373237L;

	private String name;
	private int colour;
	private int score;
	private int kills;
	private boolean alive;
	
	public Score(Player player) {
		this.name = player.getName();
		this.colour = player.getShip().getColour();
		this.score = 0;
		this.kills = 0;
		this.alive = true;
	}
	/**
	 * Awards the score earned for landing a shot.
	 * 
	 * score = DISTANCE_MULTIPLIER * bullet.getDistance() + 15;
	 *                 |                     |            
	 *    a modifiable parameter     distance travelled by the bullet before it hit
	 * 
	 */
	public void giveHit(Bullet bullet) {
		this.score += (int) bullet.getDistance() * Parameters.DISTANCE_MULTIPLIER + 15;
	}
	/**
	 * Awards the score earned for killing someone
	 * 
	 * score per kill = SCORE_PER_KILL * ( 1 + number of kills * KILL_STREAK_MULTIPLIER)
	 */
	public void giveKill() {
		this.score += (int) Parameters.SCORE_PER_KILL * (1 + kills*Parameters.KILL_STREAK_MULTIPLIER);
		this.kills++;
	}
	
	public void kill() {
		this.alive = false;
	}
	
	public String getName() {
		return name;
	}

	public int getColour() {
		return colour;
	}

	public long getScore() {
		return score;
	}

	public int getKills() {
		return kills;
	}
	
	public boolean getAlive() {
		return alive;
	}
	
	@Override
	public int compareTo(Score score) {
		// Descending
		return (int) score.score - this.score;
	}
	
	@Override
	public String toString() {
		String result = "" + this.name + " " + this.score + " " + this.kills;
		return result;
	}

}
