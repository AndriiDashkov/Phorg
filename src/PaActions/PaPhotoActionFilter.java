package PaActions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventPhotoFilter;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;


public class PaPhotoActionFilter extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionFilter() {
		
		super("paimageactionfilter");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paphotofilter.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("filterMenuToolTip")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("filterMenuName"));
		
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("filterAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
	}
	
	
	public void enableAction(PaEventEnable e) {
		
		if (e.getType() == PaEventEnable.TYPE.ALBUM_LIST_IS_NOT_EMPTY) {
			
			setEnabled(true);
			
		} else {
			
			if (e.getType() == PaEventEnable.TYPE.ALBUM_LIST_IS_EMPTY) {
				
				setEnabled(false);
			}
		}
		
		if ( e.getType() == PaEventEnable.TYPE.SELECT_CONTAINER_IS_EMPTY ) {
			
			setEnabled(false);
		}
		else {
			if ( e.getType() == PaEventEnable.TYPE.IMAGE_LIST_IS_EMPTY ||  
					e.getType() == PaEventEnable.TYPE.IMAGE_LIST_NOT_EMPTY) {
				
				setEnabled(true);
			}
		}

	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEventPhotoFilter());
	}		
}	

