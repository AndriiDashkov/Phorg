/**
 * 
 */
package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEventDispatcher; 
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;


/**
 * 
 * @author avd
 *<p>Image rotation action</p>
 */
public class PaImageRotateRight extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaImageRotateRight() {
		
		super("paimageactionrtright");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paturnimageright.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("rotateImageRightMenuToolTip")); 
		
		putValue(NAME,getMenusStrs("rotateRightMenuName")); 

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
			
		PaUtils.get().getViewPanel().rotateSelectedImages(true);
	}		
}
