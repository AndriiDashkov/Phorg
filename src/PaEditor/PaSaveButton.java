
package paeditor;


import static paglobal.PaUtils.getGuiStrs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 * <p>This class determines the complex button for save image or ROI file operation; 
 * the special parameters panel creates here</p>
 */
public class PaSaveButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;
	/**
	 * Determines the type of operation - true - 'save as'; false - 'save'
	 */
	boolean m_saveAsFlag = false;

	private Font m_font = PaUtils.get().getBaseFont();
	
	/**
	 * @param parent - parent window where the instrument for this button operates
	 * @param d - size of the button
	 * @param hash - the container to hold all complex buttons; it must be only one for the application
	 */
	public PaSaveButton(PaInstrumentsWindow parent, Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_SAVE, parent, null, d, false, hash);

		
	}
	/**
	 * 
	 * @return true if the status of the button and save operation is 'save as'
	 */
	public boolean isSaveAs() { return m_saveAsFlag; }
	
	
	/**
	 * <p>Opens the special side panel for this button.
	 * It can be reloaded in order to get a specific side panel</p>
	 */
	protected void openSpecialPanel() {	
		
		super.openSpecialPanel();
		
	}
	
	/**
	 * The function helps to switch between radio buttons "save" and "save as"
	 */
	public void setRadioButtonSaveAs(boolean flag) {
		
		((SpecialPanel) m_menuPanel).setRadioButtonSaveAs(flag);
		
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {
		
		SpecialPanel p = new SpecialPanel();
		
		return p;
		
	}
	
	/**
	 * Sets specific tooltips for main button of the complex button, and 2 radio buttons on side panel
	 */
	public void setToolTips(String mainButtonTip, String saveRadioTip, String saveAsRadioTip) {
		
		((SpecialPanel) m_menuPanel).setToolTips(saveRadioTip, saveAsRadioTip);
		
		getMainButton().setToolTipText(mainButtonTip);
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

		JRadioButton m_saveRadio = new JRadioButton(getGuiStrs("saveRadioCaption"));
		
		JRadioButton m_saveAsRadio = new JRadioButton(getGuiStrs("saveAsRadioCaption"));
				
		ButtonGroup m_butGroupType = new ButtonGroup();
		
		public SpecialPanel() {
			
			super();
		
			createGui();
			
			setListeners();
			
			setToolTips(getGuiStrs("saveImageRadioTooltip"), getGuiStrs("saveImageAsRadioTooltip"));
			
			pack();
			
			setResizable(false);
			
		}
		/**
		 * <p>Creates all gui components for this special panel</p>
		 */
		private void createGui() {
					
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

			JPanel panelButtons = new JPanel();
			
			panelButtons.setLayout(new GridLayout(2, 1));
			
			panelButtons.add(m_saveRadio);
			
			panelButtons.add(m_saveAsRadio);
	
			Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getGuiStrs("saveGroupEditorOperation"),
					TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
			
			panelButtons.setBorder(title_0);
			
			m_butGroupType.add(m_saveRadio);
			
			m_butGroupType.add(m_saveAsRadio);
			
			add(panelButtons);
			
			m_saveRadio.setSelected(true);
			
			PaUtils.setComponentsFont(this, m_font);
	
		}
		
		/**
		 * Sets tooltips for the radio buttons 'save' and 'save as'
		 */
		private void setToolTips(String saveTip, String saveAsTip) {
			
			m_saveRadio.setToolTipText(saveTip);
			
			m_saveAsRadio.setToolTipText(saveAsTip);
			
		}
		
		/**
		 * <p>Sets listener for the radion buttons</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
			
			m_saveRadio.addActionListener(l);
			
			m_saveAsRadio.addActionListener(l);
				
		}
		
		/**
		 * 
		 * @author Andrii Dashkov
		 * <p>Listener class to listen  buttons</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {

				m_saveAsFlag = m_saveAsRadio.isSelected();

			}
		}
		
		public void setRadioButtonSaveAs(boolean flag) {
			
			m_saveRadio.setSelected(!flag);
			
			m_saveAsRadio.setSelected(flag);
		}

	}

}
