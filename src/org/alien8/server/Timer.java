package org.alien8.server;

import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

public class Timer {
	public int minutes;
	public int seconds;
	
	public Timer(TimerEvent event) {
		this.minutes = event.getSeconds() / 60;
		this.seconds = event.getSeconds() % 60;
	}
	
	public Timer(int seconds) {
		this.minutes = seconds / 60;
		this.seconds = seconds % 60;
	}
	
	public int getMinutes() {
		return this.minutes;
	}
	
	public int getSeconds() {
		return this.seconds;
	}
	
	public void render() {
		Renderer.getInstance().drawText("TIMER", 110, 16, true, FontColor.WHITE);
		Renderer.getInstance().drawText("" + minutes + "-" + seconds, 110, 40, true, FontColor.WHITE);
	}
	
}
