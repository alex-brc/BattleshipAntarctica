package org.alien8.server;

import java.net.*;
import java.io.*;

public class ServerCommandReceiver extends Thread {
	
	InetAddress clientIP = null;
	DatagramSocket socket = null;
	
	public ServerCommandReceiver(InetAddress clientIP, DatagramSocket socket) {
		this.clientIP = clientIP;
		this.socket = socket;
	}
	
	public void run() {
		try {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            byte[] commandByte = packet.getData();
            
            // TODO: code for deserializing command byte[] into Command object...
        }
		catch (IOException e) {
            e.printStackTrace();
        }
	}

}
