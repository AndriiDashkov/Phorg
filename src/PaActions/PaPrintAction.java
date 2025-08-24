/**
 * 
 */
package paactions;

import static paglobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import pacollection.PaImage;
import padialogs.PaPrintDialog;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * 
 * @author Andrii Dashkov
 *
 */
public class PaPrintAction extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPrintAction() {
		
		super("paprintaction");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paprint.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION,getMenusStrs("printActionMenuToolTip"));
		
		putValue(NAME, getMenusStrs("printActionMenuName"));

		setEnabled(false);
	}
	
	/**
	 * <p>Does enable or disable of the action according to last operation</p>
	 * @param event - enable event which has the information about last operation
	 * @throws InvocationTargetException
	 */
	public void enableAction (PaEventEnable event) {
				
		switch(event.getType()) {
		
			default:	
				
			case NO_SELECTED_IMAGES :
				
			case IMAGE_LIST_IS_EMPTY :
				
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
					
			case SINLGE_IMAGE_SELECTED : 
				
			case MULTISELECTED_IMAGES : { setEnabled(true); break; }
	
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		ArrayList<PaImage> selectedList  = PaUtils.get().getViewPanel().getSelectedItems(); 
		
		if(!selectedList.isEmpty()) {
			
			PaPrintDialog p = new PaPrintDialog( PaUtils.get().getMainWindow(), selectedList);
			
			p.setVisible(true);
		}
	}		
}
