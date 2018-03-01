package org.alien8.server;

import java.net.InetAddress;
import org.alien8.ship.Ship;

public class Player {

  private String name;
  private InetAddress ip;
  private int udpPort;
  private Ship ship;

  public Player(String name, InetAddress ip, int port, Ship s) {
    this.name = name;
    this.ip = ip;
    this.udpPort = port;
    this.ship = s;
  }

  public InetAddress getIP() {
    return this.ip;
  }

  public int getPort() {
    return this.udpPort;
  }

  public Ship getShip() {
    return this.ship;
  }

  public String toString() {
    return name + ", " + ip.getHostAddress() + ", " + udpPort;
  }

  public String getName() {
    return this.name;
  }

}
