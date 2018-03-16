package org.alien8.items;

import java.util.Random;

import org.alien8.core.Entity;
import org.alien8.core.ServerModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;
import org.alien8.server.Server;

import net.jafama.FastMath;

public class PlaneDropper extends Entity {
	private static final long serialVersionUID = 460749617575081588L;
	
	private Position packetPosition;
	
	public PlaneDropper() {
		super(new Position(0,0), 0, Parameters.PLANE_SPEED, 0, 0, 0);
		// Get a random no ice position where it will drop the packet
        packetPosition = Server.getInstance().getRandomPosition();
        
        // Check if it's good looking
        while(!packetPosition.approximately(getMiddleOfMap(), Parameters.MAP_HEIGHT / 3))
        	packetPosition = Server.getInstance().getRandomPosition();
		// Find a point on left side of map to spawn
		Random rand = new Random();
        this.setPosition(new Position(0, rand.nextInt(Parameters.MAP_HEIGHT)));
        
		// Find the direction needed to go to the packetPosition
		this.setDirection(this.getPosition().getAngleTo(packetPosition) * (-1) + FastMath.PI / 2);
		// Then it spawns
	}
	
	public PlaneDropper(Position position, double direction) {
		super(position, direction, Parameters.PLANE_SPEED, 0, 0, 0);
	}

	@Override
	public void setPosition(Position position) {
		if(position.approximately(packetPosition, 0.5))
			dropPacket();
		super.setPosition(position);
	}
	
	private void dropPacket() {
		Pickup pickup = null;
		Random rand = new Random();
		switch(rand.nextInt(Pickup.NUMBER_OF_PICKUPS)) {
		case Pickup.HEALTH_PICKUP: 
			pickup = new HealthPickup(packetPosition);
			break;
		case Pickup.MINE_PICKUP: 
			pickup = new MinePickup(packetPosition);
			break;
		case Pickup.INVULNERABLE_PICKUP: 
			pickup = new InvulnerablePickup(packetPosition);
			break;
		case Pickup.SPEED_PICKUP: 
			pickup = new SpeedPickup(packetPosition);
			break;
		case Pickup.NO_COOLDOWN_PICKUP: 
			pickup = new NoCooldownPickup(packetPosition);
			break;
		case Pickup.TORPEDO_PICKUP:
			pickup = new TorpedoPickup(packetPosition);
			break;
		}
		ServerModelManager.getInstance().addEntity(pickup);
	}
	
	private Position getMiddleOfMap() {
		return new Position(Parameters.MAP_WIDTH / 2, Parameters.MAP_HEIGHT / 2);
	}
	
	@Override
	public void render() {
		// TODO 
		Renderer.getInstance().drawSprite((int) this.getPosition().getX(),(int) this.getPosition().getY(), Sprite.plane.rotateSprite(-(this.getDirection() - FastMath.PI / 2)), false);
	}

	@Override
	public void dealWithOutOfBounds() {
		// When it gets to out of bounds, should be deleted
		if(this.isOutOfBounds())
			this.delete();
	}

	@Override
	public void dealWithInIce(boolean[][] iceGrid) {
		// Nothing, it's flying
	}
	
}
