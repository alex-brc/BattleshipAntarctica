package org.alien8.core;

import java.io.Serializable;

public class ClientMessage implements Serializable {

  private static final long serialVersionUID = 7767942303743306515L;
  private int type;
  private String playerName;
  private int udpPort;

  /**
   * Constructor
   * 
   * @param type Type of the message
   * @param udpPort Client's UDP port number
   * @param playerName Client's in-game name
   */
  public ClientMessage(int type, int udpPort, String playerName) {
    this.type = type;
    this.udpPort = udpPort;
    this.playerName = playerName;
  }

  /**
   * Get the message type
   * @return
   */
  public int getType() {
    return this.type;
  }
  
  /**
   * Get client's in-game name
   * @return
   */
  public String getPlayerName() {
    return this.playerName;
  }

  /**
   * Get client's UDP port number
   * @return
   */
  public int getUdpPort() {
    return this.udpPort;
  }

}
