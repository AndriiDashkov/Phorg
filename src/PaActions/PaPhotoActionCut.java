package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * 
 * @author Andrii Dashkov
 *<p> Cut image action</p>
 */
public class PaPhotoActionCut extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionCut() {
		
		super("paimageactioncut");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paphotocut.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("cutMenuToolTipName"));
		
		putValue(AbstractAction.NAME,  getMenusStrs("cutMenuName"));
	}
	
	public void enableAction (PaEventEnable e) {
		
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
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.IMAGE_CUT_EVENT) );
	}		
}	

