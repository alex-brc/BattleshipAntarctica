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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.client.ClientInputSample;
import org.alien8.core.ClientRequest;
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
  private static InetAddress groupIP = null;
  private static String groupIPStr = "224.0.0.5"; // multicast group ipString
  private static ServerSocket tcpSocket = null;
  private static DatagramSocket udpSocket = null;
  // private static DatagramSocket eventSocket = null;
  private static ConcurrentLinkedQueue<Entity> lastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
  private static ConcurrentHashMap<Player, ClientInputSample> latestCIS = new ConcurrentHashMap<Player, ClientInputSample>();
  private static ModelManager model = ModelManager.getInstance();
  private static ArrayList<Player> playerList = new ArrayList<Player>();
  private static LinkedList<GameEvent> events = new LinkedList<GameEvent>();
  private static int serverPort = 4446;
  private static int clientMultiCastPort = 4445;
  private static Long seed = (new Random()).nextLong();
  private static volatile boolean run = true;
	
  public static void main(String[] args) {
	    Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
	    model.makeMap(seed);
		try {
			setHostIP();
			tcpSocket = new ServerSocket(serverPort, 50, hostIP);
			udpSocket = new DatagramSocket(serverPort, hostIP);
			// eventSocket = new DatagramSocket(4447, hostIP);
			System.out.println("TCP socket Port: " + tcpSocket.getLocalPort());
			System.out.println("TCP socket IP: " + tcpSocket.getInetAddress());
			System.out.println("UDP socket Port: " + udpSocket.getLocalPort());
			System.out.println("UDP socket IP: " + udpSocket.getLocalAddress());
		    // System.out.println("UDP event socket Port: " + eventSocket.getLocalPort());
		    // System.out.println("UDP event socket IP: " + eventSocket.getLocalAddress());
		    
			// Process clients' connect/disconnect request
			while (run) {
				// Receive and process client's packet
		        LogManager.getInstance().log("Server", LogManager.Scope.INFO,
		                "Waiting for client request...");
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
	    LogManager.getInstance().log("Server", LogManager.Scope.INFO, "Initialising game state...");
	    Ship notPlayer = new Ship(new Position(100, 100), 0, 0xF8F8F8); // white

	    // Initialise ScoreBoard
	    // Without a thread, it doesn't listen on input.
	    ScoreBoard.getInstance();

	    notPlayer.setSpeed(0.8);
	    model.addEntity(notPlayer);
	    LogManager.getInstance().log("Server", LogManager.Scope.INFO,
	        "Game set up. Waiting for players.");
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
	    LogManager.getInstance().log("Server", LogManager.Scope.INFO,
	            "Client attempting connect from " + clientIP);
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
			
			// Setup client's ship
			int randColour = (new Random()).nextInt(0xFFFFFF);
			Ship s = new Ship(new Position(randomX, randomY), 0, randColour);
			
		    // TODO: ADD NAMES TO PLAYERS
		    int k = (new Random()).nextInt(1000);
		    String name = "RAND_NAME_" + k;
			Player p = new Player(name, clientIP, clientUdpPort, s);
			playerList.add(p);
			
		    // Add player to scoreboard
		    ScoreBoard.getInstance().add(p);
			
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
			
		    // Send the map seed to the client
		    sendMapSeed(clientIP, toClient, seed);

			// Start the ServerMulticastSender thread if it is the first client connection
			if (playerList.size() == 1)
				new ServerMulticastSender(udpSocket, clientMultiCastPort, groupIP, lastSyncedEntities, latestCIS).start();
		}
		else {
		      LogManager.getInstance().log("Server", LogManager.Scope.INFO,
		          "Client " + clientIP + " is already connected.");
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
	private static void sendFullSnapshot(ObjectOutputStream toClient, ConcurrentLinkedQueue<Entity> ents) {
		ArrayList<EntityLite> entitiesLite = calculateEntitiesLite(ents);
		try {
			toClient.writeObject(entitiesLite);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void sendMapSeed(InetAddress clientIP, ObjectOutputStream toClient, Long seed) {
		try {
			toClient.writeObject(seed);
		} catch (IOException ioe) {
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
			        // Remove player from scoreboard
			        ScoreBoard.getInstance().remove(p);
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

	    for (Player p : playerList)
	      if (p.getShip().getSerial() == bullet.getSource())
	        return p;

	    LogManager.getInstance().log("Server", LogManager.Scope.CRITICAL,
	        "Bullet source ship does not exist. Exiting...");
	    System.exit(-1);
	    return null;
	  }

	  public static void addEvent(GameEvent event) {
	    events.add(event);
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
	 
	 public static GameEvent getNextEvent() {
	   if (events.size() == 0)
	     return null;
	   return events.removeFirst();
     }

}
