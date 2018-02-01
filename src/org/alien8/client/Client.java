package org.alien8.client;

import java.net.*;

public class Client {
	
	public void connect(String serverIPStr) {
		try {
			DatagramSocket socket = new DatagramSocket(4446);
			InetAddress serverIP = InetAddress.getByName(serverIPStr);
			new ClientGameStateReceiver(serverIP, socket).start();
			new ClientCommandSender(serverIP, socket).start();
		}
		catch (SocketException se) {
			
		}
		catch (UnknownHostException uhe) {
			
		}
	}
	
}
