package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaDialogs.PaSettingsDialog;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * 
 * @author avd
 *
 */
public class PaAlbumActionSettings extends PaAction {
	
	private static final long serialVersionUID = 1L;
	
	public PaAlbumActionSettings() {
		
		super("paalbomactionsettings");

		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("settingsMenuToolTipName")); 
		
		putValue(NAME,  getMenusStrs("settingsMenuName")); 
		
		setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		String nameFrame =getMenusStrs("settingsMenuName");
		
		PaSettingsDialog settingDialog = new PaSettingsDialog(PaUtils.get().getMainWindow(), nameFrame);
		
		settingDialog.setVisible(true);
	}		
}	