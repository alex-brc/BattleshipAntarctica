package org.alien8.ui;

import org.alien8.rendering.Renderer;

public class MainMenu {

	private SinglePlayerButton spBtn;
	private IPInfo ipInfo;
	private CreateServerButton csBtn;
	private IPField ipField;
	private ConnectButton conBtn;
	private ExitButton exBtn;
	
	public MainMenu(){
	    int infoWidth = (12 * 16) + (15 * 16) + 4; //Prepend text * font size + IP length * font size + padding
		int fieldWidth = (15 * 16) + 4; //IP length * font size + padding
		int btnWidth = (18 * 16) + 4; //Max button text length * font size + padding
		int height = 20; //Font size + padding;
		
		Renderer r = Renderer.getInstance();
		spBtn = new SinglePlayerButton(r.getWidth() / 2 - btnWidth / 2, r.getHeight() / 5, btnWidth, height);
		ipInfo = new IPInfo(r.getWidth() / 2 - infoWidth / 2, (r.getHeight() / 5) * 2 - height - 2, infoWidth, height);
		csBtn = new CreateServerButton(r.getWidth() / 2 - btnWidth / 2, (r.getHeight() / 5) * 2, btnWidth, height);
		ipField = new IPField(r.getWidth() / 2 - fieldWidth / 2, (r.getHeight() / 5) * 3 - height - 2, fieldWidth, height);
		conBtn = new ConnectButton(r.getWidth() / 2 - btnWidth / 2, (r.getHeight() / 5) * 3, btnWidth, height, ipField);
		exBtn = new ExitButton(r.getWidth() / 2 - btnWidth / 2, (r.getHeight() / 5) * 4, btnWidth, height);
	}
	
	public void render(Renderer r){
		spBtn.render(r);
		ipInfo.render(r);
		csBtn.render(r);
		ipField.render(r);
		conBtn.render(r);
		exBtn.render(r);
	}
	
	public String getIP(){
		return ipField.getInput();
	}
	
}
