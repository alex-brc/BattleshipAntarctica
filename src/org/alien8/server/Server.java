package org.alien8.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.client.ClientInputSample;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.AABB;
import org.alien8.physics.Position;
import org.alien8.ship.Ship;

public class Server {
	
	private static InetAddress hostIP = null;
	private static DatagramSocket socket = null;
	private static ArrayList<Entity> initialGameState = new ArrayList<Entity>();
	private static ModelManager modelManager = ModelManager.getInstance();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static ArrayList<ServerGameStateSender> sgssList = new ArrayList<ServerGameStateSender>();
	private static boolean run = true;
	
	public static void main(String[] args) {
		try {
			setHostIP();
			socket = new DatagramSocket(4446, hostIP);
			System.out.println("Port: " + socket.getLocalPort());
			System.out.println("IP: " + socket.getLocalAddress());
			
			// Initialize the game state with only ice present
			initializeGameState();
			
			while (run) {
				// Create a packet for receiving client's packet
				byte[] buf = new byte[65536];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				
				// Receive and process client's packet
				System.out.println("Waiting for packet....");
			    socket.receive(packet);
			    System.out.println("A packet received");
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
			initialGameState.add(aabb.getEntity());
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
			Object obj = objIn.readObject();
			
			if (obj instanceof ClientInputSample) { // Server receive a client's input sample
				ClientInputSample cis = (ClientInputSample) obj;
				
				// Update the game state
				modelManager.updateServer(cis);
				System.out.println("Game State updated according to client's input sample");
			}
			else if (obj instanceof Boolean) { // Server receive a client's network request
				// Deserialize client's byte data into Boolean object (representing connect/disconnect request)
				Boolean clientNetworkRequest = (Boolean) obj;
				
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
	
	public static void setHostIP() {
		try {
			// Obtain an host IP reachable by the client
			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
		    while (nis.hasMoreElements()) {
		        NetworkInterface nic = nis.nextElement();
		        String niName = nic.getName();
		        
		        if (niName.equals("eth1") || niName.equals("wlan0")) {
		        	Enumeration<InetAddress> addrs = nic.getInetAddresses();
			        while (addrs.hasMoreElements()) {
			            InetAddress addr = addrs.nextElement();
			            if (addr instanceof Inet4Address) {
			            	hostIP = addr;
			            } 
			        }
		        }
		        
		        // Make sure if the host has ethernet connection, it would be the priority choice
		        if (niName.equals("eth1")) {
		        	break;
		        }
		    }
		}
		catch (SocketException se) {
			se.printStackTrace();
		}
	}

	public static void setupClient(InetAddress clientIP) {
		if (!isClientConnected(clientIP)) {
			boolean[][] iceGrid = modelManager.getMap().getIceGrid();
			Random r = new Random();
			double randomX = 0;
			double randomY = 0;
			boolean isIcePosition = true;
			
			// Generate a random position without ice for ship spawning
			while (isIcePosition) {
				randomX = r.nextInt(Parameters.MAP_WIDTH);
				randomY = r.nextInt(Parameters.MAP_HEIGHT);
				
				if (!iceGrid[(int) randomX][(int) randomY]) {
					isIcePosition = false;
				}
			}
			
			// Setup client's ship
			Ship s = new Ship(new Position(randomX, randomY), 0d);
			
			// Add client to current player list
			playerList.add(new Player(clientIP, s));
			
			// Spawn client's ship at random location without ice
			modelManager.addEntity(s);
			
			// Send a full snapshot of initial game state to the client
			sendFullSnapshot(clientIP, initialGameState);
			System.out.println("Full snapshot of initial game state sent");
			
			// Create a dedicated thread for sending compressed game state to the client
			ServerGameStateSender sgss = new ServerGameStateSender(clientIP, socket);
			sgssList.add(sgss);
			sgss.start();
		}
	}
	
	private static void sendFullSnapshot(InetAddress clientIP, ArrayList<Entity> ents) {
		try {
			ArrayList<Entity> fullSnapshotPartition = new ArrayList<Entity>();
	
			// Send the full snapshot using multiple packets
			for (int i = 0; i < ents.size(); i++) {
				if ( i == ents.size() - 1 || (i % Parameters.LIST_LENGTH_PER_PACKET == 0 && i != 0) ) {
					fullSnapshotPartition.add(ents.get(i));
					ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
					ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
					objOut.writeObject(fullSnapshotPartition);
					byte[] fullSnapshotPartitionByte = byteOut.toByteArray();
					
			        DatagramPacket packet = new DatagramPacket(fullSnapshotPartitionByte, fullSnapshotPartitionByte.length, clientIP, 4445);

			        socket.send(packet);
			        fullSnapshotPartition.clear();
				}
				else if (i % Parameters.LIST_LENGTH_PER_PACKET != 0 || i == 0) {
					fullSnapshotPartition.add(ents.get(i));
				}
			}
			
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(new ArrayList<Entity>());
			byte[] emptyArrayListByte = byteOut.toByteArray();
	        DatagramPacket packet = new DatagramPacket(emptyArrayListByte, emptyArrayListByte.length, clientIP, 4445);
	        
	        // Send an empty array list to notice client the completion of full snapshot sending
	        socket.send(packet);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
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
