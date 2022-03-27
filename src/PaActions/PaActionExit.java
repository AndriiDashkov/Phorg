/**
 * 
 */
package PaActions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * @author avd
 *
 */
class PaActionExit extends PaAction {
	
	private static final long serialVersionUID = 1L;

	PaActionExit() {
		
		super("paactionexit");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paexit.png"));
		
		putValue(NAME, getMenusStrs("exitMenuName"));
		
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("exitAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
	}
	public void actionPerformed(ActionEvent e) {
		
		AbstractAction act = PaActionsMngr.get().getAction("paactionsave");
		
		//to give a last chance to save something
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT));
		
		if (act.isEnabled() == true) {		
			
			int response = JOptionPane.showConfirmDialog(null, PaUtils.getMessagesStrs("saveChangesQuestion"), 
					PaUtils.getMessagesStrs("programExitDialogCaption"), JOptionPane.YES_NO_CANCEL_OPTION);
			
			switch (response) {
		
				case JOptionPane.YES_OPTION: {

					PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SAVE_EVENT));
					
				
					PaUtils.get().getMainWindow ().dispose();
					
					System.exit(0);
				}
		
				case JOptionPane.NO_OPTION: {
					

					PaUtils.get().getMainWindow ().dispose();
					
					System.exit(0);
				}
				
				case JOptionPane.CANCEL_OPTION: {
					
					break;
				}
					
				case JOptionPane.CLOSED_OPTION: {
					
					System.out.print("CLOSED");
					
					break;
				}
			}
		} else {
			
			PaUtils.get().getMainWindow ().dispose();
			
			System.exit(0);				
		}
	}
}
