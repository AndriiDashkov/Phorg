/**
 * 
 */
package PaActions;

import static PaGlobal.PaUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;

/**
 * @author avd
 *<p>Action for properties dialog</p>
 */
public class PaAlbumActionProp extends PaAction {

	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaAlbumActionProp() 
	{
		
		super("paalbomactionprop");

		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbomprop.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION,getMenusStrs("propAlbomMenuToolTip"));
		
		putValue(NAME, getMenusStrs("propAlbomMenuName"));

		setEnabled(false);
	}
	
	/**
	 * <p>Does enable or disable of the action according to last operation</p>
	 * @param event - enable event which has the information about last operation
	 * @throws InvocationTargetException
	 */
	public void enableAction (PaEventEnable event)  
	{
			switch(event.getType()) {
			
				case ALBUM_LIST_IS_EMPTY :
					
				case ALBUM_SELECTION_EMPTY :
					
				case ALBUM_SELECTED_2 :	
					
				case ALBUM_MULTI_SELECTION : { setEnabled(false); break; }
				
				case ALBUM_SELECTED_1 : { setEnabled(true); break; }
				
				default : 
		
			}

	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().get_albomsForm().openPropertyDialog();
	}	
}
