package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * <p>Action for album's find operation</p>
 * @author Andrii Dashkov
 *
 */
public class PaAlbumActionFind extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaAlbumActionFind() {
		
		super("paalbomactionfind");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbomfind.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("findAlbomMenuToolTipName")); 
		
		putValue(NAME, getMenusStrs("findAlbomMenuName"));
	}
	
	/**
	 * <p>Does enable or disable of the action according to the last operation</p>
	 * @param event - enable event which has the information about the last operation
	 * @throws InvocationTargetException
	 */
	public void enableAction (PaEventEnable event) {
		
		switch(event.getType()) {
		
			case ALBUM_LIST_IS_EMPTY : { setEnabled(false); break; }
			
			case ALBUM_LIST_IS_NOT_EMPTY : { setEnabled(true); break; }
			
			default :
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_FIND_EVENT) );
	}		
}