
package PaEditor;

import java.awt.image.BufferedImage;

/**
 * @author Andrey Dashkov
 *
 */
public class PaIntegralImageData {

	/**
	 * 
	 */
	public PaIntegralImageData() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	/**
	 * Creates integral data for an image. The image is treated as binary 0/1 with a threshold, the red component is used
	 * for determination of the pixel value; the
	 * @param img
	 * @param threshold - threshold to determine the value in a pixel - 0 or 1
	 * @return
	 */
	public static int[][] getIntegralDataFromImage(BufferedImage img, int threshold) {
		
		int h = img.getHeight();
		
		int w = img.getWidth();
		
		int[][] res = new int[h][w];

		int v;
		
		int v_j = 0;
		
		int v_i = 0;
		
		int v_ij = 0;

		
		for(int j = 0; j < h; ++j){
			
			for(int i = 0; i < w; ++i){
				
				v = ((int)((img.getRGB(i, j) & 0x00ff0000) >> 16)) >  threshold ? 1 : 0;//red
				
				if(j != 0) {
					
					v_j = res[j-1][i];
				}
				else {
					
					v_j = 0;
				}
		
				if(i != 0) {
					
					v_i = res[j][i-1];
		
					if(j != 0) {
						
						v_ij = res[j-1][i-1];
					}
				}
				else {
					
					v_i = 0;
					
					v_ij = 0;
				}
				
				res[j][i] = v - v_ij + v_i + v_j;
			}
		}
		
		return res;
	}
	
	public static int getSum(int[][] integral_image, int x, int y, int w, int h) {
		
		
		int v0 = 0;
		
		int v1 = 0;
		
		int v2 = 0;
		
		int x_1 = x - 1;
		
		int y_1 = y - 1;
		
		int y_h = y + h;
		
		int x_w =  x_1 + w;
		
		if(x_1 >= 0 ) {
			
			if(y_1 >= 0) {
				
				v0 =  integral_image[y_1][x_1];
				
				v2 = integral_image[y_1][x_w];
			}
			v1 =  integral_image[y_h - 1][x_1];
		}

		int vb = integral_image[y_h - 1][x_w];
		
		return vb  -  v2 + v0 - v1 ; 
	
	}

}
