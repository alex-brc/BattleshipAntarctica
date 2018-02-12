package org.alien8.mapgeneration;

import java.io.Serializable;

import org.alien8.core.Entity;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public class Ice extends Entity implements Serializable {
	
	private static final long serialVersionUID = -3440599654141729832L;

	public Ice(Position pos){
		super(pos,0,0,0,0,0);
	}

	@Override
	public void dealWithOutOfBounds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Renderer r) {
		r.drawRect((int) this.position.getX(), (int) this.position.getY(), 32, 32, 0xFF0000, false);
		
	}
}