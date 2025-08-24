package paactions;

import static paglobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * 
 * @author Andrii Dashkov
 *
 */
public class PaAlbumActionMove extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaAlbumActionMove() {
		
		super("paalbomactionmove");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbommove.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION,getMenusStrs("moveAlbomMenuToolTip"));
		
		putValue(NAME, getMenusStrs("moveAlbomMenuName"));

		setEnabled(false);
	}
	/**
	 * <p>Does enable or disable of the action according to last operation</p>
	 * @param event - enable event which has the information about last operation
	 * @throws InvocationTargetException
	 */
	public void enableAction (PaEventEnable event) {
				
		switch(event.getType()) {
		
			case ALBUM_LIST_IS_EMPTY :
				
			case ALBUM_SELECTED_2 :
				
			case ALBUM_SELECTION_EMPTY :
				
			case ALBUM_MULTI_SELECTION :  { setEnabled(false); break;}
		
			case ALBUM_SELECTED_1: { setEnabled(true); break; }
			
			default:
	
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_MOVE_EVENT));
	}		
}
