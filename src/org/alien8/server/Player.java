package org.alien8.server;

import java.net.*;

import org.alien8.ship.Ship;

public class Player {
	
	private InetAddress ip = null;
	private String name = null;
	private Ship ship = null;
	// add port number
	private int port = 4400;
	private ClientHandler ch = null;
	
	public Player(InetAddress ip, Ship s, ClientHandler ch, String name) {
		this.ip = ip;
		this.ship = s;
		this.ch = ch;
		this.name = name;
	}
	
	public Player(InetAddress ip, Ship s, int port, ClientHandler ch, String name) {
		this.ip = ip;
		this.ship = s;
		this.port = port;
		this.ch = ch;
		this.name = name;
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
	
	public String getName() {
		return this.name;
	}
	
	public ClientHandler getClientHandler() {
		return this.ch;
	}

}
