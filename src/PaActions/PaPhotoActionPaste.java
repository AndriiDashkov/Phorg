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
 * Ñopy image objects from buffer
 * @author avd
 *
 */
public class PaPhotoActionPaste extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionPaste() {
		
		super("paimageactionpaste");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paphotopaste.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION,  getMenusStrs("insertMenuToolTipName")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("insertMenuName")); 
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("pasteAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));

		setEnabled(false);
	}
	
	public void enableAction (PaEventEnable e) {
		
		if(e.getType() == PaEventEnable.TYPE.COPY_BUFFER_EMPTY) {
			
			setEnabled(false);
			
		}
		if(e.getType() == PaEventEnable.TYPE.COPY_BUFFER_ACTIVATED) {
			
			setEnabled(true);
		}
			
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_PASTE_EVENT) );
	}	
}
