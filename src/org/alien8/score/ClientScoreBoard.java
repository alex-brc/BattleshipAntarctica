package org.alien8.score;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

/**
 * This class represents a client-side scoreboard which contains a List of Scores, and can be
 * rendered to the screen.
 *
 */
public class ClientScoreBoard {
  private List<Score> scores = new LinkedList<Score>();
  public static ClientScoreBoard instance;
  private int cornerX;
  private int cornerY;
  private int renderVerticalBuffer;
  private int renderFontHeight;
  private Renderer renderer;

  /**
   * Constructor. Private to prevent global instantiation.
   */
  private ClientScoreBoard() {
    Position screenCenter =
        new Position(Parameters.RENDERER_SIZE.width / 2, Parameters.RENDERER_SIZE.height / 2);
    cornerX = (int) screenCenter.getX() - Parameters.SCOREBOARD_WIDTH / 2;
    cornerY = 40 + (int) screenCenter.getY() - Parameters.SCOREBOARD_HEIGHT / 2;
    renderVerticalBuffer = 10;
    renderFontHeight = 25;
    renderer = Renderer.getInstance();
  }

  /**
   * @return the singleton instance of this scoreboard class
   */
  public static ClientScoreBoard getInstance() {
    if (instance == null)
      instance = new ClientScoreBoard();
    return instance;
  }

  /**
   * Resets this scoreboard, wiping all Scores from it.
   */
  public void reset() {
    scores = new LinkedList<Score>();
  }

  /**
   * Updates a Score with data from a server.
   * 
   * @param sc the Score to update
   */
  public void update(Score sc) {
    boolean updated = false;
    for (Score score : scores)
      if (sc.getShipSerial() == score.getShipSerial()) {
        scores.remove(score);
        scores.add(sc);
        updated = true;
        break;
      }
    if (!updated)
      this.scores.add(sc);
    this.order();
    System.out.println("added score");
  }

  /**
   * Retrieves a Score for a Ship, given that Ship's serial ID.
   * 
   * @param shipSerial the serial ID of the Ship to get the Score for
   * @return the Score
   */
  public Score getScore(long shipSerial) {
    for (Score score : scores) {
      // System.out.println("SCORE: " + score.toString());
      if (score.getShipSerial() == shipSerial)
        return score;
    }
    return null;
  }

  /**
   * Sorts the Scores in this scoreboard by number of points.
   */
  private void order() {
    // Score implements Comparable so
    Collections.sort(scores);
  }

  /**
   * Renders this scoreboard to the screen.
   */
  public void render() {

    // Draw black background
    renderer.drawFilledRect(cornerX, cornerY, Parameters.SCOREBOARD_WIDTH,
        Parameters.SCOREBOARD_HEIGHT, 0x000000, true);

    // Draw header text
    renderer.drawText("Name", cornerX + 40, cornerY + this.renderVerticalBuffer, true,
        FontColor.WHITE);
    renderer.drawText("Score", cornerX + 160, cornerY + this.renderVerticalBuffer, true,
        FontColor.WHITE);
    renderer.drawText("Kills", cornerX + 280, cornerY + this.renderVerticalBuffer, true,
        FontColor.WHITE);
    renderer.drawText("Status", cornerX + 410, cornerY + this.renderVerticalBuffer, true,
        FontColor.WHITE);

    // Draw separator
    for (int x = cornerX; x < cornerX + Parameters.SCOREBOARD_WIDTH; x++)
      renderer.drawPixel(x, cornerY + this.renderFontHeight + this.renderVerticalBuffer, 0xFFFFFF,
          true);

    // Draw scores
    int offset = this.renderFontHeight + 2 * this.renderVerticalBuffer;
    for (Score score : scores) {
      // Draw the color
      renderer.drawFilledRect(cornerX + 15, cornerY + offset, 15, 15, score.getColour(), true);
      // Draw the name
      renderer.drawText(score.getName(), cornerX + 40, cornerY + offset, true, FontColor.WHITE);
      // Draw the score
      renderer.drawText(Integer.toString(score.getScore()), cornerX + 160, cornerY + offset, true,
          FontColor.WHITE);
      // Draw the kills
      renderer.drawText(Integer.toString(score.getKills()), cornerX + 310, cornerY + offset, true,
          FontColor.WHITE);
      // Draw the status
      if (score.getAlive())
        renderer.drawText("ALIVE", cornerX + 420, cornerY + offset, true, FontColor.WHITE);
      else
        renderer.drawText("DEAD", cornerX + 420, cornerY + offset, true, FontColor.WHITE);
      // Draw a separator
      for (int x = cornerX; x < cornerX + Parameters.SCOREBOARD_WIDTH; x++)
        renderer.drawPixel(x, cornerY + offset + this.renderFontHeight, 0xFFFFFF, true);

      // Increment offset
      offset += this.renderFontHeight + this.renderVerticalBuffer;
    }
  }
}
