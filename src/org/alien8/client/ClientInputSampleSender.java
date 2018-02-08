package org.alien8.client;

import java.io.*;
import java.net.*;

import org.alien8.core.Parameters;

public class ClientInputSampleSender extends Thread {
	
	private DatagramSocket socket = null;
	private InetAddress serverIP = null;
	private boolean run = true;
	
	public ClientInputSampleSender(InetAddress ip, DatagramSocket ds) {
		serverIP = ip;
		socket = ds;
	}
	
	public void run() {
		
		// Send client input sample 60 times per second
		while(run) {
			try {
				// Serialize the input sample object into byte array 
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
				objOut.writeObject(new ClientInputSample());
				byte[] clientInputSampleByte = byteOut.toByteArray();
				
				// Create a packet for holding the input sample byte data
				DatagramPacket packet = new DatagramPacket(clientInputSampleByte, clientInputSampleByte.length, serverIP, 4446);
				
				// Send the client input sample packet to the server
				socket.send(packet);
				
				// Thread pauses operation according to input sampling rate
				sleep(1000 / Parameters.INPUT_SAMPLING_RATE);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
	}
	
	public void end() {
		run = false;
	}
	
}
