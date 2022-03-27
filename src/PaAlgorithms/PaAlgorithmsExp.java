/**
 * 
 */
package PaAlgorithms;

import static PaGlobal.PaLog.writeLogOnly;
import java.awt.Image;
import java.awt.image.BufferedImage;
import PaEditor.PaSharpButton.FILTER_TYPE;

/**
 * Experimental algorithms, they are not used 
 * @author Andrey Dashkov
 *
 */
public class PaAlgorithmsExp {

	/**
	 * 
	 */
	public PaAlgorithmsExp() {
	}
	
	/**
	 * This shift enum for determination of shift operation of matrix while calculations;

	 * @author avd
	 *
	 */
	private static  enum Shift {
		
		X_DIRECTION,
		
		Y_DIRECTION
		
	}
	
	
	

	public Image countour(BufferedImage sourceImage, FILTER_TYPE filterType, 
			int level) {
		
		
		try {
			/**
			 * CornersEdgesFind 3x3 for Laplas operator
			 */
			int[][] lapMask = {
				   {0,-1,0},
				   {-1,4,-1},
				   {0,-1,0}
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
							
							rC = laplasFunction(colors, lapMask, level);
		
							int red = (int)(/*colors[0][1][1] +  */rC[0]); //red
							
							int green = (int)(/*colors[1][1][1] +  */rC[1]);//green
							
							int blue = (int)(/*colors[2][1][1] +  */rC[2]);//blue
							
							//0 ..255 range control
							newImage[0][j-1][i-1] = red < 0 ? 0 : (red > 255 ? 255 : red);
							
							newImage[1][j-1][i-1] = green < 0 ? 0 : (green > 255 ? 255 : green);
							
							newImage[2][j-1][i-1] = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
							
							break;
						}
						case SOBEL : {
							
							rC = sobelFilter(colors, level);
							
							int red = (int)(/*colors[0][1][1] +  */rC[0]); //red
							
							int green = (int)(/*colors[1][1][1] +  */rC[1]);//green
							
							int blue = (int)(/*colors[2][1][1] +  */rC[2]);//blue
							
							//0 ..255 range control
							newImage[0][j-1][i-1] = red < 0 ? 0 : (red > 255 ? 255 : red);
							
							newImage[1][j-1][i-1] = green < 0 ? 0 : (green > 255 ? 255 : green);
							
							newImage[2][j-1][i-1] = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
	
							break;
						}
						
						case SOBEL_LAPLAS : {
							
							rC = sobelFilter(colors, level);
							
							int[] rC2 = laplasFunction(colors, lapMask, level);
							
							int red = (int)(/*colors[0][1][1] + */rC[0] + rC2[0]); //red
							
							int green = (int)(/*colors[1][1][1] + */rC[1] + rC2[2]);//green
							
							int blue = (int)(/*colors[2][1][1] + */rC[2] + rC2[2]);//blue
							
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
	 * <p>Returns laplas operator value. Second index in colors is a row, or 'y' according to image; third index is a column, or 'x'
	 * according to image; first index is a color component - red, green or blue; laplasian performes a separate calculation for all
	 * color components</p>
	 * @param colors - three planes - red,green, blue every plane - 3x3 pixels; colors is a matrix with info about image
	 * laplas operator will be calculated for this value using mask - lapMask
	 * @param lapMask - mask 3 x 3 which determines the form of laplasian
	 * @param - level - the threshold level of sharpness
	 * @return three value of red,green, blue for pixel in 1,1 of colors
	 */
	private int[] laplasFunction(int[][][] colors, int lapMask[][], int level ){
		
		int[] cl = new int[3];
		
		for(int i = 0; i < 3; ++i ) {
	
			cl[i] = colors[i][0][0]*lapMask[0][0] +
					colors[i][0][1]*lapMask[0][1]+
					colors[i][0][2]*lapMask[0][2]+
					colors[i][1][0]*lapMask[1][0]+
					colors[i][1][1]*lapMask[1][1]+
					colors[i][1][2]*lapMask[1][2]+
					colors[i][2][0]*lapMask[2][0]+
					colors[i][2][1]*lapMask[2][1]+
					colors[i][2][2]*lapMask[2][2];
		

			if(cl[i] < -level ) { cl[i] = -level; }
			
			if(cl[i] > level ) { cl[i] = level; }

		}
		return cl;
	}
	
	
	private int[] sobelFilter(int[][][] colors, int level){
		
		int[] cl = new int[3];
		
		for(int i = 0; i < 3; ++i ) {
			
			int s1 = colors[i][2][0]+2*colors[i][2][1] +colors[i][2][2] - colors[i][0][0]
					-2*colors[i][0][1]-colors[i][0][2];
			
			if(s1 < 0) s1 = -s1;
			
			
			int s2 = colors[i][0][2]+2*colors[i][1][2] +colors[i][2][2] - colors[i][0][0]
					-2*colors[i][1][0]-colors[i][2][0];
			
			if(s2 < 0) s2 = -s2;
			
			cl[i] = s1 + s2;
			

			if(cl[i] < -level ) { cl[i] = -level; }
			
			if(cl[i] > level ) { cl[i] = level; }
		}
		return cl;
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
	private void shiftMatrix(int[][][] colors,Shift shiftFlag){
		
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
}
