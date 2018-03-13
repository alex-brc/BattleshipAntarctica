package org.alien8.server;

public class TimerEvent extends GameEvent {
	private static final long serialVersionUID = -2221951913857155239L;
	private int seconds;
	
	public TimerEvent(int seconds) {
		this.seconds = seconds;
	}
	
	public int getSeconds() {
		return this.seconds;
	}
}
