package org.alien8.ui;

import org.alien8.audio.AudioManager;
import org.alien8.client.Client;
import org.alien8.client.Client.State;

public class VolumeButton extends Button{
	
	public static final int UP = 1;
	public static final int DOWN = 2;
	
	private int controlType;
	private int incrementType;
	
	public VolumeButton(int controlType, int incrementType, int x, int y, int width, int height) {
		super(x, y, width, height, (incrementType == UP) ? "up" : "down");
		this.controlType = controlType;
		this.incrementType = incrementType;
	}
	
	public void executeAction(){
		if(incrementType == UP && controlType == AudioManager.SFX) {
			AudioManager.getInstance().sfxIncreaseVolume();
		} else if(incrementType == UP && controlType == AudioManager.AMBIENT) {
			AudioManager.getInstance().ambientIncreaseVolume();;
		} else if(incrementType == DOWN && controlType == AudioManager.SFX) {
			AudioManager.getInstance().sfxDecreaseVolume();
		} else if(incrementType == DOWN && controlType == AudioManager.AMBIENT) {
			AudioManager.getInstance().ambientDecreaseVolume();
		}
	}

}
