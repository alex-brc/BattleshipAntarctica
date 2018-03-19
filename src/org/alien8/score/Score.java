package org.alien8.score;

import org.alien8.core.Parameters;
import org.alien8.server.Player;
import org.alien8.ship.Bullet;

/**
 * This class represents a score for a Ship. It includes details such as the Ship's name, number of
 * kills and whether the Ship is alive or not.
 *
 */
public class Score implements Comparable<Score> {

  private long shipSerial;
  private String name;
  private int colour;
  private int score;
  private int kills;
  private boolean alive;

  /**
   * Constructor.
   * 
   * @param player the Player to create a score for
   */
  public Score(Player player) {
    this.name = player.getName();
    this.colour = player.getShip().getColour();
    this.shipSerial = player.getShip().getSerial();
    this.score = 0;
    this.kills = 0;
    this.alive = true;
  }

  /**
   * Constructor. Creates a Score from a ScoreEvent.
   * 
   * @param event the ScoreEvent to create this Score from
   */
  public Score(ScoreEvent event) {
    this.name = event.getName();
    this.shipSerial = event.getShipSerial();
    this.colour = event.getColour();
    this.score = event.getScore();
    this.kills = event.getKills();
    this.alive = event.getAlive();
  }

  /**
   * @return the serial ID of the Ship which holds this Score
   */
  public long getShipSerial() {
    return shipSerial;
  }

  /**
   * @return the name of the Ship which holds this Score
   */
  public String getName() {
    return name;
  }

  /**
   * @return the colour of the Ship which holds this Score
   */
  public int getColour() {
    return colour;
  }

  /**
   * @return the value of this Score
   */
  public int getScore() {
    return score;
  }

  /**
   * Awards a certain amount of points to this Score.
   */
  public void giveScore(int score) {
    this.score += score;
  }

  /**
   * Awards the score earned for landing a shot.
   * 
   * score = DISTANCE_MULTIPLIER * bullet.getDistance() + 15; | | a modifiable parameter distance
   * travelled by the bullet before it hit
   * 
   */
  public void giveHit(Bullet bullet) {
    this.score += (int) bullet.getDistance() * Parameters.DISTANCE_MULTIPLIER + 15;
  }

  /**
   * Awards the score earned for killing someone.
   * 
   * score per kill = SCORE_PER_KILL * ( 1 + number of kills * KILL_STREAK_MULTIPLIER)
   */
  public void giveKill() {
    this.score += (int) Parameters.SCORE_PER_KILL * (1 + kills * Parameters.KILL_STREAK_MULTIPLIER);
    this.kills++;
  }

  /**
   * @return the number of kills by the Ship which holds this Score
   */
  public int getKills() {
    return kills;
  }

  /**
   * Set the Ship which holds this Score to be dead.
   */
  public void kill() {
    this.alive = false;
  }

  /**
   * @return {@code true} if the Ship which holds this Score is alive, {@code false} if not
   */
  public boolean getAlive() {
    return alive;
  }

  /**
   * Compares this Score to another.
   * 
   * @return the difference between the other Score and this one
   */
  @Override
  public int compareTo(Score score) {
    // Descending
    return (int) score.score - this.score;
  }

  /**
   * @return a String representation of this Score
   */
  @Override
  public String toString() {
    String result = "" + this.name + " " + this.score + " " + this.kills;
    return result;
  }

  /**
   * Creates a ScoreEvent from this Score.
   * 
   * @return a ScoreEvent representation of this Score
   */
  public ScoreEvent exportToEvent() {
    return new ScoreEvent(this);
  }

}
