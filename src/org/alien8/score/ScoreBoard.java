package org.alien8.score;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.Bullet;
import org.alien8.util.LogManager;

public class ScoreBoard {
  public static ScoreBoard instance;
  private List<Score> scores = new LinkedList<Score>();
  private int cornerX;
  private int cornerY;
  private int renderVerticalBuffer;
  private int renderFontHeight;
  private Renderer renderer;

  private ScoreBoard() {
    Position screenCenter =
        new Position(Parameters.RENDERER_SIZE.width / 2, Parameters.RENDERER_SIZE.height / 2);
    cornerX = (int) screenCenter.getX() - Parameters.SCOREBOARD_WIDTH / 2;
    cornerY = (int) screenCenter.getY() - Parameters.SCOREBOARD_HEIGHT / 2;
    renderVerticalBuffer = 10;
    renderFontHeight = 25;
    renderer = Renderer.getInstance();
  }

  public static ScoreBoard getInstance() {
    if (instance == null)
      instance = new ScoreBoard();
    return instance;
  }

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
  
  public void kill(Player player) {
    for (Score score : scores)
      if (player.getShip().getSerial() == score.getShipSerial()) {
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

  public Score getScore(long shipSerial) {
    for (Score score : scores) {
      //System.out.println("SCORE: " + score.toString());
      if (score.getShipSerial() == shipSerial)
        return score;
    }
    return null;
  }

  public void render() {
	
    // Draw black background
    renderer.drawFilledRect(cornerX, cornerY, Parameters.SCOREBOARD_WIDTH, Parameters.SCOREBOARD_HEIGHT, 0x000000, true);
          
    // Draw header text
    renderer.drawText("Name", cornerX + 40, cornerY + this.renderVerticalBuffer, true, FontColor.WHITE);
    renderer.drawText("Score", cornerX + 160, cornerY + this.renderVerticalBuffer, true, FontColor.WHITE);
    renderer.drawText("Kills", cornerX + 280, cornerY + this.renderVerticalBuffer, true, FontColor.WHITE);
    renderer.drawText("Status", cornerX + 410, cornerY + this.renderVerticalBuffer, true, FontColor.WHITE);
    
    // Draw separator
    for(int x = cornerX; x < cornerX + Parameters.SCOREBOARD_WIDTH; x++)
    	renderer.drawPixel(x, cornerY + this.renderFontHeight + this.renderVerticalBuffer, 0xFFFFFF, true);
    
    // Draw scores
    int offset = this.renderFontHeight + 2 * this.renderVerticalBuffer;
    for(Score score : scores) {
    	// Draw the color
    	renderer.drawFilledRect(cornerX + 15, cornerY + offset, 15, 15, score.getColour(), true);
    	// Draw the name
    	renderer.drawText(score.getName(), cornerX + 40, cornerY + offset, true, FontColor.WHITE);
    	// Draw the score
    	renderer.drawText(Integer.toString(score.getScore()), cornerX + 160, cornerY + offset, true, FontColor.WHITE);
    	// Draw the kills
    	renderer.drawText(Integer.toString(score.getKills()), cornerX + 310, cornerY + offset, true, FontColor.WHITE);
    	// Draw the status
    	if(score.getAlive())
    		renderer.drawText("ALIVE", cornerX + 420, cornerY + offset, true, FontColor.WHITE);
    	else
    		renderer.drawText("DEAD", cornerX + 420, cornerY + offset, true, FontColor.WHITE);
    	// Draw a separator
    	for(int x = cornerX; x < cornerX + Parameters.SCOREBOARD_WIDTH; x++)
        	renderer.drawPixel(x, cornerY + offset + this.renderFontHeight, 0xFFFFFF, true);
    	
    	// Increment offset
    	offset += this.renderFontHeight + this.renderVerticalBuffer;
    }
  }
}
