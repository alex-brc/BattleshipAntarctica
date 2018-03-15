package org.alien8.ui;

import org.alien8.client.Client.State;
import org.alien8.client.InputManager;
import org.alien8.client.Launcher;
import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

public class SplashScreen {
	
	private String text;
	
	public SplashScreen(){
		text = "Press any key to continue";
	}
	
	public void render(Renderer r){
		r.drawText(text, r.getWidth() / 2 - (text.length()*16) / 2, r.getHeight()/2 - 8, true, FontColor.WHITE);
		
		if (InputManager.getInstance().anyPressed()){
			Launcher.getInstance().getRunningClient().setState(State.MAIN_MENU);
		}
	}
	
}
