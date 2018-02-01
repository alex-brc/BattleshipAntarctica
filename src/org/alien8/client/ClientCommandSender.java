package org.alien8.client;

import java.net.*;
import java.io.*;

public class ClientCommandSender extends Thread {
	
	private InetAddress serverIP = null;
	private DatagramSocket socket = null;
	private boolean run = true;
	
	public ClientCommandSender(InetAddress serverIP, DatagramSocket socket) {
		this.serverIP = serverIP;
		this.socket = socket;
	}
	
	public void run() {
		while(run) {
			try {
				// TODO: code for serializing command object into command byte[]...
				
				
				
				
				
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length, serverIP, 4446);
				socket.send(packet);
			}
			catch (IOException e) {
				
			}
		}
	}
	
	public 
}
