package org.alien8.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static DatagramSocket socket = null;
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static ArrayList<ServerGameStateSender> sgssList = new ArrayList<ServerGameStateSender>();
	private static boolean run = true;
	
	public static void main(String[] args) {
		try {
			socket = new DatagramSocket(4446);
			
			while (run) {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				// possible packet type: (1) Connection Request (2) in-game input command (3) Quit Request
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
		
		// if (1) Connection Request do the following
			if (!isClientConnected(clientIP)) {
				playerList.add(new Player(clientIP));
				ServerGameStateSender sgss = new ServerGameStateSender(clientIP, socket));
				sgssList.add(sgss);
				sgss.start();
			}
		
		// if (2) in-game input command do the following
			if (isClientConnected(clientIP)) {
				// TODO: process input command
				
				// TODO: update the game state according to the input command
			}
		
		// if (3) Quit Request do the following
			if (isClientConnected(clientIP)) {
				for (int i = 0; i < playerList.size(); i++) {
					if (playerList.get(i).getIP().equals(clientIP)) {
						playerList.remove(playerList.get(i));
						// TODO: Stop the ServerGameStateSender thread of the corresponding client
					}
				}
			}
	}
	
	public static boolean isClientConnected(InetAddress clientIP) {
		for (Player p : playerList) {
			if (p.getIP().equals(clientIP)) {
				return true;
			}
		}
		return false;
	}
}
