
package PaActions;

import static PaGlobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
//import javax.swing.Icon;
import javax.swing.ImageIcon;

import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
//import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import PaUndoRedo.PaUndoRedoDeque;

/**
 * Undo action. Not all actions can be undone.
 * @author avd
 *
 */
public class PaUndoAction extends PaAction {
	
	private static final long serialVersionUID = 1L;
	

	public PaUndoAction() {
		
		super("paundoaction");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paundosmall.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("undoButtonToolTip")); 
		
		putValue(NAME, getMenusStrs("undoButtonName")); 
		
		setEnabled(false);
	}


	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.UNDO_EVENT) );
		
		if(PaUndoRedoDeque.get().isUndoEmpty()) {setEnabled(false);} else {setEnabled(true);}
	}

}
