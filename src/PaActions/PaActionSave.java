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
 * <p>Save action</p>
 * @author avd
 *
 */
public class PaActionSave extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaActionSave() {
		
		super("paactionsave");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "pasave.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("saveOperationToolTip"));
		
		putValue(NAME,  getMenusStrs("saveOperationMenuName") );  
		
		setEnabled(false);

	}
	
	public void enableAction(PaEventEnable e) {
		
		switch(e.getType()) {
		
			case DATA_SAVED : { setEnabled(false); break; }	
			
			case DATA_CHANGED:   { setEnabled(true); break; }
			
			default : 
		
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.SAVE_EVENT) );
		
		setEnabled(false);
				
	}
	
}