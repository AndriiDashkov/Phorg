
package PaEditor;

import static PaGlobal.PaLog.writeLog;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import PaAlgorithms.PaAlgoEye;
import PaGlobal.PaUtils;

/**
 * @author avd
 *
 */
public class PaRedEInstrument extends PaZoomRectInstrument {
	
	/**
	 * custom cursor 
	 */
	Cursor m_cursor;
	
	/**
	 * @param parent - the component where the instrument draws 
	 * @param but - the instruments button (it can be useful for link instrument - it's button )
	 * @param col - color for the instrument
	 */
	public PaRedEInstrument(PaInstrumentsPanel parent, PaComplexButton but) {
		
		super(parent, but);
		
		createCustomCursor();
		
	}
	
	
	@Override
	public Image getResultView(MouseEvent e,float xScale, float yScale, BufferedImage sourceImage) {
				
		if(m_startPoint != null) {
			
			try {
				
				isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView()
			
				Point sPoint= new Point(m_rect.x, m_rect.y);
				
				SwingUtilities.convertPointToScreen(sPoint, m_parent);
				
				SwingUtilities.convertPointFromScreen(sPoint, m_parent.m_scrollPane.getMainLabel());
				
				Rectangle rec = new Rectangle(sPoint,new Dimension(m_rect.width,m_rect.height));
				
				rec = m_parent.convertFromInstrumentRectToImageRect(rec, true);
				
				BufferedImage im = (BufferedImage) m_parent.getCurrentImage();
				
				if(im == null) {
					
					 writeLog("Can't find current image to use red eye instrument" , null, true, false, true);
					 
					 return null;
				}
				
				//BufferedImage targetImage = (BufferedImage) copyImage(im,m_parent);
				BufferedImage targetImage = PaUtils.deepCopy(im);
				
				PaAlgoEye al = new PaAlgoEye();
				
				al.clearRedEyeArea(targetImage,rec);
				
				reset();
				
				return targetImage;
			}
			finally {
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		
		//operates with image in the work area, that is why return null
		return null;
	}
	
	/**
	 * <p>Can be overloaded in the children in order to reset some parameters after instrument using is finished
	 * After finish of using the size of rect should be set to null</p>
	 */
	@Override
	public void clearOperation() {
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		m_rect.width = 0;
		
		m_rect.height = 0;
		
		m_parent.resetInstrument();
	};
	
	/**
	 * creates custom instrument cursor 
	 */
	private void createCustomCursor() {
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		Image image = toolkit.getImage(PaUtils.get().getIconsPath() + "paredeyecursor.png");
		
		m_cursor = toolkit.createCustomCursor(image , new Point(6,3), "img");
	  
	}
	
	public void setCursor() {
		
		createCustomCursor();
		
		m_parent.setCursor(m_cursor);
		
	};
	
	public Cursor getCursor() {

		return m_cursor;
	};
	
	
	/**
	 * This function is called from the parent component - it paints on the parent graphic context
	 */
	@Override
	public void drawShape(Graphics2D g2) {
		
		//no sence to draw zero rect
		if(m_rect.width == 0 || m_rect.height == 0 ) return;
		
		g2.setColor(getColor());
				
		g2.draw( new Ellipse2D.Double(m_rect.x, m_rect.y, m_rect.width, m_rect.height));
		
	}
	

	@Override
	public boolean filterMouseEvent(MouseEvent e) {

		
		Point currentPoint = new Point(e.getLocationOnScreen());
		
		Rectangle controlRec = m_parent.getViewVisibleRectOnScreen();
	
		SwingUtilities.convertPointFromScreen(currentPoint, m_parent);

		if (e.getID() ==  MouseEvent.BUTTON2  ) {

		}
		
		
		if (e.getID() ==  MouseEvent.MOUSE_DRAGGED  ) {
			
			if(m_startPoint == null) return false; //this means that the mouse has been pressed outside
			//and then drag on the image
			
			int deltaX = 0;
			
			int deltaY = 0; 

			if (  m_xPrev !=  N0_PREV_MOUSE_POSITION)  {
				
				deltaX = - m_xPrev  +  currentPoint.x;
				
				deltaY = - m_yPrev +  currentPoint.y;
				
			}
			
			m_xPrev = currentPoint.x;
			
			m_yPrev  = currentPoint.y;

			m_currentSize.height += deltaY;
			
			m_currentSize.width += deltaX;	
			
			m_currentPoint.x += deltaX;
			
			m_currentPoint.y += deltaY;
			
			m_rect.x = m_startPoint.x - m_currentSize.width/2;
			
			m_rect.y = m_startPoint.y - m_currentSize.height/2;
			
			Point bottomRightPoint = new Point(m_startPoint);
			
			bottomRightPoint.x = m_startPoint.x + m_currentSize.width/2;//m_currentSize.width; 
			
			bottomRightPoint.y = m_startPoint.y - m_currentSize.height/2;//m_currentSize.height;
			
			if(!controlRec.contains(bottomRightPoint)) {
		
				if((controlRec.width + controlRec.x) < (bottomRightPoint.x) ) {
					
					m_currentSize.width -= bottomRightPoint.x - controlRec.width - controlRec.x;
				}
				if((controlRec.height + controlRec.y) < (bottomRightPoint.y) ) {
					
					m_currentSize.height -= bottomRightPoint.y - controlRec.height - controlRec.y;
				}
			}
			
			m_rect.setSize(m_currentSize);
			
			return true;	
		}
		
		if (e.getID() ==  MouseEvent.MOUSE_EXITED  ) {
		
			m_xPrev =  N0_PREV_MOUSE_POSITION;
			
			m_yPrev =  N0_PREV_MOUSE_POSITION;
			
			return true;
		}
		
		if (e.getID() ==  MouseEvent.MOUSE_MOVED  ) {
			
			m_startPoint = e.getLocationOnScreen();
			
			if(controlRec.contains(m_startPoint)) {
				
				SwingUtilities.convertPointFromScreen(m_startPoint, m_parent);
	
				m_currentSize.height = 0;
				
				m_currentSize.width = 0;
				
			} else {//start point is not in the image area
				
				m_startPoint = null;
			}
		}		
		return false;
	}
}
