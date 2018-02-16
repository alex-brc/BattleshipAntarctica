package org.alien8.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.alien8.client.ClientInputSample;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;

public class ServerInputSampleReceiver extends Thread {

	private DatagramSocket udpSocket = null;
	private ModelManager model = ModelManager.getInstance();
	private boolean run = true;
	
	public ServerInputSampleReceiver(DatagramSocket ds) {
		udpSocket = ds;
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		long currentTime = 0;
		double catchUp = 0;
		while (run) {
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
			    //System.out.println("read");
			    if(inputSample.escPressed) {
			    	System.out.println("BYE");
			    	System.exit(-1);
			    }
			    
			    // Update the game state according the input sample
			    currentTime = System.nanoTime();

			    // Get the amount of update()s the model needs to catch up
			    // 
			    //                  timeNow - timeLastUpdateWasDone    --> 
			    // timeToCatchUp = ----------------------------------
			    //							deltaTPerTick              --> how long a "tick" is
			    // 
			    catchUp += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

			    // Call update() as many times as needed to compensate before rendering
			    while (catchUp >= 1) {
			    	model.updateServer(clientIP, inputSample); // <-- Uncomment this line to test networking
			    	catchUp--;
			    	// Update last time
			    	lastTime = System.nanoTime();
			    	System.out.println("update");
			    }
			    //System.out.println("Entities: " + model.getEntities().toString());
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
