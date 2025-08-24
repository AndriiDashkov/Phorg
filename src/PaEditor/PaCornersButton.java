
package paeditor;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getGuiStrs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paalgorithms.PaAlgoCorners;
import paenums.PaInstrumentTypeEnum;
import paglobal.PaGuiTools;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for Harris/Stephens corner/edge detector; 

 * value </p>
 */
public class  PaCornersButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	
	
	private final int INIT_MASK_SIZE = 3;

	int m_maskSize = INIT_MASK_SIZE;
	
	double m_sensitivity = 0.04;
	
	double m_threshold = 0.00015;
	
	boolean m_cornersFlag = true;
	
	boolean m_edgesFlag = true;
	
	int m_gaussBlurValue = 2;

	private Font m_font = PaUtils.get().getBaseFont();
	
	public PaOperationTypePanel m_operationTypePanel;
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public  PaCornersButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.MASK, parent, new ImageIcon(PaUtils.get().getIconsPath() + "pamaskinst.png"),
				d, true, hash );
		
		//true -  toggled button
		setToolTips();
		
	}

	/**
	 * <p>Opens special panel for this button panels
	 * We reload it in order to set some flag in normal way</p>
	 */
	protected void openSpecialPanel() {	
		
		super.openSpecialPanel();
		
	}
	

	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		SpecialPanel p = new SpecialPanel();
		
		return p;
		
	}
	/**
	 * Sets tooltips
	 */
	private void setToolTips() {
		
		m_mainButton.setToolTipText(getGuiStrs("harrisFilterButtonToolTip"));	
	}
	

	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through m_saveAsFlag member of the button class.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		JSpinner m_sizeMaskSpinner = null;
		
		JSpinner m_sensitivitySpinner = null;
		
		JSpinner  m_thresholdSpinner = null;
		
		JCheckBox m_cornersCheckBox = null;
		
		JCheckBox m_edgesCheckBox = null;
		
		JSpinner m_gaussBlurLevelSpinner;
				
		public SpecialPanel() {
			super();
			
			m_operationTypePanel  = new PaOperationTypePanel();
		
			createGui();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
			
			SpinnerListener l2 = new  SpinnerListener();
			
			m_sizeMaskSpinner.addChangeListener(l2);
			
			m_sensitivitySpinner.addChangeListener(l2);
			
			 m_thresholdSpinner.addChangeListener(l2);
			 
			 CheckBoxListener l3 = new CheckBoxListener();
			 
			 m_cornersCheckBox.addActionListener(l3);
			 
			 m_edgesCheckBox.addActionListener(l3);

		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			JLabel labelSpinner1 = new JLabel(getGuiStrs("spinnerSizeMaskInstLabel") + " ");
			

			SpinnerNumberModel spModel1 = new SpinnerNumberModel(INIT_MASK_SIZE, 3, 500, 1);
			
			m_sizeMaskSpinner = new JSpinner(spModel1);
			
			PaGuiTools.fixComponentSize(m_sizeMaskSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_sizeMaskSpinner, 60);

			JLabel labelSpinner2 = new JLabel(getGuiStrs("sencitivityHarrisFilterInstLabel") + " ");
			
			SpinnerNumberModel spModel2 = new SpinnerNumberModel(0.08, 0.01, 0.2, 0.005);
			
			m_sensitivitySpinner = new JSpinner(spModel2);
			
			PaGuiTools.fixComponentSize(m_sensitivitySpinner);
			
			PaGuiTools.setComponentFixedWidth(m_sensitivitySpinner, 60);
			
			JLabel labelSpinner3 = new JLabel(getGuiStrs("maskGuassHarrisDecreaseInstLabel") + " ");
			
			SpinnerNumberModel spModel3 = new SpinnerNumberModel(2, 1, 10, 1);
			
			m_gaussBlurLevelSpinner = new JSpinner(spModel3);
			
			PaGuiTools.fixComponentSize(m_gaussBlurLevelSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_gaussBlurLevelSpinner, 60);
			
			JLabel labelSpinner4 = new JLabel(getGuiStrs("thHarrisFilterInstLabel") + " ");
			
			SpinnerNumberModel spModel4 = new SpinnerNumberModel(0.00015, 0.0, 0.2, 0.00005);
			
			m_thresholdSpinner = new JSpinner(spModel4);
			
			//fox for very small threshold values
			JSpinner.NumberEditor edit = new JSpinner.NumberEditor(m_thresholdSpinner, "#.#######");
			
			m_thresholdSpinner.setEditor(edit);
			
			PaGuiTools.fixComponentSize(m_thresholdSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_thresholdSpinner, 60);
			
			JPanel panelButtonsVert =  PaGuiTools.createVerticalPanel();
			
			JPanel panelButtons = new JPanel();
			
			panelButtons.setLayout(new GridLayout(4, 2));
			
			panelButtons.add(labelSpinner1);
			
			panelButtons.add(m_sizeMaskSpinner);
			
			panelButtons.add(labelSpinner2);
			
			panelButtons.add(m_sensitivitySpinner);
			
			panelButtons.add(labelSpinner3);
			
			panelButtons.add(m_gaussBlurLevelSpinner);
			
			panelButtons.add(labelSpinner4);
			
			panelButtons.add(m_thresholdSpinner);

			panelButtonsVert.add(panelButtons);
			
			m_sizeMaskSpinner.setFont(m_font);
			
			m_sensitivitySpinner.setFont(m_font);
			
			panelButtons.setFont(m_font);
			
			labelSpinner1.setFont(m_font);
			
			labelSpinner2.setFont(m_font);
			
			labelSpinner3.setFont(m_font);
			
			labelSpinner4.setFont(m_font);
	
			JPanel panelFind = PaGuiTools.createHorizontalPanel();
				
			m_cornersCheckBox = new JCheckBox(getGuiStrs("cornersCheckBoxLabel") + " ");
			
			m_edgesCheckBox = new JCheckBox(getGuiStrs("edgesCheckBoxLabel") + " ");
			
			m_edgesCheckBox.setFont(m_font);
			
			m_cornersCheckBox.setFont(m_font);
			
			panelFind.add(m_cornersCheckBox);
			
			
			panelFind.add(m_edgesCheckBox);		
			
			panelFind.add(Box.createHorizontalGlue());
	
		
			Border title_S = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("harrisFindLabel"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, m_font);
			
			((TitledBorder) title_S).setTitleFont(m_font);
			
			panelFind.setBorder(title_S);
			
			panelFind.setFont(m_font);
			
			JPanel panelMain =PaGuiTools.createHorizontalPanel();
			
			JPanel panelRight =PaGuiTools.createVerticalPanel();

			panelRight.add(panelFind);
	
			panelRight.add(m_operationTypePanel);
			
			panelMain.add(panelRight);
			
			panelMain.add(panelButtonsVert);
			
			PaQueuePanel pV = new PaQueuePanel(PaCornersButton.this, m_queue);
			
			panelMain.add(pV);
			
			add(panelMain);
			
			m_cornersCheckBox.setSelected(true);
			
			m_edgesCheckBox.setSelected(true);

		}
				
		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_sizeMaskSpinner.setToolTipText(getGuiStrs("spinnerSizeMaskSpinnerMaskTooltip"));
			
			m_sensitivitySpinner.setToolTipText(getGuiStrs("sensitivityHarrisSpinnerMaskTooltip"));
			
			m_thresholdSpinner.setToolTipText(getGuiStrs("thHarrisfilterSpinnerMaskTooltip")); 
			
			m_gaussBlurLevelSpinner.setToolTipText(getGuiStrs("gaussMaskBlurLevelTooltip"));
			
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen the controls</p>
		 *
		 */
		class CheckBoxListener implements ActionListener {
	

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == m_edgesCheckBox) {
					
					if(!m_edgesCheckBox.isSelected() && !m_cornersCheckBox.isSelected()) {
						
						m_cornersCheckBox.setSelected(true);
					}
					
				}
				
				if(e.getSource() == m_cornersCheckBox) {
					
					if(!m_edgesCheckBox.isSelected() && !m_cornersCheckBox.isSelected()) {
						
						m_edgesCheckBox.setSelected(true);
					}
					
				}
				
				m_cornersFlag = m_cornersCheckBox.isSelected();
				
				m_edgesFlag = m_edgesCheckBox.isSelected();
				
			}
		}
		
		private class SpinnerListener implements ChangeListener {

			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(e.getSource() == m_sizeMaskSpinner) {
					
						 m_maskSize = (int)m_sizeMaskSpinner.getValue();
					
				}
				else 
				if(e.getSource() ==m_gaussBlurLevelSpinner) {
						
					  m_gaussBlurValue = (int)m_gaussBlurLevelSpinner.getValue();
					
				}
				else 
				if(e.getSource() == m_thresholdSpinner) {
						
					 m_threshold = (double)m_thresholdSpinner.getValue();
					
				}
				else 
				if(e.getSource() == m_sensitivitySpinner) {
						
					  m_sensitivity = (double)m_sensitivitySpinner.getValue();
					
				}
				
			}
		}
		
		
		public void setCornersCheckBoxSelected(boolean flag) { 
			
			m_cornersCheckBox.setSelected(flag); 	
		}
		
		public void setEdgesCheckBoxSelected(boolean flag) { 
			
			m_edgesCheckBox.setSelected(flag); 
		}
		
		public void setSizeMaskSpinnerValue(int v) { m_sizeMaskSpinner.setValue(Integer.valueOf(v)); }
		
		public void setSensitivitySpinnerValue(double v) { m_sensitivitySpinner.setValue(Double.valueOf(v)); }
		
		public void  setThresholdSpinnerValue(double v)  { m_thresholdSpinner.setValue(Double.valueOf(v)); }
		
		public void setGaussBlurSpinnerValue(int v) { m_gaussBlurLevelSpinner.setValue(Integer.valueOf(v)); }
		
		
		
	}
	
	public int getMaskSize() {  return m_maskSize;}
	
	public double getSensitivity() {  return m_sensitivity;}
	
	public int getGaussBlurValue() { return m_gaussBlurValue; }
	
	public double getThreshold() { return m_threshold; }
		
	public boolean isWholeImage() { return m_operationTypePanel.isWholeImage(); }
	
	public boolean isCornersSelected() { return m_cornersFlag; }
	
	public boolean isEdgesSelected() { return m_edgesFlag; }
	
	
	JCheckBox m_edgesCheckBox = null;

	/**
	 * <p>Starts the corners/edges finding instrument. </p>
	 */
	
	protected void startInstrumentImpl() {
		
		writeLog("Instruments window: mask instrument  operation started.", null,
				true, false, false );
		
		PaInstrument.isAnyInstrumentWasUsed = true; 
		
		try {
		
			if(m_operationTypePanel.isWholeImage()) {
				
				m_parent.resetInstrument();
				
				BufferedImage fullImage = PaUtils.deepCopy((BufferedImage)m_parent.getSourceViewImage());
				
				int ms = getMaskSize();
				
				double sigma = -(ms*ms)/ (2.0 * Math.log(1.0/getGaussBlurValue()) );
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				PaAlgoCorners.findCornersEdges(fullImage, sigma, ms, ms, getSensitivity(), getThreshold(), isCornersSelected(),  isEdgesSelected());
				
				m_parent.setResultView(fullImage, getGuiStrs("maskInstrumentName"));
	
				m_mainButton.setSelected(false);
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
				 
				return;
			}
			
			if ( m_mainButton.isSelected() ) {
				
				writeLog("Instruments window: mask instrument operation started.", null, true,
						false, false );
				
				m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.CornersEdgesFind, this);
				
			}
			else { 
				m_parent.getWorkPanel().resetInstrument();
			}	
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
		}
	
	}

	
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
		
		m_maskSize = d.maskSize ;
		
		m_sensitivity = d.sensitivity ;
		
		m_gaussBlurValue = d.gaussBlurValue ; 
		
		m_threshold = d.threshold ;
		
		m_operationTypePanel.setWholeImageSelected(d.isWholeImage);
		
		m_cornersFlag = d.cornersFlag ; 

		m_edgesFlag = d.edgesFlag  ; 
		
		if(m_menuPanel != null) {
			
			SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			p.setCornersCheckBoxSelected(m_cornersFlag);
			
			p.setEdgesCheckBoxSelected(m_edgesFlag);
			
			p.setSizeMaskSpinnerValue(m_maskSize );
			
			p.setSensitivitySpinnerValue(m_sensitivity);
			
			p.setThresholdSpinnerValue(m_threshold);
			
			p.setGaussBlurSpinnerValue(m_gaussBlurValue);
			
		}
	}
	
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.maskSize = m_maskSize;
		
		d.sensitivity = m_sensitivity;
		
		d.gaussBlurValue = m_gaussBlurValue; 
		
		d.threshold = m_threshold;
			
		d.isWholeImage = m_operationTypePanel.isWholeImage();
		
		d.cornersFlag = m_cornersFlag; 
		
		d.edgesFlag = m_edgesFlag ; 
		
		return d;
	}
	
	private class Data {
		
		public int maskSize;
		
		public double sensitivity;
		
		public int gaussBlurValue; 
		
		public double threshold; 
			
		public boolean isWholeImage; 
		
		public boolean cornersFlag; 
		
		public boolean edgesFlag; 
		
		
	}
	
}