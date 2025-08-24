
package paundoredo;

import java.util.ArrayList;

import pacollection.PaImage;
import pacollection.PaImageContainer;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 *
 */
public class PaAddCommand extends PaUndoRedoCommand {

	
	public PaAddCommand(ArrayList<PaImage> pa,int albomId) {
		
		super(pa,albomId);
	}
	
	public PaAddCommand(PaImage pa, int id) {
		
		super(pa,id);
	}
	
	public void undo(){
	
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
	
	public void redo() {
		
		ArrayList<PaImage> l = new ArrayList<PaImage>();
		
		for(PaImage p: m_photo){
			
			PaImageContainer c = PaUtils.get().getMainContainer().getContainer(m_albumId);
			
			if(c != null) {
				
				PaImage pa = new PaImage(p);
				
				if(c.addPhoto(pa)) {
					
					l.add(pa);//we must create new list for possible reundo operation - because image objects have new id now!
				}
			}
		}
		
		m_photo.clear();
		
		m_photo = l;//list with new id which have been inserted
		
		if(PaUtils.get().getMainContainer().isCurrent(m_albumId)) {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
		}
		
	}

}
