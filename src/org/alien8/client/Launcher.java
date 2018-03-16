package org.alien8.client;

import org.alien8.audio.AudioManager;
import org.alien8.score.ServerScoreBoard;
import org.alien8.util.LogManager;

public class Launcher {
  public static Launcher instance;
  private String loadStatus;
  private Client game;

  private Launcher() {
    // Nothing
  }
  
  public static void main(String[] args) {
    Launcher launcher = new Launcher();

    // Loading client window
    launcher.game = Client.getInstance();
    Launcher.instance = launcher;
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

  public static Launcher getInstance() {
    return instance;
  }

  public Client getRunningClient() {
    return game;
  }

  private void loadLogManager() {
    LogManager.getInstance();
  }

  private void loadAudioManager() {
    AudioManager.getInstance();
  }

  private void loadScoreBoard() {
    ServerScoreBoard.getInstance();
  }

}
