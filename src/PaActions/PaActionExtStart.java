package PaActions;



import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import PaGlobal.PaUtils;


/**
 * <p> Start an external image editor action</p>
 * @author avd
 *
 */
public class PaActionExtStart extends PaAction {
	
	private static final long serialVersionUID = 1L;

	
	String command = new String();
	
	public PaActionExtStart(String c, String name, String keys) {
		
		super("paactionextstart");
		
		command = c;
		
		putValue(AbstractAction.SHORT_DESCRIPTION, name);
		
		putValue(NAME,  name);
	}
	
	public void setCommand(String c) {
		
		command = c;
	}

	
	public void actionPerformed(ActionEvent e) {
		
		PaUtils.get().getViewPanel().startExternalCommand(command);

	}
	
}