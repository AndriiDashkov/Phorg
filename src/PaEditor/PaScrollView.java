
package paeditor;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import paalgorithms.PaAlgoTransform;
import paglobal.PaUtils;


/**
 * <p>Scroll pane class to manage images visually in the editor dialog.
 * The schema is following:
 * m_baseImage has the main fullsize source image which should be shown; this class never changes this image
 * m_viewImage - the image which a user sees on the screen. This image can be dynamically changed for zoom operations
 * m_viewImage can show only part of source base image. The link with base image is made using m_baseWindow</p>
 * m_baseWindow - the part of base image or full base image; it determines the area which is in the m_viewImage;
 * base Window holds only size and point of top left relative to the base image. The image which is under base window and
 * in the view image is always the same, but the size of view image can be different
 * @author Andrii Dashkov
 * ������� ���� m_baseWindow ��� ��������� m_viewImage �� �������� �����������; m_viewImage ����� ���� ������ ���
 * viewport; ������ m_baseWindow ��� ����� ��������������� � �������� m_viewImage(�� �� ��������)
 */
public class PaScrollView extends JScrollPane {

	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>Base image; it's never changed here </p>
	 */
	private BufferedImage m_baseImage;
	/**
	 * <p>base window - the reflection of image view on the real image;
	 * it has the point of its topleft corner according to the base image (relative position of base window
	 * in the base image coordinates)</p>
	 */
	
	private Rectangle m_baseWindow; //base window - the reflection of image view on the real image
	/**
	 * <p>image view - user sees this image on the screen; it can be increased/decreased</p>
	 */
	private BufferedImage m_viewImage;
	/**
	 * <p>The main holder for the m_viewImage</p>
	 */
	private JLabel m_imageLabel;

	boolean m_baseWindowFullSize = true;
	
	private final int xStep = 50;
	private final int yStep = 50;
	

	private double  m_scaleCoeff = 1.9;
	
	private final int m_sizeViewMax = 2500;
	
	/**
	 * This is the minimal size of base window - no sense to do it smaller
	 */
	private final int m_minBaseWindowSize = 10;
	
	/**
	 * This is the minimal size of view image - no sense to do it smaller
	 */
	private final int m_minViewSize = 50;

	
	public enum Direction{
		LEFT,
		RIGHT,
		TOP,
		BOTTOM
	};
	
	/**
	 * 
	 */
	public PaScrollView()
	{
		super();

		JPanel panel = new JPanel();
		
		m_imageLabel = new JLabel();
		
		panel.add(m_imageLabel);
		
		this.setViewportView(panel);
		
		m_scaleCoeff = 1.0 + PaUtils.get().getSettings().getZoomStep();

	}
	
	/**
	 * <p>Initiation function to start working with an image</p>
	 * @param base - base image to draw
	 */
	public void init(BufferedImage base) {
		
		m_baseImage = base;
		
		//initial base window
		m_baseWindow = new Rectangle(0,0,base.getWidth(),base.getHeight());
		
		//scaling into viewport size
		double scale = getViewport().getWidth()/m_baseWindow.width;
		
		if(base.getWidth()/(float)base.getHeight() < 1.0) {
			
			scale = getViewport().getHeight()/m_baseWindow.height;
			
		}
		
		if(scale == 0) scale = 0.5;
		
		m_viewImage =  (BufferedImage) PaAlgoTransform.getScaledImage (m_baseImage, 
				(int)(m_baseWindow.width*scale), (int)(m_baseWindow.height*scale));
		
		 m_imageLabel.setIcon(new ImageIcon(m_viewImage));
	}
	

