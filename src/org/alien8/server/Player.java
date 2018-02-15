package org.alien8.server;

import java.net.*;

import org.alien8.ship.Ship;

public class Player {
	
	private InetAddress ip = null;
	private Ship ship = null;
	// add port number
	private int port = 4400;
	
	public Player(InetAddress ip, Ship s) {
		this.ip = ip;
		ship = s;
	}
	
	public Player(InetAddress ip, Ship s, int port) {
		this.ip = ip;
		this.ship = s;
		this.port = port;
	}

	
	public InetAddress getIP() {
		return ip;
	}
	
	public Ship getShip() {
		return ship;
	}
	
	public int getPort() {
		return this.port;
	}
	
}
