package org.alien8.score;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.alien8.client.InputManager;
import org.alien8.core.Parameters;
import org.alien8.server.Player;
import org.alien8.ship.Bullet;
import org.alien8.util.LogManager;

public class ScoreBoard implements Runnable {
	public static ScoreBoard instance;
	private Score[] scores = new Score[Parameters.MAX_PLAYERS];

	private ScoreBoard() {
		
	}

	public static ScoreBoard getInstance() {
		if(instance == null)
			instance = new ScoreBoard();
		return instance;
	}

	public void fill(List<Player> players) {
		LogManager.getInstance().log("ScoreBoard", LogManager.Scope.INFO, "Filling scoreboard with players...");
		int i = 0;
		try {
			for(Player p : players) {
				scores[i] = (new Score(p));
				i++;
			}
		}
		catch(IndexOutOfBoundsException e) {
			LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR, "More than Parameters.MAX_PLAYERS on the map. Ignoring the rest.");
			System.out.println("MORE THAN 16 PLAYERS ON THE MAP");
		}
	}

	public void giveKill(Player p) {
		for (Score score : scores)
			if(p.getName().equals(score.getName())) {
				score.giveKill();
				return;
			}
		LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR, "In giveKill(): given player not found on the scoreboard.");
	}

	public void giveHit(Player p, Bullet b) {
		try {
			for (Score score : scores)
				if(p.getName().equals(score.getName())) {
					score.giveHit(b);
					return;
				}
		}
		catch(NullPointerException e) {
			LogManager.getInstance().log("ScoreBoard", LogManager.Scope.CRITICAL, "In giveHit(): the bullet or player given was null. Exiting...");
			System.exit(-1);
		}
		LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR, "In giveHit(): given player not found on the scoreboard.");
	}

	private void order() {
		// Score implements Comparable so
		Arrays.sort(scores);
	}

	public List<Score> getScores(){
		List<Score> list = new LinkedList<Score>();

		for(Score score : this.scores)
			list.add(score);

		return list;
	}
	
	public void render() {
		//TODO
		System.out.println("showing scores");
	}

	@Override
	public void run() {
		while(true) {
			if(InputManager.getInstance().tabPressed()) {
				this.order();
				this.render();
			}
		}
	}

}
