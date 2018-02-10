package org.alien8.mapgeneration;

import org.alien8.core.Entity;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public class Ice extends Entity{
	
	public Ice(Position pos){
		position = pos;
		setDirection(0d);
		setSpeed(0d);
		setMass(0d);
	}

	@Override
	public void dealWithOutOfBounds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Renderer r) {
		// TODO Auto-generated method stub
		
	}
}