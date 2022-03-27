package PaForms;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import PaCollection.PaAlbum;


/**
 * @author Andrii Dashkov
 *
 */

@SuppressWarnings("rawtypes")
public class PaAlbomsComboBoxModel extends AbstractListModel implements ComboBoxModel {
	
	private static final long serialVersionUID = 1L;

	private List <PaAlbum> _itemsList;	
	
	private Object  _selectedObject;

	public PaAlbomsComboBoxModel () {
		
        _itemsList = new ArrayList<PaAlbum>();
	}

	public int getSize() {
		
		return _itemsList.size();
		
	}

	public void setSelectedItem(Object newValue) {
		
		_selectedObject = newValue;
		
	}
	

	public Object getElementAt(int index) {

		return _itemsList.get(index).getName();
	}


	public Object getSelectedItem() {

		return _selectedObject;
	}
	

	public int get_ID_Element (int index) {
		
		if (index != -1) {
			
			return _itemsList.get(index).getId();
			
		} else {
			
			return -1;
		}
	}

}
