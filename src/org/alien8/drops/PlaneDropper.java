package org.alien8.drops;

import java.util.Random;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.core.ServerModelManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.server.Server;
import net.jafama.FastMath;

/**
 * This class represents a cargo plane that flies across the map and drops a Pickup which Ships can
 * collect.
 *
 */
public class PlaneDropper extends Entity {
  private static final long serialVersionUID = 460749617575081588L;

  private Position packetPosition;

  /**
   * Constructor.
   */
  public PlaneDropper() {
    super(new Position(0, 0), 0, Parameters.PLANE_SPEED, 0, 0, 0);
    // Get a random no ice position where it will drop the packet
    packetPosition = Server.getInstance().getRandomPosition();

    // Check if it's good looking
    while (!packetPosition.approximately(getMiddleOfMap(), Parameters.MAP_HEIGHT / 3))
      packetPosition = Server.getInstance().getRandomPosition();
    // Find a point on left side of map to spawn
    Random rand = new Random();
    this.setPosition(new Position(0, rand.nextInt(Parameters.MAP_HEIGHT)));

    // Find the direction needed to go to the packetPosition
    this.setDirection(this.getPosition().getAngleTo(packetPosition) * (-1) + FastMath.PI / 2);
    // Then it spawns
  }

  /**
   * Constructor.
   * 
   * @param position the Position of the plane in XY coordinates
   * @param direction the direction the plane is travelling in
   */
  public PlaneDropper(Position position, double direction) {
    super(position, direction, Parameters.PLANE_SPEED, 0, 0, 0);
  }

  @Override
  public void setPosition(Position position) {
    if (position.approximately(packetPosition, 0.5))
      dropPacket();
    super.setPosition(position);
  }

  @Override
  public void render() {
    Renderer.getInstance().drawSprite((int) this.getPosition().getX(),
        (int) this.getPosition().getY(),
        Sprite.plane.rotateSprite(-(this.getDirection() - FastMath.PI / 2)), false);
  }

  @Override
  public void dealWithOutOfBounds() {
    // When it gets to out of bounds, should be deleted
    if (this.isOutOfBounds())
      this.delete();
  }

  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    // Nothing, it's flying
  }
  
  /**
   * @return the position where the packet will be dropped
   */
  public Position getPacketPosition() {
	  return this.packetPosition;
  }
  
  /**
   * Drops a random Pickup.
   */
  private void dropPacket() {
    Pickup pickup = null;
    Random rand = new Random();
    switch (rand.nextInt(Pickup.NUMBER_OF_PICKUPS)) {
      case Pickup.HEALTH:
        pickup = new HealthPickup(packetPosition);
        break;
      case Pickup.MINE:
        pickup = new MinePickup(packetPosition);
        break;
      case Pickup.INVULNERABLE:
        pickup = new InvulnerablePickup(packetPosition);
        break;
      case Pickup.SPEED:
        pickup = new SpeedPickup(packetPosition);
        break;
      case Pickup.NO_COOLDOWN:
        pickup = new NoCooldownPickup(packetPosition);
        break;
      case Pickup.TORPEDO:
        pickup = new TorpedoPickup(packetPosition);
        break;
    }
    ServerModelManager.getInstance().addEntity(pickup);
  }

  /**
   * @return the center of the map
   */
  private Position getMiddleOfMap() {
    return new Position(Parameters.MAP_WIDTH / 2, Parameters.MAP_HEIGHT / 2);
  }
}
