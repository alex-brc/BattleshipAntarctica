package org.alien8.server;

import java.net.*;

import org.alien8.ship.Ship;

public class Player {
	
	private InetAddress ip = null;
	private Ship ship = null;
	// add port number
	private int port = 4400;
	private ClientHandler ch = null;
	
	public Player(InetAddress ip, Ship s, ClientHandler ch) {
		this.ip = ip;
		this.ship = s;
		this.ch = ch;
	}
	
	public Player(InetAddress ip, Ship s, int port, ClientHandler ch) {
		this.ip = ip;
		this.ship = s;
		this.port = port;
		this.ch = ch;
	}

	public InetAddress getIP() {
		return this.ip;
	}
	
	public Ship getShip() {
		return this.ship;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public ClientHandler getClientHandler() {
		return this.ch;
	}

}
