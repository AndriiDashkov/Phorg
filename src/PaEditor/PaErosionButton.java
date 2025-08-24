/**
 * 
 */
package paeditor;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMenusStrs;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paalgorithms.PaAlgorithms;
import paenums.PaInstrumentTypeEnum;
import paenums.PaMorphTypes;
import paglobal.PaGuiTools;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for erosion/dilatation operation.</p>
 */
public class PaErosionButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;


	public PaOperationTypePanel m_operationTypePanel;
	
	
	//type of morphological operation (erosion/dilataion)
	PaMorphTypes m_typeEr_Dl = PaMorphTypes.EROSION; 
	
	//for mophological operations the square mask is used
	private final int INIT_MASK_VALUE = 3;
	
	int m_maskSize  = INIT_MASK_VALUE;

	int getMaskSize() { return m_maskSize; };
	
	PaMorphTypes getType() { return m_typeEr_Dl; } 
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param icon
	 * @param d
	 */
	public PaErosionButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.EROSION_DILAT, parent,new ImageIcon(PaUtils.get().getIconsPath() + "paerosioninst.png"), d, true, hash);
		
		setToolTips();
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return new SpecialPanel();
		
	}


	private void setToolTips() {
		
		m_mainButton.setToolTipText(getMenusStrs("erosionInsToolTip"));	
	}

	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through members of the class
	 * Special parameters side panel appears next to the instrument button while using the small menu 
	 * button on the right side</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		JComboBox<String> m_combo;
		
		JSpinner m_Spinner;
				
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
			// m_menuPanel.setSize(50, 25);
			
			//setUndecorated(true);
			setFont(m_font);
			
			String[] list = { 
					getGuiStrs("erosionListItem"),//0
					getGuiStrs("dilatationListItem")//1
					};
			
			JLabel lC = new JLabel(getGuiStrs("erosionComboErosionInstLabel") + " ");
			
			m_combo = new JComboBox<String>(list);
			
			JPanel panelCombo = PaGuiTools.createHorizontalPanel();
			
			panelCombo.add(lC);
			
			panelCombo.add(m_combo);
			
			JLabel labelSpinner = new JLabel(getGuiStrs("spinnerErosionMaskBinaryInstLabel") + " ");
	
			JPanel panelSpin = PaGuiTools.createHorizontalPanel();
			
			//number of thresholds spinner
			SpinnerNumberModel spModel1 = new SpinnerNumberModel(INIT_MASK_VALUE, 3, 201, 2);
			
			m_Spinner = new JSpinner(spModel1);
			
			PaGuiTools.fixComponentSize(m_Spinner);
			
			PaGuiTools.setComponentFixedWidth(m_Spinner, 60);
			
			panelSpin.add(labelSpinner);
			
			panelSpin.add(m_Spinner);
			
			JPanel leftPanel = PaGuiTools.createVerticalPanel();
			
			JPanel pPanel = PaGuiTools.createHorizontalPanel();
			
			pPanel.add(panelCombo);
			
			pPanel.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
			
			pPanel.add(panelSpin);
			
			leftPanel.add(pPanel);
			
			leftPanel.add(m_operationTypePanel);
			
			leftPanel.add(Box.createVerticalGlue());
			
			JPanel mainPanel = PaGuiTools.createHorizontalPanel();
			
			mainPanel.setFont(m_font);
			
			mainPanel.add(leftPanel);
			
			PaQueuePanel pV = new PaQueuePanel(PaErosionButton.this, m_queue);
			
			mainPanel.add(pV);
			
			add(mainPanel);
			
			setListeners();
						
			PaUtils.setComponentsFont(this, m_font);
	
		}
		private void setToolTips() {
			
			m_combo.setToolTipText(getGuiStrs("comboErosionInstTooltip"));
			
			m_Spinner.setToolTipText(getGuiStrs("spinnerErosionInstTooltip"));
			

		}
		
		/**
		 * <p>Sets listener for all components</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
			
			SpinnerListener l1 = new SpinnerListener();
			
			m_combo.addActionListener(l);
			
			m_Spinner.addChangeListener(l1);

		
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen the combo box</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {	
				
				setData();			
			}
		}
		/**
		 * @author Andrii Dashkov
		 * <p>Listener class to listen all spinners</p>
		 */
		private class SpinnerListener implements ChangeListener {

			/* (non-Javadoc)
			 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
			 */
			@Override
			public void stateChanged(ChangeEvent e) {
				
				setData();	
				
			}
		}
		/*
		/**
		 * Sets the data of special panel; is called in listeners
		 * This members with data will be used by the instrument for this button
		 */
		private void setData() {

			m_typeEr_Dl = m_combo.getSelectedIndex() == 0 ? PaMorphTypes.EROSION : PaMorphTypes.DILATATION;
			
			m_maskSize = (int)m_Spinner.getValue();
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
		
		String message = "";
		
		try {
			
			if(m_operationTypePanel.isWholeImage()) {
				
				m_parent.resetInstrument();
				  
				PaAlgorithms al = new PaAlgorithms();
				
				BufferedImage newIm = null;
				
				switch(m_typeEr_Dl) {
				
					case EROSION : { 
						
						newIm = (BufferedImage) al.erosion((BufferedImage)m_parent.getSourceViewImage(), 
								this.getMaskSize());
						
						message = getMessagesStrs("cantPerformErosionMessage");
						
						break;
						
						}
					case DILATATION : { 
						
						newIm = (BufferedImage) al.dilatation((BufferedImage)m_parent.getSourceViewImage(), 
								this.getMaskSize());
						
						message = getMessagesStrs("cantPerformDilatationMessage");
						
						break;
						
						}
					default : {}
			
				}
						
				if(newIm == null) {
					JOptionPane.showMessageDialog(
						    null,
						    message,
						    getMessagesStrs("messageCaption"),
						    JOptionPane.OK_OPTION);
					return;
				}
							
				m_parent.setResultView(newIm, getGuiStrs("erosionInstrumentName"));
				 

				m_mainButton.setSelected(false);
				return;
			}
			//the case when the definite area selected by user (set instrument for further proccessing with the instrument)
			if ( m_mainButton.isSelected() ) {
				
				writeLog("Instruments window:  erosion/dilatation instrument operation started.", null, true,
						false, false );
				
				m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.EROSION_DIL, this);
				
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
			
			writeLog("Not enough memory: can't get result image for erosion/dilatation instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window:  erosion/dilatation instrument operation finished.", null,
					true, false, false );
		}	
	}

}