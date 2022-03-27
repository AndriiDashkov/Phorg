package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaForms.PaImageTable;
import static PaGlobal.PaUtils.*;

/**
 * 
 * @author avd
 *
 */
public class PaPhotoActionEnsureVisible extends PaAction {
	
	private PaImageTable panel;
	
	private static final long serialVersionUID = 1L;
	{

	}
	public  PaPhotoActionEnsureVisible(PaImageTable panel) {
		
		super("paimageactionensurevisible");
		
		this.panel = panel;

		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("goToViewPanelToolTipName")); 
		
		putValue(NAME, getMenusStrs("goToViewPanelMenuName"));

	}

	public void actionPerformed(ActionEvent e) {
		
		panel.ensureVisible();
	}		
}
