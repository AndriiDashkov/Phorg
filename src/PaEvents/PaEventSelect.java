package paevents;

/**
 * 
 * @author Andrii Dashkov
 */

public class PaEventSelect extends PaEvent {

	/**
	 * This flag helps us to know if we should clear other selections or not
	 */
	private boolean clearOtherSelections = true;

	public PaEventSelect() {
		
		eventType = PaEventDispatcher.SELECT_EVENT;
	} 

	
	public boolean isClearOtherSelection() {

		return clearOtherSelections;
	}
	
	public void setClearOtherSelection(boolean flag) {

		clearOtherSelections = flag;
	}
}
