/**
 * 
 */
package paalgorithms;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;


/**
 * @author Andrii Dashkov
 *
 */
public class PaAlgoConvert {

	public PaAlgoConvert() {

	}

	/**
	 * Performs binary operation for an image; operation can have different number of thresholds, so this operation is
	 * not pure binary transition. The pure binary transition can be set by  number of thresholds == 1
	 * @param sourceImage - source image to process
	 * @param baseColor - 0 - red, 1 - green, 2 - blue, 3 - all; - color which are used for operation 
	*  @param thresholds - values in the range 0 - 255; set only one threshold for pure binary trasition operation
	 * @return new image resulting binary image
	 */
	static public Image toBinary(BufferedImage sourceImage,  int baseColor, int[] thresholds) {
		
		
		try {
		
			int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
			
			BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
			
			int[] rgb = new int[3];
			
			//value to put into image for multiple thresholds
			int[] th_values = new int[thresholds.length];
			
			th_values[0] = 0;
			
			for (int t = 1; t < thresholds.length; ++t) {	
					
				th_values[t] =  thresholds[t - 1];
					
			}
			
			if(baseColor != 3) {
				
				for ( int i = 0; i < wMax; i++) {	
					
					for ( int  j = 0; j < hMax; j++) {
						
						int c = sourceImage.getRGB(i, j);
						
					    rgb[0] = (c & 0xff0000) >> 16; 
					
					    rgb[1] = (c & 0xff00) >> 8;
									
						rgb[2] = c  & 0xff;
						
						c = 255;
						
						for (int t = 0; t < thresholds.length; ++t) {
							
							if (rgb[baseColor] <= thresholds[t] ) {
								
								c = th_values[t];
								
								break;
							}
						}
						
						int newRGB = 0 | //alpha byte
								(c << 16 ) | //red byte
								(c << 8) | //green byte
								(c);//blue byte
						
						targetImage.setRGB(i,j, newRGB);
						
						
					}
				}
			
			}
			else {
				
				for ( int i = 0; i < wMax; i++) {	
					
					for ( int  j = 0; j < hMax; j++) {
						
						int c = sourceImage.getRGB(i, j);
						
					    rgb[0] = (c & 0xff0000) >> 16; 
					
					    rgb[1] = (c & 0xff00) >> 8;
									
						rgb[2] = c  & 0xff;
						
						//to grayscale
				        c = (int) (rgb[0] * 0.299) + (int) (rgb[1]  * 0.587) + (int) (rgb[2] * 0.114);
										
				        int c_ = 255;
						
						for (int t = 0; t < thresholds.length; ++t) {
							
							if (c <= thresholds[t] ) {
								
								c_ = th_values[t];
								
								break;
							}
						}
						
						int newRGB = 0 | //alpha byte
								(c_ << 16 ) | //red byte
								(c_ << 8) | //green byte
								(c_);//blue byte
						
						targetImage.setRGB(i,j, newRGB);
									
					}
				}
			
			}
						 		
			return   targetImage;
	
		}
		finally {	}
		
	}

	/**
	 * Performs the convertation of the image to the grayscale format; the image still has three channels (bands) with equal values.
	 * @param sourceImage - source image to process
	
	 */
	public static Image toGrayscale(BufferedImage sourceImage)
	{
		
		try {
		
			int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
			
			BufferedImage targetImage = new BufferedImage(wMax,hMax, BufferedImage.TYPE_INT_RGB);
			
			int[] rgb = new int[3];
		
			for ( int i = 0; i < wMax; i++) {	
				
				for ( int  j = 0; j < hMax; j++) {
						
						int c = sourceImage.getRGB(i, j);
							
					    rgb[0] = (c & 0xff0000) >> 16; 
					
					    rgb[1] = (c & 0xff00) >> 8;
									
						rgb[2] = c  & 0xff;
						
						//to grayscale
				        c = (int) (rgb[0] * 0.299) + (int) (rgb[1]  * 0.587) + (int) (rgb[2] * 0.114);
										
						int newRGB = 0 | //alpha byte
								(c << 16 ) | //red byte
								(c << 8) | //green byte
								(c);//blue byte
						
						targetImage.setRGB(i,j, newRGB);
									
				}
			}
							 		
			return   targetImage;
	
		}
		finally {	}
		
	}

