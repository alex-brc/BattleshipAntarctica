package org.alien8.client;

import org.alien8.audio.AudioManager;
import org.alien8.rendering.Renderer;
import org.alien8.score.ServerScoreBoard;
import org.alien8.util.LogManager;

/**
 * This class is the entry point to the game. It launches the game, setting up the necessary
 * components.
 *
 */
public class Launcher {
  public static Launcher instance;
  private String loadStatus;
  private Client game;

  /**
   * Empty constructor.
   */
  private Launcher() {
    // Nothing
  }

  /**
   * The main method used to launch the game.
   * 
   * @param args a String[] of arguments from the command line (unused by the game)
   */
  public static void main(String[] args) {
    Launcher launcher = new Launcher();
    Launcher.instance = launcher;
    
    // Loading client window
    launcher.game = Client.getInstance();
    Renderer.getInstance().show();
    
    // Load log manager
    launcher.loadStatus = "Preparing logger...";
    System.out.println(launcher.loadStatus);
    launcher.loadLogManager();

    // Load audio manager
    launcher.loadStatus = "Loading sounds...";
    System.out.println(launcher.loadStatus);
    launcher.loadAudioManager();

    // Make the scoreboard
    launcher.loadStatus = "Constructing a scoreboard...";
    System.out.println(launcher.loadStatus);
    launcher.loadScoreBoard();

    // Start the game
    launcher.game.start();
  }

  /**
   * Returns the singleton instance of the launcher.
   * 
   * @return the singleton instance of the launcher
   */
  public static Launcher getInstance() {
    return instance;
  }

  /**
   * Returns the client object running the game.
   * 
   * @return the client object running the game
   */
  public Client getRunningClient() {
    return game;
  }

  /**
   * Returns the singleton instance of the log manager.
   * 
   * @return the singleton instance of the log manager
   */
  private void loadLogManager() {
    LogManager.getInstance();
  }

  /**
   * Returns the singleton instance of the audio manager.
   * 
   * @return the singleton instance of the audio manager
   */
  private void loadAudioManager() {
    AudioManager.getInstance();
  }

  /**
   * Returns the singleton instance of the score board.
   * 
   * @return the singleton instance of the score board
   */
  private void loadScoreBoard() {
    ServerScoreBoard.getInstance();
  }

}
