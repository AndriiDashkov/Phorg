package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * 
 * @author avd
 *<p>Delete image action</p>
 */
public class PaPhotoActionDel extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionDel() {
		
		super("paimageactiondel");
	
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "padeletephoto.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("deletePhotoMenuToolTip"));
		
		putValue(NAME,getMenusStrs("deletePhotoMenuName")); 

	}
	
	public void enableAction(PaEventEnable e) {
		
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
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_DEL_EVENT) );
	}		
}	
