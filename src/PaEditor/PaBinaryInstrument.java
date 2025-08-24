/**
 * 
 */
package paeditor;


import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import paalgorithms.PaAlgoConvert;
import paenums.PaInstrumentTypeEnum;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * The instrument to select area for converting to grayscale or binary image; inherits the zoom rectangle instrument
 */
public class  PaBinaryInstrument extends  PaZoomRectInstrument {

	 int m_currentCursorType;
	/**
	 * @param parent
	 * @param but
	 * @param col
	 */
	public PaBinaryInstrument(PaInstrumentsPanel parent, PaBinaryButton but) {
		super(parent, but);
	}
	
	/**
	 * @return the image after instrument has been used; gets the end result of the instrument using 
	 */
	@Override
	public Image getResultView(MouseEvent e,float xScale, float yScale, BufferedImage sourceImage) {
		
		if(m_startPoint != null) {
			
			try {
				
				isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView()
			
				PaBinaryButton b = (PaBinaryButton)m_button;
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
				Point sPoint = m_startPoint;
				
				SwingUtilities.convertPointToScreen(sPoint, m_parent);
				
				SwingUtilities.convertPointFromScreen(sPoint, m_parent.m_scrollPane.getMainLabel());
				
				Rectangle rec = new Rectangle(sPoint,new Dimension(m_rect.width,m_rect.height));
				
				rec = m_parent.convertFromInstrumentRectToImageRect(rec, true);
			
				BufferedImage fullImage = PaUtils.deepCopy(sourceImage);
				
				//be careful - the raster data are shared with raster data of  fullImage
				BufferedImage im = PaUtils.deepCopy(fullImage.getSubimage(rec.x, rec.y, rec.width, rec.height));
			
				if(im == null) {
					
					 writeLog("Can't find current image to use color balance instrument" , null, true, false, true);
					 
					 return null;
				}
				 
		
				
				BufferedImage newIm = null;
				
				if(b.isGrayScaleActivated()) {
					
					newIm = (BufferedImage) PaAlgoConvert.toGrayscale(im);
				}
				else {
				
					newIm = (BufferedImage) PaAlgoConvert.toBinary(im,  b.getTypeOfColor(),  b.getThresholds());
				}
								
		
				fullImage.getRaster().setRect(rec.x, rec.y, newIm.getRaster());
					
				m_parent.getWindow().setResultView(fullImage, getGuiStrs("colorBalanceInstrumentName"));
			}
			catch(OutOfMemoryError eOut) {
				
				JOptionPane.showMessageDialog(null,getMessagesStrs("outOfMemoryMessage"),
					    getMessagesStrs("messageCaption"),JOptionPane.OK_OPTION);
				
				writeLog("Not enough memory: can't get result image for color balance instrument" + NEXT_ROW +
						eOut.getMessage(), null,true, false, false );
			}
			finally {
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				writeLog("Instruments window: color balance instrument operation finished.", null,
						true, false, false );
			}

		}
			
		return null;
		
	}
	

	
	/**
	 * 
	 * @return the information string about current blur operation; this info is depended on special panel of crop button
	 */
	protected String getInfoString() {
		
		return null;
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
		m_parent.setInstrument(PaInstrumentTypeEnum.Binary,m_button);
		
		m_button.m_mainButton.setSelected(true);
				
	}
	
	/**
	 * 
	 * @return the cursor for this instrument;
	 */
	public Cursor getCursor() { 
	
		
		return Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(PaUtils.get().getIconsPath()+ "handcolorbal.png").getImage(),
				new Point(6,1),"custom cursor");

	}
}
