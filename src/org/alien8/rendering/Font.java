package org.alien8.rendering;

public class Font {

	private static Sprite font = new Sprite("/fonts/test.png");
	private static Sprite[] characters = Sprite.split(font, 16);
	
	public static String charIndex = //
			"ABDCEFGHIJKLM" + //
			"NOPQRSTUVWXYZ" + //
			"abcdefghijklm" + //
			"nopqrstuvwxyz" + //
			"1234567890-=[" + //
			"];\'#\\,.¬!\"£$%" + //
			"^&*()_+{}:@~|" + //
			"<>?` ";
			
	
	public Font(){
		
	}
	
	public void render(String text, Renderer r){
		/*for (int i = 0; i < text.length(); i++){
			char currentChar = text.charAt(i);
			int index = charIndex.indexOf(currentChar);
			screen.renderSprite(50 + i * 16, 50, characters[index], true);
		}*/
		
	}
	
}

