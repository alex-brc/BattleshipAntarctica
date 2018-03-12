package org.alien8.score;

import org.alien8.server.GameEvent;

/**
 * Package class for score changes
 */
public class ScoreEvent extends GameEvent {
	private static final long serialVersionUID = -2792805329954032793L;

	private long shipSerial;
	private String name;
	private int colour;
	private int score;
	private int kills;
	private boolean alive;
	
	public ScoreEvent(long shipSerial, String name, int colour, int score, int kills, boolean alive) {
		super();
		this.shipSerial = shipSerial;
		this.name = name;
		this.colour = colour;
		this.score = score;
		this.kills = kills;
		this.alive = alive;
	}
	
	public ScoreEvent(Score score) {
		super();
		this.shipSerial = score.getShipSerial();
		this.name = score.getName();
		this.colour = score.getColour();
		this.score = score.getScore();
		this.kills = score.getKills();
		this.alive = score.getAlive();
	}
	
	public long getShipSerial() {
		return shipSerial;
	}

	public String getName() {
		return name;
	}

	public int getColour() {
		return colour;
	}

	public int getScore() {
		return score;
	}

	public int getKills() {
		return kills;
	}

	public boolean getAlive() {
		return alive;
	}
}
