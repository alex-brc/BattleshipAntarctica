package org.alien8.mapgeneration;

import java.util.Random;

public class PerlinNoise{
	
	//The gradient vectors can only be one of the 4 unit vectors
	static MapVector[] gradientVectors = {
		new MapVector(1d,1d), new MapVector(1d,-1d), 
		new MapVector(-1d,1d), new MapVector(-1d,-1d)
		};
	
	public static MapVector distance(double px, double py, double gx, double gy){
		/*Calculates the x distance and y distance given to vectors
		* gx and gy refer to the coordinates of the grid point, px and py refer to the point's coordinates 
		* we are calculating the distance to
		*/
		double outx = px - gx;
		double outy = py - gy;
		
		return new MapVector(outx,outy);
	}
	
	public static double dotProduct(MapVector u, MapVector v){
		double out = (u.getX()*v.getX() + u.getY()*v.getY());
		return out;
	}
	
	public static double fade(double n){
		//The standard perlin fade function to give smoother noise: 6n^5 - 15n^4 + 10n^3
		return n*n*n*(n*(n* 6 - 15) + 10);
	}
	
	public static double linInterpolate(double a, double b, double w){
		//Linear Interpolation function
		return a + w*(b - a);
	}
	
	public static double perlin(double x, double y, MapVector[][] gradients){
		//Calculates the perlin value of a given xy coordinate
		/*
		Connotation - The numbers after variables generally refer to the corners on the unit square:
					x0y1 -------- x1y1
						|        |
						|        |
						|        |
					x0y0 -------- x1y0
				E.g. grad00 refers to the gradient vector on the bottom left corner of the square
		*/
		double x0 = Math.floor(x);
		double y0 = Math.floor(y);
		double x1 = Math.ceil(x);
		double y1 = Math.ceil(y);
		//These are the points coordinates as if it were in the unit square (important for calcation of the distance vector)
		double unitX = x - x0;
		double unitY = y - y0; 
		MapVector grad00 = gradients[(int)x0][(int)y0];
		MapVector grad10 = gradients[(int)x1][(int)y0];
		MapVector grad01 = gradients[(int)x0][(int)y1];
		MapVector grad11 = gradients[(int)x1][(int)y1];
		
		MapVector dist00 = distance(unitX,unitY,0d,0d);
		MapVector dist10 = distance(unitX,unitY,1d,0d);
		MapVector dist01 = distance(unitX,unitY,0d,1d);
		MapVector dist11 = distance(unitX,unitY,1d,1d);
		
		double dot00 = dotProduct(grad00, dist00);
		double dot10 = dotProduct(grad10, dist10);
		double dot01 = dotProduct(grad01, dist01);
		double dot11 = dotProduct(grad11, dist11);
		
		double fadeX = fade(dist00.getX());
		double fadeY = fade(dist00.getY());
		
		double linInt0 = linInterpolate(dot00, dot10, fadeX);
		double linInt1 = linInterpolate(dot01, dot11, fadeX);
		
		double perlinValue = linInterpolate(linInt0, linInt1, fadeY);
		
		//Want values in the range 0 to 1 instead of -1 to 1 as they are easier to process
		double outValue = (perlinValue + 1)/2;
		
		return outValue;
	}
	
	public static double[][] generateNoiseGrid(int xPxlSize, int yPxlSize, int xGridSize, int yGridSize, long seed){
		/*
		PxlSize dimensions are difining the whole picture of noise (as a grid of pixels)
		GridSize dimensions are difining the grid on top of the noise that has a gradient vector at each point
		(Changing the grid size will change the density of noise)
		*/
		double[][] noiseGrid = new double[xPxlSize][yPxlSize];
		MapVector[][] gradientGrid = new MapVector[xGridSize+1][yGridSize+1];
		Random rand = new Random(seed);
		
		//Defining the grid of gradient vectors (only contains the unit vectors defined at top of the class)
		for (int y = 0; y < yGridSize+1; y++){
			for (int x = 0; x < xGridSize+1; x++){
				int currentGVector = rand.nextInt(3);
				gradientGrid[x][y] = gradientVectors[currentGVector];
			}
		}
		double xScale = xPxlSize/xGridSize;
		double yScale = yPxlSize/yGridSize;
		
		for (int y = 0; y < yPxlSize; y++){
			for (int x = 0; x < xPxlSize; x++){
				double xCoord = x/(xScale);
				double yCoord = y/(yScale);
				noiseGrid[x][y] = perlin(xCoord, yCoord, gradientGrid);
			}
		}
		return noiseGrid;
	}
	
	//Just using this to test stuff
	/*public static void main(String[] args){
		int pxlSize = 512;
		int gridSize = 4;
		
		double[][] testNoise = generateNoiseGrid(pxlSize, pxlSize, gridSize, gridSize);
		
		BufferedImage image = new BufferedImage(pxlSize,pxlSize, BufferedImage.TYPE_INT_RGB); 
		int colV = 0;
		Color c = new Color(0,0,0);
		
		for (int y = 0; y < pxlSize; y++){
			for (int x = 0; x < pxlSize; x++){
				//colV = (int)Math.floor(testNoise[x][y]*255d);
				if (testNoise[x][y] > 0.4){
					if (testNoise[x][y] <= 0.7){
						c = new Color(0,64,128);
					}else if (testNoise[x][y] <= 0.9){
						c = new Color(0,51,102);
					}else{
						c = new Color(0,38,77);
					}
				}
				else{
					if (testNoise[x][y] >= 0.2){
						c = new Color(179,255,255);
					}else if (testNoise[x][y] >= 0.1){
						c = new Color(204,255,255);
					}else {
						c = new Color(230,255,255);
					}
				}
				image.setRGB(x, y, c.getRGB());
			}
		}
		
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}*/
}