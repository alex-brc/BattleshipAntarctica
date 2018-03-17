package org.alien8.ui;

import org.alien8.rendering.Renderer;

public class MainMenu {

	private SinglePlayerButton spBtn;
	private IPInfo ipInfo;
	private CreateServerButton csBtn;
	private IPField ipField;
	private ConnectButton conBtn;
	private InfoBox conInfo;
	private SettingButton setBtn;
	private ExitButton exBtn;
	private InteractiveLogo logo;
	
	public MainMenu(){
	    int infoWidth = (12 * 16) + (15 * 16) + 4; //Prepend text * font size + IP length * font size + padding
		int fieldWidth = (15 * 16) + 6; //IP length * font size + padding
		int btnWidth = (18 * 16) + 4; //Max button text length * font size + padding
		int height = 40; //Font size + padding;
		int vPad = 8; // Spacing between buttons = r.getWidth() / vPad
		int vOffset = 140; // Pixels to offset from the top of the window
		
		Renderer r = Renderer.getInstance();
		// Make buttons
		spBtn = new SinglePlayerButton(r.getWidth() / 2 - btnWidth / 2, vOffset + r.getHeight() / vPad, btnWidth, height);
		ipInfo = new IPInfo(r.getWidth() / 2 - infoWidth / 2, vOffset + (r.getHeight() / vPad) * 2 - height/2 - 2, infoWidth, height);
		csBtn = new CreateServerButton(r.getWidth() / 2 - btnWidth / 2, vOffset + (r.getHeight() / vPad) * 2, btnWidth, height);
		ipField = new IPField(r.getWidth() / 2 - fieldWidth / 2, vOffset + (r.getHeight() / vPad) * 3 - height/2 - 2, fieldWidth, 18);
		conInfo = new InfoBox(r.getWidth() / 2 - fieldWidth / 2 - 32, vOffset + (r.getHeight() / vPad) * 3 + height + 4, infoWidth, height);
		conBtn = new ConnectButton(r.getWidth() / 2 - btnWidth / 2, vOffset + (r.getHeight() / vPad) * 3, btnWidth, height);
		exBtn = new ExitButton(r.getWidth() / 2 - btnWidth / 2, vOffset + (r.getHeight() / vPad) * 5, btnWidth, height);
		setBtn = new SettingButton(r.getWidth() / 2 - btnWidth / 2, vOffset + (r.getHeight() / vPad) * 4, btnWidth, height);
		
		// Make the logo
		logo = new InteractiveLogo(r.getWidth()/2, 100);
		
	}
	
	public void render(Renderer r){
		spBtn.render(r);
		ipInfo.render(r);
		csBtn.render(r);
		ipField.render(r);
		conBtn.render(r);
		conInfo.render(r);
		setBtn.render(r);
		exBtn.render(r);
		
		logo.render();
	}
	
	public String getIP(){
		return ipField.getInput();
	}
	
	public void setConnectInfo(String info) {
		conInfo.updateText(info);
	}
	
}
