package org.alien8.ui;

import org.alien8.client.InputManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.util.LogManager;

public class Button {

	private int x, y, width, height;
	private int col, hCol;
	private String text;
	
	public Button(int x, int y, int width, int height, String text){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		col = 0x888888;
		hCol = 0xAAAAAA;
	}
	
	public void render(Renderer r){
		Position p = InputManager.getInstance().mousePosition();
		int mX = (int)p.getX();
		int mY = (int)p.getY();
		if (mX >= x && mY >= y && mX <= x+width && mY <= y+height){
			r.drawFilledRect(x, y, width, height, hCol, true);
			if (InputManager.getInstance().lmbPressed()){
				executeAction();
			}
		}else{
			r.drawFilledRect(x, y, width, height, col, true);
		}
		r.drawText(text, x + width/2 - (text.length()*16)/2, y+1, true);
	}
	
	public void executeAction(){
		LogManager.getInstance().log("UI", LogManager.Scope.DEBUG, "Inactive button clicked.");
		//This button does nothing; override to add functionality.
	}
}
