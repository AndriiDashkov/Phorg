/**
 * 
 */
package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * @author avd
 *
 */

public class PaSliderMarksReset extends PaAction {
	
	//private PaImageTable panel;
	
	private static final long serialVersionUID = 1L;
	{
	
	}
	public  PaSliderMarksReset() {
		
		super("paSliderMarksReset");

		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("resetSliderMarksMenuToolTip")); 
		
		putValue(NAME, getMenusStrs("resetSliderMarksMenuName"));

	}

	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().resetAllSliderMarks();
	}		
}
