package org.alien8.client;

import java.net.*;
import java.io.*;

public class ClientGameStateReceiver extends Thread {
	
	private InetAddress serverIP = null;
	private DatagramSocket socket = null;
	private boolean run = true;

	public ClientGameStateReceiver(InetAddress serverIP, DatagramSocket socket) {
		this.serverIP = serverIP;
		this.socket = socket;
	}
	
	public void run() {
		while (run) {
			try {
			    byte[] buf = new byte[256];
			    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    socket.receive(packet);
			    byte[] gameStateByte = packet.getData();
			    
			    // TODO: deserializing game state byte[] into game state object...
			    
			    // TODO: update all state of entities according to the received game state object
			}
			catch (IOException e) {
				
			}
		}
	}
}
