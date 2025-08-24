/**
 * 
 */
package paalgorithms;

import static paglobal.PaLog.writeLog;
import static paglobal.PaLog.writeLogOnly;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import paeditor.PaHeapArray;
import paeditor.PaInstrumentsWindow;
import paeditor.PaSharpButton.FILTER_TYPE;
import paenums.PaMaskTypeEnum;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 * <p>Class for the variety of image algorithms</p>
 */
public class PaAlgorithms {

	private class Pair {
		
		public Pair(int x, int y/*, int res*/) {
			
			this.x = x;
			
			this.y = y;
			//result = res;
		}
		
		public int x;
		
		public int y;
		//public int result;
	}
	
	/**
	 * This shift enum for determination of shift operation of matrix while calculations;

	 * @author Andrii Dashkov
	 *
	 */
	private static  enum Shift {
		
		X_DIRECTION,
		
		Y_DIRECTION
		
	}
	



	/**
	 * Makes the amplification of sharpness level. Can use three types of filters - Laplas, Sobel, Sobel + Laplas
	 * @param sourceImage - source image to process
	 * @param filterType - sharpness filter type (Laplas, Sobel, Sobel+Laplas)
	 * @param level - level of sharpness change; this parameter limits the maximum level of change of 
	 * every RGB component while modifications
	 * @return new image with changed sharpness 
	 */
	public Image sharpIncrease(BufferedImage sourceImage, FILTER_TYPE filterType, 
			int level) {
		
		
		try {
			
			/**
			 * CornersEdgesFind 3x3 for Laplas operator
			 */
			int[][] lapMask = {
				   { 0, -1,  0},
				   {-1,  4, -1},
				   { 0, -1,  0}
		   	};
			
			int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
			
			BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
			
			int[][][] colors = new int[3][3][3];

			
			int[][][] newImage = new int[3][hMax-2][wMax-2];
			
			//indexes in this for operator: we use 3x3 mask - we leaves the 1 pixel border without processing;
			//it will be restored in the end
			for ( int i = 1; i < wMax-1; i++) {
				
				int c_1 = sourceImage.getRGB(i-1, 0);
				
				int c = sourceImage.getRGB(i, 0);
				
				int c1 = sourceImage.getRGB(i+1, 0);
				
				colors[0][0][0] = (c_1 & 0xff0000) >> 16;//receives the red component from RGBA int
			
				colors[0][0][1] = (c & 0xff0000) >> 16; 
				
				colors[0][0][2] = (c1 & 0xff0000) >> 16;
				
				colors[1][0][0] = (c_1 & 0xff00) >> 8;//receives the green component from RGBA int
				
				colors[1][0][1] = (c & 0xff00) >> 8; 
				
				colors[1][0][2] = (c1 & 0xff00) >> 8;
				
				colors[2][0][0] = c_1  & 0xff;//receives the blue component from RGBA int
				
				colors[2][0][1] = c  & 0xff; 
				
				colors[2][0][2] = c1  & 0xff;
			
				c_1 = sourceImage.getRGB(i-1, 1);
				
				c = sourceImage.getRGB(i, 1);
				
				c1 = sourceImage.getRGB(i+1, 1);
				
				colors[0][1][0] = (c_1 & 0xff0000) >> 16;
				
				colors[0][1][1] = (c & 0xff0000) >> 16; 
				
				colors[0][1][2] = (c1 & 0xff0000) >> 16;
				
				
				colors[1][1][0] = (c_1 & 0xff00) >> 8;
				
				colors[1][1][1] = (c & 0xff00) >> 8; 
				
				colors[1][1][2] = (c1 & 0xff00) >> 8;
				
				colors[2][1][0] = c_1  & 0xff;
				
				colors[2][1][1] = c  & 0xff; 
				
				colors[2][1][2] = c1  & 0xff;
				
				for ( int j = 1 ; j < hMax-1; j++) {
					
					c_1 = sourceImage.getRGB(i-1, j+1);
					
					c = sourceImage.getRGB(i, j+1);
					
					c1 = sourceImage.getRGB(i+1, j+1);
					
				
					colors[0][2][0] = (c_1 & 0xff0000) >> 16;
				
					colors[0][2][1] = (c & 0xff0000) >> 16; 
					
					colors[0][2][2] = (c1 & 0xff0000) >> 16;
					
					colors[1][2][0] = (c_1 & 0xff00) >> 8;
					
					colors[1][2][1] = (c & 0xff00) >> 8; 
					
					colors[1][2][2] = (c1 & 0xff00) >> 8;
					
					colors[2][2][0] = c_1  & 0xff;
					
					colors[2][2][1] = c  & 0xff; 
					
					colors[2][2][2] = c1  & 0xff;
				
					int[] rC = new int[3];
					
					switch(filterType) {
					
						case LAPLAS : {
							
							rC = PaAlgoGradient.laplasFunction(colors, lapMask, level);
		
							int red = (int)(colors[0][1][1] +  rC[0]); //red
							
							int green = (int)(colors[1][1][1] +  rC[1]);//green
							
							int blue = (int)(colors[2][1][1] +  rC[2]);//blue
							
							//0 ..255 range control
							newImage[0][j-1][i-1] = red < 0 ? 0 : (red > 255 ? 255 : red);
							
							newImage[1][j-1][i-1] = green < 0 ? 0 : (green > 255 ? 255 : green);
							
							newImage[2][j-1][i-1] = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
							
							break;
						}
						case SOBEL : {
							
							rC = PaAlgoGradient.sobelFilter(colors, level);
							
							int red = (int)(colors[0][1][1] +  rC[0]); //red
							
							int green = (int)(colors[1][1][1] +  rC[1]);//green
							
							int blue = (int)(colors[2][1][1] +  rC[2]);//blue
							
							//0 ..255 range control
							newImage[0][j-1][i-1] = red < 0 ? 0 : (red > 255 ? 255 : red);
							
							newImage[1][j-1][i-1] = green < 0 ? 0 : (green > 255 ? 255 : green);
							
							newImage[2][j-1][i-1] = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
	
							break;
						}
						
						case SOBEL_LAPLAS : {
							
							rC = PaAlgoGradient.sobelFilter(colors, level);
							
							int[] rC2 = PaAlgoGradient.laplasFunction(colors, lapMask, level);
							
							int red = (int)(colors[0][1][1] + rC[0] + rC2[0]); //red
							
							int green = (int)(colors[1][1][1] + rC[1] + rC2[2]);//green
							
							int blue = (int)(colors[2][1][1] + rC[2] + rC2[2]);//blue
							
							//0 ..255 range control
							newImage[0][j-1][i-1] = red < 0 ? 0 : (red > 255 ? 255 : red);
							
							newImage[1][j-1][i-1] = green < 0 ? 0 : (green > 255 ? 255 : green);
							
							newImage[2][j-1][i-1] = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
							
							break;
							
						}
						default : {}
						
					}
				
					//shifts mask matrix in the Y direction;	
					shiftMatrix(colors,Shift.Y_DIRECTION);
				}
			}
			
			//NO any range scale is here; !!! we will have a grey image here if we try to make scaling in 0 ..255
			//3x3 mask leaves the 1 pixel around the perimeter of the image to be not processed; the function
			//setNewImageWithBoundary() restores this lack
			setNewImageWithBoundary(newImage,wMax-2,hMax-2,targetImage,sourceImage);
				 		
			return   targetImage;
	
		}
		finally {
		}
		
	}
	/**
	 * Sets new image according to date in image. Sizes of all parameters must be depended. It is used for algorithm
	 * which can't process the edges of the image - for example mask 3x3
	 * This function makes the 1 pixel border  around the image just copying the next line of the data 
	 * ( for y = 0 receives data from y=1(horizontal line) and for x = 0 receives data from x =1 (vertical line) )
	 * @param image - new data for image [color R,G,B][y coordinate][x coordinate]
	 * @param x - max X size for image array
	 * @param y - maxY size for image array
	 * @param target - resulting image -be careful the difference must be between target size and image size:
	 * sizeof(target) = sizeof(image) + 2; 
	 * @param source - source image (needed for alpha channel)
	 */
	private void setNewImageWithBoundary(int[][][] image, int x, int y,BufferedImage target, BufferedImage source) {
		try{
			for(int i = 0; i < x; ++i){
				
				for(int j = 0; j < y; ++j){
					
					int oldrgb = source.getRGB(i, j);
					
					int alpha = oldrgb >>> 24;
						
					int newRGB = (alpha << 24) | //alpha byte
							(image[0][j][i] << 16 ) | //red byte
							(image[1][j][i]<< 8) | //green byte
							(image[2][j][i]);//blue byte
					
					target.setRGB(i+1, j+1,newRGB); 
		
				}
			}
			
			for(int i = 0; i < x+2; ++i){
				
				int rgb = target.getRGB(i, 1);
				
				target.setRGB(i, 0,rgb); 
				
				rgb = target.getRGB(i, y);
				
				target.setRGB(i, y+1,rgb);
			}
			for(int j = 0; j < y+2; ++j){
				
				int rgb = target.getRGB(1, j);
				
				target.setRGB(0, j,rgb); 
				
				rgb = target.getRGB(x, j);
				
				target.setRGB(x+1, j,rgb);
			}
			
		}
		catch(Exception e) {
			
			target = null;
			
			 writeLogOnly("Can't save the target image for setNewImage operation",e);
		}
	}
	
	/**
	 * <p>Shifts rows of columns of the matrix colors on one position upper (left) in x or y direction </p>
	 * @param colors - matrix to shift
	 * @param shiftFlag - direction of shift
	 */
	private void shiftMatrix(int[][][] colors, Shift shiftFlag){
		
		for(int i = 0; i < 3; ++i ) {
		
			if(shiftFlag == Shift.Y_DIRECTION) {
				
				colors[i][0][0] = colors[i][1][0];
				
				colors[i][0][1] = colors[i][1][1];
				
				colors[i][0][2] = colors[i][1][2];
		
				colors[i][1][0] = colors[i][2][0];
				
				colors[i][1][1] = colors[i][2][1];
				
				colors[i][1][2] = colors[i][2][2];
			}
			else {
				
				colors[i][0][0] = colors[i][0][1];
				
				colors[i][1][0] = colors[i][1][1];
				
				colors[i][2][0] = colors[i][2][1];
		
				colors[i][0][1] = colors[i][0][1];
				
				colors[i][1][1] = colors[i][1][1];
				
				colors[i][2][1] = colors[i][2][1];							
			}
		}
		
	}
	
	/**
	 * 
	 * @param m - array with element where to find max and min
	 * @param prevValues - previous values of min and max, int[2]; [0] - min, [1] - max
	 */
	@SuppressWarnings("unused")
	private void setMaxMin(int[] m, int[] prevValues) {
	
		for(int i=0; i < m.length; ++i ) {
			
			if(m[i] > prevValues[1] ) prevValues[1] = m[i];
			
			if(m[i] < prevValues[0]) prevValues[0] = m[i];
		}	
	}

	/**
	 * 
	 * @param m - array with element where to find max and min for colors red, green, blue - indexes 0,1,2
	 * @param prevValues - previous values of min and max separately for colors, int[k][2]; [k][0] - min, [k][1] - max;
	 * [k] - index of color - 0 - red, 1- green, 2 - blue
	 */
	@SuppressWarnings("unused")
	private void setColorMaxMin(int[] m, int[][] prevValues) {
	
		for(int i=0; i < m.length; ++i ) {
			
			if(m[i] > prevValues[i][1] ) prevValues[i][1] = m[i];
			
			if(m[i] < prevValues[i][0]) prevValues[i][0] = m[i];

		}	
	}
	
	/**
	 * 
	 * @param m - array with element where to find max and min for colors red, green, blue - indexes 0,1,2
	 * @param prevValues - previous values of min and max separately for colors, int[k][2]; [k][0] - min, [k][1] - max;
	 * [k] - index of color - 0 - red, 1- green, 2 - blue
	 */
	@SuppressWarnings("unused")
	private void setColorMaxMin(double[] m, double[][] prevValues) {
	
		for(int i=0; i < m.length; ++i ) {
			
			if(m[i] > prevValues[i][1] ) prevValues[i][1] = m[i];
			
			if(m[i] < prevValues[i][0]) prevValues[i][0] = m[i];
		}	
	}
	
	/**
	 * 
	 * @param red - red color value
	 * @param green - green color value
	 * @param blue - blue color value
	 * @param prevValues - previous values of min and max separately for colors, int[k][2]; [k][0] - min, [k][1] - max;
	 * [k] - index of color - 0 - red, 1- green, 2 - blue
	 */
	private void setColorMaxMin(int red,int green, int blue, int[][] prevValues) {
			
			if(red > prevValues[0][1] ) prevValues[0][1] = red;
			
			if(red < prevValues[0][0]) prevValues[0][0] = red;
			
			if(green > prevValues[1][1] ) prevValues[1][1] = green;
			
			if(green < prevValues[1][0]) prevValues[1][0] = green;
			
			if(blue > prevValues[2][1] ) prevValues[2][1] = blue;
			
			if(blue < prevValues[2][0]) prevValues[2][0] = blue;
	
	}
	@SuppressWarnings("unused")
	private  void scaleBitRange1(int[][][] image, int x, int y, int [] minMax ) {

		for(int i = 0; i < x; ++i){
			
			for(int j = 0; j < y; ++j){
			
					image[0][j][i] = (int)((image[0][j][i]-minMax[0])*255.0/(double)(minMax[1]-minMax[0]));
		
					image[1][j][i] = (int)((image[1][j][i]-minMax[0])*255.0/(double)(minMax[1]-minMax[0]));

					image[2][j][i] = (int)((image[2][j][i]-minMax[0])*255.0/(double)(minMax[1]-minMax[0]));
	
			}
		}	
	}
	
	@SuppressWarnings("unused")
	private  void scaleBitRange1(double[][][] image, int x, int y, double[] minMax ) {
	
		for(int i = 0; i < x; ++i){
			
			for(int j = 0; j < y; ++j){
			
					image[0][j][i] = (image[0][j][i]-minMax[0])*255.0/(minMax[1]-minMax[0]);
		
					image[1][j][i] = (image[1][j][i]-minMax[0])*255.0/(minMax[1]-minMax[0]);

					image[2][j][i] = (image[2][j][i]-minMax[0])*255.0/(minMax[1]-minMax[0]);
	
			}
		}	
	}
	
/**
 * <>Scales data in image size to the range of 0 ..255; The function does it in the linear way<>
 * @param image - source image data
 * @param w - data width
 * @param h - data heigth
 * @param minMaxColors - min/max data for red, green, blue; first index - red,green,blue (0,1,2);
 * second index - min,max (0,1)
 */
	private  void scaleBitRange2(int[][][] image, int w, int h, int [][] minMaxColors ) {
		
		boolean redB = minMaxColors[0][0] < 0 || minMaxColors[0][1] > 255;
		
		boolean greenB = minMaxColors[1][0] < 0 || minMaxColors[1][1] > 255;
		
		boolean blueB = minMaxColors[2][0] < 0 || minMaxColors[2][1] > 255;
		
		for(int i =0; i < w; ++i){
			
			for(int j =0; j < h; ++j){
				
				if(redB) { //red
					
					image[0][j][i] = (int)((((double)(image[0][j][i]-minMaxColors[0][0]))/
							((double)(minMaxColors[0][1]-minMaxColors[0][0])))*255.0);
					
					if(image[0][j][i] < 0 ) {
						
						 writeLogOnly("Negative value for scale range operation:  red = " + 
								 + image[0][j][i]+ " ; " + NEXT_ROW +
								 "min = " + minMaxColors[0][0]+ NEXT_ROW +
								 "max = " + minMaxColors[0][1],null);
					}
				}
				
				if(greenB) { //green
					
					image[1][j][i] = (int)((((double)(image[1][j][i]-minMaxColors[1][0]))/
							((double)(minMaxColors[1][1]-minMaxColors[1][0])))*255.0);
					
					if(image[1][j][i] < 0 ) {
						
						 writeLogOnly("Negative value for scale range operation:  green = " + 
								 + image[1][j][i]+ " ; " + NEXT_ROW +
								 "min = " + minMaxColors[1][0]+ NEXT_ROW +
								 "max = " + minMaxColors[1][1],null);
					}
				}
				if(blueB) {
					
					image[2][j][i] = (int)((((double)(image[2][j][i]-minMaxColors[2][0]))/
							((double)(minMaxColors[2][1]-minMaxColors[2][0])))*255.0);
					
					if(image[2][j][i] < 0 ) {
						
						 writeLogOnly("Negative value for scale range operation:  blue = " + 
								 + image[2][j][i]+ " ; " + NEXT_ROW +
								 "min = " + minMaxColors[2][0]+ NEXT_ROW +
								 "max = " + minMaxColors[2][1],null);
					}
				}
			}
		}	
	}
	
