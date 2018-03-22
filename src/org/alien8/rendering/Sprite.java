package org.alien8.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import net.jafama.FastMath;

/**
 * This class represents a sprite image in the game. It also contains static pre-loaded sprites.
 *
 */
public class Sprite implements Serializable {

  private static final long serialVersionUID = -7826033026339264249L;

  private int width, height;
  private int[] pixels;
  private String path;

  public static Sprite bullet = new Sprite("/org/alien8/assets/bullet.png");
  public static Sprite ship_green = new Sprite("/org/alien8/assets/ship_green.png");
  public static Sprite ship_orange = new Sprite("/org/alien8/assets/ship_orange.png");
  public static Sprite ship_purple = new Sprite("/org/alien8/assets/ship_purple.png");
  public static Sprite ship_red = new Sprite("/org/alien8/assets/ship_red.png");
  public static Sprite ship_turquoise = new Sprite("/org/alien8/assets/ship_turquoise.png");
  public static Sprite ship_wreckage = new Sprite("/org/alien8/assets/ship_wreckage.png");
  public static Sprite turret = new Sprite("/org/alien8/assets/turret.png");
  public static Sprite turret_bar = new Sprite("/org/alien8/assets/turret_bar.png");
  public static Sprite health_bar = new Sprite("/org/alien8/assets/health_bar.png");
  public static Sprite item_frame = new Sprite("/org/alien8/assets/item_frame.png");
  public static Sprite plane = new Sprite("/org/alien8/assets/plane.png");
  public static Sprite item_health = new Sprite("/org/alien8/assets/item_health.png");
  public static Sprite item_invulnerable = new Sprite("/org/alien8/assets/item_invulnerable.png");
  public static Sprite item_mine = new Sprite("/org/alien8/assets/item_mine.png");
  public static Sprite item_no_cooldown = new Sprite("/org/alien8/assets/item_no_cooldown.png");
  public static Sprite item_speed = new Sprite("/org/alien8/assets/item_speed.png");
  public static Sprite item_torpedo = new Sprite("/org/alien8/assets/item_torpedo.png");
  public static Sprite pickup = new Sprite("/org/alien8/assets/pickup.png");
  public static Sprite mine = new Sprite("/org/alien8/assets/mine.png");
  public static Sprite title_screen = new Sprite("/org/alien8/assets/title_screen.png");
  public static Sprite logo = new Sprite("/org/alien8/assets/logo.png");
  public static Sprite controls = new Sprite("/org/alien8/assets/controls.png");
  public static Sprite effect_speed = new Sprite("/org/alien8/assets/effect_speed.png");
  public static Sprite effect_invulnerable = new Sprite("/org/alien8/assets/effect_invulnerable.png");
  public static Sprite crosshair = new Sprite("/org/alien8/assets/crosshair.png");
  public static Sprite torpedo = new Sprite("/org/alien8/assets/torpedo.png");
  public static Sprite fire1 = new Sprite("/org/alien8/assets/fire1.png");
  public static Sprite fire2 = new Sprite("/org/alien8/assets/fire2.png");
  public static Sprite fire3 = new Sprite("/org/alien8/assets/fire3.png");
  public static Sprite[] fires = new Sprite[] {fire1, fire2, fire3};
  
  /**
   * Constructor.
   * 
   * @param path the file path for the Sprite's image
   */
  public Sprite(String path) {
    this.path = path;
    load();
  }

  /**
   * Creates an empty Sprite.
   * 
   * @param width the width of the Sprite's image
   * @param height the height of the Sprite's image
   */
  public Sprite(int width, int height) {
    this.width = width;
    this.height = height;
    pixels = new int[width * height];
  }

  /**
   * Creates a copy of a Sprite.
   * 
   * @param s the Sprite to copy
   */
  public Sprite(Sprite s) {
    width = s.getWidth();
    height = s.getHeight();
    pixels = new int[width*height];
    System.arraycopy(s.getPixels(), 0, pixels, 0, s.getPixels().length);
  }

  /**
   * Constructor.
   * 
   * @param pixels an int[] of colours representing the pixels of the Sprite
   * @param width the width of the Sprite's image
   * @param height the height of the Sprite's image
   */
  public Sprite(int[] pixels, int width, int height) {
    this.pixels = new int[width * height];
    System.arraycopy(pixels, 0, this.pixels, 0, pixels.length);
    this.width = width;
    this.height = height;
  }

