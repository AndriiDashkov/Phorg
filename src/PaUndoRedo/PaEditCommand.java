
package PaUndoRedo;

import java.util.ArrayList;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;

/**
 * @author avd
 *
 */

public class PaEditCommand extends PaUndoRedoCommand {


	public PaEditCommand(ArrayList<PaImage> pa, int id) {
		
		super(pa, id);
	}
	
	public PaEditCommand(PaImage pa, int id) {
		
		super(pa,id);
	}

	
	public void undo(){
		
		ArrayList<PaImage> l = new ArrayList<PaImage>();
		
		for(PaImage p: m_photo){
			
			PaImageContainer c = PaUtils.get().getMainContainer().getContainer(m_albumId);
			
			if(c != null) {	
				
				PaImage pa = c.getImage(p.getId()).cloneData();	
				
				if(c.editImage(p,p.getId())) { l.add(pa); }
			}
		}
		
		m_photo.clear();
		
		m_photo = l;
		
		if(PaUtils.get().getMainContainer().isCurrent(m_albumId)) {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
			
			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
		}
	}
	
	public void redo() { 
		
		this.undo();		
	}
}
