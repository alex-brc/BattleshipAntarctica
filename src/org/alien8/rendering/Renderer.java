package org.alien8.rendering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.managers.InputManager;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;

public class Renderer extends Canvas {

  private static final long serialVersionUID = 1L;

  private int width;
  private int height;
  private int xScroll;
  private int yScroll;

  private JFrame frame;
  
  private BufferedImage image; //image which is rendered onto canvas
  private int[] pixels;

  public Renderer(Dimension size) {

    setPreferredSize(size);
    width = size.width;
    height = size.height;
    
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    addMouseListener(InputManager.getInstance());
    addMouseMotionListener(InputManager.getInstance());
    addKeyListener(InputManager.getInstance());

    frame = new JFrame();
    frame.setTitle("Battleship Antarctica");
    frame.setResizable(false);
    frame.add(this);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
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
	drawRect(0, 0, Parameters.MAP_WIDTH, Parameters.MAP_HEIGHT, 0xFF0000, false);
	
	for(Entity e : model.getEntities()){
		e.render(this);
	}
	
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
