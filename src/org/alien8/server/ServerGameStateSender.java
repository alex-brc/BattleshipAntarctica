package org.alien8.server;

import java.io.IOException;
import java.net.*;

public class ServerGameStateSender extends Thread {
	
	InetAddress clientIP = null;
	DatagramSocket socket = null;
	
	public ServerGameStateSender(InetAddress clientIP, DatagramSocket socket) {
		this.clientIP = clientIP;
		this.socket = socket;
	}
	
	public void run() {
		try {
			// TODO: code for serializing game state object into game state byte[]...
			
			
			
			

			byte[] buf = new byte[256];
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, clientIP, 4446);
	        socket.send(packet);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
