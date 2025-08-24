
package paroi;

import static paglobal.PaUtils.getGuiStrs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import paeditor.PaComplexButton;
import paeditor.PaEnumInstrumentNames;
import paglobal.PaGuiTools;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for save image or ROI file operation; 
 * the special parameters panel creates here</p>
 */
public class PaRoiLoadButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Determines the type of operation - true - 'save as'; false - 'save'
	 */
	boolean m_saveAsFlag = false;

	private Font m_font = PaUtils.get().getBaseFont();
	
	private short m_operation_code = 0;// 0 -load, 1- new, 2- recent
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d - size of the button
	 * @param hash - the container to hold all complex buttons; it must be only one for the application
	 */
	public PaRoiLoadButton(PaRoiWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_ROI_LOAD, parent,
				new ImageIcon(PaUtils.get().getIconsPath() + "paloadroifile.png"),d, false, hash);

		
	}
	
	/**
	 * <p>Opens the special side panel for this button.
	 * It can be reloaded in order to get a specific side panel</p>
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
	
	public String loadRecentFile() {
		
		String s = (String) ((SpecialPanel)m_menuPanel).m_recent_combo.getSelectedItem();
		
		return s;
	
	}
	
	public short getOperationCode() { return m_operation_code; }
	
	/**
	 * <p>Special parameter panel class with all components; the link with button is performed 
	 * through m_saveAsFlag member of the button class.
	 * Special parameters frame appears next to the instrument button by using 
	 * the small menu button on the right side. This special window has all additional parameters
	 * for the operation. For different instruments this panel is different</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;

		JRadioButton m_loadRadio = new JRadioButton(getGuiStrs("roiLoadRadioCaption"));
		
		JRadioButton m_newRadio = new JRadioButton(getGuiStrs("roiNewRadioCaption"));
		
		JRadioButton m_recentRadio = new JRadioButton(getGuiStrs("roiRecentRadioCaption"));
				
		ButtonGroup m_butGroupType = new ButtonGroup();
		
		private JComboBox<String> m_recent_combo = new JComboBox<String>();
		
		public SpecialPanel() {
			
			super();
		
			createGui();
			
			setListeners();
			
			setToolTips();
			
			m_recent_combo.setModel(new DefaultComboBoxModel<String>(PaUtils.get().getRecents().getRecentRoiList().toArray(new String[0])));
			
			
			 m_loadRadio.setSelected(true);
			 
			 m_recent_combo.setEnabled(false);
			
			pack();
			
			setResizable(false);
			
		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

			JPanel panelButtons =  PaGuiTools.createHorizontalPanel();
			
			panelButtons.add(m_loadRadio);
			
			panelButtons.add(m_newRadio);
			
			panelButtons.add(m_recentRadio);
	
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("roiLoadNewRecentGroupTitle"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelButtons.setBorder(title_0);
			
			m_butGroupType.add(m_loadRadio);
			
			m_butGroupType.add(m_newRadio);
			
			m_butGroupType.add(m_recentRadio);
			
			JPanel panelMain = PaGuiTools.createVerticalPanel();
			
			JPanel panelCombo = PaGuiTools.createHorizontalPanel();
			
			JLabel labelRecent = new JLabel (getGuiStrs("recentfilePixInsLbl")); 
			
			labelRecent.setFont(m_font);
			
			panelCombo.add(labelRecent);
			
			panelCombo.add(m_recent_combo);
			
			panelMain.add(panelButtons);
			
			panelMain.add(panelCombo);
			
			add(panelMain);
				
			PaUtils.setComponentsFont(this, m_font);
	
		}
		
		/**
		 * Sets tooltips for the radio buttons 'save' and 'save as'
		 */
		private void setToolTips() {
			
			m_loadRadio.setToolTipText(getGuiStrs("loadROIRadioToolTip"));
			
			m_newRadio.setToolTipText(getGuiStrs("newROIRadioToolTip"));
			
			m_recentRadio.setToolTipText(getGuiStrs("recentROIRadioToolTip"));
			
			m_recent_combo.setToolTipText(getGuiStrs("recentROIComboToolTip"));
			
		}
		
		
		/**
		 * <p>Sets listener for the radio buttons</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
			
			m_loadRadio.addActionListener(l);
			
			m_newRadio.addActionListener(l);
			
			m_recentRadio.addActionListener(l);
				
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen  buttons</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {

				if(e.getSource() == m_loadRadio ) {
		    		
					if(m_loadRadio.isSelected()) {
						
						m_recent_combo.setEnabled(false);
						
						m_operation_code = 0;
						
					}
		    		
		    	}
				if(e.getSource() == m_newRadio ) {
		    		
					if(m_newRadio.isSelected()) {
						
						m_recent_combo.setEnabled(false);
						
						m_operation_code = 1;
						
					}
		    		
		    	}
				if(e.getSource() == m_recentRadio) {
		    		
		    		if(m_recentRadio.isSelected()) {
		    			
		    			m_recent_combo.setEnabled(true);
		    			
		    			m_operation_code = 2;
		    		}
		    		
		    	}
			}
		}
	}
}