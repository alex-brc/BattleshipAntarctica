package org.alien8.ui;

import org.alien8.client.Client.State;
import org.alien8.client.InputManager;
import org.alien8.client.Launcher;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;
import org.alien8.rendering.Sprite;

public class SplashScreen implements Page{
	
	private String text;
	
	public SplashScreen(){
		text = "press any key to continue";
	}
	
	public void render(Renderer r){
		r.drawSprite(0, 0, Sprite.title_screen, false);
		r.drawText(text, r.getWidth() / 2 - (text.length()*16) / 2, r.getHeight()/3 - 8, true, FontColor.WHITE);
		
		if (InputManager.getInstance().anyPressed()){
			Launcher.getInstance().getRunningClient().setState(State.NAME_SCREEN);
		}
	}
	
}
