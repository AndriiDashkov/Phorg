
package PaActions;

/**
 * @author avd
 *
 */

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

public class PaBookmarksResetAction extends PaAction {
	

	private static final long serialVersionUID = 1L;
	{

	}
	
	public   PaBookmarksResetAction() {
		
		super("paBookmarsReset");

		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("resetBookmarksMenuToolTip")); 
		
		putValue(NAME, getMenusStrs("resetBookmarksMenuName"));

	}

	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().resetAllBookmarks();
	}		
}
