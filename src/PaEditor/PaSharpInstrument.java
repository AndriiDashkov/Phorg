
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
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import PaAlgorithms.PaAlgorithms;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaUtils;
import PaLong.PaSharpOperation;

/**
 * @author avd
 * The instrument to select area for change the sharpness; inherits the zoom rectangle instrument
 */
public class  PaSharpInstrument extends  PaZoomRectInstrument {

	 int m_currentCursorType;
	 
	/**
	 * @param parent - the instrument panel
	 * @param but - sharpness increase button
	 */
	public  PaSharpInstrument(PaInstrumentsPanel parent, PaComplexButton but) {
		super(parent, but);
	}
	

	/**
	 * @return the image after instrument has been used; gets the end result of the instrument using 
	 */
	@Override
	public Image getResultView(MouseEvent e,float xScale, float yScale, BufferedImage sourceImage) {
		
		if(m_startPoint != null) {
			
			try {	
				
				PaSharpButton b = (PaSharpButton)m_button;
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
				Point sPoint = m_startPoint;
				
				SwingUtilities.convertPointToScreen(sPoint, m_parent);
				
				SwingUtilities.convertPointFromScreen(sPoint, m_parent.m_scrollPane.getMainLabel());
				
				Rectangle rec = new Rectangle(sPoint,new Dimension(m_rect.width,m_rect.height));
				
				rec = m_parent.convertFromInstrumentRectToImageRect(rec, true);
				
				if(rec.width <= 0 || rec.height <= 0) {
					
					 writeLog("Sharp instrument: zero width or heght of selected area" , null, true, false, true);
					 
					 return null;
				}
				
				 //ask user if the operation is too long
				if(PaUtils.askAboutLongOperation(rec.width,rec.height, m_parent.getWindow())
						 == JOptionPane.NO_OPTION) {
					
						    return null;
				}
			
				BufferedImage fullImage = PaUtils.deepCopy(sourceImage);
				
				//be careful - the raster data are shared with raster data of  fullImage
				BufferedImage im = PaUtils.deepCopy(fullImage.getSubimage(rec.x, rec.y, rec.width, rec.height));
			
				if(im == null) {
					
					 writeLog("Can't find current image to use sharp instrument" , null, true, false, true);
					 
					 return null;
				}
				 switch(b.getFilterType()){
				 	
					 case CONTOUR: {
						 
						ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,
								getMessagesStrs("sharpLongOperationCaption")+" : " +
								getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
								+ "x"+im.getHeight(),getMessagesStrs("startSharpOperationCaption"), 0, 100);
						
						progressMonitor.setMillisToDecideToPopup(0);
						
						progressMonitor.setMillisToPopup(0);
							
						PaSharpOperation ts = new PaSharpOperation(progressMonitor,
								(double)b.getLevel(),b.S_MAX, b.S_MIN, im,fullImage,
								rec,true,m_parent.getWindow());
								
						ts.execute(); 
						
						break;
					 }
					 
					 case SOBEL_LAPLAS : {
						 
						 	BufferedImage resultImage = null;
						 	
							PaAlgorithms al = new PaAlgorithms();
							
							resultImage =  (BufferedImage)al.sharpIncrease(im,b.getFilterType(),b.getLevel());
							 
							fullImage.getRaster().setRect(rec.x, rec.y, resultImage.getRaster());
							
							m_parent.getWindow().setResultView(fullImage, getGuiStrs("sharpInstrumentName"));
							
						 break;
					 }
					 
					 default :
						 
					 case HIFR_GAUSS : {
						 
						ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,
							getMessagesStrs("sharpLongOperationCaption")+" : " +
							getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
							+ "x"+im.getHeight(),getMessagesStrs("startSharpOperationCaption"), 0, 100);
					
						progressMonitor.setMillisToDecideToPopup(0);
						
						progressMonitor.setMillisToPopup(0);
							
						PaSharpOperation ts = new PaSharpOperation(progressMonitor,
								(double)b.getLevel(),b.S_MAX, b.S_MIN, im,fullImage,
								rec,false,m_parent.getWindow());
								
						ts.execute();
						
						break;
					 }
				 };
			}
			catch(OutOfMemoryError ex) {
				
				JOptionPane.showMessageDialog(
					    null,
					    getMessagesStrs("outOfMemoryMessage"),
					    getMessagesStrs("messageCaption"),
					    JOptionPane.OK_OPTION);
				
				writeLog("Not enough memory: can't get result image for sharp instrument" + NEXT_ROW +
						ex.getMessage(), null,
						true, false, false );
			}
			finally {
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				writeLog("Instruments window: sharp instrument operation finished.", null,
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
		m_parent.setInstrument(PaInstrumentTypeEnum.Sharp,m_button);
		
		m_button.m_mainButton.setSelected(true);
				
	}
	
	/**
	 * 
	 * @return the cursor for this instrument;
	 */
	public Cursor getCursor() {
		
		return Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(PaUtils.get().getIconsPath()+ "handsharp.png").getImage(),
				new Point(6,1),"custom cursor");
		
	}
}
