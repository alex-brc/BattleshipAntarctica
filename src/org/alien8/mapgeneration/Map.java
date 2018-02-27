package org.alien8.mapgeneration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.alien8.core.Parameters;
import org.alien8.physics.AABB;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;

public class Map{
	protected int length;
	protected int width;
	protected int lengthDensity;
	protected int widthDensity;
	protected boolean[][] iceGrid;
	protected List<AABB> roughAABBs = new ArrayList<AABB>();
	protected seed;
	
	public Map(){ // Basic constructor - probably won't be used
		length = 0;
		width = 0;
		lengthDensity = 0;
		widthDensity = 0;
	}
	
	public Map(int l, int w, int lD, int wD, long s){ 
		length = l;
		width = w;
		lengthDensity = lD;
		widthDensity = wD;
		seed = s;
		//Map is a 2-D array depicting if each pixel is either ice or water (True = ice, False = water)
		iceGrid = new boolean[l][w];
		makeMap(); //Actually generates the Map using the PerlinNoise class
		makeRoughAABBs(Parameters.MAP_BOX_SIZE); //Gives the ice hitboxes
	}
	
	protected void makeMap(){
		double waterLevel = Parameters.WATER_LEVEL; //Defines the cut-off point for water or ice
		//Gets a noise grid from the PerlinNoise class
		double[][] noiseGrid = PerlinNoise.generateNoiseGrid(length, width, lengthDensity, widthDensity, seed);
		//Loops over all the pixels setting them to either ice (True) or water (False) based on the water level
		for (int y = 0; y < width; y++){
			for (int x = 0; x < length; x++){
				boolean isIce = (noiseGrid[x][y] <= waterLevel);
				iceGrid[x][y] = isIce;
			}
		}
	}
	
	protected void makeRoughAABBs(int boxSize){
		/* Function that gives all the ice basic hitboxes
		*  Works by checking every square of a given size if the majority of the pixels are ice or not
		*  If the majority is ice it makes the whole box a hitbox/entity
		*/
		for (int y = 0; y < width; y+=boxSize){
			for (int x = 0; x < length; x+=boxSize){
				
				int countIce = 0;
				for (int locY = y; locY < (y+boxSize); locY++){
					for (int locX = x; locX < (x+boxSize); locX++){
						if (iceGrid[locX][locY]){
							countIce++;
						}
					}
				}

				if (1.0 * countIce / (boxSize*boxSize) > Parameters.ICE_BOX_DENSITY){
					//Just need to generate a bunch of values for initialising an entity/hitbox

					double centerX = x + (Math.ceil(boxSize/2.0d) -1);
					double centerY = y + (Math.ceil(boxSize/2.0d) -1);
					Position center = new Position(centerX, centerY);
					Ice newIce = new Ice(center);
					
					Position newMin = new Position((double)x, (double)y);
					Position newMax = new Position((double)(x+boxSize-1), (double)(y+boxSize-1));
					AABB newAABB = new AABB(newMin, newMax, newIce);
					roughAABBs.add(newAABB);
				}
			}
		}
	}
	
	public List<AABB> getAABBs(){
		return roughAABBs;
	}
	
	public boolean[][] getIceGrid(){
		return iceGrid;
	}
	
	public void render(Renderer r) {
		r.drawMap(iceGrid);
	}
}