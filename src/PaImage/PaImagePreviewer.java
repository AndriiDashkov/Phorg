package PaImage;

import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 * @author avd
 */
public class PaImagePreviewer extends JLabel {
	
	private static final long serialVersionUID = 1L;

	public PaImagePreviewer (JFileChooser chooser) {
		
		setPreferredSize(new Dimension(200, 200));
		
		setBorder(BorderFactory.createEtchedBorder());
		
		chooser.addPropertyChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				
				if (event.getPropertyName() == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {


					File file = (File) event.getNewValue();
					
					if (file == null) {
						
						setIcon(null);
						
						return;
					}
					
					ImageIcon icon = new ImageIcon (file.getPath());
					
					if (icon.getIconWidth() > getWidth()) {
						
						icon = new ImageIcon (icon.getImage().getScaledInstance(getWidth(), -1, Image.SCALE_FAST));
					}
					
					setIcon(icon);
				}	
			}
		});		
	}
}
