package PaForms;

import static PaGlobal.PaUtils.getGuiStrs;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import PaCollection.PaSettings;

public class PaExtEditorsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	ArrayList< String[] > rowData;

	public PaExtEditorsTableModel() {
		
	}

	public void initModel(PaSettings set) {
		
		rowData = new ArrayList< String[] >();
		
		rowData = set.getExtEditorsList();

	}
	
	@Override
	public int getColumnCount() {
		
		return PaExtEditorTable.COLUMNS;
	}

	@Override
	public int getRowCount() {
		
		return rowData.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		
		 return rowData.get(row)[col];
	}
	
    public boolean isCellEditable(int row, int col)
    { 
    	return true; 
    }
    
    
	public void setValueAt(Object value, int row, int col) {
		
	    rowData.get(row)[col] = (String) value;
	    
	    fireTableCellUpdated(row, col);
	}
	
	
	
	public String getColumnName (int column) {
		
		switch (column) { 
		
	
		    case PaExtEditorTable.COLUMN_NAME:    return getGuiStrs("extRedTableColumnNameInMenu");
		
		    case PaExtEditorTable.COLUMN_COMMAND: return getGuiStrs("extRedTableColumnCommand");
		 
		    case PaExtEditorTable.COLUMN_OPTIONS: return getGuiStrs("extTableColumnOptions");
	 
	     
	    }  
		return "";
	}


	public Class<?> getColumnClass (int column) {
		
		switch (column) {
		
			case 0: return String.class;
			
			case 1: return String.class;
			
			case 2: return String.class;
	
			default: return Object.class; 
		}
	}


	public void deleteRow(int index) {
		
		rowData.remove(index);
		
		this.fireTableDataChanged();
	}
	
	public void addRow( ) {
		
		String[] s = new String[PaExtEditorTable.COLUMNS];
		
		s[0]=""; s[1]=""; s[2]="";
		
		rowData.add(s);
		
		this.fireTableDataChanged();
	}
}
