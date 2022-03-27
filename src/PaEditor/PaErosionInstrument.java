
package PaEditor;


import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;

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

import PaAlgorithms.PaAlgorithms;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaUtils;


/**
 * @author avd
 * The instrument to select area for applying of erosion/dilataion ; 
 * inherits the zoom rectangle instrument
 */
public class PaErosionInstrument extends  PaZoomRectInstrument {

	 int m_currentCursorType;
	/**
	 * @param parent
	 * @param but
	 * @param col
	 */
	public PaErosionInstrument(PaInstrumentsPanel parent, PaErosionButton but) {
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
			
				PaErosionButton b = (PaErosionButton)m_button;
				
				//m_currentCursorType = PaUtils.get().getCurrentCursor();
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
					
					 writeLog("Can't find current image to erosion/dilataion instrument" , null, true, false, true);
					 
					 return null;
				}
				
				PaAlgorithms al = new PaAlgorithms();
				
				BufferedImage newIm = null;
				
				String message = "";
				
				switch(b.getType()) {
				
					case EROSION : { 
						
						newIm = (BufferedImage) al.erosion(im, b.getMaskSize());
						
						message = getMessagesStrs("cantPerformErosionMessage");
						
						break;
						
					}
					case DILATATION : { 
						
						newIm = (BufferedImage) al.dilatation(im, b.getMaskSize());
						
						message = getMessagesStrs("cantPerformDilatationMessage");
						
						break;
					}
					default : {}
		
			}
					
			if(newIm == null) {
				
				JOptionPane.showMessageDialog(
					    null,
					    message,
					    getMessagesStrs("messageCaption"),
					    JOptionPane.OK_OPTION);
			}
			else {	
				
				fullImage.getRaster().setRect(rec.x, rec.y, newIm.getRaster());
					
				m_parent.getWindow().setResultView(fullImage, getGuiStrs("erosionInstrumentName"));
			}
			}
			catch(OutOfMemoryError eOut) {
				
				JOptionPane.showMessageDialog(null,getMessagesStrs("outOfMemoryMessage"),
					    getMessagesStrs("messageCaption"),JOptionPane.OK_OPTION);
				
				writeLog("Not enough memory: can't get result image for erosion/dilataion instrument" + NEXT_ROW +
						eOut.getMessage(), null,true, false, false );
			}
			finally {
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				writeLog("Instruments window: erosion/dilataion instrument operation finished.", null,
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
		m_parent.setInstrument(PaInstrumentTypeEnum.EROSION_DIL,m_button);
		
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