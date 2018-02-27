package org.alien8.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.client.ClientInputSample;
import org.alien8.core.ClientRequest;
import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;

public class Server {
	
	private static InetAddress hostIP = null;
	private static InetAddress groupIP = null;
	private static String groupIPStr = "224.0.0.5"; // multicast group ipString
	private static ServerSocket tcpSocket = null;
	private static DatagramSocket udpSocket = null;
	private static ConcurrentLinkedQueue<Entity> lastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
	private static ConcurrentHashMap<Player, ClientInputSample> latestCIS = new ConcurrentHashMap<Player, ClientInputSample>();
	private static ModelManager model = ModelManager.getInstance();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static int serverPort = 4446;
	private static int clientMultiCastPort = 4445;
	private static volatile boolean run = true;
	
	public static void main(String[] args) {
		try {
			setHostIP();
			tcpSocket = new ServerSocket(serverPort, 50, hostIP);
			udpSocket = new DatagramSocket(serverPort, hostIP);
			System.out.println("TCP socket Port: " + tcpSocket.getLocalPort());
			System.out.println("TCP socket IP: " + tcpSocket.getInetAddress());
			System.out.println("UDP socket Port: " + udpSocket.getLocalPort());
			System.out.println("UDP socket IP: " + udpSocket.getLocalAddress());
			
			// Process clients' connect/disconnect request
			while (run) {
				// Receive and process client's packet
				System.out.println("Waiting for client request....");
				Socket client = tcpSocket.accept();
				InetAddress clientIP = client.getInetAddress();
				ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
				ClientRequest cr = (ClientRequest) fromClient.readObject();
			    processClientRequest(clientIP, cr, toClient);
			}
			
			tcpSocket.close();
			udpSocket.close();
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}
	
	public static void initializeGameState() {
	    Ship notPlayer = new Ship(new Position(100, 100), 0);
	    notPlayer.setSpeed(0.8);
	    model.addEntity(notPlayer);
	}
	
	public static void processClientRequest(InetAddress clientIP, ClientRequest cr, ObjectOutputStream toClient) {
		if (cr.getType() == 0) { // Connect request
			setupClient(clientIP, cr.getUdpPort(), toClient);
		}
		else if (cr.getType() == 1) { // Disconnect Request
			disconnectPlayer(clientIP, cr.getUdpPort());
		}
	}
	
	public static boolean isPlayerConnected(InetAddress clientIP, int clientPort) {
		for (Player p : playerList) {
			if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
				return true;
			}
		}
		return false;
	}
	
	public static Player getPlayerByIpAndPort(InetAddress clientIP, int clientPort) {
		for (Player p : playerList) {
			if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
				return p;
			}
		}	
		return null;
	}
	
	public static void setHostIP() {
		try {
			hostIP = Inet4Address.getLocalHost();
        	groupIP = InetAddress.getByName(groupIPStr);
		}
        catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static void setupClient(InetAddress clientIP, int clientUdpPort, ObjectOutputStream toClient) {
		if (!isPlayerConnected(clientIP, clientUdpPort)) {
			boolean[][] iceGrid = model.getMap().getIceGrid();
			Random r = new Random();
			double randomX = 0;
			double randomY = 0;
			boolean isIcePosition = true;
			
			// Choose a random position without ice for ship spawning
			while (isIcePosition) {
				randomX = (double) r.nextInt(Parameters.MAP_WIDTH);
				randomY = (double) r.nextInt(Parameters.MAP_HEIGHT);
				
				if (!iceGrid[(int) randomX][(int) randomY]) {
					isIcePosition = false;
				}
			}
			
			// Setup the player information for the client
			Ship s = new Ship(new Position(randomX, randomY), 0);
			Player p = new Player(clientIP, clientUdpPort, s);
			playerList.add(p);
			System.out.println("Player added: " + p);
			
			// Initialize the game state if it is the first client connection
			if (playerList.size() == 1)
				initializeGameState();
			
			model.addEntity(s);
			
			// Update the last synced set of entities right before sending a full snapshot to client for full sync
			ConcurrentLinkedQueue<Entity> newLastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
			for (Entity e : model.getEntities()) {
				newLastSyncedEntities.add((Entity) deepClone(e));
			}
			lastSyncedEntities = newLastSyncedEntities;	
			
			// Send a full snapshot of current game state to the client
			sendFullSnapshot(toClient, model.getEntities());
			
			// Start the ServerMulticastSender thread if it is the first client connection
			if (playerList.size() == 1)
				new ServerMulticastSender(udpSocket, clientMultiCastPort, groupIP, lastSyncedEntities, latestCIS).start();
		}
	}
	
	/* 
	 * Create a compressed set of entities (game state) from the original set of entities
	 */
	private static ArrayList<EntityLite> calculateEntitiesLite(ConcurrentLinkedQueue<Entity> ents) {
		ArrayList<EntityLite> EntitiesLite = new ArrayList<EntityLite>();
		
		for (Entity e : ents) {
			if (e instanceof Ship) {
				Ship s = (Ship) e;
				EntitiesLite.add(new EntityLite(s.getSerial(), 1, 0, s.getPosition(), s.isToBeDeleted(), s.getDirection(), s.getSpeed(), s.getHealth(), 
						 s.getFrontTurretDirection(), s.getMidTurretDirection(), s.getRearTurretDirection()));	
			}
			else if (e instanceof SmallBullet) {
				SmallBullet sb = (SmallBullet) e;
				EntitiesLite.add(new EntityLite(sb.getSerial(), 1, 1, sb.getPosition(), sb.isToBeDeleted(), sb.getDirection(), sb.getSpeed(),
						 		 sb.getDistance(), sb.getTravelled(), sb.getSource()));	
			}
			else if (e instanceof BigBullet) {
				BigBullet bb = (BigBullet) e;
				EntitiesLite.add(new EntityLite(bb.getSerial(), 1, 2, bb.getPosition(), bb.isToBeDeleted(), bb.getDirection(), bb.getSpeed(),
						 		 bb.getDistance(), bb.getTravelled(), bb.getSource()));	
			}
		}
		
		return EntitiesLite;
	}
	
	/* 
	 * Send the compressed set of all entities (full snapshot) to client
	 */
	private static void sendFullSnapshot(ObjectOutputStream toClient, ConcurrentLinkedQueue<Entity> ents) {
		ArrayList<EntityLite> entitiesLite = calculateEntitiesLite(ents);
		try {
			toClient.writeObject(entitiesLite);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void disconnectPlayer(InetAddress clientIP, int clientPort) {
		if (isPlayerConnected(clientIP, clientPort)) {
			for (Player p : playerList) {
				if (p.getIP().equals(clientIP) && p.getPort() == clientPort) {
					// Remove player from the PlayerList
					model.getEntities().remove(p.getShip());
					latestCIS.remove(p);
					playerList.remove(p);
				}
			}
		}
	}
	
	/*
	 * This method makes a "deep clone" of any Java object it is given.
	 */
	 public static Object deepClone(Object object) {
	   try {
	     ByteArrayOutputStream baos = new ByteArrayOutputStream();
	     ObjectOutputStream oos = new ObjectOutputStream(baos);
	     oos.writeObject(object);
	     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	     ObjectInputStream ois = new ObjectInputStream(bais);
	     return ois.readObject();
	   }
	   catch (Exception e) {
	     e.printStackTrace();
	     return null;
	   }
	 }
	 
}
