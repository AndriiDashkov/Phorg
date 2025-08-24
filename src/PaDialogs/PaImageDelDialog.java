package padialogs;

import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import paglobal.PaUtils;

public class PaImageDelDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JRadioButton case_1 = new JRadioButton(" "+getGuiStrs("deleteImageOneAlbomRadioBut"), true); 

	private JRadioButton case_2 = new JRadioButton(" "+getGuiStrs("deleteImageAllAlbomRadioBut")); 
	
	private JRadioButton case_11 = new JRadioButton(" "+getGuiStrs("noDeleteImagesRadioButtonCaption"), true); 

	private JRadioButton case_12 = new JRadioButton(" "+getGuiStrs("deleteImagesRadioButtonCaption")); 
	
	private JButton Ok = new JButton( getGuiStrs("buttonOkCaption") );
	
	private JButton Cancel = new JButton( getGuiStrs("buttonCancelCaption") );	
	
	private Forwarder forwarder = new Forwarder();
	
	private CloseFlag m_closeFlag;
	
	private JPanel panel_But = new JPanel();
	
	private JPanel panel_ButR = new JPanel();
	
	private JPanel panel_ButR2 = new JPanel();
	
	private ButtonGroup butGroup = new ButtonGroup();
	
	private ButtonGroup butGroup2 = new ButtonGroup(); 
	
	
	public enum CloseFlag {
		
		CANCEL,
		
		CLEAR_ALBOM_SAVE_FILES,
		
		CLEAR_ALBOM_DELETE_FILES,
		
		DELETE_ALBOM_SAVE_FILES,
		
		DELETE_ALBOM_DELETE_FILES	
	};

	public PaImageDelDialog (JFrame jfrm, String nameFrame) { 
		
		super (jfrm, nameFrame, true);
		
		setLayout(new BorderLayout());	
		
		setBounds(450, 350, 400, 220);
		
		setResizable(true);

		case_1.setActionCommand("01");
		
		case_2.setActionCommand("02");
		
		case_11.setActionCommand("11");
		
		case_12.setActionCommand("12");
		
		panel_ButR.setLayout(new GridLayout(2, 1));
		
		panel_ButR.add(case_1);
		
		panel_ButR.add(case_2);
		
		butGroup.add(case_1);
		
		butGroup.add(case_2);	
		
		panel_ButR2.setLayout(new GridLayout(2, 1));
		
		panel_ButR2.add(case_11);
		
		panel_ButR2.add(case_12);
		
		butGroup2.add(case_11);	
		
		butGroup2.add(case_12);	
		
		panel_But.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 6));
		
		panel_But.add(Ok);
		
		panel_But.add(Cancel);
		
		Ok.addActionListener(forwarder);
		
		Cancel.addActionListener(forwarder);
		
		Ok.setMnemonic(KeyEvent.VK_O);
		
		Cancel.setMnemonic(KeyEvent.VK_C);
		
		Border etched = BorderFactory.createEtchedBorder();

		Border titled = BorderFactory.createTitledBorder(etched,  getGuiStrs("operationsOptionCaptionName"));
		
		panel_ButR.setBorder(titled);
		
		Border titled1 = BorderFactory.createTitledBorder(etched, getGuiStrs("optionsCaptionName"));
		
		panel_ButR2.setBorder(titled1);
		
		add(panel_ButR, BorderLayout.NORTH);
		
		add(panel_ButR2, BorderLayout.CENTER);
		
		add(panel_But, BorderLayout.SOUTH);
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			if ( e.getSource() == Ok ) onOK(e);
			
			if ( e.getSource() == Cancel ) onCancel(e);			
		}
	}

	public void onOK(ActionEvent e) {	
		
	       String operationName = new String();
			
			if ((butGroup.getSelection().getActionCommand()).equals("01") & (butGroup2.getSelection().getActionCommand()).equals("11")) {
				
				m_closeFlag = CloseFlag.CLEAR_ALBOM_SAVE_FILES; //1;
				
				operationName = getMessagesStrs("startPhotoDeleteQuestion")+NEXT_ROW +getMessagesStrs("imagesFilesWillNotBeDeleted"); 
			
			}
			if ((butGroup.getSelection().getActionCommand()).equals("01") & (butGroup2.getSelection().getActionCommand()).equals("12")) {
				
				m_closeFlag = CloseFlag.CLEAR_ALBOM_DELETE_FILES; //2;
				
				operationName = getMessagesStrs("startPhotoDeleteQuestion") + NEXT_ROW +getMessagesStrs("imagesFilesWillBeDeleted"); 
				
				
			}
			if ((butGroup.getSelection().getActionCommand()).equals("02") & (butGroup2.getSelection().getActionCommand()).equals("11")) {
				
				m_closeFlag = CloseFlag.DELETE_ALBOM_SAVE_FILES; //3;
				
				operationName = getMessagesStrs("startPhotoDeleteQuestion") + NEXT_ROW +getMessagesStrs("imagesFilesWillNotBeDeleted");
			
			}
			if ((butGroup.getSelection().getActionCommand()).equals("02") & (butGroup2.getSelection().getActionCommand()).equals("12")) {
				
				m_closeFlag = CloseFlag.DELETE_ALBOM_DELETE_FILES; //4;
				
				operationName = getMessagesStrs("startPhotoDeleteQuestion")+NEXT_ROW +getMessagesStrs("imagesFilesWillBeDeleted");
			
			}
			
			int n = JOptionPane.showConfirmDialog(
				    PaUtils.get().getMainWindow(),
				    operationName,
				    getMessagesStrs("messageAnswerCaption"),
				    JOptionPane.YES_NO_OPTION);
		
			if ( n == JOptionPane.YES_OPTION) {
				
				dispose();
			
			}
	}

	public void onCancel(ActionEvent e) {
		
		m_closeFlag = CloseFlag.CANCEL;
		
		dispose();
	}
	/**
	 * 
	 * @return close flag indicator - what operation has been done by user
	 */
	public CloseFlag getClosedFlagValue () {
		
		return m_closeFlag;
	}
}
