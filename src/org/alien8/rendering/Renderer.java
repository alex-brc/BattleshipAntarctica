package org.alien8.rendering;

import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Renderer extends Canvas {

  private static final long serialVersionUID = 1L;

  private int width;
  private int height;

  private JFrame frame;

  public Renderer(Dimension size) {

    setPreferredSize(size);
    width = size.width;
    height = size.height;

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

  }

}
