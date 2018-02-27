package org.alien8.rendering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import org.alien8.client.ClientWindowListener;
import org.alien8.client.InputManager;
import org.alien8.core.Entity;
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;

public class Renderer extends Canvas {

  private static final long serialVersionUID = 1L;
  public static Renderer instance;
  private int width;
  private int height;
  private int xScroll;
  private int yScroll;

  private JFrame frame;
  
  private BufferedImage image; //image which is rendered onto canvas
  private int[] pixels;

  private Renderer() {

    setPreferredSize(Parameters.RENDERER_SIZE);
    width = Parameters.RENDERER_SIZE.width;
    height = Parameters.RENDERER_SIZE.height;
    
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    
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
    frame.setVisible(true);
    
  }
  
  public static Renderer getInstance() {
	  if(instance == null)
		  instance = new Renderer();
	  return instance;
  }

  /**
   * The render() method renders all entities to the screen in their current state
   */
  public void render(ModelManager model) {
    BufferStrategy bs = getBufferStrategy(); //gets canvas buffer strategy
	if (bs == null){
	  createBufferStrategy(3); //if none found, create a triple buffering strategy
	  requestFocus();
	  return;
	}
	
	clear();
	
	//Actual drawing goes here
	Entity player = model.getPlayer();
	xScroll = (int) (player.getPosition().getX() - width / 2);
	yScroll = (int) (player.getPosition().getY() - height / 2);
	
	//Render terrain here
	model.getMap().render(this);
	drawRect(0, 0, Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 0xFF0000, false); //bounding box, remove later?
	
	for(Entity e : model.getEntities()){
		e.render();
	}
	
	drawText("Test", 50, 50, true);
	
	Graphics g = bs.getDrawGraphics(); //graphics object from buffer strategy
	
	g.setColor(Color.BLACK);
	g.fillRect(0, 0, getWidth(), getHeight()); //background rectangle same size as canvas
	g.drawImage(image, 0 , 0 , getWidth(), getHeight(), null); //draw image with pixel data from image raster
	//g.fillRect(Mouse.getX(), Mouse.getY(), 64, 64);
	g.dispose(); //necessary to clear memory
	bs.show(); //displays the buffer strategy to the monitor
  }
  	
  public void clear(){
	for (int i = 0; i < pixels.length; i++){
	  pixels[i] = 0; //cycles through all pixels and sets them to 0, resetting the array
	}
  }
  
  public void drawRect(int xp, int yp, int width, int height, int col, boolean fixed) {
	if (!fixed){
	  xp -= xScroll;
	  yp -= yScroll;
	}
	for (int x = xp; x <= xp + width; x++){
	  if (x < 0 || x >= this.width || yp >= this.height) continue;
	  if (yp > 0) pixels[x + yp * this.width] = col;
	  if (yp + height >= this.height) continue;
	  if (yp + height > 0) pixels[x + (yp + height) * this.width] = col;
	}
	for (int y = yp; y < yp + height; y++){
	  if (xp >= this.width || y < 0 || y >= this.height) continue;
	  if (xp > 0) pixels[xp + y * this.width] = col;
	  if (xp + width >= this.width) continue;
	  if (xp + width > 0) pixels[(xp + width) + y * this.width] = col;
	}
  }
  
  public void drawPixel(int x, int y, int col, boolean fixed){
	  if (!fixed){
		  x -= xScroll;
		  y -= yScroll;
	  }
	  if (x >= 0 && y >= 0 && x < width && y < height) pixels[x + y * width] = col;
  }
  
  public void drawSprite(int xp, int yp, Sprite sprite, boolean fixed){
		if (!fixed){
			xp -= xScroll;
			yp -= yScroll;
		}
		for (int y = 0; y < sprite.getHeight(); y++){
			int ya = y + yp;
			for (int x = 0; x < sprite.getWidth(); x++){
				int xa = x + xp;
				if (xa < -sprite.getWidth() || xa >= width|| ya < 0 || ya >= height) break;
				if (xa < 0) xa = 0;
				int col = sprite.getPixels()[x + y * sprite.getWidth()];
				if (col != 0xffff00ff && col != 0xff7f007f) pixels[xa+ya*width] = col;
			}
		}
	}
  
  public void drawText(String text, int x, int y, boolean fixed){
	  Font.defaultFont.render(text, this, x, y, fixed);
  }
  
  public void drawMap(boolean[][] grid){
	int x0 = xScroll;
	int x1 = (xScroll + width + 1);
    int y0 = yScroll;
	int y1 = (yScroll + height + 1);
		
	for (int y = y0; y < y1;y++){
      for (int x = x0; x < x1;x++){
    	if (x >= 0 && y >= 0 && x < Parameters.MAP_WIDTH && y < Parameters.MAP_HEIGHT){
    	  if (grid[x][y]){
    		drawPixel(x, y, 0xffffff, false);
    	  }else{
    	    drawPixel(x, y, 0x5555ff, false);
    	  }
    	}
      }
    }
  }
  
  /**
   * TODO
   * Returns the position on the screen of the given
   * position in game coordinates
   * @return the Position of the center in 
   */
  public static Position getScreenPosition(Position position) {
	  return new Position(400,300);
  }
}
