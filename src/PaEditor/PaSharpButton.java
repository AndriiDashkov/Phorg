/**
 * 
 */
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
import PaAlgorithms.PaAlgorithms;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import PaLong.PaSharpOperation;

/**
 * @author avd
 * <p>This class determines the complex button for sharpness increase operation.</p>
 */
public class PaSharpButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	/*
	 * depth of the sharpness algorithm
	 */
	int m_level = 70;

	/**
	 * minimal and max values of sharpness depth
	 */
	public final int  S_MIN = 5;
	
	public final int  S_MAX = 70;
	
	public PaOperationTypePanel m_operationTypePanel;
	/**
	 * 
	 * @author avd
	 * Enum for types of image filter operations
	 */
	public enum FILTER_TYPE {	
		
		SOBEL,// Sobel filter for sharpness
		
		LAPLAS, //Laplas filter for sharpness
		
		SOBEL_LAPLAS, //mix of the filters
		
		HIFR_GAUSS,
		
		CONTOUR
	}
	
	private FILTER_TYPE m_filterType = FILTER_TYPE.SOBEL_LAPLAS;
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param icon
	 * @param d
	 */
	public PaSharpButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_SHARP, parent,new ImageIcon(PaUtils.get().getIconsPath() + "pasharpinst.png"), d, true, hash);
		
		setToolTips();
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return new SpecialPanel();
		
	}
	/**
	 * 
	 * @return the type of filter that was chosen by user through radio buttons for sobel and laplas filters
	 */
	public FILTER_TYPE  getFilterType() {	
		
		return 	m_filterType;
	}

	private void setToolTips() {
		
		m_mainButton.setToolTipText(getMenusStrs("sharpInsToolTip"));	
	}

	/**
	 * 
	 * @author avd
	 * <p>Special parameter panel class with all components; the link with button is perfomed through m_filterType and 
	 * m_postProcType members of PaSharpButton.
	 * Special parameters frame appears next to the instrument button while use the small menu button on the right side</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		JRadioButton m_contourRadio = new JRadioButton(getGuiStrs("contourFilterRadioCaption"));
		
		JRadioButton m_hifrRadio = new JRadioButton(getGuiStrs("hiFrFilterRadioCaption"));
		
		JRadioButton m_sobelLaplasRadio = new JRadioButton(getGuiStrs("laplasAndSobelFilterRadioCaption"));
					
		ButtonGroup m_butGroupType = new ButtonGroup();
		
		JSlider m_slider;
		
		public SpecialPanel() {
			
			super();
			
			m_operationTypePanel  = new PaOperationTypePanel();
		
			createGui();
			
			setListeners();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
		}
		
		private void createGui() {
			
			
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

			setFont(m_font);
			
			JPanel panelButtons = PaGuiTools.createVerticalPanel();
			
			panelButtons.add(m_sobelLaplasRadio);
			
			panelButtons.add(m_hifrRadio);
			
			panelButtons.add(m_contourRadio);
			
			TitledBorder title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("typeOfAlgorithm"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, m_font);
			
			panelButtons.setBorder(title_0);
		
			
			m_butGroupType.add(m_contourRadio);
			
			m_butGroupType.add(m_hifrRadio);
			
			m_butGroupType.add(m_sobelLaplasRadio);
			
			JPanel panelLabel = PaGuiTools.createHorizontalPanel();
			
			JPanel panelSL =PaGuiTools.createVerticalPanel();
	
			JLabel slLabel = new JLabel(getGuiStrs("sliderSharpDepthName"));
			
			slLabel.setFont(m_font);
			
			panelLabel.add(Box.createHorizontalGlue());
			
			panelLabel.add(slLabel);
			
			panelLabel.add(Box.createHorizontalGlue());
			
			JPanel sliderPanel = PaGuiTools.createHorizontalPanel();
			
			m_slider = new JSlider(JSlider.HORIZONTAL,S_MIN, S_MAX, 25);
			 
			sliderPanel.add(m_slider);
			
			panelSL.add(panelLabel);
			
			panelSL.add(sliderPanel);
			
			panelSL.add(Box.createVerticalGlue());
			
			panelSL.add(m_operationTypePanel);
			
			JPanel mainPanel = PaGuiTools.createHorizontalPanel();
			
			mainPanel.add(panelSL);
			
			mainPanel.add(panelButtons);
			
			mainPanel.setFont(m_font);
			
			panelButtons.setFont(m_font);
			
			m_sobelLaplasRadio.setFont(m_font);
			
			m_contourRadio.setFont(m_font);
			
			m_hifrRadio.setFont(m_font);
			
			PaQueuePanel pV = new PaQueuePanel(PaSharpButton.this, m_queue);
			
			mainPanel.add(pV);
			
			add(mainPanel);
			
			m_hifrRadio.setSelected(true);
			
			m_filterType = FILTER_TYPE.HIFR_GAUSS;	
			
			PaUtils.setComponentsFont(this, m_font);
	
		}
		private void setToolTips() {
			
			m_contourRadio.setToolTipText(getGuiStrs("contourFilterRadioTooltip"));
			
			m_hifrRadio.setToolTipText(getGuiStrs("hifrSharpFilterRadioTooltip"));
			
			m_sobelLaplasRadio.setToolTipText(getGuiStrs("laplasSobelFilterRadioTooltip"));
			
			m_slider.setToolTipText(getGuiStrs("sliderSharpDepthTooltip"));

		}
		
		/**
		 * <p>Sets listener for the radion buttons</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
			
			m_contourRadio.addActionListener(l);
			
			m_hifrRadio.addActionListener(l);
			
			m_sobelLaplasRadio.addActionListener(l);
			
			m_slider.addChangeListener(new SliderListener());
				
		}
		
		/**
		 * 
		 * @author avd
		 * <p>Listener class to listen all radio buttons</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {	
				
			 	if(e.getSource() == m_contourRadio) {
			 		
			 		if(m_contourRadio.isSelected())  	{ m_filterType = FILTER_TYPE.CONTOUR; }
			 	}
				
			 	if(e.getSource() == m_hifrRadio) {
			 		
			 		if(m_hifrRadio.isSelected())  { m_filterType = FILTER_TYPE.HIFR_GAUSS; }
			 	}
			 	
			 	if(e.getSource() == m_sobelLaplasRadio) {
			 		
			 		if(m_sobelLaplasRadio.isSelected())  { m_filterType = FILTER_TYPE.SOBEL_LAPLAS; }
			 	}		
			}
		}
		
		/**
		 * 
		 * @author avd
		 * <p>Listener class to listen the slider</p>
		 *
		 */
		class SliderListener implements ChangeListener {
			
			@Override
			public void stateChanged(ChangeEvent e) {
					
				 	if(e.getSource() == m_slider) {
				 		
				 		m_level = m_slider.getValue();
				 		
				 	} 
			}
		}
		
	}
	
	/**
	 * <p>Starts sharpness instrument</p>
	 */
	protected void startInstrumentImpl() {
		
		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		writeLog("Instruments window: sharp increase  started.", null, true,
				false, false );
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			
			if(m_operationTypePanel.isWholeImage()) {
				
				m_parent.resetInstrument();
				
				 BufferedImage im = PaUtils.deepCopy((BufferedImage) m_parent.getSourceViewImage());
				 
				 if(im == null) {
					 
					 writeLog("Can't find current image to use sharp increase instrument" , null, true, false, true);
					 
					 return;
				 }
				 //ask user if the operation is too long
				 if(PaUtils.askAboutLongOperation(im.getWidth(),im.getHeight(), m_parent)
						 == JOptionPane.NO_OPTION) {
					 
							m_parent.getWorkPanel().resetInstrument();
							
							m_mainButton.setSelected(false);
							
						    return;
				 }
				 
				 switch(m_filterType){
				 	 
			
					 case CONTOUR: {
							ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,
									getMessagesStrs("sharpLongOperationCaption")+" : " +
									getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
									+ "x"+im.getHeight(),getMessagesStrs("startSharpOperationCaption"), 0, 100);
							
								progressMonitor.setMillisToDecideToPopup(0);
								
								progressMonitor.setMillisToPopup(0);
									
								PaSharpOperation ts = new PaSharpOperation(progressMonitor,
									m_level, S_MAX, S_MIN, im,null,null,true, m_parent);
										
								ts.execute();
								
								break;
						 
					 }
					
					 case SOBEL_LAPLAS : {
						 
						 BufferedImage resultImage = null;
						 
						 PaAlgorithms al = new PaAlgorithms();
						 
						 resultImage =  (BufferedImage)al.sharpIncrease(im,m_filterType,m_level);
		 
						 m_parent.setResultView(resultImage, getGuiStrs("sharpInstrumentName"));
						 
						 break;
					 }
					 default :
					 case HIFR_GAUSS : {
						 
						ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,
							getMessagesStrs("sharpLongOperationCaption")+" : " +
							getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
							+ "x"+im.getHeight(),getMessagesStrs("startSharpOperationCaption"), 0, 100);
					
						progressMonitor.setMillisToDecideToPopup(0);
						
						progressMonitor.setMillisToPopup(0);
							
						PaSharpOperation ts = new PaSharpOperation(progressMonitor,
							m_level, S_MAX, S_MIN, im,null,null,false , m_parent);
								
						ts.execute();
						
						break;
					 }
				 
				 };
				 
				m_mainButton.setSelected(false);
				
				return;
			}
			if ( m_mainButton.isSelected() ) {
				
				writeLog("Instruments window: sharpness instrument operation for subimage started.", null, true,
						false, false );
				
				m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.Sharp,this);
				
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
			
			writeLog("Not enough memory: can't get result image for sharpness instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window: sharpness instrument operation finished.", null,
					true, false, false );
		}	
	}
	/**
	 * 
	 * @return the level of sharp amplification
	 */
	public int getLevel() { return m_level; }
	
	
	
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
				
		m_operationTypePanel.setWholeImageSelected(d.isWholeImage);

		
		 m_level = d.level;
		 
		 m_filterType = d.filterType ;
				
		if(m_menuPanel != null) {
			
			SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			p.m_slider.setValue(m_level);
		
			
			 switch(m_filterType){
		 	 
				
				 case CONTOUR: {
					 	
					 p.m_contourRadio.setSelected(true);
					 
					 p.m_sobelLaplasRadio.setSelected(false);
					 
					 p.m_hifrRadio.setSelected(false);
							
					break;	 
				 }
				
				 case SOBEL_LAPLAS : {
					 
					 p.m_sobelLaplasRadio.setSelected(true);
					 
					 p.m_hifrRadio.setSelected(false);
					 
					 p.m_contourRadio.setSelected(false);
					 
					 break;
				 }
				 
				 default :
					 
				 case HIFR_GAUSS : {
					 
					 p.m_hifrRadio.setSelected(true);
					 
					 p.m_sobelLaplasRadio.setSelected(false);
					 
					 p.m_contourRadio.setSelected(false);
					
					break;
				 }
		 
			 };
			
		}
	}
	
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.isWholeImage = m_operationTypePanel.isWholeImage();
		
		d.level =  m_level;
		
		d.filterType = m_filterType;
				
		return d;
	}
	
	private class Data {
		
		public int level;
		
		FILTER_TYPE filterType;
		
		boolean isWholeImage;
	
	}

}
