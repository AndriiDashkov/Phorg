package PaEvents;

/**
 * Incapsulates 2 parameters: the reference to the object-receiver and the name of method for invocation by event
 * PaEentObjectMethod represents the information about single receiver
 * 
 * @author Andrey Dashkov
 */
public class PaEventObjectMethod {

	private Object _listener;	// reference to the receiver
	
	private String _method;	//the function (method) to invoke
	
	public PaEventObjectMethod (Object listener, String methodName) {
		
		_listener = listener;
		
		_method = methodName;
		
	}

	public Object getListener() {
		
		return _listener;
	}

	public void set_listeners(Object listeners) {
		
		_listener = listeners;
	}

	public String get_method() {
		
		return _method;
	}

	public void set_method(String methodName) {
		
		_method = methodName;
	}
	
}
