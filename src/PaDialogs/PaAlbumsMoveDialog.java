package PaDialogs;

import PaCollection.PaAlbumContainer;
import PaGlobal.*;
import static PaGlobal.PaUtils.*;
import java.awt.Color;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import PaGlobal.PaGuiTools;

/**
 * 
 * @author avd
 * <p>Dialog for move albom's files in other place</p>
 */
public class PaAlbumsMoveDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	
	public PaButtonEnter m_OkButton, m_CancelButton, m_pathButton;
	
	private JCheckBox m_checkBox1, m_checkBox2;
	
	private JTextField m_path_alb = new JTextField(25);
	
	private JFileChooser m_sourceFile = new JFileChooser();
	
	private PaAlbumContainer m_refContainer;
	
	private Integer m_id;

	JLabel m_errorLabel;
	
	private boolean m_closeFlag = false;
	
	private String m_albomName = new String();
	
	public PaAlbumsMoveDialog (JFrame jfrm, PaAlbumContainer rContainer, String nameAlbom, Integer idN) 
	{
		super (jfrm, getGuiStrs("moveAlbomDialogCaption")+" " + nameAlbom, true); 
		
		m_refContainer = rContainer;
		
		m_id = idN;

		m_albomName = nameAlbom;

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		
		
		add(createGUI());
		
		setBounds(250, 150, 225, 350);
		
		pack();
		
		setResizable(true);

	}
	private JPanel createGUI () {
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();

		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
	
		JPanel panelTop = PaGuiTools.createHorizontalPanel();
		
		JLabel jlabTop = new JLabel(getGuiStrs("currentAlbomImageLocation"));
		
		 Font font = PaUtils.get().getBaseFont();
		
		jlabTop.setFont(font);
	
		panelTop.add(jlabTop); panelTop.add(Box.createHorizontalGlue());
		
		JPanel panelTop1 = PaGuiTools.createHorizontalPanel();
		
		JTextField textF = new JTextField(25);
		
		textF.setText(m_refContainer.getAlbum(m_id).getFullStandardPath());
		
		textF.setEditable(false);
		
		panelTop1.add(textF); panelTop1.add(Box.createHorizontalGlue());
		
		JPanel panel_lable = PaGuiTools.createHorizontalPanel();
		
		JLabel jlab = new JLabel(getGuiStrs("newAlbomImageLocation"));
		
		panel_lable.add(jlab);
		
		panel_lable.add(Box.createHorizontalGlue());
		
		jlab.setFont(font);
		
		JPanel panel_center = PaGuiTools.createHorizontalPanel();
		
		m_path_alb = new JTextField(25);
		
		m_path_alb.setBorder(BorderFactory.createLoweredBevelBorder());
		
		m_pathButton = new PaButtonEnter(". . .");
		
		panel_center.add(m_path_alb);
		
		panel_center.add(Box.createHorizontalStrut(5));
		
		panel_center.add( m_pathButton);
		
		panel_center.add(Box.createHorizontalStrut(5));
		
		JPanel panel_check_1 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));

		m_path_alb.setText(PaUtils.get().getSettings().getStandardFolderPlace());
		
		m_path_alb.setEditable(false);
		
		m_pathButton.setEnabled(false);

		m_checkBox1 = new JCheckBox("  "+getGuiStrs("useStandartPlaceLabelCaption"));
		m_path_alb.setText(PaUtils.get().getSettings().getStandardFolderPlace()+
				m_refContainer.getAlbum(m_id).getFolderName()+"+1");
		
		m_path_alb.setEditable(true);
		
		m_pathButton.setEnabled(true);
		
		m_checkBox1.setFont(font);		
		
		panel_check_1.add(m_checkBox1);
		
		JPanel panel_check_2 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
		m_checkBox2 = new JCheckBox(getGuiStrs("deleteOldAlbomFolder"));//удалить старую папку хранения");
		
		m_checkBox2.setFont(font);
		
		panel_check_2.add(m_checkBox2);
		
		JPanel south = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_OkButton = new PaButtonEnter(getGuiStrs("buttonOkCaption"));
		
		m_CancelButton = new PaButtonEnter(getGuiStrs("buttonCancelCaption"));
			
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
		
		south.add(panel_Ok_Cancel);
		
		JPanel south1 = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		m_errorLabel = new JLabel("<html> <br> </html>   ");
		
		m_errorLabel.setHorizontalAlignment(JLabel.LEFT);
		
		m_errorLabel.setForeground(Color.RED);
		
		south1.add(m_errorLabel);
		
		panel_MAIN.add(panelTop);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panelTop1);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_lable);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_center);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT/2));
		
		panel_MAIN.add(panel_check_1);
		
		panel_MAIN.add(panel_check_2);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(south1);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(south);
		
		PaGuiTools.createRecommendedMargin(new JButton[] { m_OkButton, m_CancelButton,  m_pathButton } );

		PaGuiTools.fixTextFieldSize(m_path_alb);
		
		setAllActionListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);
			
			if ( e.getSource() ==  m_pathButton ) onPath(e);
		}
	}
	
	private void setAllActionListeners() 
	{
		Forwarder l = new Forwarder();
		
		m_OkButton.addActionListener(l);
		
		m_CancelButton.addActionListener(l);
		
		m_pathButton.addActionListener(l);
		
		m_checkBox1.addItemListener(new IListener() );
		
		m_path_alb.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	        	
	        }
	        
	    });
	
		m_path_alb.addFocusListener(new PaNewPathCustomListener());
		
		m_path_alb.addKeyListener(new  PaKeyListener());
		
		DocumentListener myListener = new KeyEnterListener();
		   
		m_path_alb.getDocument().addDocumentListener(myListener);

	}
	
	/**
	 * <p>m_Ok button reaction, starts move operation after confirmation</p>
	 * @param e - action event
	 */
	public void onOK(ActionEvent e) 
	{
		String operationMessage = new String();
		operationMessage = m_albomName + getMessagesStrs("questionStartAlbomMoving")+NEXT_ROW +
				getMessagesStrs("newSavingPlaceForAlbom")+ m_path_alb.getText(); 

		if ( m_checkBox2.isSelected() ) {
			
			operationMessage +=NEXT_ROW+getMessagesStrs("oldAlbomFolderWillBeDeleted");

		}
		
		int n = JOptionPane.showConfirmDialog(
			    PaUtils.get().getMainWindow(),
			    operationMessage,
			    getMessagesStrs("messageAnswerCaption"),
			    JOptionPane.YES_NO_OPTION);
		
		if ( n == JOptionPane.YES_OPTION)
		{
			
			m_closeFlag = true;
			
			dispose();
		}
		
	}
	/**
	 * <p>removes albom files which are in the standard folder only (operation 'remove albom')
	 * Deletes old folder if the user checked the check box</p>
	 */
	public String getPathText()
	{
		return m_path_alb.getText();
	}
	
	public boolean isFolderDelete()
	{
		return m_checkBox2.isSelected();
	}
	
	public int getAlbomId()
	{
		return m_id;
	}

	public boolean isOkClosedFlag()
	{
		return m_closeFlag;	
	}
	
	/**
	 * <p>Info validation function</p>
	 */
	private void verifyInfo()
	{
		int flag = 1;
		
		String sS= PaUtils.checkFilePermisions(m_path_alb.getText(),true,true);
		
		if (! sS.isEmpty() ) {
			
			 m_errorLabel.setText(sS);
			 
			flag =0;
		}
			
		if ( PaUtils.isSamePathes(m_refContainer.getAlbum(m_id).getFullStandardPath(),m_path_alb.getText()) ) {
			
			 m_errorLabel.setText(getMessagesStrs("newSavingPlaceIsOldPlace"));
			 
			flag =0;		
		}
				
		if ( flag == 0 ) {
			
			m_OkButton.setEnabled(false);			
		}
		else {
			
			m_errorLabel.setText("   ");
			
			m_OkButton.setEnabled(true);
		}
	}
	
	public void onCancel(ActionEvent e) {
		
		dispose();
	}	
	
	public void onPath(ActionEvent e) 
	{
		m_sourceFile.setCurrentDirectory(new File("."));
		
		m_sourceFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = m_sourceFile.showOpenDialog(m_path_alb);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = m_sourceFile.getSelectedFile().getPath();
			
			m_path_alb.setText(name);	
			
			verifyInfo();
		}
	}
	
	
	
	class PaNewPathCustomListener implements FocusListener
	{
		@Override
		public void focusGained(FocusEvent arg0) {
			
		}

		@Override
		public void focusLost(FocusEvent arg0) 
		{	
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
		public void keyReleased(KeyEvent arg0) {
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
					
		}
	}
	
	class IListener implements ItemListener {	
		
		public void itemStateChanged(ItemEvent itemEvent) {
			
			if (m_checkBox1.isSelected()) {
				
				m_path_alb.setText(PaUtils.get().getSettings().getStandardFolderPlace()+
						m_refContainer.getAlbum(m_id).getFolderName());
				
				m_path_alb.setEditable(false);
				
				m_pathButton.setEnabled(false);
				
				verifyInfo();
				
			} else {
				
				m_path_alb.setEditable(true);
				
				m_pathButton.setEnabled(true);
				
				verifyInfo();
			}
		}
	}
	
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		 m_checkBox1.setToolTipText(getGuiStrs("useStandartFolderToMoveAlbomToolTip"));
		 
		 m_checkBox2.setToolTipText(getGuiStrs("deleteOldAlbumFolderCheckBoxToolTip"));
		 
		 m_path_alb.setToolTipText(getGuiStrs("enterFoderToMovesAlbomToolTip"));
		 
		 m_sourceFile.setToolTipText(getGuiStrs("pathChooserToolTip"));
				
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 
	 /**
	  * Key listener to verify the text input for bad symbols &*%$ etc
	  * @author avd
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




