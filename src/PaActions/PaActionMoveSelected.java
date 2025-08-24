/**
 * 
 */
package paactions;

import static paglobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 *<p>Action for move of selected images to other albom</p>
 */
public class PaActionMoveSelected extends PaAction {


	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaActionMoveSelected() {
		
		super("paactionmoveselected");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "pacopyselecttoalbom.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("moveSelectedToolMenuTip")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("moveSelectedMenuName"));
		
		setEnabled(false);
	}
	
	/**
	 * @param event - enable event
	 *<p>enables the action only for multiselection case</p>
	 */
	public void enableAction(PaEventEnable e) {
		
		switch(e.getType()) {
		
			case NO_SELECTED_IMAGES :
				
			case SINLGE_IMAGE_SELECTED  : { setEnabled(false); break; }	
			
			case MULTISELECTED_IMAGES :   { setEnabled(true); break; }
			
			default : 
		
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().moveSelectedImagesToAlbum();
	}
	
}
