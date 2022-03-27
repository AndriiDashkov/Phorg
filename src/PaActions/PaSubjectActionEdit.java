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
 * Action for editing a subject.
 * Subjects are notion used to unite images in the albums.
 * @author avd
 *
 */
public class PaSubjectActionEdit extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaSubjectActionEdit() {
		
		super("pasunactionedit");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "patermedit.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("editSubMenuToolTip"));
		
		putValue(NAME, getMenusStrs("editSubMenuName"));

		setEnabled(false);
	}
	
	public void enableAction(PaEventEnable e) {
		
		switch(e.getType()) {
		
			case SUBJECT_NOT_SELECTED : 
				
			case SUBJECT_MULTISELECTED :
				
			case SUBJECT_LIST_EMPTY : { setEnabled(false); break; }
		
			case SUBJECT_SELECTED_1 : {  setEnabled(true); break; } 
			
			default:
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent( PaEventDispatcher.SUBJECT_EDIT_EVENT));
	}		
}
