package PaDialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import PaForms.PaAlbumsTreeForm;
import PaForms.PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES;
import PaGlobal.PaButtonEnter;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

public class PaAlbumsDelDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;
	
	private JRadioButton case_01 = new JRadioButton(" "+getGuiStrs("clearAlbomRadioButtonCaption"), true); 
	
	private JRadioButton case_02 = new JRadioButton(" "+getGuiStrs("deleteAlbomRadioButtonCaption")); 
	
	private JRadioButton case_11 = new JRadioButton(" "+getGuiStrs("noDeleteImagesRadioButtonCaption"), true);
	
	private JRadioButton case_12 = new JRadioButton(" "+getGuiStrs("deleteImagesRadioButtonCaption"));
	
	
	private JCheckBox m_checkBox = new JCheckBox(" "+getGuiStrs("childAlbomOperationsEnabled")); 
	
	
	private PaButtonEnter m_OkButton = new PaButtonEnter(getGuiStrs("buttonOkCaption"));
	
	private PaButtonEnter m_CancelButton = new PaButtonEnter(getGuiStrs("buttonCancelCaption"));
	
	private Forwarder forwarder = new Forwarder();
	
	private  PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES flag = ALBOMS_CLEAR_TYPES.NO_OPERATION ;
	
	private ButtonGroup butGroup_0 = new ButtonGroup(); 
	
	private ButtonGroup butGroup_1 = new ButtonGroup();
	
	private String albomName;
	
	public PaAlbumsDelDialog (JFrame jf, String name_alb) {
		
		super (jf, " " + getGuiStrs("clearAndDeleteAlbomCaption") + "  " + name_alb, true); //""Очистка\\Удаление альбома : "
		
		albomName = new String(name_alb);
		
		setLayout(new BorderLayout());	
		
		setBounds(450, 350, 420, 260);
		
		setResizable(true);
		
		case_01.setActionCommand("01");
		
		case_02.setActionCommand("02");
		
		case_11.setActionCommand("11");
		
		case_12.setActionCommand("12");
		
		JPanel gridPanel = new JPanel();
		
		JPanel panel_But = new JPanel();
		
		JPanel panel_ButR_0 = new JPanel();
		
		JPanel panel_ButR_1 = new JPanel();
			
		panel_ButR_0.setLayout(new GridLayout(2, 1));
		
		panel_ButR_0.add(case_01);
		
		panel_ButR_0.add(case_02);
		
		butGroup_0.add(case_01);
		
		butGroup_0.add(case_02);

		panel_ButR_1.setLayout(new GridLayout(2, 1));
		
		panel_ButR_1.add(case_11);
		
		panel_ButR_1.add(case_12);
		
		butGroup_1.add(case_11);
		
		butGroup_1.add(case_12);
		
		gridPanel.setLayout(new GridLayout(3, 1));
		
		gridPanel.add(panel_ButR_0);
		
		gridPanel.add(panel_ButR_1);
		
		gridPanel.add(m_checkBox);
		
		panel_But.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 6));
		
		JLabel infoLabel = new JLabel(getMessagesStrs("messageOperationWillSave")); 

		infoLabel.setForeground( PaUtils.get().getSelectionColor() );
		
		panel_But.add(infoLabel);
		
		panel_But.add(m_OkButton);
		
		panel_But.add(m_CancelButton);
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_OkButton.setMnemonic(KeyEvent.VK_O);
		
		m_CancelButton.setMnemonic(KeyEvent.VK_C);
		
		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("operationsOptionCaptionName")); 
		
		panel_ButR_0.setBorder(titled_0);
		
		Border etched_1 = BorderFactory.createEtchedBorder();
		
		Border titled_1 = BorderFactory.createTitledBorder(etched_1, getGuiStrs("optionsCaptionName"));
		
		panel_ButR_1.setBorder(titled_1);
		
		add(gridPanel, BorderLayout.CENTER);
		
		add(panel_But, BorderLayout.SOUTH);
		
		setToolTips();
	}
	
    class MyItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

        }
    }
	 	
	/**
	 * 
	 * @author avd
	 * <p>Event listener for buttons m_Ok and m_Cancel</p>
	 */
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);		
		
		}
	}
	
	/**
	 * <p>Sets the flag to the  appropriative way and asks the user about confirmation<p>
	 * @param e event
	 */
	public void onOK(ActionEvent e) {	
		
       String operationName = new String();
		
		if ((butGroup_0.getSelection().getActionCommand()).equals("01") & (butGroup_1.getSelection().getActionCommand()).equals("11")) {
			flag = PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES.CLEAR_LINKS_ONLY;
			
			operationName = albomName +" : "+getMessagesStrs("statrAlbomCleanQuestion")+NEXT_ROW 
					+ getMessagesStrs("thisIsNotUndoOperation")+NEXT_ROW		
					+getMessagesStrs("imagesFilesWillNotBeDeleted");
		
		}
		if ((butGroup_0.getSelection().getActionCommand()).equals("01") & (butGroup_1.getSelection().getActionCommand()).equals("12")) {
			
			flag = PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES.CLEAR_WITH_FILES;
			operationName = albomName +" : " + getMessagesStrs("statrAlbomCleanQuestion") + NEXT_ROW 
					+ getMessagesStrs("thisIsNotUndoOperation")+NEXT_ROW
					+getMessagesStrs("imagesFilesWillBeDeleted"); 
			
		}
		if ((butGroup_0.getSelection().getActionCommand()).equals("02") & (butGroup_1.getSelection().getActionCommand()).equals("11")) {
			
			flag =  PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES.DELETE_WITHOUT_FILES;
			operationName = albomName +" : " + getMessagesStrs("statrAlbomDeleteQuestion") + NEXT_ROW
					+ getMessagesStrs("thisIsNotUndoOperation")+NEXT_ROW
					+getMessagesStrs("imagesFilesWillNotBeDeleted");
		
		}
		if ((butGroup_0.getSelection().getActionCommand()).equals("02") & (butGroup_1.getSelection().getActionCommand()).equals("12")) {
			
			flag = PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES.DELETE_WITH_FILES;
			operationName = albomName +" : " + getMessagesStrs("statrAlbomDeleteQuestion")+NEXT_ROW
					+ getMessagesStrs("thisIsNotUndoOperation")+NEXT_ROW
					+getMessagesStrs("imagesFilesWillBeDeleted");
		
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
	

	/**
	 * 
	 * @param e event
	 */
	public void onCancel(ActionEvent e) {
		
		flag = PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES.NO_OPERATION;
		
		dispose();
	}
	public PaAlbumsTreeForm.ALBOMS_CLEAR_TYPES getClosedFlagValue () {
		
		return flag;
	}
	
	/**
	 * @return true if the checkbox about child albom's is on
	 */
	public boolean isChildAlbomsInvolved() { return m_checkBox.isSelected(); }
	
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		case_01.setToolTipText(getGuiStrs("clearAlbomRadioButtonToolTip"));
		
		case_02.setToolTipText(getGuiStrs("deleteAlbomRadioButtonToolTip"));
		
		case_11.setToolTipText(getGuiStrs("noDeleteImagesAlbomToolTip"));
		
		case_12.setToolTipText(getGuiStrs("deleteImagesAlbomToolTip"));
				
		m_checkBox.setToolTipText(getGuiStrs("childAlbomCheckBoxDeleteToolTip"));
				 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
}
