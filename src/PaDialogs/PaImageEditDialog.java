
package PaDialogs;

import static PaGlobal.PaUtils.*;
import javax.swing.JFrame;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;

/**
 * @author avd
 *
 */
public class PaImageEditDialog extends PaImageNewDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * @param jfrm parent frame
	 * @param nameFrame - name
	 * @param pC - current container
	 * @param photo - image to edit
	 */
	public PaImageEditDialog(JFrame jfrm, String nameFrame, PaImage photo,PaImageContainer pC) {
		
		super(jfrm, nameFrame, photo.getName(), photo.getFullPath(), 
				dateToString( photo.getDate(),GUI_DATE_FORMAT ), photo.getSubjectsList(), 
				pC,photo.getId(), true);
		
		m_rButton3.setSelected( photo.isPrinted());
		
		m_rButton4.setSelected( photo.isBookmarked());
		
		m_commentsEdit.setText(photo.getComments());
		
	
	}

	/**
	 * <p>Initiation function</p>
	 */
	protected void init() {
		
		m_rButton1.setEnabled(false);
		
		m_rButton2.setEnabled(false);
		
		m_pathPhoto.setEnabled(false);
		
		m_selectPath.setEnabled(false);
	}

}
