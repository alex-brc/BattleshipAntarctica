package org.alien8.core;

import java.io.Serializable;
import java.net.InetAddress;
import org.alien8.physics.Position;

/*
 * This class represent the compressed version of an entity, used when sending change in game state
 * across the network
 */
public class EntityLite implements Serializable {

  private static final long serialVersionUID = -7757472834374226318L;

  // For Ship and bullet
  public long serial;
  public int entityType;
  public Position position;
  public boolean toBeDeleted = false;
  public double direction;
  public double speed;

  // For Ship
  public double health;
  public double frontTurretDirection;
  public double midTurretDirection;
  public double rearTurretDirection;
  public int colour;
  
  // For player's Ship
  public InetAddress clientIP;
  public int clientUdpPort;

  // For bullet
  public double distance;
  public double travelled;
  public long source;

  // For Player Ship
  public EntityLite(long serial, int entityType, Position position,
      boolean toBeDeleted, double direction, double speed, double health,
      double frontTurretDirection, double midTurretDirection, double rearTurretDirection,
      int colour, InetAddress clientIP, int clientUdpPort) {
    this.serial = serial;
    this.entityType = entityType;
    this.position = position;
    this.toBeDeleted = toBeDeleted;
    this.direction = direction;
    this.speed = speed;
    this.health = health;
    this.frontTurretDirection = frontTurretDirection;
    this.midTurretDirection = midTurretDirection;
    this.rearTurretDirection = rearTurretDirection;
    this.colour = colour;
    this.clientIP = clientIP;
    this.clientUdpPort = clientUdpPort;
  }
  
  // For AI Ship
  public EntityLite(long serial, int entityType, Position position,
      boolean toBeDeleted, double direction, double speed, double health,
      double frontTurretDirection, double midTurretDirection, double rearTurretDirection,
      int colour) {
    this.serial = serial;
    this.entityType = entityType;
    this.position = position;
    this.toBeDeleted = toBeDeleted;
    this.direction = direction;
    this.speed = speed;
    this.health = health;
    this.frontTurretDirection = frontTurretDirection;
    this.midTurretDirection = midTurretDirection;
    this.rearTurretDirection = rearTurretDirection;
    this.colour = colour;
  }

  // For Bullet
  public EntityLite(long serial, int entityType, Position position,
      boolean toBeDeleted, double direction, double speed, double distance, double travelled,
      long source) {
    this.serial = serial;
    this.entityType = entityType;
    this.position = position;
    this.toBeDeleted = toBeDeleted;
    this.direction = direction;
    this.speed = speed;
    this.distance = distance;
    this.travelled = travelled;
    this.source = source;
  }

  public String toString() {
    return serial + "," + entityType;
  }

}
