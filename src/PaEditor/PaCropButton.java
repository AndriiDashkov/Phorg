
package PaEditor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMenusStrs;



/**
 * @author avd
 * <p>This class determines the complex button for the crop operation; </p>
 */
public class PaCropButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	
	float m_prop = 9.0f/13.0f;
	
	boolean m_sizeActive = true;
	
	boolean m_propActive = true;
	
	boolean m_ratioControActive = true;
	
	
	private Font m_font =PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaCropButton(PaInstrumentsWindow parent, Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_CROP, parent,new ImageIcon(PaUtils.get().getIconsPath() + "pacropinst.png"), 
				d, true, hash);
		
		//true - toggled button
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
		m_mainButton.setToolTipText(getMenusStrs("cropToolTip"));	
	}

	/**
	 * <p>Starts contrast instrument</p>
	 */
	protected void startInstrumentImpl() {
		
		if ( m_mainButton.isSelected() ) {
			
			writeLog("Instruments window: crop instrument operation started.", null, true,
					false, false );
			m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.Crop,this);
			
		}
		else { 
			m_parent.getWorkPanel().resetInstrument();
		}	
	}	
	
	public float getProportion() { return m_prop; }
	
	public boolean isRatioControled() { return m_ratioControActive;  }
	
	
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

		JSpinner m_wSpinner = null;
		
		JSpinner m_hSpinner = null;
		
		JCheckBox m_ratioBox  = new JCheckBox(getGuiStrs("cutInstCheckBoxRatioCaption"));
		
		JRadioButton m_landscapeRadio  = new JRadioButton(getGuiStrs("landscapeCropRadioCaption"));
		
		JRadioButton m_portrRadio  = new JRadioButton(getGuiStrs("portrCropRadioCaption"));
		
		ButtonGroup m_butGroupType = new ButtonGroup();

		/**
		 * Combo for the list of standard proportions
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
			
			String[] list = { 
					
					getGuiStrs("x9x13PropListItem"),//0
					
					getGuiStrs("x10x15PropListItem"),//1
					
					getGuiStrs("x13x18PropListItem"),//2
					
					getGuiStrs("A4210x297PropListItem"),//3
					
					getGuiStrs("letterPropListItem"),//4
					
					getGuiStrs("customPropListItem")//5
			};

			JLabel lC = new JLabel(getGuiStrs("propComboCropInsLabel") + " ");
			
			m_combo = new JComboBox<String>(list);
			
			JPanel panelCombo = PaGuiTools.createHorizontalPanel();
			
			panelCombo.add(lC);
			
			panelCombo.add(m_combo);
			
			panelCombo.add(m_ratioBox);
			
			panelCombo.add(Box.createHorizontalGlue());
			
			SpinnerNumberModel spModel1 = new SpinnerNumberModel(9, 1, 100, 1);
			
			m_wSpinner = new JSpinner(spModel1);
			
			PaGuiTools.fixComponentSize(m_wSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_wSpinner, 60);

		
			SpinnerNumberModel spModel2 = new SpinnerNumberModel(13, 1, 100, 1);
			
			m_hSpinner = new JSpinner(spModel2);
			
			PaGuiTools.fixComponentSize(m_hSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_hSpinner , 60);
			
			JLabel lwSp = new JLabel(" " + getGuiStrs("cropInsWpropLabel") + " ");
			
			JLabel lhSp = new JLabel(" " + getGuiStrs("cropInsHpropLabel") + " ");
			
			JPanel panelX = PaGuiTools.createHorizontalPanel();
			
			panelX.add(lwSp);
			
			panelX.add(m_wSpinner);
			
			panelX.add(lhSp);
			
			panelX.add(m_hSpinner);
			
			panelX.add(Box.createHorizontalGlue());
			
			
			JPanel panelButtons = new JPanel();
			
			panelButtons.setLayout(new GridLayout(1, 2));
			
			panelButtons.add(m_portrRadio);
			
			panelButtons.add(m_landscapeRadio);
			
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("typeOfImageSizeCropOperation"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, m_font);
			
			panelButtons.setBorder(title_0);
			
			m_butGroupType.add(m_portrRadio);
			
			m_butGroupType.add(m_landscapeRadio);
			
			m_portrRadio.setSelected(true);
			
			JPanel panelParam = PaGuiTools.createVerticalPanel();
			
			panelParam.add(panelCombo);
			
			panelParam.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			panelParam.add(panelX);
			
			panelParam.add(panelButtons);
			
			Border title1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("infoCropInsGroupCaption"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelParam.setBorder(title1);
			
			add(panelParam);
			
			PaUtils.setComponentsFont(this, m_font);
			
			m_ratioBox.setSelected(true);
			
			m_wSpinner.setEnabled(false);
			
			m_hSpinner.setEnabled(false);
			
			
			CustomListener l = new CustomListener();
			
			m_combo.addActionListener(l);
			
			m_ratioBox.addActionListener(l);
			
			m_portrRadio.addActionListener(l);
			
			m_landscapeRadio.addActionListener(l);
			
			SpinnerListener sp_listener = new SpinnerListener();
			
			m_wSpinner.addChangeListener(sp_listener);
			
			m_hSpinner.addChangeListener(sp_listener);
		
		}
		/**
		 * Sets all spiner's data; used in spinners listener 
		 */
		private void setData() {
			
			m_prop = getProportion();
			
			m_ratioControActive = m_ratioBox.isSelected();
			
		}
		
		/**
	     * 
	     * @return the currently chosen by user  proportion width/height
	     */
	    private float getProportion() {
	    	
	    	float k = 1.0f;
	    	
	    	switch(m_combo.getSelectedIndex()) {
	    	
		    	case 0: { //9X13
		    		
		    		if(m_portrRadio.isSelected()) { k = 9.0f/13.0f;}
		    		
		    		else { k = 13.0f/9.0f;}
		    		
		    		break;
		    	}
		    	case 1: { //10x15
		    		
		    		if(m_portrRadio.isSelected()) { k = 10.0f/15.0f;}
		    		
		    		else { k = 15.0f/10.0f;}
		    		
		    		break;
		    	}
		    	case 2: { //13x18
		    		
		    		if(m_portrRadio.isSelected()) { k = 13.0f/18.0f;}
		    		
		    		else { k = 18.0f/13.0f;}
		    		
		    		break;
		    	}
		    	case 3: { //A4
		    		
		    		if(m_portrRadio.isSelected()) { k = 210.0f/297.0f;}
		    		
		    		else { k = 297.0f/210.0f;}
		    		
		    		break;
		    	}
		    	case 4: { //letter
		    		
		    		if(m_portrRadio.isSelected()) { k = 8.5f/11.0f;}
		    		
		    		else { k = 11.0f/8.5f;}
		    		
		    		break;
		    	}
		    	
		    	case 5: { //custom
		    		
		    		float w = ((Integer)m_wSpinner.getValue()).floatValue();
		    		
		    		float h = ((Integer)m_hSpinner.getValue()).floatValue();
		    		
		    		if(m_portrRadio.isSelected()) { k = (float) w/h; }
		    		
		    		else { k = h/w;}
		    		
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
					
					boolean flag = m_combo.getSelectedIndex() == 5;
					
					m_wSpinner.setEnabled(flag);
					
					m_hSpinner.setEnabled(flag);
					
					
		    	}
				
				if(e.getSource() == m_ratioBox) {
					
					setData();
		    	}
				if(e.getSource() == m_portrRadio || e.getSource() == m_landscapeRadio) {
					
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
		class SpinnerListener implements ChangeListener,FocusListener {
			
		    public void stateChanged(ChangeEvent e) {
		    	
		    	
		    	if(e.getSource() == m_wSpinner ) {
		    		
		    		setData();
		     		
		    		return;
		    	}
		    	if(e.getSource() == m_hSpinner ) {
		    		
		    		setData();
		     		
		    		return;
		    	}
		    	
		    }
		    
		    
			@Override
			public void focusGained(FocusEvent arg0) {}


			@Override
			public void focusLost(FocusEvent e) {

			}
		}
		
		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
	
			m_combo.setToolTipText(getGuiStrs("comboCropInsTooltip"));
			
			m_ratioBox.setToolTipText(getGuiStrs("checkBoxRatioCropInsTooltip"));
		}	
	}
}
