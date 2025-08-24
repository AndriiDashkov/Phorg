package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import pacollection.PaImageContainer;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * <p>Selects all images in the loaded album</p>
 * @author Andrii Dashkov
 *
 */
public class PaPhotoActionSelect extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionSelect() {
		
		super("paimageactionselect");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paselect.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("selectAllMenuToolTip")); 
		
		putValue(NAME, getMenusStrs("selectAllMenuName")); 
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("selectImageAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
	}
	
	public void enableAction(PaEventEnable eventPhoto) {
		
		PaImageContainer c = PaUtils.get().getMainContainer().getCurrentContainer(); 
		
		if (c != null && 
				PaUtils.get().getSelectedImages().size() !=  c.size()) {
			
			setEnabled(true);
			
		} else {
			
			setEnabled(false);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SELECT_ALL_EVENT) );
	}		
}


