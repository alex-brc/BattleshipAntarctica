package org.alien8.server;

import java.net.InetAddress;

import org.alien8.client.ClientInputSample;
import org.alien8.core.Controller;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;
import org.alien8.ship.Ship;

import net.jafama.FastMath;

public class Player implements Controller {

  private String name;
  private InetAddress ip;
  private int udpPort;
  private Ship ship;
  private ClientInputSample cis;

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

  @Override
  public void update() {
	ship.updateEffect();
	
	// Apply forward OR backward force
    if (cis.wPressed)
      PhysicsManager.applyForce(ship, Parameters.SHIP_FORWARD_FORCE, ship.getDirection());
    else if (cis.sPressed)
      PhysicsManager.applyForce(ship, Parameters.SHIP_BACKWARD_FORCE,
          PhysicsManager.shiftAngle(ship.getDirection() + FastMath.PI));

    // Apply rotation
    if (cis.aPressed)
      PhysicsManager.rotateEntity(ship,
          (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
    if (cis.dPressed)
      PhysicsManager.rotateEntity(ship,
          Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);

    // Apply "friction"
    PhysicsManager.applyFriction(ship);

    // Use item
    if (cis.spacePressed)
      ship.useItem();

    // Prepare for shooting
    // Orientation
    ship.setTurretsDirection(cis.mousePosition);

    if (cis.lmbPressed)
      ship.frontTurretCharge();
    else
      ship.frontTurretShoot();

    if (cis.rmbPressed)
      ship.rearTurretCharge();
    else
      ship.rearTurretShoot();

  }
  
  /**
   * Sets the client input sample for an update().
   * 
   * @param cis the client input sample
   */
  public void setClientInputSample(ClientInputSample cis) {
	  this.cis = cis;
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
