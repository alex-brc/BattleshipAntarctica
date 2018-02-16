package org.alien8.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;

public class Server {
	
	private static InetAddress hostIP = null;
	private static ServerSocket tcpSocket = null;
	private static DatagramSocket udpSocket = null;
	private static ConcurrentLinkedQueue<Entity> lastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
	private static ModelManager model = ModelManager.getInstance();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static ArrayList<ServerGameStateSender> sgssList = new ArrayList<ServerGameStateSender>();
	private static boolean run = true;
	
	public static void main(String[] args) {
		try {
			setHostIP();
			tcpSocket = new ServerSocket(4446, 50, hostIP);
			udpSocket = new DatagramSocket(4446, hostIP);
			System.out.println("Port: " + tcpSocket.getLocalPort());
			System.out.println("IP: " + tcpSocket.getInetAddress());
			
			initializeGameState();
			
			// Create a thread for receiving input sample from client
			ServerInputSampleReceiver sisr = new ServerInputSampleReceiver(udpSocket);
			sisr.start();
			
			// Process clients' connect/disconnect request
			while (run) {
				// Receive and process client's packet
				System.out.println("Waiting for client request....");
				Socket client = tcpSocket.accept();
				InetAddress clientIP = client.getInetAddress();
				ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream toClient = new ObjectOutputStream(client.getOutputStream());
				Boolean clientRequest = (Boolean) fromClient.readObject();
			    processClientRequest(clientIP, clientRequest, toClient);
			}
			
			tcpSocket.close();
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
		// Add ice
		//for(AABB aabb : model.getMap().getAABBs()) {
			//model.addEntity(aabb.getEntity());
			//lastSyncedEntities.add(aabb.getEntity());
		//}
		
	}
	
	public static void processClientRequest(InetAddress clientIP, Boolean clientRequest, ObjectOutputStream toClient) {
		if (clientRequest) { // Connect request
			setupClient(clientIP, toClient);
		}
		else if (!clientRequest) { // Disconnect Request
			disconnectPlayer(clientIP);
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
		        
		        if (niName.equals("eth1") || niName.equals("wlan1")) {
		        	Enumeration<InetAddress> addrs = nic.getInetAddresses();
			        while (addrs.hasMoreElements()) {
			            InetAddress addr = addrs.nextElement();
			            System.out.println(addr);
			            System.out.println(niName);
			            if (addr instanceof Inet4Address) {
			            	hostIP = addr;
			            } 
			        }
		        }
		        
		        try {
					hostIP = Inet4Address.getLocalHost();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	public static void setupClient(InetAddress clientIP, ObjectOutputStream toClient) {
		if (!isClientConnected(clientIP)) {
			// For generating a random position without ice for ship spawning, causing bug at the moment
//			boolean[][] iceGrid = modelManager.getMap().getIceGrid();
//			Random r = new Random();
//			double randomX = 0;
//			double randomY = 0;
//			boolean isIcePosition = true;
//			
//			// Generate a random position without ice for ship spawning
//			while (isIcePosition) {
//				randomX = (double) r.nextInt(Parameters.MAP_WIDTH);
//				randomY = (double) r.nextInt(Parameters.MAP_HEIGHT);
//				
//				if (!iceGrid[(int) randomX][(int) randomY]) {
//					isIcePosition = false;
//				}
//			}
			
			// Setup client's ship
			Ship s = new Ship(new Position(200, 200), 0);
						
			playerList.add(new Player(clientIP, s));
			model.addEntity(s);
			
			// Send a full snapshot of current game state to the client
			sendFullSnapshot(clientIP, toClient, model.getEntities());
			
			// Create a dedicated thread for sending difference in game state to client
			ServerGameStateSender sgss = new ServerGameStateSender(clientIP, udpSocket, lastSyncedEntities);
			sgssList.add(sgss);
			sgss.start();
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
				long serial = s.getSerial();
				Position position = s.getPosition();
				boolean isToBeDeleted = s.isToBeDeleted();
				double direction = s.getDirection();
				double speed = s.getSpeed();
				double health = s.getHealth();
				
				EntitiesLite.add(new EntityLite(serial, 0, position, isToBeDeleted, direction, speed, health));	
			}
			else if (e instanceof SmallBullet) {
				SmallBullet sb = (SmallBullet) e;
				long serial = sb.getSerial();
				Position position = sb.getPosition();
				boolean isToBeDeleted = sb.isToBeDeleted();
				double direction = sb.getDirection();
				double speed = sb.getSpeed();
				double distance = sb.getDistance();
				double travelled = sb.getTravelled();
				long source = sb.getSource();
				
				EntitiesLite.add(new EntityLite(serial, 1, position, isToBeDeleted, direction, speed, distance, travelled, source));	
			}
			else if (e instanceof BigBullet) {
				BigBullet bb = (BigBullet) e;
				long serial = bb.getSerial();
				Position position = bb.getPosition();
				boolean isToBeDeleted = bb.isToBeDeleted();
				double direction = bb.getDirection();
				double speed = bb.getSpeed();
				double distance = bb.getDistance();
				double travelled = bb.getTravelled();
				long source = bb.getSource();
				
				EntitiesLite.add(new EntityLite(serial, 2, position, isToBeDeleted, direction, speed, distance, travelled, source));	
			}
		}
		
		return EntitiesLite;
	}
	
	/* 
	 * Send the compressed set of entities (full snapshot) to client
	 */
	private static void sendFullSnapshot(InetAddress clientIP, ObjectOutputStream toClient, ConcurrentLinkedQueue<Entity> ents) {
		ArrayList<EntityLite> entitiesLite = calculateEntitiesLite(ents);
		try {
			toClient.writeObject(entitiesLite);
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
					
					// Stop and remove the dedicated sgss thread for the player
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
