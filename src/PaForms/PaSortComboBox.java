
package PaForms;


import java.awt.Dimension;
import javax.swing.JComboBox;
import PaGlobal.PaSortOrderEnum;
import PaGlobal.PaUtils;

/**
 * @author avd
 * <p>This is the combo box for sort order variants</p>
 */
public class PaSortComboBox extends JComboBox<String> {

	private static final long serialVersionUID = 1L;

	
	public PaSortComboBox() {
		
		init();
	}

	/**
	 * <p>Initialization function for sort combo box</p>
	 */
	private void init() {
		
		PaUtils.get().setSortCombo(this); //we will need this combo directly
		
		setMaximumSize(new Dimension(100, 50));
		
		setToolTipText(PaUtils.getGuiStrs("comboBoxSortToolTip"));
		
		insertItemAt(PaSortOrderEnum.toString(PaSortOrderEnum.DATE_ORDER),
				PaSortOrderEnum.toInt(PaSortOrderEnum.DATE_ORDER));
		
		insertItemAt(PaSortOrderEnum.toString(PaSortOrderEnum.ID_ORDER),
				PaSortOrderEnum.toInt(PaSortOrderEnum.ID_ORDER));
		
		insertItemAt(PaSortOrderEnum.toString(PaSortOrderEnum.CUSTOM_ORDER),
		PaSortOrderEnum.toInt(PaSortOrderEnum.CUSTOM_ORDER));
		
		insertItemAt(PaSortOrderEnum.toString(PaSortOrderEnum.PRINTED_ORDER),
				PaSortOrderEnum.toInt(PaSortOrderEnum.PRINTED_ORDER));
		
		insertItemAt(PaSortOrderEnum.toString(PaSortOrderEnum.BOOOMAKED_ORDER),
				PaSortOrderEnum.toInt(PaSortOrderEnum.BOOOMAKED_ORDER));
		
		insertItemAt(PaSortOrderEnum.toString(PaSortOrderEnum.LINKS_ORDER),
				PaSortOrderEnum.toInt(PaSortOrderEnum.LINKS_ORDER));
		
		insertItemAt(PaSortOrderEnum.toString(PaSortOrderEnum.ST_FOLDER_ORDER),
				PaSortOrderEnum.toInt(PaSortOrderEnum.ST_FOLDER_ORDER));
	}
}
