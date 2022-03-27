package PaDialogs;

import static PaGlobal.PaUtils.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import PaCollection.PaImageContainer;
import PaCollection.PaSubject;
import PaForms.PaSubjectsPanel;
import PaGlobal.PaCloseFlag;
import PaGlobal.PaGuiTools;
import PaGlobal.PaTokenizer;
import PaGlobal.PaUtils;
import PaImage.PaFileIconView;
import PaImage.PaImagePreviewer;

/**
 * Dialog for adding new image in an album
 * @author avd
 *
 */
public class PaImageNewDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	protected JFileChooser m_chooserPhotoFile;
	/**
	 * save the previous folder value which has bee chosen by user
	 */
	private static String m_prevPath = null;
	
	protected BufferedImage bufferedImage;
	
	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel jlab_view;					// icon label for image
	
	protected PaSubjectsPanel m_subPanel;				//subjects panel	
	
	protected JPanel panel_R = new JPanel();		// image panel
	
	protected JPanel panel_0 = PaGuiTools.createVerticalPanel();
	
	protected JRadioButton m_rButton1 = new JRadioButton(getGuiStrs("radioButton1NewPhotoDialogCaption")); //

	
	protected JRadioButton m_rButton2 = new JRadioButton(getGuiStrs("radioButton2NewPhotoDialogCaption")); //

	protected ButtonGroup butGroup = new ButtonGroup(); 
	
	protected JRadioButton m_rButton3 = new JRadioButton(getGuiStrs("radioButton3NewPhotoDialogCaption")); //

	protected JRadioButton m_rButton4 = new JRadioButton(getGuiStrs("radioButton4NewPhotoDialogCaption")); //

	protected JTextField m_namePhoto = new JTextField(25);
	
	protected JTextField m_pathPhoto = new JTextField(25);
	
	protected JTextField m_datePhoto = new JTextField(25);
	
	protected JPanel panelButtons = null;
	
	protected PaImageContainer photoContainer;
	
	protected int id = -1;
	
	protected JTextField m_commentsEdit = new JTextField(25);
	
	protected ArrayList<Integer> _temId = new ArrayList<Integer>();

	private Forwarder forwarder = new Forwarder();
	
	private PaCloseFlag flag = PaCloseFlag.CANCEL;
	
	private Font m_font = PaUtils.get().getBaseFont(); 
	
	public JButton m_selectPath = new JButton(" ... ");
	
	public JButton m_OkButton, m_CancelButton;
	
	/**
	 * true if this dialog is for edit image operation
	 */
	private boolean m_editMode = false;
	
	
	public PaImageNewDialog(JFrame jfrm, String nameFrame, String name_photo, 
			String path_photo, String date_photo,
			ArrayList<Integer> temId, PaImageContainer pC, int id, boolean editMode) {
		
		this(jfrm, nameFrame, name_photo, path_photo, date_photo,temId, editMode);
		
		this.id = id;
		
		photoContainer = pC;

		verifyInfo(null);

	}
	
	public PaImageNewDialog(JFrame jfrm, String nameFrame, String name_photo, String path_photo, 
			String date_photo,ArrayList<Integer> temId, boolean editMode) {
		super (jfrm, nameFrame, true);
		
		m_namePhoto.setText(name_photo); m_namePhoto.setBorder(BorderFactory.createLoweredBevelBorder());
		
		m_pathPhoto.setText(path_photo); m_pathPhoto.setBorder(BorderFactory.createLoweredBevelBorder());
		
		m_datePhoto.setText(date_photo); m_datePhoto.setBorder(BorderFactory.createLoweredBevelBorder());
	
		m_commentsEdit.setBorder(BorderFactory.createLoweredBevelBorder());
		
		_temId.addAll(temId);
		
		m_editMode = editMode;
		
		
		add(createGUI());
		
		setBounds(250, 150, 225, 220);
		
		init();
	
		m_rButton2.setSelected(true);
		
		pack();
		
		setResizable(false);
		
	}
	
	public void setId(int id) {
		
		this.id = id;
	}
	/**
	 * Listener for buttons reaction
	 * @author avd
	 *
	 */
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);
			
			if ( e.getSource() == m_selectPath ) onSelectPath(e);
		}
	}
	
	public void onOK(ActionEvent e) {

		flag = PaCloseFlag.OK;
		
		dispose();

	}
	/**
	 * Cancel button reaction
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
		
		flag = PaCloseFlag.CANCEL;
		
		dispose();
	}	
	
	/**
	 * Reaction for ... button
	 * @param e
	 */
	public void onSelectPath(ActionEvent e) {

		m_infoLabel.setText(" ");
		
		if(m_prevPath != null && m_pathPhoto.getText().isEmpty()) {
			
			m_chooserPhotoFile.setCurrentDirectory(new File(m_prevPath));
		}
		else {
			
			m_chooserPhotoFile.setCurrentDirectory(new File(m_pathPhoto.getText()));
		}
		
		int result = m_chooserPhotoFile.showOpenDialog(PaImageNewDialog.this);
		
		//file is Ok
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = m_chooserPhotoFile.getSelectedFile().getPath();
			
			m_pathPhoto.setText(name);
			
			m_prevPath = new String(name);
			
			//read icon and set as an icon
			ImageIcon icon = new ImageIcon (name);
			
			if (icon.getImageLoadStatus() != MediaTracker.COMPLETE ) {
							
				m_infoLabel.setText(getMessagesStrs("notImageFileMessage"));
				
				 m_OkButton.setEnabled(false);
				 
				 return;
			}
				
			//the icon is too big
			if (icon.getIconWidth() > panel_R.getWidth()) 
				
				icon = new ImageIcon (icon.getImage().getScaledInstance(panel_R.getWidth()-25, -1, Image.SCALE_FAST));
			
			jlab_view.setIcon(icon);
			
			verifyInfo(null);
		}

	}
	
	/**
	 * 
	 * @return name of the new /edit image
	 */
	public String getImageName() {
		
		return m_namePhoto.getText();
	}
	
	/**
	 * 	
	 * @return path to the image file in the file system
	 */
	public String getImagePath() {
		
		return m_pathPhoto.getText();
	}

	/**
	 * 
	 * @return date of the image
	 */
	public String getImageDate() {
		
		return m_datePhoto.getText();
	}
	
	/**
	 * 	
	 * @return close flag to determine what operation should be done after the dialog close
	 */
	public PaCloseFlag getClosedFlagValue () {
		
		return flag;
	}
	

	public String getComments() {
		
		return m_commentsEdit.getText();
	}
	
	public void setComments(  String text) {
		
		m_commentsEdit.setText(text);
	}
	
	public boolean isPrinted() {
		
		return m_rButton3.isSelected();
	}
	
	public boolean isBookmarked() {
		
		return m_rButton4.isSelected();
	}
	
	public void setPrinted(boolean flag) {
		
		m_rButton3.setSelected(flag);
	}
	
	public void setBookmarked(boolean flag) {
		
		m_rButton4.setSelected(flag);
	}
	
	
	public boolean isCopyFile() {
		
		return m_rButton1.isSelected();
	}
	
	/**
	 * 
	 * @return the list of chosen subjects in the dialog
	 */
	public ArrayList<Integer> getSubjectsList () {
		
		_temId.clear();
		
		for (PaSubject x : m_subPanel.get_temListModelPhoto().get_temsList()) {
			
			_temId.add(x.getId());
		}
		
		return _temId;
	}
	
	/**
	 * 
	 * @return main UI panel with all components
	 */
	private JPanel createGUI() {
		
		m_subPanel = new PaSubjectsPanel(true, _temId, PaUtils.get().getSubjectsContainer(), getGuiStrs("subCaptionChooser")); //"Темы для изображения: "

		JPanel panel_1 = PaGuiTools.createHorizontalPanel();
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);

		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
		
		JPanel panel_name = PaGuiTools.createHorizontalPanel();	
		
		JLabel name_lph = new JLabel (getGuiStrs("newPhotoLabelPhotoName")); 
		
		name_lph.setFont(m_font);
		
		panel_name.add(name_lph);
		
		panel_name.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_name.add(m_namePhoto);
		
		panel_name.add(Box.createHorizontalGlue());
		
		JPanel panel_path = PaGuiTools.createHorizontalPanel();	
		
		JLabel path_lph = new JLabel (getGuiStrs("newPhotoFileCaption")); 
		
		path_lph.setFont(m_font);

		panel_path.add(path_lph);
		
		panel_path.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_path.add(m_pathPhoto);
		
		panel_path.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_path.add(m_selectPath);
		
		panel_path.add(Box.createHorizontalGlue());
		
		JPanel panel_date = PaGuiTools.createHorizontalPanel();	
		
		JLabel date_lph = new JLabel (getGuiStrs("newPhotoDateOfCreationCaption")); 
		
		date_lph.setFont(m_font);

		panel_date.add(date_lph);
		
		panel_date.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_date.add(m_datePhoto);
		
		panel_date.add(Box.createHorizontalGlue());
		
		JPanel panelComments = PaGuiTools.createHorizontalPanel();	
		
		JLabel commentLabel = new JLabel (getGuiStrs("newPhotoCommentsLabelCaption")); 
		
		commentLabel.setFont(m_font);

		panelComments.add(commentLabel);
		
		panelComments.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelComments.add( m_commentsEdit );
		
		panelComments.add(Box.createHorizontalGlue());
		
		Dimension DIM_1 = new Dimension(280, 280);
		
		panel_R.setSize(DIM_1);
		
		panel_R.setPreferredSize(DIM_1);
		
		panel_R.setBorder(BorderFactory.createEmptyBorder());
		
		jlab_view = new JLabel();
		
		File file = new File(m_pathPhoto.getText());

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Photo", "gif", "GIF", "JPEG", "jpeg", "JPG", "jpg");
		
		m_chooserPhotoFile = new JFileChooser();
		
		m_chooserPhotoFile.setCurrentDirectory(file);
		
		m_chooserPhotoFile.setAccessory(new PaImagePreviewer(m_chooserPhotoFile));
		
		m_chooserPhotoFile.setFileView(new PaFileIconView(filter, new ImageIcon(PaUtils.get().getIconsPath() + 
										"pencil.png")));
		
		ImageIcon icon1 = new ImageIcon (file.getAbsolutePath());

		//if the icon is too big
		if (icon1.getIconWidth() > panel_R.getWidth()) {	
			
			ImageIcon icon_2 = new ImageIcon (icon1.getImage().getScaledInstance((int) (panel_R.getWidth()), -1, Image.SCALE_FAST));
			
			jlab_view.setIcon(icon_2 );
		}
		
		panel_R.add(jlab_view);
		
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
	
		south_right.add(m_infoLabel);
		
		south_right.add(panel_Ok_Cancel);
					
		PaGuiTools.setGroupAlignmentY(
				new JComponent[] {panel_name, panel_path, panel_date, south_right, m_subPanel.getMainPanel(),// panel_CenterGroup, 
						panel_R, panel_0, panel_1, panel_MAIN },
									Component.RIGHT_ALIGNMENT);

		PaGuiTools.setGroupAlignmentY(
						new JComponent[] { m_namePhoto, m_pathPhoto, m_datePhoto, 
								name_lph, path_lph, date_lph },
						Component.CENTER_ALIGNMENT);
	
		PaGuiTools.makeSameSize(new JComponent[] { name_lph, path_lph, date_lph,commentLabel } );

		PaGlobal.PaGuiTools.createRecommendedMargin(new JButton[] { m_OkButton, m_CancelButton, m_selectPath } );
		
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new GridLayout(2, 1));
		
		panelButtons.add( m_rButton1 );
		
		panelButtons.add( m_rButton2 );
		
		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("newPhotoDialoOptionsCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font); 
		
		panelButtons.setBorder(titled_0);
		
		panelButtons.setFont(m_font);
		
		butGroup.add( m_rButton1 );
		
		butGroup.add( m_rButton2 );
		
		JPanel panelState = new JPanel();
		
		panelState.setLayout(new GridLayout(2, 1));
		
		panelState.add( m_rButton3 );
		
		panelState.add( m_rButton4 );
		
		m_rButton1.setFont(m_font);
		
		m_rButton2.setFont(m_font);
		
		m_rButton3.setFont(m_font);
		
		m_rButton4.setFont(m_font);

		Border titled_01 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("newPhotoDialogStateCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font); 
		
		panelState.setBorder(titled_01);
		
		panelButtons.setFont(m_font);
		
		//this reduce the endless size of text fields
		PaGuiTools.fixTextFieldSize(m_namePhoto);
		
		PaGuiTools.fixTextFieldSize(m_pathPhoto);
		
		PaGuiTools.fixTextFieldSize(m_datePhoto);
			
		panel_0.add(panel_path);
		
		panel_0.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_0.add(panel_name);
		
		panel_0.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_0.add(panel_date);
		
		panel_0.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_0.add( panelComments );
		
		panel_0.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_0.add(m_subPanel.getMainPanel());
		
		panel_0.add( panelButtons );
		
		panel_0.add( panelState );
		
		panel_1.add(panel_0);
		
		panel_1.add(Box.createHorizontalStrut(PaUtils.VERT_STRUT));
		
		panel_1.add(panel_R);
		
		panel_MAIN.add(panel_1);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));	
		
		panel_MAIN.add(south_right);

		
		m_rButton1.setSelected(true);
		
		setAllListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_selectPath.addActionListener(forwarder);
		
		PaFocusCustomListener fListener = new PaFocusCustomListener();
		
		m_namePhoto.addFocusListener(fListener);
		
		m_pathPhoto.addFocusListener(fListener);
		
		m_datePhoto.addFocusListener(fListener);
		
		DocumentListener myListener = new KeyListener();
	   
	    m_namePhoto.getDocument().addDocumentListener(myListener);
	    
		m_pathPhoto.getDocument().addDocumentListener(myListener);
		
		m_commentsEdit.getDocument().addDocumentListener(myListener);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
	
		
	}
	

	/**
	 * <p>Validator for all info in the dialog</p>
	 * @param s - additional string to validate - sometimes we have to check inner string
	 */
	public void verifyInfo(String s) {
		
		boolean flag = true;
		
		if (m_namePhoto.getText().equals("") || m_pathPhoto.getText().equals("")) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("noEmptyPhotoNameMessage")); 
			
		}
		
		if ( PaTokenizer.isValidate(m_namePhoto.getText()) == false ||
				PaTokenizer.isValidate( m_commentsEdit.getText()) == false ) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("allowedSymbolsMessage")); 
			//Allowed symbols - letters, 0-9,  ( _ . , - ) ");
			
		}
		
		 
		if ( stringToDate(getImageDate(),GUI_DATE_FORMAT) == null ) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("dateIncorrectMessage")); //"Некорректно введена дата ");
			
		}
		
		File f = new File(m_pathPhoto.getText());
		
		if (f.exists() == false) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("correctPathMessage"));//"С таким именем файл не существует. Проверьте правильность пути к файлу.");
		}
		else {
			
		 	if ( ! f.canRead() ) {
		 		
		 		flag = false;
		 		
				m_infoLabel.setText(getMessagesStrs("imageFileCantBeReadMessage"));
		 		
		 	} 
		}
		
		if ( !m_editMode && ! photoContainer.isUniquePhotoPath(m_pathPhoto.getText(), id) ) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("notUniquePathInAlbomMessage"));
			
		}
		
		if(s != null) {
			
			if ( PaTokenizer.isValidate(s) == false ) {
				
				flag = false;
				
				m_infoLabel.setText(getMessagesStrs("allowedSymbolsMessage"));
			}
		}

	
	   m_OkButton.setEnabled(flag);
	   
	   if (flag) { m_infoLabel.setText(""); }
		
		
	}
	
	
	class PaFocusCustomListener implements FocusListener
	{
		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusLost(FocusEvent arg0) {
		
			 verifyInfo(null);
			
		}
	    
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		m_namePhoto.setToolTipText(getGuiStrs("nameNewImageDToolTip"));
		
		m_pathPhoto.setToolTipText(getGuiStrs("pathNewImageDToolTip"));
		
		m_datePhoto.setToolTipText(getGuiStrs("dateNewImageDToolTip"));
		
		m_selectPath.setToolTipText(getGuiStrs("pathChooserToolTip"));
		
		m_commentsEdit.setToolTipText(getGuiStrs("commentsNewImageDToolTip"));
		
		m_rButton1.setToolTipText(getGuiStrs("copyRButNewImageDToolTip"));
		
		m_rButton2.setToolTipText(getGuiStrs("linkRButNewImageDToolTip"));
		
		m_rButton3.setToolTipText(getGuiStrs("printNewImageDToolTip"));
		
		m_rButton4.setToolTipText(getGuiStrs("bookNewImageDToolTip"));
			 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 
	 /**
	  * Key listener to verify the text input for bad symbols &*%${@link #changedUpdate(DocumentEvent)}etc
	  * @author avd
	  *
	  */
	 class KeyListener implements DocumentListener {

	
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			
			verifyInfo(null);
		}


		@Override
		public void insertUpdate(DocumentEvent arg0) {
			
			verifyInfo(null);
		}


		@Override
		public void removeUpdate(DocumentEvent arg0) {
			
			verifyInfo(null);
		}
		 
		 
	 }
	 
	/**
	 * <p>Initiation function.Don't remove it! It must be empty, 
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {}
}
