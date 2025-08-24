/**
 * 
 */
package paalgorithms;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * @author Andrii Dashkov
 *
 */
public class PaAlgoTransform {

	/**
	 * 
	 */
	public PaAlgoTransform() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Rotates image
	 * @param sourceImage
	 * @param angle - angle for rotation - 90, 180,270
	 * @param right - true for clockwise direction of rotation
	 * @return
	 */
	static public  BufferedImage getRotatedImage(BufferedImage sourceImage, float angle, boolean right) {
		
		if(sourceImage == null) {
			
			return null;
		}
		
		int wMax =   sourceImage.getWidth();
		
		int  hMax =		 sourceImage.getHeight();
		
		BufferedImage rotatedImage = null;
		
	
		 if (right) {
			 
			 if( angle == 90.0f){
	
				 rotatedImage = new BufferedImage( hMax, wMax, BufferedImage.TYPE_INT_RGB);
				 
				 for ( int i = 0; i < wMax; i++) {
					 
					 for ( int j = 0 ; j < hMax; j++) {
						 
						 rotatedImage.setRGB(hMax-1-j, i,  sourceImage.getRGB(i, j));
					 }
					}
			 }
			 
			 if(angle == 180.0f) {
				 
				 rotatedImage = new BufferedImage( wMax, hMax, BufferedImage.TYPE_INT_RGB);
				 
				 for ( int i = 0; i < wMax; i++) {
					 
					 for ( int j = 0 ; j < hMax; j++) {
						 
						 rotatedImage.setRGB(wMax-1-i, hMax-1-j,  sourceImage.getRGB(i, j));
					 }
				}
			 }
			 
			 if(angle == 270.0f){
				 
				 rotatedImage = new BufferedImage( hMax, wMax, BufferedImage.TYPE_INT_RGB);
				 
				 for ( int i = 0; i < wMax; i++) {
					 
					 for ( int j = 0 ; j < hMax; j++) {
						 
						 rotatedImage.setRGB(j, wMax-1-i,  sourceImage.getRGB(i, j));
					 }
				}
			 }
		 }
		 else {
			 
			 if(angle == 90.0f){
				 
				 rotatedImage = new BufferedImage( hMax, wMax, BufferedImage.TYPE_INT_RGB);
				 
				 for ( int i = 0; i < wMax; i++) {
					 
					 for ( int j = 0 ; j < hMax; j++) {
						 
						 rotatedImage.setRGB(j, wMax-1-i, sourceImage.getRGB(i, j));
					 }
				 }
			 }
			 if(angle == 180.0f){
				 
				 rotatedImage = new BufferedImage( wMax, hMax, BufferedImage.TYPE_INT_RGB);
				 
				 for ( int i = 0; i < wMax; i++) {
					 
					 for ( int j = 0 ; j < hMax; j++) {
						 
						 rotatedImage.setRGB(wMax-1-i, hMax-1-j, sourceImage.getRGB(i, j));
					 }
				 }
			 }
			 if(angle == 270.0f){
				 
				 rotatedImage = new BufferedImage( hMax, wMax, BufferedImage.TYPE_INT_RGB);
				 
				 for ( int i = 0; i < wMax; i++) {
					 
					 for ( int j = 0 ; j < hMax; j++) {
						 
						 rotatedImage.setRGB(hMax-1-j, i,  sourceImage.getRGB(i, j));
					 }
				}
			 }
		 }
		 
		 return rotatedImage;
		 
	}

	/**
	 * Image resize operation.
	 * 
	 * @param im - source image
	 * @param w - desired width 
	 * @param h - desired height
	 * @return the resized image
	 */
	static public  BufferedImage resizeImage(BufferedImage im, int w, int h) {
		
		return getScaledIm(im,w,h);
	}
	
	
	
/**
 * Returns scaled image; no control of proportion; paramters widht and height must be in right proportion already
 * @param srcImg - source image
 * @param width - new width
 * @param height - new height
 * @return
 */
	public static  Image getScaledImage (Image srcImg, int width, int height) {
		
		BufferedImage resizedImg; 
		
		try {
		
			resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
		catch(IllegalArgumentException e) {
			
			resizedImg = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
		}
		
		if(srcImg != null) {

			Graphics2D g2 = resizedImg.createGraphics();
			
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			g2.drawImage(srcImg, 0, 0, width, height, null);
			
			g2.dispose();
		}
		
		return resizedImg;		
	}

	
	/**
	 * Scales image to a new size. It uses bilinear mode of interpolation while resize operation
	 * @param image - image to scale
	 * @param width - new width
	 * @param height - new height
	 * @return
	 */
	static public BufferedImage getScaledIm(BufferedImage image, int width, int height) {
		
	    int imageWidth  = image.getWidth();
	    
	    int imageHeight = image.getHeight();

	    double scaleX = (double)width/imageWidth;
	    
	    double scaleY = (double)height/imageHeight;
	    
	    AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
	    
	    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

	    int type = image.getType();
	    
	    if(type == 0) {
	    	
	    	type = BufferedImage.TYPE_INT_RGB;
	    }
	    
	    return bilinearScaleOp.filter(image, new BufferedImage(width, height, type));
	}
	
	static public BufferedImage getScaledIm(BufferedImage image, float scale)
	{
		
		int  width = (int)(image.getWidth()*scale); 
		
		int height = (int)(image.getHeight()*scale);
		
		return getScaledIm(image, width, height);
		
	}
		
	
}
