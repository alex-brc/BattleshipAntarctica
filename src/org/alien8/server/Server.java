package org.alien8.server;

import java.io.*;
import java.net.*;
import java.util.*;

import org.alien8.client.ClientInputSample;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.AABB;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;

public class Server {
	
	private static DatagramSocket socket = null;
	private static ModelManager modelManager = ModelManager.getInstance();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static ArrayList<ServerGameStateSender> sgssList = new ArrayList<ServerGameStateSender>();
	private static boolean run = true;
	
	public static void main(String[] args) {
		try {
			// Initialize the game state with only ice present
			initializeGameState();
			socket = new DatagramSocket(4446);
			
			while (run) {
				// Create a packet for receiving client's packet
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				
				System.out.println("Waiting for packet....");
				// Receive and process client's packet
			    socket.receive(packet);
			    System.out.println("A packet received!");
			    processPacket(packet);
			}
			
			socket.close();
		}
		catch (SocketException e) {
			e.printStackTrace();
			socket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			socket.close();
		}
	}
	
	public static void initializeGameState() {
		// Add ice
		for(AABB aabb : modelManager.getMap().getAABBs()) {
			modelManager.addEntity(aabb.getEntity());
		}
	}
	
	public static void processPacket(DatagramPacket packet) {
		try {
			// Get the IP from the packet to identify the sender
			InetAddress clientIP = packet.getAddress();
			
			// Get the byte data from client's packet
			byte[] packetByte = packet.getData();
			
			ByteArrayInputStream byteIn = new ByteArrayInputStream(packetByte);
			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			
			if (objIn.readObject() instanceof ClientInputSample) { // Server receive a client's input sample
				ClientInputSample cis = (ClientInputSample) objIn.readObject();
				
				// Update the game state
				modelManager.updateServer(cis);
			}
			else if (objIn.readObject() instanceof Boolean) { // Server receive a client's network request
				// Deserialize client's byte data into Boolean object (representing connect/disconnect request)
				Boolean clientNetworkRequest = (Boolean) objIn.readObject();
				
				if (clientNetworkRequest.booleanValue()) { // Connect Request
					setupClient(clientIP);
				}
				else if ( !(clientNetworkRequest.booleanValue()) ) { // Disconnect Request
					disconnectPlayer(clientIP);
				}
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}	
	}
	
	public static boolean isClientConnected(InetAddress clientIP) {
		for (Player p : playerList) {
			if (p.getIP().equals(clientIP)) {
				return true;
			}
		}
		return false;
	}
	
	public static Player getPlayerByIp(InetAddress clientIP) {
		for (Player p : playerList) {
			if (p.getIP().equals(clientIP)) {
				return p;
			}
		}	
		return null;
	}
	
	public static void setupClient(InetAddress clientIP) {
		if (!isClientConnected(clientIP)) {
			// Setup client's ship
			Random r = new Random();
			double randomX = r.nextInt(Parameters.MAP_WIDTH + 1);
			double randomY = r.nextInt(Parameters.MAP_HEIGHT + 1);
			Ship s = new Ship(new Position(randomX, randomY), 0d);
			
			// Add client to current player list
			playerList.add(new Player(clientIP, s));
			
			// Spawn client's ship at random location without ice
			modelManager.addEntity(s);
			
			// Create a dedicated thread for sending game state to the client
			ServerGameStateSender sgss = new ServerGameStateSender(clientIP, socket);
			sgssList.add(sgss);
			sgss.start();
		}
	}
	
	public static void disconnectPlayer(InetAddress clientIP) {
		if (isClientConnected(clientIP)) {
			for (Player p : playerList) {
				if (p.getIP().equals(clientIP)) {
					// Remove player from the PlayerList
					playerList.remove(p);
					
					// Stop and remove the dedicated thread for the player
					getSGSSThreadByIP(clientIP, sgssList).end();
					removeSGSSThreadByIP(clientIP, sgssList);
				}
			}
		}
	}
	
	public static ServerGameStateSender getSGSSThreadByIP (InetAddress clientIP, ArrayList<ServerGameStateSender> sgssList) {
		for (ServerGameStateSender sgss : sgssList) {
			if (clientIP.equals(sgss.getClientIP())) {
				return sgss;
			}
		}
		return null;
	}
	
	public static void removeSGSSThreadByIP(InetAddress clientIP, ArrayList<ServerGameStateSender> sgssList) {
		for (ServerGameStateSender sgss : sgssList) {
			if (clientIP.equals(sgss.getClientIP())) {
				sgssList.remove(sgss);
			}
		}
	}
	
}
