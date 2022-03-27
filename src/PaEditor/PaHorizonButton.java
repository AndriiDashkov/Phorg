
package PaEditor;


import java.awt.Dimension;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaUtils;
import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.*;


/**
 * @author avd
 * <p>This class determines the complex button for horizon operation; the special parameters panel is
 *  created here</p>
 */
public class PaHorizonButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	
	/**
	 * @param parent - parent link to window class where the instrument works
	 * @param size - size of this button panel
	 */
	public PaHorizonButton(PaInstrumentsWindow parent,Dimension size, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_HORIZON, parent,new ImageIcon(PaUtils.get().getIconsPath() +
				 "pahorizon.png"), size, true, hash);//true - toggle button
		
		m_menuButton.setEnabled(false);
		
		m_menuButton.setVisible(false);
		
		m_mainButton.setToolTipText(getMenusStrs("horizonToolTip"));
		

	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 * <p>For horizon operation there is no need for special parameters - so there is no special panel </p>
	 */
	protected JDialog createSpecialPanel() {
		
		return null;
		
	}
	
	/**
	 * <p>Starts horizon instrument</p>
	 */
	protected void startInstrumentImpl() {
		
		if ( m_mainButton.isSelected() ) {
			

			writeLog("Instruments window: horizon operation started.", null, true, false, false );
			
			m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.Horizon,this);
			
		}
		else {
			m_parent.getWorkPanel().resetInstrument();
		}
			
	}
}