	/**
	 * <>Scales data in image size to the range of 0 ..255; The function does it in the linear way<>
	 * @param image - source image data
	 * @param w - data width
	 * @param h - data heigth
	 * @param minMaxColors - min/max data for red, green, blue; first index - red,green,blue (0,1,2);
	 * second index - min,max (0,1)
	 */
	@SuppressWarnings("unused")
	private  void scaleBitRange2(double[][][] image, int w, int h, double[][] minMaxColors ) {
			
		boolean redB = minMaxColors[0][0] < 0 || minMaxColors[0][1] > 255;
		
		boolean greenB = minMaxColors[1][0] < 0 || minMaxColors[1][1] > 255;
		
		boolean blueB = minMaxColors[2][0] < 0 || minMaxColors[2][1] > 255;
		
		for(int i =0; i < w; ++i){
			
			for(int j =0; j < h; ++j){
				
				if(redB) { //red
					
					image[0][j][i] = (((image[0][j][i]-minMaxColors[0][0]))/
							((minMaxColors[0][1]-minMaxColors[0][0])))*255.0;
					
					if(image[0][j][i] < 0.0 ) {
						
						 writeLogOnly("Negative value for scale range operation:  red = " + 
								 + image[0][j][i]+ " ; " + NEXT_ROW +
								 "min = " + minMaxColors[0][0]+ NEXT_ROW +
								 "max = " + minMaxColors[0][1],null);
					}
				}
				if(greenB) { //green
					
					image[1][j][i] = (((image[1][j][i]-minMaxColors[1][0]))/
							((minMaxColors[1][1]-minMaxColors[1][0])))*255.0;
					
					if(image[1][j][i] < 0.0 ) {
						
						 writeLogOnly("Negative value for scale range operation:  green = " + 
								 + image[1][j][i]+ " ; " + NEXT_ROW +
								 "min = " + minMaxColors[1][0]+ NEXT_ROW +
								 "max = " + minMaxColors[1][1],null);
					}
				}
				if(blueB) {
					
					image[2][j][i] = (((image[2][j][i]-minMaxColors[2][0]))/
							((minMaxColors[2][1]-minMaxColors[2][0])))*255.0;
					
					if(image[2][j][i] < 0.0 ) {
						
						 writeLogOnly("Negative value for scale range operation:  blue = " + 
								 + image[2][j][i]+ " ; " + NEXT_ROW +
								 "min = " + minMaxColors[2][0]+ NEXT_ROW +
								 "max = " + minMaxColors[2][1],null);
					}
				}
			}
		}	
	}

	/**
	 * Sets new image according to date in image. Sizes of all parameters must be the same
	 * @param image - new data for image [color R,G,B][y coordinate][x coordinate]
	 * @param x - max X size for image
	 * @param y - maxY size for image
	 * @param target - resulting image
	 * @param source - source image (needed for alpha channel)
	 */
	private  void setNewImage(int[][][] image, int x, int y,BufferedImage target, BufferedImage source) {
		
		try{
			
			for(int i =0; i < x; ++i){
				
				for(int j =0; j < y; ++j){
					
					int oldrgb = source.getRGB(i, j);
					
					int alpha = oldrgb >>> 24;
						
						//Color clr = new Color(image[0][j][i],image[1][j][i],image[2][j][i]);
						
						int newRGB = (alpha << 24) | //alpha byte
								(image[0][j][i] << 16 ) | //red byte
								(image[1][j][i]<< 8) | //green byte
								(image[2][j][i]);//blue byte
						
						target.setRGB(i, j,newRGB); 
		
				}
			}	
		}
		catch(Exception e) {
			
			target = null;
			
			 writeLogOnly("Can't save the target image for setNewImage operation",e);
		}
	}

	
/**
 * 	<p>Linearization function</p>
 * @param sourceImage
 * @return new image
 */
public  Image linearization(BufferedImage sourceImage) {
		

		try {
			
			int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
			
			int[] red = new int[256];
			
			int[] green = new int[256];
			
			int[] blue = new int[256];
			
			int[] alpha = new int[256];
			
			for(int i =0; i < 256; ++i){
				
				red[i]=0;
				
				green[i]=0;
				
				blue[i]=0;
				
			
			}
			
			for ( int i = 0; i < wMax; i++) {
				
				for ( int j = 0 ; j < hMax; j++) {
					
					int c = sourceImage.getRGB(i, j);
					
					++red[(c & 0xff0000) >> 16];
					
					++green[(c & 0xff00) >> 8];
					
					++blue[c  & 0xff];
					
					++alpha[(c & 0xff000000) >>> 24]; // using operator >>> - because of sign 
				}
			}
			double[] red2 = new double[256];
			
			double[] green2 = new double[256];
			
			double[] blue2 = new double[256];
			
			for(int i =0; i < 256; ++i){
				
				red2[i] = 0;
				
				green2[i] = 0;
				
				blue2[i] = 0;
				
			
			} 
			
			for(int i =0; i < 256; ++i){
				
				int j = i;
				
				while(j > 0) {
					
					 red2[i] += red[j];
					 
					 green2[i] += green[j];
					 
					 blue2[i] += blue[j];
					 
					 --j;
				}
				
				int n = wMax*hMax;
				
				red2[i] /= n;
				
				green2[i] /= n;
				
				blue2[i] /= n;
			}
			
		BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
		
		for ( int i = 0; i < wMax; i++) {
			
			for ( int j = 0 ; j < hMax; j++) {
		
				int rgb = sourceImage.getRGB(i, j);
				
				int newRGB = (((rgb & 0xff000000) >>> 24) << 24) | //alpha byte
				(((int)(red2[(rgb & 0xff0000) >> 16]*255)) << 16 ) | //red byte
				(((int)(green2[(rgb & 0xff00) >> 8]*255)) << 8) | //green byte
				((int)(blue2[rgb & 0xff]*255)); //blue byte
				
				
				targetImage.setRGB(i, j,newRGB);
			}
		}
		
			return   targetImage;		
		}
		finally {
		 
		
		}
}

/**
 * Calculates spectrum of the image
 * @param sourceImage - source image
 * @param cN - number of color for spectrum - 0 - red, 1- green, 2 - blue 
 * @param centered - true if the user has chosen the centered radio button
 * @return the image of Furie spectrum
 */
@Deprecated
public  BufferedImage getSpectrum(BufferedImage sourceImage, int cN, boolean centered){
		
	
	int xN = sourceImage.getWidth();  
	int yN =  sourceImage.getHeight();
	
	int nT2 = PaAlgoUtils.get2maxNumber(xN,yN);
	
	double[][] FuvRl = new double[nT2][nT2];
	
	double[][] FuvIm = new double[nT2][nT2];
	
	//********** preparation of centred data
	double[][] imDataRl = getCentered2dData(sourceImage, cN , nT2, nT2, centered);
	
	double[][] imDataIm = getZero2dData( nT2,nT2 );
	
	
	PaAlgoFFT.getFFT(true,imDataRl,imDataIm,nT2,nT2,FuvRl,FuvIm);
	
	BufferedImage image = new BufferedImage(xN,yN,BufferedImage.TYPE_INT_RGB);
	
	double[][] res = new double[yN][xN];
	
	double max = 0;
	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			double rl = FuvRl[y][x];
			
			double im = FuvIm[y][x]; 
			
			//we need log because the range of spectrum is very high - much more then > 255
			double result =  Math.log10(1+Math.sqrt(rl*rl + im*im)); 
			
			res[y][x] = result; 
			
			if( max < result) max = result;

		}
		
	}
	//here we will strech the range in 0 ... 255
	double coeff = 255.0/max;
	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			int r =  (int) (coeff * res[y][x]); 
			
			int rgb = 0;
			
			//result of this - the image in gray halftones
			rgb |= r; //blue
			
			rgb |= r << 8; //green
			
			rgb |= r << 16; //red
		 
			image.setRGB(x, y, rgb);
		}
		
	}
	
	return image;

}



/**
 * Calculates spectrum of the image This variant uses an array on the heap - PaHeapArray
 * @param sourceImage - source image
 * @param cN - number of color for spectrum - 0 - red, 1- green, 2 - blue ; 3 - all channels, transformation to grayscale
 * @param centered - true if the user has chosen the centered radio button
 * @return the image of Furie spectrum
 */
public  BufferedImage getSpectrum1(BufferedImage sImage, int cN, boolean centered){
		
	
	int xN = sImage.getWidth();  
	
	int yN =  sImage.getHeight();
	
	int[]  t = PaAlgoUtils.getPowerOf2_MaxValue(xN,yN);
	
	int nT2 = t[1];
	
	int n2 = t[0];
	
	PaHeapArray<Double> FuvRl = new PaHeapArray<Double>(nT2,nT2);
	
	PaHeapArray<Double> FuvIm = new PaHeapArray<Double>(nT2,nT2);
	
	//********** centred data preparation
	
	BufferedImage sourceImage = sImage;
	
	//transformation to grayscale
	if (cN == 3) {
		
		sourceImage = (BufferedImage) PaAlgoConvert.toGrayscale(sourceImage);
		
		cN = 0; //doesn't matter what channel is now. all channels are equal
	}
	
	PaHeapArray<Double> imDataRl = getCentered2dData1(sourceImage, cN , nT2, nT2, centered);
	
	PaHeapArray<Double> imDataIm = getZero2dData1( nT2,nT2 );
	
	PaAlgoFFT.getFFT1(true,imDataRl,imDataIm,nT2,nT2,n2,FuvRl,FuvIm);
	
	BufferedImage image = new BufferedImage(xN,yN,BufferedImage.TYPE_INT_RGB);
	
	PaHeapArray<Double> res = new PaHeapArray<Double>(yN,xN);
	
	double max = 0;
	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			double rl = FuvRl.get(y,x);
			
			double im = FuvIm.get(y,x); 
			
			//we need log because the range of spectrum is very high - much more then > 255
			double result =  Math.log10(1+Math.sqrt(rl*rl + im*im)); 
			
			res.set(y,x,result); 
			
			if( max < result) max = result;

		}
		
	}
	//here we will stretch the range in 0 ... 255
	double coeff = 255.0/max;
	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			int r =  (int) (coeff * res.get(y,x)); 
			
			int rgb = 0;
			//result of this - the image in gray halftones
			rgb |= r; //blue
			
			rgb |= r << 8; //green
			
			rgb |= r << 16; //red
		 
			image.setRGB(x, y, rgb);
		}
		
	}
	
	imDataRl.free();
	
	imDataIm.free();
	
	res.free();
	
	FuvRl.free();
	
	FuvIm.free();
	
	return image;

}


	
/**
 * <p>Function calculates a coefficients for three lines which are parts of linear piecewise-defined function for contrast stretching
 *  operation 
 *  - First line (0 index) is a short line in the very begining of 0 .. 255 range, it starts from 0,0 point and has 
 *  the angle < 45 degrees in order to compress the range of dark area of brightness.
 * - Second line is a main  long line whicj coves the central area of 0..255 range; The angle is more then 45 degrees - 
 * this stretches the range of brightness.
 * - Third line is a line in the very end of the range 0 ..255; the angle is < 45 degrees and the end point is 255,255.
 * Important note - the points of crosslinking is (255 - w)/2 and 255 - (255 -w)/2 .</p>
 * @param w the range of brightness inside the range 0 ..255, for example 50 ... 200 = 150
 * @param k -the coefficient of the range w stretching; for example if k =1.1 the the range w will be streched in the range which
 *  on 10 % more 
 * @return the array with coefficients (indices [][0,1] ) for three lines (indices [0,1,2][])
 */
public double[][] getA0A13Lines(double w,double k){
	
	//two coefficients for three lines y = a0*x + a1  
	double[][] res = new double[3][2];
	
	//first line
	res[0][1] = 0.0;
	
	res[0][0] = (255.0-w*k)/(255.0-w);
	
	//second line - the main line of the range
	res[1][0] = k;
	
	res[1][1] = 255.0*(1.0-k)/2.0;
	
	//third line
	res[2][0] = (255.0-w*k)/(255.0-w);
	
	res[2][1] = 255.0*w*(k-1)/(255.0-w);
	
	return res;
	
}

public  double linear3part(double [][] a, double aX, double bX, double x) {
	
	if(x < aX) {
		
		return a[0][0]*x + a[0][1];
		
	}
	else if( bX >= x &&  x >= aX ) {
		
		return a[1][0]*x + a[1][1];
	}
	else {
		
		return a[2][0]*x + a[2][1];
	}
}


/**
 * <p>This function returns coefficients for folowing formula:
 *  s = a0(r+a1)**gamma; (** - power in gamma coefficient)
 *  We use two points for calculation a0,a1 - s1,r1, s2, r2
 *  r - souce value in the range 0...255
 *  s- result value in the range 0...255
 *  The power gamma is usually set by user through UI
 *  This function is usually used as power-gradation transition in the
 *  'second' part of the brightness range  > 127 - that is why we added special coefficient
 *  a1 under the power gamma
 *  The important note: for the contrast algorithm and 'second' part of the brightness range the coeff gamma must be
 *  in the range 0 ... 1.0 - the function should be convex in top direction
 *   <p>
 * @param s1 - first point result value
 * @param r1 - first point source value
 * @param s2 - second point result value
 * @param r2 - second point result value
 * @param gamma - gamma coefficient
 * @return two coefficients a0, a1 
 */
public  double[] getA0A1Power(double s1,double r1, double s2, double r2, double gamma){
	
	double[] res = new double[2];
	
	double gamma_1 = 1.0/gamma;
	
	res[0] = Math.pow((Math.pow(s1, gamma_1)-Math.pow(s2, gamma_1))/(r1-r2), gamma);
	
	res[1] = Math.pow(s1/res[0], gamma_1)- r1;
	
	return res;
	
}

/**
 * <p>This function returns coefficient for folowing formula:
 *  s = a0(r)**gamma; (** - power in gamma coefficient)
 *  We use one point for calculation a0 - s1,r1
 *  r - souce value in the range 0...255
 *  s- result value in the range 0...255
 *  The power gamma is usually set by user through the UI
 *  This function is usually used as power-gradation transition in the
 *  'first' part of the brightness range  < 127 
 *   *  The important note: for the contrast algorithm and  'first' part of the brightness range the coeff gamma must be
 *  in the range > 1.0 - the function should be convex in bottom direction
 *   <p> 
 * @param s1 - point for result value
 * @param r1 - point for source value
 * @param gamma - 0 power coeff
 * @return
 */
public  double getA0Power(double s1,double r1, double gamma){
	
	return s1/Math.pow(r1,gamma);
	
}
/**
 * <p>The function changes the contrast of image using the power function;
 * The range of brightness 0 ..255 is divided into two parts < 127 and  > 127;
 * Resulting power function is a mix of two different power functions for these two parts</p>
 * @param sourceImage - souce image 
 * @param gamma - power coefficient which is set by user; the value of gamma determines 
 * is the contrast will be increased or decreased; if gamma > 1 - increased, if gamma < 1 0 decreased
 * @param onePartGamma -determines if the function has only one branch; in other case the function is a
 * combination of two power functions
 * @return
 */
