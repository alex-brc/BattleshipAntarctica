package org.alien8.ai;

import java.util.Iterator;
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
	protected boolean rightTurnDefault = true;
	protected int changeDefaultTurn = 0;
	
	public AIController(Ship ship){
		model = ModelManager.getInstance();
		iceGrid = model.getMap().getIceGrid();
		myShip = ship;
	}
	
	public void update(){
		target = findClosestTarget();
		myShip.setTurretsDirectionAI(target.getPosition());
		//Fires both turrets at the closest ship
		myShip.frontTurretCharge();
		myShip.rearTurretCharge();
		if (myShip.hasItem()){
			myShip.useItem();
		}
		changeDefaultTurn++;
		if (changeDefaultTurn > 1200){
			rightTurnDefault = !rightTurnDefault;
			changeDefaultTurn = 0;
		}
		wander(); //Moves around the map, avoiding ice
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
			if (rightTurnDefault){
				if (obstToWest){
					myShip.setDirection(PhysicsManager.shiftAngle(myShip.getDirection()-Math.PI/64d));
				}
				else{
					myShip.setDirection(PhysicsManager.shiftAngle(myShip.getDirection()+Math.PI/64d));
				}
			}
			else{
				if (obstToEast){
					myShip.setDirection(PhysicsManager.shiftAngle(myShip.getDirection()+Math.PI/64d));
				}
				else{
					myShip.setDirection(PhysicsManager.shiftAngle(myShip.getDirection()-Math.PI/64d));
				}
			}
		}
		else{
			PhysicsManager.applyForce(myShip, Parameters.SHIP_FORWARD_FORCE, myShip.getDirection());
		}
	}

}