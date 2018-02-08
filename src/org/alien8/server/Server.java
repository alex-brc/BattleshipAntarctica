package org.alien8.server;

import java.io.*;
import java.net.*;
import java.util.*;

import org.alien8.client.ClientInputSample;

public class Server {
	private static DatagramSocket socket = null;
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static ArrayList<ServerGameStateSender> sgssList = new ArrayList<ServerGameStateSender>();
	private static boolean run = true;
	
	public static void main(String[] args) {
		try {
			socket = new DatagramSocket(4446);
			
			while (run) {
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				
				// Receive client's input command packet
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
		try {
			byte[] packetByte = packet.getData();
			ByteArrayInputStream byteIn = new ByteArrayInputStream(packetByte);
			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			InetAddress clientIP = packet.getAddress();
			
			if (objIn.readObject() instanceof ClientInputSample) { // Server receive a client's input sample
				ClientInputSample clientInputSample = (ClientInputSample) objIn.readObject();
				
				if (clientInputSample.lmbPressed) {
					if (isClientConnected(clientIP)) {

						// TODO: update the game state according to the input command
						
					}
				}
				else if (clientInputSample.lmbPressed) {
					if (isClientConnected(clientIP)) {

						// TODO: update the game state according to the input command
						
					}
				}
				else if (clientInputSample.rmbPressed) {
					if (isClientConnected(clientIP)) {

						// TODO: update the game state according to the input command
						
					}
				}
				else if (clientInputSample.wPressed) {
					if (isClientConnected(clientIP)) {

						// TODO: update the game state according to the input command
						
					}
				}
				else if (clientInputSample.aPressed) {
					if (isClientConnected(clientIP)) {

						// TODO: update the game state according to the input command
						
					}
				}
				else if (clientInputSample.sPressed) {
					if (isClientConnected(clientIP)) {
						
						// TODO: update the game state according to the input command
						
					}
				}
				else if (clientInputSample.dPressed) {
					if (isClientConnected(clientIP)) {
						
						// TODO: update the game state according to the input command
						
					}
				}
				else if (clientInputSample.spacePressed) {
					if (isClientConnected(clientIP)) {
						
						// TODO: update the game state according to the input command
						
					}
				}
			}
			else if (objIn.readObject() instanceof Boolean) { // Server receive a client's network request
				Boolean clientNetworkRequest = (Boolean) objIn.readObject();
				
				if (clientNetworkRequest.booleanValue()) {
					if (!isClientConnected(clientIP)) {
						playerList.add(new Player(clientIP));
						ServerGameStateSender sgss = new ServerGameStateSender(clientIP, socket);
						sgssList.add(sgss);
						sgss.start();
					}
				}
				else if ( !(clientNetworkRequest.booleanValue()) ) {
					if (isClientConnected(clientIP)) {
						for (int i = 0; i < playerList.size(); i++) {
							if (playerList.get(i).getIP().equals(clientIP)) {
								playerList.remove(playerList.get(i));
								getSGSSThreadByIP(clientIP, sgssList).end();
								removeSGSSThreadByIP(clientIP, sgssList);
							}
						}
					}
				}
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
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
	
	public static ServerGameStateSender getSGSSThreadByIP (InetAddress clientIP, ArrayList<ServerGameStateSender> sgssList) {
		for (int i = 0; i < sgssList.size(); i++) {
			if (clientIP.equals(sgssList.get(i).getClientIP())) {
				return sgssList.get(i);
			}
		}
		return null;
	}
	
	public static void removeSGSSThreadByIP(InetAddress clientIP, ArrayList<ServerGameStateSender> sgssList) {
		for (int i = 0; i < sgssList.size(); i++) {
			if (clientIP.equals(sgssList.get(i).getClientIP())) {
				sgssList.remove(sgssList.get(i));
				break;
			}
		}
	}
	
	
}
