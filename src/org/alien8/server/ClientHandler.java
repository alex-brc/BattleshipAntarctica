package org.alien8.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alien8.client.ClientInputSample;
import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;

public class ClientHandler extends Thread {
	private DatagramSocket udpSocket = null;
	private InetAddress clientIP = null;
	private ModelManager model = ModelManager.getInstance();
	private ConcurrentLinkedQueue<Entity> lastSyncedEntities = null;
	private ConcurrentLinkedQueue<Entity> currentEntities = ModelManager.getInstance().getEntities();
	long lastTime = System.nanoTime();
    long currentTime = 0;
    double tick = 0;
	private static boolean run = true;
	
	public ClientHandler(DatagramSocket udpSocket, InetAddress clientIP, ConcurrentLinkedQueue<Entity> lastSyncedEntities) {
		this.udpSocket = udpSocket;
		this.clientIP = clientIP;
		this.lastSyncedEntities = lastSyncedEntities;
	}
	
	public void run() {
		while (run) {
			currentTime = System.nanoTime();
			tick += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

	        while (tick >= 1) {
	    	   this.readInputSample();
	    	   this.sendGameState();
	    	   tick--;
	           // Update last time
	           lastTime = System.nanoTime();
	        }
		}    
	}
	
	private void readInputSample() {
		try {
			// Create a packet for receiving input sample packet
		    byte[] buf = new byte[65536];
		    DatagramPacket packet = new DatagramPacket(buf, buf.length);
		    
		    // Receive an input sample packet and obtain its byte data
		    udpSocket.receive(packet);
		    InetAddress clientIP = packet.getAddress();
		    byte[] inputSampleByte = packet.getData();
		    
		    // Deserialize the input sample byte data into object
		    ByteArrayInputStream byteIn = new ByteArrayInputStream(inputSampleByte);
		    ObjectInputStream objIn = new ObjectInputStream(byteIn);
		    ClientInputSample inputSample = (ClientInputSample) objIn.readObject();
		    
		    // Update the game state according the input sample
		    model.updateServer(clientIP, inputSample);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}
	
	private void sendGameState() {
		try {
			System.out.println("Last synced entities: " + lastSyncedEntities);
			System.out.println("Current entities: " + currentEntities);
			ArrayList<EntityLite> difference = calculateDifference(lastSyncedEntities, currentEntities);
			System.out.println("Difference: " + difference);
			
			// Update the last synced set of entities right before sending the difference to client for syncing
			ConcurrentLinkedQueue<Entity> newLastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
			for (Entity e : currentEntities) {
				newLastSyncedEntities.add((Entity) deepClone(e));
			}
			lastSyncedEntities = newLastSyncedEntities;
			
			// Serialize the difference arraylist into byte array
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(difference);
			byte[] differenceByte = byteOut.toByteArray();
			
			// Create a packet for holding the difference byte data
	        DatagramPacket packet = new DatagramPacket(differenceByte, differenceByte.length, clientIP, 4445);
	        
	        // Send the difference packet to client
	        udpSocket.send(packet);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* 
	 * Calculate the difference between two set of entities, difference is represented as an arraylist of compressed entities that are modified or added or removed
	 */
	public static ArrayList<EntityLite> calculateDifference(ConcurrentLinkedQueue<Entity> lastSyncedEntities, ConcurrentLinkedQueue<Entity> currentEntities) {
		ArrayList<EntityLite> difference = new ArrayList<EntityLite>();
		
		if (currentEntities.size() >= lastSyncedEntities.size()) {
			for (Entity ce : currentEntities) {
				boolean entityToBeAdded = true;
				// See if the entity is being modified
				for (Entity lse : lastSyncedEntities) {
					if (ce.getSerial() == lse.getSerial()) {
						if (ce instanceof Ship) {
							Ship currentShip = (Ship) ce;
							Ship lastSyncedShip = (Ship) lse;
							if (!currentShip.equals(lastSyncedShip)) {
								difference.add(new EntityLite(currentShip.getSerial(), 0, 0, currentShip.getPosition(), currentShip.isToBeDeleted(), 
											   currentShip.getDirection(), currentShip.getSpeed(), currentShip.getHealth(), currentShip.getFrontTurretDirection(),
											   currentShip.getMidTurretDirection(), currentShip.getRearTurretDirection()));
							}
						}
						else if (ce instanceof SmallBullet) {
							SmallBullet currentSB = (SmallBullet) ce;
							SmallBullet lastSyncedSB = (SmallBullet) lse;
							if (!currentSB.equals(lastSyncedSB)) {
								difference.add(new EntityLite(currentSB.getSerial(), 0, 1, currentSB.getPosition(), currentSB.isToBeDeleted(),
											   currentSB.getDirection(), currentSB.getSpeed(), currentSB.getDistance(), currentSB.getTravelled(),
											   currentSB.getSource()));
								
							}
						}
						else if (ce instanceof BigBullet) {
							BigBullet currentBB = (BigBullet) ce;
							BigBullet lastSyncedBB = (BigBullet) lse;
							if (!currentBB.equals(lastSyncedBB)) {
								difference.add(new EntityLite(currentBB.getSerial(), 0, 2, currentBB.getPosition(), currentBB.isToBeDeleted(),
											   currentBB.getDirection(), currentBB.getSpeed(), currentBB.getDistance(), currentBB.getTravelled(),
											   currentBB.getSource()));
								
							}
						}
						entityToBeAdded = false;
						break;
					}
				}
				
				// If reaches this point, the entity is to be added
				if (entityToBeAdded) {
					if (ce instanceof Ship) {
						Ship currentShip = (Ship) ce;
						difference.add(new EntityLite(currentShip.getSerial(), 1, 0, currentShip.getPosition(), currentShip.isToBeDeleted(), 
								       currentShip.getDirection(), currentShip.getSpeed(), currentShip.getHealth(), currentShip.getFrontTurretDirection(),
									   currentShip.getMidTurretDirection(), currentShip.getRearTurretDirection()));
					}
					else if (ce instanceof SmallBullet) {
						SmallBullet currentSB = (SmallBullet) ce;
						difference.add(new EntityLite(currentSB.getSerial(), 1, 1, currentSB.getPosition(), currentSB.isToBeDeleted(),
								   currentSB.getDirection(), currentSB.getSpeed(), currentSB.getDistance(), currentSB.getTravelled(),
								   currentSB.getSource()));
					}
					else if (ce instanceof BigBullet) {
						BigBullet currentBB = (BigBullet) ce;
						difference.add(new EntityLite(currentBB.getSerial(), 1, 2, currentBB.getPosition(), currentBB.isToBeDeleted(),
								   currentBB.getDirection(), currentBB.getSpeed(), currentBB.getDistance(), currentBB.getTravelled(),
								   currentBB.getSource()));
					}
				}
			}
		}
		else if (lastSyncedEntities.size() > currentEntities.size()) {
			for (Entity lse : lastSyncedEntities) {
				boolean entityToBeRemoved = true;
				// See if the entity is being modified
				for (Entity ce : currentEntities) {
					if (ce.getSerial() == lse.getSerial()) {
						if (lse instanceof Ship) {
							Ship lastSyncedShip = (Ship) lse;
							Ship currentShip = (Ship) ce;
							if (!lastSyncedShip.equals(currentShip)) {
								difference.add(new EntityLite(currentShip.getSerial(), 0, 0, currentShip.getPosition(), currentShip.isToBeDeleted(), 
											   currentShip.getDirection(), currentShip.getSpeed(), currentShip.getHealth(), currentShip.getFrontTurretDirection(),
											   currentShip.getMidTurretDirection(), currentShip.getRearTurretDirection()));
							}
						}
						else if (lse instanceof SmallBullet) {
							SmallBullet lastSyncedSB = (SmallBullet) lse;
							SmallBullet currentSB = (SmallBullet) ce;						
							if (!lastSyncedSB.equals(currentSB)) {
								difference.add(new EntityLite(currentSB.getSerial(), 0, 1, currentSB.getPosition(), currentSB.isToBeDeleted(),
											   currentSB.getDirection(), currentSB.getSpeed(), currentSB.getDistance(), currentSB.getTravelled(),
											   currentSB.getSource()));
							}
						}
						else if (lse instanceof BigBullet) {
							BigBullet lastSyncedBB = (BigBullet) lse;
							BigBullet currentBB = (BigBullet) ce;
							if (!lastSyncedBB.equals(currentBB)) {
								difference.add(new EntityLite(currentBB.getSerial(), 0, 2, currentBB.getPosition(), currentBB.isToBeDeleted(),
											   currentBB.getDirection(), currentBB.getSpeed(), currentBB.getDistance(), currentBB.getTravelled(),
											   currentBB.getSource()));
							}
						}
						entityToBeRemoved = false;
						break;
					}
				}
				
				// If reaches this point, the entity is to be removed
				if (entityToBeRemoved) {
					if (lse instanceof Ship) {
						Ship lastSyncedShip = (Ship) lse;
						difference.add(new EntityLite(lastSyncedShip.getSerial(), 2, 0, lastSyncedShip.getPosition(), lastSyncedShip.isToBeDeleted(), 
									   lastSyncedShip.getDirection(), lastSyncedShip.getSpeed(), lastSyncedShip.getHealth(), lastSyncedShip.getFrontTurretDirection(),
									   lastSyncedShip.getMidTurretDirection(), lastSyncedShip.getRearTurretDirection()));
					}
					else if (lse instanceof SmallBullet) {
						SmallBullet lastsyncedSB = (SmallBullet) lse;
						difference.add(new EntityLite(lastsyncedSB.getSerial(), 2, 1, lastsyncedSB.getPosition(), lastsyncedSB.isToBeDeleted(),
									   lastsyncedSB.getDirection(), lastsyncedSB.getSpeed(), lastsyncedSB.getDistance(), lastsyncedSB.getTravelled(),
									   lastsyncedSB.getSource()));
					}
					else if (lse instanceof BigBullet) {
						BigBullet lastSyncedBB = (BigBullet) lse;
						difference.add(new EntityLite(lastSyncedBB.getSerial(), 2, 2, lastSyncedBB.getPosition(), lastSyncedBB.isToBeDeleted(),
									   lastSyncedBB.getDirection(), lastSyncedBB.getSpeed(), lastSyncedBB.getDistance(), lastSyncedBB.getTravelled(),
									   lastSyncedBB.getSource()));
					}
				}
			}
		}
		
		return difference;
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
	 
	 public void end() {
		 run = false;
	 }
}
