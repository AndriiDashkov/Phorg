
package PaROI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import PaAlgorithms.PaAlgoTransform;
import PaAlgorithms.PaAlgorithms;
import PaGlobal.PaButtonEnter;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import PaLong.PaRoiGenerateSaveTask;
import static PaGlobal.PaUtils.*;

/**
 * Dialog windows for cutting and saving ROI subimages as separate images according to there coordinates and types.
 * (dataset creation)
 * @author Andrey Dashkov
 *
 */
public class PaRoiSaveRoiDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	JComboBox<String> m_typesCombo = null;
	
	JButton m_startCut = null;
	
	JCheckBox m_scaleToSize =  new JCheckBox(getGuiStrs("scaleRoiCheckBoxLableButton"));
	
	JCheckBox m_saveNegs =  new JCheckBox(getGuiStrs("saveNegativesRoiCheckBoxLableButton"));
	
	JCheckBox m_createSubFolders = new JCheckBox(getGuiStrs("createSubFoldersForRoiCheckBox"));
	
	JButton m_selectPath = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	JLabel labelWhereToSave = new JLabel(getGuiStrs("whereSaveRoiPath"));
	
	JLabel labelNegsOverlapSpinner = new JLabel(getGuiStrs("roiOverlapNegThreshold"));
	
	JLabel labelNegsNumSpinner = new JLabel(getGuiStrs("roiNegNumberSpinnerLabel"));
	
	JLabel labelWidth = new JLabel(getGuiStrs("roiWidthForSaveLabel"));
	
	JLabel labelHeight = new JLabel(getGuiStrs("roiHeightForSaveLabel"));
	
	JLabel typesLabel = new JLabel(getGuiStrs("roiTypesComboLabel"));
	
	JLabel labelNegsStartScsle = new JLabel(getGuiStrs("roiNegStartScaleSpinnerLabel"));
	
	JLabel labelNegsEndScale = new JLabel(getGuiStrs("roiNegEndScaleSpinnerLabel"));
	
	JLabel labelNegsScalesNum = new JLabel(getGuiStrs("roiNegsScaleNumSpinnerLabel"));
	
	JLabel labelNegsBaseWidth = new JLabel(getGuiStrs("roiNegsBaseWidthSpinnerLabel"));
	
	JLabel labelNegsBaseHeight = new JLabel(getGuiStrs("roiNegsBaseHeightSspinnerLabel"));
	
	private JTextField m_path = new JTextField(50);
	
	private Font m_font = PaUtils.get().getBaseFont(); 
	
	JSpinner m_widthSpinner = null;
	
	JSpinner m_heightSpinner = null;
	
	JSpinner m_negOverlapSpinner = null;
	
	JSpinner m_negsNumberSpinner = null;
	
	JSpinner m_neg_widthSpinner = null;
	
	JSpinner m_neg_heightSpinner = null;
	
	JSpinner m_negStartScaleSpinner = null;
	
	JSpinner m_negEndScaleSpinner = null;
	
	JSpinner m_scalesNumSpinner = null;
	
	ArrayList<String> m_typesList = null; 
	
	String  m_roi_file_path = null;
	
	PaRoiWindow m_parent = null;
	
	JRadioButton m_currentImageButton = new JRadioButton(getGuiStrs("currentImageRoiCaption"));
	
	JRadioButton m_allImagesButton = new JRadioButton(getGuiStrs("allImagesRoiCaption"));

	private PaButtonEnter m_OkButton = new PaButtonEnter(getGuiStrs("buttonOkCaption"));
	
	private PaButtonEnter m_CancelButton = new PaButtonEnter(getGuiStrs("buttonCancelCaption"));
	
	PaRoiGenerateSaveTask m_longSaveTask = null;
	
	private Forwarder forwarder = new Forwarder();
	
	String[] m_types = {};
	
	int m_negativesCounter = 0;
	
	int m_positivesCounter = 0;
	
	public PaRoiSaveRoiDialog (PaRoiWindow parent, ArrayList<String> typesList, String roi_file_path) 
	{
		
		super (parent, getGuiStrs("roiSaveDialog"), true); 
		
		 m_typesList = typesList;
		
		m_parent = parent;
		
		m_roi_file_path = roi_file_path;
		
		int types_number = m_typesList.size() + 1;
		
		m_types = new String[types_number];
		
		m_types[0] = getGuiStrs("AllRoiTypeItem");//"All";
	
		for(int i = 1; i < types_number; ++i) {
			
			m_types[i] = m_typesList.get(i - 1);
		}
		
		m_typesCombo = new JComboBox<String> (m_types);
		
		
		SpinnerNumberModel saveNegmodel = new SpinnerNumberModel(new Float(0.25), new Float(0.0), new Float(1.0), new Float(0.01)); 
		
		m_negOverlapSpinner = new JSpinner(saveNegmodel);
		
		PaGuiTools.fixComponentSize(m_negOverlapSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_negOverlapSpinner, 60);
		
		SpinnerNumberModel negsNumModel = new SpinnerNumberModel(100, 1, 100000000, 1);
		
		m_negsNumberSpinner = new JSpinner(negsNumModel);
		
		PaGuiTools.fixComponentSize(m_negsNumberSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_negsNumberSpinner , 120);
		
		SpinnerNumberModel spModel = new SpinnerNumberModel(1, 1, 10000, 1);
		
		m_widthSpinner = new JSpinner(spModel);
		
		PaGuiTools.fixComponentSize(m_widthSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_widthSpinner , 60);
		
		SpinnerNumberModel spModel1 = new SpinnerNumberModel(1, 1, 10000, 1);
		
		m_heightSpinner = new JSpinner(spModel1);
		
		PaGuiTools.fixComponentSize(m_heightSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_heightSpinner , 60);
		
		SpinnerNumberModel spModelNegW = new SpinnerNumberModel(30, 2, 1000, 1);
		
		m_neg_widthSpinner = new JSpinner(spModelNegW);
		
		PaGuiTools.fixComponentSize(m_neg_widthSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_neg_widthSpinner, 60);
		
		SpinnerNumberModel spModelNegH = new SpinnerNumberModel(80, 2, 1000, 1);
		
		m_neg_heightSpinner = new JSpinner(spModelNegH);
		
		PaGuiTools.fixComponentSize(m_neg_heightSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_neg_heightSpinner, 60);
		
		SpinnerNumberModel spModelNegStart = new SpinnerNumberModel(new Float(0.15), new Float(0.05), new Float(10.0), new Float(0.01));
		
		m_negStartScaleSpinner = new JSpinner(spModelNegStart);
		
		PaGuiTools.fixComponentSize(m_negStartScaleSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_negStartScaleSpinner, 60);
		
		SpinnerNumberModel spModelNegEnd = new SpinnerNumberModel(new Float(2.0), new Float(0.05), new Float(10.0), new Float(0.01));
		
		m_negEndScaleSpinner = new JSpinner(spModelNegEnd);
		
		PaGuiTools.fixComponentSize(m_negEndScaleSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_negEndScaleSpinner, 60);
		
		SpinnerNumberModel spModelNegNum = new SpinnerNumberModel(5, 2, 100, 1);
		
		m_scalesNumSpinner = new JSpinner(spModelNegNum);
		
		PaGuiTools.fixComponentSize(m_scalesNumSpinner);
		
		PaGuiTools.setComponentFixedWidth(m_scalesNumSpinner, 60);
		
		m_createSubFolders.setSelected(true);
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		 m_selectPath.addActionListener(forwarder);
				
		m_OkButton.setMnemonic(KeyEvent.VK_O);
		
		m_CancelButton.setMnemonic(KeyEvent.VK_C);
		
		m_saveNegs.addActionListener(forwarder);
		
		m_scaleToSize.addActionListener(forwarder);
		
		add( createGUI() );
		
		m_typesCombo.addActionListener(new ComboListener());
		
		m_currentImageButton.addActionListener(forwarder);
		
		m_allImagesButton.addActionListener(forwarder);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});

		pack();
		
		setResizable(false);
	}
	

	private JPanel createGUI () 
	{
		
		JPanel panelTypes = PaGuiTools.createHorizontalPanel();
		
		panelTypes.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelTypes.add(m_typesCombo);
		
		panelTypes.add(Box.createHorizontalGlue());
		
		JPanel panelSavePath = PaGuiTools.createHorizontalPanel();
		
		panelSavePath.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelSavePath.add( m_path);
		
		panelSavePath.add(m_selectPath);
		
		m_path.setPreferredSize(new Dimension(100,m_selectPath.getPreferredSize().height));
		
		m_path.setMaximumSize(new Dimension(100,m_selectPath.getPreferredSize().height));
		
		m_path.setMinimumSize(new Dimension(100,m_selectPath.getPreferredSize().height));
		
		panelSavePath.add(Box.createHorizontalGlue());
		
		JPanel panelGrid = PaGuiTools.createHorizontalPanel();
		
		JPanel  pGrid = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		
		c.gridy = 0;
		
		pGrid.add(typesLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 1;
		
		c.gridy = 0;
		
		
		pGrid.add(panelTypes, c);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		
		c.gridy = 1;
		
		pGrid.add(labelWhereToSave, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 1;
		
		c.gridy = 1;
		
		pGrid.add(panelSavePath, c);
		
		panelGrid.add(pGrid);
		
		panelGrid.add(Box.createHorizontalGlue());
		
		JPanel panelSizeScale = PaGuiTools.createHorizontalPanel();
		
		panelSizeScale.add(m_scaleToSize);
		
		panelSizeScale.add(labelWidth);
		
		panelSizeScale.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelSizeScale.add(m_widthSpinner); 
		
		panelSizeScale.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelSizeScale.add(labelHeight);
		
		panelSizeScale.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelSizeScale.add(m_heightSpinner); 
		
		panelSizeScale.add(Box.createHorizontalGlue());
		
		JPanel panelSubFolders = PaGuiTools.createHorizontalPanel();
		
		panelSubFolders.add(m_createSubFolders);
		
		panelSubFolders.add(Box.createHorizontalGlue());
		
		JPanel panelIntImage = PaGuiTools.createHorizontalPanel();
		
		panelIntImage.add(Box.createHorizontalGlue());
		
		JPanel panelRadio = PaGuiTools.createHorizontalPanel();
		
		panelRadio.add(m_currentImageButton);
		
		panelRadio.add(m_allImagesButton);
		
		panelRadio.add(Box.createHorizontalGlue());

		JPanel panelOverlapNegs = PaGuiTools.createHorizontalPanel();
		
		panelOverlapNegs.add(labelNegsOverlapSpinner);
		
		panelOverlapNegs.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelOverlapNegs.add(m_negOverlapSpinner);
		
		panelOverlapNegs.add(Box.createHorizontalGlue());
		
		JPanel panelNegsNum = PaGuiTools.createHorizontalPanel();
		
		panelNegsNum.add(labelNegsNumSpinner);
		
		panelNegsNum.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsNum.add(m_negsNumberSpinner);
		
		panelNegsNum.add(Box.createHorizontalGlue());
		
		JPanel panelNegsWH = PaGuiTools.createHorizontalPanel();
		
		panelNegsWH.add(labelNegsBaseWidth);
		
		panelNegsWH.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsWH.add(m_neg_widthSpinner);
		
		panelNegsWH.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsWH.add(labelNegsBaseHeight);
		
		panelNegsWH.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsWH.add(m_neg_heightSpinner);

		panelNegsWH.add(Box.createHorizontalGlue());
		
		JPanel panelNegsScaleRange = PaGuiTools.createHorizontalPanel();
		
		panelNegsScaleRange.add(labelNegsStartScsle);
		
		panelNegsScaleRange.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsScaleRange.add(m_negStartScaleSpinner);
		
		panelNegsScaleRange.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsScaleRange.add(labelNegsEndScale);
		
		panelNegsScaleRange.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsScaleRange.add(m_negEndScaleSpinner);
		
		panelNegsScaleRange.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsScaleRange.add(labelNegsScalesNum);
		
		panelNegsScaleRange.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelNegsScaleRange.add(m_scalesNumSpinner);
		
		panelNegsScaleRange.add(Box.createHorizontalGlue());
		
		JPanel panelNegButtons = new JPanel();
		
		panelNegButtons.setLayout(new GridLayout(5, 1));
		
		panelNegButtons.add( m_saveNegs );
		
		panelNegButtons.add( panelOverlapNegs );
		
		panelNegButtons.add( panelNegsNum );
		
		panelNegButtons.add(panelNegsWH);
		
		panelNegButtons.add(panelNegsScaleRange);
		 
		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("panelNegsButtonRoiCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font); 
		
		panelNegButtons.setBorder(titled_0);
		
		panelNegButtons.setFont(m_font);
		
		panelNegButtons.setToolTipText(getGuiStrs("roiNegsPanelTooltip"));
		
		m_saveNegs.setToolTipText(getGuiStrs("roiNegsPanelTooltip"));
		
		m_negsNumberSpinner.setToolTipText(getGuiStrs("roiNegsNumSpinnerPanelTooltip"));
		
		m_negStartScaleSpinner.setToolTipText(getGuiStrs("roiStartScaleSpinnerPanelTooltip"));
		
		m_scalesNumSpinner.setToolTipText(getGuiStrs("roiNumScaleSpinnerPanelTooltip"));
		
		JPanel panelButtons =  PaGuiTools.createHorizontalPanel();
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);

		panelButtons.add(panel_Ok_Cancel);
				
		panelButtons.add(Box.createHorizontalGlue());
		
		panelButtons.add(m_OkButton);
		
		panelButtons.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelButtons.add(m_CancelButton);
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();
		
		panel_MAIN.add(panelGrid);
		
		panel_MAIN.add(panelRadio);
		
		panel_MAIN.add(panelSubFolders);
		
		panel_MAIN.add(panelSizeScale);
		
		panel_MAIN.add(panelIntImage);
		
		panel_MAIN.add(panelNegButtons);
		
		panel_MAIN.add(panelButtons);
		
		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(VERT_STRUT,VERT_STRUT,VERT_STRUT,
				VERT_STRUT));
		
		boolean flag = false;
		
		m_negOverlapSpinner.setEnabled(flag);
		
		m_negsNumberSpinner.setEnabled(flag);
		
		labelNegsStartScsle.setEnabled(flag);
		
		labelNegsEndScale.setEnabled(flag);
		
		labelNegsScalesNum.setEnabled(flag);
		
		labelNegsBaseWidth.setEnabled(flag);
		
		labelNegsBaseHeight.setEnabled(flag);
		
		labelNegsOverlapSpinner.setEnabled(flag);
		
		labelNegsNumSpinner.setEnabled(flag);
		
		m_currentImageButton.setSelected(true);
		
		return panel_MAIN;
		
	}
	
	/**
	 * 
	 * @author avd
	 * <p>Event listener for all buttons</p>
	 */
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			if ( e.getSource() == m_OkButton ) { startOperation(); }
		
			if ( e.getSource() == m_CancelButton ) {  onCancel(e); }
			
			if ( e.getSource() == m_selectPath ) {  onPath(e); }
			
			if ( e.getSource() == m_saveNegs ) {  changeNegsButtonStatus(); }
			
			if ( e.getSource() == m_scaleToSize ) {  changeSizeButtonStatus(); }
			
			if ( e.getSource() == m_currentImageButton   ) {  
				
				if(m_currentImageButton.isSelected()) { 
					
					 m_allImagesButton.setSelected(false);
				}
				else {
					
					 m_allImagesButton.setSelected(true);
				}
			}
			
			if ( e.getSource() == m_allImagesButton  ) {  
				
				if( m_allImagesButton.isSelected()) { 
					
					m_currentImageButton.setSelected(false);
				}
				else {
					
					m_currentImageButton.setSelected(true);
				} 
				
			}
			
		}
	}
	
	private void  changeNegsButtonStatus() {
		
		boolean flag = false;
		
		if(m_saveNegs.isSelected()) {
			
			flag = true;
		}
		else {
			
			flag = false;
		}
		
		m_negOverlapSpinner.setEnabled(flag);
		
		m_negsNumberSpinner.setEnabled(flag);
		
		labelNegsStartScsle.setEnabled(flag);
		
		labelNegsEndScale.setEnabled(flag);
		
		labelNegsScalesNum.setEnabled(flag);
		
		labelNegsBaseWidth.setEnabled(flag);
		
		labelNegsBaseHeight.setEnabled(flag);
		
		labelNegsOverlapSpinner.setEnabled(flag);
		
		labelNegsNumSpinner.setEnabled(flag);
		
	}
	
	private void changeSizeButtonStatus()
	{
		if( m_scaleToSize.isSelected()) {
	
			
			m_widthSpinner.setEnabled(true);
			
			m_heightSpinner.setEnabled(true);
		}
		else {
			
			m_widthSpinner.setEnabled(false);
			
			m_heightSpinner.setEnabled(false);
		}
		
	}
	
	 /**
	 * Starts the operation of cutting ROI subimages (positives) and negative subimages (negatives). 
	 */
	public void startOperation()
	{
		
		String roiSaveTaskCaption = null;
		
		if(m_saveNegs.isSelected()) {
			
			int n = JOptionPane.showConfirmDialog(
				    this,
				    getMessagesStrs("startNegsROIOperationQuestion"),
				    getMessagesStrs("messageAnswerCaption"),
				    JOptionPane.YES_NO_OPTION); 
			
			if ( n != JOptionPane.YES_OPTION) {
	
				return;
			}
			
			roiSaveTaskCaption = getMessagesStrs("roiSaveTaskCaption2");
			
		}
		else {
			
			roiSaveTaskCaption = getMessagesStrs("roiSaveTaskCaption");
		}

		
		ProgressMonitor progressMonitor = new ProgressMonitor(this,
				roiSaveTaskCaption +" ", getMessagesStrs("roiSaveOperationNote"), 0, 100);
		
		progressMonitor.setMillisToDecideToPopup(0);
		
		progressMonitor.setMillisToPopup(0);
		
	
		m_longSaveTask = new PaRoiGenerateSaveTask(progressMonitor, this);
		
		m_longSaveTask.execute();

	}
	
	public void startCutting()
	{
		 String typeName = "";
		 
		if(m_typesList != null && !m_typesList.isEmpty()) {
		
			typeName = ((String) m_typesCombo.getSelectedItem()).toLowerCase();
		}
		 
		 boolean all_types_flag = typeName.equals(getGuiStrs("AllRoiTypeItem").toLowerCase());
		
		 Path base_path = Paths.get(m_path.getText());

		 if ((m_path.getText()).isEmpty() || !Files.isDirectory(base_path)) {
			 
		  		JOptionPane.showMessageDialog( this,  getMessagesStrs("saveRoiEmptyPathOperation"),
	    			    getMessagesStrs("messageErrorCaption"), JOptionPane.ERROR_MESSAGE);
			 
			 return;
		 }
				
		ArrayList<String> types = new ArrayList<String>();
		
		ArrayList<Path> all_pathes = new ArrayList<Path>();
		
		if ( all_types_flag ) {
			
			types = m_typesList;
		}
		else {
		
			types.add(typeName);
			
		}
		
		//creates structure of folders
		if (m_createSubFolders.isSelected()) {
			
			if(! createSubFolders( m_path.getText(), types, all_pathes) ) {
		
				return;
			}
		}
		else {
			
			all_pathes.add(base_path);
		}
		
		
		HashMap<String,ArrayList<PaRectangle>> roi_map = m_parent.getRoiMap();
		
		if(roi_map.isEmpty()) {
			
			return;
		}
		
		
		HashMap<String,String> path_map = null;
		
		HashMap<String,Dimension> sizes_map  = m_parent.getImageSizesMap();
		
		if (!m_currentImageButton.isSelected()) {
		
			path_map = m_parent.getImagesPathMap();
			
		}
		else {
		
			path_map = m_parent.getCurrentImagePathMap();
			
		}
		
		ArrayList<Path> all_pathes_neg = new ArrayList<Path>();
		
		//generate negatives
		if(m_saveNegs.isSelected()) {
			
			all_pathes_neg.addAll(all_pathes);
		
			createNegSubFolder(m_path.getText(), all_pathes_neg);
		
		}
		
		float f_task_size = (float)path_map.size(); 
		
		int counter = 1;
		
		m_negativesCounter = 0;
		
		m_positivesCounter = 0;
		
		for (Entry<String,String> entry : path_map.entrySet()) {
			
		
			Path path = Paths.get(entry.getValue());
			
			String imageName = entry.getKey();
			
			if(!Files.exists(path)) {
	
				continue;
			}
			
			if( m_longSaveTask.isCancelled() ) { return; }
			
			BufferedImage image;
			
			try {
				
				image = ImageIO.read(new File(path.toString()));
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
				continue;
			}
			
			if(null == image) {
			
				continue;
			}
			
			ArrayList<PaRectangle> roi_list = roi_map.get(imageName);
			
			Dimension im_size =  sizes_map.get(imageName);
			
			//positives cut
			if(null != roi_list) { 
				
			
				for(int j = 0; j < roi_list.size(); ++j) {
					
					if( m_longSaveTask.isCancelled() ) { return; }
					
					PaRectangle rect = roi_list.get(j);
					
					//additional check of valid ROI geometry
					if (im_size != null && (rect.x >= im_size.width || rect.y >= im_size.height )) {
						
						continue;
					}
					
					int typeIndex = types.indexOf(rect.type);
					
					Path targetFolder = base_path;
					
					if(all_types_flag) {
						
					
						if(all_pathes.size() == 1) {
							
							targetFolder =  all_pathes.get(0);
							
							++m_positivesCounter;
							
							saveROISubImage(image, rect, targetFolder, m_scaleToSize.isSelected(), (int)m_widthSpinner.getValue(),
									 (int)m_heightSpinner.getValue(), m_positivesCounter);
						}
						else {
							
								
							if (typeIndex < 0) {
								targetFolder = base_path;//all ROI with no type are saved into base folder
							}
							else {
								targetFolder = all_pathes.get(typeIndex);
							}
							
							++m_positivesCounter;
									
							saveROISubImage(image, rect, targetFolder,  m_scaleToSize.isSelected(), (int)m_widthSpinner.getValue(),
									 (int)m_heightSpinner.getValue(), m_positivesCounter);
							
							if( m_longSaveTask.isCancelled() ) { return; }
							
						}
							
						
					}
					else if(rect.type.equals(types.get(0))) {
						
						targetFolder = all_pathes.get(0);
						
						++m_positivesCounter;
								
						saveROISubImage(image, rect, targetFolder, m_scaleToSize.isSelected(), (int)m_widthSpinner.getValue(),
								 (int)m_heightSpinner.getValue(), m_positivesCounter);
						
						if( m_longSaveTask.isCancelled() ) { return; }
						
					}
				
				}
			}
			
			
			//generate negatives
			if(m_saveNegs.isSelected()) {
				
				
				Path targetNewFolder = all_pathes_neg.get(all_pathes_neg.size() - 1);//the path for negative samples is the last one
				
				Dimension baseRoiSize = new Dimension((int)m_neg_widthSpinner.getValue(), (int)m_neg_heightSpinner.getValue() );
				
				int numberPerImage =  (int)m_negsNumberSpinner.getValue();
				
				float startScale = (float)m_negStartScaleSpinner .getValue();
				
				float endScale = (float)m_negEndScaleSpinner .getValue(); 
				
				int scalesNumber = (int) m_scalesNumSpinner.getValue() ;
				
				float overlapWithPositivesThreshold =  (float)m_negOverlapSpinner.getValue();
				
				if( m_longSaveTask.isCancelled() ) { return; }
				
				generateNegatives(numberPerImage, startScale, endScale, scalesNumber, overlapWithPositivesThreshold,
						baseRoiSize, roi_list, image, targetNewFolder);
				
				if( m_longSaveTask.isCancelled() ) { return; }
			
			}
			
			int pr = (int)(counter*99/f_task_size);
			
			if(pr < 1) pr = 1;
			
			if(pr >= 100) pr = 99;
			
			m_longSaveTask.setCurrentProgress(pr);
			
			++counter;
		
		}
	}
	
	private void generateNegatives(int numberPerImage, float startScale, float endScale, int scalesNumber, float overlapWithPositivesThreshold,
			Dimension baseRoiSize,
			ArrayList<PaRectangle> roi_list, BufferedImage image, Path targetFolder) 
	{
		
		float[] scales = new float[scalesNumber];
		
		scales[0] =  startScale;
		
		int scalesNumber_ = scalesNumber - 2 + 1;
		
		float scaleStep = (endScale - startScale)/scalesNumber_;
		
		int i = 0;
		
		for(i = 0; i < scalesNumber_; ++i ) {
			
			scales[i + 1] =  scales[i] + scaleStep;
		} 
		
		scales[i] =  endScale;
		
		int numberPerScale = numberPerImage/scalesNumber;
		
		for(i = 0; i < scalesNumber; ++i) {
			
			if( m_longSaveTask.isCancelled() ) { return; }
			
			
			int num = numberPerScale;
			
			if(i == (scalesNumber -1) ) {
				num = numberPerScale + numberPerImage - (numberPerScale*scalesNumber);
			}
			
			int scaled_width = (int)(baseRoiSize.width);
			
			int scaled_height = (int)(baseRoiSize.height);
			
			
			for(int j = 0; j < num; ++j) {
				
				Rectangle rect = null;
				
				boolean overlap = false;
				
				if( m_longSaveTask.isCancelled() ) { return; }
				
				do {
					
					int xR = (int) (Math.random()*(image.getWidth() - scaled_width));
					
					int yR = (int) (Math.random()*(image.getHeight() - scaled_height));
					
					rect = new Rectangle(xR, yR,  scaled_width, scaled_height);
					
					overlap = false;
	
					for(int j1 = 0; j1 < roi_list.size(); ++j1) {
						
						PaRectangle rectPa = roi_list.get(j1);
						
						Rectangle rect_Roi = new Rectangle(rectPa.x, rectPa.y, rectPa.w, rectPa.h);
						
						Rectangle ints = rect.intersection(rect_Roi);
						
						if(ints.isEmpty()) { 
							continue;
						}
						else {
							
							 float d = ((float)(ints.width*ints.height))/(rect.width*(float)rect.height + rect_Roi.width*rect_Roi.height - ints.width*ints.height);
							 
							 if(d >= overlapWithPositivesThreshold) {
								 
								 overlap = true;
								 
								 break;
							 
							 }
						}
					}
					
				}while(overlap);
				
				++m_negativesCounter;
				
				saveROISubImage(image, new PaRectangle(rect.x, rect.y, rect.width, rect.height), targetFolder,  false,
						baseRoiSize.width, baseRoiSize.height, m_negativesCounter);
				 
			}
		}
	}
		
		
		
	private void saveROISubImage(BufferedImage image, PaRectangle rect, Path targetFolder, boolean scaletoFixedSize,
			int width, int height, int samples_counter)
	{
		
		BufferedImage im = null;
		
		int pad = 30;
		
		double scaleRatio = width/(double)rect.w;
		
		if( scaletoFixedSize) {
			
			Rectangle widerRect = new Rectangle(rect.x - pad, rect.y - pad, rect.w + 2*pad, rect.h + 2*pad);
			
			try {
				
				im = image.getSubimage(widerRect.x, widerRect.y, widerRect.width, widerRect.height);
				
			} catch(RasterFormatException ex) {
				
				//the geometry is over the boundaries (user draw a selection oves boundary of the source image)
				im = PaAlgorithms.getSubImageWithPadding(image, widerRect.x, widerRect.y, widerRect.width, widerRect.height);
			}
			
			im = (BufferedImage) PaAlgoTransform.getScaledImage(im, (int)Math.ceil((widerRect.width*scaleRatio)),
					(int)Math.ceil((widerRect.height*scaleRatio)));
			
			try {
				
				im = im.getSubimage((int)Math.ceil(pad*scaleRatio), (int)Math.ceil(pad*scaleRatio), width, height);
				
			} catch(RasterFormatException ex) {
			
				//the geometry is over the boundaries (user draw a selection oves boundary of the source image)
				im = PaAlgorithms.getSubImageWithPadding(im, (int)Math.ceil(pad*scaleRatio), (int)Math.ceil(pad*scaleRatio), width, height);
			}
			
			
		}
		else {
			
			try {
				
				im = image.getSubimage(rect.x, rect.y, rect.w, rect.h);
				
			} catch(RasterFormatException ex) {
				
				//the geometry is over the boundaries (user draw a selection oves boundary of the source image)
				im = PaAlgorithms.getSubImageWithPadding(image, rect.x, rect.y, rect.w, rect.h);
			}
		}
		
		String newName = String.valueOf(samples_counter) + ".jpg";
		
		Path fullNewPath = Paths.get(targetFolder.toString(), newName);
		
		File outputfile = new File( fullNewPath.toString() );
		
		try {
			
			if(im != null) {
				
				ImageIO.write(im, "jpg", outputfile);
			}
			
		} catch (IOException e) {
			
			JOptionPane.showMessageDialog(m_parent,
    				"<html>" + getMessagesStrs("impposibleToSaveFileCropSaveOperation")
    				+ "<br>" + outputfile.getAbsolutePath() + "</html>",
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
		}
	
	
	}
	
	/**
	 * Reaction on Cancel button
	 * @param e
	 */
	public void onCancel(ActionEvent e) 
	{

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
		
		int result = sourcePhotoFile.showOpenDialog( this );
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourcePhotoFile.getSelectedFile().getPath();
			
			m_path.setText(name);	
		
		}
	}

	 
	 
	 private class ComboListener implements ActionListener {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				
			  
			       String typeName = (String)m_typesCombo.getSelectedItem();
			       
			       if(typeName.equals(m_types[0])) {
			    	   
			    	   m_createSubFolders.setSelected(true);
			    	   
			       }
			       else {
			    	   if(!m_saveNegs.isSelected()) {
				    	   
				    	   m_createSubFolders.setSelected(false);
				    	   
				       }  
			       }  
			}
	}
	 
	 private boolean createSubFolders(String baseFolder, ArrayList<String> types, ArrayList<Path> basePathes)
	 {
		 for (int i =0; i < types.size(); ++i) {
			 
			 Path path = Paths.get(baseFolder, types.get(i));
			
			 try {
				  
				  if ( !Files.exists(path)) {
					  Files.createDirectories(path);
					 
				  }
				  basePathes.add(path);
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
				return false;
			}
			 
		 }
		 
		 
		 return true;

	 }
	 
	private boolean createNegSubFolder(String baseFolder, ArrayList<Path> basePathes)
	{
		
			 Path pathNeg = Paths.get(baseFolder, "negs");
			 
			 try {
				  
				  if ( !Files.exists(pathNeg)) {
					  
					  Files.createDirectories(pathNeg);
					 
				  }
				  
				  basePathes.add(pathNeg);
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
				return false;
			}
			 
			return true;
	
	}

}
