package PaActions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;

import static PaGlobal.PaUtils.*;

/**
 * <p>Opens properties window for an image</p>
 * @author avd
 *
 */
public class PaPhotoActionProperties extends PaAction {
	
	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
			
	public PaPhotoActionProperties() {
		
		super("paimageactionproperties");
		
		putValue(AbstractAction.SMALL_ICON, null); 
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("propPhotoToolTipName")); 
		
		putValue(NAME,  getMenusStrs("propPhotoMenuName")); 
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("propImageAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
	}
	
	public void enableAction (PaEventEnable event) {
		
		
		switch(event.getType()) {
		
			case NO_SELECTED_IMAGES :	
				
			case MULTISELECTED_IMAGES :	
				
			case IMAGE_LIST_IS_EMPTY :	
				
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
			
		
			case SINLGE_IMAGE_SELECTED : { setEnabled(true); break; }
			
			default : 
	
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.IMAGE_PROP_EVENT));
	}
}
