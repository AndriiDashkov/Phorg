/**
 * 
 */
package paactions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import paevents.PaEventDispatcher;
import paevents.PaEventEnable;

/**
 * @author Andrii Dashkov
 *
 */
public class PaAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private String m_name;
	/**
	 * @param name
	 */
	protected PaAction(String name) {
		
		super();
		
		m_name = name;
	}

	public String getActName() { return m_name; }

	public void enableAction (PaEventEnable e) {}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}
	/**
	 * Disconnects the action from events framework
	 */
	public void disconnect() {
		
		PaEventDispatcher.get().disconnect(this);
	}

}
