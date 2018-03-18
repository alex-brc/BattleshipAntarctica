package org.alien8.core;

import java.io.Serializable;

public class ClientMessage implements Serializable {

  private static final long serialVersionUID = 7767942303743306515L;
  private int type; // 0 for connect, 1 for ready
  private String playerName;
  private int udpPort;

  public ClientMessage(int type, int udpPort, String playerName) {
    this.type = type;
    this.udpPort = udpPort;
    this.playerName = playerName;
  }

  public int getType() {
    return this.type;
  }

  public int getUdpPort() {
    return this.udpPort;
  }
  
  public String getPlayerName() {
	  return this.playerName;
  }

}
