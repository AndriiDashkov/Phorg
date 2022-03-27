package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * <p>Invokes ROI instruments window</p>
 * @author Andrey Dashkov
 *
 */
public class PaRoiAction extends PaAction {
	
	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
			
	public PaRoiAction() {
		
		super("paroisaction");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "painstrumentsroi.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("roiMenuToolTip")); 
		
		putValue(NAME,getMenusStrs("roiMenuName")); 
			
	}
	
	/**
	 * <p>Does enable or disable of the action according to the last operation</p>
	 * @param event - enable event which has the information about the last operation
	 */
	public void enableAction (PaEventEnable event) {
		
		
		switch(event.getType()) {
		
			case NO_SELECTED_IMAGES:
				
			case MULTISELECTED_IMAGES:
				
			case IMAGE_LIST_IS_EMPTY :
				
			case SELECT_CONTAINER_IS_EMPTY :  { setEnabled(false); break;}
			
			case SINLGE_IMAGE_SELECTED: { setEnabled(true); break; }
			
			default:

		}
		
	}
	public void actionPerformed(ActionEvent e) {
				
		PaUtils.get().getViewPanel().openRoiInstrumentsWindow();
		
	}
}
