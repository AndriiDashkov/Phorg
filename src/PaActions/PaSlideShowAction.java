package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * <p>Starts the slider window</p>
 * @author Andrii Dashkov
 *
 */
public class PaSlideShowAction extends PaAction {
	
	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public  PaSlideShowAction() {
		
		super("paslideshowaction");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paslider.png"));

		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("slideShowMenuNameToolTip"));
		
		putValue(NAME,  getMenusStrs("slideShowMenuName"));
		
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("slideAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
		
		setEnabled(false);
	}
	
	/**
	 * <p>Enables or disables action according to the type of the event e</p>
	 */
	public void enableAction (PaEventEnable e) {
		
		switch(e.getType()) {
		
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
				
			case IMAGE_LIST_NOT_EMPTY : { setEnabled(true); break; }
			
			default : 
	
		}
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().openSlideWindow();
	}		
}
