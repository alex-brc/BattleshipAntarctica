package org.alien8.server;

import java.io.*;
import java.net.*;

public class Server {
	private static DatagramSocket socket = null;
	private static PlayerList playerList = new PlayerList();
	
	public static void main(String[] args) {
		try {
			socket = new DatagramSocket(4446);
			
			while (true) {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    socket.receive(packet);
			    InetAddress clientIP = packet.getAddress();
			    playerList.addPlayer(new Player(clientIP));
			    new ServerGameStateSender(clientIP, socket).start();
			    new ServerCommandReceiver(clientIP, socket).start();
			}
			
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}
