/**
 * 
 */
package PaActions;

import static PaGlobal.PaUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;

/**
 * @author avd
 *<p>Creation new album action for selected images only</p>
 */
public class PaActionAlbomForSelection extends PaAction {


	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaActionAlbomForSelection() {
		
		super("paactionalbomforselection");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paselectnewalbom.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("albomNewSelectionToolMenuTip")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("albomNewSelectionMenuName"));
		
		setEnabled(false);
	}
	
	/**
	 * @param event - enable event
	 *<p>enables the action only for multiselection case</p>
	 */
	public void enableAction(PaEventEnable event) {
		
		
		if (event.getType() == PaEventEnable.TYPE.MULTISELECTED_IMAGES) {
			
			setEnabled(true);
			
		} else {
			
			if ((event.getType() == PaEventEnable.TYPE.NO_SELECTED_IMAGES) || 
					(event.getType() == PaEventEnable.TYPE.SINLGE_IMAGE_SELECTED)) {
				
				setEnabled(false);
			}
		} 
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().createNewAlbumForSelectedItems();
	}
	
	
	
}