
package paeditor;


import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getGuiStrs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paalgorithms.PaAlgorithms;
import paglobal.PaGuiTools;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for the gamma correction operation; the special parameters panel is created here</p>
 */
public class PaGammaButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	
	private double m_gamma = 0.75;
	
	/**
	 * @param parent - main window link through which instruments will work with image
	 * @param d - size of button panel
	 */
	public PaGammaButton(PaInstrumentsWindow parent,Dimension size, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_FFT, parent, 
				new ImageIcon(PaUtils.get().getIconsPath() + "pagammabutton.png"), size, false, hash);
		
		//false - not toggle button
		m_mainButton.setToolTipText(getGuiStrs("gammaButtonMainToolTip"));
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return new SpecialPanel();
		
	}
	/**
	 * 
	 * @return the gamma koeff
	 */
	public double  getGamma() {	
		return 	m_gamma;
	}


	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Special parameter panel class with all components; the link with button is perfomed through m_filterType and 
	 * m_postProcType members of PaSharpButton.
	 * Special parameters frame appears next to the instrument button while use the small menu button on the right side</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		JSpinner m_gammaSpinner;
		
		public SpecialPanel() {
			
			super();
		
			createGui();
			
			setListeners();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
		}
		/**
		 * Creates UI elements.
		 */
		private void createGui() {
			
			
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			SpinnerNumberModel spModel1 = new SpinnerNumberModel(0.75, 0.0,
					 3.0, 0.1);
			
			m_gammaSpinner = new JSpinner(spModel1);
			
			Dimension d = new Dimension(50,20);
			
			m_gammaSpinner.setMaximumSize(d);
			
			m_gammaSpinner.setMinimumSize(d);
			
			m_gammaSpinner.setPreferredSize(d);
			
	
			JLabel l1 = new JLabel(getGuiStrs("gammaCoefLabel")+" ");
			
			JLabel l2 = new JLabel(getGuiStrs("amplCoefLabel") + " ");
			
			JPanel panelGamma = PaGuiTools.createHorizontalPanel();
			
			panelGamma.add(l1);
			
			panelGamma.add(m_gammaSpinner);
			
			PaGuiTools.makeSameSize(new JComponent[] { l1, l2 } );
			
			JPanel mainPanel = PaGuiTools.createVerticalPanel();
			
			mainPanel.setBorder(BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,
					PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
			
			mainPanel.add(panelGamma);
			
			mainPanel.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));

			add(mainPanel);
		
		}
		
		private void setToolTips() {
			
			m_gammaSpinner.setToolTipText(getGuiStrs("gammaSpinnerTooltip"));
		
		}
		
		/**
		 * <p>Sets listener for the radion buttons</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
			
			m_gammaSpinner.addChangeListener(l);
				
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen all radio buttons</p>
		 *
		 */
		private class ButtonsListener implements ChangeListener {
			

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				 m_gamma = (double)m_gammaSpinner.getValue();
				 		
			}
		}
		
	}
	
	/**
	 * <p>Starts instrument; must be overloaded in the child class.</p>
	 */
	protected void startInstrumentImpl() {
		
		writeLog("Instruments window: power function  started.", null, true,
				false, false );

		PaInstrument.isAnyInstrumentWasUsed = true;
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
				m_parent.resetInstrument();
			
			 BufferedImage im = (BufferedImage) m_parent.getSourceViewImage();
			 
			 if(im == null) {
				 
				 writeLog("Can't find current image to use power function instrument" , null, true, false, true);
				 
				 return;
			 }
			 
			 BufferedImage resultImage = null;
			 
			 PaAlgorithms al = new PaAlgorithms();
			 
			 resultImage =  al.powerFunction(im,m_gamma);
			 
			 m_parent.setResultView(resultImage, getGuiStrs("powerFuncInstrumentName"));
			
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window:power function instrument operation finished.", null,
					true, false, false );
		}
			
	}

}