package org.alien8.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.alien8.client.ClientInputSample;
import org.alien8.managers.ModelManager;

public class ServerInputSampleReceiver extends Thread {

	private DatagramSocket udpSocket = null;
	private ModelManager model = ModelManager.getInstance();
	private boolean run = true;
	
	public ServerInputSampleReceiver(DatagramSocket ds) {
		udpSocket = ds;
	}
	
	public void run() {
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
			    
			    // Update the game state according the input sample
			    model.updateServer(clientIP, inputSample);
			    System.out.println("Entities: " + model.getEntities().toString());
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
