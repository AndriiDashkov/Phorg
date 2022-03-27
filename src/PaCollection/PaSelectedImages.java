package PaCollection;

import java.util.ArrayList;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventInt;
import PaGlobal.PaLog;
import PaImage.PaViewPhotosForm;
import static PaGlobal.PaUtils.getMessagesStrs;

/**
 * Class to control selected view forms in the main view panel
 * @author avd
 *
 */
public class PaSelectedImages {
	
	private ArrayList<PaViewPhotosForm> m_list;
	
	public PaSelectedImages () {
		
		 m_list = new ArrayList<PaViewPhotosForm>();
	}
	
	public ArrayList<PaViewPhotosForm> getList() {
		return m_list;
	}

	public void clear () {
		
		m_list.clear();
		
		PaLog.writeInfoOnly(getMessagesStrs("NoselectedImagesNumberMessage"));
		
		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.NO_SELECTED_IMAGES));
	}

	public int size () {
		
		return m_list.size();
	}
	/**
	 * 
	 * @param viewForm - view form to add into container for selected items
	 */
	public void add (PaViewPhotosForm viewForm) {
		
		if(findForm(viewForm)) { return; }
		
		m_list.add(viewForm);
		
		int s = m_list.size();
		
		PaLog.writeInfoOnly(getMessagesStrs("selectedImagesNumberMessage") + " " + s);
		
		if (s == 1) {
			
			PaEventInt eventEnable = new PaEventEnable(1);
			
			PaEventDispatcher.get().fireCustomEvent(eventEnable);
			
		} else {
			
			PaEventInt eventEnable = new PaEventEnable(2);
			
			PaEventDispatcher.get().fireCustomEvent(eventEnable);
		}
	}
	
	public void remove (PaViewPhotosForm viewPhotoForm) {
		
		m_list.remove(viewPhotoForm);
		
		int s = m_list.size();
		
		if(s != 0) {
			
			PaLog.writeInfoOnly(getMessagesStrs("selectedImagesNumberMessage") + " " + s);
		}
		else {
			
			PaLog.writeInfoOnly(getMessagesStrs("NoselectedImagesNumberMessage"));
		}
		
		if (s == 1) {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.SINLGE_IMAGE_SELECTED));
		} else {
			
			if (m_list.size() > 1) {
				
				PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.MULTISELECTED_IMAGES));
				
			} else {
				
				PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.NO_SELECTED_IMAGES));
				
			}
		}
	}
	/**
	 * 
	 * @param viewForm - form to check
	 * @return true if the view form has been already selected and in the list
	 */
	private boolean findForm(PaViewPhotosForm viewForm) {
		
		int id = viewForm.getImage().getId();
		
		for(PaViewPhotosForm f: m_list){
			
			if(f.getImage().getId() == id) { return true; }
		}
		
		return false;
	}

}
