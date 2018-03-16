package org.alien8.ui;

import org.alien8.client.InputManager;
import org.alien8.physics.Position;
import org.alien8.rendering.FontColor;
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
		Position l = InputManager.getInstance().lastLmbClick();
		int lX = (int)l.getX();
		int lY = (int)l.getY();
		
		if (mX >= x && mY >= y && mX <= x+width && mY <= y+height){
			r.drawFilledRect(x, y, width, height, hCol, true);
			if (lX >= x && lY >= y && lX <= x+width && lY <= y+height){
				InputManager.getInstance().resetLastLmbClick();
				executeAction();
			}
		}else{
			r.drawFilledRect(x, y, width, height, col, true);
		}
		r.drawText(text, x + width/2 - (text.length()*16)/2, y + (height-16)/2, true, FontColor.WHITE);
	}
	
	public void executeAction(){
		LogManager.getInstance().log("UI", LogManager.Scope.DEBUG, "Inactive button clicked.");
		//This button does nothing; override to add functionality.
	}
	
	/**
	 * Changes the text of the button
	 * 
	 * @param text the new text to display
	 */
	protected void changeText(String text) {
		this.text = text;
	}
}
