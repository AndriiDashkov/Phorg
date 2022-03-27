package PaGlobal;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import PaEvents.PaEventAlbumResizePanel;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventInt;

/**
 * Button group class to show in the caption of the main panel
 * can change it's text and icons
 * @author avd
 *
 */
public class PaButtonsGroup {

	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "refreshGroupButton");
	}
	
	private JPanel _panel_MAIN;
	
	private JPanel panelGrid;
	
	private JLabel m_lableName;
	
	JLabel filterLabel;
	
	private PaButton m_turnButton;
	
	private PaButton m_expandButton;
	
	private PaButton m_resizeButton;

	public PaButtonsGroup (String namePanel, ImageIcon ic) {

		_panel_MAIN = PaGuiTools.createHorizontalPanel();
			
		m_lableName = new JLabel(namePanel);
		
		m_lableName.setIcon(ic);
		
		JPanel panelFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		
		panelGrid = new JPanel( new GridLayout( 1,3,0,0) );
		
		panelFlow.add(panelGrid);
		
		filterLabel = new JLabel("     ");
		
		filterLabel.setIcon(new ImageIcon(PaUtils.get().getIconsPath() + "pafiltersmall.png"));
		
		m_turnButton = new PaButton(PaUtils.get().getIconsPath() + "paclose.png", 0);
	
		m_expandButton = new PaButton(PaUtils.get().getIconsPath() + "paopen.png", 1);
			
		m_resizeButton = new PaButton(PaUtils.get().getIconsPath() + "pafit.png", 2);
		
		panelGrid.add(filterLabel);
		
		panelGrid.add(m_turnButton.get_Button());
		
		panelGrid.add(m_expandButton.get_Button());

		m_turnButton.get_Button().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				panelGrid.removeAll();	
				
				panelGrid.add(filterLabel);
				
				panelGrid.add(m_turnButton.get_Button());
				
				panelGrid.add(m_expandButton.get_Button());
				
				_panel_MAIN.updateUI();
					
				PaEventAlbumResizePanel event = new PaEventAlbumResizePanel(getButtonGroup(), m_turnButton);
				
				PaEventDispatcher.get().fireCustomEvent(event);
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});
		
		m_expandButton.get_Button().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				panelGrid.removeAll();
				
				panelGrid.add(filterLabel);
				
				panelGrid.add(m_turnButton.get_Button());
				
				panelGrid.add(m_resizeButton.get_Button());
				
				_panel_MAIN.updateUI();
				
				PaEventInt event = new PaEventAlbumResizePanel(getButtonGroup(), m_expandButton);
				
				PaEventDispatcher.get().fireCustomEvent(event);
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});
		
		m_resizeButton.get_Button().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				panelGrid.removeAll();
				
				panelGrid.add(filterLabel);
				
				panelGrid.add(m_turnButton.get_Button());
				
				panelGrid.add(m_expandButton.get_Button());
				
				_panel_MAIN.updateUI();

				PaEventInt event = new PaEventAlbumResizePanel(getButtonGroup(), m_resizeButton);
				
				PaEventDispatcher.get().fireCustomEvent(event);
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});
		
	    filterLabel.setVisible(false);
		
		panelFlow.add(panelGrid);
		
		_panel_MAIN.add(new JLabel(" ")); //just as spacing
		
		_panel_MAIN.add(m_lableName);
		
		_panel_MAIN.add(Box.createHorizontalGlue());
		
		_panel_MAIN.add(panelFlow);
		
		setToolTips();
	}
	/**
	 * 
	 * @param text - text to show in the group
	 */
	public void setMainText(String text) {
		
		m_lableName.setText(text);
	}
	
	public void setFilterLabelVisible(boolean flag) {
		
		filterLabel.setVisible(flag);
	}
	
	public void setFilterLabelEnabled(boolean flag) {
		
		filterLabel.setEnabled(flag);
	}
	
	public JLabel getMainText () {
		
		return m_lableName;
	}
		

	public JPanel getMainPanel () {
		
		return _panel_MAIN;
	}
	

	public void refreshGroupButton (PaEventEnable eventAlbom) {
		
		if (eventAlbom.get_flag() == 100) {
			
			panelGrid.removeAll();	
			
			panelGrid.add(filterLabel);
			
			panelGrid.add(m_turnButton.get_Button());
			
			panelGrid.add(m_expandButton.get_Button());
			
			_panel_MAIN.updateUI();
		} 
	}

	public PaButtonsGroup getButtonGroup () {
		
		return this;
	}
	
	public void setExpandButtonVisible(boolean flag) {
		
		m_expandButton.setVisible(flag);
	}
	/**
	 * Sets tooltips for buttons
	 */
	private void setToolTips() {
		

		filterLabel.setToolTipText(PaUtils.getGuiStrs("filterLabelIndicatorToolTip"));
		
		m_turnButton.get_Button().setToolTipText(PaUtils.getGuiStrs("closeButtonGroupToolTip"));
		
		m_expandButton.get_Button().setToolTipText(PaUtils.getGuiStrs("openButtonGroupToolTip"));
		
		m_resizeButton.get_Button().setToolTipText(PaUtils.getGuiStrs("resizeButtonGroupToolTip"));
	}
}