	//refresh the view image without changing the geometry of base Window
	public void refresh(BufferedImage base) 
	{
		 m_baseImage = base;
		 
		 BufferedImage m_viewNewImage = ((BufferedImage) m_baseImage).getSubimage(m_baseWindow.x,
					m_baseWindow.y,m_baseWindow.width,m_baseWindow.height);
		 
		if(m_viewImage.getWidth() !=  m_viewNewImage.getWidth()) {
			
			
			m_viewImage =  (BufferedImage) PaAlgoTransform.getScaledImage (m_viewNewImage,
					m_viewImage.getWidth(),m_viewImage.getHeight());
			
		}
		else {
			m_viewImage = m_viewNewImage;
		}
		
		
		 m_imageLabel.setIcon(new ImageIcon(m_viewImage));
		
	}
	
	
	public void resizeViewImage(Direction d) {
		
		if(m_baseWindowFullSize) return;
		
		double scale = m_baseWindow.width/m_viewImage.getWidth();
		
		switch(d) {
		
			case RIGHT: {
				
				m_baseWindow.width = m_baseWindow.width + xStep;
				
				break;
			}
			case LEFT: {
				
				m_baseWindow.width = m_baseWindow.width + xStep;
				
				m_baseWindow.x = m_baseWindow.x + xStep; 
				
				break;
			}
			case BOTTOM: {
				
				m_baseWindow.height = m_baseWindow.height + yStep; 
				
				break;
			}
			case TOP: {
				m_baseWindow.height = m_baseWindow.height + yStep;
				
				m_baseWindow.y = m_baseWindow.y - yStep; 
				
				break;
			}
	
		}
		//control for case if the width and height of base window > base image size
		double yD = m_baseWindow.height/m_baseImage.getHeight();
		
		double xD = m_baseWindow.width/m_baseImage.getWidth();
		
		if((m_baseWindow.height+m_baseWindow.y) > m_baseImage.getHeight()) {
			
			m_baseWindow.height = m_baseImage.getHeight() - m_baseWindow.y;
			
		}
		if((m_baseWindow.width+m_baseWindow.x) > m_baseImage.getWidth()) {
			
			m_baseWindow.width = m_baseImage.getWidth() - m_baseWindow.x;
			
		}
		if((yD > 0.85) || (xD > 0.85) ) {
			
			m_baseWindow.height = m_baseImage.getHeight();
			
			m_baseWindow.width = m_baseImage.getWidth();
			
			m_baseWindow.x = 0;
			
			m_baseWindow.y = 0;
			
			m_baseWindowFullSize = true;
		}
		
		BufferedImage im = ((BufferedImage) m_baseImage).getSubimage(m_baseWindow.x,
				m_baseWindow.y,m_baseWindow.width,m_baseWindow.height);
		
		m_viewImage =  (BufferedImage) PaAlgoTransform.getScaledImage (im, (int)(m_baseWindow.width*scale),  
				(int)(m_baseWindow.height*scale));
	}
	
	private Rectangle changeSizeOfRect(Rectangle rec, int delta) {
		
		return new Rectangle(
				rec.x - delta/2,
				rec.y - delta/2,
				rec.width + delta/2,
				rec.height + delta/2
				);
	}
	/**
	 * Sets the visible in viewport image according to the area rec on the base image
	 * So it reflects tthe rectangle rec on real image into viewport of the base image label
	 * @param rec - rectangle on the base image
	 */
	public void setSubImage(Rectangle rec){
		
		//makes the rec a bit wider
		Rectangle recNew = changeSizeOfRect(rec, xStep);
		
		//proportions control -   the rec should be in a viewport proportion
		if(rec.width < rec.height) { 
			
			recNew.width = (int)(((double)recNew.height*(double)getViewport().getWidth())/(double)(getViewport().getHeight()));
		}
		else {
			
			recNew.height = (int)(((double)recNew.width*(double)getViewport().getHeight())/(double)(getViewport().getWidth()));
		}
		
		//control - the new value can be  > real sizes of base image
		//here we have a new variant of m_baseWindow
		m_baseWindow = new Rectangle(controlOfBaseWindow(recNew));
		
		try {
			
			//here we have the image which is part of base image, we should scale it for viewport
			m_viewImage = ((BufferedImage) m_baseImage).getSubimage(m_baseWindow.x,
					m_baseWindow.y,m_baseWindow.width,m_baseWindow.height);
			
			
			//gets the new scale - base window should be scaled to viewport + extra margin
			double newScale = ((double)getViewport().getHeight())/((double)m_baseWindow.height);
			
			//result new image
			m_viewImage =  PaAlgoTransform.getScaledIm (m_viewImage, (int)(m_baseWindow.width*newScale),
					(int)(m_baseWindow.height*newScale));
			
				
			m_imageLabel.setIcon(new ImageIcon(m_viewImage));
		
		}
		catch(RasterFormatException ex ) {
			 
			writeLog(getMessagesStrs("subImageOutRastesMessage") + NEXT_ROW, ex, true, true, false);
			 
		}

	}
	/**
	 * 
	 * @param rec - rectangle to control
	 * @return the rectangle which is always <= m_baseImage
	 */
	private Rectangle controlOfBaseWindow(Rectangle rec) {
		
		if(rec.x < 0 ) rec.x = 0;
		
		if(rec.y < 0) rec.y = 0;
		
		if((rec.width + rec.x) > m_baseImage.getWidth()) {
			
			rec.width =  (int)(m_baseImage.getWidth() - rec.x);
		}
		
		if((rec.height + rec.y) > m_baseImage.getHeight()) {
			
			rec.height =  (int)(m_baseImage.getHeight() - rec.y);
		}
		return rec;
	}

