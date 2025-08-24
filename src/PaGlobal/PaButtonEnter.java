
package paglobal;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Andrii Dashkov
 *<p>Common button class which has Enter press support</p>
 */
public class PaButtonEnter extends JButton {


	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public PaButtonEnter() {
		
		setListener();
	}

	/**
	 * @param arg0
	 */
	public PaButtonEnter(Icon arg0) {
		
		super(arg0);
		
		setListener();
	}

	/**
	 * @param arg0
	 */
	public PaButtonEnter(String arg0) {
		
		super(arg0);
		
		setListener();
	}

	/**
	 * @param arg0
	 */
	public PaButtonEnter(Action arg0) {
		
		super(arg0);
		
		setListener();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PaButtonEnter(String arg0, Icon arg1) {
		
		super(arg0, arg1);

	}

	private void setListener(){
		
		this.addKeyListener(new ButtonEnterListener());
	}
	
	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Listener for Enter button event </p>
	 */
	private class ButtonEnterListener extends KeyAdapter {
		
	    public void keyPressed(KeyEvent e) {
	    	
	         if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	        	 
	        	 if(hasFocus()) {		      
	        		 
		        	doClick();
		        	
	        	 }
			}
	    } 
	}
}
