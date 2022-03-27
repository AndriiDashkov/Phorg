package PaActions;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * <p>Merge of albums</p>
 * @author avd
 *
 */
public class PaAlbumActionMerge extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaAlbumActionMerge() {
		
		super("paalbomactionmerge");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbommerge.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("mergeAlbomsMenuToolTipName")); 
		
		putValue(NAME, getMenusStrs("mergeAlbomsMenuName"));

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
				
			case ALBUM_SELECTION_EMPTY :
				
			case ALBUM_SELECTED_1 :	{ setEnabled(false); break; }
			
			case ALBUM_MULTI_SELECTION : 
				
			case ALBUM_SELECTED_2 : { setEnabled(true); break; }
			
			default : 
		
		}

	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_MERGE_EVENT));
	}		
}