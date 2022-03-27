
package PaEditor;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMenusStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import PaAlgorithms.PaAlgoConvert;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;


/**
 * @author avd
 * <p>This class determines the complex button for binary operation; 
 * the special parameters side panel creates here also. Binary operation is an operation which converts an image to
 * black/white state with using some thresholds</p>
 */
public class PaBinaryButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;


	public PaOperationTypePanel m_operationTypePanel;
	
	//color to use for operation; the chosen threshold are used with selected color only (red, green, blue)
	//other color components just are filled with calculated value
	int m_color = 0; 
	
	//number of thresholds; classical binary operation uses only one threshold; here some thresholds can be used - in this case
	//the operation can't be considered as binary, but it is still can be interesting for some purposes
	int m_numberOfThreshold = 1;
	
	//if number of thresholds == 1 then the user can chose the value of this threshold; in  opposite case 
	//the thresholds values are automatically calculated by dividing the range 0 ..255 in appropriative manner
	int m_singleThreshold = 50;
	
	boolean m_greyScaleActivated = false;
	
	boolean isWholeImage = false;
	

	int getNumberOfThresholds() { return m_numberOfThreshold; };
	
	int getTypeOfColor() { return m_color; }
	
	boolean isGrayScaleActivated() { return m_greyScaleActivated; } 
	
	int[] getThresholds() { 
		
		int[] ar = new int[m_numberOfThreshold];
		
		if (m_numberOfThreshold == 1) {
				
			ar[0]= m_singleThreshold;
			
			return ar;
		}
		else {
			
			int step = 255/(m_numberOfThreshold + 1);
			
			for(int i = 0; i < ar.length; ++i) {
				
				ar[i] = step * (i + 1);
			}
		}
		
		return ar;
	}
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param icon
	 * @param d
	 */
	public PaBinaryButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_BINARY, parent,new ImageIcon(PaUtils.get().getIconsPath() + "pabinaryinst.png"), d, true, hash);
		
		setToolTips();
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return new SpecialPanel();
		
	}


	private void setToolTips() {
		
		m_mainButton.setToolTipText(getMenusStrs("binaryInsToolTip"));	
	}

	/**
	 * 
	 * @author avd
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through members of the class
	 * Special parameters side panel appears next to the instrument button while using the small menu 
	 * button on the right side</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		
		JComboBox<String> m_combo = null;
		
		JCheckBox m_checkBoxGray = null;
		
		JSpinner m_Spinner = null;
		
		JSpinner m_Spinner2 = null;
		
		public SpecialPanel() {
			
			super();
			
			m_operationTypePanel  = new PaOperationTypePanel();
		
			createGui();
			
			setListeners();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
		}
		/**
		 * <p>Creates UI for button side parameters panel</p>
		 */
		private void createGui() {
			
			
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

			setFont(m_font);
			
			m_checkBoxGray = new JCheckBox();
		
			
			JLabel labelCheckBox = new JLabel(getGuiStrs("binaryInstCheckGrayLabel"));
			
			JPanel panelCheck = PaGuiTools.createHorizontalPanel();
			
			panelCheck.add(m_checkBoxGray);
			
			panelCheck.add(labelCheckBox);
			
			panelCheck.add(Box.createHorizontalGlue());
			
			panelCheck.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("grayScaleBinaryCaption"),TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, m_font));
			
			String[] list = { 
					
					getGuiStrs("redListItem"),//0
					
					getGuiStrs("greenListItem"),//1
					
					getGuiStrs("blueListItem"),//2
					
					getGuiStrs("allListItem")//3
					};
			
			
			JLabel lC = new JLabel(getGuiStrs("colorsToUseBinaryInstLabel") + " ");
			
			m_combo = new JComboBox<String>(list);
			
			JPanel panelCombo = PaGuiTools.createHorizontalPanel();
			
			panelCombo.add(lC);
			
			panelCombo.add(m_combo);
			
			
			JLabel labelSpinner = new JLabel(getGuiStrs("spinnerThBinaryInstLabel") + " ");
	
			JPanel panelSpin = PaGuiTools.createHorizontalPanel();
			
			//number of thresholds spinner
			SpinnerNumberModel spModel1 = new SpinnerNumberModel(1, 1, 50, 1);
			
			m_Spinner = new JSpinner(spModel1);
			
			PaGuiTools.fixComponentSize(m_Spinner);
			
			PaGuiTools.setComponentFixedWidth(m_Spinner, 60);
			
			panelSpin.add(labelSpinner);
			
			panelSpin.add(m_Spinner);
			
			panelSpin.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
			
			
			//JPanel panelSpin2 = PaGuiTools.createHorizontalPanel();
			JLabel labelSpinner2 = new JLabel(getGuiStrs("spinnerSThBinaryInstLabel") + " ");
			
			//threshold spinner
			SpinnerNumberModel spModel2 = new SpinnerNumberModel(50, 1, 255, 1);
			
			m_Spinner2 = new JSpinner(spModel2);
			
			PaGuiTools.fixComponentSize(m_Spinner2);
			
			PaGuiTools.setComponentFixedWidth(m_Spinner2, 60);
			
			panelSpin.add(labelSpinner2);
			
			panelSpin.add(m_Spinner2);
			
			JPanel mainPanel = PaGuiTools.createHorizontalPanel();
			
			JPanel leftPanel = PaGuiTools.createVerticalPanel();
			
			leftPanel.add(panelCheck);
			
			leftPanel.add(m_operationTypePanel);
			
			JPanel binPanel = PaGuiTools.createVerticalPanel();
			
			binPanel.add(panelCombo);
			
			binPanel.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			binPanel.add(panelSpin);
			
			mainPanel.add(leftPanel);
			
			binPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("binaryInstBinaryPanelCaption"),TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, m_font));
			
			mainPanel.add(binPanel);
			
			PaQueuePanel pV = new PaQueuePanel(PaBinaryButton.this, m_queue);
			
			mainPanel.add(pV);
			
			mainPanel.setFont(m_font);
			
			add(mainPanel);
						
			PaUtils.setComponentsFont(this, m_font);
			
			m_checkBoxGray.setSelected(false); 
	
		}
		private void setToolTips() {
			
			m_combo.setToolTipText(getGuiStrs("comboBinaryInstTooltip"));
			
			m_Spinner.setToolTipText(getGuiStrs("spinnerThBinaryInstTooltip"));
			
			m_Spinner2.setToolTipText(getGuiStrs("spinnerSThBinaryInstTooltip"));

		}
		
		/**
		 * <p>Sets listener for all components</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
			
			SpinnerListener l1 = new SpinnerListener();
			
			m_combo.addActionListener(l);
			
			m_Spinner.addChangeListener(l1);
			
			m_Spinner2.addChangeListener(l1);
			
			m_checkBoxGray.addActionListener(l);
		
		}
		
		/**
		 * 
		 * @author avd
		 * <p>Listener class to listen the combo box</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {		
				
				if(e.getSource() ==  m_checkBoxGray) {
					
					m_greyScaleActivated = m_checkBoxGray.isSelected(); 
					
					if( m_greyScaleActivated) {
						
						m_Spinner2.setEnabled(false);
						
						m_Spinner.setEnabled(false);
						
						m_combo.setEnabled(false);
					}
					else {
						
						m_Spinner2.setEnabled(true);
						
						m_Spinner.setEnabled(true);
						
						m_combo.setEnabled(true);
					}

		    	}
				if(e.getSource() ==  m_combo) {
					
					 m_color = m_combo.getSelectedIndex();
					 
				}
				
			}
		}
		/**
		 * @author avd
		 * <p>Listener class to listen all spinners</p>
		 */
		private class SpinnerListener implements ChangeListener {

			@Override
			public void stateChanged(ChangeEvent e) {	
				
				if(e.getSource() == m_Spinner ) {
					
					m_numberOfThreshold = (int)m_Spinner.getValue();
					
					if (m_numberOfThreshold > 1) {
						
						m_Spinner2.setEnabled(false);
					}
					else {
						
						m_Spinner2.setEnabled(true);
					}
					
				}
				else
				if(e.getSource() == m_Spinner2 ) {
					
					 m_singleThreshold = (int)m_Spinner2.getValue();
					
				}
				else
				if(e.getSource() == m_Spinner2 ) {
					
					 m_singleThreshold = (int)m_Spinner2.getValue();
					
				}

			}
		}
		
	}
	
	/**
	 * <p>Starts binary instrument</p>
	 */
	protected void startInstrumentImpl() {
		
		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		writeLog("Instruments window: binary convertation  started.", null, true,
				false, false );
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			
			if(m_operationTypePanel.isWholeImage()) {
				
				m_parent.resetInstrument();
				
				BufferedImage newIm = null;
				
				if(isGrayScaleActivated()) {
					
					newIm = (BufferedImage) PaAlgoConvert.toGrayscale((BufferedImage)m_parent.getSourceViewImage());
				}
				else {
				
					newIm = (BufferedImage) PaAlgoConvert.toBinary((BufferedImage)m_parent.getSourceViewImage(),  getTypeOfColor(),  getThresholds());
				}
							
				m_parent.setResultView(newIm, getGuiStrs("binaryInstrumentName"));
				 

				m_mainButton.setSelected(false);
				
				return;
			}
			
			//the case when the definite area selected by user (set instrument for further proccessing with the instrument)
			if ( m_mainButton.isSelected() ) {
				
				writeLog("Instruments window: binary instrument operation started.", null, true,
						false, false );
				
				m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.Binary, this);
				
			}
			else { 
				m_parent.getWorkPanel().resetInstrument();
			}	
						
		}
		catch(OutOfMemoryError e) {
			
			JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("outOfMemoryMessage"),
				    getMessagesStrs("messageCaption"),
				    JOptionPane.OK_OPTION);
			
			writeLog("Not enough memory: can't get result image for binary instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			writeLog("Instruments window: binary instrument operation finished.", null,
					true, false, false );
		}	
	}
	
	
	
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
				
		m_operationTypePanel.setWholeImageSelected(d.isWholeImage);
		
	    m_color = d.color; 
		
		m_numberOfThreshold = d.numberOfThreshold ;
		
		m_singleThreshold = d.singleThreshold ;
		
		m_greyScaleActivated = d.greyScaleActivated ;
		
		
		if(m_menuPanel != null) {
			
			SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			p.m_combo. 	setSelectedIndex(m_color);
			
			p.m_checkBoxGray.setSelected(m_greyScaleActivated);
			
			p.m_Spinner.setValue(Integer.valueOf(m_numberOfThreshold));
			
			p.m_Spinner2.setValue(Integer.valueOf(m_singleThreshold));
			
			
			if (m_numberOfThreshold > 1) {
				
				p.m_Spinner2.setEnabled(false);
			}
			else {
				
				p.m_Spinner2.setEnabled(true);
			}
			
			p.m_Spinner2.setEnabled(!m_greyScaleActivated);
			
			p.m_Spinner.setEnabled(!m_greyScaleActivated);
			
			p.m_combo.setEnabled(!m_greyScaleActivated);
			
		}
	}
	
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.isWholeImage = m_operationTypePanel.isWholeImage();
		
		d.color = m_color; 
		
		d.numberOfThreshold = m_numberOfThreshold;
		
		d.singleThreshold =  m_singleThreshold;
		
		d.greyScaleActivated =  m_greyScaleActivated;
				
		return d;
	}
	
	private class Data {
		
		public int color; 
		
		public int numberOfThreshold;
		
		public int singleThreshold;
		
		public boolean greyScaleActivated;
		
		public boolean isWholeImage;
			
	}
	
}