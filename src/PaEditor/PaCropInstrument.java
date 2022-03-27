package PaEditor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.getGuiStrs;

/**
 * @author Andrey Dashkov
 * The instrument for the crop a part of image using a rectangle of fixed size. The instrument allows to move and resize crop area
 * with or without ratio control   
 *
 */
public class PaCropInstrument extends PaInstrument {
	 
	//for reset the previous cursor position
	final int N0_PREV_MOUSE_POSITION =  -10000;
	

	//values for previous mouse position capture
	int m_xPrev =  N0_PREV_MOUSE_POSITION;
	
	int m_yPrev =  N0_PREV_MOUSE_POSITION;
	
	//parent component where the image is on. We need it for cursor manipulation
	PaInstrumentsPanel m_parent;

	//square markers on crop frame
	Rectangle[] m_markers = new Rectangle[5];
	
	int m_markerWidth = 15;
	
    //crop frame's parameters
	Rectangle m_rect; 
	
	Point m_currentPoint = new Point(10,10);
	
	Dimension  m_currentSize = new Dimension(100,100);
	
	//flags for selected markers
	boolean[] m_readyForResize = new boolean[5];
	
	boolean m_ratioKeeping = false;
	
	double m_ratio;
	 
	/**
	 * start point of instrument drawing
	 */
	Point m_startPoint;
	
	 
	public PaCropInstrument(PaInstrumentsPanel parent, Dimension startRec,  PaComplexButton but) {
		
		super(but,PaUtils.get().getSettings().getInstrumentsColor(),getGuiStrs("cropInstrName"));
		
		m_dragMouseRepaintEnabled = false; //no reaction while dragging mouse
		
		m_parent = parent;

		initialFitWithRatioControl();

		m_markerWidth = PaUtils.get().getSettings().getInstrumentMarkerSize();
		
		m_rect = new Rectangle(m_currentPoint, m_currentSize);
		 
		 
		for ( int i=0; i < m_markers.length; ++i) {
			 
			 m_markers[i] = new Rectangle( );
			 
			 m_markers[i].setSize(m_markerWidth,m_markerWidth);
		}
		
		updateMarkersSize();
		
	} 	 
	
	/**
	 * recalculates the geometry of markers
	 */
	private void updateMarkersSize() {
		
		
		 m_markers[0].setLocation( m_currentPoint.x,  m_currentPoint.y);
		 
		 m_markers[1].setLocation( m_currentPoint.x + m_currentSize.width-  m_markerWidth,  m_currentPoint.y );
		 
		 m_markers[2].setLocation( m_currentPoint.x + m_currentSize.width-  m_markerWidth,  m_currentPoint.y  + m_currentSize.height-  m_markerWidth);
		 
		 m_markers[3].setLocation( m_currentPoint.x ,  m_currentPoint.y  + m_currentSize.height-  m_markerWidth);
		 
		 m_markers[4].setLocation( m_currentPoint.x + m_currentSize.width/2 - m_markerWidth/2 ,  m_currentPoint.y  + m_currentSize.height/2 -  m_markerWidth/2);
	}
	
