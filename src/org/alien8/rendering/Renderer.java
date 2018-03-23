package org.alien8.rendering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.alien8.client.Client;
import org.alien8.client.ClientWindowListener;
import org.alien8.client.InputManager;
import org.alien8.client.Launcher;
import org.alien8.core.ClientModelManager;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.core.ServerModelManager;
import org.alien8.drops.Pickup;
import org.alien8.drops.PlaneDropper;
import org.alien8.physics.Position;
import org.alien8.score.ClientScoreBoard;
import org.alien8.score.Score;
import org.alien8.server.KillEvent;
import org.alien8.server.Timer;
import org.alien8.ship.Ship;
import org.alien8.ui.Page;

import net.jafama.FastMath;

/**
 * This class is used to draw images and text onto the screen.
 *
 */
public class Renderer extends Canvas {

  private static final long serialVersionUID = 1L;
  public static Renderer instance;
  public LinkedList<Wreck> wrecks;
  private int width;
  private int height;
  private int xScroll;
  private int yScroll;

  private JFrame frame;

  private BufferedImage image; // image which is rendered onto canvas
  private int[] pixels;

  /**
   * Constructor.
   */
  private Renderer() {
    setPreferredSize(Parameters.RENDERER_SIZE);
    width = Parameters.RENDERER_SIZE.width;
    height = Parameters.RENDERER_SIZE.height;

    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    wrecks = new LinkedList<Wreck>();

    frame = new JFrame();

    addMouseListener(InputManager.getInstance());
    addMouseMotionListener(InputManager.getInstance());
    addKeyListener(InputManager.getInstance());

    frame.setTitle("Battleship Antarctica");
    frame.setResizable(false);
    frame.add(this);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addWindowListener(new ClientWindowListener());
    frame.setLocationRelativeTo(null);
    frame.setVisible(false);
  }
  
  /**
   * Call to set the frame as visible.
   */
  public void show() {
	  frame.setVisible(true);
  }

  /**
   * @return the singleton instance of the Renderer
   */
  public static Renderer getInstance() {
    if (instance == null)
      instance = new Renderer();
    return instance;
  }
  
  /**
   * Add a ship wreckage to the map
   * @param killEvent the killEvent caused by the ship dying
   */
  public void addWreck(KillEvent killEvent) {
	  this.wrecks.add(new Wreck(killEvent));
  }

  /**
   * Returns the position on the screen of the given position in game coordinates.
   * 
   * @return the Position of the center
   */
  public Position getScreenPosition(Position position) {
    // Position pos = new Position(position.getX() - xScroll, position.getY() - yScroll);
    return new Position(400, 300);
  }

