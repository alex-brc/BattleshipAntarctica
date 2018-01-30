package org.alien8.server;

import java.util.ArrayList;

public class PlayerList {
	private ArrayList<Player> playerList = null;
	
	public PlayerList() {
		playerList = new ArrayList<Player>();
	}
	
	public void addPlayer(Player p) {
		playerList.add(p);
	}
	
	public void removePlayer(Player p) {
		playerList.remove(p);
	}
}
