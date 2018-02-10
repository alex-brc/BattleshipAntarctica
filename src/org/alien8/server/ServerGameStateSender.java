package org.alien8.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;

public class ServerGameStateSender extends Thread {
	
	private InetAddress clientIP = null;
	private DatagramSocket socket = null;
	private ModelManager modelManager = ModelManager.getInstance();
	private boolean run = true;
	
	public ServerGameStateSender(InetAddress ip, DatagramSocket ds) {
		clientIP = ip;
		socket = ds;
	}
	
	public void run() {
		// Send game state snapshot 60 times per second
		while (run) {
			try {
				// Create a compressed game state which hold all entities except ice
				ConcurrentLinkedQueue<Entity> gameState = modelManager.getEntities();
				ArrayList<Entity> compressedGameState = new ArrayList<Entity>();
				
				for (Entity e : gameState) {
					if (e instanceof Ship || e instanceof Bullet) {
						compressedGameState.add(e);
					}
				}
				
				// Serialize the compressed game state object into byte array
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
				objOut.writeObject(compressedGameState);
				byte[] compressedGameStateByte = byteOut.toByteArray();
				
				// Create a packet for holding the game state byte data
		        DatagramPacket packet = new DatagramPacket(compressedGameStateByte, compressedGameStateByte.length, clientIP, 4445);
		        
		        // Send the game state packet to client
		        socket.send(packet);
		        System.out.println("Compressed game state sent");
		        System.out.println("Compressed game state byte size: " + compressedGameStateByte.length + " Bytes");
				System.out.println("Compressed game state list length: " + compressedGameState.size());
		        
		        // Thread pauses operation according to snapshot sending rate
		        sleep(1000 / Parameters.SNAPSHOTS_PER_SECOND);
			}
			catch (IOException e) {
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
