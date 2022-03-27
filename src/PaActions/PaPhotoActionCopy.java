package PaActions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * 
 * @author avd
 *
 */
public class PaPhotoActionCopy extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionCopy() {
		
		super("paimageactioncopy");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paphotocopy.png"));
		

		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("copyPhotoMenuToolTip")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("copyPhotoMenuName"));
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("copyAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
	}
	
	public void enableAction (PaEventEnable e) {
			
		switch(e.getType()) {
		
			case NO_SELECTED_IMAGES :
				
			case IMAGE_LIST_IS_EMPTY :
				
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
					
			case SINLGE_IMAGE_SELECTED : 
				
			case MULTISELECTED_IMAGES : { setEnabled(true); break; }
			
			default : 
	
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.COPY_BUFFER_EVENT) );
	}		
}