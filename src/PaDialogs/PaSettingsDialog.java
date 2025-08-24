package padialogs;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.xml.stream.XMLStreamException;

import pacollection.PaSettings;
import paforms.PaExtEditorTable;
import paforms.PaExtEditorsTableModel;
import paforms.PaSortComboBox;
import paglobal.PaGuiTools;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * Dialog window for settings manipulation. All data can be save to disk in xml format
 */
public class PaSettingsDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComboBox<Integer> m_viewScale;			

	private JSpinner m_amountColumn;		// Number of columns with images on the main panel

	private JTextField m_standardSavingPlace;	//path to standard place of saving albums
	
	private JSpinner m_prevArea;
	
	private JSpinner m_histoPanelHeight;
	
	private JTabbedPane m_tabPanel;
	
	private JButton m_okButton = new JButton(getGuiStrs("buttonOkCaption"));
	
	private JButton m_closeButton = new JButton(getGuiStrs("buttonCancelCaption"));
	
	private JButton m_fileChButton;

	private Forwarder forwarder = new Forwarder();
	
	//private int flag;
	private Font m_font = PaUtils.get().getBaseFont();
	
	private JComboBox<String> m_comboCountries;
	
	private JComboBox<String> m_comboLan;
	
	private JComboBox<String> m_comboDateFormat;
	
	JButton m_addButton = null;
	
	JButton m_delButton = null;
	
	PaExtEditorTable table = null;
	
	PaExtEditorsTableModel model = null;
	
	PaSettings m_settings = null;
	
	private JCheckBox m_albomCheckBox;  

	private JCheckBox m_subjectCheckBox; 

	private JCheckBox m_imagesCheckBox ; 

	private JCheckBox m_mainPanelCheckBox ;
	
	private JCheckBox m_nameViewCaption;
	
	private JCheckBox m_fileNameViewCaption;
	
	private JCheckBox m_dateViewCaption;
	
	private JCheckBox m_idViewCaption;
	
	private JCheckBox m_customIconsCheckBox;
	
	private JCheckBox m_specIconsCheckBox;
	
	private JCheckBox m_logCheckBox;
	
	private JSpinner m_logSpinner; 
	
	private JSpinner m_boostIconsSpinner;
	
	private JCheckBox  m_hiddenDialogsCheckBox;
	
	PaSortComboBox m_sortCombo = new PaSortComboBox();
	
	private JSpinner m_heightField = null;
	
	private JSpinner m_scaleField = null;
	
	private JSpinner m_widthField = null;
	
	private JSpinner m_widthMarker = null;
	
	private JSpinner m_contrastStep = null;
	
	private BufferedImage m_instrImageIcon = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
	
	private Color m_instCurrentColor;

	private BufferedImage m_selectImageIcon = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
	
	private Color m_selectColor;
	
	/**
	 * combobox for choice of double click on image reaction
	 */
	JComboBox<String> m_comboDblClick;
	

	private int VERTICAL_STRUT = 12;
	

	
	public PaSettingsDialog (JFrame jframe, String nameFrame) {
		
		super(jframe, nameFrame, true);
		
		m_settings = PaUtils.get().getSettings();
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
		
		
		Container contentPane = getContentPane();
		
		contentPane.add(createCUI(), BorderLayout.CENTER);
		
		contentPane.add(createButtonsPanel(), BorderLayout.PAGE_END);
		
		
		PaUtils.setComponentsFont ( this, PaUtils.get().getBaseFont());
		
		pack();

		addListeners();
		
		init();
		
		setBounds(350, 200, 600, 500);
		
	}
	
	private void init() {
		
		m_viewScale.setSelectedItem( m_settings.getPhotoScale()  );
		
		m_amountColumn.setValue(m_settings.getColumnsAmount());
		
		m_standardSavingPlace.setText(m_settings.getStandardFolderPlace() );
		
		m_specIconsCheckBox.setSelected(m_settings.getCreateImagesCopiesFlag() );
		
		m_logCheckBox.setSelected(m_settings.isLogEnabled());
		
		m_comboCountries.setSelectedItem(m_settings.getCountry());
		
		m_comboLan.setSelectedItem(m_settings.getLanguage());
		
		m_hiddenDialogsCheckBox.setSelected(false);
		
				
		if ( m_settings.getGuiDateFormat().equals( PaSettings.LOCALE_DATE_DEFAULT_CAPTION ) ) {
			
			m_comboDateFormat.setSelectedItem(0);
		}
		else {
			
			m_comboDateFormat.setSelectedItem(m_settings.getGuiDateFormat());
		}
		
		//view tab init
		m_albomCheckBox.setSelected( m_settings.is_alVisible() );
		
		m_subjectCheckBox.setSelected(m_settings.isSubjectsVisible());
		
		m_imagesCheckBox.setSelected(m_settings.is_phVisible());
		
		m_mainPanelCheckBox.setSelected(m_settings.is_viVisible() );
		
		m_widthField.setValue(m_settings.getInstrumentsWinInitSize().width);
		
		m_heightField.setValue(m_settings.getInstrumentsWinInitSize().height);
		
		m_scaleField.setValue((int)(m_settings.getZoomStep()*100.0f));
		
		Color c = m_settings.getInstrumentsColor();
		
		fillInstrImageIcon( c ,m_instrImageIcon);
		
		m_instCurrentColor =c;
		
		
		c = m_settings.getSelectColor();
		
		fillInstrImageIcon( c ,m_selectImageIcon );
		
		m_selectColor = c;
			 
		m_widthMarker.setValue( m_settings.getInstrumentMarkerSize( ) );	
		
		m_contrastStep.setValue( m_settings.getContrastStep() );	
		
		m_sortCombo.setSelectedIndex(m_settings.getInitialSortingIndex());
		
		m_customIconsCheckBox.setSelected(m_settings.isCustomIconsEnabled());
		
		m_logSpinner.setValue(m_settings.getMaxLogFileLength());
		
		m_boostIconsSpinner.setValue(m_settings.getSpecIconsSize());
		
		m_prevArea.setValue(m_settings.getPrevAreaFixedSize().width);
		
		m_histoPanelHeight.setValue(m_settings.getHistoPanelHeight());
		
		m_comboDblClick.setSelectedIndex(m_settings.getDblClickReactionType());
		
		initImageViewInfo();
	}
	
	
	private void addListeners()
	{
		m_okButton.addActionListener(forwarder);
		
		m_closeButton.addActionListener(forwarder);
		
		m_fileChButton.addActionListener(forwarder);
		
		m_addButton.addActionListener(forwarder);
		
		m_delButton.addActionListener(forwarder);
		
		m_hiddenDialogsCheckBox.addActionListener(forwarder);
		
	}
	
	class Forwarder implements ActionListener {
		public void actionPerformed(ActionEvent e) {


		if ( e.getSource() == m_okButton ) onOK(e);
		
		if ( e.getSource() == m_closeButton ) onCancel(e);		
		
		if ( e.getSource() == m_addButton ) addRowToTable(e);
		
		if ( e.getSource() == m_delButton ) deleteRowFromTable(e);
		
		if ( e.getSource() == m_fileChButton ) folderChoose(e);
		
		if ( e.getSource() ==  m_hiddenDialogsCheckBox)  {
			
			if(m_hiddenDialogsCheckBox.isSelected()) {
				
				m_settings.clearHiddenDialogLists();
			}
		}
		

		}
	}
	
	public void onOK(ActionEvent e) {	

		
		try {
			
			int n = JOptionPane.showConfirmDialog(
					
				    PaUtils.get().getMainWindow(),
				    getMessagesStrs("messageSaveSettingsQuestion"),
				    getMessagesStrs("messageAnswerCaption"),
				    JOptionPane.YES_NO_OPTION);
			
			if ( n == JOptionPane.YES_OPTION) {

				setAllParameters();
				
				m_settings.saveXMLdata();
				
				JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
	    				getMessagesStrs("messageInfoAfterSavingSettings"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
				
			}
			
			
			
		} catch (FileNotFoundException | XMLStreamException e1) {
	
			writeLog("FileNotFoundException | XMLStreamException " + NEXT_ROW, e1, true, false, true);
			
    		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
    				getMessagesStrs("messageErroWhileSavingSettings"),
    			    getMessagesStrs("messageErrorCaption"),
    			    JOptionPane.ERROR_MESSAGE);
			
			
		}
		finally {

		}
	}

	public void onCancel(ActionEvent e) {
		
		dispose();
	}
	
	
	private JPanel createButtonsPanel() 
	{

		JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5) );
		
		m_okButton = new JButton(getGuiStrs("buttonSaveCaption"));
		
		m_closeButton = new JButton(getGuiStrs("buttonCloseCaption"));
		
		panel.add(m_okButton);
		
		panel.add(m_closeButton);
		
		return panel;
	}
	
	
	private JTabbedPane createCUI() {
		
		m_tabPanel = new JTabbedPane();
	 		
		createFirstTab();
		
		createLocalizationTab();	
		
		createExternalEditorsTab();		
		
		createViewTab();		
		
		createInstrumentsTab();
		
		setToolTips();
			
		return m_tabPanel;
		
	}
	
	private void createFirstTab() 
	{
		m_viewScale = new JComboBox<Integer>( PaUtils.get().getImageSizes());
		
		m_viewScale.setFont(m_font);
		
		m_viewScale.setToolTipText(getGuiStrs("scaleSeetingsToolTip"));
		
		PaUtils.get().setFixedSizeBehavior(m_viewScale);
		
		m_viewScale.setSelectedItem(m_settings.getPhotoScale());
			
		int l = PaUtils.get().getNumberImageColumns().length;
		
		int startValue = (int) PaUtils.get().getNumberImageColumns()[0];
		
		SpinnerNumberModel spModel = new SpinnerNumberModel(startValue, startValue,
				 (int) PaUtils.get().getNumberImageColumns()[l-1], 1);
		
		m_amountColumn = new JSpinner(spModel);
		
		m_amountColumn.setToolTipText(getGuiStrs("scaleSeetingsAmountColumn"));
		
		PaUtils.get().setFixedSizeBehavior(m_amountColumn);
	 

		m_standardSavingPlace = new JTextField();
		
		PaGuiTools.fixTextFieldSize(m_standardSavingPlace);
		
		m_standardSavingPlace.setEnabled(false);
		
		JPanel panelMAIN = PaGuiTools.createVerticalPanel();
		
		panelMAIN.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
		

		JPanel panelViewScale = PaGuiTools.createHorizontalPanel();		
		
		JLabel lableViewScale = new JLabel (getGuiStrs("scaleSeetingsLabelCaption"));
		
		lableViewScale.setFont(m_font);
		
		panelViewScale.add(lableViewScale);
		
		panelViewScale.add(Box.createHorizontalStrut(6));
		
		panelViewScale.add(m_viewScale);
		
		JLabel _lableAmountColumn = new JLabel (" " + getGuiStrs("imagesColumnsSettingsLabel")+" ");  
		
		_lableAmountColumn.setFont(m_font);
		
		panelViewScale.add(_lableAmountColumn);
		
		panelViewScale.add(m_amountColumn);
		
		PaUtils.get().setFixedSizeBehavior(panelViewScale);

		JPanel panelStandardPlace = PaGuiTools.createVerticalPanel();	
		
		JLabel lableStandardPlace = new JLabel (getGuiStrs("placeOfPhysicalSavingPlaceLabelSettings")); 
		
		lableStandardPlace.setFont(m_font);
		
		JPanel panelStandardPlace2 = PaGuiTools.createHorizontalPanel();
		
		panelStandardPlace2.add(lableStandardPlace );
		
		panelStandardPlace2.add(Box.createHorizontalGlue());
		
		JPanel panelStandardPlace1 = PaGuiTools.createHorizontalPanel();	
		
		m_fileChButton = new JButton(getGuiStrs("captionForFileChooseButton"));
		
		m_standardSavingPlace.setToolTipText(getGuiStrs("scaleSeetingsStandardSavingPlace"));
		 
		m_fileChButton.setVisible(false); 
		
		panelStandardPlace1.add(m_standardSavingPlace);
		
		panelStandardPlace1.add(m_fileChButton);		
		
		panelStandardPlace.add(panelStandardPlace2);

		panelStandardPlace.add(panelStandardPlace1);
		
		JPanel panelCreateSpecIcon = PaGuiTools.createHorizontalPanel();
		
		m_specIconsCheckBox = new JCheckBox(getGuiStrs("createSpecialIconsSettingCheckButton"));
		
		m_specIconsCheckBox.setFont(m_font);
		
		panelCreateSpecIcon.add(m_specIconsCheckBox);
		
		panelCreateSpecIcon.add(Box.createHorizontalGlue());
		
		JPanel panelLogCheck = PaGuiTools.createHorizontalPanel();
		
		m_logCheckBox = new JCheckBox(getGuiStrs("logSettingCheckButton"));
		
		m_logCheckBox.setFont(m_font);
		
		panelLogCheck.add(m_logCheckBox);
		
		panelLogCheck.add(Box.createHorizontalGlue());
		
		
		JPanel panelIconBoost = PaGuiTools.createHorizontalPanel();
		
		SpinnerNumberModel spModel12 = new SpinnerNumberModel(0, 0,
				 400, 1);
		
		m_boostIconsSpinner = new JSpinner(spModel12);
		
		m_boostIconsSpinner.setFont(m_font);
		
		Dimension size = m_boostIconsSpinner.getPreferredSize();
		
		size.width = 30;
		
		m_boostIconsSpinner.setMaximumSize(size);
		
		m_boostIconsSpinner.setToolTipText(getGuiStrs("boostIconsSpinnerToolTip"));
		
		JLabel iconsLabelSpinner = new JLabel (getGuiStrs("boostIconsSpinnerLabel")+" ");
		
		JLabel iconsLabelPixels = new JLabel(" " + getGuiStrs("pixelsUnitLabel"));
		
		iconsLabelSpinner.setFont(m_font);
		
		iconsLabelPixels.setFont(m_font);
		
		panelIconBoost.add(iconsLabelSpinner);
		
		panelIconBoost.add(m_boostIconsSpinner);
		
		panelIconBoost.add(iconsLabelPixels);
		
		panelIconBoost.add(Box.createHorizontalGlue());
		
	
		JPanel panelLogMax = PaGuiTools.createHorizontalPanel();
		
		SpinnerNumberModel spModel1 = new SpinnerNumberModel(0, 0,
				 10, 1);
		
		m_logSpinner = new JSpinner(spModel1);
		
		m_logSpinner.setFont(m_font);
		
		Dimension sz = m_logSpinner.getPreferredSize();
		
		sz.width = 30;
		
		m_logSpinner.setMaximumSize(sz);
		
		JLabel logLabelSpinner = new JLabel (getGuiStrs("logMaxSettingsLabel")+" ");
		
		JLabel logLabelSpinner1 = new JLabel ("  "+getGuiStrs("logMaxSettingsLabelMB"));
		
		logLabelSpinner1.setFont(m_font);
		
		logLabelSpinner.setFont(m_font);
		
		panelLogMax.add(logLabelSpinner);
		
		panelLogMax.add(m_logSpinner);
		
		panelLogMax.add(logLabelSpinner1);
		
		panelLogMax.add(Box.createHorizontalGlue());
		
		JPanel colorSelectPanel = PaGuiTools.createHorizontalPanel();
		
		JButton colorSelectButton = new JButton();	 
		
		colorSelectButton.setAction(new  PaColorSelectAction()) ;
		
		JLabel labelSelectColor = new JLabel(getMenusStrs("chooseColorSelect")+" ");
		
		labelSelectColor.setFont(m_font);
		
		colorSelectPanel.add(labelSelectColor);
		
		colorSelectPanel.add(colorSelectButton);
		
		colorSelectPanel.add(Box.createHorizontalGlue());
		
		JPanel dblClickPanel = PaGuiTools.createHorizontalPanel();
		
		JLabel labelDblClick = new JLabel(getGuiStrs("dblClickOptionLabel")+" ");
		
		labelDblClick.setFont(m_font);
		
		String[] sList = {getGuiStrs("redaktDblClickOptionLabel"),
				getGuiStrs("propDblClickOptionLabel"),
				getGuiStrs("instrDblClickOptionLabel")};
		
		m_comboDblClick = new JComboBox<String>(sList);
		
		m_comboDblClick.setFont(m_font);
		
		PaUtils.get().setFixedSizeBehavior(m_comboDblClick);
		
		dblClickPanel.add(labelDblClick);
		
		dblClickPanel.add(m_comboDblClick);
		
		panelMAIN.add(panelViewScale);
		
		panelMAIN.add(Box.createVerticalStrut(VERTICAL_STRUT));

		panelMAIN.add(panelStandardPlace);
		
		panelMAIN.add(Box.createVerticalStrut(VERTICAL_STRUT));
		
		panelMAIN.add(panelCreateSpecIcon);
		
		panelMAIN.add(Box.createVerticalStrut(VERTICAL_STRUT));
		
		panelMAIN.add(panelIconBoost);
		
		panelMAIN.add(Box.createVerticalStrut(VERTICAL_STRUT));
		
		panelMAIN.add(panelLogCheck);	
		
		panelMAIN.add(Box.createVerticalStrut(VERTICAL_STRUT));
		
		panelMAIN.add(panelLogMax);	
		
		panelMAIN.add(Box.createVerticalStrut(VERTICAL_STRUT));
	
		panelMAIN.add(colorSelectPanel);
		
		panelMAIN.add(Box.createVerticalStrut(VERTICAL_STRUT));
		
		panelMAIN.add(dblClickPanel);
		
		panelMAIN.add(Box.createVerticalGlue());
		
		m_tabPanel.addTab(getGuiStrs("settingsFristTabCaption"), panelMAIN);
	
	}
	
	private void createLocalizationTab() {
		
		
		JPanel panelV = PaGuiTools.createVerticalPanel();	
		
		GridLayout gridLayout = new GridLayout(4,2);
		
		gridLayout.setVgap(VERTICAL_STRUT);
		
		JPanel panel = PaGuiTools.createHorizontalPanel();	
		
		panel.setLayout(gridLayout);
		
		String[] ar = new String[2];
		
		ar[0] = "EN";
		
		//ar[1] = "US";
		
		ar[1] = "RU";
		//ar[3] = "UA";
	
		m_comboCountries =new JComboBox<String>(ar);
		
		PaUtils.get().setFixedSizeBehavior(m_comboCountries);
		
		JLabel cLabel = new JLabel(getGuiStrs("localizationTabCountryLabel")+ " :  ");
		
		String[] ar1 = new String[2];
		
		ar1[0] = "en";
		//ar1[1] = "us";
		ar1[1] = "ru";
		//ar1[3] = "ua";
	
		m_comboLan =new JComboBox<String>(ar1);

		PaUtils.get().setFixedSizeBehavior(m_comboLan);
				
		JLabel lLabel = new JLabel(getGuiStrs("localizationTabLanguageLabel")+ " :  ");
		
		String[] ar2 = new String[9];
		
		ar2[0] = getGuiStrs("localeDefaultDateFormatCaption");
		
		ar2[1] = "dd.MM.yy";
		
		ar2[2] = "dd.MM.yyyy";
		
		ar2[3] = "dd/MM/yy";
		
		ar2[4] = "dd/MM/yyyy";
		
		ar2[5] = "MM.dd.yy";
		
		ar2[6] = "MM.dd.yyyy";
		
		ar2[7] = "MM/dd/yy";
		
		ar2[8] = "MM/dd/yyyy";

	
		m_comboDateFormat =new JComboBox<String>(ar2);	
		
		PaUtils.get().setFixedSizeBehavior(m_comboDateFormat);
			
		JLabel formatLabel = new JLabel(getGuiStrs("localizationTabDateFormatLabel")+ " : ");
		
		JLabel infoLabel = new JLabel("");
		
		panel.add(cLabel); panel.add(m_comboCountries);
		
		panel.add(lLabel); panel.add(m_comboLan);
		
		panel.add(formatLabel); panel.add(m_comboDateFormat);
		
		panel.add(infoLabel); //panel.add(new JLabel());
		
		PaUtils.get().setFixedSizeBehavior(panel);
		
		panelV.add(panel);
		
		panelV.add(Box.createVerticalGlue());
		
		
		m_tabPanel.addTab( getGuiStrs("localizationTabName"), panelV);
	
		
	}
	
	
	private void createExternalEditorsTab() {
		
		JPanel panelV = PaGuiTools.createVerticalPanel();		
		
		JPanel panelH1 = PaGuiTools.createHorizontalPanel();
		
		model = new PaExtEditorsTableModel();
		
		model.initModel(m_settings);
		
		table = new PaExtEditorTable(model);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		table.setFillsViewportHeight(true);
		
		table.setRowSorter(null);
		
		table.setToolTipText(getGuiStrs("tableExtEditorsToolTip"));
		
		m_addButton = new JButton(getGuiStrs("addRowExtEditorTableButton"));
		
		m_delButton = new JButton(getGuiStrs("delRowExtEditorTableButton"));
		
		JLabel infoLabel = new JLabel(getGuiStrs("extEditorLabelSettingsDialog"));
		
		infoLabel.setToolTipText(getGuiStrs("extEditorsTableToolTip"));
		
		panelH1.add(infoLabel);  panelH1.add(Box.createHorizontalGlue() ); panelH1.add(m_addButton);  
		
		panelH1.add(Box.createHorizontalStrut(4));  panelH1.add(m_delButton);
		
		panelV.add(Box.createVerticalStrut(VERTICAL_STRUT));
		
	    panelV.add(panelH1);
	    
	    panelV.add(Box.createVerticalStrut(VERTICAL_STRUT));
	    
		panelV.add(scrollPane);

		m_tabPanel.addTab( getGuiStrs("externalEditorsTabName"), panelV);
		
	}
	
	
	private void addRowToTable(ActionEvent e) {
		
		model.addRow();
		
	}
	private void  deleteRowFromTable(ActionEvent e){
		
		int index = table.getSelectedRow();
		
		model.deleteRow(index);
	}
	
	
	private void createViewTab() {
		
		JPanel panelV = PaGuiTools.createVerticalPanel();
			
		m_albomCheckBox = new JCheckBox(getGuiStrs("albomsListSettingCheckButton"));  
		
		m_subjectCheckBox = new JCheckBox(getGuiStrs("subjectsListSettingsCheckButton")); 
		
		m_imagesCheckBox = new JCheckBox(getGuiStrs("imagesListSettingsCheckButton")); 
		
		m_mainPanelCheckBox = new JCheckBox(getGuiStrs("mainPanelSettingsCheckButton"));
		
		m_customIconsCheckBox = new JCheckBox(getGuiStrs("customIconsCheckButton"));
		
		m_nameViewCaption  = new JCheckBox(getGuiStrs("nameViewInfoSettingCheckButton")); 
		
		m_fileNameViewCaption  = new JCheckBox(getGuiStrs("fileNameViewInfoSettingCheckButton")); 
		
		m_dateViewCaption  = new JCheckBox(getGuiStrs("dateViewInfoSettingCheckButton")); 
		
		m_idViewCaption  = new JCheckBox(getGuiStrs("idViewInfoListSettingCheckButton")); 
		
		m_hiddenDialogsCheckBox = new JCheckBox(getGuiStrs("hiddenDialogsSettingCheckButton"));
		
		m_hiddenDialogsCheckBox.setFont(m_font);
	
		m_albomCheckBox.setFont(m_font);
		
		m_subjectCheckBox.setFont(m_font);
		
		m_imagesCheckBox.setFont(m_font);
		
		m_mainPanelCheckBox.setFont(m_font);
		
		m_customIconsCheckBox.setFont(m_font);
		
		m_nameViewCaption.setFont(m_font);
		
		m_fileNameViewCaption.setFont(m_font);
		
		m_dateViewCaption.setFont(m_font); 
		
		m_idViewCaption.setFont(m_font);
				
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new GridLayout(4, 1,10,10));
		
		panelButtons.setAlignmentX(LEFT_ALIGNMENT);
		
		panelButtons.add( m_mainPanelCheckBox  );
		
		panelButtons.add( m_albomCheckBox );	
		
		
		panelButtons.add( m_subjectCheckBox );
		
		panelButtons.add( m_imagesCheckBox );
		
		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("settingsShowWhileStartGroupCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font); 
		
		panelButtons.setBorder(titled_0);
		
		panelButtons.setFont(m_font);
		
		PaUtils.get().setFixedSizeBehavior(panelButtons);
		
		JPanel panelViewInfo = new JPanel();
		
		panelViewInfo.setLayout(new GridLayout(4, 1,10,10));
		
		panelViewInfo.setAlignmentX(LEFT_ALIGNMENT);
		
		panelViewInfo.add(m_nameViewCaption);
		
		panelViewInfo.add(m_fileNameViewCaption);	
		
		panelViewInfo.add(m_dateViewCaption);
		
		panelViewInfo.add(m_idViewCaption);
		
		Border bod1 = BorderFactory.createEtchedBorder();
		
		Border title1 = BorderFactory.createTitledBorder(bod1, getGuiStrs("settingsViewInfoGroupCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font); 

		panelViewInfo.setBorder(title1);
		
		panelViewInfo.setFont(m_font);
		
		PaUtils.get().setFixedSizeBehavior(panelViewInfo);
		
		JPanel panelSorting = PaGuiTools.createHorizontalPanel();
	
		JLabel sortLabel = new JLabel(getGuiStrs("settingsSortLabel")+" ");
		
		sortLabel.setFont(m_font);
		
		m_sortCombo.setFont(m_font);
		
		panelSorting.add(sortLabel);
		
		panelSorting.add(m_sortCombo);
		
		panelSorting.add(Box.createHorizontalGlue());
		
		PaUtils.get().setFixedSizeBehavior(m_sortCombo);
		
		PaUtils.get().setFixedSizeBehavior(panelSorting);
		
		JPanel panel1 = PaGuiTools.createHorizontalPanel();
		
		panel1.add(panelButtons); panel1.add(Box.createHorizontalGlue());
		
		JPanel panel2 = PaGuiTools.createHorizontalPanel();
		
		panel2.add(panelViewInfo); panel2.add(Box.createHorizontalGlue());
		
		JPanel panelCustomIcons = PaGuiTools.createHorizontalPanel();
		
		panelCustomIcons.add(m_customIconsCheckBox); panelCustomIcons.add(Box.createHorizontalGlue());
		
		panelV.add(panel1);
		
		panelV.add(Box.createVerticalStrut(VERTICAL_STRUT));
		
		panelV.add(panelSorting);
		
		panelV.add(panelCustomIcons);
		
		panelV.add(panel2);
		
		JPanel panel4 = PaGuiTools.createHorizontalPanel();
		
		panel4.add(m_hiddenDialogsCheckBox);  panel4.add(Box.createHorizontalGlue());
		
		panelV.add(panel4);
	  
		panelV.add(Box.createVerticalGlue());
			
		m_tabPanel.addTab( getGuiStrs("settingViewTabName"), panelV);		
	}
	
	
	
	private void createInstrumentsTab() {	
		
		JPanel panelInstruments = PaGuiTools.createVerticalPanel();
		
		GridLayout gridLayout = new GridLayout(9,2);
		
		gridLayout.setVgap(VERTICAL_STRUT);
		
		JPanel panel = new JPanel();
		
		panel.setLayout(gridLayout);
		
		PaUtils.get().setFixedSizeBehavior(panel);
		
		
		 SpinnerNumberModel spModel1 = new SpinnerNumberModel(600, 150,
				 2400, 50);
		 
		 SpinnerNumberModel spModel2 = new SpinnerNumberModel(600, 150,
				 2400, 50);
		
		
		JLabel widthLabel = new JLabel(getGuiStrs("initSizeLabelCaption")+" "+ getGuiStrs("widthLabelInstCaption")+" ");
	
		m_widthField = new JSpinner(spModel1);
		
		PaUtils.get().setFixedSizeBehavior(m_widthField);
		
		JLabel heightLabel = new JLabel(getGuiStrs("initSizeLabelCaption")+" "+ getGuiStrs("heigthLabelInstCaption")+" ");
	
		m_heightField = new JSpinner(spModel2);
		
		PaUtils.get().setFixedSizeBehavior(m_heightField);
		
		JLabel scaleLabel = new JLabel(getGuiStrs("scaleStepLabelCaption")+"  ");
		
		 SpinnerNumberModel spModel = new SpinnerNumberModel(10, 1,
				 20, 1);
		 m_scaleField = new JSpinner(spModel);
		 
		 PaUtils.get().setFixedSizeBehavior( m_scaleField);
		
		 JButton colorInstrButton = new JButton();	
		 
		 colorInstrButton.setAction(new  PaColorAction()) ;
		
		 		
		 JLabel markerWidthLabel = new JLabel(getGuiStrs("markerWidthLabelInstCaption")+" ");
		 
		 SpinnerNumberModel spModel3 = new SpinnerNumberModel(20, 5,
				 50, 1);
		 m_widthMarker = new JSpinner(spModel3);
		 
		 PaUtils.get().setFixedSizeBehavior(m_widthMarker);
		 
		 JLabel contrastStepLabel = new JLabel(getGuiStrs("contrastStepLabelInstCaption")+" ");
		 
		 SpinnerNumberModel spModel4 = new SpinnerNumberModel(5, 5,
				 50, 1);
		 
		 m_contrastStep = new JSpinner(spModel4);
		 
		 PaUtils.get().setFixedSizeBehavior(m_contrastStep);
		 
		 //these components are not used for now
		 
		 m_contrastStep.setVisible(false);
		 
		 contrastStepLabel.setVisible(false);
		 
		 JLabel prevAreaLabel = new JLabel(getGuiStrs("preAreaLabelInstCaption")+" ");
		 
		 SpinnerNumberModel spModel5 = new SpinnerNumberModel(500, 200,
				 1000, 10);
		 
		 m_prevArea = new JSpinner(spModel5);
		 
		 PaUtils.get().setFixedSizeBehavior(m_prevArea);
		 
		 JLabel prevHistAreaLabel = new JLabel(getGuiStrs("preHistLabelInstCaption")+" ");
		 SpinnerNumberModel spModel6 = new SpinnerNumberModel(200, 100,
				 700, 10);
		 
		 m_histoPanelHeight = new JSpinner(spModel6);
		 
		 PaUtils.get().setFixedSizeBehavior(m_histoPanelHeight);
		 
		panel.add(widthLabel); panel.add(m_widthField);
		
		panel.add(heightLabel); panel.add(m_heightField);
		
		panel.add(scaleLabel); panel.add(m_scaleField);
		
		panel.add(markerWidthLabel); panel.add(m_widthMarker);
		
		panel.add(new JLabel(getMenusStrs("chooseColorForInstr"))); panel.add(colorInstrButton);
		
		panel.add(prevAreaLabel); panel.add(m_prevArea);
		
		panel.add(prevHistAreaLabel); panel.add(m_histoPanelHeight);
			
		 panelInstruments.add(panel);

		 panelInstruments.add(Box.createVerticalGlue());
			
		 m_tabPanel.addTab( getGuiStrs("settingInstrumentsTabName"), panelInstruments);
		 	
	}
	
	
	class PaColorAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
	
		public PaColorAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(m_instrImageIcon));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("chooseColorForInstrToolTip")); 
			
		}
		
		public void actionPerformed(ActionEvent e) {
			
			 Color newColor = JColorChooser.showDialog(
					 PaSettingsDialog.this,
	                 getGuiStrs("chooseColorInstrLabel"), m_instCurrentColor);
			 
			 if (newColor != null ) {
				 
				 fillInstrImageIcon(newColor,m_instrImageIcon);
				 
				 m_instCurrentColor = newColor;
			 }

		}		
	}
	
	private class PaColorSelectAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
	
		
		
		public PaColorSelectAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(m_selectImageIcon));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("chooseColorSelectToolTip")); 
				
		}
		
		public void actionPerformed(ActionEvent e) {
			
			 Color newColor = JColorChooser.showDialog(
					 PaSettingsDialog.this,
	                 getGuiStrs("chooseColorSelectLabel"), m_selectColor);
			 
			 if (newColor != null ) {
				 
				 fillInstrImageIcon(newColor,m_selectImageIcon);
				 
				 m_selectColor = newColor;
			 }

		}		
	}

	private void setAllParameters() {
		
		//view tab
		m_settings.set_alVisible(m_albomCheckBox.isSelected() );
		
		m_settings.set_phVisible(m_imagesCheckBox.isSelected());
		
		m_settings.setSubjectsVisible(m_subjectCheckBox.isSelected());
		
		m_settings.set_viVisible( m_mainPanelCheckBox.isSelected() );
		
		m_settings.setCreateImagesCopiesFlag(m_specIconsCheckBox.isSelected() );
		
		m_settings.setLogEnabled(m_logCheckBox.isSelected() );
		
		m_settings.setCountry((String) m_comboCountries.getSelectedItem());
		
		m_settings.setLanguage((String) m_comboLan.getSelectedItem());
		
		if ( m_comboDateFormat.getSelectedIndex() == 0 )  {
			
			m_settings.setGuiDateFormat(PaSettings.LOCALE_DATE_DEFAULT_CAPTION );
		}
		else {
			m_settings.setGuiDateFormat((String) m_comboDateFormat.getSelectedItem());
		}
		
		m_settings.setPhotoScale( (Integer) m_viewScale.getSelectedItem()  );
		
		m_settings.setColumnsAmount( (int) m_amountColumn.getValue() );
		
		m_settings.setStandardFolderPlace( m_standardSavingPlace.getText()  );
		
		m_settings.setStandardFolderPlace( m_standardSavingPlace.getText()  );
		
		m_settings.setInstrumentsWinInitSize(new Dimension((Integer) m_widthField.getValue(),(Integer) m_heightField.getValue()));
		
		m_settings.setZoomStep( ( (Integer) m_scaleField.getValue() )/100.0f);	
		
		m_settings.setInstrumentsColor( m_instCurrentColor);
		
		m_settings.setSelectColor( m_selectColor);
		
		m_settings.setInstrumentMarkerSize((Integer) m_widthMarker.getValue() );
		
		m_settings.setContrastStep((Integer) m_contrastStep .getValue() );
		
		m_settings.setInitialSorting(m_sortCombo.getSelectedIndex());
		
		m_settings.setCustomIconsEnabled(m_customIconsCheckBox.isSelected());
		
		m_settings.setMaxLogFileLength((int)m_logSpinner.getValue());
		
		m_settings.setSpecIconsSize((int)m_boostIconsSpinner.getValue());
		
		int w = (int)m_prevArea.getValue();
		
		m_settings.setPrevAreaFixedSize(new Dimension(w,1000));
		
		w = (int)m_histoPanelHeight.getValue();
		
		m_settings.setHistoPanelHeight(w);
		
		m_settings.setDblClickReactionType(m_comboDblClick.getSelectedIndex());
		
		setImageViewInfo();
	
	}

	private void fillInstrImageIcon(Color c, BufferedImage img) {
		
	    Graphics2D g = (Graphics2D) img.getGraphics();
	    
		g.setColor(c);
		
		g.fillRect(0, 0, img.getWidth(),img.getHeight());
	}
	
	public void folderChoose(ActionEvent e) 
	{

		JFileChooser sourceFile = new JFileChooser();
		
		sourceFile.setCurrentDirectory(new File(m_standardSavingPlace.getText()));
		
		sourceFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = sourceFile.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourceFile.getSelectedFile().getPath();
			
			m_standardSavingPlace.setText(name);	
		}
	}
	/**
	 * <p>Sets tooltips for all elements</p>
	 */
	private void setToolTips() {
		
		m_prevArea.setToolTipText(getGuiStrs("prevAreaSettingsToolTip"));
		
		m_histoPanelHeight.setToolTipText(getGuiStrs("histHeightSettingsToolTip"));
		
		m_scaleField.setToolTipText(getGuiStrs("scaleSettingsToolTip"));
		
		m_widthMarker.setToolTipText(getGuiStrs("widthMarkerSettingsToolTip"));
		
		m_widthField.setToolTipText(getGuiStrs("widthInstrSettingsToolTip"));
		
		m_heightField.setToolTipText(getGuiStrs("heightInstSettingsToolTip"));
	}
	
	/**
	 * Inits the state of checkboxes according to bit information about visibility of captions on the
	 * view image forms - name, file name, date, id
	 */
	private void initImageViewInfo() {
		short mask = m_settings.getInfoBitmask();
		
		m_nameViewCaption.setSelected(false);
		
		m_fileNameViewCaption.setSelected(false); 
		
		m_dateViewCaption.setSelected(false); 
		
		m_idViewCaption.setSelected(false); 
		
		if((mask & 1) == 1) { m_nameViewCaption.setSelected(true); }
		
		if((mask & 2) == 2) { m_fileNameViewCaption.setSelected(true); }
		
		if((mask & 4) == 4) { m_dateViewCaption.setSelected(true); }
		
		if((mask & 8) == 8) { m_idViewCaption.setSelected(true); }
	}
	
	private void setImageViewInfo() {
		
		short mask = 0;
		
		if(m_nameViewCaption.isSelected()) { mask |= 1;  }
		
		if(m_fileNameViewCaption.isSelected()) { mask |= 2;  }
		
		if( m_dateViewCaption.isSelected()) { mask |= 4;  }
		
		if(m_idViewCaption.isSelected()) { mask |= 8;  }
		
		 m_settings.setInfoBitmask(mask);
	}
		
}
