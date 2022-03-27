package PaEditor;



import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;


public class PaHorizonInstrument extends PaInstrument {
	 
	//parent component where the image is on. We need it for cursor manipulation
	private PaInstrumentsPanel m_parent;

	//square markers on crop frame
	private Rectangle[] m_markers = new Rectangle[2];
	
	private int m_markerWidth = 15;
	
    //crop frame's parameters
	private Line2D.Float m_line; 
	
	private Point m_currentPoint = new Point(10,10);
	
	private Point m_nextPoint = new Point(10,10);
	
	//Dimension  m_currentSize = new Dimension(100,100);
	
	//flags for selected marker
	private boolean[] m_readyForResize = new boolean[2];
	
	String m_strAngle;

	 
	//start point for the instrument
	private Point m_startPoint;
	 
	public PaHorizonInstrument(PaInstrumentsPanel parent, PaComplexButton but) {
		super(but,PaUtils.get().getSettings().getInstrumentsColor(),getGuiStrs("horizonInstName"));
	
		m_parent = parent;
		
		Rectangle controlRec1 = m_parent.getViewVisibleRectOnScreen();
	
		Point sP = new Point(controlRec1.x+10,controlRec1.y+10);
		
		SwingUtilities.convertPointFromScreen(sP, m_parent);
		
		m_startPoint =  sP;
	
		m_currentPoint.x = m_startPoint.x+20;
		
		m_currentPoint.y = m_startPoint.y+20;
		
		
		m_nextPoint.x =  controlRec1.width -40;
		
		m_nextPoint.y = m_currentPoint.y;
		  		
		m_markerWidth = PaUtils.get().getSettings().getInstrumentMarkerSize();
		
		m_line = new Line2D.Float(m_currentPoint,m_nextPoint);
		 
		 
		for ( int i=0; i < m_markers.length; ++i) {
			 
			 m_markers[i] = new Rectangle( );
			 
			 m_markers[i].setSize(m_markerWidth,m_markerWidth);
		}
		
		updateMarkersSize();
		
		m_strAngle = getGuiStrs("angleHorizonCaption");

	} 	 
	//updates the marker's geometry
	private void updateMarkersSize() {
		  
		 m_markers[0].setLocation( m_currentPoint.x,  m_currentPoint.y);
		 
		 m_markers[1].setLocation( m_nextPoint.x, m_nextPoint.y );

	}
	

