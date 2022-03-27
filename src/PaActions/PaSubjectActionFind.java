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
 * Action for finding a subject.
 * Subjects are notion used to unite images in the albums.
 * @author avd
 *
 */
public class PaSubjectActionFind extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaSubjectActionFind() {
		
		super("patemactionfind");
	
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "patermfind.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("findSubMenuToolTip"));
		
		putValue(NAME, getMenusStrs("findSubMenuName"));
	}
	
	public void enableAction(PaEventEnable e){
		
	
		switch(e.getType()) {

			case SUBJECT_LIST_EMPTY : { setEnabled(false); break; }		
			
			case SUBJECT_LIST_NOT_EMPTY : {  setEnabled(true); break; } 
			
			default:
		}
	}
	public void actionPerformed(ActionEvent event) {

		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.SUBJECT_FIND_EVENT) );
	}		
}
