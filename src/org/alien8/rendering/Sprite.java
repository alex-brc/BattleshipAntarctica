package org.alien8.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {

	private int width, height;
	private int[] pixels;
	private String path;
	
	public static Sprite bullet = new Sprite("/org/alien8/assets/bullet.png");
	public static Sprite ship_green = new Sprite("/org/alien8/assets/ship_green.png");
	public static Sprite ship_orange = new Sprite("/org/alien8/assets/ship_orange.png");
	public static Sprite ship_purple = new Sprite("/org/alien8/assets/ship_purple.png");
	public static Sprite ship_red = new Sprite("/org/alien8/assets/ship_red.png");
	public static Sprite ship_turquoise = new Sprite("/org/alien8/assets/ship_turquoise.png");
	public static Sprite turret = new Sprite("/org/alien8/assets/turret.png");
	
	public Sprite(String path){
		this.path = path;
		load();
	}
	
	/**
	 * Creates an empty sprite
	 * @param width
	 * @param height
	 */
	public Sprite(int width, int height){
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Creates a copy of a sprite
	 * @param s
	 */
	public Sprite(Sprite s){
		width = s.getWidth();
		height = s.getHeight();
		System.arraycopy(s.getPixels(), 0, pixels, 0, s.getPixels().length);
	}
	
	public Sprite rotateSprite(double a){
		Sprite s = new Sprite((int)(height * Math.cos(a) + width * Math.sin(a)), (int)(height * Math.sin(a) + width * Math.cos(a)));
		for (int y = 0; y < s.getHeight(); y++){
			for (int x = 0; x < s.getWidth(); x++){
				
			}
		}
		return s;
	}
	
	private void load(){
		try {
			BufferedImage image = ImageIO.read(Sprite.class.getResource(path));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0 , width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int[] getPixels(){
		return pixels;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}
