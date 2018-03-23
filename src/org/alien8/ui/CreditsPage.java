package org.alien8.ui;

import org.alien8.rendering.Renderer;

public class CreditsPage implements Page {
	
	private InfoBox creditsTitle;
	private InfoBox sfxCredits;
	private InfoBox sfxCredits2;
	private InfoBox sfxCredits3;
	private InfoBox sfxCredits4;
	private InfoBox sfxCredits5;
	private InfoBox musicCredits;
	private ReturnToMainButton returnBtn;
	
	public CreditsPage() {
		Renderer r = Renderer.getInstance();
		int btnWidth = (18 * 16) / 3 + 4; 
		
		String text = "CREDITS";
		creditsTitle = new InfoBox(r.getWidth()/2 - text.length()*8, r.getHeight() / 9 - 8, text.length() * 16, 16);
		creditsTitle.updateText(text);
		
		text = "many thanks to:";
		sfxCredits = new InfoBox(r.getWidth()/2 - text.length()*8, r.getHeight() / 9 * 3 - 8, text.length() * 16, 16);
		sfxCredits.updateText(text);
		text = "LittleRobotSoundFactory for sound effects";
		sfxCredits2 = new InfoBox(r.getWidth()/2 - text.length()*8, r.getHeight() / 9 * 4 - 8, text.length() * 16, 16);
		sfxCredits2.updateText(text);
		text = "- freesound.org -";
		sfxCredits3 = new InfoBox(r.getWidth()/2 - text.length()*8, r.getHeight() / 9 * 4 + 12, text.length() * 16, 16);
		sfxCredits3.updateText(text);
		text = "OZZED for his great music";
		musicCredits = new InfoBox(r.getWidth()/2 - text.length()*8, r.getHeight() / 9 * 5 - 8, text.length() * 16, 16);
		musicCredits.updateText(text);
		text = "- ozzed.net -";
		sfxCredits4 = new InfoBox(r.getWidth()/2 - text.length()*8, r.getHeight() / 9 * 5 + 12, text.length() * 16, 16);
		sfxCredits4.updateText(text);
		text = "and every member of Alien8";
		sfxCredits5 = new InfoBox(r.getWidth()/2 - text.length()*8, r.getHeight() / 9 * 6 - 8, text.length() * 16, 16);
		sfxCredits5.updateText(text);
		
		returnBtn = new ReturnToMainButton(r.getWidth()/2 - btnWidth, r.getHeight() / 9 * 8 - 10, 2 * btnWidth, 40);
	}
	
	@Override
	public void render(Renderer r) {
		creditsTitle.render(r);
		sfxCredits.render(r);
		sfxCredits2.render(r);
		sfxCredits3.render(r);
		sfxCredits4.render(r);
		sfxCredits5.render(r);
		musicCredits.render(r);
		returnBtn.render(r);
	}
}
