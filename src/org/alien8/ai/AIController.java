package org.alien8.ai;

import java.util.Iterator;
import java.util.Random;

import org.alien8.core.Entity;
import org.alien8.core.ModelManager;
import org.alien8.ship.Ship;
import org.alien8.physics.Position;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;

public class AIController{
	
	protected ModelManager model;
	protected Ship myShip;
	protected Entity target;
	protected boolean[][] iceGrid;
	
	public AIController(Ship ship){
		// Note: changed this to a Ship constructor, easier to handle in server and more practical
		model = ModelManager.getInstance();
		iceGrid = model.getMap().getIceGrid();
		myShip = ship; //All ai ships start facing East - temporary
	}
	
	public void update(){
		target = findClosestTarget();
		myShip.setTurretsDirection(target.getPosition());
		//At the moment it just fires all turrets at the closest ship
		//myShip.frontTurretCharge();
		//myShip.midTurretCharge();
		//myShip.rearTurretCharge();
		wander(); //Moves mostly aimlessly around the map
	}
	
	public Entity findClosestTarget(){
		Entity closestTarget = null;
		Position currentPos = myShip.getPosition();
		Iterator<Entity> entities = model.getEntities().iterator();
		double shortestDistance = 10000;
		//Iterates over all the entities finding the closest entity that is also a ship (and not itself)
		while(entities.hasNext()){
			Entity currentEntity = entities.next();
			if (currentEntity instanceof org.alien8.ship.Ship){
				double currentDistance = currentPos.distanceTo(currentEntity.getPosition());
				if ((currentDistance < shortestDistance) && (myShip.getSerial() != currentEntity.getSerial())){
					closestTarget = currentEntity;
					shortestDistance = currentDistance;
				}
			}
		}
		return closestTarget;
	}
	
	public void setTarget(Ship target){
		this.target = target;
	}
	
	public Entity getTarget(){
		return target;
	}
	
	public Ship getShip(){
		return myShip;
	}
	
	public boolean rayDetect(int rayLength){
		Position[] corners = myShip.getObb();
		double direction = myShip.getDirection();
		double xNose = (corners[0].getX() + corners[1].getX())/2.0;
		double yNose = (corners[0].getY() + corners[1].getY())/2.0;
		Position nose = new Position(xNose,yNose);
		return drawRay(corners[0], direction, rayLength) || drawRay(corners[1], direction, rayLength) || drawRay(nose, direction, rayLength); 
		
	}
	
	public boolean drawRay(Position start, double dir, int maxR){
		double x0 = start.getX();
		double y0 = start.getY();
		for (int r = 1;r <=maxR; r++){
			int x = (int)Math.round(x0 + r*Math.cos(dir));
			int y = (int)Math.round(y0 + r*Math.sin(dir));
			if (y >= Parameters.MAP_HEIGHT || x <= 0 || y <= 0 || x >= Parameters.MAP_WIDTH || iceGrid[x][y] ){
				return true;
			}
		}
		return false;
	}
	
	public void wander(){
		if (rayDetect((int)Parameters.SHIP_LENGTH)){
			PhysicsManager.applyForce(myShip, Parameters.SHIP_BACKWARD_FORCE, PhysicsManager.shiftAngle(myShip.getDirection() + Math.PI));

			double locEastDir = myShip.getDirection();
			locEastDir = PhysicsManager.shiftAngle(locEastDir - (Math.PI/2d));
			double locWestDir = PhysicsManager.shiftAngle(locEastDir + Math.PI);
			boolean obstToEast = drawRay(myShip.getPosition(), locEastDir, (int)Parameters.SHIP_LENGTH);
			boolean obstToWest = drawRay(myShip.getPosition(), locWestDir, (int)Parameters.SHIP_LENGTH);
			
			if (obstToEast && obstToWest){ //Obsticles to both sides
				myShip.setDirection(PhysicsManager.shiftAngle(myShip.getDirection() + Math.PI));
			}
			else if (obstToWest){
				PhysicsManager.rotateEntity(myShip,Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
			}
			else{
				PhysicsManager.rotateEntity(myShip,(-1)*Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
			}
			System.out.println(myShip.getSpeed());
		}
		else{
			PhysicsManager.applyForce(myShip, Parameters.SHIP_FORWARD_FORCE, myShip.getDirection());
		}
	}

}