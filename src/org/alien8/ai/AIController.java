import java.util.Iterator;

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
	private static int collisionCheckStart = 120;
	protected int collisionCheckCountDown;
	
	public AIController(Position startPos){
		model = ModelManager.getInstance();
		myShip = new Ship(startPos, 0);
		collisionCheckCountDown = 120;
	}
	
	public void update(){
		model.update();
		target = findClosestTarget();
		myShip.setTurretsDirection(target.getPosition());
		myShip.frontTurretShoot();
		myShip.midTurretShoot();
		myShip.rearTurretShoot();
		wander();
	}
	
	public Entity findClosestTarget(){
		Entity closestTarget = null;
		Position currentPos = myShip.getPosition();
		Iterator<Entity> entities = model.getEntities().iterator();
		double shortestDistance = 10000;
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
	
	public void wander(){
		if ((collisionCheckCountDown == collisionCheckStart) && predictCollision()){
			collisionCheckCountDown--;
			PhysicsManager.applyForce(myShip, Parameters.SHIP_BACKWARD_FORCE,
              PhysicsManager.shiftAngle(myShip.getDirection() + Math.PI));
			PhysicsManager.rotateEntity(myShip,
              (-1) * Parameters.SHIP_ROTATION_PER_SEC / Parameters.TICKS_PER_SECOND);
		}
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
		else{
			PhysicsManager.applyForce(myShip, Parameters.SHIP_FORWARD_FORCE, myShip.getDirection());
		}
	}
	
	public boolean predictCollision(){
		Entity closestCollider = null;
		Position currentPos = myShip.getPosition();
		Iterator<Entity> entities = model.getEntities().iterator();
		double shortestDistance = 10000;
		while(entities.hasNext()){
			Entity currentEntity = entities.next();
			double currentDistance = currentPos.distanceTo(currentEntity.getPosition());
			if ((currentDistance < shortestDistance) && (myShip.getSerial() != currentEntity.getSerial())){
				closestCollider = currentEntity;
				shortestDistance = currentDistance;
			}
		}
		if (shortestDistance <= 50){
			return true;
		}
		return false;
	}
}