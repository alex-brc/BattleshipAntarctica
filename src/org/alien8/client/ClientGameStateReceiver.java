package org.alien8.client;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.core.*;
import org.alien8.managers.ModelManager;

public class ClientGameStateReceiver extends Thread {
	
	private DatagramSocket socket = null;
	private boolean run = true;

	public ClientGameStateReceiver(DatagramSocket ds) {
		socket = ds;
	}
	
	public void run() {
		while (run) {
			try {
				// Create a packet for receiving game state packet
			    byte[] buf = new byte[1024];
			    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    
			    // Receive a game state packet and obtain the byte data
			    socket.receive(packet); 
			    byte[] gameStateByte = packet.getData();
			    
			    // Deserialize the game state byte data into object1
			    ByteArrayInputStream byteIn = new ByteArrayInputStream(gameStateByte);
			    ObjectInputStream objIn = new ObjectInputStream(byteIn);
			    ConcurrentLinkedQueue<Entity> gameState = (ConcurrentLinkedQueue<Entity>) objIn.readObject();
			    
			    // Update the game state
			    ModelManager.getInstance().setEntities(gameState);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}
		}
	}
	
	public void end() {
		run = false;
	}
	
}
