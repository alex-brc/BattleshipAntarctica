package org.alien8.server;

import java.net.*;

import org.alien8.ship.Ship;

public class Player {
	
	private InetAddress ip;
	private int udpPort;
	private Ship ship;
	
	public Player(InetAddress ip, int port, Ship s) {
		this.ip = ip;
		this.udpPort = port;
		this.ship = s;
	}

	public InetAddress getIP() {
		return this.ip;
	}
	
	public int getPort() {
		return this.udpPort;
	}
	
	public Ship getShip() {
		return this.ship;
	}
	
	public String toString() {
		return ip.getHostAddress() + ", " + udpPort;
	}

}
