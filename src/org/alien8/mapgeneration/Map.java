package org.alien8.mapgeneration;

import org.alien8.mapgeneration.PerlinNoise;
import org.alien8.mapgeneration.Ice;
import org.alien8.core.Entity;
import org.alien8.physics.AABB;
import org.alien8.physics.Position;
import java.util.List;

public class Map{
	protected int length;
	protected int width;
	protected int lengthDensity;
	protected int widthDensity;
	protected boolean[][] iceGrid;
	//protected List<Entity> iceEntities;
	protected List<AABB> roughAABBs;
	
	public Map(){
		length = 0;
		width = 0;
		lengthDensity = 0;
		widthDensity = 0;
	}
	
	public Map(int l, int w, int lD, int wD){
		length = l;
		width = w;
		lengthDensity = lD;
		widthDensity = wD;
		iceGrid = new boolean[l][w];
		makeMap();
		makeRoughAABBs();
	}
	
	protected void makeMap(){
		double waterLevel = 0.4d;
		
		double[][] noiseGrid = PerlinNoise.generateNoiseGrid(length, width, lengthDensity, widthDensity);
		for (int y = 0; y < width; y++){
			for (int x = 0; x < length; x++){
				boolean isIce = (noiseGrid[x][y] <= waterLevel);
				iceGrid[x][y] = isIce;
			}
		}
	}
	
	protected void makeRoughAABBs(int boxSize){
		for (int y = 0; y < width; y+=boxSize){
			for (int x = 0; x < length; x+=boxSize){
				
				int countIce = 0;
				for (int locY = y; locY < (y+boxSize); y++){
					for (int locX = x; locX < (x+boxSize); x++){
						if (iceGrid[locX][locY]){
							countIce++;
						}
					}
				}
				if (countIce > ((boxSize*boxSize)/2)){
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
}