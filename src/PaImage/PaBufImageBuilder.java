
package PaImage;



import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * @author avd
 *
 */
public class PaBufImageBuilder  {

	private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
 
	public BufferedImage bufferImage(Image image) {
		
			return getBufferImage(image, DEFAULT_IMAGE_TYPE);
	}

	public BufferedImage getBufferImage(Image image, int type) {
		
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		
		Graphics2D g = bufferedImage.createGraphics();
		
		g.drawImage(image, null, null);
		
		return bufferedImage;
	}
 
}