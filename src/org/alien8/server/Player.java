package org.alien8.server;

import java.net.*;

public class Player {
	
	private InetAddress ip = null;
	
	public Player(InetAddress ip) {
		this.ip = ip;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
}
