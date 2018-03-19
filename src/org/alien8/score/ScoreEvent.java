package org.alien8.score;

import org.alien8.server.GameEvent;

/**
 * This class is a package class for score changes.
 */
public class ScoreEvent extends GameEvent {
  private static final long serialVersionUID = -2792805329954032793L;

  private long shipSerial;
  private String name;
  private int colour;
  private int score;
  private int kills;
  private boolean alive;

  /**
   * Constructor.
   * 
   * @param shipSerial the serial ID of the Ship to whose Score is being changed.
   * @param name the name of the Ship
   * @param colour the colour of the Ship
   * @param score the current Score of the Ship
   * @param kills the number of kills the Ship has
   * @param alive whether the Ship is alive or not
   */
  public ScoreEvent(long shipSerial, String name, int colour, int score, int kills, boolean alive) {
    super();
    this.shipSerial = shipSerial;
    this.name = name;
    this.colour = colour;
    this.score = score;
    this.kills = kills;
    this.alive = alive;
  }

  /**
   * Constructor. Creates a ScoreEvent from a Score.
   * 
   * @param score the Score to create this ScoreEvent from
   */
  public ScoreEvent(Score score) {
    super();
    this.shipSerial = score.getShipSerial();
    this.name = score.getName();
    this.colour = score.getColour();
    this.score = score.getScore();
    this.kills = score.getKills();
    this.alive = score.getAlive();
  }

  /**
   * @return the serial ID of the Ship that this ScoreEvent affects
   */
  public long getShipSerial() {
    return shipSerial;
  }

  /**
   * @return the name of the Ship that this ScoreEvent affects
   */
  public String getName() {
    return name;
  }

  /**
   * @return the colour of the Ship that this ScoreEvent affects
   */
  public int getColour() {
    return colour;
  }

  /**
   * @return the current Score of the Ship that this ScoreEvent affects
   */
  public int getScore() {
    return score;
  }

  /**
   * @return the number of kills of the Ship that this ScoreEvent affects
   */
  public int getKills() {
    return kills;
  }

  /**
   * @return {@code true} if the Ship this ScoreEvent affects is alive, {@code false} if not
   */
  public boolean getAlive() {
    return alive;
  }
}
