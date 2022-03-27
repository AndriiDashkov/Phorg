
package PaROI;

import static PaGlobal.PaLog.writeLog;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import PaEditor.PaComplexButton;
import PaEditor.PaZoomRectInstrument;
import PaGlobal.PaUtils;

/**
 * @author Andrey Dashkov
 * Instrument for crop a part of image using rectangle of fixed size, and then save it to
 * chosen folder. The naming of results files is automatic.
 * Allows to make blur the central area of the rectangle.  
 *
 */
public class PaRoiChangeInstrument extends PaZoomRectInstrument {
	/**
	 * custom cursor 
	 */
	Cursor m_cursor;
	
	//this is size of the crop window of the instrument - these values are always in the scale of real processed image
	int m_cropWidth = 0;
	
	int m_cropHeight = 0;
	
	/**
	 * value for folder to save
	 */
	String m_saveFolderPath = null;

	boolean m_keep_ratio = true;
	
	int m_step_rec = 2;
	
	final String forbiddenFileDelimeter = PaUtils.getRoiDelimeter1();
	
	final String forbiddenFileName = "list_of_areas.txt"; 
	
	float m_initial_ratio;
	
	Rectangle m_current_roi = null;
	
	int m_current_roi_table_index;
	
	String m_current_roi_type = null;
	
	private ArrayList<PaRectangle> roi_data = null;
	
	private ArrayList<String> m_roiTypesData = null;
	
