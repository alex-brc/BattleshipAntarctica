package org.alien8.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;

public class ServerGameStateSender extends Thread {
	
	private InetAddress clientIP = null;
	private DatagramSocket socket = null;
	private ModelManager model = ModelManager.getInstance();
	private boolean run = true;
	
	public ServerGameStateSender(InetAddress ip, DatagramSocket ds) {
		clientIP = ip;
		socket = ds;
	}
	
	public void run() {
		// Send game state snapshot 60 times per second
		while (run) {
			try {
				
				// Serialize the game state object into byte array 
				ConcurrentLinkedQueue<Entity> gameState = model.getEntities();
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
				objOut.writeObject(gameState);
				byte[] gameStateByte = byteOut.toByteArray();
				
				// Create a packet for holding the game state byte data
		        DatagramPacket packet = new DatagramPacket(gameStateByte, gameStateByte.length, clientIP, 4446);
		        
		        // Send the game state packet to client
		        socket.send(packet);
		        
		        // Thread pauses operation according to snapshot sending rate
		        sleep(1000 / Parameters.SNAPSHOTS_PER_SECOND);
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
	
	public InetAddress getClientIP() {
		return clientIP;
	}
	
	public void end() {
		run = false;
	}
	

}
