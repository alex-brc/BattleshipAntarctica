package org.alien8.client;

import java.awt.Dimension;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.alien8.audio.AudioManager;
import org.alien8.core.Parameters;
import org.alien8.rendering.Renderer;
import org.alien8.util.LogManager;

public class Launcher {
	public static Launcher instance;
	private String loadStatus;
	private Renderer renderer;
	private String serverIPstr = "172.22.35.217";
	private Client game;
	
	private Launcher() {
		// Nothing
	}
	
	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		Launcher.instance = launcher;
		// Load log manager
		launcher.loadStatus = "Preparing logger...";
		launcher.loadLogManager();
		
		// Load renderer
		launcher.loadStatus = "Building renderer...";
		launcher.loadRenderer();
		
		// Load audio manager
		launcher.loadStatus = "Loading sounds...";
		launcher.loadAudioManager();
		
		// Connect to server
		launcher.loadStatus = "Connecting to server at " + launcher.serverIPstr +"...";
		launcher.connect();
		
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

	private void connect() {
		game = new Client();
		System.out.println("Connecting to " + serverIPstr + "...");
		if(!game.connect(serverIPstr)) {
			System.out.println("Connection failed. Are you running a server?");
			System.exit(-1);
		}
	}
}
