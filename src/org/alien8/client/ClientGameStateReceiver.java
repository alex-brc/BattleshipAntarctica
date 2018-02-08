package org.alien8.client;

import java.net.*;
import java.io.*;
import java.util.*;

import org.alien8.core.*;

public class ClientGameStateReceiver extends Thread {
	
	private DatagramSocket socket = null;
	private boolean run = true;

	public ClientGameStateReceiver(DatagramSocket ds) {
		socket = ds;
	}
	
	public void run() {
		while (run) {
			try {
			    byte[] buf = new byte[1024];
			    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    
			    // Receive gameStateByte array from server
			    socket.receive(packet); 
			    byte[] gameStateByte = packet.getData();
			    
			    // Deserialize gameStateByte array into gameState object
			    ByteArrayInputStream byteIn = new ByteArrayInputStream(gameStateByte);
			    ObjectInputStream objIn = new ObjectInputStream(byteIn);
			    LinkedList<Entity> gameState = (LinkedList<Entity>) objIn.readObject();
			    
			    // TODO: update all state of entities according to the received gameState object
			    
			    
			    
			    
			    
			    
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}
		}
	}
	
	public void end() {
		run = false;
	}
}
