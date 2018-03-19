package org.alien8.score;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.alien8.core.Parameters;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.alien8.util.LogManager;

public class ServerScoreBoard {
  public static ServerScoreBoard instance;
  private List<Score> scores = new LinkedList<Score>();

  private ServerScoreBoard() {
    // Prevent global instantiation
  }

  public static ServerScoreBoard getInstance() {
    if (instance == null)
      instance = new ServerScoreBoard();
    return instance;
  }
  
  public void reset() {
    scores = new LinkedList<Score>();
  }

  public void add(Player player) {
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.INFO,
        "Adding player " + player.getName() + " to scoreboard");
    Score score = new Score(player);
    scores.add(score);
    Server.getInstance().addEvent(score.exportToEvent());
  }

  public void remove(Player player) {
    scores.remove(this.getScore(player));
  }

  public void giveKill(Player player) {
    for (Score score : scores)
      if (player.getShip().getSerial() == score.getShipSerial()) {
        score.giveKill();
        Server.getInstance().addEvent(score.exportToEvent());
        order();
        return;
      }
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR,
        "In giveKill(): given player not found on the scoreboard.");
  }

  public void giveHit(Player player, Bullet bullet) {
    try {
      for (Score score : scores)
        if (player.getShip().getSerial() == score.getShipSerial()) {
          score.giveHit(bullet);
          Server.getInstance().addEvent(score.exportToEvent());
          order();
          return;
        }
    } catch (NullPointerException e) {
      LogManager.getInstance().log("ScoreBoard", LogManager.Scope.CRITICAL,
          "In giveHit(): the bullet or player given was null. Exiting...");
      e.printStackTrace();
      System.exit(-1);
    }
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR,
        "In giveHit(): given player not found on the scoreboard.");
  }
  
  public void giveScore(Player player, int score) {
	  try {
	      for (Score sc : scores)
	        if (player.getShip().getSerial() == sc.getShipSerial()) {
	          sc.giveScore(Parameters.TORPEDO_SCORE);
	          Server.getInstance().addEvent(sc.exportToEvent());
	          order();
	          return;
	        }
	    } catch (NullPointerException e) {
	      LogManager.getInstance().log("ScoreBoard", LogManager.Scope.CRITICAL,
	          "In giveHit(): the bullet or player given was null. Exiting...");
	      e.printStackTrace();
	      System.exit(-1);
	    }
	    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR,
	        "In giveHit(): given player not found on the scoreboard.");
  }
  
  public void kill(Ship ship) {
    for (Score score : scores)
      if (ship.getSerial() == score.getShipSerial()) {
        score.kill();
        Server.getInstance().addEvent(score.exportToEvent());
        return;
      }
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR,
        "In kill(): given player not found on the scoreboard.");
  }

  private void order() {
    // Score implements Comparable so
    Collections.sort(scores);
  }

  public List<Score> getScores() {
    return scores;
  }

  public Score getScore(Player player) {
    for (Score score : scores) {
      //System.out.println("SCORE: " + score.toString());
      if (player.getShip().getSerial() == score.getShipSerial())
        return score;
    }
    return null;
  }

}
