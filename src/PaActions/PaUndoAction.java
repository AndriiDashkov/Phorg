
package paactions;

import static paglobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
//import javax.swing.Icon;
import javax.swing.ImageIcon;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paglobal.PaUtils;
import paundoredo.PaUndoRedoDeque;

/**
 * Undo action. Not all actions can be undone.
 * @author Andrii Dashkov
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
