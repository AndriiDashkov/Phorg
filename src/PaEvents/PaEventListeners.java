package PaEvents;

import java.util.ArrayList;

/**
 * Collects into ArrayList all receivers, represented by classes PaEventObjectMethod
 * and saves the type of event for collected receivers.
 * PaEventListener collects only those receivers, which are collected to one type of event 
 * @author Andrey Dashkov
 *
 */
public class PaEventListeners {
	
	private int _eventType;	
	
	private ArrayList<PaEventObjectMethod> _listeners; //listeners
	
	
	public PaEventListeners (int eventType) {
		
		_eventType = eventType;
		
		_listeners = new ArrayList<PaEventObjectMethod>();
	}



	public int get_eventType() {
		
		return _eventType;
	}

	public ArrayList<PaEventObjectMethod> getListeners() {
		
		return _listeners;
	}
	
	public boolean addListener (PaEventObjectMethod listener) {
		
		_listeners.add(listener);
		
		return true;
	}
	public void remove_listener (PaEventObjectMethod listener) {
		
		_listeners.remove(listener);
	}
}
