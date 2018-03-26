package org.alien8.core;

import java.io.Serializable;
import java.net.InetAddress;
import org.alien8.physics.Position;

/**
 * This class represent the compressed version of an entity, used when sending game state across the
 * network.
 */
public class EntityLite implements Serializable {

  private static final long serialVersionUID = -7757472834374226318L;

  // For Ship and bullet
  public long serial;
  /*
   * 0 - Player Ship 1 - AI Ship 2 - Bullet
   */
  public int entityType;
  public Position position;
  public boolean toBeDeleted = false;
  public double direction;
  public double speed;

  // For Ship
  public double health;
  public double frontTurretDirection;
  public double rearTurretDirection;
  public int colour;

  // For player's Ship
  public InetAddress clientIP;
  public int clientUdpPort;
  public double frontTurretCharge;
  public double rearTurretCharge;
  public int itemType;
  public int effectType;

  // For bullet
  public double distance;
  public double travelled;
  public long source;

  // For pickups
  public int pickupType;

  public long getSerial() {
    return serial;
  }

  public void setSerial(long serial) {
    this.serial = serial;
  }

  public int getEntityType() {
    return entityType;
  }

  public void setEntityType(int entityType) {
    this.entityType = entityType;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public boolean isToBeDeleted() {
    return toBeDeleted;
  }

  public void setToBeDeleted(boolean toBeDeleted) {
    this.toBeDeleted = toBeDeleted;
  }

  public double getDirection() {
    return direction;
  }

  public void setDirection(double direction) {
    this.direction = direction;
  }

  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public double getHealth() {
    return health;
  }

  public void setHealth(double health) {
    this.health = health;
  }

  public double getFrontTurretDirection() {
    return frontTurretDirection;
  }

  public void setFrontTurretDirection(double frontTurretDirection) {
    this.frontTurretDirection = frontTurretDirection;
  }

  public double getRearTurretDirection() {
    return rearTurretDirection;
  }

  public void setRearTurretDirection(double rearTurretDirection) {
    this.rearTurretDirection = rearTurretDirection;
  }

  public int getColour() {
    return colour;
  }

  public void setColour(int colour) {
    this.colour = colour;
  }

  public InetAddress getClientIP() {
    return clientIP;
  }

  public void setClientIP(InetAddress clientIP) {
    this.clientIP = clientIP;
  }

  public int getClientUdpPort() {
    return clientUdpPort;
  }

  public void setClientUdpPort(int clientUdpPort) {
    this.clientUdpPort = clientUdpPort;
  }

  public double getFrontTurretCharge() {
    return frontTurretCharge;
  }

  public void setFrontTurretCharge(double frontTurretCharge) {
    this.frontTurretCharge = frontTurretCharge;
  }

  public double getRearTurretCharge() {
    return rearTurretCharge;
  }

  public void setRearTurretCharge(double rearTurretCharge) {
    this.rearTurretCharge = rearTurretCharge;
  }

  public int getItemType() {
    return itemType;
  }

  public void setItemType(int itemType) {
    this.itemType = itemType;
  }

  public int getEffectType() {
    return effectType;
  }

  public void setEffectType(int effectType) {
    this.effectType = effectType;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double getTravelled() {
    return travelled;
  }

  public void setTravelled(double travelled) {
    this.travelled = travelled;
  }

  public long getSource() {
    return source;
  }

  public void setSource(long source) {
    this.source = source;
  }

  public int getPickupType() {
    return pickupType;
  }

  public void setPickupType(int pickupType) {
    this.pickupType = pickupType;
  }

  /**
   * String representation of this EntityLite
   */
  @Override
  public String toString() {
    return "Serial: " + serial + ", " + "EntityType: " + entityType;
  }

}
