package org.alien8.server;

import java.net.InetAddress;
import org.alien8.ship.Ship;

public class Player {

  private String name;
  private InetAddress ip;
  private int udpPort;
  private Ship ship;

  /**
   * Constructor
   * @param name Name the player
   * @param ip IP address of the player
   * @param port UDP port number of the player
   * @param s Player's ship
   */
  public Player(String name, InetAddress ip, int port, Ship s) {
    this.name = name;
    this.ip = ip;
    this.udpPort = port;
    this.ship = s;
  }
  
  /**
   * Get the player's name
   * @return The player's name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get the player's IP address
   * @return The player's IP address
   */
  public InetAddress getIP() {
    return this.ip;
  }

  /**
   * Get the player's UDP port number
   * @return The player's UDP port number
   */
  public int getPort() {
    return this.udpPort;
  }

  /**
   * Get the player's ship
   * @return The player's ship
   */
  public Ship getShip() {
    return this.ship;
  }

  /**
   * String representation of the player
   */
  public String toString() {
    return name + ", " + ip.getHostAddress() + ", " + udpPort;
  }

}
