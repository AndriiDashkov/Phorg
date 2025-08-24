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
import paevents.PaEventPhotoFilter;
import paglobal.PaUtils;

/**
 * <p>Clear filter action</p>
 * @author Andrii Dashkov
 *
 */
public class PaPhotoActionFilterClear extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT , this, "enablePhoto");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_FILTER_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionFilterClear() {
		
		super("paimageactionfilterclear");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paphotofilterclear.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("filterClearMenuToolTip")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("filterClearMenuName"));
		
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("filterResetAccelerator").charAt(0)), 
				ActionEvent.ALT_MASK));
	}
	
	public void enablePhoto (PaEventEnable eventPhoto) {
		
		if (eventPhoto.getType() == PaEventEnable.TYPE.ALBUM_LIST_IS_NOT_EMPTY) {
			
			setEnabled(true);
			
		} else {
			
			if (eventPhoto.getType() == PaEventEnable.TYPE.ALBUM_LIST_IS_EMPTY) {
				
				setEnabled(false);
			}
		}
		
		if ( eventPhoto.getType() == PaEventEnable.TYPE.SELECT_CONTAINER_IS_EMPTY ) {
			
			setEnabled(false);
		}
		else {
			
			if ( eventPhoto.getType() == PaEventEnable.TYPE.IMAGE_LIST_IS_EMPTY ||  
					eventPhoto.getType() == PaEventEnable.TYPE.IMAGE_LIST_NOT_EMPTY) {
				
				setEnabled(true);
			}
		}

	}
	
	public void enableAction (PaEventPhotoFilter event) {
		
		if (event instanceof  PaEventPhotoFilter ) {
				
			 PaEventPhotoFilter e = ( PaEventPhotoFilter) event;
			 
			 if ( e.getType() == PaEventPhotoFilter.TYPE.SETFILTER ) {
				 
				 setEnabled(true);
			 }	 
		}
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventPhotoFilter filterEvent = new PaEventPhotoFilter();
		
		filterEvent.setType(PaEventPhotoFilter.TYPE.CLEARFILTER);
		
		PaEventDispatcher.get().fireCustomEvent(filterEvent);
	}		
}