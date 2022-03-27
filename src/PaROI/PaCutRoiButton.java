
package PaROI;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import PaEditor.PaComplexButton;
import PaEditor.PaEnumInstrumentNames;
import PaEditor.PaQueuePanel;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;

/**
 * @author Andrey Dashkov
 * <p>New ROI crop operation button.</p>
 */
public class PaCutRoiButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	
	private int m_width;
	
	private int m_height;
	
	private int m_stride;
	
	private boolean m_blurFlag;
	
	private int m_blurLevel;
	
	private boolean m_keepRatioFlag = true;
	
	private int m_step = 1;
	
	private boolean m_nextImageAutoSwitch = false;
	
	private boolean m_keepActivated = false;
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d - size of the complex button
	 * @param hash - the container to hold all complex buttons in the application; it must be only one
	 */
	public PaCutRoiButton(PaRoiWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_ROI_CROP, parent,
				new ImageIcon(PaUtils.get().getIconsPath() + "pacropfile.png"),d, true, hash);
		
		setToolTips();
		
		setInitialData();
	}
	
	/**
	 * <p>Opens special side panel for this button. </p>
	 */
	protected void openSpecialPanel() {	
		
		super.openSpecialPanel();
		
	}
	
	/**
	 * Sets initial data from outside queue for this instrument.
	 * Overloaded function, is invoked from PaComplexButton level.
	 */
	@Override
	protected void setInitialData() {
		
		Data data = new Data();
		
		data.width = 30;
		
		data.height = 80;
		
		data.stride = 0;
		
		data.blurLevel = 3;
		
		data.blurFlag = false;

		data.keepRatioFlag = true;
		
		data.step = 1;
			
		if(m_queue != null && !m_queue.isEmpty()) {	
			
			try {
				
				Data data1 = (Data)m_queue.take();
				
				setData(data1);
				
				m_queue.put(data1);
				
			} catch (InterruptedException e) {
				
				setData(data);
			}
		}
		else {
			
			setData(data);
		}
	}
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters side panel</p>
	 */
	@Override
	protected JDialog createSpecialPanel() {
		
		SpecialPanel p = new SpecialPanel();
		
		return p;
		
	}
	/**
	 * Sets tooltips
	 */
	private void setToolTips() {
		
		m_mainButton.setToolTipText(getGuiStrs("createNewRoiInstButtonToolTip"));	
		
	}
	
	public int getCropWidth() { return m_width; }
	
	public int getCropHeight() { return m_height; }	
	
	public boolean isRatioMustBeKept() { return m_keepRatioFlag;}
	
	public int getStep_Inc_Dec() { return m_step; }

	public boolean isKeepInstrumentActivated() { return m_keepActivated;}
	
	public boolean isNextImageSwitchActivated() { return m_nextImageAutoSwitch; }
	
	
	/**
	 * 
	 * @author Andrey Dashkov
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

		JSpinner m_stepSpinner;
		
		CustomListener m_forwarder = new CustomListener();
		
		JCheckBox m_keepRatioCheck  = new JCheckBox();
		
		JCheckBox m_nextImageAuto  = new JCheckBox();
		
		JCheckBox m_keepInstAuto  = new JCheckBox();
		
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
	
			SpinnerNumberModel spModel1 = new SpinnerNumberModel(100, 1, 10000, 1);
			
			m_wSpinner = new JSpinner(spModel1);
			
			PaGuiTools.fixComponentSize(m_wSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_wSpinner, 60);

			SpinnerNumberModel spModel2 = new SpinnerNumberModel(100, 1, 10000, 1);
			
			m_hSpinner = new JSpinner(spModel2);
			
			PaGuiTools.fixComponentSize(m_hSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_hSpinner , 60);
						
			SpinnerNumberModel spModel5 = new SpinnerNumberModel(1, 1, 10, 1);
			
			m_stepSpinner = new JSpinner(spModel5);
			
			PaGuiTools.fixComponentSize(m_stepSpinner);
			
			PaGuiTools.setComponentFixedWidth(m_stepSpinner , 60);
			
			JPanel panelHoriz = PaGuiTools.createHorizontalPanel();
			
			JPanel panelParam = PaGuiTools.createVerticalPanel();
			
			JLabel lX = new JLabel(" " + getGuiStrs("cropSaveWidthInsLabel") + " ");
			
			JLabel lY = new JLabel(" " + getGuiStrs("cropSaveHeightInsLabel") + " ");
			
			JPanel panelX = PaGuiTools.createHorizontalPanel();
			
			panelX.add(lX);
			
			panelX.add(m_wSpinner);
			
			panelX.add(lY);
			
			panelX.add(m_hSpinner);
			
			panelX.add(Box.createHorizontalGlue());
						
			JLabel lkeepRatioCheck = new JLabel(" " + getGuiStrs("keepRatioCropWinLabel") + " ");
			
			JLabel lStep = new JLabel(" " + getGuiStrs("stepCropWindow_WSAD_resizeLabel") + " ");
			
			JPanel panelKeepRatio = PaGuiTools.createHorizontalPanel();
			
			panelKeepRatio.add(lkeepRatioCheck);
			
			panelKeepRatio.add(m_keepRatioCheck);
			
			panelKeepRatio.add(lStep);
			
			panelKeepRatio.add(m_stepSpinner);
			
			panelKeepRatio.add(Box.createHorizontalGlue());
							
			JLabel lNextImage = new JLabel(" " + getGuiStrs("autoNextImageCropLabel") + " ");
			
			JLabel lautoInstr = new JLabel(" " + getGuiStrs("keepInstrActivatedCropLabel") + " ");
			
			JPanel autoPanel = PaGuiTools.createHorizontalPanel();
			
			autoPanel.add(lNextImage);
			
			autoPanel.add(m_nextImageAuto );
			
			autoPanel.add(lautoInstr);
			
			autoPanel.add(m_keepInstAuto);
			
			autoPanel.add(Box.createHorizontalGlue());
			
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("roiNewGroupNameInsCaption"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelParam.setBorder(title_0);
			
			panelParam.add(panelX);
			
			panelParam.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			panelParam.add(panelKeepRatio);
			
			panelParam.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
			
			panelParam.add(autoPanel);
			
			panelParam.add(Box.createVerticalGlue());
			
			PaQueuePanel pV = new PaQueuePanel(PaCutRoiButton.this, m_queue);
			
			panelHoriz.add(panelParam);
			
			panelHoriz.add(pV);
			
			add(panelHoriz);
			
			m_listener = new SpinnerListener();
						
			m_wSpinner.addChangeListener(m_listener);
					
			m_hSpinner.addChangeListener(m_listener);
						
			m_stepSpinner.addChangeListener(m_listener);
	
			m_keepRatioCheck.addActionListener(m_forwarder);
			
			m_nextImageAuto.addActionListener(m_forwarder);
			
			m_keepInstAuto.addActionListener(m_forwarder);
					
			PaUtils.setComponentsFont(this, m_font);

			
		}
		
	   class CustomListener implements ActionListener {


			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == m_keepRatioCheck) {
					
					m_keepRatioFlag = m_keepRatioCheck.isSelected();
					

				}
				else
			   	if(e.getSource() == m_nextImageAuto ) {
					   		
					m_nextImageAutoSwitch = m_nextImageAuto.isSelected();
					
		    	}
			   	else
		    	if(e.getSource() == m_keepInstAuto ) {
		    		
		    		m_keepActivated = m_keepInstAuto.isSelected();
		    		
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
		    		
		     		m_width = (int)m_wSpinner.getValue();
		     		
		    		return;
		    	}
		    	if(e.getSource() == m_hSpinner ) {
		    		
		     		m_height = (int)m_hSpinner.getValue();
		     		
		    		return;
		    	}
		    	if(e.getSource() == m_stepSpinner ) {
		    		
		     		m_step = (int)m_stepSpinner.getValue();
		     		
		    		return;
		    	}
		    	
		 
		    }
		    
		    
			@Override
			public void focusGained(FocusEvent arg0) {}


			@Override
			public void focusLost(FocusEvent e) {}
		}

		/**
		 * Sets tooltips
		 */
		private void setToolTips() {
			
			m_wSpinner.setToolTipText(getGuiStrs("cropSaveWidthTooltip"));
			
			m_hSpinner.setToolTipText(getGuiStrs("cropSaveHeightTooltip"));
			
			m_stepSpinner.setToolTipText(getGuiStrs("cropResizeStepSpinnerTooltip"));
			
			m_keepRatioCheck.setToolTipText(getGuiStrs("cropKeepRatioCheckTooltip"));
			
			m_nextImageAuto.setToolTipText(getGuiStrs("nextImageAutoInstCheckTooltip"));
			
			m_keepInstAuto.setToolTipText(getGuiStrs("keepInstrActivatedCheckTooltip"));
			

		}
	}
	/**
	 * Sets all data to inner members of the button
	 */
	protected void setAllData() {

	}

	
	/**
	 * <p>Starts the new ROI creation instrument</p>
	 */
	@Override
	protected void startInstrumentImpl() {
		
		
		boolean roiFileFlag = ((PaRoiWindow) m_parent).isRoiListFileLoaded();
		
		if (  m_mainButton.isSelected() && roiFileFlag) {
			
			writeLog("Instruments window: the select roi instrument has been invoked.", null, 
					true, false, false );
			
			
			m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.ROI_CUT,this);

			
		}
		else {
			
			if( !roiFileFlag ) {
				
				JOptionPane.showMessageDialog(m_parent,
	    				getMessagesStrs("messageROIFileNotLoaded"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
			}
			
			
			m_parent.resetInstrument();
			
			m_mainButton.setSelected(false);
		}
	}
	@Override
	public void setData(Object data) {
		
		Data d = (Data) data;
		
		m_width = d.width;
		
		m_height = d.height;
		
		m_stride = d.stride;
		
		m_blurLevel = d.blurLevel;
		
		m_blurFlag = d.blurFlag;
		
		m_keepRatioFlag = d.keepRatioFlag;
		
		m_step = d.step;
		
		m_nextImageAutoSwitch = d.nextImageAutoSwitch;
		
		m_keepActivated = d.keepActivated;
		
		if(m_menuPanel != null) {
			
			SpecialPanel p = (SpecialPanel) m_menuPanel;
			
			p.m_hSpinner.setValue(m_height);
			
			p.m_wSpinner.setValue(m_width);
			
			p.m_keepRatioCheck.setSelected(m_keepRatioFlag);
			
			p.m_stepSpinner.setValue(m_step);
			
			p.m_nextImageAuto.setSelected(m_nextImageAutoSwitch);
			
			p.m_keepInstAuto.setSelected(m_keepActivated);
			
		}
	}
	
	@Override
	public Object getData() {
		
		Data d = new Data();
		
		d.width = m_width;
		
		d.height = m_height;
		
		d.stride = m_stride;
		
		d.blurLevel = m_blurLevel;
		
		d.blurFlag = m_blurFlag;
		
		d.keepRatioFlag = m_keepRatioFlag;
		
		d.step = m_step;
		
		d.nextImageAutoSwitch = m_nextImageAutoSwitch;
		
		d.keepActivated = m_keepActivated;
		
		return d;
	}
	
	private class Data {
		
		public int width;
		
		public int height;
		
		public int stride;
		
		public int blurLevel;
		
		public boolean blurFlag;
		
		public boolean keepRatioFlag;
		
		public int step;
		
		public boolean nextImageAutoSwitch;
		
		public boolean keepActivated;
	}
}
