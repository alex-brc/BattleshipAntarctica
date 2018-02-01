package org.alien8.client;

import java.net.*;
import java.io.*;

public class ClientGameStateReceiver extends Thread {
	
	private InetAddress serverIP = null;
	private DatagramSocket socket = null;

	public ClientGameStateReceiver(InetAddress serverIP, DatagramSocket socket) {
		this.serverIP = serverIP;
		this.socket = socket;
	}
	
	public void run() {
		try {
		    byte[] buf = new byte[256];
		    DatagramPacket packet = new DatagramPacket(buf, buf.length);
		    socket.receive(packet);
		    byte[] gameStateByte = packet.getData();
		    
		    // TODO: code for deserializing game state byte[] into game state object...
		}
		catch (IOException e) {
			
		}
	}
}