public  BufferedImage contrastPowerFunction(BufferedImage sourceImage,double gamma,
		boolean onePartGamma,double maxGamma){
	
	int xN = sourceImage.getWidth();  
	
	int yN =  sourceImage.getHeight();
	
	int rgb[][][] = new int[3][yN][xN];
	
	//we will use here the two-part function:
	// s = a0*r**gamma - int the 0 ..127 range
	//and s - a0(r+a1)**gamma - in the 127 ... 255 range
	//coefficients for first part of brighness range
	double[] a = new double[2];
	
	a[0] = getA0Power(127.0,127.0,gamma);
	
	a[1] = 0.0;
	
	//coefficients for second part of brightness range
	double[] b = new double[2];
	
	b = getA0A1Power(127.5,127.5, 255, 255,gamma);
	
	int [][] minMaxColors = {
			{0,0},//red color min,max
			{0,0},//green color min,max
			{0,0} //blue color min,max
	};
	
	//coeff for single power function
	double a0 = 255.0/Math.pow(255.0,gamma);
	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			int c = sourceImage.getRGB(x, y);
			
			if(onePartGamma) {
			
				rgb[0][y][x] = (int)PaAlgoUtils.gammaFunction((double)((c & 0xff0000) >> 16),a0,gamma);//red
				
				rgb[1][y][x] = (int)PaAlgoUtils.gammaFunction((double)((c & 0xff00) >> 8),a0,gamma);//green
				
				rgb[2][y][x] = (int)PaAlgoUtils.gammaFunction((double)(c  & 0xff),a0,gamma);//blue
			}
			else {
				rgb[0][y][x] = (int)PaAlgoUtils.gamma2PartFunction((double)((c & 0xff0000) >> 16),a, b,gamma,maxGamma);//red
				
				rgb[1][y][x] = (int)PaAlgoUtils.gamma2PartFunction((double)((c & 0xff00) >> 8),a, b,gamma,maxGamma);//green
				
				rgb[2][y][x] = (int)PaAlgoUtils.gamma2PartFunction((double)(c  & 0xff),a, b,gamma,maxGamma);//blue
			}
			
			//we need max and min for every color component in order to use them in postproccessing
			setColorMaxMin(rgb[0][y][x],rgb[1][y][x],rgb[2][y][x], minMaxColors);
		}
		
	}
	
	//postprocessing - the range of data inside rgb should be in 0 ... 255
	scaleBitRange2(rgb, xN,yN, minMaxColors );
	
	BufferedImage targetImage = new BufferedImage(xN,yN, BufferedImage.TYPE_INT_RGB);
	
	setNewImage(rgb,xN,yN,targetImage, sourceImage);
	
	return targetImage;
		
}



/**
 * <p>The function changes the contrast of image using the power function</p>
 * @param sourceImage - souce image 
 * @param gamma - power coefficient which is set by user; the value of gamma determines 
 * is the contrast will be increased or decreased; if gamma > 1 - increased, if gamma < 1 0 decreased
 * @param a0 - the coeff in the formula s = a0*r**gamma ; this coeff is set by user
 * @return
 */
public  BufferedImage powerFunction(BufferedImage sourceImage,double gamma){
	
	int xN = sourceImage.getWidth();  
	
	int yN =  sourceImage.getHeight();
	
	int rgb[][][] = new int[3][yN][xN];

	//we found the coeff before power function - this is for stratch the source range and result range
	//in 0..255
	double a0 = 255.0/Math.pow(255.0, gamma);
	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			int c = sourceImage.getRGB(x, y);
			
			rgb[0][y][x] = (int)PaAlgoUtils.gammaFunction((double)((c & 0xff0000) >> 16),a0,gamma);//red
			
			rgb[1][y][x] = (int)PaAlgoUtils.gammaFunction((double)((c & 0xff00) >> 8),a0,gamma);//green
			
			rgb[2][y][x] = (int)PaAlgoUtils.gammaFunction((double)(c  & 0xff),a0,gamma);//blue
			
			//just in case
			if(rgb[0][y][x] > 255) rgb[0][y][x] = 255;
			
			if(rgb[1][y][x] > 255) rgb[1][y][x] = 255;
			
			if(rgb[2][y][x] > 255) rgb[2][y][x] = 255;
		}
		
	}
	
	BufferedImage targetImage = new BufferedImage(xN,yN, BufferedImage.TYPE_INT_RGB);
	
	setNewImage(rgb,xN,yN,targetImage, sourceImage);
	
	return targetImage;		
}



/**
 * <p>The function converts the image to binary black/white</p>
 * @param sourceImage  - souce image 
 * @param thd - a threshold which is used for convert operation 
 * @return - the array with black/ white values ( 0/255)
 */
public  int[][] thresholdFunction(BufferedImage sourceImage, int thd){
	
	int xN = sourceImage.getWidth();  
	
	int yN =  sourceImage.getHeight();
	
	int rgb[][] = new int[yN][xN];

	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			int rgbValue = sourceImage.getRGB(x, y);
			
	         int red = (rgbValue >> 16) & 0xFF;
	         
	         int green = (rgbValue >> 8) & 0xFF;
	         
	         int blue = (rgbValue & 0xFF);

	         int gray = (red + green + blue)/3;
	         
	         int val = (gray << 16) + (gray << 8) + gray; 
	         
	         if(val <= thd) {
	        	 
	 			rgb[y][x] = 0;
	 			
	         }
	         else {
	        	 
	 			rgb[y][x] = 255;
	 			
	         }
			
		}
		
	}
	
	return rgb;		
}

/**
 * <p>The function converts the image to binary black/white</p>
 * @param sourceImage souce image 
 * @param thd  - a threshold which is used for convert operation 
 * @return the image with black/ white values ( 0/255)
 */
public  BufferedImage thresholdFunctionImage(BufferedImage sourceImage, int thd){
	
	int xN = sourceImage.getWidth();  
	
	int yN =  sourceImage.getHeight();
	
	int rgb[][][] = new int[3][yN][xN];

	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			int rgbValue = sourceImage.getRGB(x, y);
			
	         int red = (rgbValue >> 16) & 0xFF;
	         
	         int green = (rgbValue >> 8) & 0xFF;
	         
	         int blue = (rgbValue & 0xFF);

	         int gray = (red + green + blue)/3;
	         
	         int val = (gray << 16) + (gray << 8) + gray; 
	         
	         if(val <= thd) {
	        	 
	 			rgb[0][y][x] = 0;
	 			
	 			rgb[1][y][x] = 0;
	 			
	 			rgb[2][y][x] = 0;
	         }
	         else {
	        	 
	 			rgb[0][y][x] = 255;
	 			
	 			rgb[1][y][x] = 255;
	 			
	 			rgb[2][y][x] = 255;
	         }
		}
		
	}
	
	BufferedImage targetImage = new BufferedImage(xN,yN, BufferedImage.TYPE_INT_RGB);
	
	setNewImage(rgb, xN, yN, targetImage, sourceImage);
	
	return targetImage;		
}