	/**
	 * ���� ����� ������ ��� base Window ��� ������ ��������� rec - ���������� ��� ����� ������
	 * ��� view Image; �������� ������� ����� ������ ������ viewImage � baseWindow
	 * @param rec - rectangle 'inside' m_viewImage, relative to m_viewImage (the point of rec can be
	 * negative in the case if new rec is > then m_viewImage)
	 * @return new rectangle (position and size) for base window
	 */
	public Rectangle convertViewRectToBaseWindow(Rectangle rec, boolean proportionControl, boolean baseWindowControl){
		
		//normalization of input rect - it should have the same proportions as viewport
		if(proportionControl){
			
			//change selected area to align with the viewport ratio
			if(rec.width < rec.height) { 
				
				rec.width = (int)(((double)rec.height*(double)getViewport().getWidth())/(double)(getViewport().getHeight()));
			}
			else {
				rec.height = (int)(((double)rec.width*(double)getViewport().getHeight())/(double)(getViewport().getWidth()));
			}
			
		}

		//gets the relative scale between m_viewImage and m_baseWindow
		double scale = ((double)m_baseWindow.width)/((double)m_viewImage.getWidth());
		

		Rectangle recNew = new Rectangle();
		
		//gets the point inside m_baseImage
		recNew.x = (int)(rec.x*scale + m_baseWindow.x);
		
		recNew.y = (int)(rec.y*scale  + m_baseWindow.y);
		
		recNew.width = (int)((rec.width)*scale);
		
		recNew.height = (int)((rec.height)*scale);
		
		//control - the new value can be  > real sizes of base image
		//here we have a new variant of m_baseWindow
		if (baseWindowControl) {
			
			recNew = controlOfBaseWindow(recNew);
		}
		
		return recNew;
		
	}
	
	/**
	 * Returns the appropriative size of rectangle in current view image; converts
	 * from original image coordinates to visible on the screen coordinates and sizes
	 * @param rec - rectangle which coordinates is in original image dimensions
	 * @return the rectangle with coordinate which are in the visible on the screen dimension
	 */
	public Rectangle convertBaseWindowRecToViewRec(Rectangle rec){

		double scale = ((double)m_viewImage.getWidth())/((double)m_baseWindow.width);
		
		Rectangle recNew = new Rectangle();
		
		recNew.x = (int)(rec.x*scale + m_baseWindow.x);
		
		recNew.y = (int)(rec.y*scale  + m_baseWindow.y);
		
		recNew.width = (int)((rec.width)*scale);
		
		recNew.height = (int)((rec.height)*scale);
		
		return recNew;
		
	}
	
	
	public Rectangle convertRectFromBaseImageToViewImage(Rectangle rec){

		double scale = ((double)m_viewImage.getWidth())/((double)m_baseWindow.width);
		
		Rectangle recNew = new Rectangle();
		
		recNew.x = (int)(rec.x*scale - m_baseWindow.x);
		
		recNew.y = (int)(rec.y*scale  - m_baseWindow.y);
		
		recNew.width = (int)((rec.width)*scale);
		
		recNew.height = (int)((rec.height)*scale);
		
		return recNew;
		
	}
	
	public Rectangle convertViewRectToBaseWindow(Rectangle rec){
		
		return convertViewRectToBaseWindow(rec, true, true);
	}
	
	/**
	 * <p>Sets the new view image for new base window and new scale between them</p>
	 * @param newBaseWindow
	 * @param newScale
	 */
	public void setSubViewImage(Rectangle newBaseWindow, double newScale) {
	 
		 setSubViewImage(newBaseWindow,(int)(m_baseWindow.width*newScale),
				 (int)(m_baseWindow.height*newScale));
	}
	
	public void setSubViewImage(Rectangle newBaseWindow, int newW, int newH) 
	{
		
		m_baseWindow = newBaseWindow;
		
		setSubViewImage(newW,newH);
	}
	
