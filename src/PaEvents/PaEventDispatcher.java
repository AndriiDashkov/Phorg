package paevents;

import static paglobal.PaLog.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;


/** 
 * 
 *  Events dispatcher - works in synchronous way. Every new created event must be registered here.
 * @author Andrii Dashkov
 *
 */

public class PaEventDispatcher {
	
	public static final short SAVE_EVENT = 1;
	
	public static final short ENABLE_EVENT = 2;
	
	public static final short SUBJECT_REF_NEW_IMAGE_EVENT = 4;
	
	public static final short ALBUM_REFRESH_EVENT = 5;
	
	public static final short RESIZE_ALBOM_PANEL_EVENT = 6;
	
	public static final short VIEWPANEL_REFRESH_EVENT =7;
	
	public static final short REFRESH_EVENT = 13;
	
	public static final short SUBJECT_NEW_EVENT = 30;
	
	public static final short SUBJECT_EDIT_EVENT = 31; 
	
	public static final short SUBJECT_DEL_EVENT = 32;
	
	public static final short SUBJECT_REFRESH_EVENT = 33;
	
	public static final short SUBJECT_FIND_EVENT = 34;
	
	public static final short SELECT_ALL_EVENT = 70;
	
	public static final short SELECT_EVENT  = 71;
	
	public static final short ALBUM_NEW_EVENT = 80;
	
	public static final short ALBUM_EDIT_EVENT = 81;
	
	public static final short ALBUM_DEL_EVENT = 82;
	
	public static final short ALBUM_MOVE_EVENT = 83;
	
	public static final short ALBUM_MERGE_EVENT = 84;
	
	public static final short ALBUM_FIND_EVENT = 85;
	
	public static final short ALBUM_NEW_ICON_EVENT = 86;
	
	public static final short IMAGE_NEW_EVENT = 90;
	
	public static final short IMAGE_EDIT_EVENT = 91;
	
	public static final short IMAGE_DEL_EVENT = 92;
	
	public static final short GROUP_NEW_EVENT = 93;
	
	public static final short COPY_BUFFER_EVENT = 95;
	
	public static final short IMAGE_PASTE_EVENT = 96;
	
	public static final short IMAGE_CUT_EVENT = 94;
	
	public static final short COPY_IN_EVENT = 97;
	
	public static final short IMAGE_FILTER_EVENT = 98;
	
	public static final short IMAGE_PROP_EVENT = 99;
	
	public static final short SORT_START_EVENT = 115;
	
	public static final short UNDO_EVENT = 116;
	
	public static final short REDO_EVENT = 117;
	
	public static final short MOVE_TO_STANDARD_FOLDER = 118;
	
	public static final short IMAGE_EDITOR_CLOSED = 119;
	
	public static final short IMAGE_EDITED_EVENT = 120;
	
	public static final short SET_BOOKMARKED_EVENT = 121;
	
	public static final short SET_UN_BOOKMARKED_EVENT = 122;
	
	public static final short SET_SLIDER_SHOW_EVENT = 123;
	
	public static final short SET_SLIDER_HIDE_EVENT = 124;
	
	public static final short BEFORE_APPLICATION_EXIT_EVENT = 125;
	
	private static PaEventDispatcher _instance;

		
	private ArrayList<PaEventListeners> m_listeners = new ArrayList<PaEventListeners>();

	/**
	 * empty constructor
	 */
	private PaEventDispatcher() {}
	
	public  static PaEventDispatcher get() {
		
		if (_instance == null) {
			
			_instance = new PaEventDispatcher();
			
			_instance.initDispatcher();

		}
		return _instance;
	}
	
