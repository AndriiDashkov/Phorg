package PaForms;


import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 * @author avd
 *
 */
public class PaExtEditorTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int COLUMN_NAME = 0;
	
	public static final int COLUMN_COMMAND = 1;
	
	public static final int COLUMN_OPTIONS = 2;
	
	public static final int COLUMNS = 3; 

	public PaExtEditorTable(PaExtEditorsTableModel model) {
		
		super(model);
		
		TableColumnModel colModel = getColumnModel();

		colModel.getColumn(COLUMN_NAME).setMaxWidth(160);
		
	}

}
