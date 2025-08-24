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
 * @author Andrii Dashkov
 *<p>Delete a subject action. Subjects are notion used to unite images in the albums.</p>
 */
public class PaSubjectActionDel extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaSubjectActionDel() {
		
		super("patemactiondel");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "patermdel.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("delSubMenuToolTip"));
		
		putValue(NAME, getMenusStrs("delSubMenuName"));
		
		setEnabled(false);

	}
	
	public void enableAction(PaEventEnable event) {
		
		switch(event.getType()) {
		
			case SUBJECT_NOT_SELECTED : 
				
			case SUBJECT_LIST_EMPTY : { setEnabled(false); break; }
		
			case SUBJECT_SELECTED_1 : {  setEnabled(true); break; } 
			
			default:
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SUBJECT_DEL_EVENT) );
	}		
}
