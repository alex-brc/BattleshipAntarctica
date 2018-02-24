package org.alien8.client;

import java.awt.Dimension;

import org.alien8.managers.AudioManager;
import org.alien8.managers.LogManager;
import org.alien8.rendering.Renderer;

public class Launcher {
	private String loadStatus;
	
	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		// Load log manager
		launcher.loadStatus = "Preparing logger...";
		LogManager.getInstance();
		
		// Load renderer
		launcher.loadStatus = "Building renderer...";
		Renderer renderer = new Renderer(new Dimension(800, 600));
		
		// Load audio manager
		launcher.loadStatus = "Loading sounds...";
		AudioManager.getInstance();
		
		Client game = new Client(renderer, "172.22.35.217");
		game.start();
	}
}