    //filters mouse event from the parent
	@Override
	public boolean filterMouseEvent(MouseEvent e) {
				
		//int  minWidth =  m_markerWidth*2 ;
	
		Rectangle controlRec1 = m_parent.getViewVisibleRectOnScreen();
		
		//change the control rectangles because we need left half for 1 point and right half for second point
		Rectangle controlRec2 = new Rectangle((int)(controlRec1.x+controlRec1.width/2.0+m_markerWidth),
				controlRec1.y,(int)(controlRec1.width/2.0 - m_markerWidth),controlRec1.height);
		
		controlRec1.width = (int)(controlRec1.width/2.0 - m_markerWidth);
		
		Point currentPoint = new Point(e.getLocationOnScreen());
		
		SwingUtilities.convertPointFromScreen(currentPoint, m_parent);
		
		if (e.getID() ==  MouseEvent.MOUSE_DRAGGED  ) {
			
			if (  m_readyForResize[1] ) {
					 
				 m_nextPoint.y = currentPoint.y;
				 
				 m_nextPoint.x = currentPoint.x;
					 
			} else  if (  m_readyForResize[0] ) {
				   
				m_currentPoint.y = currentPoint.y;
				
				m_currentPoint.x = currentPoint.x;
					 	 
			}
			//controls of boundaries for two point
			Point point1 = new Point(m_currentPoint);
			
			Point point2 = new Point(m_nextPoint);
			
			SwingUtilities.convertPointToScreen(point1, m_parent);
			
			SwingUtilities.convertPointToScreen(point2, m_parent);
			
			if(!controlRec1.contains(point1)) {
				
				if(controlRec1.x > point1.x ) {
					
					m_currentPoint.x += controlRec1.x - point1.x;
				}
				if(controlRec1.y > point1.y ) {
					
					m_currentPoint.y += controlRec1.y - point1.y;
				}
				if((controlRec1.y + controlRec1.height - m_markerWidth) < point1.y ) {
					
					m_currentPoint.y -= point1.y - controlRec1.y - controlRec1.height + m_markerWidth;
				}
				if((controlRec1.x + controlRec1.width) < point1.x ) {
					
					m_currentPoint.x -= point1.x - controlRec1.x - controlRec1.width;
				}
			}
			if(!controlRec2.contains(point2)) {
				
				if((controlRec2.x +controlRec2.width - m_markerWidth) < point2.x ) {
					
					m_nextPoint.x -= point2.x - controlRec2.x - controlRec2.width + m_markerWidth;
				}
				if((controlRec2.y +controlRec2.height - m_markerWidth) < point2.y ) {
					
					m_nextPoint.y -= point2.y - controlRec2.y - controlRec2.height + m_markerWidth;
				}
				if(controlRec2.y > point2.y ) {
					
					m_nextPoint.y += controlRec2.y - point2.y;
				}
				if(controlRec2.x > point2.x ) {
					
					m_nextPoint.x += controlRec2.x - point2.x ;
				}
			}
			  
			m_line.setLine( m_currentPoint, m_nextPoint);
					 
			updateMarkersSize(); 
			
			return true;
			
		}
		
		if (e.getID() ==  MouseEvent.MOUSE_EXITED  ) {
					
				m_readyForResize[0] = false; m_readyForResize[1] = false;
				
				return true;
		
		}
		
		if (e.getID() ==  MouseEvent.MOUSE_MOVED  ) {
			
				m_readyForResize[0] = false; m_readyForResize[1] = false;
			
			if (  m_markers[0].contains( currentPoint.x,currentPoint.y)  ) {
										
				m_readyForResize[0] = true; 
				
				m_parent.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
			} else if ( m_markers[1].contains( currentPoint.x,currentPoint.y) ) {
				
				m_readyForResize[1] = true;
				
				m_parent.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				
			}  else {
					
				m_parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
			
			}
			
			return true;
		}
		
		if (e.getID() ==  MouseEvent.MOUSE_RELEASED  ) {
			
			return true;
		
		}
		return false;
	}

 

    //this function is called from the paint() function of the parent
	@Override
	public void drawShape(Graphics2D g2) {
		
		g2.setColor(getColor());
				
		g2.draw(m_line);
		
		g2.draw(m_markers[0]);
		
		double a = Math.atan((m_line.getY2()-m_line.getY1())/(m_line.getX2()-m_line.getX1())); 
		
		if(a < 0.0) a *= -1;
		
		BigDecimal x = new BigDecimal(a*180.0/Math.PI);
		
		x = x.setScale(2, BigDecimal.ROUND_HALF_UP);

	
		g2.drawString(m_strAngle +" " +x.toString(),m_markers[0].x+m_markers[0].width+5, m_markers[0].y+m_markers[0].height+5);
		
		//g2.fill(  m_markers[0]);
		
		g2.draw(  m_markers[1]);
		
		//g2.fill(  m_markers[1]);
		
	}
	@Override
	public Image getResultView(MouseEvent e, float xScale, float yScale, BufferedImage sourceImage) {
		
		if ( e.getID() != MouseEvent.MOUSE_RELEASED )  {
			
			return null;
		}
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
		
			isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView()
			
			int deltaY = m_nextPoint.y - m_currentPoint.y;
			
			int deltaX = m_nextPoint.x - m_currentPoint.x;
			
			double tangle = ( (double)deltaY )/( (double)deltaX);
			
			double angle = Math.atan(tangle);
			
			BufferedImage targetImage = PaUtils.deepCopy( sourceImage);
			
			AffineTransform at = new AffineTransform(); 
	
			at.rotate(-angle, targetImage.getWidth()/2,targetImage.getHeight()/2 );     
			
			AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			
			 targetImage = op.filter( targetImage, null);
					
			return  targetImage;
		
		}
		catch(OutOfMemoryError ex) {
			
			JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("outOfMemoryMessage"),
				    getMessagesStrs("messageCaption"),
				    JOptionPane.OK_OPTION);
			
			writeLog("Not enough memory: can't get result image for horizon instrument" + NEXT_ROW +
					ex.getMessage(), null,
					true, false, false );
		}
		finally {
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window: horizon instrument operation finished.", null,
					true, false, false );
		}
		
		return  null;
		
	}



}