package org.alien8.ui;

import org.alien8.client.Client;
import org.alien8.rendering.Renderer;

public class Lobby implements Page {
	private String ip;
	private boolean isHost = false;
	private LobbyList list;
	private StartButton startBtn;
	
	public Lobby() {
		Renderer r = Renderer.getInstance();
		list = new LobbyList(100);
		startBtn = new StartButton(360, 500, 80, 30);
	}
	
	public void fill() {
		this.ip = Client.getInstance().getMenu().getIP();
		for(String name : Client.getInstance().getOpponents()) {
			list.add(name);
		}
	}
	
	public void setHost() {
		this.isHost = true;
	}
	
	public void render(Renderer renderer) {
		startBtn.render(renderer);
		list.render(renderer);
		
		
		
	}	
}
