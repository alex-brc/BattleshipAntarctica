package org.alien8.client;

import java.awt.Dimension;

import org.alien8.rendering.Renderer;

public class Client implements Runnable{
	
	private boolean running = false;
	private Thread thread;
	private Renderer renderer;
	
	public static void main(String[] args){
		
		Client game = new Client();
		game.start();
	}
	
	public Client(){
		renderer = new Renderer(new Dimension(800, 600));
	}
	
	public void start(){
		running = true;
		thread = new Thread(this, "Battleship Antarctica");
		thread.start();
	}
	
	public void stop(){
		running = false;
		try{
			thread.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		//Game loop goes here
		while(running){
			
		}
	}
	
}
