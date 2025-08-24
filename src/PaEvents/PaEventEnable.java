package paevents;

public class PaEventEnable extends PaEvent {


	private int m_flag = 0;
	
	private TYPE m_type;
	
	public enum TYPE{
		
		NO_SELECTED_IMAGES, //0 - no selection;
		
		SINLGE_IMAGE_SELECTED, //1 - only 1 image is selected;  
		
		MULTISELECTED_IMAGES, //2 - more than 1 images are selected;
		
		IMAGE_LIST_IS_EMPTY,              //4 - list of images is empty;
		
		IMAGE_LIST_NOT_EMPTY,          //	5 - list of image is not empty;
		
		SELECT_CONTAINER_IS_EMPTY,    // 6 - PaMainConainer.get_photoContainerSelect() = null
		
		COPY_BUFFER_ACTIVATED,      //activation of copy paste buffer
		
		COPY_BUFFER_EMPTY,//copy paste buffer is empty
		
		DATA_CHANGED,				//101 - data have been changed setEnable(true);
		
		DATA_SAVED,		//102 - data have been saved setEnable(true);
		
		UNDO_ENABLED,
		
		UNDO_DISABLED,
		
		REDO_ENABLED,
		
		REDO_DISABLED,
		
		ALBUM_LOADED,
		
		ALBUM_LIST_IS_EMPTY, //14 - the list of albums is empty FindAlbom - setEnable(false);
		
		ALBUM_LIST_IS_NOT_EMPTY, //* 			15 -  the list of albums is not empty FindAlbom - setEnable(true);
		
		ALBUM_SELECTION_EMPTY,  //10 - no selection for albums;
		
		ALBUM_SELECTED_1, // 			11 - 1 album selected;
		
		ALBUM_SELECTED_2,// 			12 - 2 albums are selected; 
		
		ALBUM_MULTI_SELECTION, //13 - more than 2 albums are selected;
		
		SUBJECT_NOT_SELECTED, //		20 - no selection for subjects;
		
		SUBJECT_SELECTED_1, // 			21 - 1 subject selected;
		
		SUBJECT_MULTISELECTED, //	22 - more than 1 subject selected;
		
		SUBJECT_LIST_EMPTY, //		24 - the list of subjects is empty PaSubjectActionFind - setEnable(false);
		
		SUBJECT_LIST_NOT_EMPTY, //			25 - the list of subjects is not empty PaSubjectActionFind - setEnable(true);
		
		UNVALID_TYPE	
	}

	public PaEventEnable (int flag) {
		
		eventType = PaEventDispatcher.ENABLE_EVENT; //2;
		
		m_flag = flag;
		
		m_type = getTypeForFlag(flag);
		
	}
	
	public PaEventEnable (TYPE t) {
		
		eventType = PaEventDispatcher.ENABLE_EVENT; //2;
		
		m_type = t; 
		
		m_flag = getFlagForType(t);
		
	}
	

	
	public int get_flag() {
		
		return m_flag;
	}
	
	
	public TYPE getType() {

		return m_type;
	}
	
	public  void setType(TYPE t) {

		m_type =t;
	}
	
	public  int getFlagForType(TYPE t) {

		switch(t) {
		
			case NO_SELECTED_IMAGES: return 0; 
			
			case SINLGE_IMAGE_SELECTED: return 1;
			
			case MULTISELECTED_IMAGES: return 2;
			
			case IMAGE_LIST_IS_EMPTY: return 4;   
			
			case IMAGE_LIST_NOT_EMPTY: return 5;    
			
			case SELECT_CONTAINER_IS_EMPTY: return 6;   
			
			case COPY_BUFFER_ACTIVATED:   return -1; 
			
			case COPY_BUFFER_EMPTY:   return -1;
			
			case DATA_CHANGED: return 101;	
			
			case DATA_SAVED: return 102;	
			
			case ALBUM_LIST_IS_EMPTY: return 14; 
			
			case ALBUM_LIST_IS_NOT_EMPTY: return 15; 
			
			case ALBUM_SELECTION_EMPTY: return 10; 
			
			case ALBUM_SELECTED_1: return 11;
			
			case ALBUM_SELECTED_2: return 12;
			
			case ALBUM_MULTI_SELECTION: return 13; 
			
			default: return -1;		
		}
	}
	
	public  TYPE getTypeForFlag(int f) {

		switch(f) {
		
			case 0: return TYPE.NO_SELECTED_IMAGES;
			
			case 1: return TYPE.SINLGE_IMAGE_SELECTED;
			
			case 2: return TYPE.MULTISELECTED_IMAGES;
			
			case 4: return TYPE.IMAGE_LIST_IS_EMPTY;   
			
			case 5: return TYPE.IMAGE_LIST_NOT_EMPTY;  
			
			case 6: return TYPE.SELECT_CONTAINER_IS_EMPTY;  
			
			case -1: return TYPE.COPY_BUFFER_ACTIVATED;   
			
			case 101: return TYPE.DATA_CHANGED;		
			
			case 102: return TYPE.DATA_SAVED;	
			
			case 14: return TYPE.ALBUM_LIST_IS_EMPTY; 
			
			case 15: return TYPE.ALBUM_LIST_IS_NOT_EMPTY; 
			
			case 10: return TYPE.ALBUM_SELECTION_EMPTY;
			
			case 11: return TYPE.ALBUM_SELECTED_1;
			
			case 12: return TYPE.ALBUM_SELECTED_2;
			
			case 13: return TYPE.ALBUM_MULTI_SELECTION; 
			
			default: return TYPE.UNVALID_TYPE;		
		}
	}

}
