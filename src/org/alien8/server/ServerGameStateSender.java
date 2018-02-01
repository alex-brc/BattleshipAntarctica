package org.alien8.server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import org.alien8.managers.*;
import org.alien8.core.*;

public class ServerGameStateSender extends Thread {
	
	private InetAddress clientIP = null;
	private DatagramSocket socket = null;
	private ModelManager model = ModelManager.getInstance();
	private final int SNAPSHOTS_PER_SECOND = 60;
	boolean run = true;
	
	public ServerGameStateSender(InetAddress clientIP, DatagramSocket socket) {
		this.clientIP = clientIP;
		this.socket = socket;
	}
	
	public void run() {
		// Send gameState 60 times per second
		while (run) {
			try {
				LinkedList<Entity> gameState = model.getEntities();
				// TODO: code for serializing game state object into game state byte[]...
				
				
				
				
				byte[] buf = new byte[256];
		        DatagramPacket packet = new DatagramPacket(buf, buf.length, clientIP, 4446);
		        socket.send(packet);
		        sleep(1000 / SNAPSHOTS_PER_SECOND);
			}
			catch (IOException e) {
				System.err.println("Error on sending game state packet");
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
