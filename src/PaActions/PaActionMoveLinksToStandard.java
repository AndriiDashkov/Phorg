/**
 * 
 */
package PaActions;


import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * @author avd
 * <p>Moves all linked images to the standard albom's folder</p>
 */
public class PaActionMoveLinksToStandard extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaActionMoveLinksToStandard() {
		
		super("paactionmovelinkstostand");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "pamovestn.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("moveStandardToolTipName")); 
		
		putValue(NAME, getMenusStrs("moveStandardMenuName"));

		setEnabled(false);
	}
	
	public void enableAction(PaEventEnable e){

		switch(e.getType()) {
		
			case ALBUM_SELECTION_EMPTY :
				
			case ALBUM_SELECTED_2  :
				
			case ALBUM_LIST_IS_EMPTY :
				
			case ALBUM_MULTI_SELECTION : { setEnabled(false); break; }	
			
			case ALBUM_SELECTED_1 : 	 { setEnabled(true); break; }
			
			default : 
		
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.MOVE_TO_STANDARD_FOLDER));
	}		
}