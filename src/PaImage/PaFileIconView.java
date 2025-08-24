package paimage;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

/**
 *
 * @author Andrii Dashkov
 *
 */
public class PaFileIconView extends FileView {

	private FileFilter filter;
	
	private Icon icon;
	
	public PaFileIconView  (FileFilter aFilter, Icon anIcon) {
		
		filter = aFilter;
		
		icon = anIcon;
	}
	
	public Icon getIcon (File file) {
		
		if (!file.isDirectory() && filter.accept(file)) {

			return icon;
			
		} else {
			
			return null;
		}
	}
}
