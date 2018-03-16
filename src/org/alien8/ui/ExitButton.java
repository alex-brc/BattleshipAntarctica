package org.alien8.ui;

public class ExitButton extends Button{

	public ExitButton(int x, int y, int width, int height) {
		super(x, y, width, height, "exit");
	}
	
	public void executeAction(){
		System.exit(0);
	}

}
