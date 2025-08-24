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
 * <p>Save action</p>
 * @author Andrii Dashkov
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