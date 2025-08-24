
package paactions;

import static paglobal.PaUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paevents.PaEventInt;
import paevents.PaEventSortStart;
import paglobal.PaSortOrderEnum;


/**
 * Sorting  operation
 * @author Andrii Dashkov
 *
 */
public class PaSortAction extends PaAction {
	
	private int m_index;

	private static final long serialVersionUID = 1L;
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	

	public PaSortAction(int index) {
		
		super("pasortaction");
		
		m_index = index;
	
		putValue(AbstractAction.SMALL_ICON, null);	
		
		putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("sortingMenuToolTip")); 
	
		putValue(NAME,PaSortOrderEnum.toString(PaSortOrderEnum.fromInt(index)));
	}

	
	public void enableAction(PaEventEnable e) {
		
		switch(e.getType()) {
		
			case NO_SELECTED_IMAGES : 
				
			case MULTISELECTED_IMAGES :
				
			case IMAGE_LIST_IS_EMPTY :
				
			case SELECT_CONTAINER_IS_EMPTY : { setEnabled(false); break; }
		
			case SINLGE_IMAGE_SELECTED : {  setEnabled(true); break; } 
			
			default:
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PaEventInt sortEvent = new PaEventSortStart(PaEventDispatcher.SORT_START_EVENT);
		
		((PaEventSortStart) sortEvent).setSortIndex(m_index);
		
		PaEventDispatcher.get().fireCustomEvent(sortEvent);	

	}	
}
