package PaForms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.AbstractListModel;
import PaCollection.PaImage;
import PaCollection.PaSubject;
import PaCollection.PaSubjectContainer;
import PaGlobal.PaUtils;


/**
 * @author Andrii Dashkov
 */
public class PaSubjectsListModel extends AbstractListModel<Object> {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<PaSubject> _temsList;


	public PaSubjectsListModel () {
		
		_temsList = new ArrayList<PaSubject> ();
	}


	public Object getElementAt(int index) {

		return _temsList.get(index).getName();
	}
	
	public PaSubject getRowAt (int rowIndex) {
		
		return _temsList.get(rowIndex);
	}
	
	public ArrayList<PaSubject> get_temsList() {
		return _temsList;
	}


	public int getSize() {

		return _temsList.size();
	}
	

	public void setDataSourse (PaSubjectContainer temContainer) {
		
		_temsList.clear();
		
		for(PaSubject tema : temContainer.get_tems_sort()) {
			
			_temsList.add(tema);
		}
	}

	
	public void setDataPhotos (PaImage photo) {
		
		for (int tem : photo.getSubjectsList()) {
			
			_temsList.add(photo.get_tem(tem));
		}
	}
	

	public void setDataPhotos (ArrayList<Integer> temaPhoto) {
		
		for (int temId : temaPhoto) {		
			
			Iterator<PaSubject> iter = PaUtils.get().getSubjectsContainer().get_tems().iterator();
			
			PaSubject x;
			
			while (iter.hasNext()) {
				
				x = iter.next();
				
				if (x.getId() == temId) {
					
					_temsList.add(x);
				}
			}
		}
		
		Collections.sort(_temsList);
	}
	

	public void removeData (int [] photoIdTems) {

		for (int temId : photoIdTems) {		
			
			Iterator<PaSubject> iter = PaUtils.get().getSubjectsContainer().get_tems().iterator();
			
			PaSubject x;
			
			while (iter.hasNext()) {
				
				x = iter.next();
				
				if (x.getId() == temId) {
					
					_temsList.remove(x);
				}
			}
		}
	}
	
	public void removeData (ArrayList<Integer> photoIdTems) {

		for (int temId : photoIdTems) {		
			
			Iterator<PaSubject> iter = PaUtils.get().getSubjectsContainer().get_tems().iterator();
			
			PaSubject x;
			
			while (iter.hasNext()) {
				
				x = iter.next();
				
				if (x.getId() == temId) {
					
					_temsList.remove(x);
				}
			}
		}
	}


	public void removeList (int index) {
		
		_temsList.remove(index);
		
		Collections.sort(_temsList);
	}
	

	/**
	 * add subject and control the id duplication
	 * @param s - subject to insert
	 * @return true if the operation is successful
	 */
	public boolean addTema (PaSubject s) {	
		
		if(findSubject(s) == -1) {
			
			_temsList.add(s);
			
			Collections.sort(_temsList);
			
			return true;
		}
		
		return false;
	}
	
	 /**
     * <p>Find the subject in the container</p>
     * @param sb - subject to be find
     */
	public int findSubject(PaSubject  sb)
	{
		 for(int i=0; i < _temsList.size(); ++i) {
			 
			 if(_temsList.get(i).getId() == sb.getId()) {	
				 
				 return i;
			 } 
		 }
		return -1;
	}
}
