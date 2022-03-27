
package PaEditor;


import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getMenusStrs;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import PaGlobal.PaUtils;

/**
 * @author avd
 * <p>This class determines the complex button for zoom in/out operation; 
 * the special parameters panel creates here</p>
 */
public class PaZoomButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>Flag for instrument type - true for zoom in and false for zoom out</p>
	 */
	private boolean m_zoomIn =  true;
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 * @param inZoom - true if this button is 'zoom in' and false if this button is 'zoom out'
	 */
	public PaZoomButton(PaInstrumentsWindow parent,Dimension d, boolean inZoom, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_ZOOM, parent,new ImageIcon(PaUtils.get().getIconsPath() + "pazoomout.png"), d, false, hash);
		
		//false - not toggled button
		m_zoomIn = inZoom;
		
		if(inZoom) {
			
			m_mainButton.setIcon(new ImageIcon(PaUtils.get().getIconsPath() + "pazoomin.png"));
		}
		
		setToolTips();
		
		m_menuButton.setEnabled(false);
		
		m_menuButton.setVisible(false);
		
	}
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel.
	 * No special panels for zoom in/out instruments</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return null;
		
	}
	/**
	 * Sets tooltips
	 */
	private void setToolTips() {
		
		if(m_zoomIn) {
			
			m_mainButton.setToolTipText(getMenusStrs("instZoomInMenuToolTip"));
			
		}
		else {
			
			m_mainButton.setToolTipText(getMenusStrs("instZoomOutMenuToolTip"));
			
		}
	}

	/**
	 * <p>Starts zoom in/out instrument</p>
	 */
	protected void startInstrument() {
		
		writeLog("Instruments window: zoom operation started.", null, true, false, false );
		
		if(m_zoomIn) {
			
			m_parent.getWorkPanel().zoomIn();
			
		}
		else {
			
			m_parent.getWorkPanel().zoomOut();
		}
			
	}	
}