/**
 * 
 */
package PaEditor;

import java.awt.Dimension;
import javax.swing.ImageIcon;
import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import PaLong.PaBlurImageOperation;

/**
 * @author avd
 * <p>This class determines the complex button for contrast operation; 
 * the special parameters panel creates here</p>
 */
public class PaShiftButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	private double m_velInit = 0.1;
	
	private double m_xVel = m_velInit;
	
	private double m_yVel = m_velInit;
	
	private double m_expTime = 1;

	//private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaShiftButton(PaInstrumentsWindow parent, Dimension d,
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		super(PaEnumInstrumentNames.INST_SHIFT, parent,new ImageIcon(PaUtils.get().getIconsPath() + "pablur.png"),
				d, false, hash);
		
		//false - not toggled button
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
		m_mainButton.setToolTipText(getGuiStrs("blurButtonToolTip"));	
	}

	/**
	 * 
	 * @author avd
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through m_coeff member of PaContrastButton.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;

		JSpinner m_xSpinner;
		
		JSpinner m_ySpinner;
		
		JSpinner m_tSpinner;
		
		private final double VELOCITY_LIMIT = 1.0;
		
		private final double VELOCITY_INIT_VALUE = 0.05;
		
		private final double VELOCITY_STEP_VALUE = 0.005;
		

		public SpecialPanel() {
			
			super();
		
			createGui();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	
			SpinnerNumberModel spModel1 = new SpinnerNumberModel(VELOCITY_INIT_VALUE, -VELOCITY_LIMIT,
					VELOCITY_LIMIT, VELOCITY_STEP_VALUE);
			
			m_xSpinner = new JSpinner(spModel1);
			
			m_xSpinner.setEditor(new JSpinner.NumberEditor(m_xSpinner));
			
			PaGuiTools.fixComponentSize(m_xSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_xSpinner, 50);

		
			SpinnerNumberModel spModel2 = new SpinnerNumberModel(VELOCITY_INIT_VALUE, -VELOCITY_LIMIT,
					VELOCITY_LIMIT, VELOCITY_STEP_VALUE);
			
			m_ySpinner = new JSpinner(spModel2);
			
			PaGuiTools.fixComponentSize(m_ySpinner);
			
			PaGuiTools.setComponentFixedWidth(m_ySpinner , 50);
			
			JPanel panelParam = PaGuiTools.createVerticalPanel();
			
			JLabel lX = new JLabel(" " + getGuiStrs("xVelBlurLabel") + " ");
			
			JLabel lY = new JLabel(" " + getGuiStrs("yVelBlurLabel") + " ");
			
			JPanel panelX = PaGuiTools.createHorizontalPanel();
			
			panelX.add(lX);
			
			panelX.add(m_xSpinner);
			
			JPanel panelY = PaGuiTools.createHorizontalPanel();
			
			panelY.add(lY);
			
			panelY.add(m_ySpinner);
			
			PaGuiTools.makeSameSize(new JComponent[] { lX, lY} );
			
			panelParam.add(panelX);
			
			panelParam.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			panelParam.add(panelY);
			
			
			SpinnerNumberModel spModel3 = new SpinnerNumberModel(1.0, 0.0,
					 2.0, 0.005);
			
			m_tSpinner = new JSpinner(spModel3);
			
			m_tSpinner.setEditor(new JSpinner.NumberEditor(m_tSpinner));
			
			PaGuiTools.fixComponentSize(m_tSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_tSpinner , 50);
			
			JLabel lT = new JLabel(" " + getGuiStrs("timeBlurLabel") + " ");
			
			JPanel panelT = PaGuiTools.createHorizontalPanel();
			
			panelT.add(lT);
			
			panelT.add(m_tSpinner);
			
			JPanel panel = PaGuiTools.createHorizontalPanel();
			
			panel.add(panelParam);
			
			panel.add(panelT);
			
			add(panel);
			
			SpinnerListener l = new SpinnerListener();
			
			m_xSpinner.addChangeListener(l);
			
			m_ySpinner.addChangeListener(l);
			
			m_tSpinner.addChangeListener(l);
		}
		/**
		 * Sets all spiner's data; used in spinners listener 
		 */
		private void setData() {
			
			//fixing the bug with manual edit of JSpinner
			try {
				
				m_xSpinner.commitEdit();
				
				m_ySpinner.commitEdit();
				
				m_tSpinner.commitEdit();
				
			} catch (ParseException e) {
				
				writeLogOnly("Can't parse spinner data for blur instrument", e);
			}
			
			m_xVel = (double)(m_xSpinner.getValue());
			
			m_yVel = (double)(m_ySpinner.getValue());
			
			m_expTime = (double)(m_tSpinner.getValue());
		}
		
		/**
		 * 
		 * @author avd
		 * <p>Listener class to listen all spinners; also listens the focus event because
		 * during the manual change of the value in spinners the event stateChange isnt't invoked </p>
		 *
		 */
		class SpinnerListener implements ChangeListener,FocusListener {
		    public void stateChanged(ChangeEvent e) {
		    	
		    	setData();
		    }

			@Override
			public void focusGained(FocusEvent arg0) {
				
			}


			@Override
			public void focusLost(FocusEvent e) {
				
				setData();	
			}
		}

		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_xSpinner.setToolTipText(getGuiStrs("xVelBlurTooltip"));
			
			m_ySpinner.setToolTipText(getGuiStrs("yVelBlurTooltip"));
			
			m_tSpinner.setToolTipText(getGuiStrs("timeBlurTooltip"));
		}
		
	}
	/**
	 * Sets all data to inner members of the button
	 */
	protected void setAllData() {
		
		((SpecialPanel)m_menuPanel).setData();
	}
	/**
	 * <p>Starts blur removing instrument. We don't need here any child of PaInstrument class because 
	 * the blur instrument does not operate in work area, only in preview area</p>
	 */
	protected void startInstrumentImpl() {

		writeLog("Instruments window: blur instrument  operation started.", null,
				true, false, false );		
		
		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		try {
			 m_parent.resetInstrument();
			
			 BufferedImage im = (BufferedImage) m_parent.getSourceViewImage();
			 
			 if(im == null) {
				 
				 writeLog("Can't find current image to use contrast instrument" , null, true, false, true);
				 
				 return;
			 }
			 
			 if(im.getHeight() > 499 || im.getWidth() > 499) {
				 
					int n = JOptionPane.showConfirmDialog(
						    null,
						    getMessagesStrs("potentialLongOperationMessage"),
						    getMessagesStrs("messageAnswerCaption"),
						    JOptionPane.YES_NO_OPTION);
					
					if ( n == JOptionPane.NO_OPTION) {
						
						writeLog("User: Refuse from potentially long operation", null,
								true, false, false );
						
					    return;
			
					}
				 
			 }

			 PaUtils.get().setCursor(PaUtils.get().getCurrentCursor(),Cursor.WAIT_CURSOR);
			 
			 //start long operation because blur can take minutes
			 ProgressMonitor progressMonitor = new ProgressMonitor(m_parent,
						getMessagesStrs("blurLongOperationCaption")+" : " +
						getMessagesStrs("blurLongOperationSizeCaption")+ " " + im.getWidth() 
						+ "x"+im.getHeight(),
						getMessagesStrs("startBlurOperationCaption"), 0, 100);
				
			 progressMonitor.setMillisToDecideToPopup(0);
			 
			 progressMonitor.setMillisToPopup(0);
				
			 PaBlurImageOperation ts = new PaBlurImageOperation(progressMonitor,
						 m_xVel,m_yVel, m_expTime, im, m_parent);
				
			 ts.execute();
			 
	 
		}
		
		catch(OutOfMemoryError e) {
			
			JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("outOfMemoryMessage"),
				    getMessagesStrs("messageErrorCaption"),
				    JOptionPane.OK_OPTION);
			
			writeLog("Not enough memory: can't get result image for blur instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {

		}
		 		
	}	
	
}

