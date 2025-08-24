/**
 * 
 */
package paactions;


import static paglobal.PaUtils.*;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 *<p>Inserts the selected subject into the selected images</p>
 */
public class PaSubjectsSelectedInsert extends PaAction {


	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public PaSubjectsSelectedInsert() {
		
		super("pasubselectedinsert");
	
		putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "pasubjectinsert.png"));
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("insertSubjectsToImageMenuToolTip")); 
		
		putValue(AbstractAction.NAME, getMenusStrs("insertSubjectsToImageMenuName"));
		
		setEnabled(false);
	}
	
	/**
	 * @param event - enable event
	 *<p>enables the action only for selection case</p>
	 */
	public void enableAction(PaEventEnable event) {
		
		switch(event.getType()) {
		
			case NO_SELECTED_IMAGES : { setEnabled(false); break; }
			
			case MULTISELECTED_IMAGES :	
				
			case SINLGE_IMAGE_SELECTED : { setEnabled(true); break; }
			
			default : 
	
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().insertSubjectInSelectedImages();
	}

}