	public PaRoiChangeInstrument(PaInstrumentsRoiPanel parent, PaComplexButton but) {
		super(parent, but);
		
		PaRoiChangeButton b = (PaRoiChangeButton)but;
		
		m_cropWidth = b.getCropWidth();
		
		m_cropHeight = b.getCropHeight();	
	
		m_keep_ratio = b.isRatioMustBeKept();
		
		m_step_rec = b.getStep_Inc_Dec();
		
		m_keepActivated = b.isKeepInstrumentActivated();
		
		m_nextImageAutoSwitch = b.isNextImageSwitchActivated();
		
		m_initial_ratio =  ((float)m_cropWidth)/m_cropHeight;
		
		Rectangle newRec = new Rectangle(0, 0, m_cropWidth, m_cropHeight);
		
		m_currentSize.width = newRec.width;
		
	    m_currentSize.height = newRec.height;
		
		createCustomCursor();

	}
	
	
	public void setRoiData(ArrayList<PaRectangle> roiData, int roi_index, ArrayList<String> roiTypesData) 
	{
		
		m_current_roi_table_index = roi_index;
		
		roi_data = roiData;
		
		m_roiTypesData = roiTypesData;
		
		
		if (m_current_roi_table_index < 0) { return; }
		
		m_currentSize.width = roi_data.get(m_current_roi_table_index).w;
		
		m_currentSize.height = roi_data.get(m_current_roi_table_index).h; 
		
		//get real size of ROI on real image
		m_rect.x = roi_data.get(m_current_roi_table_index).x;
		
		m_rect.y = roi_data.get(m_current_roi_table_index).y;
		
		m_rect.setSize(m_currentSize);
		
		//crop size is always in the scale of processed image
		m_cropHeight = m_rect.height;
		
		m_cropWidth = m_rect.width;
		
	   	Rectangle newRec = new Rectangle(0, 0, m_cropWidth, m_cropHeight);
	   	
		newRec = m_parent.m_scrollPane.convertBaseWindowRecToViewRec(newRec);
					
		m_currentSize.width = newRec.width;
		
		m_currentSize.height = newRec.height;
					
		m_rect.setSize(m_currentSize);
		
		m_current_roi_type = roi_data.get(m_current_roi_table_index).type;
	}
	

	
	/**
	 * Returns the result of instrument working - new result of an image.
	 * @param sourceImag - the initial image
	 */
	@Override
	public Image getResultView(MouseEvent e, float xScale, float yScale, BufferedImage sourceImage) 
	{
		
		if(m_rect != null && m_rect.width != 0 && m_rect.height != 0) {
			
			try {
				isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView()
	
				
				Point sPoint = new Point(e.getLocationOnScreen());
				
				SwingUtilities.convertPointFromScreen(sPoint, m_parent.m_scrollPane.getMainLabel());
				
				Rectangle rec = new Rectangle(sPoint,new Dimension(m_cropWidth, m_cropHeight));
				
				//false - without the control of nonvalid geometry; we need the original sizes to calculate the padding
				//this instrument must be able to draw the select rectangle over the boundaries of image!
				rec = m_parent.convertFromInstrumentRectToImageRect(rec, false);   
				
				rec.width = m_cropWidth;
				
				rec.height = m_cropHeight;
				
				PaRectangle r = new PaRectangle( (int) rec.getX(),(int) rec.getY(), (int) rec.getWidth(), (int) rec.getHeight());
				
				if(m_current_roi_type == null) {r.type = "";}
				
				else { r.type = m_current_roi_type; }
				
				if(m_current_roi_table_index < 0) {
					
					writeLog("No selected ROI for the edit operation." , null, true, true, false);
					
					return null;
				}
				
				roi_data.set(m_current_roi_table_index,r);
			
				BufferedImage targetImage = ((PaInstrumentsRoiPanel) m_parent).setImageWithRoi();
				
				if(targetImage == null) {
					
					 writeLog("Can't find current image to use the instrument" , null, true, false, true);
					 
					 return null;
				}
		
				return targetImage;
			}
			finally {
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		
		return null;
	}
	
	/**
	 * <p>Can be overloaded in the children in order to reset some parameters after instrument using is finished
	 * After finish of using the size of rect should be set to null</p>
	 */
	@Override
	public void clearOperation() 
	{
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		m_rect.width = 0;
		
		m_rect.height = 0;
		
		m_parent.resetInstrument();
		
	};
	
	/**
	 * creates custom instrument cursor 
	 */
	private void createCustomCursor() {
		
	}
	
	public void setCursor() 
	{
		createCustomCursor();
	
		m_parent.setCursor(m_cursor);
	}
	
	public Cursor getCursor() {

		return m_cursor;
	};
	
	/**
	 * This function is called from the parent component - it paints on the parent graphic context the 
	 * instrument shape.
	 */
	@Override
	public void drawShape(Graphics2D g2) {
		
		if(m_rect.width == 0 || m_rect.height == 0 ) return;
		
		g2.setColor(Color.RED);
				
		g2.draw(m_rect);
		
		g2.draw(new Rectangle(m_rect.x + 1, m_rect.y + 1, m_rect.width - 2, m_rect.height - 2 ));
		
		final int markWidth = 8;
		
		g2.draw(new Line2D.Double(new Point(m_rect.x + m_rect.width/2, m_rect.y),
				new Point(m_rect.x + m_rect.width/2, m_rect.y + markWidth)));
		
		g2.draw(new Line2D.Double(new Point(m_rect.x + m_rect.width/2, m_rect.y + m_rect.height),
				new Point(m_rect.x + m_rect.width/2, m_rect.y + m_rect.height - markWidth)));
		
		g2.draw(new Line2D.Double(new Point(m_rect.x, m_rect.y + m_rect.height/2),
				new Point(m_rect.x + markWidth, m_rect.y + m_rect.height/2)));
		
		g2.draw(new Line2D.Double(new Point(m_rect.x + m_rect.width, m_rect.y + m_rect.height/2),
				new Point(m_rect.x + m_rect.width - markWidth, m_rect.y + m_rect.height/2)));
		
		g2.drawString(String.valueOf(m_cropWidth) + "x" + String.valueOf(m_cropHeight),m_rect.x + m_rect.width + 3, m_rect.y + 5);
		
		g2.drawString(String.valueOf(m_cropWidth) + "x" + String.valueOf(m_cropHeight),m_rect.x + m_rect.width + 3, m_rect.y + 5);
		
		if( null != m_current_roi_type && !m_current_roi_type.isEmpty()) {
			
			Font currentFont = g2.getFont();
			
			Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
			
			g2.setFont(newFont);
			
			int ln = 3;
			
			if(m_current_roi_type.length() < ln) { ln = m_current_roi_type.length(); } 
			
			g2.drawString(m_current_roi_type.substring(0, ln), m_rect.x + m_rect.width/3, m_rect.y + + m_rect.height/2);
		}

	}
	
    //filtering mouse events from parent
	@Override
	public boolean filterMouseEvent(MouseEvent e) {

		Point currentPoint = new Point(e.getLocationOnScreen());
		
		SwingUtilities.convertPointFromScreen(currentPoint, m_parent);
		
		
		switch(e.getID()) {
		
			case  MouseEvent.BUTTON2 : { m_startPoint = currentPoint; break; }
			
			case  MouseEvent.MOUSE_WHEEL :
			{ 
				float ratio =  m_initial_ratio;
				
				int notches = ((MouseWheelEvent)e).getWheelRotation();
				  
			    if (notches < 0) { //"Mouse wheel moved UP "
			    	
			    	m_cropHeight -= m_step_rec;
			    	
					//if the user chose to save proportions between height and width
					if(m_keep_ratio) {
						
						m_cropWidth = (int)(m_cropHeight *ratio); 
						
					}
				  
					
			    } else { //"Mouse wheel moved DOWN "
			          
			 		m_cropHeight += m_step_rec;
			   		
					//if the user chose to save proportions between height and width
					if(m_keep_ratio) {
						
						m_cropWidth = (int)(m_cropHeight *ratio); 
					}
			                  
			    }
			       
			   	Rectangle newRec = new Rectangle(0, 0, m_cropWidth, m_cropHeight);
			   	
				newRec = m_parent.m_scrollPane.convertBaseWindowRecToViewRec(newRec);
				
				m_currentSize.width = newRec.width;
				
			    m_currentSize.height = newRec.height;
				
				m_rect.setSize(m_currentSize);
				
				return true;
			
			}
			
			case  MouseEvent.MOUSE_DRAGGED :
			{ }
			
			case  MouseEvent.MOUSE_EXITED :
			{ 
				m_xPrev =  N0_PREV_MOUSE_POSITION;
				
				m_yPrev =  N0_PREV_MOUSE_POSITION;
				
				return true;
				
			}
			
			case  MouseEvent.MOUSE_MOVED :
			{ 
				currentPoint = e.getLocationOnScreen();
				
				SwingUtilities.convertPointFromScreen(currentPoint, m_parent);

				int deltaX = 0;
				
				int deltaY = 0; 

				if (  m_xPrev !=  N0_PREV_MOUSE_POSITION)  {
					
					deltaX = - m_xPrev  +  currentPoint.x;
					
					deltaY = - m_yPrev +  currentPoint.y;
					
				}
				
				m_xPrev = currentPoint.x;
				
				m_yPrev  = currentPoint.y;
		 
				m_currentPoint.x += deltaX;
				
				m_currentPoint.y += deltaY;
				
				m_rect.setLocation(currentPoint);
			
				m_rect.setSize(m_currentSize);

				return true;
			
			}
		
		};
		
			
		return false;
	}
	
	/**
	 * <p>Function to handle the keys W,S,A,D press events. It is used for a change of the 
	 * crop rectangle size (increasing and decreasing).</p>
	 */
	public boolean filterKeyEvent(int keyCode) 
	{
		
		float ratio =  m_initial_ratio;
		
		boolean inteceptFlag = false;
		
		switch(keyCode) {
		
			case KeyEvent.VK_S : {
				
				m_cropHeight += m_step_rec;
				
				//if the user chose to save proportions between height and width
				if(m_keep_ratio) {
					
					m_cropWidth = (int)(m_cropHeight *ratio); 
				}
				
				inteceptFlag = true;
				
				break;	
			}
			case KeyEvent.VK_W : {
				
				m_cropHeight -= m_step_rec;
				
				//if the user chose to save proportions between height and width
				if(m_keep_ratio) {
					
					m_cropWidth = (int)(m_cropHeight *ratio); 
				}
				
				inteceptFlag = true;
				
				break;
			}
			case KeyEvent.VK_A : {
				
				m_cropWidth -= m_step_rec;
				
				//if the user chose to save proportions between height and width
				if(m_keep_ratio) {
					
					m_cropHeight = (int)(m_cropWidth/ratio); 
				}
				
				inteceptFlag = true;
				
				break;
			}
			case KeyEvent.VK_D : {
				
				m_cropWidth += m_step_rec;
				
				//if the user chose to save proportions between height and width
				if(m_keep_ratio) {
					
					m_cropHeight = (int)(m_cropWidth/ratio); 
				}
				
				inteceptFlag = true;
				
				break;
			}
			case KeyEvent.VK_E : {
				
				if(m_roiTypesData != null && m_current_roi_type != null) {
					
					 int new_index = m_roiTypesData.indexOf(m_current_roi_type) + 1;
					 
					 if(new_index >= m_roiTypesData.size()) { new_index = 0;}
					
					 m_current_roi_type = m_roiTypesData.get(new_index);
					
					 inteceptFlag = true;
				}
				 
				 break;
			}
		
			default : {}
		};
		
		if(inteceptFlag){
			
			Rectangle newRec = new Rectangle(0, 0, m_cropWidth, m_cropHeight);
			
			newRec = m_parent.m_scrollPane.convertBaseWindowRecToViewRec(newRec);
			
			m_currentSize.width = newRec.width;
			
		    m_currentSize.height = newRec.height;
			
			m_rect.setSize(m_currentSize);
			
			return true;
		}
		return false;
	}
}