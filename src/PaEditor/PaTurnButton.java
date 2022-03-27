
package PaEditor;


import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMenusStrs;
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import PaAlgorithms.PaAlgoTransform;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;

/**
 * @author avd
 * <p>This class determines the complex button for turn left operation; 
 * the special parameters panel creates here</p>
 */
public class PaTurnButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	
	private Font m_font =PaUtils.get().getBaseFont();
	
	float m_angle = 90.0f;
	
	boolean m_right = false;
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaTurnButton(PaInstrumentsWindow parent, Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_TURN, parent,new ImageIcon(PaUtils.get().getIconsPath() + "paturnleft.png"), 
				d, false, hash);
		
		//false - not toggled button
		setToolTips();

	}
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return new SpecialPanel();
		
	}
	/**
	 * Sets tooltips
	 */
	private void setToolTips() {
		
		m_mainButton.setToolTipText(getMenusStrs("instTurnLeftMenuToolTip"));	
	}

	/**
	 * <p>Starts turn left/right instrument</p>
	 */
	protected void startInstrumentImpl() {
		
	
		writeLog("Instruments window: rotation instrument operation started.", null, true, false, false );	

		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			
			 m_parent.resetInstrument();
			
			 BufferedImage im = (BufferedImage) m_parent.getSourceViewImage();
			 
			 if(im == null) {
				 
				 writeLog("Can't find current image to use rotation instrument" , null, true, false, true);
				 
				 return;
			 }
		
			 BufferedImage resultImage = PaAlgoTransform.getRotatedImage(im,getAngle(),isRightTurn());
						 
			 m_parent.setResultView(resultImage, getGuiStrs("rotationInstrumentName"));
			
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window:rotation instrument operation finished.", null,
					true, false, false );
		}
		
		
	}	
	
	public float getAngle() { return 	m_angle; }
	
	public boolean isRightTurn() { return 	 m_right; }
	
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

		JRadioButton m_leftRadio;
		
		JRadioButton m_rightRadio;

		/**
		 * Combo for list of standard proportions
		 */
		JComboBox<String> m_combo;
		

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
	
			ButtonGroup m_butGroup = new ButtonGroup();
			
			m_leftRadio = new JRadioButton(getGuiStrs("turnLeftRadioButtonCaption"));
			
			JPanel panelRadio = PaGuiTools.createHorizontalPanel();
			
			panelRadio.add(m_leftRadio);
			
			m_rightRadio = new JRadioButton(getGuiStrs("turnRightRadioButtonCaption"));
			
			panelRadio.add(m_rightRadio);
			
			panelRadio.add(Box.createHorizontalGlue());
			
			m_butGroup.add(m_leftRadio);
			
			m_butGroup.add(m_rightRadio);
			
			String[] list = { 
					"90",//0
					"180",//1
					"270"//2
					};

			JLabel lC = new JLabel(getGuiStrs("angleComboTurnInsLabel") + " ");
			
			m_combo = new JComboBox<String>(list);
			
			JPanel panelCombo = PaGuiTools.createHorizontalPanel();
			
			panelCombo.add(lC);
			
			panelCombo.add(m_combo);
			
			JPanel panelParam = PaGuiTools.createVerticalPanel();
			
			panelParam.add(panelRadio);
			
			panelParam.add(panelCombo);
			
			
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("rotationInfoInsGroupCaption"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelParam.setBorder(title_0);
			
			add(panelParam);
			
			RadioListener listener = new RadioListener();
			
			m_rightRadio.addChangeListener(listener);
			
			m_leftRadio.addChangeListener(listener);
			
			m_combo.addActionListener(new CustomListener());
			
			m_leftRadio.setSelected(true);
			
			PaUtils.setComponentsFont(this, m_font);
		
		}
		/**
		 * Sets all spiner's data; used in spinners listener 
		 */
		private void setData() {
			
			m_angle = getAngle();
			
			m_right = m_rightRadio.isSelected();
			
		}
		
		/**
	     * 
	     * @return the angle chosen by user 
	     */
	    private float getAngle() {
	    	
	    	float k = 1.0f;
	    	
	    	switch(m_combo.getSelectedIndex()) {
	    	
		    	case 0: { //9X13
		    		
		    		k = 90.0f;
		    		
		    		break;
		    	}
		    	case 1: { //10x15
		    		
		    		k = 180.0f;
		    		
		    		break;
		    	}
		    	case 2: { //13x18
		    		
		    		k = 270.0f;
		    		
		    		break;
		    	}
		    	
		    	default:{}
	    	}
	    	return k;
	    }
	    
		
	   class CustomListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == m_combo) {
					
					setData();
		    	}
				
			}
    
	    } 
		
		
		/**
		 * 
		 * @author avd
		 * <p>Listener class to listen all spinners; also listens the focus event because
		 * during the manual change of the value in spinners the event stateChange isnt't invoked </p>
		 *
		 */
		class RadioListener implements ChangeListener {
			
		    public void stateChanged(ChangeEvent e) {
		    	
		    	setData();
		    }
		}

		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_rightRadio.setToolTipText(getGuiStrs("rightTurnRadioTooltip"));
			
			m_leftRadio.setToolTipText(getGuiStrs("leftTurnpRadioTooltip"));
			
			m_combo.setToolTipText(getGuiStrs("comboTurnInsTooltip"));
		}
		
	}
	

}