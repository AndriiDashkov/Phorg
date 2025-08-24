package paevents;
/**
 *  Main event class
 * @author Andrii Dashkov
 *
 */
public class PaEvent implements PaEventInt {

	protected short eventType;
	
	/**
	 * Info message - must be set in the case if you want a special message in the end of even 
	 * propagation
	 * The receiver (function which gets an event) is responsible for this message; there is no guarantee that this 
	 * message will not be shadowed by another message. The main using of this is to show messages after
	 * long tasks which are doing asynchronously
	 */
	protected String m_endMessage = null;
	
	/**
	 * This id can be used for transfer information this the event
	 */
	protected int m_id = -1;
	
	public PaEvent() {
		
		eventType = -1;
	}
	
	public PaEvent(short eType) {
		
		eventType = eType;
	}

	
	public short getEventType() {
		
		return eventType;
	}

	public void setEventType(short t) {
		
		eventType = t;
	}

	
	@Override
	public short get_EventType() {
		
		return eventType;
	}	
	
	public void setId(int i) { m_id =i; }
	
	public int getId() { return m_id; }
	/**
	 * 
	 * @param s - end message string
	 */
	public void setEndMessage(String s) {
		
		m_endMessage = s;
	}
	/**
	 * 
	 * @return end message text
	 */
	public String getEndMessage() {
		
		return m_endMessage;
	}
}
