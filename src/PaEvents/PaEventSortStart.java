
package PaEvents;

/**
 * @author avd
 * 
 */
public class PaEventSortStart extends PaEvent {

	/**
	 * 
	 */
	
	private int m_index;
	
	public PaEventSortStart() {
	
	}

	/**
	 * @param eType
	 */
	public PaEventSortStart(short eType) {
		super(eType);
		// TODO Auto-generated constructor stub
	}
	
	public void setSortIndex(int index) {
		
		m_index= index;
	}
	
	public int getSortIndex() {
		
		return m_index;
	}


}

