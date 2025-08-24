package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import paforms.PaImageTable;

/**
 * 
 * @author Andrii Dashkov
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
