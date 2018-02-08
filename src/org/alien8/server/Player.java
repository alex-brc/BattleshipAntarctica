package org.alien8.server;

import java.net.*;

import org.alien8.ship.Ship;

public class Player {
	
	private InetAddress ip = null;
	private Ship ship = null;
	
	public Player(InetAddress ip, Ship s) {
		this.ip = ip;
		ship = s;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public Ship getShip() {
		return ship;
	}
	
}
