package padialogs;

import static paglobal.PaLog.*;
import static paglobal.PaUtils.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pacollection.PaAlbum;
import pacollection.PaAlbumContainer;
import paforms.PaAlbumTreeNode;
import paglobal.*;

/**
 * Dialog for enter and edit an album
 * @author Andrii Dashkov
 *
 */
public class PaAlbumNewDialog extends JDialog {
	
	
	private static final long serialVersionUID = 1L;
	
	protected JTextField m_name = new JTextField(25);
	
	protected JTextField m_path_alb = new JTextField(25);
	
	protected JTextField m_date = new JTextField(25);
	
	protected JTextField m_coment = new JTextField(25);
	
	private JButton m_OkButton = new PaButtonEnter(getGuiStrs("buttonOkCaption"));
	
	private JButton m_CancelButton = new PaButtonEnter(getGuiStrs("buttonCancelCaption"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JLabel m_infoLabel = new JLabel(" ");
	
	protected int m_id = -5;
	
	protected String folderName;
	
	private JCheckBox m_checkBoxStandartPath;
	
	private JCheckBox m_checkBoxLoadAlbom;
	
	private JComboBox<String> m_parentsCombo;
	
	private Map<String,Integer> m_parentsMap = new HashMap<String,Integer>();
	
	private Forwarder forwarder = new Forwarder();
	
	protected int flag = 0;
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	 public  PaAlbumNewDialog(JFrame jf,PaAlbumContainer rContainer, int id,String title) {
		 
		super (jf, title, true);
		
		add(createGUI());
		
		setBounds(250, 150, 400, 220);
		
		pack();
		
		setResizable(false);
		 
		init( rContainer, id); 
		 
		setToolTips();
	 }
	 
	 
	 public  PaAlbumNewDialog(ArrayList<PaAlbumTreeNode> parentsList,JFrame jf,
			 PaAlbumContainer rContainer,int id,String title) 
	 {
		 this(jf,rContainer,id,title);
		 
		 initParentsList(parentsList);
	 }
	 
	 

	private void init( PaAlbumContainer rContainer, int idN ) 
	{
		
		m_id = idN;
		
		 if ( idN != 0 ) {//edit case
			 
			 PaAlbum  albom= rContainer.getAlbum(idN);
			 
			 if (albom != null ) {
				 
				m_name.setText(albom.getName()); 
				
				m_path_alb.setText(albom.getFullStandardPath()); 
				
				m_date.setText( albom.getDateAsString(GUI_DATE_FORMAT) ); 
				
				m_coment.setText(albom.getComment());
				
				folderName=albom.getFolderName();
				
				//edit case: the path is forbidden to change
				m_checkBoxStandartPath.setText("  "+getGuiStrs("noPossToChangeSavePlaceLabelCaption")); 
				
				m_checkBoxStandartPath.setEnabled(false);
				
				m_path_alb.setEditable(false);
				
				m_pathButton.setEnabled(false);	
				
				m_checkBoxLoadAlbom.setSelected(false);
				
				m_checkBoxLoadAlbom.setVisible(false);
			 }
		 }
		 else {//new album case
			 	
				m_date.setText( dateToString(new Date(),GUI_DATE_FORMAT) ); 
			
				folderName = PaUtils.getNewAlbumFolderName(PaAlbum.getNextId());
				
				String pathText = PaUtils.get().getSettings().getStandardFolderPlace()+folderName;
				
				//the case when in a strange way the standard folder with such name exists
				//in this case we start to iterate latest album id, because this is the only
				//way to escape possible id duplication
				while(Files.exists(Paths.get(pathText))) {
					
					PaAlbum.getNextIdAndIterate(); // if exists, then iterate
					//PaAlbum.getNextId() doesn't iterate next id
					
					folderName = PaUtils.getNewAlbumFolderName(PaAlbum.getNextId());
					
					pathText = PaUtils.get().getSettings().getStandardFolderPlace()+folderName;
				}
				
				m_path_alb.setText(pathText);
				
				m_path_alb.setEditable(false);
				
				m_pathButton.setEnabled(false);			 
		 }
		 //the listener should be declared exactly here because it can start to work while loading 
		DocumentListener myListener = new KeyEnterListener();
		   
		m_name.getDocument().addDocumentListener(myListener);
		
		m_coment.getDocument().addDocumentListener(myListener);
		
		writeLogOnly("New Albom dialog: init operation finished",null);
	}
	
	private JPanel createGUI () {
		
		//main panel
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();

		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,
				PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);
		
		JPanel panelParentCombo = PaGuiTools.createHorizontalPanel();	
		
		JLabel parentLabel = new JLabel(getGuiStrs("parentAlbomNewLabel"));
		
		parentLabel.setFont(m_font);
		
		m_parentsCombo = new JComboBox<String>();
		
		m_parentsCombo.setFont(m_font);
		
		panelParentCombo.add(parentLabel);
		
		panelParentCombo.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelParentCombo.add(m_parentsCombo);
		
		JPanel panel_name = PaGuiTools.createHorizontalPanel();		
		
		JLabel name_lab = new JLabel (getGuiStrs("albomNameLabelCaption"));  
		
		name_lab.setFont(m_font);
		
		panel_name.add(name_lab);
		
		panel_name.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_name.add(m_name);
		
		panel_name.add(Box.createHorizontalGlue());
		
		JPanel panel_path = PaGuiTools.createHorizontalPanel();		
		
		JLabel path_lab = new JLabel (getGuiStrs("albomSavingPlaceEditLabel"));
		
		path_lab.setFont(m_font);

		panel_path.add(path_lab);
		
		panel_path.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_path.add(m_path_alb);
		
		panel_path.add(Box.createHorizontalStrut(6));
		
		panel_path.add(m_pathButton);
		
		panel_path.add(Box.createHorizontalGlue());
			
		JPanel panel_check_1 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
	    m_checkBoxStandartPath = new JCheckBox("  "+getGuiStrs("useStandartPlaceLabelCaption"), true); 

		m_checkBoxStandartPath.setFont(m_font);		
		
		panel_check_1.add(m_checkBoxStandartPath);
		
		JPanel panelCheckBox2 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
	    m_checkBoxLoadAlbom = new JCheckBox("  "+getGuiStrs("loadAlbomAfterCreationCaption"), true); 
	    
	    m_checkBoxLoadAlbom.setFont(m_font);		
	    
	    panelCheckBox2.add(m_checkBoxLoadAlbom);
		
		JPanel panel_date = PaGuiTools.createHorizontalPanel();		
		
		JLabel date_lab = new JLabel (getGuiStrs("creationDateDateEditCaption")); 
		
		date_lab.setFont(m_font);

		panel_date.add(date_lab);
		
		panel_date.add(Box.createHorizontalStrut(6));
		
		panel_date.add(m_date);
		
		panel_date.add(Box.createHorizontalGlue());

		JPanel panel_coment = PaGuiTools.createHorizontalPanel();	
		
		JLabel coment_lab = new JLabel (getGuiStrs("commentsAlbomNewEditLabel"));
		
		coment_lab.setFont(m_font);

		panel_coment.add(coment_lab);
		
		panel_coment.add(Box.createHorizontalStrut(6));
		
		panel_coment.add(m_coment);
		
		panel_coment.add(Box.createHorizontalGlue());

		JPanel south = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);

//		south.add(jlab_0);
		
		south.add(panel_Ok_Cancel);
				
		//mnemonics
		m_OkButton.setMnemonic(KeyEvent.VK_O);
		
		m_CancelButton.setMnemonic(KeyEvent.VK_C);
				
		//alignment of all panels
		PaGuiTools.setGroupAlignmentX(
			new JComponent[] { panel_name, panelParentCombo, panel_path, panel_check_1,
						panel_date, panel_coment, panelCheckBox2, south, panel_MAIN }, Component.LEFT_ALIGNMENT);
		
		//makes all labels to be the same size
		PaGuiTools.makeSameSize(new JComponent[] { name_lab, parentLabel, path_lab, date_lab, coment_lab } );
		
		//this removes the 'endless' height of fields
		PaGuiTools.fixTextFieldSize(m_name);
		
		PaGuiTools.fixTextFieldSize(m_path_alb);
		
		PaGuiTools.fixTextFieldSize(m_date);
		
		PaGuiTools.fixTextFieldSize(m_coment);

		panel_MAIN.add(panel_name);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panelParentCombo);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_path);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
		
		panel_MAIN.add(panel_check_1);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
		
		panel_MAIN.add(panel_date);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_coment);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panelCheckBox2);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(m_infoLabel);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(south);
			
		setAllListeners();
		
		return panel_MAIN;
		
	}
	/**
	 * Creates a list of albums for parent album combobox
	 * @param list - data list with all nodes of albums tree
	 */
	 private void initParentsList(ArrayList<PaAlbumTreeNode> list)
	 {
		 for(int i=0; i < list.size(); ++i) {
			 
			 PaAlbumTreeNode n = list.get(i);
			 
			 int idA = n.getId();
			 
			 if(m_id == 0 || idA != m_id ) { //control of absence "myself" in the list of "my" parents
				 
				 m_parentsCombo.addItem(n.getAlbumName());
				 
				 m_parentsMap.put(n.getAlbumName(),idA);
			 }
		 }
	 }
	 
	 public int getParentAlbomId() 
	 {		 
		 String name = m_parentsCombo.getSelectedItem().toString();
		 
		 return m_parentsMap.get(name);
	 }

	 
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);
			
			if ( e.getSource() == m_pathButton ) onPath(e);
			
		}
	}
	
	class IListener implements ItemListener {			
		
		public void itemStateChanged(ItemEvent itemEvent) {
			
			if (m_checkBoxStandartPath.isSelected()) {
				
				m_path_alb.setText(PaUtils.get().getSettings().getStandardFolderPlace()+folderName);
				
				m_path_alb.setEditable(false);
				
				m_pathButton.setEnabled(false);
				
			} else {
				//path_alb.setText("");
				m_path_alb.setEditable(true);
				
				m_pathButton.setEnabled(true);
			}
			verifyInfo();
		}
	}
	
	private void setAllListeners() {
		
		//catch focus exit event
		m_path_alb.addFocusListener(new PaNewPathCustomListener());
		
		m_date.addFocusListener(new PaNewPathCustomListener());
		
		m_name.addFocusListener(new PaNewPathCustomListener());
		//catch focus enter event
		m_path_alb.addKeyListener(new  PaKeyListener());
		
		m_date.addKeyListener(new  PaKeyListener());
		
		m_name.addKeyListener(new  PaKeyListener());
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_pathButton.addActionListener(forwarder);
		
		m_checkBoxStandartPath.addItemListener( new IListener() );
	
	}
	
	
	public void onOK(ActionEvent e) 
	{
		
		flag =   verifyInfo();
		
		if ( flag == 1 ) { 	dispose(); }
	}
	
	/**
	 * Validation of all information entered by user
	 * @return 1 if all information in the fields of the dialog is valid and can be saved
	 */
	protected int  verifyInfo() 
	{
		int flag =1;
		
		PaUtils.get().get_tokenizer();
		//name validation
		
		if (m_name.getText().equals("") || PaTokenizer.isValidate(m_name.getText()) == false) {
			
				m_infoLabel.setText(getMessagesStrs("allowedSymbolsMessage")); 
				
				writeLogOnly("New Albom dialog: verification for forbidden symbols failed for name"+m_name.getText(),null);
				
				flag =0;		
		}
		//date validation		
		if ( stringToDate(getDateAsString(),GUI_DATE_FORMAT) == null ) {
			
			m_infoLabel.setText(getMessagesStrs("dateIncorrectMessage"));
			
			writeLogOnly("New Albom dialog: verification for date failed for name"+m_name.getText(),null);
			
			flag =0;
			
		}
		//unique name validation
		
		if (  ! PaUtils.get().getAlbumContainer().albumNameUniqueValidator( m_name.getText(), m_id ) ) {
			
			m_infoLabel.setText(getMessagesStrs("albomExistsWithSuchNameMessage"));
			
			writeLogOnly("New Albom dialog: verification for existing albom failed for name"+m_name.getText(),null);
			
			flag =0;
		}
		
		if (  ! PaUtils.get().getAlbumContainer().albumPathUniqueValidator( m_path_alb.getText(), m_id  ) ) {
			
			m_infoLabel.setText(getMessagesStrs("chosenFolderExistsMessage"));
			
			writeLogOnly("New Albom dialog: verification for used folder failed for path "+m_path_alb.getText(),null);
			
			flag =0;	
		}
		
		PaUtils.get();
		//path existence and access rights validation
		String sS= PaUtils.checkFilePermisions(m_path_alb.getText(),true,true);
		
		if (! sS.isEmpty() ) {
			
			m_infoLabel.setText(sS);
			
			writeLogOnly("New Albom dialog: permisions verification failed for path "+m_path_alb.getText(),null);
			
			flag =0;
		}
				
		if ( flag == 0 ) {
			
			m_OkButton.setEnabled(false);
			
			return 0;
		}
		else {
			
			m_OkButton.setEnabled(true);
			
			m_infoLabel.setText("");
			
			return 1;
			
		}
	}

	/**
	 * Reaction on Cancel button
	 * @param e
	 */
	public void onCancel(ActionEvent e) {

		flag = 0;
		
		dispose();
	}
	
	/**
	 * Reaction on button ...
	 * @param e
	 */
	public void onPath(ActionEvent e) {

		JFileChooser sourcePhotoFile = new JFileChooser();
		
		sourcePhotoFile.setCurrentDirectory(new File("."));
		
		sourcePhotoFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = sourcePhotoFile.showOpenDialog(m_path_alb);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourcePhotoFile.getSelectedFile().getPath();
			
			m_path_alb.setText(name);	
			
			verifyInfo();
		}
		
		flag = 2;
	}
	
	public String getAlbomName() {
		
		return m_name.getText();
		
	}	
	
	public String getAlbomPath() {
		
		return m_path_alb.getText();
		
	}
	
	public String getDateAsString() {
		
		return m_date.getText();
		
	}
	
	public Date getDate() {	
				
		return stringToDate(m_date.getText(),GUI_DATE_FORMAT);
			
	}

	public String getCommentAlbum() {
		
		return m_coment.getText();
	}
	
	public int getClosedFlagValue () {
		
		return flag;
	}
	
	public String getRootPath () {
		
		 return PaUtils.get().getPathFromString(m_path_alb.getText());
		//return path_alb.getText().substring(0, path_alb.getText().lastIndexOf("\\")+1) ;
		
	}
	
	public String getFolderName () {
		
		return PaUtils.getFileNameFromString(m_path_alb.getText());
	
	}
	
	public boolean isAlbomShouldBeLoaded() {
		
		return m_checkBoxLoadAlbom.isSelected();
	}
	
	
	class PaNewPathCustomListener implements FocusListener
	{


		@Override
		public void focusGained(FocusEvent arg0) {}

		@Override
		public void focusLost(FocusEvent arg0) {
		
			 verifyInfo();
			
		}
	    
	}
	
	class PaKeyListener implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent arg0) {
			
			if (arg0.getExtendedKeyCode() ==  KeyEvent.VK_ENTER ) {
				
				verifyInfo();
			}
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent arg0) {}
	    
	}
	
	public void setParentComboItem(String name)
	{
		m_parentsCombo.setSelectedItem(name);		
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		m_name.setToolTipText(getGuiStrs("nameEditAlbomToolTip"));
		
		m_path_alb.setToolTipText(getGuiStrs("pathEditToolTip"));
		
		m_date.setToolTipText(getGuiStrs("dateEditAlbomToolTip"));
		
		m_coment.setToolTipText(getGuiStrs("commentEditAlbomToolTip"));
				
		m_pathButton.setToolTipText(getGuiStrs("pathChooserToolTip"));
				
		m_checkBoxStandartPath.setToolTipText(getGuiStrs("useStandartPathCheckBoxToolTip"));
		
		m_checkBoxLoadAlbom.setToolTipText(getGuiStrs("loadAfterCreationAlbomCheckBoxToolTip"));
		
		m_parentsCombo.setToolTipText(getGuiStrs("paretnsAlbomComboToolTip"));
			 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 
	 /**
	  * Key listener to verify the text input for bad symbols &*%${@link #changedUpdate(DocumentEvent)}etc
	  * @author Andrii Dashkov
	  *
	  */
	 class KeyEnterListener implements DocumentListener {


		@Override
		public void changedUpdate(DocumentEvent arg0) {
			
			verifyInfo();
		}


		@Override
		public void insertUpdate(DocumentEvent arg0) {
			
			verifyInfo();
		}

	
		@Override
		public void removeUpdate(DocumentEvent arg0) {
			
			verifyInfo();
		}
		 
		 
	 }
}
