package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * 
 * @author Andrii Dashkov
 *<p>Add new image action</p>
 */
public class PaPhotoActionNew extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionNew() {
		
		super("paimageactionnew");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "panewphoto.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("addFotoMenuToolTip"));
		
		putValue(NAME, getMenusStrs("addFotoMenuName"));
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("addImageAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));

	}
	
	public void enableAction(PaEventEnable event) {
		
		switch(event.getType()) {
		
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
			
			case IMAGE_LIST_IS_EMPTY :	
				
			case IMAGE_LIST_NOT_EMPTY : { setEnabled(true); break; }
			
			default : 
	
		}

		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.IMAGE_NEW_EVENT) );
	}		
}