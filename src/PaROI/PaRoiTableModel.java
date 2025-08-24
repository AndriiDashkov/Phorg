
package paroi;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.util.*;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author Andrii Dashkov
 * The table model for the representation of the list of ROIs for the current image
 *
 */
public class PaRoiTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private ArrayList<PaRectangle> roi_data = null ;

	private JTable m_table;
	
	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	
	public PaRoiTableModel() {
	}
	
	
	public void loadData( ArrayList<PaRectangle> roi_list)
	{
		roi_data = roi_list;
		
	}
	

	public void addTableModelListener(TableModelListener listener) {
		
	   	listeners.add(listener);
	}

	public boolean isCellEditable(int row, int col) {

		return col == 5;
    }
	
	public void removeTableModelListener(TableModelListener listener) {
		
	   	listeners.remove(listener);
	}

	public int getColumnCount() {	
		
		return 6;
	}

	public int getRowCount() {

		if(roi_data == null) return 0;
		
		return roi_data.size();
	}

	public String getColumnName (int column) {
		
		switch (column) {  
		
	        case 0: return "�";  
	        
	        case 1: return "x";  
	        
	        case 2: return "y";  
	        
	        case 3: return getGuiStrs("roiTableWColInsLabel"); 
	        
	        case 4: return getGuiStrs("roiTableHColInsLabel");
	        
	        case 5: return getGuiStrs("roiTableTypeColInsLabel");  
	        
	        default:

        }  
		
		return "";
	}
	
	public Class<?> getColumnClass (int column) {
		
		switch (column) {
		
			case 0: return Integer.class;
			
			case 1: return Integer.class;
			
			case 2: return Integer.class;
			
			case 3: return Integer.class;
			
			case 4: return Integer.class;
			
			case 5: return String.class;
			
			default: return Object.class; 
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {  
		
		try {
	        PaRectangle r = roi_data.get(rowIndex);   

	        switch (columnIndex) {  
	        
        		case 0: return rowIndex;
        		
        		case 1: return r.x;
        		
        		case 2: return r.y;
        		
        		case 3: return r.w;
        		
        		case 4: return r.h; 
        		
        		case 5: return r.type;
       	
        		default:
  
	       }
	        
	       return null;
	       
		} catch (ArrayIndexOutOfBoundsException e1) {

			writeLog("ArrayIndexOutOfBoundsException :  " + NEXT_ROW, e1, true, false, true);
		}
		
		return columnIndex;
	}
	
	
	public PaRectangle getRectangleAt(int rowIndex) {  
		
		try {
			
	        PaRectangle r = roi_data.get(rowIndex);   

	        return r; 
	     
		} catch (ArrayIndexOutOfBoundsException e1) {

			writeLog("ArrayIndexOutOfBoundsException :  " + NEXT_ROW, e1, true, false, true);
		}

		return null;
	}
	
	
	
	public PaRectangle get_roi (int rowIndex) {
		
		return roi_data.get(rowIndex);
	}
	
	public int findIndexforId(int x, int y, int w, int h) {
			
		int endIndex = getRowCount();
		 
		for( int rowIndex = 0; rowIndex < endIndex; ++rowIndex) {
			
			PaRectangle r = get_roi(rowIndex);
			
			if ( r.x == x && r.y == y && r.w == w && r.h == h ) {
				
				return rowIndex;
			}
		}
			 
		return -1;
	}
    
	public void set_table (JTable t) {
		
		m_table = t;
	}
	
	public void setRowSelected(Integer index) {
		
		 m_table.setRowSelectionInterval(index,index);
		 
		 m_table.scrollRectToVisible(m_table.getCellRect(index, 2,true));
		 
	}
	
}