	/**
	 * <p>Converts HSI data to RGB data</p>
	 * @param rgb - input RGB data [0-red,1-green,2-blue]
	 * @param hsi - output HSI data [0 - halftone, 1- saturation, 2-intensity]
	 * @param normalized - must be true if you want the result R,G,B components are in the normalized form (in the range 0 ..1)
	 * if normalized == false then RGB components are in the 0 ..255 range
	 */
	static public  void convertHSItoRGB(boolean normalized,double[] hsi,double[] rgb) {
		
		double H = hsi[0]*2.0*Math.PI; //convert from range 0 .. 1
		
		double S = hsi[1];
		
		double I = hsi[2];
		
		double Hgr = hsi[0]*360.0;
		
		if(Hgr >= 0.0 && Hgr < 120.0) {
			
			rgb[2] = I*(1-S); 
			
			rgb[0] = I*(1.0+(S*Math.cos(H))/(Math.cos(Math.PI/3.0-H)));
			
			
			rgb[1] = 3*I-(rgb[0]+rgb[2]);
			
		}
		else if(Hgr >= 120.0 && Hgr < 240.0) {
			
			H = H - 120.0*Math.PI/180.0;
			
			rgb[0] = I*(1-S); 
			
			rgb[1] = I*(1.0+(S*Math.cos(H))/(Math.cos(Math.PI/3.0-H)));
			
			rgb[2] = 3*I-(rgb[0]+rgb[1]);
			
			
		}else if(Hgr >= 240.0 && Hgr <= 360.0){
			
			H = H - 240.0*Math.PI/180.0;
			
			rgb[1] = I*(1-S); 
			
			rgb[2] = I*(1.0+(S*Math.cos(H))/(Math.cos(Math.PI/3.0-H)));
			
			rgb[0] = 3*I-(rgb[2]+rgb[1]);
		}	
		if(!normalized) {
			
			rgb[0] *= 255.0;
			
			rgb[1] *= 255.0;
			
			rgb[2] *= 255.0;
			
		}
	}

		
		/**
	 * <p>Converts RGB data to HSI data All results components are in the range 0 ...1</p>
	 * @param rgb - input RGB data [0-red,1-green,2-blue]
	 * @param hsi - output HSI data [0 - halftone, 1- saturation, 2-intensity]
	 * @param normalized - must be true if the R,G,B components are normalized (in the range (0 ..1))
	 */
	static public  void convertRGBtoHSI(double[] rgb,double[] hsi, boolean normalized) {
		
		double red =	rgb[0];
		
		double green =	rgb[1];
		
		double blue =	rgb[2];
		
		if(!normalized) {
			
			red /= 255.0;
			
			green /= 255.0;
			
			blue /=	255.0;
		}
		
		double R_G = red-green;
		
		double R_B = red-blue;
		
		double G_B = green-blue;
		
		double z = (2.0*Math.sqrt(R_G*R_G+R_B*G_B));
		
		double teta;
		
		if(z == 0.0) {
			
			teta = Math.acos(0);
		}
		else {
			
			teta = Math.acos((R_G+R_B)/(2.0*Math.sqrt(R_G*R_G+R_B*G_B)));
		}
		
		if(rgb[2] <= rgb[1]) {
			
			hsi[0] = teta;
		}
		else {
			
			hsi[0] = 2.0*Math.PI - teta;
		}
		
		hsi[0] /= 2.0*Math.PI;//to be in the range 0 ... 1
		
		double sum = red+green+blue;
		
		double min = red <= green ? ( red <= blue ? red : blue ) : ( green <= blue ? green : blue);
		
		hsi[1] = 1.0 - 3.0*min/(sum +0.000001);
		
		hsi[2] = sum/3.0;
		
	}

	/**
	 * Converts RGB int to HSI model 
	 * @param rgb - converts the Java rgb int to the HSI model
	 * @param hsi - output HSI array [0] - halftones [1] - saturation [2] - intensity
	 */
	static public  void convertRGBtoHSI(int rgb,double[] hsi) {
		
		double[] rgbArray = new double[3];
		
		rgbArray[0] = (rgb & 0xff0000) >> 16;//red
				
		rgbArray[1] = (rgb & 0xff00) >> 8;//green
		
		rgbArray[2] = rgb  & 0xff;//blue
		
		convertRGBtoHSI(rgbArray, hsi, false);
	}
	
	
	
	static public void savePixelsToFile(BufferedImage im,  Rectangle rec, boolean saveHSI, boolean saveRGB, PrintWriter out) {
		
		
		int x_center = rec.x + rec.width/2;
		
		int y_center = rec.y + rec.height/2;
		
		int a =  rec.width/2;
		
		int b =  rec.height/2;

		for(int x = rec.x; x < rec.x + rec.width; x++ ) {
			
			for(int y = rec.y; y < rec.y + rec.height; y++ ) {
				
				int rx = Math.abs(x - x_center);
				
				int ry = Math.abs(y - y_center);
				
				double rc = Math.sqrt(rx*rx + ry*ry);
				
				double fi = Math.atan(y/(double)x);
				
				double re = a*b/ Math.sqrt(a*a*Math.sin(fi)*Math.sin(fi) + b*b*Math.cos(fi)*Math.cos(fi));
				
				if(rc > re) { continue; }
				
				Color c = new Color(im.getRGB(x, y));
				
				int red = c.getRed();
				
				int blue = c.getBlue();
				
				int green = c.getGreen();
				
				double[] rgb = {(double)red, (double)green, (double)blue};
				

				if(saveRGB) {
					
					out.print("" + rgb[0] + " " + rgb[1] + " " + rgb[2]);
					
				}
				
				if(saveHSI) {
					
					double[] hsi = new double[3];
					
					PaAlgoConvert.convertRGBtoHSI(rgb, hsi, false);
					
					out.print(" " + hsi[0] + " " + hsi[1] + " " + hsi[2]);
				}
					
				out.println();
			}
		}
	}
}
