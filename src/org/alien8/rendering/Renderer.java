package org.alien8.rendering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import org.alien8.managers.InputManager;

public class Renderer extends Canvas {

  private static final long serialVersionUID = 1L;

  private int width;
  private int height;

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
  public void render() {
    BufferStrategy bs = getBufferStrategy(); //gets canvas buffer strategy
	if (bs == null){
	  createBufferStrategy(3); //if none found, create a triple buffering strategy
	  requestFocus();
	  return;
	}
	
	clear();
	
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
}
