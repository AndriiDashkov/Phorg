
package paeditor;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paalgorithms.PaAlgorithms;
import paenums.PaInstrumentTypeEnum;
import paglobal.PaGuiTools;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for HSI balance change operation; 
 * the special parameters panel consists the sliders which determine the range of changing of red, green, blue color:
 * reduction of blue value is a warm direction and opposite is a cold direction </p>
 */
public class  PaHSIbutton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	/**
	 * Determines the type of operation - true - 'save as'; false - 'save'
	 */

	private final int MAX_SL = 1000;
	
	private final double SC_COEFF = 4.0; // this coeff means that the blue color will change in the range of 20 %

	public PaOperationTypePanel m_operationTypePanel;
	
	int[] m_sliderValue = new int[3];

	
	private Font m_font = PaUtils.get().getBaseFont();
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public  PaHSIbutton(PaInstrumentsWindow parent, Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_HSI, parent,new ImageIcon(PaUtils.get().getIconsPath() + "pahsibalance.png"),
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
		m_mainButton.setToolTipText(getGuiStrs("hsiBalanceButtonToolTip"));	
	}
	

	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Special parameter panel class with all control components.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;

		JSlider m_sliderH = null;
		
		JSlider m_sliderS = null;
		
		JSlider m_sliderI = null;
		
		public SpecialPanel() {
			
			super();
			
			m_operationTypePanel  = new PaOperationTypePanel();
		
			createGui();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
			
			ButtonsListener l = new ButtonsListener();
			
			m_sliderH.addChangeListener(l);
			
			m_sliderS.addChangeListener(l);
			
			m_sliderI.addChangeListener(l);
			
		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setFont(m_font);
			
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			m_sliderI = new JSlider(JSlider.HORIZONTAL,-MAX_SL, MAX_SL, 0);
			
			m_sliderH = new JSlider(JSlider.HORIZONTAL,-MAX_SL, MAX_SL, 0);
			
			m_sliderS = new JSlider(JSlider.HORIZONTAL,-MAX_SL, MAX_SL, 0);

			JPanel panelMain =PaGuiTools.createHorizontalPanel();
			
			JPanel panelLeft =PaGuiTools.createVerticalPanel();
			
			JPanel panelRight =PaGuiTools.createVerticalPanel();
			
			JPanel panelSliderBlue =PaGuiTools.createHorizontalPanel();
			
			JPanel panelSliderRed =PaGuiTools.createHorizontalPanel();
			
			JPanel panelSliderGreen =PaGuiTools.createHorizontalPanel();
				
			JLabel iLabel = new JLabel(" " +getGuiStrs("intensityCaptionColorBalalnce")+ " : ");
			
			iLabel.setFont(m_font);
			
			panelSliderBlue.add(iLabel);
			
			panelSliderBlue.add(m_sliderI);

			JLabel hLabel = new JLabel(" " +getGuiStrs("halftoneCaptionColorBalalnce")+ " : ");
			
			hLabel.setFont(m_font);
			
			panelSliderRed.add(hLabel);
			
			panelSliderRed.add(m_sliderH);

			JLabel sLabel = new JLabel(" " +getGuiStrs("saturCaptionColorBalalnce")+ " : ");
			
			sLabel.setFont(m_font);
			
			panelSliderGreen.add(sLabel);
			
			panelSliderGreen.add(m_sliderS);

			 PaGuiTools.makeSameSize(new JComponent[] {hLabel,sLabel ,iLabel});

			 panelRight.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			 
			 panelRight.add(panelSliderRed);
			 
			 panelRight.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
			 
			 panelRight.add(panelSliderGreen);
			 
			 panelRight.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
			 
			 panelRight.add(panelSliderBlue);
			 
			 panelRight.add(Box.createVerticalGlue());
			 
			 panelLeft.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			 
			 panelLeft.add(m_operationTypePanel);
			 
			 panelLeft.add(Box.createVerticalGlue());
			 
			 panelMain.add(panelLeft);
			 
			 panelMain.add(panelRight);
			 
			 PaQueuePanel pV = new PaQueuePanel(PaHSIbutton.this, m_queue);
			 
			 panelMain.add(pV);
			 
			add(panelMain);
	
		}
		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_sliderH.setToolTipText(getGuiStrs("sliderHalfColorBalalnceTooltip"));
			
			m_sliderS.setToolTipText(getGuiStrs("sliderSatColorBalalnceTooltip"));
			
			m_sliderI.setToolTipText(getGuiStrs("sliderIntColorBalalnceTooltip"));
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen the slider</p>
		 *
		 */
		class ButtonsListener implements ChangeListener {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				 m_sliderValue[2] = m_sliderI.getValue();
				 
				 m_sliderValue[0] = m_sliderH.getValue();
				 
				 m_sliderValue[1] = m_sliderS.getValue();
	
			}
		}
	}
	
	/**
	 * <p>Starts color balance instrument. </p>
	 */
	protected void startInstrumentImpl() {

		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		writeLog("Instruments window: hsi balance instrument  operation started.", null,
				true, false, false );		
		
		double[] v = getCoeffData();
	
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			
			if(m_operationTypePanel.isWholeImage()) {
				
				m_parent.resetInstrument();
				
				BufferedImage im = PaUtils.deepCopy((BufferedImage) m_parent.getSourceViewImage());
				
				if(im == null) {
					
					 writeLog("Can't find current image to use  hsi balance instrument" , null, true, false, true);
					 
					 return;
				}
				 
				BufferedImage resultImage = null;
	
				PaAlgorithms al = new PaAlgorithms();
				
				resultImage =  al.changeHSIBalance(im,v);
					
				m_parent.setResultView(resultImage, getGuiStrs("hsiBalanceInstrumentName"));
				
				m_mainButton.setSelected(false);
				
				return;
			}
			if ( m_mainButton.isSelected() ) {
				
				writeLog("Instruments window: HSI balance instrument operation for subimage started.", null, true,
						false, false );
				
				m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.HSIBalance,this);
				
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
			
			writeLog("Not enough memory: can't get result image for hsi balance instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			writeLog("Instruments window: hsi balance instrument operation finished.", null,
					true, false, false );
		}
		 		
	}
	/**
	 * 
	 * @return the coefficients of amplification which were chosen by user for three HSI components [0],[1],[2] - red, green, blue 
	 */
	public double[] getCoeffData() {
		
		double[] v = { 0.0, 0.0, 0.0};
		
		//calculates the coefficient for changing level of HSI components
		for(int i =0; i < 3; ++i) {
			
			if(m_sliderValue[i] > 0) { //increase 
				
				v[i] = 1.0 + SC_COEFF*m_sliderValue[i]/(10.0*MAX_SL);
			}
			else {// decrease 
				
				v[i] = 1.0 - SC_COEFF*m_sliderValue[i]/(10.0*MAX_SL);
				
				v[i] *= -1;
			}
		}
		return v;
	
	}
	
	/**
	 * Sets the data from the queue storage into the button parameters; sets all appropriative controls according to saved parameters in the data 
	 */
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
				
		m_operationTypePanel.setWholeImageSelected(d.isWholeImage);
		
		
		for(int i = 0; i < 3; ++i) {
			
			m_sliderValue[i] = d.sliderValue[i];
			
		}
		
		if(m_menuPanel != null) {
			
			 SpecialPanel p = (SpecialPanel) m_menuPanel;
			 			
			 p.m_sliderH.setValue(m_sliderValue[0]);
			 
			 p. m_sliderS.setValue(m_sliderValue[1]);
			 
			 p.m_sliderI.setValue(m_sliderValue[2]);
			 	 
			
		}
	}
	
	/**
	 * Saves the data from the controls to the Data object storage, usually it's further used to put this Data object into the queue  
	 */
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.isWholeImage = m_operationTypePanel.isWholeImage();
		
		
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

			
	}

}

