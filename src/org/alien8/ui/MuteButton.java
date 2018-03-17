package org.alien8.ui;

import org.alien8.audio.AudioManager;

public class MuteButton extends Button{
	
	private int controlType;
	
	public MuteButton(int controlType, int x, int y, int width, int height) {
		super(x, y, width, height, "m");
		this.controlType = controlType;
	}
	
	public void executeAction(){
		if(controlType == AudioManager.SFX) {
			if(AudioManager.getInstance().sfxMuteToggle())
				this.changeText("um");
			else
				this.changeText("m");
		} else if(controlType == AudioManager.AMBIENT) {
			if(AudioManager.getInstance().ambientMuteToggle())
				this.changeText("um");
			else
				this.changeText("m");
		}
	}

}
