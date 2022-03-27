package PaActions;

import static PaGlobal.PaUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;

/**
 * <p>Action for edit album operation</p>
 * @author avd
 *
 */
public class PaAlbumActionEdit extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaAlbumActionEdit() {
		
		super("paalbomactionedit");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbomedit.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION,  getMenusStrs("editAlbomToolTip"));
		
		putValue(NAME,  getMenusStrs("editAlbomMenuName"));
			
		setEnabled(false);
	}
	
	/**
	 * <p>Does enable or disable of the action according to last operation</p>
	 * @param event - enable event which has the information about last operation
	 * @throws InvocationTargetException
	 */
	public void enableAction (PaEventEnable event) {
		
		switch(event.getType()) {
		
			case ALBUM_SELECTION_EMPTY : 
				
			case ALBUM_SELECTED_2 :
				
			case ALBUM_MULTI_SELECTION  :
				
			case ALBUM_LIST_IS_EMPTY : { setEnabled(false); break; }
		
			case ALBUM_SELECTED_1 : {  setEnabled(true); break; } 
			
			default:
	
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_EDIT_EVENT) );
	}		
}

