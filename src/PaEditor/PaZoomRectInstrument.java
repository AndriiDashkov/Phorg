
package paeditor;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getGuiStrs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import paenums.PaInstrumentTypeEnum;
import paglobal.PaUtils;



public class PaZoomRectInstrument extends PaInstrument {
	 
	protected final int N0_PREV_MOUSE_POSITION =  -10000;

	//values for previous mouse position capture
	protected int m_xPrev =  N0_PREV_MOUSE_POSITION;
	
	protected int m_yPrev =  N0_PREV_MOUSE_POSITION;
	
	//parent component where the image is on. We need it for cursor manipulation
	protected PaInstrumentsPanel m_parent;

	protected Rectangle m_rect;
	
	protected Point m_currentPoint = new Point(10,10);
	
	protected Dimension  m_currentSize = new Dimension(50,50);
 
	//start point of an instrument
	protected Point m_startPoint = null;
	
	Dimension m_viewSize;
	
	/**
	 * custom cursor 
	 */
	Cursor m_cursor;
	

	 
	public PaZoomRectInstrument(PaInstrumentsPanel parent, PaComplexButton but) {
		
		super(but,PaUtils.get().getSettings().getInstrumentsColor(), getGuiStrs("zoomRectInstrName"));
		
		m_dragMouseRepaintEnabled = false; //no reaction while dragging mouse
				
		m_parent = parent;
		
		m_currentPoint.x = 0;
		
		m_currentPoint.y = 0;
		  
		m_currentSize.width = 0;	
		
		m_currentSize.height = 0;
						
		m_rect = new Rectangle(m_currentPoint, m_currentSize);
		
		m_viewSize = m_parent.getViewVisibleSize();
		
		 
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
			
			m_rect.setLocation(m_startPoint);
			
			Point bottomRightPoint = new Point(m_startPoint);
			
			bottomRightPoint.x += m_currentSize.width; 
			
			bottomRightPoint.y += m_currentSize.height;
			
			SwingUtilities.convertPointToScreen(bottomRightPoint, m_parent);
			
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

	/**
	 * <p>Can be overloaded in the children in order to reset some parameters after instrument using is finished
	 * After finish of using the size of rect should be set to null</p>
	 */
	@Override
	public void clearOperation() {
		
		m_rect.width = 0;
		
		m_rect.height = 0;
			
	    m_parent.resetInstrument();
	    
	    //start the instrument again - it is active until the user
		m_parent.setInstrument(PaInstrumentTypeEnum.ZoomRect,m_button);
		
		m_button.m_mainButton.setSelected(true);
				
	}

	/**
	 * This function is called from the parent component - it paints on the parent graphic context
	 */
	@Override
	public void drawShape(Graphics2D g2) {
		
		//no sense to draw zero rectangle
		if(m_rect.width == 0 || m_rect.height == 0 ) return;
		
		g2.setColor(getColor());
				
		g2.draw( m_rect);
		
	}
	
	@Override
	public Image getResultView(MouseEvent e,float xScale, float yScale, BufferedImage sourceImage) {
		
		if(m_startPoint != null) {
			
			isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView()
			
			Point sPoint = m_startPoint;
			
			SwingUtilities.convertPointToScreen(sPoint, m_parent);
			
			SwingUtilities.convertPointFromScreen(sPoint, m_parent.m_scrollPane.getMainLabel());
			
			Rectangle rec = new Rectangle(sPoint,new Dimension(m_rect.width, m_rect.height));
			
			rec = m_parent.convertFromInstrumentRectToImageRect(rec, true);
			
			
			System.out.println("PaZoomRectInstr:getResultView(): rec.x = " + Integer.toString(rec.x) + " rec.y = " +
					Integer.toString(rec.y));
			
			BufferedImage im = (BufferedImage) m_parent.getCurrentImage();
			
			if(im == null) {
				
				 writeLog("Can't find current image to use zoom rect instrument" , null, true, false, true);
				 
				 return null;
				 
			}
			
			m_parent.setNewViewSubImage(rec);


		}
		
		//operates with image in the work area, that is why return null
		return null;
		
	}
	

	/**
	 * 
	 * @return the cursor for this instrument;
	 */
	public Cursor getCursor() { 
		
		return Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(PaUtils.get().getIconsPath()+ "pahandzoomarea.png").getImage(),
				new Point(6,1),"custom cursor");
		
	}	
}

