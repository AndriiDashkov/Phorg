
package PaEditor;

import static PaGlobal.PaLog.writeLog;
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
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaUtils;
import PaLong.PaBlurHiFOperation;
import PaLong.PaBlurOperation;

/**
 * @author Andrey Daskov
 * Instrument for the blur operation
 *
 */
public class PaBlurInstrument extends  PaZoomRectInstrument {

	 int m_currentCursorType;
	/**
	 * @param parent - parent instruments panel
	 * @param but - instrument button, which launches the instrument
	 */
	public PaBlurInstrument(PaInstrumentsPanel parent, PaBlurButton but) {
		
		super(parent, but);
	
	}
	
	/**
	 * @return the image after instrument has been used; gets the end result of the instrument using 
	 */
	@Override
	public Image getResultView(MouseEvent e,float xScale, float yScale, BufferedImage sourceImage) {
		
		if(m_startPoint != null) {
			
				isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView()
				
				Point sPoint = m_startPoint;
				
				PaBlurButton b = (PaBlurButton)m_button;
				
				m_currentCursorType = PaUtils.get().getCurrentCursor();
			
				SwingUtilities.convertPointToScreen(sPoint, m_parent);
				
				SwingUtilities.convertPointFromScreen(sPoint, m_parent.m_scrollPane.getMainLabel());
				
				Rectangle rec = new Rectangle(sPoint,new Dimension(m_rect.width,m_rect.height));
				
				rec = m_parent.convertFromInstrumentRectToImageRect(rec, true);
			
				if(rec.width <= 0 ||  rec.height <= 0 ) {
					
					writeLog("Selection: can't perform operation for zero size", null,
							true, false, false );
					
					return null;
				}
				
				if(PaUtils.askAboutLongOperation( rec.width,rec.height, m_parent.getWindow())
						 == JOptionPane.NO_OPTION) {
					
							return null;
				 }
				
				BufferedImage fullImage = PaUtils.deepCopy(sourceImage);

				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
		
					
				if(b.isAvarageFilterChosen() ) {
					
					BufferedImage im = PaUtils.deepCopy(sourceImage.getSubimage(rec.x, rec.y, rec.width, rec.height));
					
					 //start long operation because blur can take minutes
					ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,getMessagesStrs("blurLongOperationCaption")+" : " +
								getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
								+ "x"+im.getHeight(),
								getMessagesStrs("startBlurOperationCaption"), 0, 100);
					
						
					progressMonitor.setMillisToDecideToPopup(0);
					
					progressMonitor.setMillisToPopup(0);
					
					PaBlurOperation ts = new PaBlurOperation(progressMonitor,
							 b.getLevel(), im, fullImage, rec, false, m_parent.getWindow(), false);
						
					ts.execute();	
				}
				else {
					//here we create extra boundary around selected area: the idea to avoid
					//black boundary moire after DFT
					int delta = (int)(rec.width*0.1);
					
					Rectangle extraRec = new Rectangle(rec);
					
					extraRec.x -= delta; extraRec.y -= delta;
					
					extraRec.width += 2*delta; extraRec.height += 2*delta;
					
					extraRec = sizeControl(extraRec,fullImage.getWidth(),fullImage.getHeight());
					
					BufferedImage im = PaUtils.deepCopy(sourceImage.getSubimage(extraRec.x, extraRec.y, extraRec.width, extraRec.height));
					
					 //start long operation because blur can take minutes
					ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,getMessagesStrs("blurLongOperationCaption")+" : " +
								getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
								+ "x"+im.getHeight(),
								getMessagesStrs("startBlurOperationCaption"), 0, 100);
						
					progressMonitor.setMillisToDecideToPopup(0);
					
					progressMonitor.setMillisToPopup(0);
					
					PaBlurHiFOperation ts = new PaBlurHiFOperation(progressMonitor,
							 b.getLevel(), b.MAX_SL,b.MIN_SL, im,fullImage, 
							 extraRec,rec,m_parent.getWindow());
						
					ts.execute();	
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
			
		//reset();
	    m_parent.resetInstrument();
	    
	    //start the instrument again - it is active until the user
		m_parent.setInstrument(PaInstrumentTypeEnum.Blur,m_button);
		
		m_button.m_mainButton.setSelected(true);
				
	}
	
	/**
	 * 
	 * @return the cursor for this instrument;
	 */
	public Cursor getCursor() { 
		
		return Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(PaUtils.get().getIconsPath()+ "handblur.png").getImage(),
				new Point(6,1),"custom cursor");
			
	}
	/**
	 * Controls the size of rectangle rec
	 * @param rec - rectangle to control
	 * @param w - boundary width (usually full image width)
	 * @param h - boundary height (usually full inage height)
	 * @return the rectangle that is strictly inside rectangle 0,0,w,h
	 */
	private Rectangle sizeControl(Rectangle rec,int w,int h) {
		
		Rectangle r = new Rectangle(rec);
		
		if(r.x < 0) r.x = 0;
		
		if(r.y < 0) r.y = 0;
		
		if((r.x+r.width) > w ) r.width = w - r.x;
		
		if((r.y+r.height) > h ) r.height = h - r.y;
		
		return r;
		
	}

}