	/**
	 * 	Connects the receiver with event
	 * @param eventType - type of event for connection
	 * @param receiver - the object which will receive the event and react to it with method methodName
	 * @param methodName - method name which will be connect to the event eventType
	 */
	public void addConnect(int eventType, Object receiver, String methodName) {
		
		PaEventObjectMethod listener = new PaEventObjectMethod(receiver, methodName);
		
		for (PaEventListeners x : m_listeners) {
			
			if (x.get_eventType() == eventType) {
				
				x.addListener(listener);
			}
		}
	}
	/**
	 * Disconnects all events from the object receiver
	 * @param receiver - object to disconnect
	 */
	public void disconnect (Object receiver) {
		
		for(Iterator<PaEventListeners> it = m_listeners.iterator(); it.hasNext();){
			
			for(Iterator<PaEventObjectMethod> it1 = it.next().getListeners().iterator(); it1.hasNext();){
				
				if (it1.next().getListener() == receiver) {
					
					it1.remove();
				}
			}
		}
	}
 	
	/**
	 * Starts the propagation of the event ev
	 * @param ev - event to start
	 */
	public void fireCustomEvent (PaEventInt ev) {

		String methodName = null;
		
		for (PaEventListeners x : m_listeners) {
			
			if (x.get_eventType() == ev.get_EventType()) {
				
				for (PaEventObjectMethod _objectMethod : x.getListeners()) {
					
					try {
					
						Class<?> cls = _objectMethod.getListener().getClass();

						methodName = _objectMethod.get_method();
						
						Method meth_1 = cls.getMethod(methodName, ev.getClass());	
						
						meth_1.invoke(_objectMethod.getListener(), ev);

					} 
					catch (IllegalAccessException  e) {
				
						writeLog("Dispatcher IllegalAccessException :"+methodName,e,true,false,true);
					}
					catch (NoSuchMethodException  e) {
						
						writeLog("Dispatcher NoSuchMethodException:"+methodName,e,true,false,true);
						
					}	catch (InvocationTargetException  e) {
						
						writeLog("Dispatcher InvocationTargetException:"+methodName,e,true,false,true);
					}									
				}
			}
		}
	}
	/**
	 * Event dispatcher initialization
	 */
	public void initDispatcher () {
		
		PaEventListeners saveEvent = new PaEventListeners(SAVE_EVENT);		
		
		m_listeners.add(saveEvent);										
		
		PaEventListeners enableEvent = new PaEventListeners(ENABLE_EVENT);
		
		m_listeners.add(enableEvent);
		
		PaEventListeners newTemaEvent = new PaEventListeners(SUBJECT_NEW_EVENT);
		
		m_listeners.add(newTemaEvent);

		PaEventListeners editTemaEvent = new PaEventListeners( SUBJECT_EDIT_EVENT );	
		
		m_listeners.add(editTemaEvent);
		
		PaEventListeners delTemaEvent = new PaEventListeners(SUBJECT_DEL_EVENT);	

		m_listeners.add(delTemaEvent);
		
		PaEventListeners refrashTemaEvent = new PaEventListeners(SUBJECT_REFRESH_EVENT);	

		m_listeners.add(refrashTemaEvent);								

		PaEventListeners findTemaEvent = new PaEventListeners(SUBJECT_FIND_EVENT);	

		m_listeners.add(findTemaEvent);
		
		PaEventListeners refrashNewTemPhotoEvent = new PaEventListeners(SUBJECT_REF_NEW_IMAGE_EVENT);	

		m_listeners.add(refrashNewTemPhotoEvent);							
		
		PaEventListeners refrashAlbum = new PaEventListeners(ALBUM_REFRESH_EVENT);		

		m_listeners.add(refrashAlbum);									
		
		PaEventListeners resizeAlbumPanel = new PaEventListeners( RESIZE_ALBOM_PANEL_EVENT );	

		m_listeners.add(resizeAlbumPanel);
		
	
		PaEventListeners refrashViewPanel = new PaEventListeners(VIEWPANEL_REFRESH_EVENT);	
		
		m_listeners.add(refrashViewPanel);					

		PaEventListeners eventSelectAllPhotos = new PaEventListeners(SELECT_ALL_EVENT);
		
		m_listeners.add(eventSelectAllPhotos);							
		
		PaEventListeners eventDeselect = new PaEventListeners(SELECT_EVENT);	

		m_listeners.add(eventDeselect);


		PaEventListeners newAlbomEvent = new PaEventListeners(ALBUM_NEW_EVENT);		

		m_listeners.add(newAlbomEvent);

		PaEventListeners editAlbomEvent = new PaEventListeners(ALBUM_EDIT_EVENT);	

		m_listeners.add(editAlbomEvent);
		
		PaEventListeners delAlbomEvent = new PaEventListeners(ALBUM_DEL_EVENT);		

		m_listeners.add(delAlbomEvent);
		
		PaEventListeners moveAlbumEvent = new PaEventListeners(ALBUM_MOVE_EVENT);		

		m_listeners.add(moveAlbumEvent);
		
		PaEventListeners mergeAlbumEvent = new PaEventListeners(ALBUM_MERGE_EVENT);	

		m_listeners.add(mergeAlbumEvent);
		
		PaEventListeners findAlbomEvent = new PaEventListeners(ALBUM_FIND_EVENT);	

		m_listeners.add(findAlbomEvent);
		
		m_listeners.add(new PaEventListeners(ALBUM_NEW_ICON_EVENT));     
	
		PaEventListeners newPhotoEvent = new PaEventListeners(IMAGE_NEW_EVENT);		

		m_listeners.add(newPhotoEvent);
		
		PaEventListeners editPhotoEvent = new PaEventListeners(IMAGE_EDIT_EVENT);		

		m_listeners.add(editPhotoEvent);
		
		PaEventListeners deletePhotoEvent = new PaEventListeners(IMAGE_DEL_EVENT);	

		m_listeners.add(deletePhotoEvent);				
		
		PaEventListeners newPhotoGroupEvent = new PaEventListeners(GROUP_NEW_EVENT);	

		m_listeners.add(newPhotoGroupEvent);							
		
		PaEventListeners cutPhotoEvent = new PaEventListeners(IMAGE_CUT_EVENT);		

		m_listeners.add(cutPhotoEvent);								
		
		PaEventListeners copyPhotoEvent = new PaEventListeners(COPY_BUFFER_EVENT);		

		m_listeners.add(copyPhotoEvent);								

		
		PaEventListeners pastePhotoEvent = new PaEventListeners(IMAGE_PASTE_EVENT);	

		m_listeners.add(pastePhotoEvent);							

		PaEventListeners copyPhotoInEvent = new PaEventListeners(COPY_IN_EVENT);	

		m_listeners.add(copyPhotoInEvent);							
		
		PaEventListeners filterPhotoInEvent = new PaEventListeners(IMAGE_FILTER_EVENT);	

		m_listeners.add(filterPhotoInEvent);							

		PaEventListeners properPhotoInEvent = new PaEventListeners(IMAGE_PROP_EVENT);	
		
		m_listeners.add(properPhotoInEvent);							
		
		PaEventListeners refreshEvent = new PaEventListeners(REFRESH_EVENT);	
		
		m_listeners.add(refreshEvent);								
		
		PaEventListeners sortEvent = new PaEventListeners(SORT_START_EVENT);
		
		m_listeners.add(sortEvent);								
		
		m_listeners.add(new PaEventListeners(UNDO_EVENT));
		
		m_listeners.add(new PaEventListeners(REDO_EVENT));	
		
		m_listeners.add(new PaEventListeners(MOVE_TO_STANDARD_FOLDER));
		
		m_listeners.add(new PaEventListeners(IMAGE_EDITOR_CLOSED)); 
		
		m_listeners.add(new PaEventListeners(IMAGE_EDITED_EVENT)); 
		
		m_listeners.add(new PaEventListeners(SET_BOOKMARKED_EVENT));
		
		m_listeners.add(new PaEventListeners(SET_UN_BOOKMARKED_EVENT));
		
		m_listeners.add(new PaEventListeners(SET_SLIDER_HIDE_EVENT));
		
		m_listeners.add(new PaEventListeners(SET_SLIDER_SHOW_EVENT));
		
		m_listeners.add(new PaEventListeners(BEFORE_APPLICATION_EXIT_EVENT));
	
	}
	
}
