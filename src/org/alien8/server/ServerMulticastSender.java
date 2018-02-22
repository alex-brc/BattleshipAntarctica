package org.alien8.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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

public class ServerMulticastSender extends Thread{
	
	
	private int clientPort = 4445;
	private InetAddress clientIP = null;
	private MulticastSocket multiSocket = null;
	private ConcurrentLinkedQueue<Entity> lastSyncedEntities = null;
	private ConcurrentLinkedQueue<Entity> currentEntities = ModelManager.getInstance().getEntities();
	private boolean run = true;
	private String clientGroup = null;
		
	
	
	//public ServerMulticastSender(InetAddress ip, MulticastSocket ms, ConcurrentLinkedQueue<Entity> ents, int port) {
	ServerMulticastSender(MulticastSocket ms, ConcurrentLinkedQueue<Entity> ents, String group, int port) {
		//clientIP = ip;
		clientPort = port;		
		multiSocket = ms;
		clientGroup = group;
		// init
		lastSyncedEntities = ents;
	
	}
	
	public void run() {
		// Send game state snapshot 60 times per second
		while (run) {
			try {
				ArrayList<EntityLite> difference = ClientHandler.calculateDifference(lastSyncedEntities, currentEntities);
				
				// System.out.println("server currentEntities: " + currentEntities);

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
				
				// System.out.println("server newLastSyncedEntities: " + newLastSyncedEntities);
				
		        //System.out.println("server sender ready send, client PORT:  " + clientPort);
								
				// Serialize the difference arraylist into byte array
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
				objOut.writeObject(difference);
				byte[] differenceByte = byteOut.toByteArray();
				
			
				// Create a packet for holding the difference byte data
		        DatagramPacket packet = new DatagramPacket(differenceByte, differenceByte.length, InetAddress.getByName(clientGroup), clientPort);

		        // Send the difference packet to client
		        multiSocket.send(packet);
		        
		        //System.out.println("server sender finish send");

			        				
		        // Thread pauses operation according to snapshot sending rate
		        sleep(1000 / Parameters.TICKS_PER_SECOND);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}	

