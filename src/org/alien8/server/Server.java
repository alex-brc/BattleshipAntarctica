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
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.score.ScoreBoard;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;
import org.alien8.util.LogManager;
import org.alien8.util.ServerShutdownHook;

public class Server {
	
	private static InetAddress hostIP = null;
	private static ServerSocket tcpSocket = null;
	private static DatagramSocket udpSocket = null;
	private static DatagramSocket eventSocket = null;
	private static ConcurrentLinkedQueue<Entity> lastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
	private static ModelManager model = ModelManager.getInstance();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static LinkedList<AudioEvent> audioEvents = new LinkedList<AudioEvent>();
	private static boolean run = true;
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
		try {
			setHostIP();
			tcpSocket = new ServerSocket(4446, 50, hostIP);
			udpSocket = new DatagramSocket(4446, hostIP);
			eventSocket = new DatagramSocket(4447, hostIP);
			System.out.println("TCP socket Port: " + tcpSocket.getLocalPort());
			System.out.println("TCP socket IP: " + tcpSocket.getInetAddress());
			System.out.println("UDP socket Port: " + udpSocket.getLocalPort());
			System.out.println("UDP socket IP: " + udpSocket.getLocalAddress());
			System.out.println("UDP event socket Port: " + eventSocket.getLocalPort());
			System.out.println("UDP event socket IP: " + eventSocket.getLocalAddress());
			
			initializeGameState();
			
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
		LogManager.getInstance().log("Server", LogManager.Scope.INFO, "Initialising game state...");
	    Ship notPlayer = new Ship(new Position(100, 100), 0, 0xF8F8F8); // white
	    
	    // Initialise ScoreBoard 
	    // Without a thread, it doesn't listen on input.
	    ScoreBoard.getInstance();
	    
	    notPlayer.setSpeed(0.8);
	    model.addEntity(notPlayer);
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
//			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
//		    while (nis.hasMoreElements()) {
//		        NetworkInterface nic = nis.nextElement();
//		        String niName = nic.getName();
//		        
//		        if (niName.equals("eth1") || niName.equals("wlan1")) {
//		        	Enumeration<InetAddress> addrs = nic.getInetAddresses();
//			        while (addrs.hasMoreElements()) {
//			            InetAddress addr = addrs.nextElement();
//			            if (addr instanceof Inet4Address) {
//			            	hostIP = addr;
//			            } 
//			        }
//		        }
//		        
//		        // Make sure if the host has ethernet connection, it would be the priority choice
//		        if (niName.equals("eth1")) {
//		        	break;
//		        }
//		    }
		    
		    // If the above fails to set the IP properly, try the following
	        if (hostIP == null) {
				hostIP = Inet4Address.getLocalHost();
	        }
		}
//		catch (SocketException se) {
//		se.printStackTrace();
//	}
        catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static void setupClient(InetAddress clientIP, ObjectOutputStream toClient) {
		if (!isClientConnected(clientIP)) {
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
			
			// Setup client's ship
			int randColour = (new Random()).nextInt(0xFFFFFF); 
			Ship s = new Ship(new Position(randomX, randomY), 0, randColour);
			model.addEntity(s);
			
			// Update the last synced set of entities right before sending a full snapshot to client for full sync
			ConcurrentLinkedQueue<Entity> newLastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
			for (Entity e : model.getEntities()) {
				newLastSyncedEntities.add((Entity) deepClone(e));
			}
			lastSyncedEntities = newLastSyncedEntities;
			
			// Send a full snapshot of current game state to the client
			sendFullSnapshot(clientIP, toClient, model.getEntities());
			
			// Create a dedicated thread for receiving client's input and sending game state to client
			ClientHandler ch = new ClientHandler(udpSocket, eventSocket, clientIP, lastSyncedEntities);
			// TODO: ADD NAMES TO PLAYERS
			playerList.add(new Player(clientIP, s, ch, "NO_NAMES_YET"));
			
			// Get relevant port and IP information
			ch.init();
			
			// Start the dedicated thread
			ch.start();
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
						 s.getFrontTurretDirection(), s.getMidTurretDirection(), s.getRearTurretDirection(), s.getColour()));	
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
					// Stop the dedicated thread for the client
					p.getClientHandler().end();
					// Remove player from the PlayerList
					playerList.remove(p);
				}
			}
		}
	}
	/**
	 * Gets player by bullet. Used in awarding score.
	 * 
	 * @param bullet the bullet belonging to the player
	 * @return the player who owns the bullet
	 */
	public static Player getPlayer(Bullet bullet) {
		
		for(Player p : playerList) 
			if(p.getShip().getSerial() == bullet.getSource())
				return p;
		
		LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL, "Bullet source ship does not exist. Exiting...");
		System.exit(-1);
		return null;
	}
	
	public static AudioEvent getAudioEvent() {
		if(audioEvents.size() == 0)
			return null;
		return audioEvents.removeFirst();
	}
	
	public static void addAudioEvent(AudioEvent audioEvent) {
		audioEvents.add(audioEvent);
	}
	
	/**
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