	public void setSubViewImage(int newW, int newH) {

		m_viewImage = ((BufferedImage) m_baseImage).getSubimage(m_baseWindow.x,
				m_baseWindow.y,m_baseWindow.width,m_baseWindow.height);
		
		//result new image
		m_viewImage =  (BufferedImage) PaAlgoTransform.getScaledImage (m_viewImage,
				newW,newH);
		
		 m_imageLabel.setIcon(new ImageIcon(m_viewImage));
	}
	

	/**
	 * <p>Increases the image, this means - increasing of size of view Image. In this proccess the upper
	 * limit can be reached and the out of memory exception is possible - that is why this function
	 * starts to use subimages of base image in order to be in the upper limit, but have possibility to 
	 * continue increase operation</p>
	 * 
	 * @param scaleCoeff - the scaling coefficient, the operation is applied to the current state of m_viewImage
	 */
	
	public void increaseViewImage(double scaleCoeff) {
		
		//increase visible region
		int wNew = (int)(m_viewImage.getWidth()*scaleCoeff);
		
		int hNew = (int)(m_viewImage.getHeight()*scaleCoeff);
		
		//save the old scale between visible region and base window
		double oldScale = (double)(m_viewImage.getWidth())/((double)m_baseWindow.width);
		
		//max size control =- java can't increase the image endlessly - to continue the process we must work with subimage
		if(wNew > m_sizeViewMax || hNew > m_sizeViewMax) {
			
			//needs a new subimagebecause we can't increase the size of 
			//view image while zoom - the limit of max size of the  viewImage has been reached
			Rectangle rec = new Rectangle();
			
			int dS= m_sizeViewMax - m_viewImage.getWidth();
			
			//now we have new sizes of our visible image
			if(wNew > m_sizeViewMax) {
				
				dS= m_sizeViewMax - m_viewImage.getWidth();
				
				wNew = m_sizeViewMax;
				
				hNew = wNew* m_viewImage.getHeight()/m_viewImage.getWidth();//
			}
			else {
				
				dS= m_sizeViewMax - m_viewImage.getHeight();
				
				hNew = m_sizeViewMax;
				
				wNew = hNew* m_viewImage.getWidth()/m_viewImage.getHeight();//
			}
			
			//the we restore the size and position of base window - after that the visual scale of image is the same as before,
			//because we increase view image size, but we also increased the sie of base window in the same way
			rec.x = (int)(m_baseWindow.x + dS/(oldScale*2.0));
			
			rec.width = (int)(wNew/oldScale);
			
			rec.height =  (int)(hNew/oldScale);
			
			rec.y = (int)(m_baseWindow.y + (rec.height - m_baseWindow.height)/2.0);
			
			//stops the increasing - base window too small, no sence to do it further
			if(!controlMinBaseWindow(rec)) return;
			
			m_baseWindow = controlOfBaseWindow(rec);
			
			//here we increase the size of visible image without changing the size of base window - this gives to us the effect of increasing
			wNew = (int)(wNew*scaleCoeff);
			
			hNew = (int)(hNew*scaleCoeff);
						
			setSubViewImage(wNew,hNew);
		}
		else {
			
			setSubViewImage(wNew,hNew);
		}
		
	}
	
	/**
	 * <p>Increases the image by one step with scaling coefficient == predefine scaling step value<p>
	 * 
	 */
	public void increaseViewImage() {
		
		
		increaseViewImage(m_scaleCoeff);
	
	}
	
