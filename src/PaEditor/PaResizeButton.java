
package PaEditor;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.*;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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
 * <p>This class determines the complex button for contrast operation; 
 * the special parameters panel creates here</p>
 */
public class PaResizeButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	private int m_velInit = 100;
	
	private int m_width = m_velInit;
	
	private int m_height = m_velInit;
	
	private Font m_font =PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d
	 */
	public PaResizeButton(PaInstrumentsWindow parent, Dimension d,
				HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_RESIZE, parent,new ImageIcon(PaUtils.get().getIconsPath() + "paresizebut.png"), d, false, hash);
		
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
		
		m_mainButton.setToolTipText(getGuiStrs("resizeInstButtonToolTip"));	
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

		JSpinner m_wSpinner;
		
		JSpinner m_hSpinner;
		
		JRadioButton m_landscapeRadio  = new JRadioButton(getGuiStrs("landscapeCropRadioCaption"));
		
		JRadioButton m_portrRadio  = new JRadioButton(getGuiStrs("portrCropRadioCaption"));
		
		ButtonGroup m_butGroupType = new ButtonGroup();
		
		/**
		 * spinner for custom proportion
		 */
		JSpinner m_customSpinner;
		
		/**
		 * Combo for list of standard proportions
		 */
		JComboBox<String> m_combo;
		
		SpinnerListener m_listener;

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
	
			SpinnerNumberModel spModel1 = new SpinnerNumberModel(100, 10,
					 10000, 10);
			
			m_wSpinner = new JSpinner(spModel1);
			
			PaGuiTools.fixComponentSize(m_wSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_wSpinner, 60);

		
			SpinnerNumberModel spModel2 = new SpinnerNumberModel(100, 10,
					 10000, 10);
			
			m_hSpinner = new JSpinner(spModel2);
			
			PaGuiTools.fixComponentSize(m_hSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_hSpinner , 60);
			
			SpinnerNumberModel spModel3 = new SpinnerNumberModel(1.0, 0.1,
					 10.0, 0.1);
			
			m_customSpinner = new JSpinner(spModel3);
			
			JPanel panelParam = PaGuiTools.createVerticalPanel();
			
			JLabel lX = new JLabel(" " + getGuiStrs("widthResizeInsLabel") + " ");
			
			JLabel lY = new JLabel(" " + getGuiStrs("heightResizeInsLabel") + " ");
			
			JPanel panelX = PaGuiTools.createHorizontalPanel();
			
			panelX.add(lX);
			
			panelX.add(m_wSpinner);
			
			JPanel panelY = PaGuiTools.createHorizontalPanel();
			
			panelY.add(lY);
			
			panelY.add(m_hSpinner);
			
			PaGuiTools.makeSameSize(new JComponent[] { lX, lY} );
			
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("newSizeResizeInsGroupCaption"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelParam.setBorder(title_0);
			
			panelParam.add(panelX);
			
			panelParam.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			panelParam.add(panelY);
			
			panelParam.add(Box.createVerticalGlue());
			
			JPanel panelT = PaGuiTools.createVerticalPanel();
			
			Border title_1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("propResizeInsGroupCaption"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			panelT .setBorder(title_1);
			
			String[] list = { 
					getGuiStrs("notFixedPropListItem"),//0
					getGuiStrs("customPropListItem"), //1
					getGuiStrs("imagePropListItem"), //2
					getGuiStrs("x9x13PropListItem"),//3
					getGuiStrs("x10x15PropListItem"),//4
					getGuiStrs("x13x18PropListItem"),//5
					getGuiStrs("A4210x297PropListItem"),//6
					getGuiStrs("letterPropListItem")//7
					};

			JLabel lC = new JLabel(getGuiStrs("propComboResizeInsLabel") + " ");
			
			m_combo = new JComboBox<String>(list);
			
			JPanel panelCombo = PaGuiTools.createHorizontalPanel();
			
			panelCombo.add(lC);
			
			panelCombo.add(m_combo);
			
			panelCombo.add(Box.createHorizontalGlue());
			
			JLabel lCs = new JLabel(getGuiStrs("propCustomSpinResizeInsLabel")+ " ");
			
			JPanel panelC = PaGuiTools.createHorizontalPanel();
			
			panelC.add(lCs);
			
			panelC.add(m_customSpinner);
			
			JPanel panelButtons = PaGuiTools.createHorizontalPanel();

			panelButtons.add(m_landscapeRadio);
			
			panelButtons.add(m_portrRadio);
			
			panelButtons.add(Box.createHorizontalGlue());
	
			m_butGroupType.add(m_landscapeRadio);
			
			m_butGroupType.add(m_portrRadio);
			
			m_landscapeRadio.setSelected(true);
			
			panelT.add(panelCombo);
			
			panelT.add(panelButtons);
			
			panelT.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			panelT.add(panelC);
			
			PaGuiTools.makeSameSize(new JComponent[] { lC, lCs} );
		
			JPanel panel = PaGuiTools.createHorizontalPanel();
			
			panel.add(panelParam);
			
			panel.add(panelT);
			
			add(panel);
			
			m_listener = new SpinnerListener();
			
			m_wSpinner.addChangeListener(m_listener);
			
			m_landscapeRadio.addChangeListener(m_listener);
			
			m_portrRadio.addChangeListener(m_listener);
			
			m_combo.addActionListener(new CustomListener());
			
			m_customSpinner.addChangeListener(m_listener);
			
			m_customSpinner.setEnabled(false);
			
			PaUtils.setComponentsFont(this, m_font);
		
		}
		/**
		 * Sets all spiner's data; used in spinners listener 
		 */
		private void setData() {
			//fixing the bug with manual edit of JSpinner
			try {
				
				m_wSpinner.commitEdit();
				
				m_hSpinner.commitEdit();
				
			} catch (ParseException e) {
				
				writeLogOnly("Can't parse spinner data for resize instrument", e);
			}
			
			m_width = (int)m_wSpinner.getValue();
			
			m_height = (int)m_hSpinner.getValue();
			
		}
		
		/**
	     * 
	     * @return the currently chosen by user  proportion width/height
	     */
	    private double getProportion() {
	    	
	    	double k = 1.0;
	    	
	    	boolean landscape = false;
	    	
	    	if(m_landscapeRadio.isSelected()) {
	    		
	    		landscape = true;
	    	}
	    	switch(m_combo.getSelectedIndex()) {
	    	
		    	case 1: { //custom proportion
		    		
		    		k = (double)m_customSpinner.getValue();
		    		
		    		break;
		    	}
		    	case 2: {//current image proportion
		    		
		    		BufferedImage im = (BufferedImage)m_parent.getWorkPanel().getCurrentImage();
		    		
		    		if(!landscape) {
		    			
		    			k = ((double)im.getWidth())/im.getHeight();
		    		}
		    		
		    		else {  
		    			
		    			k = ((double)im.getHeight())/im.getWidth(); 
		    		}
		    		
		    		break;
		    	}
		    	case 3: { //9X13
		    		
		    		if(!landscape) {
		    			
		    			k = 13.0/9.0; 
		    		}
		    		else {  
		    			
		    			k = 9.0/13.0;
		    		}
		    		
		    		break;
		    	}
		    	
		    	case 4: { //10x15
		    		
		    		if(!landscape) { 
		    			
		    			k = 15.0/10.0; 
		    		}
		    		else {
		    			
		    			k = 10.0/15.0; 
		    		}
		    		
		    		break;
		    	}
		    	case 5: { //13x18
		    		
		    		if(!landscape) { 
		    			
		    			k = 18.0/13.0; 
		    		}
		    		else {  
		    			k = 13.0/18.0;
		    		}
		    		break;
		    	}
		    	case 6: { //A4
		    		
		    		if(!landscape) { 
		    			
		    			k = 297.0/210.0; 
		    		}
		    		else {  
		    			
		    			k = 210.0/297.0; 
		    		}
		    		break;
		    	}
		    	case 7: { //letter
		    		
		    		if(!landscape) { 
		    			
		    			k = 11/8.5;
		    		}
		    		else {  
		    			
		    			k = 8.5/11; 
		    		}
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
		    		
		    		
		    		if(m_combo.getSelectedIndex() == 1) {
		    			
		    			m_customSpinner.setEnabled(true);
		    			
		    			m_landscapeRadio.setEnabled(false);
		    			
		    			m_portrRadio.setEnabled(false);
		    			
		    			m_hSpinner.setEnabled(false);
		    			
		    		} else {
		    			
		    			m_customSpinner.setEnabled(false);
		    			
		    			m_landscapeRadio.setEnabled(true);
		    			
		    			m_portrRadio.setEnabled(true);
		    			
		    		}
		    		if(m_combo.getSelectedIndex() == 0) {

		    			m_hSpinner.setEnabled(true);
		    			
		    			return;
		    		}
		    		
		    		m_width = (int)m_wSpinner.getValue();
		    		
					m_height = (int)(m_width*getProportion());
					
					m_hSpinner.setValue(m_height);
					
		    		return;
		    		
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
		    	
		    	if(m_combo.getSelectedIndex() == 0) return;
    
		    	if(e.getSource() == m_customSpinner && (m_combo.getSelectedIndex() == 1)) {
		    		
		     		m_width = (int)m_wSpinner.getValue();
		     		
					m_height = (int)(m_width*getProportion());
					
					m_hSpinner.setValue(m_height);
					
		    		return;
		    	}
		    	
		    	if(e.getSource() == m_wSpinner ) {
		    		
		     		m_width = (int)m_wSpinner.getValue();
		     		
					m_height = (int)(m_width*getProportion());
					
					m_hSpinner.setValue(m_height);
					
		    		return;
		    	}
		    	if(e.getSource() == m_hSpinner ) {

		    		return;
		    	}
		    	if(e.getSource() == m_landscapeRadio ||  e.getSource() == m_portrRadio) {
		    		
		    		m_width = (int)m_wSpinner.getValue();
		    		
					m_height = (int)(m_width*getProportion());
					
					m_hSpinner.setValue(m_height);
				
		    		return;
		    	
		    	}
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
			
			m_wSpinner.setToolTipText(getGuiStrs("widthResizeSpinTooltip"));
			
			m_hSpinner.setToolTipText(getGuiStrs("heightResizeSpinTooltip"));
			
			m_combo.setToolTipText(getGuiStrs("comboResizeInsTooltip"));
			
			m_customSpinner.setToolTipText(getGuiStrs("customSpResizeInsTooltip"));
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

		writeLog("Instruments window: resize instrument  operation started.", null,
				true, false, false );		
		
		PaInstrument.isAnyInstrumentWasUsed = true; //this row should be in all instruments calls of getResultView() 
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			
			m_parent.resetInstrument();
			
			BufferedImage im = (BufferedImage) m_parent.getSourceViewImage();
			
			if(im == null) {
				
				 writeLog("Can't find current image to use resize instrument" , null, true, false, true);
				 
				 return;
			}
			 
			if(im.getWidth() == m_width && m_height == im.getHeight()) {
				
				writeLog("The desired sizes are the same as image sizes" , null, true, false, true);
				
				return;
			}
							 
			BufferedImage resultImage = null;
			
			resultImage =  PaAlgoTransform.resizeImage(im,m_width,m_height);
			
			if(resultImage != null) {	
				
				m_parent.setResultView(resultImage, getGuiStrs("resizeInstrumentName"));
			}
			else {
				
				writeLog("Instruments window: can't get result image for resize instrument", null,
						true, false, false );
			} 
		}
		catch(OutOfMemoryError e) {
			
			JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("outOfMemoryMessage"),
				    getMessagesStrs("messageCaption"),
				    JOptionPane.OK_OPTION);
			
			writeLog("Not enough memory: can't get result image for resize instrument" + NEXT_ROW +
					e.getMessage(), null,
					true, false, false );
		}
		finally {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window: blur instrument operation finished.", null,
					true, false, false );
		}
		 		
	}	
	
}
