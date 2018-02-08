package org.alien8.mapgeneration;

import java.util.ArrayList;
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
		makeRoughAABBs(Parameters.MAP_BOX_SIZE);
	}
	
	protected void makeMap(){
		double waterLevel = Parameters.WATER_LEVEL;
		
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
				for (int locY = y; locY < (y+boxSize); locY++){
					for (int locX = x; locX < (x+boxSize); locX++){
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
	
	public void render(Renderer r) {
		for (int y = 0; y < width; y++)
			for (int x = 0; x < length; x++){
				if(iceGrid[x][y])
					r.drawRect(x, y, 1, 1, 0x00FFFF, false);
			}
	}
	
	/*public static void main(String[] args){
		Map test = new Map(512,512,8,8);
	}*/
}