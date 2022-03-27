
package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * @author avd
 *<p>Removes selected subjects</p>
 */
public class PaSubjectsSelectedRemove extends PaAction {

	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaSubjectsSelectedRemove() {
		
		super("pasubselectedremove");
	
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("removeSubjectsToImageMenuToolTip")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("removeSubjectsToImageMenuName"));
		
		setEnabled(false);
	}
	
	/**
	 * @param event - enable event
	 *<p>enables the action only for selection case</p>
	 */
	public void enableAction(PaEventEnable event) {
		
		switch(event.getType()) {
		
			case NO_SELECTED_IMAGES : { setEnabled(false); break; }
			
			case MULTISELECTED_IMAGES :	
				
			case SINLGE_IMAGE_SELECTED : { setEnabled(true); break; }
			
			default : 
	
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().removeSubjectInSelectedImages();
	}
	
}