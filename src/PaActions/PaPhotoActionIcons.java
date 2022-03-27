package PaActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * @author Andrey Dashkov
 *
 */
public class PaPhotoActionIcons extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionIcons() {
		
		super("paimageactionicons");
		
		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("createAlbomIconToolTip"));
		
		putValue(NAME,  getMenusStrs("createAlbomIconMenuName"));
	}
	
	public void enableAction (PaEventEnable eventPhoto) {
		
		if (PaUtils.get().getSelectedImages().size() > 0) {
			
			setEnabled(true);
			
		} else {
			
			setEnabled(false);
			
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().setAsAlbumIcon();

	}		
}