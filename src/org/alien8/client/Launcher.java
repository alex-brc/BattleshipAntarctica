package org.alien8.client;

import org.alien8.audio.AudioManager;
import org.alien8.rendering.Renderer;
import org.alien8.score.ScoreBoard;
import org.alien8.util.LogManager;

public class Launcher {
  public static Launcher instance;
  private String loadStatus;
  private String serverIPstr = "192.168.0.16";
  private Client game;

  private Launcher() {
    // Nothing
  }

  public static void main(String[] args) {
    Launcher launcher = new Launcher();
    
    // Loading client window
    launcher.game = new Client();
    Launcher.instance = launcher;
    // Load log manager
    launcher.loadStatus = "Preparing logger...";
    launcher.loadLogManager();

    // Load audio manager
    launcher.loadStatus = "Loading sounds...";
    launcher.loadAudioManager();

    // Connect to server
    launcher.loadStatus = "Connecting to server at " + launcher.serverIPstr + "...";
    launcher.connect();

    // Make the scoreboard
    launcher.loadStatus = "Constructing a scoreboard...";
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

  private void loadRenderer() {
    Renderer.getInstance();
  }

  private void loadAudioManager() {
    AudioManager.getInstance();
  }

  private void loadScoreBoard() {
    ScoreBoard.getInstance().startListener();
  }

  private void connect() {
    System.out.println("Connecting to " + serverIPstr + "...");
    LogManager.getInstance().log("Networking", LogManager.Scope.INFO,
        "Connecting to host at " + serverIPstr + "...");
    if (!game.connect(serverIPstr)) {
      LogManager.getInstance().log("Networking", LogManager.Scope.CRITICAL, "Connection failed.");
      System.out.println("Connection failed. Are you running a server?");
      System.exit(-1);
    }
  }
}
