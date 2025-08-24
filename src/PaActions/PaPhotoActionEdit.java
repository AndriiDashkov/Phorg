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
 *
 */
public class PaPhotoActionEdit extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionEdit() {
		
		super("paimageactionedit");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paeditphoto.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("editPhotoMenuToolTip")); 
		
		putValue(NAME, getMenusStrs("editPhotoMenuName")); 
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("editPhotoActionAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));

	}
	
	/**
	 * <p>Does enable or disable of the action according to last operation</p>
	 * @param event - enable event which has the information about last operation
	 */
	public void enableAction (PaEventEnable event) {
		
		switch(event.getType()) {
		
			case NO_SELECTED_IMAGES : 
				
			case MULTISELECTED_IMAGES :
				
			case IMAGE_LIST_IS_EMPTY :
				
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
		
			case SINLGE_IMAGE_SELECTED : {  setEnabled(true); break; } 
			
			default:
		}
		

	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_EDIT_EVENT));
	}		
}