	/**
	 * This filter intercepts the mouse events from parent component; the instrument is used as a visitor pattern
	 */
	@Override
	public boolean filterMouseEvent(MouseEvent e) {
				
		Rectangle controlRec = m_parent.getViewVisibleRectOnScreen();
		
		Point currentPoint = new Point(e.getLocationOnScreen());
		
		SwingUtilities.convertPointFromScreen(currentPoint, m_parent);
		
		if (e.getID() ==  MouseEvent.MOUSE_DRAGGED  ) {
			
			int deltaX = 0;
			
			int deltaY = 0; 
	      
		
			if (  m_xPrev !=  N0_PREV_MOUSE_POSITION)  {
				
				deltaX = - m_xPrev  +  currentPoint.x;
				
				deltaY = - m_yPrev +  currentPoint.y;
				
			}
		
			//control for minimum size of rectangle
			int minWidth = (2*m_markerWidth +5);
			
			if ( m_currentSize.height < minWidth) {  m_currentSize.height =  minWidth; }
			
			if ( m_currentSize.width < minWidth) {  m_currentSize.width =  minWidth; }

			 m_xPrev = currentPoint.x;
			 
			 m_yPrev  = currentPoint.y;
			 
	
			  if (  m_readyForResize[3] ) {
						
					 m_currentSize.height += deltaY;
					 
					 m_currentSize.width -= deltaX;		
					 
					 m_currentPoint.x += deltaX;
					 
		
			  } else  if (  m_readyForResize[2] ) {
				   
					 m_currentSize.height += deltaY;
					 
					 m_currentSize.width += deltaX;
				
			  } else  if (  m_readyForResize[1] ) {
				  
					 m_currentSize.height -= deltaY;
					 
					 m_currentSize.width += deltaX;
					 
					 m_currentPoint.y += deltaY;	
				  
			  } else  if (  m_readyForResize[0] ) {
				  
				  	m_currentSize.height -= deltaY;
				  	
					 m_currentSize.width -= deltaX;		
					 
					 m_currentPoint.y += deltaY;
					 
					 m_currentPoint.x += deltaX;
					 
			  }
			  else  if (  m_readyForResize[4] ) {
				  
				  	//m_currentSize.height += deltaY;
				  	
					// m_currentSize.width += deltaX;		
					 
					 m_currentPoint.y += deltaY;
					 
					 m_currentPoint.x += deltaX;
					 
			  }
			  
			  m_rect.setLocation( m_currentPoint.x,  m_currentPoint.y);
		  
		  
				Point topLeftPoint = new Point(m_currentPoint);
				
				Point bottomRightPoint = new Point(topLeftPoint);
				
				bottomRightPoint.x += m_currentSize.width; 
				
				bottomRightPoint.y += m_currentSize.height;
				
				SwingUtilities.convertPointToScreen(bottomRightPoint, m_parent);
				
				SwingUtilities.convertPointToScreen(topLeftPoint, m_parent);
				
				if(!controlRec.contains(bottomRightPoint)) {
			
					if((controlRec.width + controlRec.x) < (bottomRightPoint.x) ) {
						
						m_currentSize.width -= bottomRightPoint.x - controlRec.width - controlRec.x;
					}
					if((controlRec.height + controlRec.y) < (bottomRightPoint.y) ) {
						
						m_currentSize.height -= bottomRightPoint.y - controlRec.height - controlRec.y;
					}
				}
				if(!controlRec.contains(topLeftPoint)) {
					
					if(controlRec.x > topLeftPoint.x) {
						
						m_currentPoint.x += controlRec.x - topLeftPoint.x;
						
						m_currentSize.width -= controlRec.x - topLeftPoint.x;
					}
					
					if(controlRec.y > topLeftPoint.y) {
						
						m_currentPoint.y += controlRec.y - topLeftPoint.y;
						
						m_currentSize.height -= controlRec.y - topLeftPoint.y;
					}
				}
				
				 if (m_ratioKeeping) {
					 
					 if(deltaY > deltaX) {
						 
						 m_currentSize.width = (int) (m_currentSize.height *m_ratio);
					 }
					 else {
						 m_currentSize.height = (int) (m_currentSize.width /m_ratio);
					 }
				 }
		  
			  m_rect.setSize( m_currentSize);
			  
			  m_rect.x = m_currentPoint.x;
			  
			  m_rect.y = m_currentPoint.y;
			  
			  updateMarkersSize(); 
			 // 
			  return true;
			
		}
		
		if (e.getID() ==  MouseEvent.MOUSE_EXITED  ) {
		
				m_xPrev =  N0_PREV_MOUSE_POSITION;
				
				m_yPrev =  N0_PREV_MOUSE_POSITION;
				
				m_readyForResize[0] = false; m_readyForResize[1] = false;
				
				m_readyForResize[2] = false; m_readyForResize[3] = false;
				
				m_readyForResize[4] = false;
				
				return true;
		
		}
		
		if (e.getID() ==  MouseEvent.MOUSE_MOVED  ) {
			
				m_readyForResize[0] = false; m_readyForResize[1] = false;
				
				m_readyForResize[2] = false; m_readyForResize[3] = false;
				
				m_readyForResize[4] = false;
			
			if (  m_markers[0].contains(currentPoint.x,currentPoint.y)  ) {
										
				m_readyForResize[0] = true;
				
				m_parent.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
			} else if ( m_markers[1].contains(currentPoint.x,currentPoint.y) ) {
				
				m_readyForResize[1] = true;
				
				m_parent.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
			} else if ( m_markers[2].contains(currentPoint.x,currentPoint.y) ) {
				
				m_readyForResize[2] = true;
				
				m_parent.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
			} else if ( m_markers[3].contains(currentPoint.x,currentPoint.y) ) {
				
				m_readyForResize[3] = true;
				
				m_parent.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
			} 
			else if ( m_markers[4].contains(currentPoint.x,currentPoint.y) ) {
				
				m_readyForResize[4] = true;
				
				m_parent.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
			} else {
				
	
				m_parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				m_xPrev =  N0_PREV_MOUSE_POSITION;
				
				m_yPrev =  N0_PREV_MOUSE_POSITION;
				
			}
			
			return false;
		}
		
		return false;
	}
	/**
	 * 
	 * @return the information string about current crop rectangle; this info is depended on special panel of crop button
	 */
	protected String getInfoString() {
		
		String s = new String();
			
		s += String.valueOf((int)(m_rect.width*m_parent.getXScale()))+" x " 
					+ (int)(m_rect.height*m_parent.getYScale());

		return s;
	}

