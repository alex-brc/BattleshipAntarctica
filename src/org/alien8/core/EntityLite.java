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

  /**
   * Constructor for Player Ship
   * 
   * @param serial Serial of the ship
   * @param entityType Entity type number for ship
   * @param position Position of the ship
   * @param toBeDeleted Whether the ship is to be deleted
   * @param direction Direction of the ship
   * @param speed Speed of the ship
   * @param health Health of the ship
   * @param frontTurretDirection Front turret direction of the ship
   * @param rearTurretDirection Rear turret direction of the ship
   * @param frontTurretCharge Front turret charge of the ship
   * @param rearTurretCharge Rear turret charge of the ship
   * @param colour Colour of the ship
   * @param itemType Item type of the ship
   * @param effectType Effect type of the ship
   * @param clientIP Client's IP address of the player that control the ship
   * @param clientUdpPort Client's UDP port number of the player that control the ship
   */
  public EntityLite(long serial, int entityType, Position position, boolean toBeDeleted,
      double direction, double speed, double health, double frontTurretDirection,
      double rearTurretDirection, double frontTurretCharge, double rearTurretCharge, int colour,
      int itemType, int effectType, InetAddress clientIP, int clientUdpPort) {
    this.serial = serial;
    this.entityType = entityType;
    this.position = position;
    this.toBeDeleted = toBeDeleted;
    this.direction = direction;
    this.speed = speed;
    this.health = health;
    this.frontTurretDirection = frontTurretDirection;
    this.rearTurretDirection = rearTurretDirection;
    this.frontTurretCharge = frontTurretCharge;
    this.rearTurretCharge = rearTurretCharge;
    this.colour = colour;
    this.itemType = itemType;
    this.effectType = effectType;
    this.clientIP = clientIP;
    this.clientUdpPort = clientUdpPort;
  }

  /**
   * Constructor for AI ship
   * 
   * @param serial Serial number of the ship
   * @param entityType Entity type number for ship
   * @param position Position of the ship
   * @param toBeDeleted Whether the ship is to be deleted
   * @param direction Direction of the ship
   * @param speed Speed of the ship
   * @param health Health of the ship
   * @param frontTurretDirection Front turret direction of the ship
   * @param rearTurretDirection Rear turret direction of the ship
   * @param effectType Effect type of the ship
   * @param colour Colour of the ship
   */
  public EntityLite(long serial, int entityType, Position position, boolean toBeDeleted,
      double direction, double speed, double health, double frontTurretDirection,
      double rearTurretDirection, int effectType, int colour) {
    this.serial = serial;
    this.entityType = entityType;
    this.position = position;
    this.toBeDeleted = toBeDeleted;
    this.direction = direction;
    this.speed = speed;
    this.health = health;
    this.frontTurretDirection = frontTurretDirection;
    this.rearTurretDirection = rearTurretDirection;
    this.colour = colour;
    this.effectType = effectType;
  }

  /**
   * Constructor for Bullet
   * 
   * @param serial Serial number of the bullet
   * @param entityType Entity type number for bullet
   * @param position Position of the bullet
   * @param toBeDeleted Whether the bullet is to be deleted
   * @param direction Direction of the bullet
   * @param speed Speed of the bullet
   * @param distance Distance that will be traveled by the bullet
   * @param travelled Distance travreled by the bullet
   * @param source Source of the bullet
   */
  public EntityLite(long serial, int entityType, Position position, boolean toBeDeleted,
      double direction, double speed, double distance, double travelled, long source) {
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

  /**
   * Constuctor for pickup
   * 
   * @param entityType Entity type number for pickup
   * @param position Position of the pickup
   * @param pickupType Type of the pickup
   * @param toBeDeleted Whether the pickup is to be deleted
   */
  public EntityLite(int entityType, Position position, int pickupType, boolean toBeDeleted) {
    this.entityType = entityType;
    this.position = position;
    this.pickupType = pickupType;
    this.toBeDeleted = toBeDeleted;
  }

  /**
   * Constructor for plane drooper
   * 
   * @param entityType Entity type number for plane dropper
   * @param position Position of the plane dropper
   * @param toBeDeleted Whether the plane dropper is to be deleted
   * @param direction Direction of the plane dropper
   */
  public EntityLite(int entityType, Position position, boolean toBeDeleted, double direction) {
    this.entityType = entityType;
    this.position = position;
    this.toBeDeleted = toBeDeleted;
    this.direction = direction;
  }

  /**
   * String representation of this EntityLite
   */
  public String toString() {
    return "Serial: " + serial + ", " + "EntityType: " + entityType;
  }

}
