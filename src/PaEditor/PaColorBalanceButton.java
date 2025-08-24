
package paeditor;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMessagesStrs;

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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paalgorithms.PaAlgorithms;
import paenums.PaInstrumentTypeEnum;
import paglobal.PaGuiTools;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for color balance change operation; 
 * the special parameters panel consists the sliders which determine the range of changing of red, green, blue color:
 * reduction of blue value is a warm direction and opposite is a cold direction </p>
 */
public class PaColorBalanceButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	/**
	 * Determines the type of operation - true - 'save as'; false - 'save'
	 */

	private final int MAX_SL = 1000;
	
	private final double SC_COEFF = 4.0; // this coeff means that the blue color will change in the range of 20 %

	public PaOperationTypePanel m_operationTypePanel;
	
	int[] m_sliderValue = new int[3];
	
	/**
	 * true if the user selected 'light hue' check box
	 */
	boolean m_lightHTCondition;
	
	private Font m_font = PaUtils.get().getBaseFont();
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaColorBalanceButton(PaInstrumentsWindow parent, Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_COLOR_BALANCE, parent, new ImageIcon(PaUtils.get().getIconsPath() + "pacolorbalance.png"), 
				d, true, hash);
		
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
		
		m_mainButton.setToolTipText(getGuiStrs("colorBalanceButtonToolTip"));	
	}
	

	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Special panel class.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;

		JSlider m_sliderBlue;
		
		JSlider m_sliderRed;
		
		JSlider m_sliderGreen;
	
		private JRadioButton m_lightHT = new JRadioButton(getGuiStrs("lightHalfTone"));
		
		private JRadioButton m_mediumHT = new JRadioButton(getGuiStrs("mediumHalfTone"));
		
		private ButtonGroup m_butGroupType = new ButtonGroup();
	
					
		public SpecialPanel() {
			
			super();
			
			m_operationTypePanel  = new PaOperationTypePanel();
		
			createGui();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
			
			m_lightHT.setSelected(true);
			
			ButtonsListener l = new ButtonsListener();
			
			ActionL actL = new ActionL();
			
			m_sliderBlue.addChangeListener(l);
			
			m_sliderRed.addChangeListener(l);
			
			m_sliderGreen.addChangeListener(l);
			
			m_lightHT.addActionListener(actL); 
			
			m_mediumHT.addActionListener(actL); 
				
		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setFont(m_font);
			
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			m_sliderBlue = new JSlider(JSlider.HORIZONTAL,-MAX_SL, MAX_SL, 0);
			
			m_sliderRed = new JSlider(JSlider.HORIZONTAL,-MAX_SL, MAX_SL, 0);
			
			m_sliderGreen = new JSlider(JSlider.HORIZONTAL,-MAX_SL, MAX_SL, 0);
			
			m_butGroupType.add(m_lightHT);
			
			m_butGroupType.add(m_mediumHT);

			JPanel panelMain = PaGuiTools.createHorizontalPanel();
			
			//left panel
			JPanel panelLabel = PaGuiTools.createHorizontalPanel();
			
			panelLabel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("sliderColorBalalnceName"),TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font));
			
			panelLabel.add(m_lightHT);
			
			panelLabel.add(m_mediumHT);
			
			panelLabel.add(Box.createHorizontalGlue());
			
			panelLabel.setFont(m_font);
			
			m_lightHT.setFont(m_font);
			
			m_mediumHT.setFont(m_font);
			
			JPanel panelLeft = PaGuiTools.createVerticalPanel();
			
			panelLeft.add(panelLabel);
			
			panelLeft.add(m_operationTypePanel);
			
			panelMain.add(panelLeft);
			
			//right panel
			JPanel panelRight = PaGuiTools.createVerticalPanel();
			
			panelMain.add(panelRight);
			
			JPanel panelSliderBlue =PaGuiTools.createHorizontalPanel();
			
			JPanel panelSliderRed =PaGuiTools.createHorizontalPanel();
			
			JPanel panelSliderGreen =PaGuiTools.createHorizontalPanel();
	
			JLabel blueLabel = new JLabel(" " +getGuiStrs("blueCaptionColorBalalnce"));
			
			blueLabel.setFont(m_font);
			
			panelSliderBlue.add(blueLabel);
			
			panelSliderBlue.add(m_sliderBlue);

			JLabel redLabel = new JLabel(" " +getGuiStrs("redCaptionColorBalalnce")); 
			
			redLabel.setFont(m_font);
			
			panelSliderRed.add(redLabel);
			
			panelSliderRed.add(m_sliderRed);

			JLabel greenLabel = new JLabel(" " +getGuiStrs("greenCaptionColorBalalnce"));
			
			greenLabel.setFont(m_font);
			
			panelSliderGreen.add(greenLabel);
			
			panelSliderGreen.add(m_sliderGreen);

			PaGuiTools.makeSameSize(new JComponent[] {redLabel,greenLabel ,blueLabel});
			
			panelRight.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
			 
			panelRight.add(panelSliderRed);
			
			panelRight.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
			
			panelRight.add(panelSliderGreen);
			
			panelRight.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
			
			panelRight.add(panelSliderBlue);
			
			panelRight.add(Box.createVerticalGlue());
			 
			panelMain.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
			
			panelMain.add(panelRight);
			
			PaQueuePanel pV = new PaQueuePanel(PaColorBalanceButton.this, m_queue);
			
			panelMain.add(pV);
		
			add(panelMain);
	
		}
		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_sliderBlue.setToolTipText(getGuiStrs("sliderBlueColorBalalnceTooltip"));
			
			m_sliderRed.setToolTipText(getGuiStrs("sliderRedColorBalalnceTooltip"));
			
			m_sliderGreen.setToolTipText(getGuiStrs("sliderGreenColorBalalnceTooltip"));
		}
		
		
		
	   class ActionL implements ActionListener {
		   
		   public void actionPerformed (ActionEvent e) {

	            if(e.getSource() == m_lightHT) {
	            	
	            	m_lightHTCondition = m_lightHT.isSelected();
	            }
	            else
	            if(e.getSource() == m_mediumHT) {
	            	
	            	m_lightHTCondition = !m_mediumHT.isSelected();
	            }
	        }
	    }
		
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen all the controls</p>
		 *
		 */
		class ButtonsListener implements ChangeListener {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(e.getSource() == m_sliderBlue) {
					
					m_sliderValue[2] = m_sliderBlue.getValue();
					
				}
				else
				if(e.getSource() == m_sliderRed) {
					
					m_sliderValue[0] = m_sliderRed.getValue();
				}
				else
				if(e.getSource() == m_sliderGreen) {
					
					m_sliderValue[1] = m_sliderGreen.getValue();
				}
			
			}
		}
	}
	
	/**
	 * <p>Starts color balance instrument. </p>
	 */
	protected void startInstrumentImpl() {

		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		writeLog("Instruments window: color balance instrument  operation started.", null,
				true, false, false );		
		
		double[] v = getCoeffData();
	
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			
			if(m_operationTypePanel.isWholeImage()) {
				
				m_parent.resetInstrument();
				
				BufferedImage im = PaUtils.deepCopy((BufferedImage) m_parent.getSourceViewImage());
				
				if(im == null) {
					
					 writeLog("Can't find current image to use color balance instrument" , null, true, false, true);
					 
					 return;
				}
				 
				BufferedImage resultImage = null;
	
				PaAlgorithms al = new PaAlgorithms();
				
				resultImage =  al.changeColorBalance2(im,v,m_lightHTCondition);
					
				m_parent.setResultView(resultImage, getGuiStrs("colorBalanceInstrumentName"));
				
				m_mainButton.setSelected(false);
				
				return;
			}
			if ( m_mainButton.isSelected() ) {
				
				writeLog("Instruments window: color balance instrument operation for subimage started.", null, true,
						false, false );
				
				m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.ColorBalance,this);
				
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
			
			writeLog("Not enough memory: can't get result image for color balance instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			writeLog("Instruments window: color balance instrument operation finished.", null,
					true, false, false );
		}
		 		
	}
	/**
	 * 
	 * @return the coefficients of amplification which were chosen by user for three color components [0],[1],[2] - red, green, blue 
	 */
	public double[] getCoeffData() {
		
		double[] v = { 0.0, 0.0, 0.0};
		
		//calculates the coefficient for changing level of colors
		for(int i =0; i < 3; ++i) {
			
			if(m_sliderValue[i] > 0) { //color increase (the image becomes more 'cold') 
				
				v[i] = 1.0 + SC_COEFF*m_sliderValue[i]/(10.0*MAX_SL);
				
			}
			else {// color decrease (the image becomes more 'warm') 
				
				v[i] = 1.0 - SC_COEFF*m_sliderValue[i]/(10.0*MAX_SL);
				
				v[i] *= -1;
			}
		}
		return v;
	
	}
	
	/**
	 * 
	 * @return true if the check box 'light hues' was chosen by user
	 */
	public boolean isLightHues() {
		
		return m_lightHTCondition;
		
	}
	
	
	/**
	 * Sets the data from the queue storage into the button parameters; sets all appropriative controls according to saved parameters in the data 
	 */
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
				
		m_operationTypePanel.setWholeImageSelected(d.isWholeImage);
		
		m_lightHTCondition = d.lightHTCondition;
		
		for(int i = 0; i < 3; ++i) {
			
			m_sliderValue[i] = d.sliderValue[i];
			
		}
		
		if(m_menuPanel != null) {
			
			 SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			 p.m_sliderBlue.setValue(m_sliderValue[2]);
			 
			 p.m_sliderRed.setValue(m_sliderValue[0]);
			 
			 p.m_sliderGreen.setValue(m_sliderValue[1]);
			 
			 if(m_lightHTCondition) {
				 
				 p.m_lightHT.setSelected(true);
				 
			 }
			 else {
				 
				 p.m_mediumHT.setSelected(true);
			 }
			
		}
	}
	
	/**
	 * Saves the data from the controls to the Data object storage, usually it's further used to put this Data object into the queue  
	 */
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.isWholeImage = m_operationTypePanel.isWholeImage();
		
		d.lightHTCondition = m_lightHTCondition;
		
		for(int i = 0; i < 3; ++i) {
			
			d.sliderValue[i] =  m_sliderValue[i];
			
		}
				
		return d;
	}
	
	/**
	 * Class for saving/loading parameters from the special storage  queue  
	 * Saved parameters reflects the state of all controls  
	 */
	private class Data {
		
		
		public int[] sliderValue = new int[3];
		
		public boolean isWholeImage;
		
		public boolean lightHTCondition;
			
	}

}



	
