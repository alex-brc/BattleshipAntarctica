package org.alien8.client;

import java.io.*;
import java.net.*;

public class ClientInputSampleSender extends Thread {
	private final int INPUT_SAMPLING_RATE = 60;
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
				// Serialize the command object into byte array 
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
				objOut.writeObject(new ClientInputSample());
				byte[] clientInputSampleByte = byteOut.toByteArray();
				
				DatagramPacket packet = new DatagramPacket(clientInputSampleByte, clientInputSampleByte.length, serverIP, 4446);
				
				// Send the client input sample packet to the server
				socket.send(packet);
				
				// Thread pauses operation according to input sampling rate
				sleep(1000 / INPUT_SAMPLING_RATE);
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
