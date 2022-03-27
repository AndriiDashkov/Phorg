package PaEvents;

import PaCollection.PaSubject;

/**
 * @author avd
 * 
 */
public class PaEventSubjectRefreshNewPhotoDialog extends PaEvent {
	
	
	private PaSubject _subject;

	public PaEventSubjectRefreshNewPhotoDialog (PaSubject t) {
		
		eventType = PaEventDispatcher.SUBJECT_REF_NEW_IMAGE_EVENT;//4;
		
		_subject = t;
	} 


	public PaSubject get () {
		
		return _subject;
	}	

}
