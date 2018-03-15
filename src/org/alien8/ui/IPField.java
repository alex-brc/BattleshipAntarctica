package org.alien8.ui;

import org.alien8.client.InputManager;
import org.alien8.rendering.Renderer;

public class IPField {

	private int x, y, width, height;
	private int backCol, bordCol;
	private String text;
	
	public IPField(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		backCol = 0x000000;
		bordCol = 0x888888;
		text = "";
	}
	
	public void render(Renderer r){
		r.drawFilledRect(x, y, width, height, backCol, true);
		r.drawRect(x, y, width, height, bordCol, true);
		r.drawText(text, x, y, true);
		InputManager in = InputManager.getInstance();
		char c = in.getKeyTyped();
		if (((c >= '0' && c <= '9') || c == '.') && text.length() < 15){
			text += c;
		}else if (c == 8 && text.length() > 0){ //code for backspace
			text = text.substring(0, text.length() - 1);
		}
	}
	
	public String getInput(){
		return text;
	}
	
}
