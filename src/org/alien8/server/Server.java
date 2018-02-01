package org.alien8.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static DatagramSocket socket = null;
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static boolean run = true;
	
	public static void main(String[] args) {
		try {
			socket = new DatagramSocket(4446);
			
			while (run) {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    socket.receive(packet);
			    processPacket(packet);
			}
			
			socket.close();
			
		}
		catch (SocketException e) {
			e.printStackTrace();
			socket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			socket.close();
		}

	}
	
	public static void processPacket(DatagramPacket packet) {
		byte[] packetByte = packet.getData();
		InetAddress clientIP = packet.getAddress();
		boolean isClientConnected = false;
		
		for (Player p : playerList)) {
			if ( p.getIP().getAddress().equals(clientIP.getAddress()) ) {
				isClientConnected = true;
			}
		}
		
		if (isClientConnected) {
			// TODO: process input command
			
			// TODO: update the game state according to the input command
		}
		else {
			playerList.add(new Player(clientIP));
			new ServerGameStateSender(clientIP, socket).start();
		}
	}
}
