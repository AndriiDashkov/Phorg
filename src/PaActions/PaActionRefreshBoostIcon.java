/**
 * 
 */
package PaActions;

/**
 * @author Andrey Dashkov
 *
 */
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * Action to refresh the single boost icons in view panel
 * @author Andrey
 *
 */
public class PaActionRefreshBoostIcon extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaActionRefreshBoostIcon() {
		
		super("paimagerefreshboosticon");
	
		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("refreshBoostIconToolTip"));
		
		putValue(NAME,  getMenusStrs("refreshBoostIconMenuName"));
	}
	
	public void enableAction (PaEventEnable eventPhoto) {
		
		PaImageContainer c = PaUtils.get().getMainContainer().getCurrentContainer(); 
		
		if (c != null && c.size() > 0 ) {
		
			setEnabled(true);
				
		} else {
			
			setEnabled(false);
				
		}
	}
	
	public void actionPerformed(ActionEvent e) {
			
		ArrayList<PaImage> list =PaUtils.get().getViewPanel().getSelectedItems();
		
		if(list.size() == 1) {//for single selection only
			
			PaUtils.get().getViewPanel().refreshBoostIcon(list.get(0).getId());
			
		}
	}		
}