package org.alien8.client;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

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
				// Create a packet for receiving compressed game state packet
			    byte[] buf = new byte[65536];
			    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    
			    // Receive a compressed game state  state packet and obtain the byte data
			    socket.receive(packet);
			    byte[] compressedGameStateByte = packet.getData();
			    
			    // Deserialize the compressed game state byte data into object
			    ByteArrayInputStream byteIn = new ByteArrayInputStream(compressedGameStateByte);
			    ObjectInputStream objIn = new ObjectInputStream(byteIn);
			    ArrayList<Entity> compressedGameState = (ArrayList<Entity>) objIn.readObject();
			    
			    // Sync the game state with server
			    ModelManager.getInstance().sync(compressedGameState);
			    System.out.println("Game state synced with server");
				System.out.println("Compressed game state list length: " + compressedGameState.size());
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
