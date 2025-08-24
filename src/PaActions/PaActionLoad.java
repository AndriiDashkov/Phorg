package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * 
 * @author Andrii Dashkov
 *<p>The action for loading images operations</p>
 */
public class PaActionLoad extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaActionLoad() {
		
		super("paactionload");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbomopen.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("imageLoadingToolTip")); 
		
		putValue(NAME, getMenusStrs("imabeLoadingMenuName")); 
		
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("loadAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
		
		setEnabled(false);

	}
	
	public void enableAction (PaEventEnable e) {
		
		switch(e.getType()) {
		
			case ALBUM_SELECTION_EMPTY :
				
			case ALBUM_SELECTED_2  :
				
			case ALBUM_MULTI_SELECTION : { setEnabled(false); break; }	
			
			case ALBUM_SELECTED_1 : 	 { setEnabled(true); break; }
			
			default : 
		
		}
	}
	

	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.REFRESH_EVENT) );
		
		setEnabled(false);
		
		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.ALBUM_LOADED));
		
	}
	
}