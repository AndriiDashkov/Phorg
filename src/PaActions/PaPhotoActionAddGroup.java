package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paevents.PaEventInt;
import paevents.PaEventPhotoGroupNew;
import paglobal.PaUtils;

/**
 * 
 * @author Andrii Dashkov
 * <p>Action for dialog off group images adding</p>
 */
public class PaPhotoActionAddGroup extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionAddGroup() {
		
		super("pactionaddgroup");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paaddpackphoto.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("addFotoPackToolTip"));
		
		putValue(NAME,  getMenusStrs("addFotoPackMenuName"));
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("addGroupAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
		
		setEnabled(false);

	}
	/**
	 * <p>Does enable or disable of the action according to last operation</p>
	 * @param event - enable event which has the information about last operation
	 */
	public void enableAction (PaEventEnable event) {
		
		switch(event.getType()) {
		
			case SELECT_CONTAINER_IS_EMPTY :
				
			case ALBUM_LIST_IS_EMPTY : { setEnabled(false); break; }
			
			case IMAGE_LIST_NOT_EMPTY :
				
			case IMAGE_LIST_IS_EMPTY :
				
			case ALBUM_LOADED : { setEnabled(true); break; }
			
			default : 
		}

	}

	
	public void actionPerformed(ActionEvent e) {
		
		PaEventInt newPhotoGroupEvent = new PaEventPhotoGroupNew();
		
		PaEventDispatcher.get().fireCustomEvent(newPhotoGroupEvent);
	}		
}