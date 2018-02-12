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
import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;
import org.alien8.ship.BigBullet;
import org.alien8.ship.Ship;
import org.alien8.ship.SmallBullet;

public class ServerGameStateSender extends Thread {
	
	private InetAddress clientIP = null;
	private DatagramSocket udpSocket = null;
	private ConcurrentLinkedQueue<Entity> lastSyncedEntities = null;
	private ConcurrentLinkedQueue<Entity> currentEntities = ModelManager.getInstance().getEntities();
	private boolean run = true;
	
	public ServerGameStateSender(InetAddress ip, DatagramSocket ds, ConcurrentLinkedQueue<Entity> ents) {
		clientIP = ip;
		udpSocket = ds;
		lastSyncedEntities = ents;
	}
	
	public void run() {
		// Send game state snapshot 60 times per second
		while (run) {
			try {
				ArrayList<EntityLite> difference = calculateDifference(lastSyncedEntities, currentEntities);
				
				// Update the last synced set of entities after calculating the difference
				ConcurrentLinkedQueue<Entity> newLastSyncedEntities = new ConcurrentLinkedQueue<Entity>();
				try {
					for (Entity e : currentEntities) {
						newLastSyncedEntities.add((Entity) e.clone());
					}
				}
				catch (CloneNotSupportedException cnse) {
					cnse.printStackTrace();
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
	
	/* 
	 * Calculate the difference between two set of entities, difference is represented as an arraylist of compressed entities that are modified or added
	 * TODO: entities that are removed is not identified, which causes problem such as bullets do not disappear
	 */
	public static ArrayList<EntityLite> calculateDifference(ConcurrentLinkedQueue<Entity> lastSyncedEntities, ConcurrentLinkedQueue<Entity> currentEntities) {
		ArrayList<EntityLite> difference = new ArrayList<EntityLite>();
		
		for (Entity ce : currentEntities) {
			if (ce instanceof Ship) {
				Ship currentShip = (Ship) ce;
				boolean entityChanged = true;
				
				for (Entity lse : lastSyncedEntities) {
					if (lse instanceof Ship) {
						Ship initialShip = (Ship) lse;
						if (initialShip.equals(currentShip)) {
							entityChanged = false;
							break;
						}
					}
				}
				
				if (entityChanged) {
					long serial = currentShip.getSerial();
					int type = 0; // 0 - Ship
					Position position = currentShip.getPosition();
					boolean toBeDeleted = currentShip.isToBeDeleted();
					double direction = currentShip.getDirection();
					double speed = currentShip.getSpeed();
					double health = currentShip.getHealth();
					difference.add(new EntityLite(serial, type, position, toBeDeleted, direction, speed, health));
				}
			}
			else if (ce instanceof SmallBullet) {
				SmallBullet currentSmallBullet = (SmallBullet) ce;
				boolean entityChanged = true;
				
				for (Entity lse : lastSyncedEntities) {
					if (lse instanceof SmallBullet) {
						SmallBullet initialSmallBullet = (SmallBullet) lse;
						if (initialSmallBullet.equals(currentSmallBullet)) {
							entityChanged = false;
							break;
						}
					}
				}
				
				if (entityChanged) {
					long serial = currentSmallBullet.getSerial();
					int type = 1; // 1 - SmallBullet
					Position position = currentSmallBullet.getPosition();
					boolean toBeDeleted = currentSmallBullet.isToBeDeleted();
					double direction = currentSmallBullet.getDirection();
					double speed = currentSmallBullet.getSpeed();
					double distance = currentSmallBullet.getDistance();
					double travelled = currentSmallBullet.getTravelled();
					long source = currentSmallBullet.getSource();
					difference.add(new EntityLite(serial, type, position, toBeDeleted, direction, speed, distance, travelled, source));
				}
			}
			else if (ce instanceof BigBullet) {
				BigBullet currentBigBullet = (BigBullet) ce;
				boolean entityChanged = true;
				
				for (Entity lse : lastSyncedEntities) {
					if (lse instanceof BigBullet) {
						BigBullet initialBigBullet = (BigBullet) lse;
						if (initialBigBullet.equals(currentBigBullet)) {
							entityChanged = false;
							break;
						}
					}
				}
				
				if (entityChanged) {
					long serial = currentBigBullet.getSerial();
					int type = 2; // 2 - BigBullet
					Position position = currentBigBullet.getPosition();
					boolean toBeDeleted = currentBigBullet.isToBeDeleted();
					double direction = currentBigBullet.getDirection();
					double speed = currentBigBullet.getSpeed();
					double distance = currentBigBullet.getDistance();
					double travelled = currentBigBullet.getTravelled();
					long source = currentBigBullet.getSource();
					difference.add(new EntityLite(serial, type, position, toBeDeleted, direction, speed, distance, travelled, source));
				}
			}
		}
		
		return difference;
	}
	
	public InetAddress getClientIP() {
		return clientIP;
	}
	
	public void end() {
		run = false;
	}

}
