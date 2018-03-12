package org.alien8.mapgeneration;

import java.util.ArrayList;
import java.util.List;

import org.alien8.core.Parameters;
import org.alien8.physics.AABB;
import org.alien8.rendering.Renderer;

public class Map {
  protected int length;
  protected int width;
  protected int lengthDensity;
  protected int widthDensity;
  protected boolean[][] iceGrid;
  protected List<AABB> roughAABBs = new ArrayList<AABB>();
  protected long seed;

  public Map(int l, int w, int lD, int wD, long s) {
    length = l;
    width = w;
    lengthDensity = lD;
    widthDensity = wD;
    seed = s;
    // Map is a 2-D array depicting if each pixel is either ice or water (True = ice, False = water)
    iceGrid = new boolean[l][w];
    makeMap(); // Actually generates the Map using the PerlinNoise class
    // makeRoughAABBs(Parameters.MAP_BOX_SIZE); //Gives the ice hitboxes
  }

  protected void makeMap() {
    double waterLevel = Parameters.WATER_LEVEL; // Defines the cut-off point for water or ice
    // Gets a noise grid from the PerlinNoise class
    double[][] noiseGrid =
        PerlinNoise.generateNoiseGrid(length, width, lengthDensity, widthDensity, seed);
    // Loops over all the pixels setting them to either ice (True) or water (False) based on the
    // water level
    for (int y = 0; y < width; y++) {
      for (int x = 0; x < length; x++) {
        boolean isIce = (noiseGrid[x][y] <= waterLevel);
        iceGrid[x][y] = isIce;
      }
    }
  }
  
  public List<AABB> getAABBs() {
    return roughAABBs;
  }

  public boolean[][] getIceGrid() {
    return iceGrid;
  }

  public void render(Renderer r) {
    // r.drawMap(iceGrid);
    r.drawViewport(iceGrid);
  }
}
