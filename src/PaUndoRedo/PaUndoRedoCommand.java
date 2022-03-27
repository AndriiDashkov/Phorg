
package PaUndoRedo;

import java.util.ArrayList;
import PaCollection.PaImage;

/**
 * @author avd
 *
 */
public class PaUndoRedoCommand {
	

	protected ArrayList<PaImage> m_photo = new ArrayList<PaImage>();
	
	protected  int m_albumId;

	public PaUndoRedoCommand(PaImage pa, int id) {
		
		m_photo.add(pa);
		
		m_albumId = id;
	}
	
	public PaUndoRedoCommand(ArrayList<PaImage> pa, int id) {
		
		m_photo = pa;
		
		m_albumId = id;
	}
	
	public PaImage getImage() { 
		
		if(m_photo.isEmpty()) return null;
		
		return m_photo.get(0);  
	}
	
	public ArrayList<PaImage> getImages() { 
		
		return m_photo;  
	}
	
	public void setAlbomId(int id) { m_albumId = id; }

	public void undo() {}
	
	public void redo() {}
}
