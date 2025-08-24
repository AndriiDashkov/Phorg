
package paeditor;


import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import paalgorithms.PaAlgorithms;
import paglobal.PaGuiTools;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for Fourier spectrum; the special parameters panel is
 *  created here</p>
 */
public class PaFftButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	public PaOperationTypePanel m_operationTypePanel;//must not set it to null!!!

	private boolean m_centered = true;
	
	private int m_color = 0;
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent link to window class where the instrument works
	 * @param size - size of this button panel
	 */
	public PaFftButton(PaInstrumentsWindow parent, Dimension size, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_FFT, parent,new ImageIcon(PaUtils.get().getIconsPath() +
				 "pafftspectrum.png"), size, false, hash);//false - not toggle button
		
		
		m_mainButton.setToolTipText(getGuiStrs("fftMainButtonToolTip"));
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 * <p>For linear operation there is no need for special parameters - so no special panel </p>
	 */
	protected JDialog createSpecialPanel() {
		
		return new SpecialPanel();
		
	}
	
	/**
	 * <p>Starts FFT spectrum instrument</p>
	 */
	protected void startInstrumentImpl() {
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		writeLog("Instruments window: spectrum instrument operation started.", null,
				true, false, false );
		
		try{	
			
			 BufferedImage im = (BufferedImage) m_parent.getResultViewImage();
			 
			 if(im == null) {
				 
				 writeLog("Can't find current image to use FFT spectrum instrument" , null, true, false, true);
				 
				 return;
			 }
			 
			 PaAlgorithms al = new PaAlgorithms();
			 
			 BufferedImage resultImage =  al.getSpectrum1(im, m_color, m_centered);
			 
			 String s = "";
			 
			 switch(m_color) {
			  
				  case 0: s = getGuiStrs("red"); break;
				  
				  case 1: s = getGuiStrs("green"); break;
				  
				  case 2: s = getGuiStrs("blue"); break;
				  
				  case 3: s = getGuiStrs("grScale"); break;
				  
				  default:
			  
			 }
			 PaInstrument.isAnyInstrumentWasUsed = true;
			 
			 m_parent.setResultView(resultImage, getGuiStrs("fourierSpectrum") +" - "+ s);	
		}
		catch(OutOfMemoryError e) {
			
			JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("outOfMemoryMessage"),
				    getMessagesStrs("messageCaption"),
				    JOptionPane.OK_OPTION);
			
			writeLog("Not enough memory: can't get result image for spectrum instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window: spectrum instrument operation finished.", null,
					true, false, false );
		}		
	}
	
	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Special parameter panel class with all components; the link with button is performed through m_color and 
	 * m_centered members of PaFftButton.
	 * Special parameters frame appears next to the instrument button while use the small menu button on the right side</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		JComboBox<String> m_combo = null;
		
		JRadioButton m_centeredRadio = new JRadioButton(getGuiStrs("centeredFFTSpecRadioCaption"));
		
		JRadioButton m_notCenteredRadio = new JRadioButton(getGuiStrs("notCenteredFFTSpecRadioCaption"));
					
		ButtonGroup m_butGroupCenter = new ButtonGroup(); 
		
		public SpecialPanel() {
			
			super();
			
			 m_operationTypePanel =  new PaOperationTypePanel();
		
			createGui();
			
			setListeners();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
			
			m_operationTypePanel.setControlsEnabled(false);
		}
		
		private void createGui() {
			
			
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			String[] list = { 
					
						getGuiStrs("redListItem"),//0
						
						getGuiStrs("greenListItem"),//1
						
						getGuiStrs("blueListItem"),//2
						
						getGuiStrs("grayScaleListItem")//3
					};
			
			
			JLabel lC = new JLabel(getGuiStrs("colorsToUseBinaryInstLabel") + " ");
			
			m_combo = new JComboBox<String>(list);
			
			m_combo.setFont(m_font);
			
			lC.setFont(m_font);
			
			JPanel panelCombo = PaGuiTools.createHorizontalPanel();
			
			panelCombo.add(lC);
			
			panelCombo.add(m_combo);
			
			JPanel panelButtons = PaGuiTools.createVerticalPanel();
			
			panelButtons.add(Box.createVerticalStrut(PaUtils.VERT_STRUT/2));
			
			panelButtons.add(panelCombo);
			 
			panelButtons.add(Box.createVerticalGlue());
			 
			panelButtons.add(m_operationTypePanel);
			 
			JPanel panelButtons1 = new JPanel();
			
			panelButtons1.setLayout(new GridLayout(2, 1));
			
			panelButtons1.add(m_centeredRadio);
			
			panelButtons1.add(m_notCenteredRadio);
			
			panelButtons1.setFont(m_font);
			
			m_notCenteredRadio.setFont(m_font);
			
			m_centeredRadio.setFont(m_font);


			Border title1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("typeOfCenterSpecFFT"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION);
			
			panelButtons1.setBorder(title1);
			
			m_butGroupCenter.add(m_centeredRadio);
			
			m_butGroupCenter.add(m_notCenteredRadio);
				 
			JPanel mainPanel = PaGuiTools.createHorizontalPanel();
			
			mainPanel.add(panelButtons);
			
			mainPanel.add(panelButtons1);
			
			PaQueuePanel pV = new PaQueuePanel(PaFftButton.this, m_queue);
			
			mainPanel.add(pV);
			
			add(mainPanel);
			
			m_centeredRadio.setSelected(true);			
		}
		private void setToolTips() {
			
			m_combo.setToolTipText(getGuiStrs("comboBinaryInstTooltip"));

			m_centeredRadio.setToolTipText(getGuiStrs("centeredFFTspecRadioTooltip"));
			
			m_notCenteredRadio.setToolTipText(getGuiStrs("notCentFFTspecRadioTooltip"));
		}
		
		/**
		 * <p>Sets listener for the radion buttons</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
			
			m_centeredRadio.addActionListener(l);
			
			m_notCenteredRadio.addActionListener(l);

			m_combo.addActionListener(l);
				
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen all radio buttons</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {
		
				if(e.getSource() ==  m_centeredRadio )  	{
					
					m_centered = m_centeredRadio.isSelected();
				}
				else 
				if(e.getSource() ==  m_notCenteredRadio )  {
					
					m_centered = !m_notCenteredRadio.isSelected();
				}
				else
				if(e.getSource() ==  m_combo) {
					
					 m_color = m_combo.getSelectedIndex();
					 
				}
				
			}
		}
		
	}
	
	
	
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
				
		m_operationTypePanel.setWholeImageSelected(d.isWholeImage);
		
	    m_color = d.color; 
		
	    m_centered = d.centered;
		
		if(m_menuPanel != null) {
			
			SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			p.m_combo.setSelectedIndex(m_color);
			
			if(m_centered) {
			
				p.m_centeredRadio.setSelected(true);
			}
			else {
				
				p.m_notCenteredRadio.setSelected(true);
				
			}
		}
	}
	
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.isWholeImage = m_operationTypePanel.isWholeImage();
		
		d.centered = m_centered; 
		
		d.color = m_color;
				
		return d;
	}
	
	private class Data {
		 
		public int  color;
		
		public boolean centered;
		
		public boolean isWholeImage;
	
	}
}