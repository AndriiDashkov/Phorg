package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import paforms.PaImageTable;



/**
 * <p>Find image action</p>
 * @author Andrii Dashkov
 *
 */
public class PaPhotoFormActionFind extends PaAction {
	
	private PaImageTable panel;
	
	private static final long serialVersionUID = 1L;
	{

	}
	
	public   PaPhotoFormActionFind(PaImageTable panel) {
		
		super("paimageformactionFind");
		
		this.panel = panel;

		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("findMenuPhotoFormToolTip")); 
		
		putValue(NAME, getMenusStrs("findMenuPhotoFormName"));

	}

	public void actionPerformed(ActionEvent e) {
		
		panel.openFindDialog();
	}		
}