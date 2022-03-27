package PaDialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import PaCollection.PaAlbum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaCloseFlag;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;
/**
 * Dialog for subjects delete operation
 * @author avd
 */
public class PaSubjectDeleteDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JRadioButton case_1 = new JRadioButton(getGuiStrs("deleteFromCurrentAlbomLabel"), true); 

	private JRadioButton case_2 = new JRadioButton(getGuiStrs("deleteFromAllAlbomsLabel"));
	
	private JButton m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
	
	private JButton m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
	
	public enum DeleteCase {
		
		CLEAR_SUB_FROM_ALBOM,
		
		DELETE_SUB_AT_ALL
		
	}
	
	private Forwarder forwarder = new Forwarder();
	
	private PaCloseFlag flag = PaCloseFlag.CANCEL;
	
	private JPanel panel_But = new JPanel();
	
	private JPanel panel_ButR = new JPanel();
	
	private ButtonGroup butGroup = new ButtonGroup(); 
	
	private JComboBox<String> comboAlboms;
	
	private JLabel comboLabel;
	
	private String selectedAlbom = new String();
	
	public PaSubjectDeleteDialog (JFrame jf, String nameFrame, String keywordName) {
		
		super(jf, nameFrame, true);
		
		initCombo();

		setLayout(new BorderLayout());	
		
		setBounds(650, 450, 400, 200);
		
		setResizable(false);
		
		case_1.setActionCommand("01");
		
		case_2.setActionCommand("02");
		
		JPanel panelCombo = PaGuiTools.createHorizontalPanel();
		
		comboLabel = new JLabel("        "+getGuiStrs("selectedAlbomLabel")+"  ");
		
		panelCombo.add(comboLabel);
		
		panelCombo.add(comboAlboms);
		
		panel_ButR.setLayout(new GridLayout(5, 1));
		
		panel_ButR.add(case_1);
		
		panel_ButR.add(Box.createVerticalStrut(6));
		
		panel_ButR.add(panelCombo);
		
		panel_ButR.add(Box.createVerticalStrut(12));
		
		panel_ButR.add(case_2);
		
		butGroup.add(case_1);
		
		butGroup.add(case_2);
		
		panel_But.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 6));	
		
		panel_But.add(m_OkButton);
		
		panel_But.add(m_CancelButton);
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		case_1.addActionListener(forwarder);
		
		case_2.addActionListener(forwarder);
		
		comboAlboms.addActionListener(forwarder);
		
		m_OkButton.setMnemonic(KeyEvent.VK_O);
		
		m_CancelButton.setMnemonic(KeyEvent.VK_C);
		
		Border etched = BorderFactory.createEtchedBorder();
		
		Border titled = BorderFactory.createTitledBorder(etched, getGuiStrs("deleteOptionsGroupLabel"),
				TitledBorder.LEFT,TitledBorder.TOP,PaUtils.get().getBaseFont());
		
		panel_ButR.setBorder(titled);
		
		add(panel_ButR, BorderLayout.CENTER);
		
		add(panel_But, BorderLayout.SOUTH);
		
		setToolTips();
		
		PaUtils.setComponentsFont (this, PaUtils.get().getBaseFont() );
				
	}
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			if ( e.getSource() == comboAlboms ) {
				
				selectedAlbom = (String) comboAlboms.getSelectedItem();
			}	
			if ( e.getSource() ==  case_1 || e.getSource() ==  case_2 ) {
						
					comboLabel.setEnabled(case_1.isSelected());
					
					comboAlboms.setEnabled(case_1.isSelected());
			
			}
			if ( e.getSource() == m_OkButton ) {
				
				flag = PaCloseFlag.OK;		
				
				dispose();
			}
			if ( e.getSource() == m_CancelButton ) {
				
				flag = PaCloseFlag.CANCEL;	
				
				dispose();
			}		
		}
	}
	/**
	 * <p>Initiates the combo with albom's names<p>
	 */
	private void initCombo() {
		
		Iterator<PaAlbum> it=PaUtils.get().getAlbumContainer().iterator();
		
		int s =  PaUtils.get().getAlbumContainer().size();
		
		if(s == 0) {
			
			comboAlboms = new JComboBox<String>();
			
			comboAlboms.setEnabled(false);
			
			return;
		}
		
		String[] ar = new String[s];
		
		int i = 0;
		
		while(it.hasNext()) {
			
			ar[i] = it.next().getName();
			
			++i;
		}
		
		comboAlboms = new JComboBox<String>(ar);
		
		selectedAlbom = ar[0];
		
	}
	/**
	 * 
	 * @return the chosen case of delete operation
	 */
	public  DeleteCase  getCase() {
		
		if(case_1.isSelected()) { return DeleteCase.CLEAR_SUB_FROM_ALBOM; }
		
		if(case_2.isSelected()) { return DeleteCase.DELETE_SUB_AT_ALL; }
		
		return DeleteCase.CLEAR_SUB_FROM_ALBOM;
	}
	
	public PaCloseFlag getClosedFlag () {
		
		return flag;
	}
	
	public String getSelectedAlbom() {
		
		return selectedAlbom;
		
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		case_1.setToolTipText(getGuiStrs("subjectDeleteCase1ToolTip"));
		
		case_2.setToolTipText(getGuiStrs("subjectDeleteCase2ToolTip"));
		  
        m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
        
        m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
}

