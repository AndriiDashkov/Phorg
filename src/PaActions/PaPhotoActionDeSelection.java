package PaActions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventInt;
import PaEvents.PaEventSelect;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;


/**
 * 
 * @author avd
 *
 */
public class PaPhotoActionDeSelection extends PaAction {
	
	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
			
	public PaPhotoActionDeSelection() {
		
		super("paimageactiondeselect");
	
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "padeselect.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("deselectMenuToolTip")); 
		
		putValue(NAME,getMenusStrs("deselectMenuName"));
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("deSelectImageAccelerator").charAt(0)), 
				ActionEvent.ALT_MASK));
	}
	
	public void enableAction(PaEventEnable eventPhoto) {
		
		if (PaUtils.get().getSelectedImages().size() > 0) {
			
			setEnabled(true);
			
		} else {
			
			setEnabled(false);
			
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventInt eventSelect = new PaEventSelect();
		
		PaEventDispatcher.get().fireCustomEvent(eventSelect);
	}
}