    /**
     * This function is called from the paint() of parent component
     */
	@Override
	public void drawShape(Graphics2D g2) {
		
		g2.setColor(getColor());
	
		g2.draw(m_rect);
		
		g2.draw(new Rectangle(m_rect.x + 1, m_rect.y + 1,m_rect.width - 2, m_rect.height - 2));
		
		String s = getInfoString();
		
		if(s != null && !s.isEmpty()) {
			
			g2.drawString(s,m_markers[0].x+m_markers[0].width+5, m_markers[0].y+m_markers[0].height+5);
		}
		
		g2.draw(  m_markers[0]);
		

		g2.draw(  m_markers[1]);
		
		g2.draw(  m_markers[2]);

		g2.draw(  m_markers[3]);
	
		
		g2.draw(  m_markers[4]);

		
	}
	/**
	 * @return the image after instrument has been used; gets the end result of the instrument using 
	 */
	@Override
	public Image getResultView(MouseEvent e,float xScale, float yScale, BufferedImage sourceImage) {
		
		isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView()
		
		if ( e.getID() == MouseEvent.MOUSE_DRAGGED ) { return null; }
		
	
		int x = (int) ((m_currentPoint.x - m_startPoint.x) * xScale);
		
		int y = (int) ((m_currentPoint.y- m_startPoint.y) * yScale);
		
		int w = (int) ( m_currentSize.width * xScale);
		
		int h = (int) ( m_currentSize.height * yScale);
		
		if((x+w) > sourceImage.getWidth() ) w = sourceImage.getWidth() - x;
		
		if ((y+h) >  sourceImage.getHeight() ) h = sourceImage.getHeight() - y;
		
		if(x < 0) { x = 0; }
		
		if(y < 0) { y =0; }
		
		Point p = new Point(m_rect.x,m_rect.y);
		
		SwingUtilities.convertPointToScreen(p, m_parent);
		
		SwingUtilities.convertPointFromScreen(p, m_parent.getImageLabel());
		
		Rectangle rec = new Rectangle(p.x,p.y, m_rect.width, m_rect.height);
		
		rec = m_parent.m_scrollPane.convertViewRectToBaseWindow(rec, false, true);
	
		return ((BufferedImage) sourceImage).getSubimage(rec.x, rec.y, rec.width, rec.height);
		
	}
	
	
	private void initialFitWithRatioControl() {
		
		m_ratio = ((PaCropButton)m_button).getProportion();
		
		m_ratioKeeping = ((PaCropButton)m_button).isRatioControled();
		
		Rectangle controlRec = m_parent.getViewVisibleRectOnScreen();
		
		Point sP = new Point(controlRec.x,controlRec.y);
		
		SwingUtilities.convertPointFromScreen(sP, m_parent);
		
		m_startPoint =  sP;
		
		m_currentPoint.x = m_startPoint.x;
		
		m_currentPoint.y = m_startPoint.y;
		
		if (m_ratioKeeping) {
			
			int new_width = controlRec.width - 10;
			
			int new_height = (int) (new_width / m_ratio);
			
			while(new_height >= (controlRec.height - 10)) {
			
				new_width -= 5;
			  
				new_height = (int) (new_width / m_ratio);
			}
			
			m_currentSize.width = new_width;	
			
			m_currentSize.height = new_height;
			
		}
		else {
			
			m_currentSize.width = controlRec.width - 10;	
			
			m_currentSize.height = controlRec.height - 10;
			
		}	
	}
}
 