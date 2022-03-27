
package PaDialogs;

import static PaGlobal.PaUtils.VERT_STRUT;
import static PaGlobal.PaUtils.getGuiStrs;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;


/**
 * @author avd
 *
 */
public class PaAlbumProblemsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTable m_table;
	
	private int m_problemsCounter;
	
	private String m_albomName = null;
	
	/**
	 * This dialog shows the table with the list of problems of specific album. Initiates through the task from PaLong
	 * and albom's property dialog PaAlbomPropDialog class
	 * @param mainF - parent frame for this dialog
	 * @param t - table with list of problems; it is initiated in PaAlbomImageControl class for long tasks
	 * @param albomId - the id of an album for which the table t keeps information 
	 * @param albomName - the name of an album for which the table t keeps information
	 * @param prCounter - total number of problem issues (just for info label)
	 */

	public PaAlbumProblemsDialog(JFrame mainF,JTable t, String albomName, int prCounter)
	{
		super (mainF, getGuiStrs("albomCheckResultCaptionDialog"), true); 
	
		m_table = t;
		
		m_albomName = albomName;
		
		m_problemsCounter = prCounter;
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
		
		add(createGUI());
		
		setBounds(250, 150, 200, 220);

		pack();
	
	}
	
	/**
	 * @return main panel of the dialog
	 *
	 */
	private JPanel createGUI () {
		
		Font f = PaUtils.get().getBaseFont();
		
		setFont(f);
		
		JPanel mainPanel = PaGuiTools.createVerticalPanel();

		mainPanel.setBorder( BorderFactory.createEmptyBorder(VERT_STRUT,VERT_STRUT,VERT_STRUT,
				VERT_STRUT));
		
		JPanel labelPanel = PaGuiTools.createHorizontalPanel();
		
		JLabel infoLabel = new JLabel(getGuiStrs("nameAlbomProblemDialog") + " " + m_albomName +
			" " + getGuiStrs("foundAlbomProblemCaption") +" " + m_problemsCounter);
		
		labelPanel.add(infoLabel);
		
		labelPanel.add(Box.createHorizontalGlue());
		
		JScrollPane scrol = new JScrollPane(m_table);
		
		mainPanel.add(labelPanel);
		
		mainPanel.add(scrol);
				
		return mainPanel;
	}

}
