package org.alien8.mapgeneration;

import org.alien8.core.Parameters;
import org.alien8.rendering.Renderer;

/**
 * This class represents the map in the game, made up of ice and water.
 *
 */
public class Map {
  protected int length;
  protected int width;
  protected int lengthDensity;
  protected int widthDensity;
  protected boolean[][] iceGrid;
  protected double[][] noiseGrid;
  protected int[][] minimap;
  protected long seed;

  /**
   * Constructor.
   * 
   * @param l the length (height) of this Map
   * @param w the width of this Map
   * @param lD the length density of this Map, used in this Map's generation
   * @param wD the height density of this Map, used in this Map's generation
   * @param s the random seed used to generate this Map
   */
  public Map(int l, int w, int lD, int wD, long s) {
    length = l;
    width = w;
    lengthDensity = lD;
    widthDensity = wD;
    seed = s;
    // Map is a 2-D array depicting if each pixel is either ice or water (True = ice, False = water)
    iceGrid = new boolean[l][w];
    makeMap(); // Actually generates the Map using the PerlinNoise class
    makeMinimap();
  }

  /**
   * Creates the Map using procedural generation.
   */
  protected void makeMap() {
    double waterLevel = Parameters.THIN_ICE_LEVEL; // Defines the cut-off point for water or ice
    // Gets a noise grid from the PerlinNoise class
    noiseGrid =
        PerlinNoise.generateNoiseGrid(length, width, lengthDensity, widthDensity, seed);
    // Loops over all the pixels setting them to either ice (True) or water (False) based on the
    // water level
    for (int y = 0; y < width; y++) {
      for (int x = 0; x < length; x++) {
        boolean isIce = (noiseGrid[x][y] >= waterLevel);
        iceGrid[x][y] = isIce;
      }
    }
  }

  /**
   * Creates a minimap from the large Map. This method is called once to avoid regenerating the
   * minimap at each tick, which would be unnecessary as the terrain doesn't change.
   */
  private void makeMinimap() {
    int bigWidth = Parameters.MAP_WIDTH;
    int bigHeight = Parameters.MAP_HEIGHT;
    int smallWidth = Parameters.MINIMAP_WIDTH;
    int smallHeight = Parameters.MINIMAP_HEIGHT;

    int widthScale = bigWidth / smallWidth;
    int heightScale = bigHeight / smallHeight;

    minimap = new int[smallWidth][smallHeight];

    for (int j = 0; j < smallHeight; j++) {
      for (int i = 0; i < smallWidth; i++) {
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
  }

  /**
   * @return a 2D array of booleans where {@code true} represents ice and {@code false} represents
   *         water
   */
  public boolean[][] getIceGrid() {
    return iceGrid;
  }
  
  public double[][] getNoiseGrid() {
	  return noiseGrid;
  }

  /**
   * Gets the minimap.
   * 
   * @return an int array where each int represents a coloured pixel
   */
  public int[][] getMinimap() {
    return minimap;
  }

  /**
   * Renders the currently visible area of this Map to the screen.
   * 
   * @param r the Renderer instance used to render this Map
   */
  public void render(Renderer r) {
    r.drawViewport(noiseGrid);
  }
}
