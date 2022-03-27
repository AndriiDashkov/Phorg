package PaEvents;

/**
 * 
 * @author Andrey Dashkov
 */

public class PaEventPhotoFilter extends PaEvent {
	

	public enum TYPE {
		
		SETFILTER,
		
		CLEARFILTER
		
	}
	
	TYPE type;
	
	public PaEventPhotoFilter () {
		
		eventType = PaEventDispatcher.IMAGE_FILTER_EVENT; //98;
		
		type = TYPE.SETFILTER;
	} 

	
	public TYPE getType() {
		
		return  type;
	}
	
	public void setType(TYPE t) {
		
		type = t;
	}
}
