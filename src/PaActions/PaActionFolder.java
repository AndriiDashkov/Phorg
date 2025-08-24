/**
 * 
 */
package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 *
 */
public class PaActionFolder extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaActionFolder() {
		
		super("paactionfolder");
		
		//icon for action
		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("openFolderForImageToolTip")); 
		
		putValue(NAME,getMenusStrs("openFolderForImageMenuName"));
		
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("folderAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
		
	}
	
	public void enableAction (PaEventEnable e) {
		
		switch(e.getType()) {
		
			case NO_SELECTED_IMAGES :
				
			case MULTISELECTED_IMAGES :
				
			case IMAGE_LIST_IS_EMPTY :	
				
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }	
			
			case SINLGE_IMAGE_SELECTED : 	 { setEnabled(true); break; }
			
			default : 
		
		}
		
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		
		PaUtils.get().getViewPanel().openSystemFolder();
	}		
}
