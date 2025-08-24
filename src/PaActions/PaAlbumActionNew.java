package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paglobal.PaUtils;

/**
 * <p>Action for opening of window for new album enter</p>
 * @author Andrii Dashkov
 *
 */
public class PaAlbumActionNew extends PaAction {
	
	private static final long serialVersionUID = 1L;
	
	public PaAlbumActionNew() {
		
		super("paalbomactionnew");
		
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paalbomnew.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("newAlbomMenuToolTip"));
		
		putValue(NAME,  getMenusStrs("newAlbomMenuName"));
		
		setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent( PaEventDispatcher.ALBUM_NEW_EVENT) );
	}		
}

