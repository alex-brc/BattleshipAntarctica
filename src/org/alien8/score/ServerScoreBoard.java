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

/**
 * This class represents a server-side scoreboard containing all the scores of Ships in the game.
 *
 */
public class ServerScoreBoard {
  public static ServerScoreBoard instance;
  private List<Score> scores = new LinkedList<Score>();

  /**
   * Private constructor to prevent global instantiation.
   */
  private ServerScoreBoard() {
    // Prevent global instantiation
  }

  /**
   * @return the singleton instance of this scoreboard
   */
  public static ServerScoreBoard getInstance() {
    if (instance == null)
      instance = new ServerScoreBoard();
    return instance;
  }

  /**
   * @return a List of all Scores in this scoreboard
   */
  public List<Score> getScores() {
    return scores;
  }

  /**
   * Retrieves the Score for a given Player.
   * 
   * @param player the Player to get the Score from
   * @return the Score
   */
  public Score getScore(Player player) {
    for (Score score : scores) {
      // System.out.println("SCORE: " + score.toString());
      if (player.getShip().getSerial() == score.getShipSerial())
        return score;
    }
    return null;
  }

  /**
   * Reset this scoreboard, wiping all scores from it.
   */
  public void reset() {
    scores = new LinkedList<Score>();
  }

  /**
   * Adds a Player to this scoreboard
   * 
   * @param player the Player to add
   */
  public void add(Player player) {
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.INFO,
        "Adding player " + player.getName() + " to scoreboard");
    Score score = new Score(player);
    scores.add(score);
    Server.getInstance().addEvent(score.exportToEvent());
  }

  /**
   * Removes a Player from this scoreboard
   * 
   * @param player the Player to remove
   */
  public void remove(Player player) {
    scores.remove(this.getScore(player));
  }

  /**
   * Awards a specified number of points to a given Player.
   * 
   * @param player the Player to award points to
   * @param score the amount of points to award
   */
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

  /**
   * Give a kill to a Player, updating their Score and number of kills.
   * 
   * @param player the Player to give a kill to
   */
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

  /**
   * Give the player points for hitting another Ship.
   * 
   * @param player the Player to award points to
   * @param bullet the Bullet which hit another Ship
   */
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

  /**
   * Kill a certain Player, marking that player as dead on the scoreboard.
   * 
   * @param ship the ship belonging to the player to kill
   */
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

  /**
   * Sort the Scores by number of points.
   */
  private void order() {
    // Score implements Comparable so
    Collections.sort(scores);
  }

}
