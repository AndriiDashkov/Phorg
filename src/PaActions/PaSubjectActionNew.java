package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * Action for the creation of a new subject.
 * Subjects are notion used to unite images in the albums.
 * @author avd
 *
 */
public class PaSubjectActionNew extends PaAction {
	
	private static final long serialVersionUID = 1L;
	
	public PaSubjectActionNew() {
		
		super("pasubactionnew");


		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "patermnew.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("newSubMenuToolTip"));
		
		putValue(NAME,getMenusStrs("newSubMenuName"));
		
		setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.SUBJECT_NEW_EVENT) );
	}		
}

