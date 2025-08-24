
package paeditor;


import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMenusStrs;
import static paglobal.PaUtils.getMessagesStrs;

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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.ProgressMonitor;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paalgorithms.PaAlgorithms;
import paalgorithms.PaFftAlgorithms;
import paglobal.PaGuiTools;
import paglobal.PaUtils;
import palong.PaContrastOperation;

/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for contrast operation; 
 * the special parameters panel creates here</p>
 */
public class PaContrastButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	
	private final int MAX_SL = 100;
	
	private final int MIN_SL = -100;
	
	private double m_gamma = 0.45;

	private int m_sliderValue = 0;
	/**
	 * This member determines the range of brightness which will be affected by linear algorithm
	 * The whole range is 0 ..255 
	 */
	private int m_range = 150;

	private PaContrastInfo m_contrastFlags;
	
	/**
	 * Very important value - it determines the range which will be reflected from gamma
	 * range 0 ...1.0 when the two part function is created
	 */
	private final double m_maxGamma = 6.0;
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaContrastButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_CONTRAST, parent,
				new ImageIcon(PaUtils.get().getIconsPath() + "pacontrast.png"), d, false, hash);
		
		//false - not toggled button
		m_contrastFlags = new PaContrastInfo();
		
		setToolTips();
		
	}
	
	/**
	 * <p>Opens special panel for this button panels
	 * We reload it in order to set some flag in normal way</p>
	 */
	protected void openSpecialPanel() {	
		
		super.openSpecialPanel();
		
		((SpecialPanel)m_menuPanel).controlsButtonsState();
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
		
		m_mainButton.setToolTipText(getMenusStrs("contrastIncToolTip"));	
	}

	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through m_coeff member of PaContrastButton.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		JRadioButton m_whiteImRadio = new JRadioButton(getGuiStrs("whiteContrastRadioCaption"));
		
		JRadioButton m_darkImRadio = new JRadioButton(getGuiStrs("darkContrastRadioCaption"));
		
		JRadioButton m_lowContrastImRadio = new JRadioButton(getGuiStrs("lowContrastRadioCaption"));
		
		ButtonGroup m_butGroupImType = new ButtonGroup();
		
		JRadioButton m_linearFuncRadio  = new JRadioButton(getGuiStrs("linearContrastRadioCaption"));
		
		JRadioButton m_powerFuncRadio  = new JRadioButton(getGuiStrs("powerContrastRadioCaption"));
		
		JRadioButton m_freqFuncRadio  = new JRadioButton(getGuiStrs("freqContrastRadioCaption"));
		
		ButtonGroup m_butGroupFType = new ButtonGroup();
		
		JSlider m_slider;
		
		JSlider m_sliderRange;

		public SpecialPanel() {
			
			super();
		
			createGui();
			
			setListeners();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			JPanel panelImType = new JPanel();
			
			panelImType.setLayout(new GridLayout(3, 1));
			
			panelImType.add(m_whiteImRadio);
			
			panelImType.add(m_darkImRadio);
			
			panelImType.add(m_lowContrastImRadio);
	
			String sImType = getGuiStrs("typeOfContrastSouceImage"); 
			
			TitledBorder titleImType = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					sImType,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);

			
			panelImType.setBorder(titleImType);
			
			Dimension d1 =  panelImType.getPreferredSize();
			
			d1.width = (int)titleImType.getMinimumSize(this).getWidth()+10;	
			
			panelImType.setPreferredSize(d1);
			
			m_butGroupImType.add(m_whiteImRadio);
			
			m_butGroupImType.add(m_darkImRadio);
			
			m_butGroupImType.add(m_lowContrastImRadio);
			
			JPanel panelFuncType = new JPanel();
			
			panelFuncType.setLayout(new GridLayout(3, 1));
			
			panelFuncType.add(m_linearFuncRadio);
			
			panelFuncType.add(m_powerFuncRadio);
			
			panelFuncType.add(m_freqFuncRadio);

	
			TitledBorder titleFuncType = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("typeOfAlgorithm"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelFuncType.setBorder(titleFuncType);
			
			d1 = panelFuncType.getPreferredSize();
			
			d1.width = (int)titleFuncType.getMinimumSize(this).getWidth()+40;
			
			panelFuncType.setPreferredSize(d1);
			
			m_butGroupFType.add(m_linearFuncRadio);
			
			m_butGroupFType.add(m_powerFuncRadio);
			
			m_butGroupFType.add(m_freqFuncRadio);
			
			m_slider = new JSlider(JSlider.HORIZONTAL, MIN_SL, MAX_SL, 0);
			
			m_sliderRange = new JSlider(JSlider.HORIZONTAL, 50, 200, 150);
				
			JPanel panelParam = PaGuiTools.createVerticalPanel();
			
			JLabel labelSlider = new JLabel(getGuiStrs("sliderContrastLabel") + " ");
			
			JLabel labelSliderRange = new JLabel(getGuiStrs("rangeCoeffContrastLabel") + " ");

			JPanel panelSlider = PaGuiTools.createVerticalPanel();
			
			JPanel panelSliderLabel = PaGuiTools.createHorizontalPanel();
			
			panelSliderLabel.add(Box.createHorizontalGlue());
			
			panelSliderLabel.add(labelSlider);
			
			panelSliderLabel.add(Box.createHorizontalGlue());
			
			panelSlider.add(panelSliderLabel);
			
			panelSlider.add(m_slider);
			
			JPanel panelSliderR = PaGuiTools.createVerticalPanel();
			
			JPanel panelSliderLabelR = PaGuiTools.createHorizontalPanel();
			
			panelSliderLabelR.add(Box.createHorizontalGlue());
			
			panelSliderLabelR.add(labelSliderRange);
			
			panelSliderLabelR.add(Box.createHorizontalGlue());
			
			panelSliderR.add(panelSliderLabelR);
			
			panelSliderR.add(m_sliderRange);
			
			panelParam.add(panelSlider);
			
			panelParam.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			panelParam.add(panelSliderR);
		
			JPanel mainPanel = PaGuiTools.createHorizontalPanel();
			
			mainPanel.add(panelFuncType);
			
			mainPanel.add(panelImType);
	
			mainPanel.add(panelParam);
			
			add(mainPanel);
			
			m_contrastFlags = new PaContrastInfo();
			
			m_lowContrastImRadio.setSelected(m_contrastFlags.lowFlag);
			
			m_powerFuncRadio.setSelected(m_contrastFlags.powerFlag);

			m_whiteImRadio.setSelected(m_contrastFlags.whiteFlag);
			
			m_darkImRadio.setSelected(m_contrastFlags.darkFlag);
			
			m_linearFuncRadio.setSelected(m_contrastFlags.linearFlag);
			
			m_freqFuncRadio.setSelected(m_contrastFlags.freqFlag);
			
			PaUtils.setComponentsFont(this, m_font);
	
		}
		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_whiteImRadio.setToolTipText(getGuiStrs("whiteContrastRadioTooltip"));
			
			m_darkImRadio.setToolTipText(getGuiStrs("darkContrastRadioTooltip"));
			
			m_lowContrastImRadio.setToolTipText(getGuiStrs("lowContrastRadioTooltip"));
			
			m_linearFuncRadio.setToolTipText(getGuiStrs("linearContrastRadioTooltip"));
			
			m_powerFuncRadio.setToolTipText(getGuiStrs("powerContrastRadioTooltip"));
			
			m_freqFuncRadio.setToolTipText(getGuiStrs("freqContrastRadioTooltip"));
			
			m_slider.setToolTipText(getGuiStrs("contrastSliderTooltip"));
			
			m_sliderRange.setToolTipText(getGuiStrs("rangeSpinContrastRadioTooltip"));
		}
		
		/**
		 * <p>Sets listener for the radion buttons</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
				
			m_whiteImRadio.addActionListener(l);
			
			m_darkImRadio.addActionListener(l);
			
			m_lowContrastImRadio.addActionListener(l);
			
			m_linearFuncRadio.addActionListener(l);
			
			m_powerFuncRadio.addActionListener(l);
			
			m_freqFuncRadio.addActionListener(l);
			
			SliderListener l1 = new SliderListener(); 
			
			m_sliderRange.addChangeListener(l1);
			
			m_slider.addChangeListener(l1);
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen all radio buttons</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {
		 
				controlsButtonsState();

			}
		}
		
		class SliderListener implements ChangeListener {
			
		    public void stateChanged(ChangeEvent e) {

		    	m_sliderValue = m_slider.getValue();
		    	
		    	m_range = m_sliderRange.getValue();
		    }
		}
		
		/**
		 * <p>Controls of buttons state</p>
		 */
		private void controlsButtonsState() {
					
			m_contrastFlags.whiteFlag = m_whiteImRadio.isSelected();
			
			m_contrastFlags.darkFlag = m_darkImRadio.isSelected();
			
			m_contrastFlags.lowFlag = m_lowContrastImRadio.isSelected();
			
			m_contrastFlags.linearFlag = m_linearFuncRadio.isSelected();
			
			m_contrastFlags.powerFlag = m_powerFuncRadio.isSelected();
			
			m_contrastFlags.freqFlag = m_freqFuncRadio.isSelected();
			
			m_range = m_sliderRange.getValue();
			
			setComponentsEnabled();

		}
		
		private void setComponentsEnabled() {
	
			if(m_linearFuncRadio.isSelected()) {

				m_sliderRange.setEnabled(true);
				
				m_whiteImRadio.setEnabled(false);
				
				m_lowContrastImRadio.setEnabled(false);
				
				m_darkImRadio.setEnabled(false);
			}
			else {

				m_sliderRange.setEnabled(false);
				
				m_whiteImRadio.setEnabled(true);
				
				m_lowContrastImRadio.setEnabled(true);
				
				m_darkImRadio.setEnabled(true);
			}
			
			if(m_freqFuncRadio.isSelected()) {
				
				m_sliderRange.setEnabled(false);
				
				m_whiteImRadio.setEnabled(false);
				
				m_lowContrastImRadio.setEnabled(false);
				
				m_darkImRadio.setEnabled(false);
				
			}
			
		}
	}
	
	/**
	 * <p>Starts contrast instrument. We don't need here any child of PaInstrument class because 
	 * the contrast instrument does not operate in work area, only in preview area</p>
	 */
	protected void startInstrumentImpl() {

		writeLog("Instruments window: contrast instrument  operation started.", null,
				true, false, false );		
		//m_parent.getWorkPanel().setContrastInstrument(m_mainButton, m_coeff);
		
		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
				m_parent.resetInstrument();
			
			 BufferedImage im = PaUtils.deepCopy((BufferedImage)m_parent.getSourceViewImage());
			 
			 if(im == null) {
				 
				 writeLog("Can't find current image to use contrast instrument" , null, true, false, true);
				 
				 return;
			 }
			 BufferedImage resultImage = null;
	
			 if(m_contrastFlags.powerFlag) {
				 
				boolean onePartGamma = false;
				
				if(m_sliderValue < 0) { onePartGamma = true; }
				
				m_gamma = getGammaValue();//convert from slider value
				
				PaAlgorithms al = new PaAlgorithms();
				
				resultImage =  al.contrastPowerFunction(im,m_gamma,onePartGamma,m_maxGamma);
				
				m_parent.setResultView(resultImage, getGuiStrs("contrastInstrumentName"));
				
			 }
			 if(m_contrastFlags.linearFlag) {
				 
				 PaAlgorithms al = new PaAlgorithms();
				 
				 resultImage =  al.contrastLinearFunction(im, getLinearValue(),m_range);
				 
				 m_parent.setResultView(resultImage, getGuiStrs("contrastInstrumentName"));
			 }
			 if(m_contrastFlags.freqFlag) {
				 
				 //ask user if the operation is too long
				 if(PaUtils.askAboutLongOperation(im.getWidth(),im.getHeight(), m_parent)
						 == JOptionPane.NO_OPTION) {
							m_parent.getWorkPanel().resetInstrument();
							m_mainButton.setSelected(false);
						    return;
				 }
				 
				 double gamma = getExponentValue();
				 				 
				 //start long operation because blur can take minutes
				ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,
						getMessagesStrs("contrastLongOperationCaption")+" : " +
							getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
							+ "x"+im.getHeight(),
							getMessagesStrs("startContrastOperationCaption"), 0, 100);
					
				progressMonitor.setMillisToDecideToPopup(0);
				
				progressMonitor.setMillisToPopup(0);
				
				PaContrastOperation ts = new PaContrastOperation(progressMonitor,
						 gamma,im, m_parent);
					
				ts.execute();
			 }
		}
		catch(OutOfMemoryError e) {
			
			JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("outOfMemoryMessage"),
				    getMessagesStrs("messageCaption"),
				    JOptionPane.OK_OPTION);
			
			writeLog("Not enough memory: can't get result image for contrast instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window: contrast instrument operation finished.", null,
					true, false, false );
		}
		 		
	}	
	/**
	 * Converts the slider vaue from slider range value to the rang of powers of gamma function
	 * @return
	 */
	private double getGammaValue() {
		//0.1 0.45
		///1 2.5
		if(m_sliderValue == 0.0 ) return 0.0;
		
		PaFftAlgorithms al = new PaFftAlgorithms();
		
		if(m_sliderValue > 0 ) { //converts into range 0.6, 0.05
			
			return al.getInNewRange(0.05, 0.01, MAX_SL, 0.0, m_sliderValue);
		}
		else { //converts into range 2.5 .... 1
			
			return al.getInNewRange(2.0, 1.0, 0.0, MIN_SL, m_sliderValue);
		}
	}
	
	private double getLinearValue() {
		
		if(m_sliderValue == 0 ) { return 1.0; }
		
		PaFftAlgorithms al = new PaFftAlgorithms();
		
		if(m_sliderValue < 0 ) { //converts into range 0.9, 0.05
			
			return al.getInNewRange(0.9, 0.05, 0.0, MIN_SL, m_sliderValue);
		}
		else { //converts into range 255/range .... 1
			
			return al.getInNewRange(255.0/m_range, 1.05, MAX_SL, 0, m_sliderValue);
		}
		
	}
	
	private double getExponentValue() {
		
		if(m_sliderValue == 0 ) { return 1.0; }
		
		PaFftAlgorithms al = new PaFftAlgorithms();
		
		if(m_sliderValue < 0 ) { //converts into range 0.99, 0.85
			
			return al.getInNewRange(0.99, 0.89, 0.0, MIN_SL, m_sliderValue);
		}
		else { //converts into range
			
			return al.getInNewRange(1.2, 1.01, MAX_SL, 0, m_sliderValue);
		}
		
	}
	
	
	/*
	 * Class to get the information from outside
	 */
	public class PaContrastInfo {
		
		public boolean incFlag = true;
		
		public boolean decFlag = false;
		
		public boolean whiteFlag = false;
		
		public boolean darkFlag = false;
		
		public boolean lowFlag = true;
		
		public boolean linearFlag = false;
		
		public boolean powerFlag = true;
		
		public boolean freqFlag = false;
		
	}
}