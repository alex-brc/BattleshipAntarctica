package org.alien8.ai;

import java.util.Iterator;
import java.util.Random;

import org.alien8.core.Entity;
import org.alien8.ship.Ship;
import org.alien8.physics.Position;
import org.alien8.managers.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.PhysicsManager;

public class AIController{
	
	protected ModelManager model;
	protected Ship myShip;
	protected Entity target;
	private int compassDirection;
	/*
	*       (N)
	*     5  6  7
	*      \ | /
	*(W) 4 -- -- 0 (E)
	*      / | \
	*     3  2  1
	*       (S)
	*/
	private static int collisionCheckStart = 60; //Checks every 2 seconds (120 ticks) for a collision
	protected int collisionCheckCountDown;
	
	public AIController(Position startPos){
		model = ModelManager.getInstance();
		myShip = new Ship(startPos, 0); //All ai ships start facing East - temporary
		collisionCheckCountDown = collisionCheckStart;
	}
	
	public void update(){
		calcCompassDirection();
		target = findClosestTarget();
		myShip.setTurretsDirection(target.getPosition());
		//At the moment it just fires all turrets at the closest ship
		myShip.frontTurretCharge();
		myShip.midTurretCharge();
		myShip.rearTurretCharge();
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
	
	public void calcCompassDirection(){
		//Having the ship have a compass direction makes the isInFront function easier to compute
		double currDirection = myShip.getDirection();
		if ((currDirection >= (Math.PI/8)) && (currDirection < (3*Math.PI/8))){
			compassDirection = 1; //South-East
		}
		else if ((currDirection >= (3*Math.PI/8)) && (currDirection < (5*Math.PI/8))){
			compassDirection = 2; //South
		}
		else if ((currDirection >= (5*Math.PI/8)) && (currDirection < (7*Math.PI/8))){
			compassDirection = 3; //South-West
		}
		else if ((currDirection >= (7*Math.PI/8)) && (currDirection < (9*Math.PI/8))){
			compassDirection = 4; //West
		}
		else if ((currDirection >= (9*Math.PI/8)) && (currDirection < (11*Math.PI/8))){
			compassDirection = 5; //North-West
		}
		else if ((currDirection >= (11*Math.PI/8)) && (currDirection < (13*Math.PI/8))){
			compassDirection = 6; //North
		}
		else if ((currDirection >= (13*Math.PI/8)) && (currDirection < (15*Math.PI/8))){
			compassDirection = 7; //North-East
		}
		else{
			compassDirection = 0; //East - need to check with others that direction can't be >= 2PI
		}
	}
	
	public void wander(){
		
		/*Moving around by predicting collisions and avoiding them - Work in Progress:
		* If it sees a potencial collision and it will spin on the spot for a bit and then try and move again
		*/
		if ((collisionCheckCountDown == collisionCheckStart) && predictCollision()){
			collisionCheckCountDown--;
			PhysicsManager.applyForce(myShip, Parameters.SHIP_BACKWARD_FORCE,
              PhysicsManager.shiftAngle(myShip.getDirection() + Math.PI));
			PhysicsManager.rotateEntity(myShip,
              (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
		}
		//Differnt from above so we don't have to run predictCollision everytime
		else if (collisionCheckCountDown < collisionCheckStart){
			collisionCheckCountDown--;
			PhysicsManager.applyForce(myShip, Parameters.SHIP_BACKWARD_FORCE,
              PhysicsManager.shiftAngle(myShip.getDirection() + Math.PI));
			PhysicsManager.rotateEntity(myShip,
              (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
			  
			if (collisionCheckCountDown <= 0){
				collisionCheckCountDown = collisionCheckStart;
			}
		}
		// Just moves forward when it doesn't see any potencial collisions
		else{
			PhysicsManager.applyForce(myShip, Parameters.SHIP_FORWARD_FORCE, myShip.getDirection());
		}
	}
	
	public boolean predictCollision(){
		//Function tries to predict if the ship might collide with something soon, so that it can be avoided
		Entity closestCollider = null;
		Position currentPos = myShip.getPosition();
		/*These checks are for being close to the edge of the map (and going in the direction of the edge)
		* Values may need fiddling and also should be in terms of the map dimensions not 100 or 1949
		*/
		if ((currentPos.getX() <= 100) && (compassDirection == 3 || compassDirection == 4 || compassDirection == 5)){
			return true;
		}
		if ((currentPos.getX() >= 1948) && (compassDirection == 1 || compassDirection == 0 || compassDirection == 7)){
			return true;
		}
		if ((currentPos.getY() <= 100) && (compassDirection == 5 || compassDirection == 6 || compassDirection == 7)){
			return true;
		}
		if ((currentPos.getY() >= 1948) && (compassDirection == 1 || compassDirection == 2 || compassDirection == 3)){
			return true;
		}
		
		Iterator<Entity> entities = model.getEntities().iterator();
		double shortestDistance = 10000;
		//Iterates over the entities and finds the closest one that the ship is going in the direction of
		while(entities.hasNext()){
			Entity currentEntity = entities.next();
			double currentDistance = currentPos.distanceTo(currentEntity.getPosition());
			if ((currentDistance < shortestDistance) && (myShip.getSerial() != currentEntity.getSerial()) && isInFront(currentEntity)){
				closestCollider = currentEntity;
				shortestDistance = currentDistance;
			}
		}
		//If the found entity is within 50 pixels then it thinks it might collide with it
		//Should probably parameterise this distance
		if (shortestDistance <= 50){
			return true;
		}
		return false;
	}
	
	public boolean isInFront(Entity potentialCollider){
		//Checks to see if the potencial collider is indeed in the direction that the ship is going in
		double xc = potentialCollider.getPosition().getX();
		double yc = potentialCollider.getPosition().getY();
		double xm = myShip.getPosition().getX();
		double ym = myShip.getPosition().getY();
		
		if (compassDirection == 0){
			return (xc > xm); //Not quite right (all primary compass directions)
		}
		else if (compassDirection == 1){
			return (xc > xm)&&(yc < ym);
		}
		else if (compassDirection == 2){
			return (yc < ym);
		}
		else if (compassDirection == 3){
			return (xc < xm)&&(yc < ym);
		}
		else if (compassDirection == 4){
			return (xc < xm);
		}
		else if (compassDirection == 5){
			return (xc < xm)&&(yc > ym);
		}
		else if (compassDirection == 6){
			return (yc > ym);
		}
		else if (compassDirection == 7){
			return (xc > xm)&&(yc > ym);
		}
		else {
			return false;
		}
	}

}