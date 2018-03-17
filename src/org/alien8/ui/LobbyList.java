package org.alien8.ui;

import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

public class LobbyList {
	public static final int LOBBY_LENGTH = 8;
	private final int vPad;
	private final int sLen;
	
	private String[] players;
	private int playerNum;
	private int y;
	
	public LobbyList(int y) {
		vPad = 15;
		sLen = 400;
		
		players = new String[LOBBY_LENGTH];
		playerNum = 0;
		for(int i = 0; i < LOBBY_LENGTH; i++) {
			players[i] = "empty";
		}
		
		this.y = y;
	}
	
	public void add(String name) {
		players[playerNum++] = name;
	}
	
	public void render(Renderer r) {
		int offset = y + vPad;
		
		// Draw a background
		r.drawFilledRect(r.getWidth()/2 - sLen/2, y, sLen, 370, 0x303030, true);

		for(int i = 0; i < LOBBY_LENGTH; i++) {
			if(!players[i].equals("empty"))
	        	r.drawText(players[i], r.getWidth()/2 - players[i].length()*16/2, offset, true, FontColor.WHITE);
			else
				r.drawText(players[i], r.getWidth()/2 - players[i].length()*16/2, offset, true, FontColor.BLACK);
			
			offset += vPad;
	        offset += vPad + 16;
		}
	}
}