public  BufferedImage contrastLinearFunction(BufferedImage sourceImage,double k,double w){
	
	int xN = sourceImage.getWidth();  
	
	int yN =  sourceImage.getHeight();
	
	int rgb[][][] = new int[3][yN][xN];

	double aX = (255.0 - w)/2.0;
	
	double bX = 255.0 - aX;
	
	double coeff[][] = getA0A13Lines(w,k);
	
	for ( int x = 0; x < xN; ++x) {
		
		for ( int y = 0; y < yN; ++y) {
			
			int c = sourceImage.getRGB(x, y);
	
			rgb[0][y][x] = (int) linear3part(coeff, aX, bX, (double)((c & 0xff0000) >> 16));//red
			
			rgb[1][y][x] = (int) linear3part(coeff, aX, bX, (double)((c & 0xff00) >> 8));//green
			
			rgb[2][y][x] = (int) linear3part(coeff, aX, bX, (double)(c  & 0xff));//blue

		}
		
	}

	
	BufferedImage targetImage = new BufferedImage(xN,yN, BufferedImage.TYPE_INT_RGB);
	
	setNewImage(rgb,xN,yN,targetImage, sourceImage);
	
	return targetImage;
		
}

		
	
	/**
	 * 
	 * @param sourceImage - source image
	 * @param w - width of size image
	 * @param h - height of size image
	 * @return average brightness (first 0 index) and disper (first 1 index) for red, green, blue ([][0,1,2])
	 */
	public  double[][] getAverageBrightness(BufferedImage sourceImage) {
	
		int w = sourceImage.getWidth();
		
		int h = sourceImage.getHeight();
		
		double[][] br = { {0.0,0.0,0.0},
				{0.0,0.0,0.0}}; //0 - average brightness, 1 - disperhion; 0 - red, 1- green, 2  blue
		
		double[][] a = new double[3][256]; // 0 - red, 1- green, 2  blue
		
		for ( int i = 0; i < 3; ++i) {
			
			for ( int j = 0; j < 256; ++j) {
				
				a[i][j] = 0.0;
			}
		}
		
		double n = w*h;
		
		for ( int x = 0; x < w; ++x) {
			
			for ( int y = 0; y < h; ++y) {
				
				int c = sourceImage.getRGB(x, y);
				
				a[0][(c & 0xff0000) >> 16]++;//red
				
				a[1][(c & 0xff00) >> 8]++;//green
				
				a[2][c & 0xff]++;//blue

				
			}
		}
		
		for ( int i = 0; i < 3; ++i) {
			
			for ( int j = 0; j < 256; ++j) {
				
				br[0][i] += a[i][j]*j/n;
			}
		}
		
		for ( int i = 0; i < 3; ++i) {
			
			for ( int j = 0; j < 256; ++j) {
				
				br[1][i] += (j-br[0][i])*(j-br[0][i])*a[i][j]/n;
			}
		}
		
		for ( int i = 0; i < 3; ++i) {
			
			br[1][i] = Math.sqrt(br[1][i]);
		}
		
		return br;
		
	}
	
	/**
	 * 
	 * @param im
	 * @param color 0 -red, 1 - green, 2 -blue
	 * @param maxX - maximum X range will be spread to 0
	 * @param maxY - maximum Y range will be spread to 0
	 * @return
	 */
	public  HeapArray3D<Double> getCentered2dData3D( BufferedImage im, int maxX, int maxY, 
			boolean centered ) {
		
		int x = im.getWidth();
		
		int y = im.getHeight();
		
		double k = 1.0;
		
		HeapArray3D<Double> d = new HeapArray3D<Double>(3,maxY,maxX);
		
		for(int z=0; z < 3; ++z)
			
		for(int i=0; i < maxX; ++i) {
			
			for(int j =0; j < maxY; ++j){
				
				if(i >= x || j >= y)  { d.set(z,j,i,0.0); continue; }
				
				if(centered) k = Math.pow((-1.0),(i+j));
				
				int rgb = im.getRGB(i, j);
			
				switch(z) {
				
					case 0: {
						
						//d[j][i] = ((im.getRGB(i, j) & 0xff0000) >> 16);
						
						d.set(z,j,i,((rgb & 0xff0000) >> 16)*k);//red
						
						break;
					}
					case 1: {
						
						//d[j][i] = ((im.getRGB(i, j) & 0xff00) >> 8);
						
						d.set(z,j,i,((rgb & 0xff00) >> 8)*k);//green
						
						break;
					}
					case 2: {
						
						//d[j][i] = (im.getRGB(i, j) & 0xff);//blue
						
						d.set(z,j,i,(rgb & 0xff)*k);//blue
						
						break;
					}
					
					default : break;
				}
			}
		}
		
		return d;
		
	}

	
	/**
	 * 
	 * @param im
	 * @param color 0 -red, 1 - green, 2 -blue
	 * @param maxX - maximum X range will be spread to 0
	 * @param maxY - maximum Y range will be spread to 0
	 * @return
	 */
	public  double[][] getCentered2dData( BufferedImage im, int cl, int maxX, int maxY, boolean centered ) {
		
		int x = im.getWidth();
		
		int y = im.getHeight();
		
		double k = 1.0;
		
		double[][] d = new double[maxY][maxX];
		
		for(int i=0; i < maxX; ++i) {
			
			for(int j =0; j < maxY; ++j){
				
				if(i >= x || j >= y)  { d[j][i] = 0.0; continue; }
				
				if(centered) k = Math.pow((-1.0),(i+j));
				
				int rgb = im.getRGB(i, j);
			
				switch(cl) {
				
					case 0: {
						
						//d[j][i] = ((im.getRGB(i, j) & 0xff0000) >> 16);
						
						d[j][i] = ((rgb & 0xff0000) >> 16)*k;//red
						
						break;
					}
					case 1: {
						
						//d[j][i] = ((im.getRGB(i, j) & 0xff00) >> 8);
						
						d[j][i] = ((rgb & 0xff00) >> 8)*k;//green
						
						break;
					}
					case 2: {
						
						//d[j][i] = (im.getRGB(i, j) & 0xff);//blue
						
						d[j][i] = (rgb & 0xff)*k;//blue
						
						break;
					}
					
					default : break;
				}
			}
		}
		
		return d;
		
	}
	
	/**
	 * 
	 * @param im
	 * @param color 0 -red, 1 - green, 2 -blue
	 * @param maxX - maximum X range will be spread to 0
	 * @param maxY - maximum Y range will be spread to 0
	 * @return
	 */
	public  PaHeapArray<Double> getCentered2dData1( BufferedImage im, int cl, 
			int maxX, int maxY, boolean centered ) {
		
		int x = im.getWidth();
		
		int y = im.getHeight();
		
		double k = 1.0;
		
		PaHeapArray<Double> d = new PaHeapArray<Double>(maxY,maxX);
		
		for(int i=0; i < maxX; ++i) {
			
			for(int j =0; j < maxY; ++j){
				
				if(i >= x || j >= y)  { d.set(j,i,0.0); continue; }
				
				if(centered) k = Math.pow((-1.0),(i+j));
				
				int rgb = im.getRGB(i, j);
			
				switch(cl) {
				
					case 0: {
						
						//d[j][i] = ((im.getRGB(i, j) & 0xff0000) >> 16);
						d.set(j,i,((rgb & 0xff0000) >> 16)*k);//red
						
						break;
					}
					case 1: {
						
						//d[j][i] = ((im.getRGB(i, j) & 0xff00) >> 8);
						
						d.set(j,i,((rgb & 0xff00) >> 8)*k);//green
						
						break;
					}
					case 2: {
						//d[j][i] = (im.getRGB(i, j) & 0xff);//blue
						
						d.set(j,i,(rgb & 0xff)*k);//blue
						
						break;
					}
					default : break;
				}
			}
		}
		
		return d;
		
	}
	
	/**
	 * 
	 * @param im
	 * @param color 0 -red, 1 - green, 2 -blue
	 * @param maxX - maximum X range will be spread to 0
	 * @param maxY - maximum Y range will be spread to 0
	 * @return
	 */
	public  void  getCentered2dData3( BufferedImage im,  int maxX, int maxY, boolean centered, 
			PaHeapArray<Double> redAr,PaHeapArray<Double> greenAr, PaHeapArray<Double> blueAr,
			PaHeapArray<Double> alphaAr) {
		
		int x = im.getWidth();
		
		int y = im.getHeight();
		
		double k = 1.0;

		if(centered) {
			
			for(int i=0; i < x; ++i) {
				
				for(int j =0; j < y; ++j){
					
					k = Math.pow((-1.0),(i+j));
					
					int rgb = im.getRGB(i, j);			
					
					redAr.set(j,i,((rgb & 0xff0000) >> 16)*k);
					
					greenAr.set(j,i,((rgb & 0xff00) >> 8)*k);
					
					blueAr.set(j,i,(rgb & 0xff)*k);
					
					alphaAr.set(j,i,(double)(rgb >>> 24));
				}
			}
		}
		else {
			
			for(int i=0; i < maxX; ++i) {
				
				for(int j =0; j < maxY; ++j){
					
					int rgb = im.getRGB(i, j);		
					
					redAr.set(j,i,(double)((rgb & 0xff0000) >> 16));
					
					greenAr.set(j,i,(double)((rgb & 0xff00) >> 8));
					
					blueAr.set(j,i,(double)(rgb & 0xff)*k);
					
					alphaAr.set(j,i,(double)(rgb >>> 24));
				}
			}
		}
		for(int i=x; i < maxX; ++i) {
			
			for(int j =y; j < maxY; ++j){
				
				redAr.set(j,i,0.0);
				
				greenAr.set(j,i,0.0);
				
				blueAr.set(j,i,0.0);
	
			}
		}	
	}
	
	
	/**
	 * 
	 * @param maxX
	 * @param maxY
	 * @return
	 */
	public  double[][] getZero2dData( int maxX, int maxY ) {
		
		double[][] d = new double[maxY][maxX];
		
		for(int i=0; i < maxX; ++i) {
			
			for(int j =0; j < maxY; ++j) {
				
				d[j][i] = 0.0; 
			}
		}
			
		return d;
		
	}
	
	/**
	 * 
	 * @param maxX
	 * @param maxY
	 * @return
	 */
	public  PaHeapArray<Double> getZero2dData1( int maxX, int maxY ) {
		
		PaHeapArray<Double> d = new PaHeapArray<Double>(maxY,maxX);
		
		for(int i=0; i < maxX; ++i) {
			
			for(int j =0; j < maxY; ++j) {
				
				d.set(j,i,0.0); 
			}
		}
			
		return d;
		
	}
	
	
	/**
	 * Sets the one of the color components - red, green, blue. Alpha component is copied from source image 
	 * @param sourceImage - source image
	 * @param im - target image
	 * @param x - x - ccordinate in source and target image
	 * @param y  y - ccordinate in source and target image
	 * @param color - color to be set in target image (0 - red, 1 - green, 2 - blue)
	 * @param value - color value
	 */
	public  void setColor(BufferedImage sourceImage, BufferedImage im,
				int x, int y, int color, int value){
		
		Color c1 = new Color(sourceImage.getRGB(x, y));
		
		int alpha = c1.getAlpha();
		
		c1 = new Color(im.getRGB(x, y));
		
		Color newColor = null;
		
		switch(color) {
		
			case 0 : { //red
				
				newColor = new Color(value,c1.getGreen(),c1.getBlue(),alpha);
				
				break;
			}
			case 1 : { //green
				
				newColor = new Color(c1.getRed(),value,c1.getBlue(),alpha);
				
				break;
			}
			case 2 : { //blue
				
				newColor = new Color(c1.getRed(),c1.getGreen(),value,alpha);
				
				break;
			}
			default: { return; }
		
		}
		
		im.setRGB(x, y, newColor.getRGB());
	}
	

	/**
	 * Sets the one of the color components - red, green, blue. Alpha component is copied  
	 * @param im - target image to change one color
	 * @param x - x - ccordinate in source and target image
	 * @param y  y - ccordinate in source and target image
	 * @param color - color to be set in target image (0 - red, 1 - green, 2 - blue)
	 * @param value - color value
	 */
	public void setColor(BufferedImage im,int x, int y, int color, int value){
		
		Color c1 = new Color(im.getRGB(x, y));
		
		int alpha = c1.getAlpha();
		
		Color newColor = null;
		
		switch(color) {
		
			case 0 : { //red
				
				newColor = new Color(value,c1.getGreen(),c1.getBlue(),alpha);
				
				break;
			}
			case 1 : { //green
				
				newColor = new Color(c1.getRed(),value,c1.getBlue(),alpha);
				
				break;
			}
			case 2 : { //blue
				
				newColor = new Color(c1.getRed(),c1.getGreen(),value,alpha);
				
				break;
			}
			default: { return; }
		
		}
		im.setRGB(x, y, newColor.getRGB());
	}

	
	/**
	 * <p>Performs the linearization of histogram. The HSI model is used inside, only the I component is modified</p>
	 * @param sourceImage - source image
	 * @return the image with which the linearization has been done. 
	 */
	public  BufferedImage getLinearizationHSI(BufferedImage sourceImage){
		
		if(sourceImage == null) return null;
		
		int wMax = sourceImage.getWidth();   
		
		int hMax =  sourceImage.getHeight();

		//int M = 255*3+1;//max value of I - without /3
		int M = 256;//max value of I - without /3
		
		int[] iN = new int[M];//there is a  statistic here - array keeps the number of every intensity
		// index of array - is a value of old intensity, before linearization
		
		for(int i =0; i < M; ++i){
			
			iN[i]=0;		
		}
		
		double[] rgb = new double[3];
		

		//to HSI
		for ( int i = 0; i < wMax; i++) {
			
			for ( int j = 0 ; j < hMax; j++) {
				
				int c = sourceImage.getRGB(i, j);
				
				int red =	(c & 0xff0000) >> 16;
			
				int green = (c & 0xff00) >> 8;
		
				int blue =  c  & 0xff;
				
				rgb[0] = red;
				
				rgb[1] = green;
				
				rgb[2] = blue;
				
				float[] hsi1 = new float[3];
				
				Color.RGBtoHSB((int)rgb[0], (int)rgb[1], (int)rgb[2], hsi1);
				
				++iN[(int)(hsi1[2]*255)];
				
				//++iN[red+green+blue];//without /3 to keep integer data
				//++alpha[(c & 0xff000000) >>> 24]; // using operator >>> - because of sign 
			}
		}
		double[] in2 = new double[M];//array for new value of intensity
		//index - is the older value of intensity
		
		for(int i =0; i < M; ++i) in2[i]=0;
		
		//linearization
		int n = wMax*hMax;
		
		for(int i =0; i < M; ++i){
			
			int j = i;
			
			while(j > 0) {
				
				 in2[i] += iN[j];
				 
				 --j;
			}
			//i - old nidex of intesity,  in2[i] - new one
			in2[i] /= n;
		}
		
		//iN = null;
		BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
		
		//back to RGB
		for ( int i = 0; i < wMax; i++) {
			
			for ( int j = 0 ; j < hMax; j++) {
				
				int c = sourceImage.getRGB(i, j);
				
				int red =	(c & 0xff0000) >> 16;
			
				int green = (c & 0xff00) >> 8;
		
				int blue =  c  & 0xff;
				
				int alpha = (c & 0xff000000) >>> 24;
		
				rgb[0] = red;
				
				rgb[1] = green;
				
				rgb[2] = blue;
				
				//convertRGBtoHSI(rgb,hsi,false);
				float[] hsi1 = new float[3];
				
				Color.RGBtoHSB((int)rgb[0], (int)rgb[1], (int)rgb[2], hsi1);
				
				hsi1[2] = (float)in2[(int)(hsi1[2]*255)];
				
				int r_g_b = Color.HSBtoRGB(hsi1[0],hsi1[1], hsi1[2]);

				rgb[0] = (int)((r_g_b & 0x00ff0000) >> 16);//red
				
				rgb[1] = (int) ((r_g_b & 0x0000ff00) >> 8);//green
				
				rgb[2] = (int)(r_g_b & 0x000000ff);//blue
			
				Color c1 = new Color((float)rgb[0]/255,(float) rgb[1]/255, (float)rgb[2]/255,(float)(alpha/255.0));
		
				targetImage.setRGB(i, j,c1.getRGB());
			}
		}
		
		return targetImage;
	}
	
	/**
	 * <p>Performs the linearization of the histogram. The RGB model is used.</p>
	 * @param sourceImage - source image
	 * @return the image with which the linearization has been done. 
	 */
	public  BufferedImage getLinearizationRGB(BufferedImage sourceImage) {
		
		int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
		
		int[] red = new int[256];
		
		int[] green = new int[256];
		
		int[] blue = new int[256];
		
		int[] alpha = new int[256];
		
		for(int i =0; i < 256; ++i){
			
			red[i]=0;
			
			green[i]=0;
			
			blue[i]=0;
			
		}
		
		for ( int i = 0; i < wMax; i++) {
			
			for ( int j = 0 ; j < hMax; j++) {
				
				int c = sourceImage.getRGB(i, j);
				
				++red[(c & 0xff0000) >> 16];
				
				++green[(c & 0xff00) >> 8];
				
				++blue[c  & 0xff];
				
				++alpha[(c & 0xff000000) >>> 24]; // using operator >>> - because of sign 
			}
		}
		
		double[] red2 = new double[256];
		
		double[] green2 = new double[256];
		
		double[] blue2 = new double[256];
		
		for(int i =0; i < 256; ++i){
			
			red2[i]=0;
			
			green2[i]=0;
			
			blue2[i]=0;
			
		
		}
		for(int i =0; i < 256; ++i){
			
			int j = i;
			
			while(j > 0) {
				
				 red2[i] += red[j];
				 
				 green2[i] += green[j];
				 
				 blue2[i] += blue[j];
				 
				 --j;
			}
			int n = wMax*hMax;
			
			red2[i] /= n;
			
			green2[i] /= n;
			
			blue2[i] /= n;
		}
		
		BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
	
		for ( int i = 0; i < wMax; i++) {
			
			for ( int j = 0 ; j < hMax; j++) {
		
				int rgb = sourceImage.getRGB(i, j);
				
				int newRGB = (((rgb & 0xff000000) >>> 24) << 24) | //alpha byte
						
				(((int)(red2[(rgb & 0xff0000) >> 16]*255)) << 16 ) | //red byte
				
				(((int)(green2[(rgb & 0xff00) >> 8]*255)) << 8) | //green byte
				
				((int)(blue2[rgb & 0xff]*255)); //blue byte
				
				
				targetImage.setRGB(i, j,newRGB);
			}
		}
		return targetImage;
	}
	
	/**
	 * Return the modified color according to brightness contrast
	 * @param c1
	 * @param c2
	 * @return
	 */
	@SuppressWarnings("unused")
	private Color getModifiedColor(Color c1, Color c2) {
		
		int red = lowDigitsMove(c2.getRed(), c1.getRed());
		
		int green = lowDigitsMove(c2.getGreen(), c1.getGreen());
		
		int blue = lowDigitsMove(c2.getGreen(), c1.getGreen());
		
		return  new Color(red,green,blue);
		
	}
	/**
	 * to work with false boundaries and countours
	 * Takes low bits of v1 and adds them to the v2
	 * @param v1 - the brightness in the point  1
	 * @param v2 - the brightness in the point 2
	 * @return
	 */
	private  int lowDigitsMove(int v1, int v2) {
		
		int d = 0x0000000f & v1;
		
		if((v2 & 0x000000f0) == 0x000000f0) {
			
			//the case when upper bits  == 1111 in this case the moving of 1 in upper digit can cause
			//the jump white color -> black color
			
			 return (v2 & 0x000000f0)  + (0x0000000f & v2 + d) & 0x0000000f;
			
		}
		else {
			
			return v2 + d;
		}
		
	}
	
	
	/**
	 * Changes the color balance between cold and hot temperature of colors
	 * @param im - source image
	 * @param tValue - value of th change for every color ([0] - red, [1] - green, [2] - blue); 
	 * when = 1 then there is no change for such component
	 * @param lightTones - true if we want to use instrument for light hues only
	 * @return - result image 
	 */
	public  BufferedImage changeColorBalance( BufferedImage im,double[] tValue, boolean lightTones) {
		
		
		if(im == null) { return null; }
		
		int xN = im.getWidth();  
		
		int yN =  im.getHeight();
		
		BufferedImage targetImage = new BufferedImage(xN,yN,BufferedImage.TYPE_INT_RGB);
		
		for ( int x = 0; x < xN; ++x) {
			
			for ( int y = 0; y < yN; ++y) {
				
				int rgb = im.getRGB(x, y);
				
				double[] rgbAr = new double[3];
				
				rgbAr[0] = (double)((rgb & 0xff0000) >> 16);//red
				
				rgbAr[1] = (double) ((rgb & 0xff00) >> 8);//green
				
				rgbAr[2] = (double)(rgb & 0xff);//blue
				
				double[] hsi = new double[3];
				
				PaAlgoConvert.convertRGBtoHSI(rgbAr,hsi, false);
				
				targetImage.setRGB(x, y, rgb);
				
				boolean satCondition = false;
				
				if(!lightTones) {
					
					satCondition = hsi[1] > 0.2 ;
				}
				else {
					
					satCondition = hsi[1] < 0.2 ;
				}
				
				if(satCondition) {
					
					for(int i = 0; i < 3; ++i) {
						
						if(tValue[i] < 0) {	
							
							rgbAr[i] = rgbAr[i]*(-tValue[i]);			
						}
						else {
							
							rgbAr[i] = rgbAr[i]*(1.0/tValue[i]);
						}
						
						rgbAr[i]  = rgbAr[i]  < 0 ? 0 : (rgbAr[i]  > 255 ? 255 : rgbAr[i] );
						
						setColor(targetImage,x,y,i,(int)rgbAr[i]);//blue
					
					}
				}
			}
		}
		
		return targetImage;
	}	
		
		
		/**
		 * Changes the color balance between cold and hot temperature of colors; this function uses rast operations
		 * @param im - source image
		 * @param tValue - value of th change for every color ([0] - red, [1] - green, [2] - blue); 
		 * when = 1 then there is no change for such component
		 * @param lightTones - true if we want to use instrument for light hues only
		 * @return - result image 
		 */
		public  BufferedImage changeColorBalance2( BufferedImage im,double[] tValue, boolean lightTones) {
				
			if(im == null) { return null; }
			
			int xN = im.getWidth();  
			
			int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			   
			   float[] rgbAr = new float[3];
			   
		       float[] hsi = new float[3];
				
		       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
			          	    	
			    	int rgb = data[pixel];
			    	  
					rgbAr[0] = (float)((rgb & 0x00ff0000) >> 16);//red
					
					rgbAr[1] = (float) ((rgb & 0x0000ff00) >> 8);//green
					
					rgbAr[2] = (float)(rgb & 0x000000ff);//blue
					
					int  alpha = (rgb >>> 24); // alpha
	
					//convertRGBtoHSI(rgbAr,hsi, false);
					Color.RGBtoHSB((int)rgbAr[0], (int)rgbAr[1], (int)rgbAr[2], hsi);
					
					boolean satCondition = false;
					
					if(!lightTones) { 	
						satCondition = hsi[1] > 0.2 ; 
						}
					else { 				
						satCondition = hsi[1] < 0.2 ; 
					}
			          
					if(satCondition) {
						
						for(int i = 0; i < 3; ++i) {
							
							if(tValue[i] > 0) {	
								
								rgbAr[i] = (float)(rgbAr[i]*(tValue[i]));			
							}
							else {
								
								rgbAr[i] = (float)(rgbAr[i]*(1.0/(-tValue[i])));
							}
							
							rgbAr[i]  = rgbAr[i]  < 0 ? 0 : (rgbAr[i]  > 255 ? 255 : rgbAr[i] );
						}
						
						data[pixel] = ((int)rgbAr[0]) << 16 | ((int)rgbAr[1]) << 8 | ((int)rgbAr[2]) | alpha << 24;
						
					}
					
			        col++;
			        
			        if (col == xN) {
			        	
			             col = 0;
			             
			             row++;
			             
			             if((row) >= yN) { break;}
			        }
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			      
				float[] rgbAr = new float[3];
				
		        float[] hsi = new float[3];
		        
		        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
          			          
					rgbAr[0] = ((data[pixel+2] & 0x00ff));//red
					
					rgbAr[1] = ((data[pixel + 1]& 0x00ff));//green
					
					rgbAr[2] = ((data[pixel]& 0x00ff));//blue
	
					//convertRGBtoHSI(rgbAr,hsi, false);
					Color.RGBtoHSB((int)rgbAr[0], (int)rgbAr[1],(int)rgbAr[2], hsi);
					
					boolean satCondition = false;
					
					if(!lightTones) { 	
						
						satCondition = hsi[1] > 0.2 ;
						}
					else { 			
						
						satCondition = hsi[1] < 0.2 ; 
					}
			          
					if(satCondition) {
						
						for(int i = 0; i < 3; ++i) {
							
							if(tValue[i] > 0) {	
								
								rgbAr[i] = (float)(rgbAr[i]*(tValue[i]));			
							}
							else {
								
								rgbAr[i] = (float)(rgbAr[i]*(1.0/(-tValue[i])));
							}
							
							rgbAr[i]  = rgbAr[i]  < 0f ? 0f : (rgbAr[i]  > 255f ? 255f : rgbAr[i] );
						}
						
						data[pixel+2] = (byte)rgbAr[0];
						
						data[pixel + 1] = (byte)rgbAr[1];
						
						data[pixel] = (byte)rgbAr[2];
							
					}
			          		                  
		            col++;
		            
		            if (col == xN) {
		            	
		              col = 0;
		              
		              row++;
		              
		              if((row) >= yN) {
		            	  break;
		              }
		            }
		       }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
				
				return null;
			}
			
		return im;
	}
		
		
		/**
		 * Changes the color balance between cold and hot temperature of colors; this function uses rast operations
		 * @param im - source image
		 * @param tValue - value of th change for every color ([0] - red, [1] - green, [2] - blue); 
		 * when = 1 then there is no change for such component
		 * @param lightTones - true if we want to use instrument for light hues only
		 * @return - result image 
		 */
		public  BufferedImage changeHSIBalance( BufferedImage im,double[] tValue) {
				
			if(im == null) { return null; }
			
			int xN = im.getWidth();  
			
			int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			   
			   int[] rgbAr = new int[3];
			   
		       float[] hsi = new float[3];
				
		       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
			          	    	
			    	int rgb = data[pixel];
			    	  
					rgbAr[0] = (int)((rgb & 0x00ff0000) >> 16);//red
					
					rgbAr[1] = (int) ((rgb & 0x0000ff00) >> 8);//green
					
					rgbAr[2] = (int)(rgb & 0x000000ff);//blue
					//int  alpha = (rgb >>> 24); // alpha
	
					//convertRGBtoHSI(rgbAr,hsi, false);
					
					Color.RGBtoHSB(rgbAr[0], rgbAr[1], rgbAr[2], hsi);
									
					for(int i = 0; i < 3; ++i) {
						
						if(tValue[i] > 0) {	
							
							hsi[i] = (float)(hsi[i]*(tValue[i]));			
						}
						else {
							
							hsi[i] = (float)(hsi[i]*(1.0/(-tValue[i])));
						}
						
						hsi[i]  = hsi[i]  < 0f ? 0f : (hsi[i]  > 1.0f ? 1.0f : hsi[i] );
					}
					
					//convertHSItoRGB(false,hsi,rgbAr);
					//data[pixel] = ((int)rgbAr[0]) << 16 | ((int)rgbAr[1]) << 8 | ((int)rgbAr[2]) | alpha << 24;
					
					data[pixel] = Color.HSBtoRGB((float)hsi[0],(float) hsi[1],(float) hsi[2]);
					
			        col++;
			        if (col == xN) {
			        	
			             col = 0;
			             
			             row++;
			             
			             if((row) >= yN) { break;}
			        }
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			      
		        float[] hsi = new float[3];
		        
		        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
	
		        	int alpha = ((int) (data[pixel] >>> 24));
	
					Color.RGBtoHSB(((int) (data[pixel+2] & 0x00ff)), 
							((int) (data[pixel + 1]& 0x00ff)), ((int) (data[pixel]& 0x00ff)),
							hsi);
					

					
						for(int i = 0; i < 3; ++i) {
							
							if(tValue[i] > 0) {	
								
								hsi[i] = (float)(hsi[i]*(tValue[i]));			
							}
							else {
								
								hsi[i] = (float)(hsi[i]*(1.0/(-tValue[i])));
							}
							
							hsi[i]  = hsi[i]  < 0f ? 0f : (hsi[i]  > 1.0f ? 1.0f : hsi[i] );
						}
						//convertHSItoRGB(false,hsi,rgbAr);
						int rgb = Color.HSBtoRGB(hsi[0], hsi[1],hsi[2]);
						
						data[pixel+2] = (byte) ((rgb >> 16) & 0x000000ff);//rgbAr[0];//red
						
						data[pixel + 1] = (byte)((rgb >> 8) & 0x000000ff); //rgbAr[1]; //green
						
						data[pixel] = (byte) (rgb & 0x000000ff);//rgbAr[2];
						
						if (hasAlphaChannel) {
							
							data[pixel + 3] = (byte)alpha;
						} 	
					
			          		                  
		            col++;
		            
		            if (col == xN) {
		            	
		              col = 0;
		              
		              row++;
		              
		              if((row) >= yN) { break;}
		            }
		       }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
				
				return null;
			}
			
		return im;
	}
		
		
	/**
	 * This variant of linearization procedure uses the transformation to HSI model
	 * and correction the I value only.	
	 * @param im - input image
	 * @return - return the equalized image. The input image isn't copied; the changes
	 * are made inside the raster of input image directly. 
	 */
	public  BufferedImage equalization( BufferedImage im) {
			
			if(im == null) { return null; }
			
			//int xN = im.getWidth();  
			//int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			//auxiliary class for dealing with HSI data
			class I_data implements Comparable<I_data> {
				
				int index;
				
				float h;
				
				float s;
				
				public float i;
				
				public I_data(int ind, float h, float s, float i) { 
					
					index = ind; this.h = h; this.s = s; this.i = i;  
				}


				@Override
				public int compareTo(I_data arg0) {
					
					if(i < arg0.i) { return -1;}
					
					if(i > arg0.i) { return 1;}
					
					return 0;
				}
			};
			
			ArrayList<I_data> i_map = new ArrayList<>();
			
			float sum_i = 0;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			   
			   //float[] h_data = new float[data.length]; 
			   //float[] s_data = new float[data.length];
			   //float[] i_data = new float[data.length];
	
			   int[] rgbAr = new int[3];
			   
		       float[] hsi = new float[3];
		       
		       
				//calculates HSI data
		       for (int pixel = 0; pixel < data.length; ++pixel) {
			          	    	
			    	int rgb = data[pixel];
			    	  
					rgbAr[0] = (int)((rgb & 0x00ff0000) >> 16);//red
					
					rgbAr[1] = (int) ((rgb & 0x0000ff00) >> 8);//green
					
					rgbAr[2] = (int)(rgb & 0x000000ff);//blue
	
					Color.RGBtoHSB(rgbAr[0], rgbAr[1], rgbAr[2], hsi);
				
					i_map.add(new I_data(pixel, hsi[0] , hsi[1] , hsi[2]));
					
					sum_i += hsi[2];
		       }
		       
		       //sort HSI data
		       Collections.sort(i_map);
		       
		       //change HSI data
		       float sum_tmp = 0;
		       for(int k = 0; k < i_map.size(); ++k) {
		    	   
		    	   sum_tmp += i_map.get(k).i/sum_i;
		    	
		    	   int rgb = Color.HSBtoRGB(i_map.get(k).h, i_map.get(k).s, sum_tmp);
		    	   
		    	   data[i_map.get(k).index] = rgb;
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			    
				//float[] rgbAr = new float[3];
		        float[] hsi = new float[3];
		        
		        sum_i = 0;
		        
		        for (int pixel = 0; pixel < data.length; pixel += pixelLength) {
          			          
					//rgbAr[0] = ((int) (data[pixel+2] & 0x00ff));//red
					//rgbAr[1] = ((int) (data[pixel + 1]& 0x00ff));//green
					//rgbAr[2] = ((int) (data[pixel]& 0x00ff));//blue
	
		        	//int alpha = ((int) (data[pixel] >>> 24));
					//convertRGBtoHSI(rgbAr,hsi, false);
					Color.RGBtoHSB(((int) (data[pixel+2] & 0x00ff)), 
							((int) (data[pixel + 1]& 0x00ff)), ((int) (data[pixel]& 0x00ff)),
							hsi);
					
					i_map.add(new I_data(pixel, hsi[0] , hsi[1] , hsi[2]));
					
					sum_i += hsi[2];
		        }
		        //sort HSI data
			    Collections.sort(i_map);
			    
			    //change HSI data
			    float sum_tmp = 0;
			    for(int k = 0; k < i_map.size(); ++k) {
			    	
			    	   sum_tmp += i_map.get(k).i/sum_i;
			    	
			    	   int rgb = Color.HSBtoRGB(i_map.get(k).h, i_map.get(k).s, sum_tmp);
			    	   
			    	   int alpha = ((int) (data[i_map.get(k).index] >>> 24));
			    	   
			    	   data[i_map.get(k).index + 2] = (byte) ((rgb >> 16) & 0x000000ff);//rgbAr[0];//red
			    	   
					   data[i_map.get(k).index + 1] = (byte)((rgb >> 8) & 0x000000ff); //rgbAr[1]; //green
					   
					   data[i_map.get(k).index] = (byte) (rgb & 0x000000ff);//rgbAr[2];
					   
					   if (hasAlphaChannel) {
						   
							data[i_map.get(k).index + 3] = (byte)alpha;
					   } 
			    }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
				
				return null;
			}
			
		return im;
	}
			

		/**
		 * Save a subimage made from target image using rectangle rec. If useBlur == true the performs blur on part of subimage
		 * using stride value. User can draw the rectangle over the boundaries of image - in this case reusl subimage will have a random filling
		 * for area which are over the image geometry.
		 * @param targetImage - base image to save rectangle rec from it
		 * @param rec - rectangle to make subimage in targetImage 
		 * @param filePath - full path to folder where to save subimage
		 * @param useBlur - flag to set blur operation inside subimage with stride
		 * @param level - level of blur operation
		 * @param indent - this is an indent inside subimage to form area of blur (it can be less the subimage geometry if stride != 0)
		 * @param parent - parent instrument window
		 */
		static public  BufferedImage saveSubImageWithBlur(BufferedImage targetImage, Rectangle rec, String filePath, 
				boolean useBlur, int level, int indent, PaInstrumentsWindow parent) {
			
			int indent_2 = indent*2;
			
			if(indent_2 >= rec.width || indent_2 >= rec.height) {
				
					JOptionPane.showMessageDialog(parent,
						getMessagesStrs("indentValueTooBigCropSaveOperation"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
					
					return null;
				
			}
			
			File outputfolder = new File(filePath);
			
			if(!outputfolder.isDirectory() || !outputfolder.exists()) {
				
				JOptionPane.showMessageDialog(parent,
	    				"<html>" + getMessagesStrs("folderNotValidFileCropSaveOperation")
	    				+ "<br>" + outputfolder.getAbsolutePath() + "<br>" + 
	    				getMessagesStrs("checkParametersMessage") + "</html>",
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
				
				return null;
			}
			
			BufferedImage im = null;
			try {
				im = targetImage.getSubimage(rec.x, rec.y, rec.width, rec.height);
				
			} catch(RasterFormatException ex) {
				
				//the geometry is over the boundaries (user draw a selection oves boundary of the source image)
				im = getSubImageWithPadding(targetImage, rec.x, rec.y, rec.width, rec.height);
			}
			
			//debug code TODO
			File outputfile = new File(filePath + findLastNumber(filePath) + ".jpg");
			
			try {
				
				if(im != null) {
					
					ImageIO.write(im, "jpg", outputfile);
				}
				
			} catch (IOException e) {
				
				JOptionPane.showMessageDialog(parent,
	    				"<html>" + getMessagesStrs("impposibleToSaveFileCropSaveOperation")
	    				+ "<br>" + outputfile.getAbsolutePath() + "</html>",
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
			}
			
			if(useBlur && level > 0) {
				
				try {
					
					parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					Rectangle recBlur = new Rectangle(indent, indent, rec.width - indent_2, rec.height - indent_2);
					
					BufferedImage im1 = im.getSubimage(recBlur.x, recBlur.y, recBlur.width, recBlur.height);
					
					BufferedImage imSub = PaUtils.deepCopy(im1);
					
					return blurByAverage(imSub, level);
				}
				finally {
					
					parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
			else {
				return null;
			}
		}
		
		static public  BufferedImage getSubImageWithPadding(BufferedImage sourceImage, int x, int y, int width, 
				int height)
		{
			
			//BufferedImage im = null;
			
			int bottomPadding = 0;
			
			int rightPadding = 0;
			
			int topPadding = 0;
			
			int leftPadding = 0;
			
			if((y + height) > sourceImage.getHeight()) {
				
				bottomPadding = y + height - sourceImage.getHeight();
			}
			
			if((x + width) > sourceImage.getWidth()) {
				
				rightPadding = x + width - sourceImage.getWidth();
			}
			
			if(y < 0) {
				
				topPadding = -y;
			}
			
			if(x < 0) {
				
				leftPadding = -x;
			}
			
			BufferedImage newImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
			
			Graphics2D g2d = newImage.createGraphics();
			
			Random rnd = new Random();
			//random filling
			int[] array = ((DataBufferInt) newImage.getRaster().getDataBuffer()).getData();
			
		    for(int i=0; i<array.length; ++i) {
		    	
		         array[i] = rnd.nextInt(0xFFFFFF);
		    }
		      
		    BufferedImage im1 = sourceImage.getSubimage(x + leftPadding, y + topPadding, width - rightPadding, 
		    		height - bottomPadding);

		    
		    g2d.drawImage(im1, null,  leftPadding, topPadding);
		    
		    g2d.dispose();
			
			return newImage;
		}
		
		
	/**
	 * Auxiliary function - finds the next file number in the folder pathTo;
	 * All files in this folder must be like '1.jpeg, 2.jpeg, 3.jpeg'
	 * @param pathTo
	 * @return
	 */
	static public int findLastNumber(String pathTo) {
		
		File folder = new File(pathTo);
		
		if(!folder.exists()) { throw new NullPointerException(); }
		
		File[] listOfFiles = folder.listFiles();
		
		int counter = 0;

		if(listOfFiles.length == 0) { return 0;}
		//counting of jpeg files
		for (int i = 0; i < listOfFiles.length; i++) {
			
			String fileName = listOfFiles[i].getName();
			
			if (listOfFiles[i].isFile() &&  (fileName.contains("jpeg") || fileName.contains("jpg"))) {
				
				String l = fileName.substring(0, fileName.indexOf("."));
				
				try {
					
					Integer value = Integer.valueOf(l);
					
					if(value > counter) { counter = value; }
					
				}
				catch(NumberFormatException e) {
					//do nothing
				}
				
			} 
		}
		
		return counter + 1;
	}
		
			
	/**
	 * Debug purpose only. Save in jpeg file the part of image
	 * @param targetImage - base image to save rectangle rec from it
	 * @param rec - rectangle in targetImage to save 
	 */
	static public  void debugSubImage(BufferedImage targetImage, Rectangle rec, String filePath) {
		
		BufferedImage subIm = targetImage.getSubimage(rec.x, rec.y, rec.width, rec.height);
		
		File outputfile = new File(filePath);
		
		try {
			
			ImageIO.write(subIm, "jpg", outputfile);
			
		} catch (IOException e) {
			
			JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
    				"<html>" + getMessagesStrs("impposibleToSaveFileCropSaveOperation")
    				+ "<br>" + outputfile.getAbsolutePath() + "</html>",
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * Debug purpose only. Save in jpeg file the part of image
	 * @param targetImage - base image to save rectangle rec from it
	 * @param rec - rectangle in targetImage to save 
	 */
	static public  void debugImage(BufferedImage im, String filePath) {
	
		//debug code TODO
		  File outputfile = new File(filePath);
		    
				try {
					
					ImageIO.write(im, "jpg", outputfile);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	/**
	 * 
	 * @param im - source image
	 * @param level
	 * @return
	 */
	public BufferedImage getBlur(BufferedImage im, int level) {
		
		int im_width = im.getWidth();  
		
		int im_height =  im.getHeight();
		
		int min1 = im_width;
		
		if(im_height < im_width) min1 = im_height;
		
		if(level > 99) level = 99;
		
		int radius = (int)(min1*(100-level)/100.0)/2;
		


		
		int[]  t = PaAlgoUtils.getPowerOf2_MaxValue(2*im_width,2*im_height);
		
		int nT2 = t[1];
		int n2 = t[0];
	
		PaHeapArray<Double> Red_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Alpha_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		int deltaX = (nT2 - im_width)/2;
		
		if(deltaX > 0) --deltaX;
		
		int deltaY = (nT2 - im_height)/2;
		
		if(deltaY > 0) --deltaY;
		

		//long time_op  = System.currentTimeMillis();
		
		int rgb_data = im.getRGB(2, 2);
		
		double r_data = (double)((rgb_data & 0x00ff0000) >> 16);
		
		double g_data = (double)((rgb_data & 0x0000ff00) >> 8);
		
		double b_data = (double)(rgb_data & 0x000000ff);
		
		double[] maxData = {r_data,g_data,b_data};
		
		double[] minData = {r_data,g_data,b_data};

		int x1 =0; int y1 =0;

		for ( int i = 0; i < nT2; ++i) {
			
			for ( int j = 0; j < nT2; ++j) {
				
				 int i_j = i + j;
				 
				 x1 = i - deltaX;
				 
				 y1 = j - deltaY;
				 
				 Red_Im.set(j,i,0.0);
				 
				 Green_Im.set(j,i,0.0);
				 
				 Blue_Im.set(j,i,0.0);
				 				
				 if(x1 < 0 || y1 < 0 || x1 >= im_width || y1 >= im_height) {	
					 
					 Red_Rl.set(j,i,0.0);   Green_Rl.set(j,i,0.0);  Blue_Rl.set(j,i,0.0);
				 }
				 else{	
					 
					 int rgb = 0;

					 rgb = im.getRGB(x1, y1);
		
					 double rD = (double)((rgb & 0x00ff0000) >> 16);
					 
					 double gD = (double)((rgb & 0x0000ff00) >> 8);
					 
					 double bD = (double)(rgb & 0x000000ff);
					 
					 Alpha_Rl.set(y1,x1,(double)(rgb >>> 24));
			
					if(i_j%2 != 0 && i_j != 0) {		
						
						Red_Rl.set(j,i,-rD);  Green_Rl.set(j,i,-gD); Blue_Rl.set(j,i,-bD);
					}
					else {		
						
						Red_Rl.set(j,i,rD);  Green_Rl.set(j,i,gD);  Blue_Rl.set(j,i,bD);
					}
					
					if(rD > maxData[0]) maxData[0] = rD;
					
					if(rD < minData[0]) minData[0] = rD;
					
					if(gD > maxData[1]) maxData[1] = gD;
					
					if(gD < minData[1]) minData[1] = gD;
					
					if(bD > maxData[2]) maxData[2] = bD;
					
					if(bD < minData[2]) minData[2] = bD;
					
					
				 }
				
			}
		}

		
		PaAlgoFFT.getFFT1(true,Red_Rl,Red_Im,nT2,nT2,n2,Red_Rl2,Red_Im2);
		
		PaAlgoFFT.getFFT1(true,Green_Rl,Green_Im,nT2,nT2,n2,Green_Rl2,Green_Im2);
		
		PaAlgoFFT.getFFT1(true,Blue_Rl,Blue_Im,nT2,nT2,n2,Blue_Rl2,Blue_Im2);
			
		int deltaX1 = (nT2 - radius*2)/2;
		
		int deltaY1 = (nT2 - radius*2)/2;
		
		int dX1 = deltaX1  + radius*2;
		
		int dY1 = deltaY1 + radius*2;
		
		
		for ( int i = 0; i < nT2; ++i) {
			
			for ( int j = 0; j < deltaY1; ++j) {
				
				 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
				 
				 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
			}
			for ( int j = dY1; j < nT2; ++j) {
				
				 Red_Rl2.set(j,i,0.0);   Green_Rl2.set(j,i,0.0);  Blue_Rl2.set(j,i,0.0);
				 
				 Red_Im2.set(j,i,0.0);   Green_Im2.set(j,i,0.0);  Blue_Im2.set(j,i,0.0);
			}
		}

		for ( int j = deltaY1; j < dY1; ++j) {
			
		for ( int i = 0; i < deltaX1; ++i) {
		
				Red_Rl2.set(j,i,0.0);  Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
				
				Red_Im2.set(j,i,0.0);  Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
			}
			for ( int i = dX1; i < nT2; ++i) {	
				
				 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
				 
				 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
			}
		}
		
		int cX = nT2/2;
		
		Point c = new Point(cX,cX);
		
		Point p;

		for ( int i = deltaY1; i < dX1; ++i) {
			
			for ( int j = deltaY1; j < dY1; ++j) {
				
				p = new Point(i,j);
				
				double r  = c.distance(p);
				
				if(r > radius) {
					
					 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
					 
					 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);	
				}
			}
		}
		
		PaAlgoFFT.getFFT1(false,Red_Rl2,Red_Im2,nT2,nT2,n2,Red_Rl,Red_Im);
		
		PaAlgoFFT.getFFT1(false,Green_Rl2,Green_Im2,nT2,nT2,n2,Green_Rl,Green_Im);
		
		PaAlgoFFT.getFFT1(false,Blue_Rl2,Blue_Im2,nT2,nT2,n2,Blue_Rl,Blue_Im);
		
		//initial data for finding min and max
		double red = Red_Rl.get(deltaY+1,deltaX+1);
		
		double green = Green_Rl.get(deltaY+1,deltaX+1);
		
		double blue = Blue_Rl.get(deltaY+1,deltaX+1);
		
		if((deltaY+1 + deltaX+1)%2 != 0) {
			
			red = -red;
			
			green = - green;
			
			blue = - blue;
		}
		
		double[] max = {red,green,blue};
		
		double[] min = {red,green,blue};
		
		//*************************************
		
		//get back centered data and find max and min values for all color components
		for ( int x = 0; x < im_width; ++x) {
			
			for ( int y = 0; y < im_height; ++y) {
				
				int j = y + deltaY;
				
				int i = x + deltaX;
				
				int i_j = i+j;

				red = Red_Rl.get(j,i);
				
				green = Green_Rl.get(j,i);
				
				blue = Blue_Rl.get(j,i);
	
				//get back the centered data
				if((i_j)%2 != 0 && i_j != 0) {
					
					red = -red;
					
					green = - green;
					
					blue = - blue;
					
					 Red_Rl.set(j,i,red);
					 
					 Green_Rl.set(j,i,green);
					 
					 Blue_Rl.set(j,i,blue);
				}
				
				if(red > max[0]) max[0] = red;
				
				if(red < min[0]) min[0] = red;
				
				if(green > max[1]) max[1] = green;
				
				if(green < min[1]) min[1] = green;
				
				if(blue > max[2]) max[2] = blue;
				
				if(blue < min[2]) min[2] = blue;
			}
		}
		//coefficients to convert initial data range to tne end data range		
		double aRed = (maxData[0]-minData[0])/(max[0]-min[0]);
		
		double aGreen = (maxData[1]-minData[1])/(max[1]-min[1]);
		
		double aBlue = (maxData[2]-minData[2])/(max[2]-min[2]);
		
		double bRed = maxData[0] - aRed*max[0];
		
		double bGreen = maxData[1] - aGreen*max[1];
		
		double bBlue = maxData[2] - aBlue*max[2];
		
		
		BufferedImage targetImage = new BufferedImage(im_width,im_height,BufferedImage.TYPE_INT_RGB);
		
		int r = 0;
		
		int g = 0;
		
		int b = 0;
		
		int j;
		
		int i;
		
		for ( int x = 0; x < im_width; ++x) {
			
			for ( int y = 0; y < im_height; ++y) {
				
				j = y + deltaY;
				
				i = x + deltaX;
				
				r = (int)(aRed*Red_Rl.get(j,i).intValue() + bRed);
				
				g = (int)(aGreen*Green_Rl.get(j,i).intValue()+bGreen);
				
				b = (int)(aBlue*Blue_Rl.get(j,i).intValue()+bBlue);

				r = r > 255 ? 255 : (r < 0 ? 0 : r);
				
				g = g > 255 ? 255 : (g < 0 ? 0 : g);
				
				b = r > 255 ? 255 : (b < 0 ? 0 : b);
				
				int rgb = r << 16 | g << 8 | b |  Alpha_Rl.get(y, x).intValue() << 24;
						
				targetImage.setRGB(x, y, rgb);	
			}
		}

		return targetImage;
	}
	
	
	
	/**
	 * Blur operation
	 * @param im - source image
	 * @param level
	 * @return
	 */
	@Deprecated
	public BufferedImage getBlur2(BufferedImage im, int level) {
		

		int im_width = im.getWidth();  
		
		int im_height =  im.getHeight();
		
		int min1 = im_width;
		
		if(im_height < im_width) min1 = im_height;
		
		if(level > 99) level = 99;
		
		int radius = (int)(min1*(100-level)/100.0)/2;
		
		//multiplication on 2 is because we need to avoid of edge filter effects while FFT
		
		int[]  t = PaAlgoUtils.getPowerOf2_MaxValue(2*im_width,2*im_height);
		
		int nT2 = t[1];
		
		int n2 = t[0];
	
		PaHeapArray<Double> Red_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Alpha_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Im2 = new PaHeapArray<Double>(nT2,nT2);

		
		int rgb_data = im.getRGB(2, 2);
		
		double r_data = (double)((rgb_data & 0x00ff0000) >> 16);
		
		double g_data = (double)((rgb_data & 0x0000ff00) >> 8);
		
		double b_data = (double)(rgb_data & 0x000000ff);
		
		double[] maxData = {r_data,g_data,b_data};
		
		double[] minData = {r_data,g_data,b_data};


		for ( int i = 0; i < nT2; ++i) {
			
			for ( int j = 0; j < nT2; ++j) {
				
				 int i_j = i + j;

				 
				 Red_Im.set(j,i,0.0);
				 
				 Green_Im.set(j,i,0.0);
				 
				 Blue_Im.set(j,i,0.0);
				 				
				 if(i >= im_width || j >= im_height) {	
					 
					 Red_Rl.set(j,i,0.0);   
					 
					 Green_Rl.set(j,i,0.0);  
					 
					 Blue_Rl.set(j,i,0.0);
				 }
				 else{	
					 
					 int rgb = 0;

					 rgb = im.getRGB(i, j);
		
					 double rD = (double)((rgb & 0x00ff0000) >> 16);
					 
					 double gD = (double)((rgb & 0x0000ff00) >> 8);
					 
					 double bD = (double)(rgb & 0x000000ff);
					 
					 Alpha_Rl.set(j,i,(double)(rgb >>> 24));
			
					if(i_j%2 != 0 && i_j != 0) {	
						
						Red_Rl.set(j,i,-rD);  Green_Rl.set(j,i,-gD); Blue_Rl.set(j,i,-bD);
					}
					else {	
						
						Red_Rl.set(j,i,rD);  Green_Rl.set(j,i,gD);  Blue_Rl.set(j,i,bD);
					}
					
					if(rD > maxData[0]) maxData[0] = rD;
					
					if(rD < minData[0]) minData[0] = rD;
					
					if(gD > maxData[1]) maxData[1] = gD;
					
					if(gD < minData[1]) minData[1] = gD;
					
					if(bD > maxData[2]) maxData[2] = bD;
					
					if(bD < minData[2]) minData[2] = bD;
					
					
				 }
				
			}
		}

		
		PaAlgoFFT.getFFT1(true,Red_Rl,Red_Im,nT2,nT2,n2,Red_Rl2,Red_Im2);
		
		PaAlgoFFT.getFFT1(true,Green_Rl,Green_Im,nT2,nT2,n2,Green_Rl2,Green_Im2);
		
		PaAlgoFFT.getFFT1(true,Blue_Rl,Blue_Im,nT2,nT2,n2,Blue_Rl2,Blue_Im2);
		
			
		int deltaX1 = (nT2 - radius*2)/2;
		
		int deltaY1 = (nT2 - radius*2)/2;
		
		int dX1 = deltaX1  + radius*2;
		
		int dY1 = deltaY1 + radius*2;
		
		
		for ( int i = 0; i < nT2; ++i) {
			
			for ( int j = 0; j < deltaY1; ++j) {
				
				 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
				 
				 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
			}
			for ( int j = dY1; j < nT2; ++j) {
				
				 Red_Rl2.set(j,i,0.0);   Green_Rl2.set(j,i,0.0);  Blue_Rl2.set(j,i,0.0);
				 
				 Red_Im2.set(j,i,0.0);   Green_Im2.set(j,i,0.0);  Blue_Im2.set(j,i,0.0);
			}
		}

		for ( int j = deltaY1; j < dY1; ++j) {
			
			for ( int i = 0; i < deltaX1; ++i) {
		
				Red_Rl2.set(j,i,0.0);  Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
				
				Red_Im2.set(j,i,0.0);  Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
			}
			
			for ( int i = dX1; i < nT2; ++i) {	
				
				 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
				 
				 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
			}
		}
		

		Point c = new Point(nT2/2,nT2/2);
		
		Point p;

		for ( int i = deltaY1; i < dX1; ++i) {
			
			for ( int j = deltaY1; j < dY1; ++j) {
				
				p = new Point(i,j);
				
				double r  = c.distance(p);
				
				if(r > radius) {
					
					 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
					 
					 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);	
				}
			}
		}
		
		
		PaAlgoFFT.getFFT1(false,Red_Rl2,Red_Im2,nT2,nT2,n2,Red_Rl,Red_Im);
		
		PaAlgoFFT.getFFT1(false,Green_Rl2,Green_Im2,nT2,nT2,n2,Green_Rl,Green_Im);
		
		PaAlgoFFT.getFFT1(false,Blue_Rl2,Blue_Im2,nT2,nT2,n2,Blue_Rl,Blue_Im);

		
		//initial data for finding min and max
		double red = Red_Rl.get(1,1);
		
		double green = Green_Rl.get(1,1);
		
		double blue = Blue_Rl.get(1,1);
	
		double[] max = {red,green,blue};
		
		double[] min = {red,green,blue};
		//*************************************
		
		//get back centered data and find max and min values for all color components
		for ( int i = 0; i < im_width; ++i) {
			
			for ( int j = 0; j < im_height; ++j) {
				
				int i_j = i+j;

				red = Red_Rl.get(j,i);
				
				green = Green_Rl.get(j,i);
				
				blue = Blue_Rl.get(j,i);
	
				//get back the centered data
				if((i_j)%2 != 0 && i_j != 0) {
					
					red = -red;
					
					green = - green;
					
					blue = - blue;
					
					Red_Rl.set(j,i,red);
					 
					Green_Rl.set(j,i,green);
					 
					Blue_Rl.set(j,i,blue);
				}
				
				if(red > max[0]) max[0] = red;
				
				if(red < min[0]) min[0] = red;
				
				if(green > max[1]) max[1] = green;
				
				if(green < min[1]) min[1] = green;
				
				if(blue > max[2]) max[2] = blue;
				
				if(blue < min[2]) min[2] = blue;
			}
		}
		
		//coefficients to convert initial data range to tne end data range		
		double aRed = (maxData[0]-minData[0])/(max[0]-min[0]);
		
		double aGreen = (maxData[1]-minData[1])/(max[1]-min[1]);
		
		double aBlue = (maxData[2]-minData[2])/(max[2]-min[2]);
		
		double bRed = maxData[0] - aRed*max[0];
		
		double bGreen = maxData[1] - aGreen*max[1];
		
		double bBlue = maxData[2] - aBlue*max[2];
		
		
		BufferedImage targetImage = new BufferedImage(im_width,im_height,BufferedImage.TYPE_INT_RGB);
		
		int r = 0;
		
		int g = 0;
		
		int b = 0;
		
		for ( int i = 0; i < im_width; ++i) {
			
			for ( int j = 0; j < im_height; ++j) {
				

				r = (int)(aRed*Red_Rl.get(j,i).intValue() + bRed);
				
				g = (int)(aGreen*Green_Rl.get(j,i).intValue()+bGreen);
				
				b = (int)(aBlue*Blue_Rl.get(j,i).intValue()+bBlue);

				r = r > 255 ? 255 : (r < 0 ? 0 : r);
				
				g = g > 255 ? 255 : (g < 0 ? 0 : g);
				
				b = r > 255 ? 255 : (b < 0 ? 0 : b);
				
				int rgb = r << 16 | g << 8 | b |  Alpha_Rl.get(j, i).intValue() << 24;
						
				targetImage.setRGB(i, j, rgb);	
			}
		}

		return targetImage;
	}
	
	
	public  void debugImage1(PaHeapArray<Double> rl, PaHeapArray<Double> im, int xN, int yN, String filePath) {
		
		
		BufferedImage targetImage = new BufferedImage(xN,yN,BufferedImage.TYPE_INT_RGB);
		
		//debug code TODO
		if (im == null) {
			
			double max =0 ;
			
			double min =0 ;
			
			for ( int x = 0; x < xN; ++x) {
				
				for ( int y = 0; y < yN; ++y) {
					
					double r = rl.get(y, x);
					
					if(r > max) max = r ;
					
					if(r < min) min = r ;
					
				}
			}
			
			for ( int x = 0; x < xN; ++x) {
				
				for ( int y = 0; y < yN; ++y) {
							
					//stretch the brightness range
					int r = (int)((rl.get(y,x)-min)*255.0/(max-min));
	
					r = r > 255 ? 255 : (r < 0 ? 0 : r);
					
					int rgb = r << 16 | r << 8 | r; 
			
					targetImage.setRGB(x, y, rgb /*newColor.getRGB()*/);	
				}
			}
		}
		else {
			
			double max =0 ;
			
			double min =0 ;
			
			for ( int x = 0; x < xN; ++x) {
				
				for ( int y = 0; y < yN; ++y) {
					
					double r = rl.get(y, x);
					
					double i = im.get(y, x);
					
					double result =  Math.log10(1+Math.sqrt(r*r + i*i)); 
					
					if(result > max) max = result ;
					
					if(result < min) min = result ;
					
					rl.set(y,x,result);
					
				}
			}
			
			for ( int x = 0; x < xN; ++x) {
				
				for ( int y = 0; y < yN; ++y) {
							
					//stretch the brightness range
					int r = (int)((rl.get(y,x)-min)*255.0/(max-min));
	
					r = r > 255 ? 255 : (r < 0 ? 0 : r);
					
					int rgb = r << 16 | r << 8 | r; 
			
					targetImage.setRGB(x, y, rgb /*newColor.getRGB()*/);	
				}
			}
		}
		
		File outputfile = new File(filePath);
		    
		try {
			
			ImageIO.write(targetImage, "jpg", outputfile);
			
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	static public BufferedImage blurByAverage(BufferedImage image, int level) {
	
	
	try {
	
	    //mask width
		final int wMask = level/2;
		
		final int wMask2 = level*level;//number of pixels in the mask
	    
        final int wMax = image.getWidth();
        
        final int hMax = image.getHeight();
        
		//BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_ARGB);
		WritableRaster wr = image.getRaster();

	    final int wMaxNew = wMax+wMask *2;
	    
	    final int hMaxNew = hMax+wMask *2;
	        
    	int[][][] imData = new int[4][hMaxNew][wMaxNew];
	
		DataBuffer buf = image.getRaster().getDataBuffer();
		
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;
		
		//read data in array imData; data can be different types of buffer
		if(buf.getDataType() == DataBuffer.TYPE_INT) {
			
			int[] data = ((DataBufferInt) buf).getData();
			
			readIntData(data,imData, wMax, hMax, wMask);
			
		}
		else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
			
			byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			
			readByteData(data,imData, hasAlphaChannel, wMax, hMax, wMask);
		}
		else {
			
			writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
					, null, true, true, true );
			
			return null;
		}
		
		//fill boundary around image - the width of boundary is half of mask width
        for(int j = 0; j < hMaxNew; ++j) {
        	
        	for(int i = 0; i < wMask; ++i) {
        		
        		 int index = i + wMask;
        		 
        	   	  imData[3][j][i] = imData[3][j][index]; // alpha
        	   	  
        	   	  imData[2][j][i] = imData[2][j][index];  // blue
        	   	  
        	   	  imData[1][j][i] = imData[1][j][index];  // green
        	   	  
        	   	  imData[0][j][i] = imData[0][j][index];  // red
        	   	  
        	   	  index = i + wMax + wMask;
        	   	  
         	   	  int index1 = i - wMask+wMaxNew - 1;
         	   	  
         	   	  imData[3][j][index] = imData[3][j][index1]; // alpha
         	   	  
        	   	  imData[2][j][index] = imData[2][j][index1];  // blue
        	   	  
        	   	  imData[1][j][index] = imData[1][j][index1];  // green
        	   	  
        	   	  imData[0][j][index] = imData[0][j][index1];  // red
        	}
        }
        
        for(int i = 0; i < wMaxNew; ++i) {
        	
        	for(int j = 0; j < wMask; ++j) {
        		
        		  int index = j+wMask;
        		  
        	   	  imData[3][j][i] = imData[3][index][i]; // alpha
        	   	  
        	   	  imData[2][j][i] = imData[2][index][i];  // blue
        	   	  
        	   	  imData[1][j][i] = imData[1][index][i];  // green
        	   	  
        	   	  imData[0][j][i] = imData[0][index][i];  // red
        	   	  
         	   	  index = j+hMax+wMask;
         	   	  
         	   	  int index1 = j+hMaxNew - 1- wMask;
         	   	  
         	   	  imData[3][index][i] = imData[3][index1][i]; // alpha
         	   	  
        	   	  imData[2][index][i] = imData[2][index1][i];  // blue
        	   	  
        	   	  imData[1][index][i] = imData[1][index1][i];  // green
        	   	  
        	   	  imData[0][index][i] = imData[0][index1][i];  // red
        	}
        }
        
		
        int red =0; int green = 0; int blue =0; int alpha = 0;
        
       // double h = 58.0/(wMax * hMax);
        
        //mask processing
    	for ( int x = wMask; x < wMaxNew - wMask; ++x ) {
    		
			for(int y = wMask; y < hMaxNew - wMask; ++y) {
				
				red =0; green = 0; blue =0; alpha = 0;
				
				
				//go over the all mask square
				for(int x1 = x - wMask; x1 <= x + wMask; ++x1 ) {
					
					for(int y1 = y - wMask; y1 <= y + wMask; ++y1 ) {
						
						red += imData[0][y1][x1];
						
						green += imData[1][y1][x1];
						
						blue += imData[2][y1][x1];
						
						alpha += imData[3][y1][x1];
					}
				}	
						
				red /= wMask2;
				
				green /= wMask2;
				
				blue /= wMask2;
				
				alpha /= wMask2;
				
				red = red > 255 ? 255 : (red < 0 ? 0 : red);
				
				green = green > 255 ? 255 : (green < 0 ? 0 : green);
				
				blue = blue > 255 ? 255 : (blue < 0 ? 0 : blue);
				

				 if (hasAlphaChannel) {
					 
					 int[] rgb = {red,green,blue,alpha};
					 
					 wr.setPixel(x - wMask, y - wMask,  rgb);
				 }
				 else {
					 
					 int[] rgb = {red,green,blue};
					 
					 wr.setPixel(x - wMask, y - wMask,  rgb);
				 }
			}
    	}
	}
	catch(ArrayIndexOutOfBoundsException ex) {//the case MUST be investigated !!!!!!!
		
		writeLog("Unexpectable problem: difference in buffer and indexed data " + NEXT_ROW +
				ex.getMessage(), ex, true, true, true );
	}


	return image;
	
}



static void readByteData(byte[] data, int[][][] imData, boolean hasAlphaChannel, int wMax, int hMax, int wMask) {
	
	//read data from image rastr
    if (hasAlphaChannel) {
    	
       final int pixelLength = 4;
       

       for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
          
          imData[3][row+wMask][col+wMask] = ((int) data[pixel+3]); // alpha
          
          imData[2][row+wMask][col+wMask] = ((int) (data[pixel]& 0x00ff)); // blue
          
          imData[1][row+wMask][col+wMask] = ((int) (data[pixel + 1]& 0x00ff)); // green
          
          imData[0][row+wMask][col+wMask] = ((int) (data[pixel+2] & 0x00ff)); // red

          col++;
          
          if (col == wMax) {
        	  
             col = 0;
             
             row++;
             
             if((row) >= hMax) { break;}
          }
       }
    } 
    else {
    	
       int pixelLength = 3;
       
       for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
    	   
    	  int rowIndex = row+wMask;
    	  
    	  int colIndex = col+wMask;
    	  
    	  imData[3][rowIndex][colIndex ] = 255; // alpha
    	  
          imData[2][rowIndex][colIndex ] = ((int) (data[pixel] & 0x00ff)); // blue
          
          imData[1][rowIndex][colIndex ] = ((int) (data[pixel + 1] & 0x00ff)); // green
          
          imData[0][rowIndex][colIndex ] = ((int) (data[pixel+2] & 0x00ff)); // red
                  
          col++;
          
          if (col == wMax) {
        	  
             col = 0;
             
             row++;
             
             if((row) >= hMax) { break;}
          }
       }
    }

}

static void readIntData(int[] data, int[][][] imData, int wMax, int hMax, int wMask) {

       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
          
    	  int rgb = data[pixel];
    	  
          imData[3][row+wMask][col+wMask] = (rgb >>> 24); // alpha
          
          imData[2][row+wMask][col+wMask] = (rgb & 0x000000ff); // blue
          
          imData[1][row+wMask][col+wMask] = ((rgb & 0x0000ff00) >> 8); // green
          
          imData[0][row+wMask][col+wMask] = ((rgb & 0x00ff0000) >> 16); // red
          
          col++;
          
          if (col == wMax) {
        	  
             col = 0;
             
             row++;
             
             if((row) >= hMax) { break;}
             
          }
       }


}


/**
 * Makes the amplification of sharpness level. Can use three types of filters - Laplas, Sobel, Sobel + Laplas
 * @param sourceImage - source image to process
 * @param filterType - sharpness filter type(Laplas,Sobel,Sobel+Laplas)
 * @param level - level of sharpness change; this parameter limits the maximum level of change of 
 * every RGB component while modification by algorithm 
 * @return new image after shrpness increase
 */
public Image useMask(BufferedImage sourceImage, int[][] mask) {
	
	

		int maskSize = mask.length;
	//	int maskSize_2 = mask.length/2;
		
		int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
		
		BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
		

		int threshold = (int)( maskSize*maskSize*0.85);
		
		ArrayList<Pair> rList = new ArrayList<Pair>();
		//indexes in this for operator: we use 3x3 mask - we leaves the 1 pixel border without processing;
		//it will be restored in the end
		for ( int i = 0; i < (wMax - maskSize); i += 1) {
			
			for ( int j = 0 ; j < (hMax -  maskSize); j += 1) {
				
				int result = 0;
				
				for(int x = 0; x <  maskSize; ++x) {
					
					for(int y = 0; y <  maskSize; ++y) {
						
						int c = sourceImage.getRGB(i + x, j + y);
						
						int m = mask[y][x];
						
						boolean imR = ((c & 0xff0000) >> 16) < 100;
					
						
						if(!(imR ^ (m < 100))) { ++result; }
					}
				}
				
				if(result > threshold)  {
					
					rList.add(new Pair(i,j));
		
				}
				
			}
		}
		
		for(Pair p : rList) {
			
			for(int x = 0; x <  maskSize; ++x) {
				
				for(int y = 0; y <  maskSize; ++y) {
					
					
					int m = mask[y][x] == 0 ? 0 : 255;
					
					int newRGB = 0 | //alpha byte
							(m << 16 ) | //red byte
							(m << 8) | //green byte
							(m);//blue byte
					
					 targetImage.setRGB(p.x + x, p.y + y, newRGB);
				}
			}
		}
			 		
		return   targetImage;
}

/**
 * Generates the square masks with different types of shapes inside : corners, circles, squares
 * Background is set to 0, the shape's occupied area is filled with 1.
 * @param size - size of the required mask
 * @param scale - distinguishing size of the shape ( radius for circle, side size for square, etc)
 * @param type - type of the shape
 * @param filler - value to fill the shape area inside mask (The value is ussually used == 1)
 * @return the 2D array with size 
 */
public int[][] maskGenerator(int size, int scale, PaMaskTypeEnum type, int filler) {
	
	int[][] r = new int[size][size];
	
	filler = 255; 
	
	if ( scale >= size ) {
		
		System.out.println("Scale can't be > size");
		
		throw new NullPointerException();
	}
	
	if(type ==  PaMaskTypeEnum.CIRCLE && (size%2 == 0 || size < 5)) {
		
		System.out.println("For mask type CIRCLE the size must be odd and >= 5 pixels");
		
		throw new NullPointerException();
		
	}
	int radius_2 = (scale * scale)/4;
	
	int center = size / 2;
	
	int size_scale = size - scale;
	
	switch(type) {
	
		case CIRCLE : {
			
			for ( int x = 0; x < size; ++x ) {
				
				for ( int y = 0; y < size; ++y ) {
					
					int x_c = (x - center);
					
					x_c *= x_c;
					
					int y_c = (y - center);
					
					y_c *= y_c;
					
					if( (x_c + y_c) <= radius_2) {
						
						r[x][y] = filler;
					}
					else {
						
						r[x][y] = 0;
					}
				}
			}
			
			break;
		}
		case CORNER_LEFT_TOP : {
			
			for ( int x = 0; x < size; ++x ) {
				
				for ( int y = 0; y < size; ++y ) {
					
					if( x <= scale || y <= scale) {
						
						r[x][y] = filler;
					}
					else {
						r[x][y] = 0;
					}
				}
			}
			
			break;
		}
		case CORNER_RIGHT_TOP : {
			
			for ( int x = 0; x < size; ++x ) {
				
				for ( int y = 0; y < size; ++y ) {
					
					if( x >= size_scale || y <= scale) {
						
						r[x][y] = filler;
					}
					else {
						
						r[x][y] = 0;
					}
				}
			}
			
			break;
		}
		case CORNER_RIGHT_BOTTOM : {
			
			for ( int x = 0; x < size; ++x ) {
				
				for ( int y = 0; y < size; ++y ) {
					
					if( x >= size_scale || y >= size_scale) {
						
						r[x][y] = filler;
					}
					else {
						r[x][y] = 0;
					}
				}
			}
			
			break;
		}
		case CORNER_LEFT_BOTTOM : {
			
			for ( int x = 0; x < size; ++x ) {
				
				for ( int y = 0; y < size; ++y ) {
					
					if( x <= scale || y >= size_scale) {
						
						r[x][y] = filler;
					}
					else {
						r[x][y] = 0;
					}
				}
			}
			
			break;
		}
		case SQUARE : {
			
			int s4 = (size - scale)/2;
			
			int s4_scale = s4 + scale;
			
			for ( int x = 0; x < size; ++x ) {
				
				for ( int y = 0; y < size; ++y ) {
					
					if( (x >= s4 && x <= s4_scale) && (y >= s4 && y <= s4_scale)) {
						
						r[x][y] = filler;
					}
					else {
						
						r[x][y] = 0;
					}
				}
			}
			
			break;
		}
		
		default : {}
	
	}
	
	return r;
}


/**
 * Makes the amplification of sharpness level. Can use three types of filters - Laplas, Sobel, Sobel + Laplas
 * @param sourceImage - source image to process
 * @param filterType - sharpness filter type(Laplas,Sobel,Sobel+Laplas)
 * @param level - level of sharpness change; this parameter limits the maximum level of change of 
 * every RGB component while modification by algorithm 
 * @return new image after shrpness increase
 */
public Image dilatation(BufferedImage sourceImage, int maskSize) {
	
		
		int maskSize_2 = maskSize/2;
		
		int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
		
		//BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
	
		BufferedImage targetImage = PaUtils.deepCopy(sourceImage);
		
		int RGB_1 = 0 | //alpha byte
				(255 << 16 ) | //red byte
				(255 << 8) | //green byte
				(255);//blue byte

		//indexes in this for operator: we use 3x3 mask - we leaves the 1 pixel border without processing;
		//it will be restored in the end
		for ( int i = maskSize_2; i < (wMax - maskSize_2); i += 1) {
			
			for ( int j = maskSize_2; j < (hMax -  maskSize_2); j += 1) {
				
				int c = (sourceImage.getRGB(i , j) & 0xff0000) >> 16;
			
				if(c == 0) { continue; }
				
				int t = (sourceImage.getRGB(i , j - 1) & 0xff0000) >> 16;
				
				int b = (sourceImage.getRGB(i , j + 1) & 0xff0000) >> 16;
				
				int l = (sourceImage.getRGB(i - 1 , j) & 0xff0000) >> 16;
					
				int r = (sourceImage.getRGB(i + 1, j) & 0xff0000) >> 16;
	
				if(t == 0 && b != 0) {	
					
					for(int x = 0; x <  maskSize; ++x) {
						
						for(int y = 0; y <  maskSize_2; ++y) {
							
							targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
						}
					}
				}
				else if(t != 0 && b == 0) {
					
					for(int x = 0; x <  maskSize; ++x) {
						
						for(int y = maskSize_2 + 1; y <  maskSize; ++y) {
							
							targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
						}
					}
				}
				if(l == 0 && r != 0) {
					
					for(int x = 0; x <  maskSize_2; ++x) {
						
						for(int y = 0; y <  maskSize; ++y) {
							
							targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
						}
					}
				}
				else if(l != 0 && r == 0) {
					
					for(int x = maskSize_2 + 1 ; x <  maskSize; ++x) {
						
						for(int y = 0; y < maskSize; ++y) {
							
							targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
						}
					}
				}
				else { continue; }
					
			}
				
		}
	 		
		return   targetImage;
}

public Image dilatation1(BufferedImage sourceImage, int maskSize) {
	
	
	int maskSize_2 = maskSize/2;
	
	int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
	
	//BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);

	BufferedImage targetImage = PaUtils.deepCopy(sourceImage);
	
	int RGB_1 = 0 | //alpha byte
			(255 << 16 ) | //red byte
			(255 << 8) | //green byte
			(255);//blue byte

	//indexes in this for operator: we use 3x3 mask - we leaves the 1 pixel border without processing;
	//it will be restored in the end
	for ( int i = maskSize_2; i < (wMax - maskSize_2); i += 1) {
		
		for ( int j = maskSize_2; j < (hMax -  maskSize_2); j += 1) {
			
			boolean flag = false;
			
			for(int x = 0; x <  maskSize; ++x) {
				
				for(int y = 0; y <  maskSize_2; ++y) {
					
					int c = (sourceImage.getRGB(i -  maskSize_2 + x, j - maskSize_2 + y) & 0xff0000) >> 16;
				
					if(c != 0) { flag = true; break;}
				
				}
				
				if(flag) { break; }
			}
			
			int c = (sourceImage.getRGB(i , j) & 0xff0000) >> 16;
		
			if(c == 0) { continue; }
			
			int t = (sourceImage.getRGB(i , j - 1) & 0xff0000) >> 16;
			
			int b = (sourceImage.getRGB(i , j + 1) & 0xff0000) >> 16;
			
			int l = (sourceImage.getRGB(i - 1 , j) & 0xff0000) >> 16;
						
			int r = (sourceImage.getRGB(i + 1, j) & 0xff0000) >> 16;

			if(t == 0 && b != 0) {	
				
				for(int x = 0; x <  maskSize; ++x) {
					
					for(int y = 0; y <  maskSize_2; ++y) {
						
						targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
					}
				}
			}
			else if(t != 0 && b == 0) {
				
				for(int x = 0; x <  maskSize; ++x) {
					
					for(int y = maskSize_2 + 1; y <  maskSize; ++y) {
						
						targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
					}
				}
			}
			if(l == 0 && r != 0) {
				
				for(int x = 0; x <  maskSize_2; ++x) {
					
					for(int y = 0; y <  maskSize; ++y) {
						
						targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
					}
				}
			}
			else if(l != 0 && r == 0) {
				
				for(int x = maskSize_2 + 1 ; x <  maskSize; ++x) {
					
					for(int y = 0; y < maskSize; ++y) {
						
						targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, RGB_1);
					}
				}
			}
			else { continue; }
				
		}
			
	}
 		
	return   targetImage;
}
/**
 * Makes the morphological operation called 'erosion'. The mask goes along the boundaries and removes symmetric part
 * of the image body according to current mask position. This algorithms uses the square mask.
 * @param sourceImage - source image to process
 * @param maskSize - size of the square mask; it must be always odd in order to have a central point. 
 * @return new image after errosion operation
 */
public Image erosion(BufferedImage sourceImage, int maskSize) {
	
	if ( sourceImage == null ) {
		
		System.out.println("Can't perform erosion operatino with null image.");
		
		return null; 
	}
	
	if(maskSize%2 == 0) {
		
		System.out.println("Can't perform erosion operatio with current size of the mask (the mask size must be odd).");
		
		return null; 
	}
	
	int maskSize_2 = maskSize/2;
	
	int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
	
	//BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);

	BufferedImage targetImage = PaUtils.deepCopy(sourceImage);
	
 //shift the center of the mask all over the image
	for ( int i = maskSize_2; i < (wMax - maskSize_2); i += 1) {
		
		for ( int j = maskSize_2; j < (hMax -  maskSize_2); j += 1) {
			
			int c = (sourceImage.getRGB(i , j) & 0xff0000) >> 16;
		
			if(c == 0 ) { continue; } //the center of the ,mask is on the background point - not interesting case for erosion, skip
			//***************************************
			//further the algorithms tries to investigate  points relative ti the centere of the mask position : over, under, left and right 
			int t = (sourceImage.getRGB(i , j - maskSize_2) & 0xff0000) >> 16; //top point (on the edge of the mask)
					
			int b = (sourceImage.getRGB(i , j + maskSize_2) & 0xff0000) >> 16; //bottom point (on the edge of the mask)
			
			int t1_index = j - maskSize_2 - 1;
			
			int t1 = 0;
			
			//control of the boundary case - the boundary of the mask is on the very edge of image, 
			//but we need a neigbour out of mask boundary - we do it in artificial way
			if(t1_index >= 0) {
				
				t1 = (sourceImage.getRGB(i , t1_index)& 0xff0000) >> 16; //next point over the top point ( top1 point)
			}
			
			int b1_index = j + maskSize_2 + 1;
			
			int b1 = 0;
			//control of boundary case - the boundary of the mask is on the very edge of image, 
			//but we need a neigbour out of mask boundary - we do it in artificial way
			if(b1_index < hMax) {
				
				b1 = (sourceImage.getRGB(i ,  b1_index )& 0xff0000) >> 16;//next point under the bottom point (bottom1 point)
			}
			
			//if the top point is white, and top1 point is background, then this is the case for using of erosion 
			if(t != 0 && t1 == 0) {	
				
				for(int x = 0; x <  maskSize; ++x) {
					
					for(int y = 0; y <  maskSize_2; ++y) {
					
						targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, 0);
						
					}
				}
			}
			else if(b != 0 && b1 == 0) {
				//if the bottom point is white, and bottom1 point is background, then this is the case for using of erosion 
				for(int x = 0; x <  maskSize; ++x) {
					
					for(int y = maskSize_2 + 1; y <  maskSize; ++y) {
						
						targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, 0);
					}
				}
			} else {
				
				int l = (sourceImage.getRGB(i - maskSize_2, j) & 0xff0000) >> 16;//left point (on the edge of the mask)
					
				int r = (sourceImage.getRGB(i + maskSize_2, j) & 0xff0000) >> 16;//right point (on the edge of the mask)
				
				int l1_index = i - maskSize_2 - 1;
				
				int l1 = 0;
				//control of boundary case - the boundary of the mask is on the very edge of image, 
				//but we need a neigbour out of mask boundary - we do it in artificial way
				if(l1_index >= 0) {
					
					l1 = (sourceImage.getRGB(l1_index, j) & 0xff0000) >> 16; //next point on the left of left point ( left1 )
				}
				
				int r1_index = i + maskSize_2 + 1;
				
				int r1 = 0;
				//control of boundary case - the boundary of the mask is on the very edge of image, 
				//but we need a neigbour out of mask boundary - we do it in artificial way
				if(r1_index < wMax) {
					
					r1 = (sourceImage.getRGB(r1_index, j) & 0xff0000) >> 16; //next point on the right of right point ( right1 )
				}
				
				if(l1 == 0 && l != 0) {
					//if the left point is white, and left1 point is background, then this is the case for using of erosion 
					for(int x = 0; x <  maskSize_2; ++x) {
						
						for(int y = 0; y <  maskSize; ++y) {
							
							targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, 0);
						}
					}
				}
				else if(r != 0 && r1 == 0) {
					//if the right point is white, and right1 point is background, then this is the case for using of erosion 
					for(int x = maskSize_2 + 1 ; x <  maskSize; ++x) {
						
						for(int y = 0; y <  maskSize; ++y) {
							
							targetImage.setRGB(i -  maskSize_2 + x, j - maskSize_2 + y, 0);
						}
					}
				}
				else { continue; }
			}	
		}	
	}
 		
	return   targetImage;
}

public ArrayList<Integer> getFigureNumber(BufferedImage sourceImage, Rectangle area, int cellSize, 
		BufferedImage targetImage) {
	
	if ( sourceImage == null ) {
		
		System.out.println("Can't perform operation with null image.");
		
		return null; 
	}
	
	int cellSize_2 = cellSize*cellSize;
	
	int [][] chainCode = {{3,2,1}, {4,-1,0}, {5,6,7}};
	
	ArrayList<Integer> resList = new ArrayList<Integer>();
	
	int initH = sourceImage.getHeight();
	
	int initW = sourceImage.getWidth();
	
	int h = sourceImage.getHeight()/cellSize;
	
	int w = sourceImage.getWidth()/cellSize;
	
	int [][] res = new int[h][w];

	for ( int y = 0; y < h; ++y  ) {
		
		for ( int x = 0; x < w; ++x  ) {
			
			res[y][x] = -1;
		}
	}
	
	//we must find the first boundary point
	int x = 0;
	
	int y = 0;
	
	boolean stFlag = false;
	
	for ( y = 0; y < h; ++y  ) {
		
		for ( x = 0; x < w; ++x  ) {
			
			int c = 0;
			
			for ( int j = y*cellSize; j < (y*cellSize + cellSize); ++j  ) {
				
				for ( int i = x*cellSize; i < (x*cellSize + cellSize); ++i  ) {
				
					c += ((sourceImage.getRGB(i, j)  & 0xff0000) >> 16) > 100 ? 1 : 0;
					
				}
			}
			
			if ( c == 0 || c == ( cellSize* cellSize)) {
				continue; 
			}

			else {
				
				stFlag = true;
				
				break;
			}
		}
		if(stFlag) { break; }
	}
		
	int cnt = 0;
	
	boolean removeStartPointFlag = true;
	
	ArrayList<Pair> forbList = new ArrayList<Pair>(); 
		
	int[][] mask = new int[3][3];
	
	int stackCounter = 0;
	
	int startX = x;
	
	int startY = y;
	
	do {	
		
		int prevX = x;
		
		int prevY = y;
		
		for(int i = 0; i < 3; ++i) {
			
			int globalX = x + i - 1;
			
			for(int j = 0; j < 3; ++j) {
								
				int globalY = y + j - 1;
				
				//central element (current) and previous cell (forbiden for investigation) is set to -1
				if((j == 1 && i == 1 ) || isForbidPoint(globalX, globalY, forbList )) { mask[j][i] = -1; continue; }
				
				//mask can be out of the scaled grid - then the mask element is set to 0 (as if it were an element with background pixels only)
				if((globalY < 0 || globalX < 0) || (globalY >= h || globalX >= w)) { mask[j][i] = 0;  continue; } 
				
				int c = 0;
				
				int pixelGlobalX = globalX*cellSize;
				
				int pixelGlobalY = globalY*cellSize;
				
				
				for ( int j1 = pixelGlobalY; j1 < (pixelGlobalY + cellSize); ++j1  ) {
					
					for ( int i1 = pixelGlobalX; i1 < (pixelGlobalX + cellSize); ++i1  ) {
							
						if(i1 >= initW || j1 >= initH) { continue; }
						
						c += ((sourceImage.getRGB(i1 , j1)  & 0xff0000) >> 16) > 50 ? 1 : 0;
	
					}
				}
				
				mask[j][i] = c;
				
			}
		}
		
		++cnt;

		int BIG_NUMBER =  1000000000;
		
		int min = BIG_NUMBER;
		
		int minX = 0;
		
		int minY = 0;
		
		for(int x1 = 0; x1 < 3; ++x1) {
			
			for(int y1 = 0; y1 < 3; ++y1) {
	
				if(mask[y1][x1] == -1) { continue; }
				
				if(mask[y1][x1] < min && (mask[y1][x1] > 0 && mask[y1][x1] < cellSize_2)) { 
					min = mask[y1][x1]; minX = x1; minY = y1;
				}
			}
		}
	
		
		if(min == BIG_NUMBER) {
			
			if(mask[0][1] == cellSize_2 && (mask[0][0] == 0 || mask[0][2] == 0)) { /*x = x*/; y = y - 1; }
			else
			if(mask[1][2] == cellSize_2 && (mask[0][2] == 0 || mask[2][2] == 0)) { x = x + 1; /*y = y*/; }
			else
			if(mask[2][1] == cellSize_2 && (mask[2][0] == 0 || mask[2][2] == 0)) { /*x = x*/; y = y + 1; }
			else
			if(mask[1][0] == cellSize_2 && (mask[0][0] == 0 || mask[2][0] == 0)) { x = x - 1; /*y = y; */}
			else
			if(mask[0][0] == cellSize_2 && (mask[1][0] == 0 || mask[0][1] == 0)) { x = x - 1; y = y - 1; }
			else
			if(mask[2][0] == cellSize_2 && (mask[2][1] == 0 || mask[1][0] == 0)) { x = x - 1; y = y + 1; }
			else
			if(mask[2][2] == cellSize_2 && (mask[2][1] == 0 || mask[1][2] == 0)) { x = x + 1; y = y + 1; }
			else
			if(mask[0][2] == cellSize_2 && (mask[0][1] == 0 || mask[1][2] == 0)) { x = x + 1; y = y - 1; }
		}
		else {
			
			x = x - 1 + minX;
			
			y =  y - 1 + minY;
		}
	
		if(startX == x && startY == y) { 
			
			resList.add(res[y][x] );
			
			//*** DEBUG
			// visualizeCell(prevX, prevY, targetImage, cellSize);
			try {
				
				ImageIO.write(targetImage, "jpg",new File("D:\\test"+cnt+".jpg"));
				
			} catch (IOException e) {
				
				writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
			}
			break; 
		}
		else {
			//we  have stacked -  one step backward
			if(prevX == x && prevY == y) { 
				
				++stackCounter;
				
				x = forbList.get(forbList.size() - stackCounter).x;
				
				y = forbList.get(forbList.size() - stackCounter).y;
				
				forbList.add(new Pair(prevX, prevY));
				
				++stackCounter;
				
				continue;
			}
			
			stackCounter = 0;
			
			res[prevY][prevX] = chainCode[minY][minX];
			
			resList.add(res[prevY][prevX] );
			
			forbList.add(new Pair(prevX, prevY));
			
			//remove the start element as the boundary shifts
			if(removeStartPointFlag && forbList.size() == 9 ) {
				
				forbList.remove(0);
				
				removeStartPointFlag = false;
			}
			
			//*** DEBUG
			// visualizeCell(prevX, prevY, targetImage, cellSize);
		try {
			
				ImageIO.write(targetImage, "jpg",new File("D:\\test"+cnt+".jpg"));
				
			} catch (IOException e) {
				
				writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
			}
		}
		
	}while(true);
	
	return resList;
}


	
	
		
		


public ArrayList<Integer> getDiffFigureNumber(ArrayList<Integer> s) {
	
	int lastIndex = s.size() - 1;
	
	if(s.get(lastIndex) == -1) { s.remove(lastIndex--); }
	
	for(int i = s.size() - 1; i >= 0; --i) {
		
		int c;
		
		if(i == 0) {
			
			c = s.get(0) - s.get(lastIndex);
		}
		else {
			c = s.get(i) - s.get(i - 1);
		}
		
		switch(c) {
		
			case  -1:  { c = 5; break; }
			
			case  -2:  { c = 6; break; }
			
			case  -3:  { c = 7; break; }
			
			case  -4:  { c = 0; break; }
			
			case  -5:  { c = 1; break; }
			
			case  -6:  { c = 2; break; }
			
			case  -7:  { c = 3; break; }
			
			default : {};
		
		};
		
		s.set(i, c);
		
	}	
	
	return s;
}


boolean isForbidPoint(int x, int y, ArrayList<Pair> forbList ) {

	for(Pair p : forbList) {
		
		if(p.x == x && p.y == y) { return true; }
		
	}
	return false;

}

}
