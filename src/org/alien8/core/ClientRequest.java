package org.alien8.core;

import java.io.Serializable;

public class ClientRequest implements Serializable {
	
	private static final long serialVersionUID = 7767942303743306515L;
	private int type; // 0 for connect, 1 for disconnect
	private int udpPort;
	
	public ClientRequest(int type, int udpPort) {
		this.type = type;
		this.udpPort = udpPort;
	}
	
	public int getType() {
		return this.type;
	}
	
	public int getUdpPort() {
		return this.udpPort;
	}
	
}
