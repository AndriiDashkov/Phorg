
package PaEditor;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import PaLong.PaBlurHiFOperation;
import PaLong.PaBlurOperation;

/**
 * @author Andrey Dashkov
 * <p>This class determines the complex button for blur change operation; 
 * the special parameters panel consists the slider which determines the range of changing of blur
 * value </p>
 */
public class PaBlurButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	public final int MAX_SL = 99;
	
	public final int MIN_SL = 3;
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	public PaOperationTypePanel m_operationTypePanel;
	
	private boolean m_averageFlag = true;
	
	int m_sliderValue = (MIN_SL + MAX_SL)/2;
	
	private boolean m_hiFrRadioFlag = false;
	
	private boolean m_theWholeImage = true;
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaBlurButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_BLUR, parent, new ImageIcon(PaUtils.get().getIconsPath() + "pablur.png"),
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
		
		m_mainButton.setToolTipText(getGuiStrs("blurTotalButtonToolTip"));	
	}
	

	/**
	 * 
	 * @author avd
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through m_saveAsFlag member of the button class.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;

		JSlider m_slider;
		
		JLabel m_infoLabel;
		
		JRadioButton m_averageRadio = new JRadioButton(getGuiStrs("averageBlurFilterRadioCaption"));
		
		JRadioButton m_hiFrRadio = new JRadioButton(getGuiStrs("hiFrBlurFilterRadioCaption"));
		
		ButtonGroup m_butGroupType = new ButtonGroup();
				
		public SpecialPanel() {
			
			super();
			
			m_operationTypePanel  = new PaOperationTypePanel();
		
			createGui();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
			
			m_averageRadio.setSelected(true);
			
			ButtonsListener l = new ButtonsListener();
			
			m_slider.addChangeListener(l);
			
			m_averageRadio.addChangeListener(l);
			
			m_hiFrRadio.addChangeListener(l);


		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			
			JPanel panelButtons = new JPanel();
			
			panelButtons.setLayout(new GridLayout(2, 1));
			
			panelButtons.add(m_averageRadio);
			
			panelButtons.add(m_hiFrRadio);
			
			m_averageRadio.setFont(m_font);
			
			m_hiFrRadio.setFont(m_font);
	
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("typeOfAlgorithm"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelButtons.setBorder(title_0);
			
			m_butGroupType.add(m_averageRadio);
			
			m_butGroupType.add(m_hiFrRadio);

				
			m_slider = new JSlider(JSlider.HORIZONTAL,MIN_SL, MAX_SL,(MIN_SL+MAX_SL)/2);

			JPanel panelMain = PaGuiTools.createHorizontalPanel();
			
			JPanel panelRight = PaGuiTools.createVerticalPanel();

			JPanel panelLabel = PaGuiTools.createHorizontalPanel();
			
			JPanel panelSlider = PaGuiTools.createHorizontalPanel();
			
			JPanel panelUnderSlider = PaGuiTools.createHorizontalPanel();
		    
			panelLabel.add(Box.createHorizontalGlue());
			
			m_infoLabel = new JLabel(getGuiStrs("sliderBlurName") + " : " +(MIN_SL+MAX_SL)/2);
			
			panelLabel.add(m_infoLabel);
			
			panelLabel.add(Box.createHorizontalGlue());
			
			m_infoLabel.setFont(m_font); 
		
			panelSlider.add(m_slider);
			
			JLabel decLabel = new JLabel("  " + getGuiStrs("decreaseBlur"));
			
			JLabel incLabel = new JLabel(getGuiStrs("increaseBlur") + "  ");
			
			incLabel.setFont(m_font);
			
			decLabel.setFont(m_font);
			
			panelUnderSlider.add(decLabel);
			
			panelUnderSlider.add(Box.createHorizontalGlue());
			
			panelUnderSlider.add(incLabel);
	
			panelRight.add(panelLabel);
			
			panelRight.add(panelSlider);
			
			panelRight.add(panelUnderSlider);
			
			panelRight.add(m_operationTypePanel);
		
			panelMain.add(panelRight);
			
			panelMain.add(panelButtons);
			
			PaQueuePanel pV = new PaQueuePanel(PaBlurButton.this, m_queue);
			
			panelMain.add(pV);
			
			add(panelMain);

		}
				
		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_slider.setToolTipText(getGuiStrs("sliderBlurTotalTooltip"));
			
			m_averageRadio.setToolTipText(getGuiStrs("averageRadioBlurTotalTooltip"));
			
			m_hiFrRadio.setToolTipText(getGuiStrs("hiFrRadioBlurTotalTooltip"));
			
		}
		
		/**
		 * 
		 * @author avd
		 * <p>Listener class to listen the slider</p>
		 *
		 */
		class ButtonsListener implements ChangeListener {
			
	
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(e.getSource() == m_slider) {
				
					m_sliderValue = m_slider.getValue();
					
					if(m_slider.getValue()%2 == 0) {
						
						m_sliderValue +=1;
					}
					
					 m_infoLabel.setText(getGuiStrs("sliderBlurTotalName") + " " +  m_sliderValue);
				}
				
				if(e.getSource() == m_averageRadio || m_hiFrRadio == e.getSource()) {
					
					 m_averageFlag = m_averageRadio.isSelected();
					 
					 m_hiFrRadioFlag  =  m_hiFrRadio.isSelected();
					
				}
			}
		}
	}
	
	public boolean isAvarageFilterChosen() {  return  m_averageFlag; }
	
	public int getLevel() {  return (int)m_sliderValue;}
	//public boolean isBoundary() {  return  isBoundary ; }
	
	/**
	 * <p>Starts color balance instrument. We don't need here any child of PaInstrument class because 
	 * the  instrument does not operate in work area, only in preview area</p>
	 */
	
	protected void startInstrumentImpl() {
		
		writeLog("Instruments window: blur instrument  operation started.", null,
				true, false, false );
		
		PaInstrument.isAnyInstrumentWasUsed = true; 
		
		if(m_operationTypePanel.isWholeImage()) {
			
			m_parent.resetInstrument();
			
			BufferedImage im = PaUtils.deepCopy((BufferedImage)m_parent.getSourceViewImage());
			
			 if(im == null) {
				 
				 writeLog("Can't find current image to use blur instrument" , null, true, false, true);
				 
				 return;
			 }
			 
			 if(PaUtils.askAboutLongOperation(im.getWidth(),im.getHeight(), m_parent)
				 == JOptionPane.NO_OPTION) {
				 
					m_parent.getWorkPanel().resetInstrument();
					
					m_mainButton.setSelected(false);
					
				    return;
			 }
			
			 m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 
			 PaUtils.get().setCursor(PaUtils.get().getCurrentCursor(),Cursor.WAIT_CURSOR);
			 
			 //start long operation because blur can take minutes
			 ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,
						getMessagesStrs("blurLongOperationCaption")+" : " +
						getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
						+ "x"+im.getHeight(),
						getMessagesStrs("startBlurOperationCaption"), 0, 100);
				
			 progressMonitor.setMillisToDecideToPopup(0);
			 
			 progressMonitor.setMillisToPopup(0);
				
			 if(m_averageFlag) {
				 
				 PaBlurOperation ts = new PaBlurOperation(progressMonitor,
						 (int)m_sliderValue, im, null, null, false , m_parent, false);
					
				 ts.execute();
			 }
			 else {
				 
				 PaBlurHiFOperation ts = new PaBlurHiFOperation(progressMonitor,
						 (int)m_sliderValue, MAX_SL, MIN_SL, im, null, null, null, m_parent);
					
				 ts.execute();
			 }

			 m_mainButton.setSelected(false);
			 return;
		}
		
		if ( m_mainButton.isSelected() ) {
			
			writeLog("Instruments window: blur instrument operation started.", null, true,
					false, false );
			
			m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.Blur, this);
			
		}
		else { 
			m_parent.getWorkPanel().resetInstrument();
		}	
	
	}
	
	
	
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;		
		
		m_averageFlag = d.averageFlag;
		
		m_sliderValue = d.sliderValue;
		
		m_hiFrRadioFlag = d.hiFrRadioFlag;
		
		 m_theWholeImage = d.theWholeImage;
		 
		 m_operationTypePanel.setWholeImageSelected(m_theWholeImage);
		
		if(m_menuPanel != null) {
			
			SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			p.m_averageRadio.setSelected(m_averageFlag);
			
			p.m_slider.setValue(Integer.valueOf(m_sliderValue));
			
			p.m_hiFrRadio.setSelected(m_hiFrRadioFlag);
			
		}
	}
	
	@Override
	public Object getData() {
		
		Data d = new Data();
				
		d.averageFlag = m_averageFlag ;
		
		d.sliderValue = m_sliderValue;
		
		d.hiFrRadioFlag = m_hiFrRadioFlag;
		
		d.theWholeImage = m_theWholeImage;
		
		return d;
	}
	
	private class Data {
			
		boolean averageFlag;
		
		int sliderValue;
		
		boolean hiFrRadioFlag;
		
		boolean theWholeImage;
		
	}

}
