
package paroi;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.util.*;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * Model class for the table of ROI types. The model has the extra column - "Statistics". This column is not a part
 * of data itself, it's just for information about current number of ROIs of different types.
 * @author Andrii Dashkov
 *
 */
public class PaRoiTypesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	
	PaRoiPanel m_parent_panel = null;

	private ArrayList<String> m_roi_types_data = new ArrayList<String>() ;
	
	//container for statistics
	private ArrayList<Integer> m_roi_types_stat = new ArrayList<Integer>() ;

	private JTable m_table;
	

	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	
	public PaRoiTypesTableModel(PaRoiPanel parent_panel) 
	{
	
		m_parent_panel = parent_panel;
	}
	
	
	public void loadData( ArrayList<String> types_list)
	{
		 m_roi_types_data = types_list;
		
	}
	

	public void addTableModelListener(TableModelListener listener) 
	{
	   	listeners.add(listener);
	}

	public boolean isCellEditable(int row, int col) 
	{
		//only the type of ROI value is editable
		return col == 1 ;
    }
	
	public void removeTableModelListener(TableModelListener listener) 
	{
		
	   	listeners.remove(listener);
	}

	public int getColumnCount()
	{		
		return 3;
	}

	public int getRowCount() 
	{

		if(m_roi_types_data == null) return 0;
		
		return m_roi_types_data.size();
	}

	public String getColumnName (int column) 
	{
		
		switch (column) { 
		
        	case 0: return "�";  
        	
        	case 1: return getGuiStrs("roiTableTypeColInsLabel");
        	
        	case 2: return getGuiStrs("roiTableStatColInsLabel"); 
       
        }  
		return "";
	}
	
	public Class<?> getColumnClass (int column) 
	{
		
		switch (column) {
		
			case 0: return Integer.class;
			
			case 1: return String.class;
			
			case 2: return Integer.class;
			
			default: return Object.class; 
		}
	}


	public Object getValueAt(int rowIndex, int columnIndex) 
	{  
		
		try {
			
        
	        switch (columnIndex) {  
	        
        		case 0: return rowIndex;
        		
        		case 1: {
        			
        			String s =  m_roi_types_data.get(rowIndex);
        			
        			return s;
        		}
        		
        		case 2: {
        			
        			int num = 0;
        			
        			if ( rowIndex < m_roi_types_stat.size() ) {
        				
        				num = m_roi_types_stat.get(rowIndex);
        			}
        			
        			return num;
        		}
        		
        		default:
    
	       }  
	        
	       return null;
	       
		} catch (ArrayIndexOutOfBoundsException e1) {

			writeLog("ArrayIndexOutOfBoundsException :  " + NEXT_ROW, e1, true, false, true);
		}
		
		return null;
	}
	
	
	public void setValueAt(Object v, int rowIndex, int columnIndex)
	{  
		
		try {
			
			 switch (columnIndex) {  
		        
		     		case 1: {
						
						String old_value = m_roi_types_data.get(rowIndex);
						
						m_roi_types_data.set(rowIndex, (String)v);
						
						m_parent_panel.changeAllRoiTypeReferences(old_value, m_roi_types_data.get(rowIndex));
						
						break;
					}
		     		case 2: {
		     			
		     			
		     			m_roi_types_stat.set(rowIndex, (Integer)v);
		     			break;
		     		}
		     		default : {}
			 };
	
		} catch (ArrayIndexOutOfBoundsException e1) {

			writeLog("ArrayIndexOutOfBoundsException :  " + NEXT_ROW, e1, true, false, true);
		}
	}
	
	
	public String getTypeAt(int rowIndex) {  
		try {
			
			if(m_roi_types_data == null || m_roi_types_data.isEmpty()) { return ""; }
			
	        String s = m_roi_types_data.get(rowIndex);   

	        return s; 
	     
		} catch (ArrayIndexOutOfBoundsException e1) {

			writeLog("ArrayIndexOutOfBoundsException :  " + NEXT_ROW, e1, true, false, true);
		}

		return "";
	}
	
	
	
	public int findIndexforType(String type) {
			
		int endIndex = getRowCount();
		 
		for( int rowIndex= 0; rowIndex < endIndex; ++rowIndex) {
			
			String s =  getTypeAt(rowIndex);
			
			if ( s.equals(type) ) {
				
				return rowIndex;
			}
		}
			 
		return -1;
	}
    
	public void set_table (JTable t) {
		
		m_table = t;
	}
	
	public void addNewRoiType () {
		
		m_roi_types_data.add("new");
	}

	public void deleteRoiType (int index) {
		
		m_roi_types_data.remove(index);
	}

	
	
	public void setRowSelected(Integer index) {
		
		 m_table.setRowSelectionInterval(index,index);
		 
		 m_table.scrollRectToVisible(m_table.getCellRect(index, 2,true));
	}
	
	
	public ArrayList<String> getTypesList() { return m_roi_types_data; } 
	

	public int indexOf( String type ) 
	{
		return m_roi_types_data.indexOf(type);
		
	}
	
	public ArrayList<Integer> getStatisticsList()
	{
		
		return m_roi_types_stat;
		
	}
}
