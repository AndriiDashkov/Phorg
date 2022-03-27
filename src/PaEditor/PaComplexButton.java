
package PaEditor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import PaDialogs.PaSpecialDialog;
import PaDialogs.PaSpecialDialog.DialogType;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaGlobal.PaUtils;
import static PaGlobal.PaLog.writeLogOnly;
import static PaGlobal.PaUtils.*;

/**
 * @author Andrey Dashkov
 * <p>The base class for complex buttons in image editor which works with instruments; 
 * It has two buttons - one is a main button for operation start, and other is a
 * 'menu' or 'special'  button which is for opening a special parameters dialog for additional 
 * parameters which user can change in order to change main operation process</p>
 */
public class PaComplexButton extends JPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * Main button which starts the instrument using
	 */
	protected AbstractButton m_mainButton;
	/**
	 * Special button; opens the special panel with additional values for instrument; it is on the right side
	 */
	JButton m_menuButton;
	
	private final int m_menuButtonWidth = 12;

	/**
	 * Special side menu panel for parameters to control the operation associated with this button
	 */
	protected JDialog m_menuPanel;
	
	protected PaInstrumentsWindow m_parent;
	/**
	 * For complex button the external action can be set
	 */
	Action m_externalAction = null;
	
	
	protected boolean isToggle = false;
	
	protected PaEnumInstrumentNames m_instrumentName = PaEnumInstrumentNames.NONE;
	/**
	 * Circle buffer container to save in multiple way  instruments current parameters.
	 * This container works in conjunction with class PaQueuePanel (this class provides
	 * the support of circle functionality)
	 * Here we have only link for a appropriate queue. Queues(buffers) for all instruments are set on the level
	 * of  PaViewPanel class. Every instrument/complex button has its own queue.
	 * See HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> m_instrumnetsData in PaViewPanel.
	 */
	protected ArrayBlockingQueue<Object> m_queue = null;
	
	
	
	/**
	 * @param - innerInstrumentName - inner name of instrument which will be used with this button.
	 * This name is mainly used to get session saved instrument parameters. So it's safe to set this parameter to
	 * null or NONE. 
	 * @param parent - parent window where the instrument operates
	 * @param icon - icon for the main instrument button
	 * @param d - size of this button panel
	 * @param toggleButton - true if we want to make the main button to be a toggled button : this varian of complex button is used when the instrument can work in to variants for whole image 
	 * and with starting selection area instrument
	 */
	public PaComplexButton(PaEnumInstrumentNames innerInstrumentName, PaInstrumentsWindow parent, ImageIcon icon, Dimension d, boolean toggleButton,
			HashMap<PaEnumInstrumentNames,ArrayBlockingQueue<Object>> hash) {
		
		super();
		//the special panel should be closed when this event is here	
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_EDITOR_CLOSED, this, "closeAll");

		m_parent = parent;
		
		if(m_instrumentName != null) { m_instrumentName = innerInstrumentName;}
		
		m_queue = hash.get(m_instrumentName);
		
		if(toggleButton) {
			
			m_mainButton = new JToggleButton(icon);
			
			isToggle = true;
			
		} else {
			
			m_mainButton = new JButton(icon);
			
		}
		m_menuButton = new JButton( new ImageIcon(PaUtils.get().getIconsPath() + "pamenubutton.png"));

		m_mainButton.setMaximumSize(d);
		
		m_menuButton.setMaximumSize(new Dimension(m_menuButtonWidth,d.height));
		
		m_mainButton.setMinimumSize(d);
		
		m_menuButton.setMinimumSize(new Dimension(m_menuButtonWidth,d.height));
		
		createGui();
			
		m_menuPanel = createSpecialPanel();
		
		setListeners();
	
		setMaximumSize(new Dimension(d.width+m_menuButtonWidth,d.height));
		
		setMinimumSize(new Dimension(d.width+m_menuButtonWidth,d.height));
		
		setPreferredSize(new Dimension(d.width+m_menuButtonWidth,d.height));
		
		m_menuButton.setToolTipText(getGuiStrs("specialPanelButtonToolTip"));
		
		setInitialData();
	}
	/**
	 * Sets initial data from the outside queue for this instrument. That queue remembers the set of parameters of
	 * every instrument where the saving into the buffer has been used.
	 * Does nothing on the base level.
	 */
	protected void setInitialData() {}
	/**
	 * 
	 * @return the main button of this complex button;
	 */
	public AbstractButton getMainButton() {
		return m_mainButton;
	}
	/**
	 * <p> Creation of all components and their layouts</p>
	 */
	private void createGui() {
		
		setLayout(new BoxLayout(this , BoxLayout.X_AXIS));
		
		add(m_mainButton);
		
		add(m_menuButton);	
	}
	
	/**
	 * <p>Sets all listeners for the buttons</p>
	 */
	private void setListeners() {
		
		Forwarder forwarder = new Forwarder();
		
		m_menuButton.addActionListener(forwarder);
		
		m_mainButton.addActionListener(forwarder);
		
	}
	
	private class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_menuButton ) {
				openSpecialPanel();
			}
			
			//if the external action has been set then it controls the reaction for button click
			//that is why we blocked this action performance
			if ( e.getSource() == m_mainButton && m_externalAction == null ) {
				
				if(m_menuPanel != null) { m_menuPanel.setVisible(false); }
				
				if(!isToggle  || (isToggle && m_mainButton.isSelected())) {
					
					setAllData();
					
					startInstrument();
					
				}
				else { //when the button is toggle and unselected - do nothing
					
					m_parent.resetInstrument();
				}
			}

		}
	}
	/**
	 * Sets all data to inner members of the button; should be reloaded in inherited classes
	 */
	protected void setAllData() 
	{
		
	}
	/**
	 * <p>Opens special panel for this button panels</p>
	 */
	protected void openSpecialPanel() {
		//we must close other opened special panels	

		if(m_menuPanel != null) {
			
			if(m_menuPanel.isVisible()) { 
				
				m_menuPanel.setVisible(false); 
				
				PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_EDITOR_CLOSED) );
			}
			else {
				
				PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_EDITOR_CLOSED) );
				
				Point p = this.getLocationOnScreen();
				
				m_menuPanel.setLocation(p.x+ this.getWidth()+10,p.y);
				
				m_menuPanel.setVisible(true);

			}
		}
		else {
			writeLogOnly("Can't open the special panel for instrument",null);
		}
				
	}
	
	/**
	 * <p>Starts instrument; must be NOT overloaded in children classes.
	 * Overload the startInstrumentImpl() instead</p>
	 */
	protected void startInstrument() {
		
		if( PaInstrument.m_previewImageNotConfirmed ) {
			
			PaSpecialDialog dialog = new PaSpecialDialog(m_parent, DialogType.YES_NO_CANCEL_OPTION,
					 getMessagesStrs("messageAnswerCaption"), 
					 getMessagesStrs("unAppliedInstrChangesMessage"),true,JOptionPane.NO_OPTION,
					 getGuiStrs("unaplDialogCheckBoxTooltip"), 2);
			
			 
			
			dialog.setVisible(true);
			
			int n = dialog.getCloseFlag();
			
			if ( n == JOptionPane.YES_OPTION) {	
				
				m_parent. getInstrumentPanel().confirmChanges();
			}
			
			if ( n == JOptionPane.CANCEL_OPTION  ) {	
				
				
				m_mainButton.setSelected(false);
				
				 return;
			}

		} 
		
		startInstrumentImpl();
			
	}
	
	/**
	 * <p>Starts instrument; must be overloaded in the child class.</p>
	 */
	protected void startInstrumentImpl() {
		
			
	}
	
	
	/**
	 * <p>Creates frame which starts when the special button is pushed</p>
	 * <p>Must be overloaded in then child class; the call of parent implementation of 
	 * this function is mandatory!</p>
	 */
	protected JDialog createSpecialPanel() {
		
		return null;
		 
	}
	/**
	 * <p>Sets action for main button</p>
	 * @param act  - action to be set
	 */
	public void setAction(Action act) {
		
		m_externalAction = act;//set the link to the action
		
		m_mainButton.setAction(act);
		
		m_mainButton.setText(null);
	}

	/**
	 * <p>Closes all special panels that can be opened at the moment of editor closing<p>
	 * @param event
	 */
	public void closeAll(PaEvent event) {
		
		if (PaEventDispatcher.IMAGE_EDITOR_CLOSED != event.getEventType() ) { return; }
		
		if(m_menuPanel != null) m_menuPanel.dispose();
	}
	/**
	 * 
	 * @param enable - sets special button to be enabled or not
	 */
	public void setSpecialButtonEnabled(boolean enable) {
		
		m_menuButton.setEnabled(enable);
	}
	/**
	 * 
	 * @param flag -- sets special menu button to be visible or not
	 */
	public void setSpecialButtonVisible(boolean flag) {
		
		m_menuButton.setVisible(flag);
	}
	
	public JDialog getSpecialPanel() { return m_menuPanel; }
	
	public void setData(Object data) {}
	
	public Object getData() 
	{
		return null;
	}
	
	public void clickMainButton()
	{
		m_mainButton.doClick();
		
	}
	
}
