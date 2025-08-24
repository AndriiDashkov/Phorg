package paactions;

import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import pacollection.PaImageContainer;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;
import palong.PaRefreshBoostIcons;


/**
 * <p>Refreshes all visible icons in an album (case when images my be modified from the outside)</p>
 * @author Andrii Dashkov
 *
 */
public class PaPhotoActionRefreshIcons extends PaAction {
	
	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaPhotoActionRefreshIcons() {
		
		super("paimageactionrefreshicons");
		
		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("refreshBoostIconsToolTip"));
	
		putValue(NAME,  getMenusStrs("refreshBoostIconsMenuName"));
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
		
		
		int n = JOptionPane.showConfirmDialog(
			    PaUtils.get().getMainWindow(),
			    getMessagesStrs("startRefreshAllBoostImages"),
			    getMessagesStrs("messageAnswerCaption"),
			    JOptionPane.YES_NO_OPTION);
	
		if ( n == JOptionPane.YES_OPTION) {
			
			//potentially long operation, so the special progress monitor is used
			
			ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
					getMessagesStrs("refreshBoostImagesOperationCaption"),
					getMessagesStrs("loadedImagesOperationNote"), 0, 100);
			
			progressMonitor.setMillisToDecideToPopup(0);
			
			progressMonitor.setMillisToPopup(0);
			
			
			PaRefreshBoostIcons ts = new PaRefreshBoostIcons(progressMonitor);
			
			ts.execute();
			

		}
	}		
}