package org.alien8.server;

import java.io.*;
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
		// Send game state snapshot 60 times per second
		
		while (run) {
			try {
				LinkedList<Entity> gameState = model.getEntities();
				
				// Serialize the game state object into byte array 
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut)
				objOut.writeObject(gameState);
				byte[] gameStateByte = byteOut.toByteArray();
		        DatagramPacket packet = new DatagramPacket(gameStateByte, gameStateByte.length, clientIP, 4446);
		        
		        // Send the game state byte array to client
		        socket.send(packet);
		        
		        // Thread pauses operation according to snapshot sending rate
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
