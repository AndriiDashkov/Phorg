package PaDialogs;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;
/**
 * 
 * @author avd
 * <p>"About" dialog</p>
 */
public class PaAboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public  PaAboutDialog  (JFrame jfrm) {
		
		super (jfrm, getGuiStrs("aboutDialogCaption"), true); 
				
		getContentPane().add(createGUI(),BorderLayout.CENTER);
		
		setBounds(250, 150, 225, 220);

		pack();
		
		setResizable(false);
	}
	
	/**
	 * @return main Gui panel
	 */
	private JPanel createGUI () {
		
		JPanel panelMain = new JPanel();

		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

		panelMain.setBorder( BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
				
		JLabel pNameLabel = new JLabel("Photo organizer");
		
		JLabel versionLabel = new JLabel(getGuiStrs("versionAbout") + " 1.19.22");
		
		JLabel design1Label = new JLabel(getGuiStrs("codingAbout"));
		
		JLabel design2Label = new JLabel(getGuiStrs("contactAbout"));
		
		JLabel yearLabel = new JLabel(getGuiStrs("placeAbout"));
		
		panelMain.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panelMain.add(pNameLabel);
		
		panelMain.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panelMain.add(versionLabel );
		
		panelMain.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panelMain.add(design1Label);
		
		panelMain.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panelMain.add(design2Label);
		
		panelMain.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panelMain.add(yearLabel);
	
		panelMain.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
	
		return panelMain;
	}
	
	
}
