/**
 * 
 */
package paactions;

import static paglobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;
import paundoredo.PaUndoRedoDeque;

/**
 * Redo action. Precaution: not all user actions can be redone.
 * @author Andrii Dashkov
 *
 */
public class PaRedoAction extends PaAction {

	private static final long serialVersionUID = 1L;
	{
		
	}


	public PaRedoAction() {
		
		super("paredoaction");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paredosmall.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("redoButtonToolTip")); 
		
		putValue(NAME, getMenusStrs("redoButtonName")); 
		
		setEnabled(false);
	}


	public void enableAction(PaEventEnable e) {
		
		if(e.get_EventType() != PaEventDispatcher.ENABLE_EVENT) return;
		
		if(e.getType() == PaEventEnable.TYPE.REDO_ENABLED) { setEnabled(true); } 
		
		if(e.getType() == PaEventEnable.TYPE.REDO_DISABLED) { setEnabled(false);}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.REDO_EVENT) );
		
		if(PaUndoRedoDeque.get().isRedoEmpty()) { 
			
			setEnabled(false); 
		} 
		else { 
			
			setEnabled(true);
		}
	}

}
