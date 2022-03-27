package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import static PaGlobal.PaUtils.*;


public class PaPhotoActionCopyIn extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionCopyIn() {
		
		super("paimageactioncopyin");

		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("copyInThePlaceToolTip"));
		
		putValue(NAME,  getMenusStrs("copyInThePlaceMenuName"));//Copy to folder
	}
	
	public void enableAction(PaEventEnable e) 
	{	
	
		switch(e.getType()) {
		
			case NO_SELECTED_IMAGES :
				
			case IMAGE_LIST_IS_EMPTY :
				
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
					
			case SINLGE_IMAGE_SELECTED : 
				
			case MULTISELECTED_IMAGES : { setEnabled(true); break; }
			
			default : 
	
		}
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.COPY_IN_EVENT) );
	}		
}