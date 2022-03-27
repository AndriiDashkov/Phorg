
package PaEditor;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getMenusStrs;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaUtils;

/**
 * @author avd
 * <p>This class determines the complex button for zoom operation; 
 * the special parameters panel creates here</p>
 */
public class PaZoomRectButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaZoomRectButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_ZOOM_RECT, 
				parent,new ImageIcon(PaUtils.get().getIconsPath() + "pazoomrect.png"), d, true, hash);

		setToolTips();
		
		m_menuButton.setEnabled(false);
		
		m_menuButton.setVisible(false);
		
	}
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return null;
		
	}
	
	/**
	 * Sets tooltips
	 */
	private void setToolTips() {
		
		m_mainButton.setToolTipText(getMenusStrs("zoomAreaButtonToolTip"));	
	}

	/**
	 * <p>Starts contrast instrument</p>
	 */
	protected void startInstrument() {
		
		if ( m_mainButton.isSelected() ) {
			
			writeLog("Instruments window: zoom rectangle instrument operation started.", null, true,
					false, false );
			
			m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.ZoomRect,this);
			
		}
		else { 
			m_parent.getWorkPanel().resetInstrument();
		}	
	}	
}