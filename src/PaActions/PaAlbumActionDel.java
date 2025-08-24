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
 * <p>Deletes an album </p>
 * @author Andrii Dashkov
 *
 */
public class PaAlbumActionDel extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaAlbumActionDel() {
		
		super("paalbomactiondel");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbomdel.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("cleanOrDeleteAlbomToolTipMenuName")); 
		
		putValue(NAME, getMenusStrs("cleanOrDeleteAlbomMenuName"));

		setEnabled(false);
	}
	
	/**
	 * <p>Does enable or disable of the action according to last operation</p>
	 * @param event - enable event which has the information about last operation
	 * @throws InvocationTargetException
	 */
	public void enableAction(PaEventEnable event) {
		
		switch(event.getType()) {
		
			case ALBUM_SELECTION_EMPTY : 
				
			case ALBUM_SELECTED_2 :
				
			case ALBUM_MULTI_SELECTION  :
				
			case ALBUM_LIST_IS_EMPTY : { setEnabled(false); break; }
		
			case ALBUM_SELECTED_1 : {  setEnabled(true); break; } 
			
			default:
		
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.ALBUM_DEL_EVENT) );
	}		
}
