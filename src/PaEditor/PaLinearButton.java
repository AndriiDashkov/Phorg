
package paeditor;


import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

import paalgorithms.PaAlgorithms;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for linearization; the special parameters panel is
 *  created here</p>
 */
public class PaLinearButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	
	/**
	 * @param parent - parent link to window class where the instrument works
	 * @param size - size of this button panel
	 */
	public PaLinearButton(PaInstrumentsWindow parent,Dimension size, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_LINEAR, parent,new ImageIcon(PaUtils.get().getIconsPath() +
				 "palinearinst.png"), size, false, hash);//false - not toggle button
		
		m_menuButton.setEnabled(false);
		
		m_menuButton.setVisible(false);
		
		m_mainButton.setToolTipText(getGuiStrs("linearMainButtonToolTip"));
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 * <p>For linear operation there is no need for special parameters - so no special panel </p>
	 */
	protected JDialog createSpecialPanel() {
		
		return null;
		
	}
	
	/**
	 * <p>Starts linearization instrument</p>
	 */
	protected void startInstrumentImpl() {
		
		writeLog("Instruments window: linearization  started.", null, true,
				false, false );

		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
				m_parent.resetInstrument();
			
			 BufferedImage im = (BufferedImage) m_parent.getSourceViewImage();
			 if(im == null) {
				 writeLog("Can't find current image to use linear instrument" , null, true, false, true);
				 return;
			 }
			 BufferedImage resultImage = null;
			 
			 PaAlgorithms al = new PaAlgorithms();
			 
			 resultImage =  al.getLinearizationHSI(im);
			 
			 m_parent.setResultView(resultImage, getGuiStrs("linearInstrumentName"));
			
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window: linearization instrument operation finished.", null,
					true, false, false );
		}	
	}
}