  /**
   * @return the width of this Sprite
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return the height of this Sprite
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return the int[] of pixels representing this Sprite
   */
  public int[] getPixels() {
    return pixels;
  }

  /**
   * Loads a Sprite from a file.
   */
  private void load() {
    try {
      BufferedImage image = ImageIO.read(Sprite.class.getResource(path));
      width = image.getWidth();
      height = image.getHeight();
      pixels = new int[width * height];
      image.getRGB(0, 0, width, height, pixels, 0, width);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Rotates a Sprite by an angle.
   * 
   * @param a the angle to rotate by
   * @return the rotated Sprite
   */
  public Sprite rotateSprite(double a) {
    Sprite s = new Sprite(
        (int) (height * FastMath.abs(FastMath.sin(a)) + width * FastMath.abs(FastMath.cos(a))),
        (int) (height * FastMath.abs(FastMath.cos(a)) + width * FastMath.abs(FastMath.sin(a))));
    double cx = (double) width / 2;
    double cy = (double) height / 2;
    double cxNew = (double) s.getWidth() / 2;
    double cyNew = (double) s.getHeight() / 2;
    for (int y = 0; y < s.getHeight(); y++) {
      for (int x = 0; x < s.getWidth(); x++) {
        s.getPixels()[x + y * s.getWidth()] = 0xffff00ff;
      }
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        double dx = x - cx;
        double dy = y - cy;
        double dist = FastMath.sqrt(dx * dx + dy * dy);
        double da = FastMath.atan(dx / dy);
        if (y > cy)
          da += FastMath.PI;
        double na = da + a;
        double nx = cxNew + dist * FastMath.sin(na);
        double ny = cyNew + dist * FastMath.cos(na);
        if (nx >= 0 && ny >= 00 && nx < s.getWidth() && ny < s.getHeight())
          s.getPixels()[(int) nx + (int) ny * s.getWidth()] = pixels[x + y * width];
      }
    }
    for (int y = 0; y < s.getHeight(); y++){
    	for (int x = 0; x < s.getWidth(); x++){
    		if (s.getPixels()[x + y * s.getWidth()] == 0xffff00ff){
    			/*
    			 * 1 4 6
    			 * 2 x 7
    			 * 3 5 8
    			 */
    			int count = 0;
    			int totalR = 0;
    			int totalG = 0;
    			int totalB = 0;
        		if (x > 0){
        			if (y > 0){
        				int hex1 = s.getPixels()[(x-1) + (y-1) * s.getWidth()] % 0xff000000;
        				if (hex1 != 0xff00ff){
        					count++;
        					totalR += (hex1 & 0xff0000) >> 16;
        					totalG += (hex1 & 0xff00) >> 8;
    						totalB += (hex1 & 0xff);
        				}
        			}
        			int hex2 = s.getPixels()[(x-1) + y * s.getWidth()] % 0xff000000;
        			if (hex2 != 0xffff00ff){
        				count++;
        				totalR += (hex2 & 0xff0000) >> 16;
    					totalG += (hex2 & 0xff00) >> 8;
						totalB += (hex2 & 0xff);
        			}
        			if (y < s.getHeight() -1){
        				int hex3 = s.getPixels()[(x-1) + (y+1) * s.getWidth()] % 0xff000000;
        				if (hex3 != 0xffff00ff){
        					count++;
        					totalR += (hex3 & 0xff0000) >> 16;
        					totalG += (hex3 & 0xff00) >> 8;
    						totalB += (hex3 & 0xff);
        				}
        			}
        		}
        		if (y > 0){
        			int hex4 = s.getPixels()[x + (y-1) * s.getWidth()] % 0xff000000;
        			if (hex4 != 0xffff00ff){
    					count++;
    					totalR += (hex4 & 0xff0000) >> 16;
    					totalG += (hex4 & 0xff00) >> 8;
						totalB += (hex4 & 0xff);
    				}
        		}
        		if (y < s.getHeight() -1){
        			int hex5 = s.getPixels()[x + (y+1) * s.getWidth()] % 0xff000000;
        			if (hex5 != 0xffff00ff){
    					count++;
    					totalR += (hex5 & 0xff0000) >> 16;
    					totalG += (hex5 & 0xff00) >> 8;
						totalB += (hex5 & 0xff);
    				}
        		}
        		if (x < s.getWidth() -1){
        			if (y > 0){
        				int hex6 = s.getPixels()[(x+1) + (y-1) * s.getWidth()] % 0xff000000;
        				if (hex6 != 0xff00ff){
        					count++;
        					totalR += (hex6 & 0xff0000) >> 16;
        					totalG += (hex6 & 0xff00) >> 8;
    						totalB += (hex6 & 0xff);
        				}
        			}
        			int hex7 = s.getPixels()[(x+1) + y * s.getWidth()] % 0xff000000;
    				if (hex7 != 0xff00ff){
    					count++;
    					totalR += (hex7 & 0xff0000) >> 16;
    					totalG += (hex7 & 0xff00) >> 8;
						totalB += (hex7 & 0xff);
    				}
    				if (y < s.getHeight() -1){
        				int hex8 = s.getPixels()[(x+1) + (y+1) * s.getWidth()] % 0xff000000;
        				if (hex8 != 0xff00ff){
        					count++;
        					totalR += (hex8 & 0xff0000) >> 16;
        					totalG += (hex8 & 0xff00) >> 8;
    						totalB += (hex8 & 0xff);
        				}
        			}
        		}
        		if (count > 6){
        			int r = totalR / count;
        			int g = totalG / count;
        			int b = totalB / count;
        			int hex = 0xff000000 + (r * 0x10000) + (g * 0x100) + b;
        			s.getPixels()[x + y * s.getWidth()] = hex;
        		}
    		}
    	}
    }

    return s;
  }

  /**
   * Creates a Sprite[] from a sprite sheet.
   * 
   * @param sheet the sheet to create Sprites from
   * @param size the size of each Sprite in the sheet
   * @return a Sprite[]
   */
  public static Sprite[] split(Sprite sheet, int size) {
    int total = (sheet.getWidth() * sheet.getHeight()) / (size * size);
    Sprite[] sprites = new Sprite[total];
    int current = 0;
    int[] pixels = new int[size * size];

    for (int yp = 0; yp < sheet.getHeight() / size; yp++) {
      for (int xp = 0; xp < sheet.getWidth() / size; xp++) {
        for (int y = 0; y < size; y++) {
          for (int x = 0; x < size; x++) {
            int xo = x + xp * size;
            int yo = y + yp * size;
            pixels[x + y * size] = sheet.pixels[xo + yo * sheet.getWidth()];
          }
        }
        sprites[current++] = new Sprite(pixels, size, size);
      }
    }

    return sprites;
  }

  /**
   * Makes a Ship Sprite of the specified colour.
   * 
   * @param colour the colour of the Ship
   * @return the Ship Sprite
   */
  public static Sprite makeShipSprite(int colour) {
    Sprite newSprite = new Sprite(Sprite.ship_green);
    for (int i = 0; i < newSprite.getPixels().length; i++){
    	if (newSprite.getPixels()[i] == 0xff00b800){
    		newSprite.getPixels()[i] = colour;
    	}
    }
    return newSprite;
  }

  /**
   * Combines the two sprites to make a new one.
   * The first sprite is on the bottom, the second one on top.
   * The two sprites must be the same size.
   * 
   * @param sprite1 the original sprite
   * @param sprite2 the sprite to paste on top of
   * @return the new sprite if the sizes match, null otherwise
   */
  public static Sprite pasteSprites(Sprite sprite1, Sprite sprite2) {
	if(sprite1.getWidth() != sprite2.getWidth() || sprite2.getHeight() != sprite2.getHeight())
		return null;
	
	Sprite newSprite = new Sprite(sprite1.getWidth(), sprite1.getHeight());
	newSprite.pixels = sprite1.pixels;
	
	for(int i = 0; i < newSprite.pixels.length; i++)
		if(sprite2.pixels[i] != 0xffff00ff)
			newSprite.pixels[i] = sprite2.pixels[i];
			
	return newSprite;
	
  }
}
