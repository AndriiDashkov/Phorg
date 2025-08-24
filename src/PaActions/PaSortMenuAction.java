/**
 * 
 */
package paactions;

import static paglobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaSortOrderEnum;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 *
 */
public class PaSortMenuAction extends PaAction {
	
	private int m_index;

	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	
	/**
	 * 
	 */
	public PaSortMenuAction(int index) {
		
		super("pasortmenuaction");
		
		m_index = index;

		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("sortingMenuToolTip")); 
	
		putValue(NAME,PaSortOrderEnum.toString(PaSortOrderEnum.fromInt(index)));
	}

	
	public void enableAction(PaEventEnable e) {
		
	
		if (e.getType() == PaEventEnable.TYPE.IMAGE_LIST_IS_EMPTY || 
				 e.getType() == PaEventEnable.TYPE.SELECT_CONTAINER_IS_EMPTY) {
			
			setEnabled(false);
		}
		else {
			
			setEnabled(true);			
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getSortCombo().setSelectedIndex(m_index);

	}	
	
}
