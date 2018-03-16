package org.alien8.ui;

import java.util.LinkedList;

import org.alien8.client.Client;
import org.alien8.core.Parameters;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

public class Lobby implements Page {
	private String ip;
	private boolean isHost = false;
	private LinkedList<String> playerNames;
	private StartButton startBtn;
	private int sepLength = 300;
	
	public Lobby() {
		Renderer r = Renderer.getInstance();
		playerNames = new LinkedList<String>();
		startBtn = new StartButton(500, r.getWidth()/2 - 40, 80, 30);
		
	}
	
	public void fill() {
		this.ip = Client.getInstance().getMenu().getIP();
		playerNames.addAll(Client.getInstance().getOpponents());
	}
	
	public void setHost() {
		this.isHost = true;
	}
	
	public void render(Renderer renderer) {
		startBtn.render(renderer);
		
		int offset = 100 + 4;
		for(String name : playerNames) {
	        renderer.drawText(name, renderer.getWidth()/2 - name.length()*16/2, offset, true, FontColor.WHITE);
	        offset += 4;
	        // Draw a separator
	        for(int x = renderer.getWidth()/2 - sepLength/2; x < Parameters.SCOREBOARD_WIDTH; x++)
	            renderer.drawPixel(x, offset + 16, 0xFFFFFF, true);
	        offset += 4 + 16 + 4;
		}
	}	
}
