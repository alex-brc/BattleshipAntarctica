package org.alien8.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import net.jafama.FastMath;

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
  public static Sprite title_screen = new Sprite("/org/alien8/assets/title_screen.png");
  public static Sprite logo = new Sprite("/org/alien8/assets/logo.png");

  public Sprite(String path) {
    this.path = path;
    load();
  }

  /**
   * Creates an empty sprite
   * 
   * @param width
   * @param height
   */
  public Sprite(int width, int height) {
    this.width = width;
    this.height = height;
    pixels = new int[width * height];
  }

  /**
   * Creates a copy of a sprite
   * 
   * @param s
   */
  public Sprite(Sprite s) {
    width = s.getWidth();
    height = s.getHeight();
    System.arraycopy(s.getPixels(), 0, pixels, 0, s.getPixels().length);
  }

  public Sprite(int[] pixels, int width, int height) {
    this.pixels = new int[width * height];
    System.arraycopy(pixels, 0, this.pixels, 0, pixels.length);
    this.width = width;
    this.height = height;
  }

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

    return s;
  }

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

  public static Sprite makeShipSprite(int colour) {
    Sprite newSprite = Sprite.ship_green;

    // TODO: Make a sprite with the given colour
    // For assigning random colours to player's ships.

    return newSprite;
  }

  public int[] getPixels() {
    return pixels;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

}
