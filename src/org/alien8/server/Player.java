package org.alien8.server;

import java.net.*;

import org.alien8.ship.Ship;

public class Player {
	
	private InetAddress ip;
	private int port;
	private Ship ship;
	
	public Player(InetAddress ip, int port, Ship s) {
		this.ip = ip;
		this.port = port;
		this.ship = s;
	}

	public InetAddress getIP() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public Ship getShip() {
		return this.ship;
	}
	
	public String toString() {
		return ip.getHostAddress() + ", " + port;
	}

}