	/**
	 * <p>Decreasing of image operation</p>
	 */
	public void decreaseViewImage(double scaleCoeff) 
	{
		
		double oldScale = (double)(m_viewImage.getWidth())/((double)m_baseWindow.width);
		
		
		
		int wNew = (int)(m_viewImage.getWidth()*(2.0 - scaleCoeff));
		
		int hNew = (int)(m_viewImage.getHeight()*(2.0 - scaleCoeff));
		
		double coeff = (double)(m_baseImage.getWidth())/((double)m_baseImage.getHeight());
		
		double coeffImage = (double)m_viewImage.getWidth()/m_viewImage.getHeight();
		
		System.out.println("decreaseViewImage:  oldScale = " + Double.toString(oldScale) +
				"   wNew = " + Integer.toString( wNew) + "   hNew = " + Integer.toString( hNew));
		
		System.out.println("decreaseViewImage:  coeff = " + Double.toString(coeff) +
				"   coeffImage = " + Double.toString(coeffImage));
		
		//no sence to do decreasing further - too small
		if(wNew < m_minViewSize || hNew < m_minViewSize) return;
		
		//the image View is subimage and it is inside viewport size - in this case the user will see
		//the subimage, not entire image - this is bad; 
		//tries to increase the view subimage until it is fit viewport (>) or it is a full image 
		int dS = 0;
		
		while(isSubImage() && isLessViewportSize(wNew,hNew)){
			
			Rectangle rec = new Rectangle();
			
			wNew = m_viewImage.getWidth();
			
			hNew = m_viewImage.getHeight();
		
			dS += 100;
			
			wNew += dS;
			
			hNew = (int)((double)wNew/coeffImage);
			
			rec.x = (int)(m_baseWindow.x - dS/(oldScale*2.0));
			
			rec.width = (int)(wNew/oldScale);
			
			rec.height =  (int)(hNew/oldScale);
			
			rec.y = (int)(m_baseWindow.y - (rec.height - m_baseWindow.height)/2.0);
			
			m_baseWindow = controlOfBaseWindow(rec);
			
			//one of the basewindow edges has reached the edge of original (base) images - the the baseWindow must be the same size as the base image
			if(m_baseWindow.width == m_baseImage.getWidth() || m_baseWindow.height == m_baseImage.getHeight()) {
				
				m_baseWindow.width = m_baseImage.getWidth();
				
				m_baseWindow.height = m_baseImage.getHeight();
				
				m_baseWindow.x = 0;  m_baseWindow.y = 0; 
				
				 if(wNew > hNew) {
					 
					 hNew = (int)(wNew/coeff); 
					 
				 }
				 else  if(wNew < hNew)  {
					 
					 wNew = (int)(hNew*coeff);
				}
			}
			
		   wNew = (int)(wNew*(2.0 - scaleCoeff));
		   
		   hNew = (int)(hNew *(2.0 - scaleCoeff));
		}
		//if there is no subImage any more then we restore the proportion of original image, because in subImage state
		// the visible image has proportions of viewport

		setSubViewImage(wNew,hNew);
		
	}
	
	
	/**
	 * <p>Decreasing of image operation</p>
	 */
	public void decreaseViewImage() {
		
		decreaseViewImage(m_scaleCoeff);
	}
	
	/**
	 * <p>Resizes view image according to the coefficient scaleCoeff </p>
	 */
	public void resizeViewImageToScale(double scaleCoeff) 
	{
		
		double currentScale = (double)(m_viewImage.getWidth())/((double)m_baseWindow.width);
		
		if(currentScale > scaleCoeff ) {
			
			scaleCoeff = 1.0 - (currentScale - scaleCoeff);
			
			decreaseViewImage(scaleCoeff);
			
		}
		else {
			
			scaleCoeff = 1.0 + (scaleCoeff - currentScale);
			
			increaseViewImage(scaleCoeff);
		}
		
	}
	
	
	
	public JLabel getMainLabel() { return m_imageLabel; }

	 
	/**
	 * <p>Class listens the scrollbars. In the case when m_viewImage has the only part of base image, then
	 * while scroll operation we must have a possibility to spread subimage in the m_viewImage</p>
	 * @author Andrii Dashkov
	 *
	 */
	@SuppressWarnings("unused")
	private class ScrollBarsListener implements AdjustmentListener {
	  

		@Override
		public void adjustmentValueChanged(AdjustmentEvent evt) {
			
			Adjustable source = evt.getAdjustable();
			
		    if (evt.getValueIsAdjusting()) {
		    	
		      return;
		      
		    }
		    
		    int orient = source.getOrientation();

		    int type = evt.getAdjustmentType();
		 
		    switch (type) {
		    
			    case AdjustmentEvent.UNIT_INCREMENT:
			    	
			    	if(orient == Adjustable.HORIZONTAL) {
			    		
			    		resizeViewImage(Direction.RIGHT);
				    }
				    else {
				    	
				    	resizeViewImage(Direction.TOP);
				    }
			
			    	break;
			    	
			    case AdjustmentEvent.UNIT_DECREMENT:
			    	
			    	if(orient == Adjustable.HORIZONTAL) {
			    		
			    		resizeViewImage(Direction.LEFT);
				    }
				    else {
				    	
				    	resizeViewImage(Direction.BOTTOM);
				    }
	
		    		break;
		    		
			    case AdjustmentEvent.BLOCK_INCREMENT:
			    	
			    	if(orient == Adjustable.HORIZONTAL) {
			    		
			    		resizeViewImage(Direction.RIGHT);
				    }
				    else {
				    	
				    	resizeViewImage(Direction.TOP);
				    }
		
			    	break;
			    	
			    case AdjustmentEvent.BLOCK_DECREMENT:
			    	
			    	if(orient == Adjustable.HORIZONTAL) {
			    		
			    		resizeViewImage(Direction.LEFT);
				    }
				    else {
				    	
				    	resizeViewImage(Direction.BOTTOM);
				    }
		
			    	break;
			      
			    case AdjustmentEvent.TRACK:
			    	
			    	if(orient == Adjustable.HORIZONTAL) {
			    		
			    		resizeViewImage(Direction.LEFT);
				    }
				    else {
				    	
				    	resizeViewImage(Direction.BOTTOM);
				    }
		
			    	break;
		    }	
		}
	}
	
