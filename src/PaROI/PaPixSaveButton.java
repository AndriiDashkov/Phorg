package PaROI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import PaEditor.PaComplexButton;
import PaEditor.PaEnumInstrumentNames;
import PaEditor.PaQueuePanel;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;

/**
 * @author Andrey Dashkov
 * <p>New ROI crop operation button.</p>
 */
public class PaPixSaveButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	
	private String m_save_path = System.getProperty("user.home") + PaUtils.getSeparator();

	private boolean m_saveRGBdata = true;
	
	private boolean m_saveHSIdata = true;
	
	private boolean m_keepActivated = true;
	
	private PrintWriter m_printWriter = null;
	
	private FileWriter m_fw = null;
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d - size of the complex button
	 * @param hash - the container to hold all complex buttons in the application; it must be only one
	 */
	public PaPixSaveButton(PaRoiWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_ROI_CROP, parent,
				new ImageIcon(PaUtils.get().getIconsPath() + "pasavepixelsinst.png"),d, true, hash);
		
		setToolTips();
		
		setInitialData();
	}
	
	/**
	 * <p>Opens special side panel for this button. </p>
	 */
	protected void openSpecialPanel() {	
		
		super.openSpecialPanel();
		
	}
	
	/**
	 * Sets initial data from outside queue for this instrument.
	 * Overloaded function, is invoked from PaComplexButton level.
	 */
	@Override
	protected void setInitialData() {
		
		Data data = new Data();
		
		data.save_path = System.getProperty("user.home") + PaUtils.getSeparator() + "rgb_hsi_data.txt";
		
		data.saveRGBdata = true;
		
		data.saveHSIdata = true;
		
		data.keepActivated = true;
			
		if(m_queue != null && !m_queue.isEmpty()) {	
			
			try {
				
				Data data1 = (Data)m_queue.take();
				
				setData(data1);
				
				m_queue.put(data1);
				
			} catch (InterruptedException e) {
				
				setData(data);
			}
		}
		else {
			
			setData(data);
		}
	}
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters side panel</p>
	 */
	@Override
	protected JDialog createSpecialPanel() {
		
		SpecialPanel p = new SpecialPanel();
		
		return p;
		
	}
	/**
	 * Sets tooltips
	 */
	private void setToolTips() {
		
		m_mainButton.setToolTipText(getGuiStrs("saveRGBDataROIbutButtonToolTip"));	
		
	}
	
	public String getSavePath() { return m_save_path; }
	
	public boolean isRGBsaveSelected() { return m_saveRGBdata; }	
	
	public boolean isHSIsaveSelected() { return m_saveHSIdata; }	

	public boolean isKeepInstrumentActivated() { return m_keepActivated;}
	
	public PrintWriter getPrintWriter() { return m_printWriter; }
	

	public void closeWriter() {
		
		if (m_printWriter != null && m_fw != null) {
			
			try {
				
				m_printWriter.flush();
				
				m_printWriter.close();
				
				m_fw.flush();
				
				m_fw.close();
				
			} catch (IOException e) {
	
			}		
		}
	}
	
	
	/**
	 * 
	 * @author Andrey Dashkov
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through m_coeff member of PaContrastButton.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;

		private JTextField m_path_ph = new JTextField(25);
		
		private JComboBox<String> m_recent_combo = new JComboBox<String>();
		
		CustomListener m_forwarder = new CustomListener();
		
		JCheckBox m_checkBoxHSI  = new JCheckBox( getGuiStrs("checkBoxHSIPixSaveInsLabel"));
		
		JCheckBox m_checkBoxRGB  = new JCheckBox( getGuiStrs("checkBoxRGBPixSaveInsLabel"));
		
		JCheckBox m_keepInstAuto  = new JCheckBox();
		
		public JButton m_selectPath = new JButton(" ... ");
		
		public SpecialPanel() {
			
			super();
		
			createGui();
			
			m_recent_combo.setModel(new DefaultComboBoxModel<String>(PaUtils.get().getRecents().getRecentPixList().toArray(new String[0])));
			
			setToolTips();
			
			pack();
			
			setResizable(false);
		}
		
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	
		
			JPanel panelParam = PaGuiTools.createVerticalPanel();
		
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("roiPixSaveInsCaption"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelParam.setBorder(title_0);
			
			JPanel panelSave = PaGuiTools.createHorizontalPanel();
			
			panelSave.add(m_checkBoxRGB);
			
			panelSave.add(m_checkBoxHSI);
			
			panelSave.add(Box.createHorizontalGlue());
						
			panelParam.add(panelSave);
			
			GridBagLayout grid = new GridBagLayout();
			
			JPanel panelGrid = PaGuiTools.createHorizontalPanel();	
			
			panelGrid.setLayout(grid);
			
			panelParam.add(panelGrid);
			
			GridBagConstraints c = new GridBagConstraints();
			
			c.fill = GridBagConstraints.HORIZONTAL;
			
			c.gridx = 0;
			
			c.gridy = 0;
			
			c.anchor = GridBagConstraints.WEST;
			
			JLabel path_lph = new JLabel (getGuiStrs("fileHSIRGBsaveInsLbl"));
			
			path_lph.setFont(m_font);
			
			grid.setConstraints(path_lph, c);
			
			panelGrid.add(path_lph);

			c.fill = GridBagConstraints.VERTICAL;
			
			c.gridx = 1;
			
			c.gridy = 0;
			
			c.anchor = GridBagConstraints.WEST;
			
			grid.setConstraints(m_path_ph, c);
			
			panelGrid.add(m_path_ph);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			
			c.gridx = 2;
			
			c.gridy = 0;
			
			c.anchor = GridBagConstraints.WEST;
			
			grid.setConstraints(m_selectPath, c);
			
			panelGrid.add(m_selectPath);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			
			c.gridx = 0;
			
			c.gridy = 1;
			
			c.anchor = GridBagConstraints.WEST;
			
			JLabel labelRecent = new JLabel (getGuiStrs("recentfilePixInsLbl")); 
			
			labelRecent.setFont(m_font);
			
			grid.setConstraints(labelRecent, c);
			
			panelGrid.add(labelRecent);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			
			c.gridx = 1;
			
			c.gridy = 1;
			
			c.anchor = GridBagConstraints.WEST;
			
			grid.setConstraints(m_recent_combo, c);
			
			panelGrid.add(m_recent_combo);
			
			JPanel autoPanel = PaGuiTools.createHorizontalPanel();
			
			JLabel lautoInstr = new JLabel(" " + getGuiStrs("keepInstrActivatedCropLabel") + " ");
			
			autoPanel.add(lautoInstr);
			
			autoPanel.add(m_keepInstAuto);
			
			autoPanel.add(Box.createHorizontalGlue());
			
			panelParam.add(autoPanel);

			JPanel panelHoriz =  PaGuiTools.createHorizontalPanel();
			
			PaQueuePanel pV = new PaQueuePanel(PaPixSaveButton.this, m_queue);
			
			panelHoriz.add(panelParam);
			
			panelHoriz.add(pV);
			
			add(panelHoriz);
			
			m_checkBoxHSI.setSelected(true);
			
			m_checkBoxRGB.setSelected(true);
			
			m_keepInstAuto.setSelected(true);
			
			m_path_ph.addActionListener(m_forwarder);
			
			m_selectPath.addActionListener(m_forwarder);
					
			m_keepInstAuto.addActionListener(m_forwarder);
			
			m_checkBoxHSI.addActionListener(m_forwarder);
			
			m_checkBoxRGB.addActionListener(m_forwarder);
			
			m_recent_combo.addActionListener(m_forwarder);
					
			PaUtils.setComponentsFont(this, m_font);
	
		}
		
	   class CustomListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				

		    	if(e.getSource() == m_keepInstAuto ) {
		    		
		    		m_keepActivated = m_keepInstAuto.isSelected();
		    		
		    	}
		    	if(e.getSource() == m_checkBoxHSI ) {
		    		
		    		m_saveHSIdata = m_checkBoxHSI.isSelected();
		    		
		    		if(!m_saveHSIdata && !m_checkBoxRGB.isSelected()) {
		    			
		    			m_checkBoxRGB.setSelected(true);
		    			
		    			m_saveRGBdata = true;
		    		}
		    		
		    	}
		    	if(e.getSource() == m_checkBoxRGB ) {
		    		
		    		m_saveRGBdata = m_checkBoxRGB.isSelected();
		    		
		    		if(!m_saveRGBdata && !m_checkBoxHSI.isSelected()) {
		    			
		    			m_checkBoxHSI.setSelected(true);
		    			
		    			m_saveHSIdata = true;
		    		}
		    		
		    	}
		    	
		    	if ( e.getSource() == m_selectPath ) {
		    		
		    		onSelectPath(e);
		    		
		    		m_save_path = m_path_ph.getText();
		    		
		    		PaUtils.get().getRecents().addtoRecentPixList(m_save_path);
		    		
		    	}
		    	
		    	if ( e.getSource() == m_path_ph ) {
		    		
		    		m_save_path = m_path_ph.getText();
		    		
		    		PaUtils.get().getRecents().addtoRecentPixList(m_save_path);
		    		
		    	}
		    	
		    	if ( e.getSource() == m_recent_combo ) {
		    		
		    		String s = (String) m_recent_combo.getSelectedItem();
		    		
		    		if(!s.isEmpty()) {
		    			
		    			m_save_path = s;
		    			
		    			m_path_ph.setText(s);
		    		}
		    		
		    	}
			}
	    } 
	   
		/**
		 * Reaction for ... button
		 * @param e
		 */
		public void onSelectPath(ActionEvent e) {

			JFileChooser m_chooserFile = new JFileChooser();
			
			int result = m_chooserFile.showOpenDialog(m_parent);
			
			//file is Ok
			if (result == JFileChooser.APPROVE_OPTION) {
				
				String name = m_chooserFile.getSelectedFile().getPath();
				
				m_path_ph.setText(name);
				
			}

		}

		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_checkBoxRGB.setToolTipText(getGuiStrs("rgbSaveInsTooltip"));
			
			m_checkBoxHSI.setToolTipText(getGuiStrs("hsiSaveInsTooltip"));
			
			m_selectPath.setToolTipText(getGuiStrs("selectPathSaveRGBHSITooltip"));
			
			m_keepInstAuto.setToolTipText(getGuiStrs("keepInstrActivatedCheckTooltip"));
			
		}
	}
	/**
	 * Sets all data to inner members of the button
	 */
	protected void setAllData() {

	}
	
	private PrintWriter getNewPrintWriter(String fullPath) {
		
		
		if(fullPath == null || fullPath == "") { return null;}
		
		Path path = Paths.get(fullPath);
		
		if (Files.isDirectory(path)) {
			
			return null;
		}
		
		File f = new File(fullPath);
		
		if (!Files.exists(path)) {
			
			 try {
				 
				f.createNewFile();
				
			} catch (IOException e) {
				
				return null;
			}
		}
		
		try {
			
			m_fw = new FileWriter(f, true);
			
		} catch (IOException e) {
			
			return null;
		}
	    
	    return new PrintWriter(new BufferedWriter(m_fw));
		
	}

	
	/**
	 * <p>Starts the new ROI creation instrument</p>
	 */
	@Override
	protected void startInstrumentImpl() {
		
		m_printWriter = getNewPrintWriter(getSavePath());
		
		if (m_printWriter == null) {
			
			JOptionPane.showMessageDialog(m_parent,
    				getMessagesStrs("saveRGBHSIFileNOTLoaded"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
				
			m_parent.resetInstrument();
			
			m_mainButton.setSelected(false);
			
			return;
		}
		
		writeLog("Instruments window: the save HSI/RGB data instrument has been invoked.", null, 
					true, false, false );
				
		m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.ROI_PIXELS_SAVE,this);

	}
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
		
		m_saveHSIdata = d.saveHSIdata;
		
		m_saveRGBdata = d.saveRGBdata;
		
		m_save_path = d.save_path;
		
		m_keepActivated = d.keepActivated;
	
		if(m_menuPanel != null) {
			
			SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			p.m_checkBoxHSI.setSelected(m_saveHSIdata);
			
			p.m_checkBoxRGB.setSelected(m_saveRGBdata);
			
			p.m_path_ph.setText(m_save_path);
			
			p.m_keepInstAuto.setSelected(m_keepActivated);
			
		}
	}
	
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.saveHSIdata = m_saveHSIdata;
		
		d.saveRGBdata = m_saveRGBdata;
		
		d.save_path = m_save_path;
		
		d.keepActivated = m_keepActivated;
		
		return d;
	}
	
	private class Data {
			
		public String save_path;
		
		public boolean saveRGBdata = true;
		
		public boolean saveHSIdata = true;
		
		public boolean keepActivated = true;
	}
}
