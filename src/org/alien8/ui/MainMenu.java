package org.alien8.ui;

import org.alien8.rendering.Renderer;

public class MainMenu {

	private SinglePlayerButton spBtn;
	private ConnectButton conBtn;
	private ExitButton exBtn;
	private IPField ip;
	
	public MainMenu(){
		int fieldWidth = (15 * 16) + 4; //IP length * font size + padding
		int btnWidth = (18 * 16) + 4; //Max button text length * font size + padding
		int height = 20; //Font size + padding;
		
		Renderer r = Renderer.getInstance();
		ip = new IPField(r.getWidth() / 2 - fieldWidth / 2, r.getHeight() / 2 - height - 2, fieldWidth, height);
		spBtn = new SinglePlayerButton(r.getWidth() / 2 - btnWidth / 2, r.getHeight() / 4, btnWidth, height);
		conBtn = new ConnectButton(r.getWidth() / 2 - btnWidth / 2, r.getHeight() / 2, btnWidth, height, ip);
		exBtn = new ExitButton(r.getWidth() / 2 - btnWidth / 2, (r.getHeight() / 4) * 3, btnWidth, height);
	}
	
	public void render(Renderer r){
		ip.render(r);
		spBtn.render(r);
		conBtn.render(r);
		exBtn.render(r);
	}
	
}