	private boolean controlMinBaseWindow(Rectangle base) {
		
		if(base.width < m_minBaseWindowSize) return false;
		
		if(base.height < m_minBaseWindowSize) return false;
		
		return true;
		
	}

	/**
	 * @return true if the m_viewImage has the part of base image (subimage) not the full
	 */
	private boolean isSubImage() {
		
		if(m_baseWindow.width < m_baseImage.getWidth()
				|| m_baseWindow.height < m_baseImage.getHeight()) return true;
		
		return false;
	}
	
	/**
	 * 
	 * @return the point inside view image
	 */
	public Point getStartPointInsideViewport() {
		
		Point p = new Point(getLocation());
		
		if((m_viewImage.getWidth() > getViewport().getWidth()) &&
				m_viewImage.getHeight() > getViewport().getHeight()) return p;
		
		if(m_viewImage.getWidth() < getViewport().getWidth()) {
			
			p.x = m_imageLabel.getLocation().x;
		}
		
		if(m_viewImage.getHeight() < getViewport().getHeight()) {
			
			p.y = m_imageLabel.getLocation().y;
		}
		return p;
		
	}
	
	/**
	 * 
	 * @return true if the size of m_viewImage is very close to the size of viewport image
	 */
	private boolean isLessViewportSize(int w, int h) {
		
		if(getViewport().getWidth() > w || getViewport().getHeight() > h) return true;
		
		return false;
		
	}
	
	/**
	 * 
	 * @return the current view image 
	 */
	public BufferedImage getViewImage() {
		
		return m_viewImage;
	}

	/**
	 * 
	 * @return size of current view image
	 */
	public Dimension getViewSize() {
		
		if(m_viewImage == null) return null;
		
		return new Dimension(m_viewImage.getWidth(),m_viewImage.getHeight());
	}
	
	
	public Rectangle getCurrentBaseWindow() 
	{
		
		
		return m_baseWindow;
	}
	
	 /**
	  * Visible rectangle on the screen can be restricted by image itself and viewport boundaries if the image view is
	  * less then viewport
	  * @return the visible image rectangle on the screen 
	  */
	public Rectangle getViewVisibleRectOnScreen() {
		
		Point pLabel = m_imageLabel.getLocationOnScreen();
		
		Dimension sizeLabel = m_imageLabel.getSize();
		
		Point pBottomLabel = new Point(pLabel); //this is bottom right point
		
		pBottomLabel.x += sizeLabel.width; 
		
		pBottomLabel.y += sizeLabel.height;
		
		Point pViewport = getViewport().getLocationOnScreen();
		
		Dimension sizeViewport = getViewport().getSize();
		
		Point pBottomViewport = new Point(pViewport);  //this is bottom right point
		
		pBottomViewport.x += sizeViewport.width; 
		
		pBottomViewport.y += sizeViewport.height;
		
		Point resultPoint = new Point(pLabel);
		
		if(pLabel.y < pViewport.y) { resultPoint.y = pViewport.y; }
		
		if(pLabel.x < pViewport.x) { resultPoint.x = pViewport.x; }
		
		int wLabel = pBottomLabel.x - resultPoint.x;
		
		int hLabel = pBottomLabel.y - resultPoint.y;
		
		int wViewport = pBottomViewport.x - resultPoint.x;
		
		int hViewport = pBottomViewport.y - resultPoint.y;
		
		Dimension s = new Dimension(wLabel,hLabel);
		
		if(wLabel > wViewport) { s.width = wViewport; }
		
		if(hLabel > hViewport) { s.height = hViewport; }
		
		return new Rectangle(resultPoint, s);
		
	}
	
	
	public double getCurrentScale() 
	{ 
		return (double)(m_viewImage.getWidth())/((double)m_baseWindow.width); 
	}
}
