package paglobal;

import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;


/**
 *  @author Andrii Dashkov
 */
public class PaButton extends AbstractButton {

	private static final long serialVersionUID = 1L;
	
	private JButton _button;
	
	private final int _flag;

	public PaButton (String iconPath, int flag) {
		
		ImageIcon icon = new ImageIcon(iconPath);
		
		_flag = flag;
		
		_button = new JButton(icon);
		
		_button.setMargin(new Insets(0, 0, 0, 0));
	
	}
	
	public JButton get_Button() {
		
		return _button;
	}

	public int get_flag() {
		
		return _flag;
	}
	
}
