package org.alien8.score;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.alien8.client.InputManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.server.Player;
import org.alien8.server.Server;
import org.alien8.ship.Bullet;
import org.alien8.util.LogManager;

public class ScoreBoard implements Runnable {
  public static ScoreBoard instance;
  private List<Score> scores = new LinkedList<Score>();
  private Thread thread;
  private volatile boolean listenerRunning = false;
  private int cornerX;
  private int cornerY;

  private ScoreBoard() {
    Position screenCenter =
        new Position(Parameters.RENDERER_SIZE.width / 2, Parameters.RENDERER_SIZE.height / 2);
    cornerX = (int) screenCenter.getX() - Parameters.SCOREBOARD_WIDTH / 2;
    cornerY = (int) screenCenter.getY() - Parameters.SCOREBOARD_HEIGHT / 2;
  }

  public static ScoreBoard getInstance() {
    if (instance == null)
      instance = new ScoreBoard();
    return instance;
  }

  public void update(Score sc) {
    for (Score score : scores)
      if (sc.getShipSerial() == score.getShipSerial())
        score = sc;
  }

  public void fill(List<Player> players) {
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.INFO,
        "Filling scoreboard with players...");

    for (Player p : players) {
      Score score = new Score(p);
      scores.add(score);
      Server.addEvent(score.exportToEvent());
    }
  }

  public void add(Player player) {
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.INFO,
        "Adding player " + player.getName() + " to scoreboard");
    Score score = new Score(player);
    scores.add(score);
    Server.addEvent(score.exportToEvent());
  }

  public void remove(Player player) {
    scores.remove(this.getScore(player));
  }

  public void giveKill(Player player) {
    for (Score score : scores)
      if (player.getShip().getSerial() == score.getShipSerial()) {
        score.giveKill();
        Server.addEvent(score.exportToEvent());
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
          Server.addEvent(score.exportToEvent());
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
        Server.addEvent(score.exportToEvent());
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
      System.out.println(score.toString());
      if (player.getShip().getSerial() == score.getShipSerial())
        return score;
    }
    return null;
  }
  
  public Score getScore(long shipSerial) {
    for (Score score : scores) {
      System.out.println(score.toString());
		  if (score.getShipSerial() == shipSerial)
			  return score;
	  }
	  return null;
  }

  public void render() {
    // TODO
    Renderer.getInstance().drawRect(cornerX, cornerY, Parameters.SCOREBOARD_WIDTH,
        Parameters.SCOREBOARD_HEIGHT, 0xFF4500, true);
  }

  public synchronized void notifyShift() {
    notifyAll();
  }

  @Override
  public synchronized void run() {
    while (listenerRunning) {
      while (!InputManager.getInstance().shiftPressed()) {
        try {
          this.wait();
        } catch (InterruptedException e) {
        }
      }
      this.order();
      this.render();
    }
  }

  public void startListener() {
    listenerRunning = true;
    thread = new Thread(ScoreBoard.getInstance(), "ScoreBoard");
    thread.start();
  }

  public void killListener() {
    try {
      listenerRunning = false;
      thread.join();
    } catch (InterruptedException e) {
      LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR,
          "Failed to kill listener thread. " + e.toString());
    }
    LogManager.getInstance().log("ScoreBoard", LogManager.Scope.ERROR,
        "Score listener killed cleanly.");
  }
}
