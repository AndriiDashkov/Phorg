package padialogs;

import static paglobal.PaUtils.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import paglobal.PaGuiTools;
import paglobal.PaUtils;

/**
 * Dialog for addition of group of images
 * @author Andrii Dashkov
 *
 */
public class PaImagesGroupNewDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField m_path_ph = new JTextField(25);	//path to the images
	
	private static String m_prevPath = null;
	
	private JTextField m_pathNew_ph = new JTextField(25);
	
	private JFileChooser m_sourcePhotoFiles;

	private Forwarder m_forwarder = new Forwarder();
	
	private CloseFlag flag = CloseFlag.CANCEL;
	
	private Font m_font = PaUtils.get().getBaseFont(); // main base font
	
	JRadioButton m_radioBut_1 = new JRadioButton(getGuiStrs("copyImagesAsLinksButtonCaption"), true); 

	JRadioButton m_radioBut_2 = new JRadioButton(getGuiStrs("copyImagesToStandartFolderButtonCaption")); 

	JRadioButton m_radioBut_3 = new JRadioButton(getGuiStrs("copyImagesToSpecialFolderButtonCaption")); 

	
	public enum CloseFlag {
		OK,
		CANCEL,
		COPY_IMAGES_AS_LINKS,
		COPY_IMAGES_IN_STANDARD,
		COPY_IMAGES_IN_SEL_FOLDER
		
	}
	
	JCheckBox m_checkRecur = new JCheckBox(getGuiStrs("recursiveAlbomsCheckBoxCaption")); 

	JButton m_selectPath = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	JButton m_selectPathNew = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	JButton m_Ok, m_Cancel;
	
	ButtonGroup m_butGroup = new ButtonGroup();
	
	JLabel m_infoLabel = new JLabel("       ");
	
	public PaImagesGroupNewDialog (JFrame jfrm, String nameFrame) {
		super (jfrm, nameFrame, true);
		
		m_path_ph.setBorder(BorderFactory.createLoweredBevelBorder());
		
		m_pathNew_ph.setBorder(BorderFactory.createLoweredBevelBorder());
		
		m_pathNew_ph.setText(PaUtils.get().getSettings().getStandardFolderPlace());
		
		m_selectPathNew.setEnabled(false);
		
		m_pathNew_ph.setEnabled(false);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
				
		add(createGUI());
		
		setBounds(250, 150, 225, 350);

		pack();
		
		setMinimumSize(new Dimension(600,getHeight()));
		
		setResizable(false);
	}
	
	/**
	 * Creates UI components
	 * @return the main panel
	 */
	private JPanel createGUI() {
	
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();
		
		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(VERT_STRUT,VERT_STRUT,VERT_STRUT,VERT_STRUT));
		
		JPanel panel_name = PaGuiTools.createHorizontalPanel();	
		
		JLabel path_ph_lab = new JLabel (getGuiStrs("folderForGroupAddingOperationLabel")); 
		
		path_ph_lab.setFont(m_font);
		
		panel_name.add(path_ph_lab);
		
		panel_name.add(Box.createHorizontalGlue());
		
		JPanel panel_path = PaGuiTools.createHorizontalPanel();		

		panel_path.add(m_path_ph);
		
		panel_path.add(Box.createHorizontalStrut(HOR_STRUT));
		
		panel_path.add(m_selectPath);
		
		panel_path.add(Box.createHorizontalGlue());
		
		JPanel panelCheck = PaGuiTools.createHorizontalPanel();		
		
		panelCheck.add(m_checkRecur);
		
		panelCheck.add(Box.createHorizontalGlue());
		
		m_radioBut_1.setActionCommand("01");
		
		m_radioBut_2.setActionCommand("02");
		
		m_radioBut_3.setActionCommand("03");
		
		JPanel panel_radioBut = new JPanel(new GridLayout(3, 1));
		
		panel_radioBut.add(m_radioBut_1);
		
		panel_radioBut.add(m_radioBut_2);
		
		panel_radioBut.add(m_radioBut_3);
		
		m_butGroup.add(m_radioBut_1);
		
		m_butGroup.add(m_radioBut_2);
		
		m_butGroup.add(m_radioBut_3);
		
		m_radioBut_1.setFont(m_font);
		
		m_radioBut_2.setFont(m_font);
		
		m_radioBut_3.setFont(m_font);
		
		m_checkRecur.setFont(m_font);
		
		
		JPanel panel_pathNew = PaGuiTools.createHorizontalPanel();	
		
		panel_pathNew.add(m_pathNew_ph);
		
		panel_pathNew.add(Box.createHorizontalStrut(HOR_STRUT));
		
		panel_pathNew.add(m_selectPathNew);
		
		panel_pathNew.add(Box.createHorizontalGlue());
		
		JPanel panel_border = PaGuiTools.createVerticalPanel();
		
		panel_border.add(panel_radioBut);
		
		panel_border.add(Box.createVerticalStrut(HOR_STRUT));
		
		panel_border.add(panel_pathNew);
		
		panel_border.add(Box.createVerticalStrut(HOR_STRUT));
		

		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("optionsForFilesMovingLabel"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
		
		panel_border.setBorder(titled_0);
		

		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,3,5,0) );
		
		m_Ok = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_Cancel = new JButton(getGuiStrs("buttonCancelCaption"));
		
		panel_Ok_Cancel.add(m_Ok);
		
		panel_Ok_Cancel.add(m_Cancel);

		south_right.add(panel_Ok_Cancel);
		
		JPanel panelInfo = PaGuiTools.createHorizontalPanel();	
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);
		
		panelInfo.add(m_infoLabel);
		
		panelInfo.add(Box.createHorizontalGlue());
						
		PaGuiTools.setGroupAlignmentY(
				new JComponent[] {panel_name, panel_path, panel_border, south_right, panelCheck, panel_MAIN },
											Component.RIGHT_ALIGNMENT);
		PaGuiTools.setGroupAlignmentY(
				new JComponent[] { m_path_ph, m_pathNew_ph, m_radioBut_1, m_radioBut_2, m_radioBut_3, path_ph_lab},
											Component.CENTER_ALIGNMENT);

		paglobal.PaGuiTools.createRecommendedMargin(new JButton[] { m_Ok, m_Cancel, m_selectPath, m_selectPathNew } );

		//reduce the endlecc height of components
		PaGuiTools.fixTextFieldSize(m_path_ph);
		
		PaGuiTools.fixTextFieldSize(m_pathNew_ph);

		panel_MAIN.add(panel_name);
		
		panel_MAIN.add(Box.createVerticalStrut(VERT_STRUT));	
		
		panel_MAIN.add(panel_path);
		
		panel_MAIN.add(Box.createVerticalStrut(VERT_STRUT));	
		
		panel_MAIN.add(panel_border);
		
		panel_MAIN.add(Box.createVerticalStrut(VERT_STRUT));	
		
		panel_MAIN.add(panelCheck);
		
		panel_MAIN.add(Box.createVerticalStrut(VERT_STRUT));
		
		panel_MAIN.add(panelInfo);
		
		panel_MAIN.add(Box.createVerticalStrut(VERT_STRUT));
		
		panel_MAIN.add(south_right);	
		
		
		setAllListeners();

		m_checkRecur.setToolTipText(getGuiStrs("recursiveAlbomsCheckBoxToolTip"));
			
		return panel_MAIN;
	}
	
	private void setAllListeners() {
		
		m_selectPath.addActionListener(m_forwarder);
		
		m_selectPathNew.addActionListener(m_forwarder);
		
		m_Ok.addActionListener(m_forwarder);
		
		m_Cancel.addActionListener(m_forwarder);
		
		m_checkRecur.addActionListener(m_forwarder);
		
		class PaFocusListener implements FocusListener
		{

			@Override
			public void focusGained(FocusEvent arg0) {
		
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			
				 verifyInfo();
				
			}
		    
		}
		
		PaFocusListener fListener = new PaFocusListener();
		
		m_path_ph.addFocusListener(fListener);
		
		m_pathNew_ph.addFocusListener(fListener);
		
		m_radioBut_1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {

				m_selectPathNew.setEnabled(false);
				
				m_pathNew_ph.setEnabled(false);
				
				m_pathNew_ph.setEditable(false);
			}
		});
		
		m_radioBut_2.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				m_pathNew_ph.setText(PaUtils.get().getSettings().getStandardFolderPlace());
				
				m_selectPathNew.setEnabled(false);
				
				m_pathNew_ph.setEnabled(true);
				
				m_pathNew_ph.setEditable(false);

			}
		});
		
		m_radioBut_3.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {

				m_selectPathNew.setEnabled(true);
				
				m_pathNew_ph.setEnabled(true);
				
				m_pathNew_ph.setEditable(true);
			}
		});
		
		
	}
	
	
	class Forwarder implements ActionListener {
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_Ok ) onOK(e);
			
			if ( e.getSource() == m_Cancel ) onCancel(e);
			
			if ( e.getSource() == m_selectPath ) onSelectPath(e);
			
			if ( e.getSource() == m_selectPathNew ) onSelectPathNew(e);
			
			if ( e.getSource() == m_checkRecur ) onSelectCheckRecur(e);
		}
	}
	

	
	public void onOK(ActionEvent e) {

		if ((m_butGroup.getSelection().getActionCommand()).equals("01")) {
			
			flag = CloseFlag.COPY_IMAGES_AS_LINKS;
			
			dispose();
		}
		if ((m_butGroup.getSelection().getActionCommand()).equals("02")) {
			
			flag = CloseFlag.COPY_IMAGES_IN_STANDARD;
			
			dispose();
		}		
		if ((m_butGroup.getSelection().getActionCommand()).equals("03")) {
			
			flag = CloseFlag.COPY_IMAGES_IN_SEL_FOLDER;
			
			dispose();
		}	
	}
	
	/**
	 * Cancel reaction
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
		
		flag = CloseFlag.CANCEL;
		
		dispose();
	}	
	
	/**
	 * Selection of source folder
	 * @param e
	 */
	public void onSelectPath(ActionEvent e) {
		
		m_sourcePhotoFiles = new JFileChooser ();
		
		m_sourcePhotoFiles.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		if( m_prevPath != null && m_path_ph.getText().isEmpty()) {
			
			m_sourcePhotoFiles.setCurrentDirectory(new File(m_prevPath));
			
		}
		else {
			
			m_sourcePhotoFiles.setCurrentDirectory(new File(PaUtils.get().getSettings().getStandardFolderPlace() + "."));
		}
		
		int result = m_sourcePhotoFiles.showOpenDialog(PaImagesGroupNewDialog.this);
		//int result = m_sourcePhotoFiles.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = m_sourcePhotoFiles.getSelectedFile().getPath();
			
			m_path_ph.setText(name + getSeparator());	
			
			m_prevPath = new String(name+ getSeparator());
			
			verifyInfo();
		}	
	}
	
	/**
	 * Select the new path to save an image
	 * @param e
	 */
	public void onSelectPathNew(ActionEvent e) {
		
		m_sourcePhotoFiles = new JFileChooser ();
		
		m_sourcePhotoFiles.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = m_sourcePhotoFiles.showOpenDialog(PaImagesGroupNewDialog.this);
		
		//if file is ok then we create icon
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = m_sourcePhotoFiles.getSelectedFile().getPath();
			
			m_pathNew_ph.setText(name + getSeparator());
			
			verifyInfo();
		}
	}
	/**
	 * <p>If we choose to create alboms in recursive way then we have to disable the option 
	 * 'copy to folder', because you can't copy all files from all created alboms to one folder</p>
	 * @param e action
	 */
	public void onSelectCheckRecur(ActionEvent e) {
		
		if(m_checkRecur.isSelected()){
			
			if ((m_butGroup.getSelection().getActionCommand()).equals("03")) {
				
				m_radioBut_1.setSelected(true);
			}
			m_radioBut_3.setEnabled(false);
		}
		else {			
			m_radioBut_3.setEnabled(true);
		}	
	}
	/**
	 * 
	 * @return close flag to determine what to do with operation after dialog close
	 */
	public CloseFlag getClosedFlagValue () {
		
		return flag;
	}
	
	public String getSourcePath() {
		
		return m_path_ph.getText();
	}
	
	
	public String getPathNew() {
		
		return m_pathNew_ph.getText();
	}
	
	public boolean isRecurSelected() { return m_checkRecur.isSelected(); }
	
	
	private void verifyInfo() {
		
	   boolean flag = true;
	   
	   String text = new String();
	   
	   String s1 =checkFilePermisions(m_path_ph.getText(),true, false);
	   
		if ( ! s1.isEmpty()) {
			
			flag = false;
			
			text = s1 + " : " +m_path_ph.getText();
		}
		
		File f = new File(m_path_ph.getText());
		
		if ( ! f.isDirectory() ) {
			
			flag = false;
			
			text = getMessagesStrs("notDirectoryMessage") +" "+m_path_ph.getText();
		}
		
		String s2 = checkFilePermisions(m_pathNew_ph.getText(),false, true);
		   
		if ( ! s2.isEmpty()) {
			
			flag = false;
			
			text = s2 + " : " +m_pathNew_ph.getText();
		}
			
		
		
		if ( flag ) {
			
			m_infoLabel.setText("");
			
			m_Ok.setEnabled(true);
		}
		else {
		
			m_infoLabel.setText(text);
			
			m_Ok.setEnabled(false);
	
		}
		
		
	}
}