  /**
   * Clears the screen.
   */
  public void clear() {
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = 0; // cycles through all pixels and sets them to 0, resetting the array
    }
  }

  /**
   * Renders all entities to the screen in their current state.
   */
  public void render(ClientModelManager model) {
    BufferStrategy bs = getBufferStrategy(); // gets canvas buffer strategy
    if (bs == null) {
      createBufferStrategy(3); // if none found, create a triple buffering strategy
      requestFocus();
      return;
    }
    clear();

    // Get x and y scroll from the player
    Ship player = model.getPlayer();
    Position mousePos = InputManager.getInstance().mousePosition();

    if (ClientScoreBoard.getInstance().getScore(player.getSerial()).getAlive()) { // Camera fixed on player's ship
      drawText("SPECTATOR-VIEW", width - "SPECTATOR-VIEW".length()*16, 600, true, FontColor.BLACK);
      xScroll = (int) (player.getPosition().getX() - width / 2
          + (mousePos.getX() - width) / Parameters.GAME_PARALLAX_WEIGHT);
      yScroll = (int) (player.getPosition().getY() - height / 2
          + (mousePos.getY() - height) / Parameters.GAME_PARALLAX_WEIGHT);
    } else { // Camera traverse the whole map according to mouse position
      xScroll = (int) (mousePos.getX() * 1.6);
      yScroll = (int) (mousePos.getY() * 2.6);
    }

    // Render terrain
    model.getMap().render(this);
    // Render white border round the map
    drawRect(0, 0, Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 0xFFFFFF, false);

    // Render wreckages
    for(Wreck wreck : wrecks) {
    	// Draw wreck sprite
        drawSprite((int) wreck.getPosition().getX() - wreck.getSprite().getWidth() / 2,
            (int) wreck.getPosition().getY() - wreck.getSprite().getHeight() / 2, wreck.getSprite(), false);
        
    }
    
    // Render Entities
    for (Entity e : model.getEntities()) {
      e.render();
    }

    /// HUD components
    // Render black frame round the edge of the screen
    drawHudFrame();
    // Render score
    drawText("SCORE", 16, 16, true, FontColor.WHITE);
    if (model.getPlayer() != null) {
      Score score = ClientScoreBoard.getInstance().getScore(model.getPlayer().getSerial());
      if (score == null)
        drawText("0", 16, 40, true, FontColor.WHITE);
      else
        drawText(Long.toString(score.getScore()), 16, 40, true, FontColor.WHITE);
    } else {
      drawText("0", 16, 40, true, FontColor.WHITE);
    }

    // Render health bar
    drawText("HEALTH", 203, 16, true, FontColor.WHITE);
    drawBar(Sprite.health_bar, player.getHealth(), Parameters.SHIP_HEALTH, 203, 40, true);
    // Render turret charge
    drawText("TURRET1", 324, 16, true, FontColor.WHITE);
    drawBar(Sprite.turret_bar, player.getFrontTurretCharge(), Parameters.TURRET_MAX_DIST, 326, 40,
        true);
    drawText("TURRET2", 462, 16, true, FontColor.WHITE);
    drawBar(Sprite.turret_bar, player.getRearTurretCharge(), Parameters.TURRET_MAX_DIST, 464, 40,
        true);

    // Draw timer
    Timer t = Launcher.getInstance().getRunningClient().getTimer();
    if (t != null)
      t.render();

    // Render use item
    drawText("ITEM", 612, 18, true, FontColor.WHITE);
    drawSprite(624, 40, new Sprite("/org/alien8/assets/item_frame.png"), true);
    try {
      drawSprite(631, 47, player.getItem().getSprite(), true);
    } catch (NullPointerException e) {

    }

    // Render minimap
    drawText("M", 704, 16, true, FontColor.WHITE);
    drawText("A", 704, 36, true, FontColor.WHITE);
    drawText("P", 704, 56, true, FontColor.WHITE);
    drawMinimap(720, 16, true);

    // Render scoreboard
    if (InputManager.getInstance().shiftPressed() || Client.getInstance().isWaitingToExit())
      ClientScoreBoard.getInstance().render();

    // Render the time before exiting the match
    if (Client.getInstance().isWaitingToExit()) {
      drawText(Integer.toString(Client.getInstance().getTimeBeforeExiting()), 190, 520, true,
          FontColor.WHITE);
      drawText("seconds", 230, 520, true, FontColor.WHITE);
      drawText("before", 360, 520, true, FontColor.WHITE);
      drawText("exiting", 480, 520, true, FontColor.WHITE);
    }

    // Display player death message
    // if (player.getHealth() <= 0) {
    // drawText("YOUDIED!", Parameters.RENDERER_SIZE.width / 2, Parameters.RENDERER_SIZE.height / 2,
    // true, FontColor.BLACK);
    // }

    // Graphics object from buffer strategy
    Graphics g = bs.getDrawGraphics();
    g.setColor(Color.BLACK);
    // Background rectangle same size as canvas
    g.fillRect(0, 0, getWidth(), getHeight());
    // Draw image with pixel data from image raster
    g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    // g.fillRect(Mouse.getX(), Mouse.getY(), 64, 64);
    // Necessary to clear memory
    g.dispose();
    // Displays the buffer strategy to the monitor
    bs.show();
  }

  public void render(Page page) {
    BufferStrategy bs = getBufferStrategy();
    if (bs == null) {
      createBufferStrategy(3); // if none found, create a triple buffering strategy
      requestFocus();
      return;
    }
    clear();

    page.render(this);

    // Graphics object from buffer strategy
    Graphics g = bs.getDrawGraphics();
    g.setColor(Color.BLACK);
    // Background rectangle same size as canvas
    g.fillRect(0, 0, getWidth(), getHeight());
    // Draw image with pixel data from image raster
    g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    // g.fillRect(Mouse.getX(), Mouse.getY(), 64, 64);
    // Necessary to clear memory
    g.dispose();
    // Displays the buffer strategy to the monitor
    bs.show();
  }

  /**
   * Draws a pixel of the specified colour at the specified location on the screen.
   * 
   * @param x the x position to display at
   * @param y the y position to display at
   * @param col the colour of the pixel
   * @param fixed {@code true} if the rectangle is at a fixed screen position (for UI elements),
   *        {@code false} if the text moves relative to the position of the player
   */
  public void drawPixel(int x, int y, int col, boolean fixed) {
    if (!fixed) {
      x -= xScroll;
      y -= yScroll;
    }
    if (x >= 0 && y >= 0 && x < width && y < height)
      pixels[x + y * width] = col;
  }

  /**
   * Draws a Sprite on the screen.
   * 
   * @param xp x position to display at
   * @param yp y position to display at
   * @param sprite the Sprite to draw
   * @param fixed {@code true} if the Sprite is at a fixed screen position (for UI elements),
   *        {@code false} if the text moves relative to the position of the player
   */
  public void drawSprite(int xp, int yp, Sprite sprite, boolean fixed) {
    if (!fixed) {
      xp -= xScroll;
      yp -= yScroll;
    }
    for (int y = 0; y < sprite.getHeight(); y++) {
      int ya = y + yp;
      for (int x = 0; x < sprite.getWidth(); x++) {
        int xa = x + xp;
        if (xa < -sprite.getWidth() || xa >= width || ya < 0 || ya >= height)
          break;
        if (xa < 0)
          xa = 0;
        int col = sprite.getPixels()[x + y * sprite.getWidth()];
        if (col != 0xffff00ff && col != 0xff7f007f)
          pixels[xa + ya * width] = col;
      }
    }
  }

  /**
   * Draws a rectangle on the screen.
   * 
   * @param xp x position to display at
   * @param yp y position to display at
   * @param width how many pixels wide the rectangle is
   * @param height how many pixels tall the rectangle is
   * @param col color of the rectangle
   * @param fixed {@code true} if the rectangle is at a fixed screen position (for UI elements),
   *        {@code false} if the text moves relative to the position of the player
   */
  public void drawRect(int xp, int yp, int width, int height, int col, boolean fixed) {
    if (!fixed) {
      xp -= xScroll;
      yp -= yScroll;
    }
    for (int x = xp; x <= xp + width; x++) {
      if (x < 0 || x >= this.width || yp >= this.height)
        continue;
      if (yp > 0)
        pixels[x + yp * this.width] = col;
      if (yp + height >= this.height)
        continue;
      if (yp + height > 0)
        pixels[x + (yp + height) * this.width] = col;
    }
    for (int y = yp; y < yp + height; y++) {
      if (xp >= this.width || y < 0 || y >= this.height)
        continue;
      if (xp > 0)
        pixels[xp + y * this.width] = col;
      if (xp + width >= this.width)
        continue;
      if (xp + width > 0)
        pixels[(xp + width) + y * this.width] = col;
    }
  }

  /**
   * Draws a filled in rectangle of solid colour on the screen.
   * 
   * @param xp x position to display at
   * @param yp y position to display at
   * @param width how many pixels wide the rectangle is
   * @param height how many pixels tall the rectangle is
   * @param col color of the rectangle
   * @param fixed {@code true} if the rectangle is at a fixed screen position (for UI elements),
   *        {@code false} if the text moves relative to the position of the player
   */
  public void drawFilledRect(int xp, int yp, int width, int height, int col, boolean fixed) {
    if (!fixed) {
      xp -= xScroll;
      yp -= yScroll;
    }
    // Like renderTileColour but a specified rectangle of any size
    for (int y = 0; y < height; y++) {
      int ya = y + yp;
      for (int x = 0; x < width; x++) {
        int xa = x + xp;
        if (xa < -width || xa >= this.width || ya < 0 || ya >= this.height)
          break;
        if (xa < 0)
          xa = 0;
        pixels[xa + ya * this.width] = col;
      }
    }
  }

  /**
   * Draws text on the screen.
   * 
   * @param text the text to display
   * @param x x position to display at
   * @param y y position to display at
   * @param fixed {@code true} if the text is at a fixed screen position (for UI elements),
   *        {@code false} if the text moves relative to the position of the player
   * @param color the color of the text to display. This is either black or white
   */
  public void drawText(String text, int x, int y, boolean fixed, FontColor color) {
    Font.defaultFont.render(text, this, x, y, fixed, color);
  }

  /**
   * Draws a UI bar on the screen. Used for health bars and turret charge bars. Consists of a frame
   * around the bar, and a bar inside which fills up according to the size of some variable compared
   * to the maximum value of that variable. The bar is effectively a meter of how 'full' the
   * variable is.
   * 
   * @param barSprite the sprite to use for the frame around the bar
   * @param value the value of the variable that the bar represents
   * @param maxValue the maximum value of the variable that the bar represents
   * @param xp x position to display at
   * @param yp y position to display at
   * @param fixed fixed {@code true} if the rectangle is at a fixed screen position (for UI
   *        elements), {@code false} if the text moves relative to the position of the player
   */
  public void drawBar(Sprite barSprite, double value, double maxValue, int xp, int yp,
      boolean fixed) {
    drawSprite(xp, yp, barSprite, fixed);
    int barHeight = 22;
    int maxBarLength = 76;
    int barLength = new Double(value / maxValue * maxBarLength).intValue();
    int color = 0x00B800;
    drawFilledRect(xp + 7, yp + 7, barLength, barHeight, color, fixed);
  }

  /**
   * Draws a black frame around the viewport. The top edge of this frame is where the HUD components
   * are displayed.
   */
  private void drawHudFrame() {
    // Top edge
    for (int y = 0; y < Parameters.BIG_BORDER; y++) {
      for (int x = 0; x < width; x++) {
        drawPixel(x, y, 0x000000, true);
      }
    }

    // Bottom edge
    for (int y = height - Parameters.SMALL_BORDER; y < height; y++) {
      for (int x = 0; x < width; x++) {
        drawPixel(x, y, 0x000000, true);
      }
    }

    // Left side
    for (int y = Parameters.BIG_BORDER; y < height - Parameters.SMALL_BORDER; y++) {
      for (int x = 0; x < Parameters.SMALL_BORDER; x++) {
        drawPixel(x, y, 0x000000, true);
      }
    }

    // Right side
    for (int y = Parameters.BIG_BORDER; y < height - Parameters.SMALL_BORDER; y++) {
      for (int x = width - Parameters.SMALL_BORDER; x < width; x++) {
        drawPixel(x, y, 0x000000, true);
      }
    }

  }

  /**
   * Draws the map on the screen.
   * 
   * @param grid
   */
  @Deprecated
  public void drawMap(boolean[][] grid) {
    int x0 = xScroll;
    int x1 = (xScroll + width + 1);
    int y0 = yScroll;
    int y1 = (yScroll + height + 1);

    for (int y = y0; y < y1; y++) {
      for (int x = x0; x < x1; x++) {
        if (x >= 0 && y >= 0 && x < Parameters.MAP_WIDTH && y < Parameters.MAP_HEIGHT) {
          if (grid[x][y]) {
            drawPixel(x, y, 0xffffff, false);
          } else {
            drawPixel(x, y, 0x5555ff, false);
          }
        }
      }
    }
  }

  /**
   * Draws the viewport on the screen. This is the area of the screen that provides a 'window' that
   * the user sees through to the game world. Includes the map, and other ships that are present.
   * 
   * @param grid a 2D boolean array where {@code true} represents ice, and {@code false} represents
   *        water
   */
  public void drawViewport(double[][] grid) {
    int x0 = xScroll + Parameters.SMALL_BORDER;
    int x1 = xScroll + width - Parameters.SMALL_BORDER + 1;
    int y0 = yScroll + Parameters.BIG_BORDER;
    int y1 = (yScroll + height - Parameters.SMALL_BORDER + 1);

    for (int y = y0; y < y1; y++) {
      for (int x = x0; x < x1; x++) {
        if (x >= 0 && y >= 0 && x < Parameters.MAP_WIDTH && y < Parameters.MAP_HEIGHT) {
        	if(grid[x][y] < Parameters.DEEP_WATER_LEVEL)
          	  drawPixel(x,y,Parameters.DEEP_WATER_COLOR,false);
            else if(Parameters.DEEP_WATER_LEVEL <= grid[x][y] && grid[x][y] < Parameters.SHALLOW_WATER_LEVEL)
          	  drawPixel(x,y,Parameters.SHALLOW_WATER_COLOR,false);
            else if(Parameters.SHALLOW_WATER_LEVEL <= grid[x][y] && grid[x][y] < Parameters.THIN_ICE_LEVEL)
          	  drawPixel(x,y,Parameters.THIN_ICE_COLOR,false);
            else if(Parameters.THIN_ICE_LEVEL <= grid[x][y])
          	  drawPixel(x,y,Parameters.THICK_ICE_COLOR,false);
        }
      }
    }
  }

  /**
   * Creates the terrain for the minimap from the game's map.
   * 
   * @param iceGrid a 2D boolean array representing the game's map where {@code true} represents
   *        ice, and {@code false} represents water
   * @return a int[] of colour pixels that represent the minimap
   */
  public int[][] createMinimapTerrain(boolean[][] iceGrid) {
    int bigWidth = Parameters.MAP_WIDTH;
    int bigHeight = Parameters.MAP_HEIGHT;
    int smallWidth = Parameters.MINIMAP_WIDTH;
    int smallHeight = Parameters.MINIMAP_HEIGHT;

    int widthScale = bigWidth / smallWidth;
    int heightScale = bigHeight / smallHeight;

    int[][] minimap = new int[smallWidth][smallHeight];

    for (int j = 0; j < heightScale; j++) {
      for (int i = 0; i < widthScale; i++) {
        int ice = 0;
        int water = 0;
        for (int y = j * heightScale; y < (j + 1) * heightScale; y++) {
          for (int x = i * widthScale; x < (i + 1) * widthScale; x++) {
            if (iceGrid[x][y]) {
              ice++;
            } else {
              water++;
            }
          }
        }
        if (ice > water) {
          minimap[i][j] = 0xffffff;
        } else {
          minimap[i][j] = 0x5555ff;
        }
      }
    }

    return minimap;
  }

  /**
   * Draws the minimap on the screen.
   * 
   * @param x x position to display at
   * @param y y position to display at
   * @param fixed {@code true} if the text is at a fixed screen position (for UI elements),
   *        {@code false} if the text moves relative to the position of the player
   */
  private void drawMinimap(int x, int y, boolean fixed) {
    if (ClientModelManager.getInstance().getMap() != null) {
      // Render terrain
      int[][] minimap = ClientModelManager.getInstance().getMap().getMinimap();
      for (int j = 0; j < minimap.length; j++) {
        for (int i = 0; i < minimap[0].length; i++) {
          drawPixel(x + i, y + j, minimap[i][j], fixed);
        }
      }

      int widthScale = Parameters.MAP_WIDTH / Parameters.MINIMAP_WIDTH;
      int heightScale = Parameters.MAP_HEIGHT / Parameters.MINIMAP_HEIGHT;
      // Render ships
      for (Entity ent : ServerModelManager.getInstance().getEntities()) {
        if (ent instanceof Ship) {
          drawEntityDot(ent, x, y, widthScale, heightScale, ((Ship) ent).getColour());
        } else if (ent instanceof Pickup) {
          drawEntityDot(ent, x, y, widthScale, heightScale, 0x503000);
        } else if (ent instanceof PlaneDropper) {
          drawEntityDot(ent, x, y, widthScale, heightScale, 0xF83800);
        }
      }

      // Render border of view
      int miniX = x + (xScroll + Parameters.SMALL_BORDER) / widthScale;
      int miniY = y + (yScroll + Parameters.BIG_BORDER) / heightScale;
      int miniViewWidth = ((Parameters.RENDERER_SIZE.width - (2 * Parameters.SMALL_BORDER))
          / Parameters.MINIMAP_WIDTH) * 2;
      int miniViewHeight =
          ((Parameters.RENDERER_SIZE.height - (Parameters.BIG_BORDER + Parameters.SMALL_BORDER))
              / Parameters.MINIMAP_HEIGHT) * 2;
      drawRect(miniX, miniY, miniViewWidth, miniViewHeight, 0xF83800, true);
    }
  }

  /**
   * Draws a dot on the minimap representing an Entity.
   * 
   * @param ent the Entity to draw a dot for
   * @param x the x position of the minimap
   * @param y the y position of the minimap
   * @param widthScale the scale of the map to the minimap (width-wise)
   * @param heightScale the scale of the map to the minimap (height-wise)
   * @param colour the colour of the dot to draw
   */
  private void drawEntityDot(Entity ent, int x, int y, int widthScale, int heightScale,
      int colour) {
    double xPos = ent.getPosition().getX();
    double yPos = ent.getPosition().getY();
    int renderXPos = (int) FastMath.round(xPos / widthScale);
    int renderYPos = (int) FastMath.round(yPos / heightScale);
    // Displays a dot for the ship on the minimap
    // As 1 pixel is too small to see, actually display 3x3 pixels for the ship
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        drawPixel(x + renderXPos + i, y + renderYPos + j, colour, true);
      }
    }
  }
}
