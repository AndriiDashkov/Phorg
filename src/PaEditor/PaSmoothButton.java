
package paeditor;


import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ProgressMonitor;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paglobal.PaGuiTools;
import paglobal.PaUtils;
import palong.PaBlurOperation;

/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for blur change operation; 
 * the special parameters panel consists the slider which determines the range of changing of blur
 * value </p>
 */
public class PaSmoothButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	private final int MAX_SL = 99;
	
	private final int MIN_SL = 3;
	
	boolean isWholeImage = false;

	private Font m_font = PaUtils.get().getBaseFont();
	
	int m_sliderValue = 0;
	
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaSmoothButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_SMOOTH, parent,
				new ImageIcon(PaUtils.get().getIconsPath() + "pablur.png"), d, true, hash);
		
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
		
		m_mainButton.setToolTipText(getGuiStrs("smoothTotalButtonToolTip"));	
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

		JSlider m_slider;
		
		JLabel m_infoLabel;
		
		public JCheckBox m_wholeImage;
		
		JCheckBox m_boundaryBox;
				
		public SpecialPanel() {
			
			super();
		
			createGui();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
			
			ButtonsListener l = new ButtonsListener();
			
			m_slider.addChangeListener(l);
			
			m_wholeImage.addChangeListener(l);
			
			m_boundaryBox.addChangeListener(l);
		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			m_slider = new JSlider(JSlider.HORIZONTAL,MIN_SL, MAX_SL,3);

			JPanel panelMain =PaGuiTools.createVerticalPanel();
			
			JPanel panelBoxes =PaGuiTools.createHorizontalPanel();
			
			JPanel panelLabel =PaGuiTools.createHorizontalPanel();
			
			JPanel panelSlider =PaGuiTools.createHorizontalPanel();
			
			JPanel panelUnderSlider =PaGuiTools.createHorizontalPanel();
			
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("typeOfOperation"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelBoxes.setBorder(title_0);
			
			m_wholeImage = new JCheckBox(getGuiStrs("wholeImageBlurCheckBoxName"));
			
		    m_boundaryBox = new JCheckBox(getGuiStrs("boundaryBlurBlurName"));
	
		    panelBoxes.add(m_wholeImage);  panelBoxes.add( m_boundaryBox);

			panelLabel.add(Box.createHorizontalGlue());
			
			m_infoLabel = new JLabel(getGuiStrs("sliderBlurName") + " " +((MIN_SL +MAX_SL)/2));
			
			panelLabel.add(m_infoLabel);
			
			panelLabel.add(Box.createHorizontalGlue());
		
			panelSlider.add(m_slider);
			
			panelUnderSlider.add(new JLabel("  " + getGuiStrs("decreaseBlur")));
			
			panelUnderSlider.add(Box.createHorizontalGlue());
			
			panelUnderSlider.add(new JLabel(getGuiStrs("increaseBlur") + "  "));
	
			panelMain.add(panelBoxes);
			
			panelMain.add(panelLabel);
			
			panelMain.add(panelSlider);
			
			panelMain.add(panelUnderSlider);
			
			add(panelMain);
	
		}
				
		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_slider.setToolTipText(getGuiStrs("sliderBlurTotalTooltip"));
			
			m_wholeImage.setToolTipText(getGuiStrs("wholeImageCheckBoxBlurTotalTooltip"));
			
			m_boundaryBox.setToolTipText(getGuiStrs("boundaryCheckBoxBlurTotalTooltip"));
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen the slider</p>
		 *
		 */
		class ButtonsListener implements ChangeListener {
			

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				m_sliderValue = m_slider.getValue();
				
				if(m_slider.getValue()%2 == 0) {
					
					m_sliderValue +=1;
				}
				
				 
				 m_infoLabel.setText(getGuiStrs("sliderBlurTotalName") + " " +  m_sliderValue);
				 
				 isWholeImage = m_wholeImage.isSelected();
			}
		}
	}
	
	public int getLevel() {  return (int)m_sliderValue;}
	
	/**
	 * <p>Starts color balance instrument. We don't need here any child of PaInstrument class because 
	 * the  instrument does not operate in work area, only in preview area</p>
	 */
	
	protected void startInstrumentImpl() {
		
		writeLog("Instruments window: blur instrument  operation started.", null,
				true, false, false );
		
		PaInstrument.isAnyInstrumentWasUsed = true; 
		
		if(isWholeImage) {
			
			m_parent.resetInstrument();
			
			BufferedImage im = (BufferedImage) m_parent.getSourceViewImage();
			
			 if(im == null) {
				 
				 writeLog("Can't find current image to use smooth instrument" , null, true, false, true);
				 
				 return;
			 }
			
			 if(im.getHeight() > 799 || im.getWidth() > 799) {
				 
					int n = JOptionPane.showConfirmDialog(
						    null,getMessagesStrs("potentialLongOperationMessage"),
						    getMessagesStrs("messageAnswerCaption"),
						    JOptionPane.YES_NO_OPTION);
					
					if ( n == JOptionPane.NO_OPTION) {
						
						writeLog("User: Refuse from potentially long operation", null,
								true, false, false );
						
					    return;
			
					}
					
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
				
			 PaBlurOperation ts = new PaBlurOperation(progressMonitor,
					 (int)m_sliderValue, im, null, null, false, m_parent, false);
				
			 ts.execute();
			 
			 m_mainButton.setSelected(false);
			 
			 return;
		}
		
		if ( m_mainButton.isSelected() ) {
			
			writeLog("Instruments window: blur instrument operation started.", null, true,
					false, false );
	
		}
		else { 
			m_parent.getWorkPanel().resetInstrument();
		}	
	
	}
}
