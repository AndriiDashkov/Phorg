
package PaUndoRedo;

import java.util.ArrayList;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaGlobal.PaUtils;

/**
 * @author avd
 *
 */

public class PaDelCommand extends PaUndoRedoCommand {


	public PaDelCommand(ArrayList<PaImage> pa, int id) {
		
		super(pa, id);
	}

	
	public void undo(){
		
		ArrayList<PaImage> l = new ArrayList<PaImage>();
		
		for(PaImage p: m_photo){
			
			PaImageContainer c = PaUtils.get().getMainContainer().getContainer(m_albumId);
			
			PaImage pa = new PaImage(p);
			
			if(c != null && c.addPhoto(pa)) {	
				
				//we must create new list for possible reredo operation - because image objects have new id now!
				l.add(pa);
			}
		}
		m_photo.clear();
		
		m_photo = l;
		
		if(PaUtils.get().getMainContainer().isCurrent(m_albumId)) {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
		}
	}
	
	public void redo() {
		
		for(PaImage p: m_photo){
			
			PaImageContainer c = PaUtils.get().getMainContainer().getContainer(m_albumId);
			
			if(c != null) {
				
				c.removePhoto(p.getId());
			}
		}
		if(PaUtils.get().getMainContainer().isCurrent(m_albumId)) {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
		}
		
	}

}


