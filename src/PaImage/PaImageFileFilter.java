package PaImage;

import java.io.File;
import java.io.FileFilter;
import PaGlobal.PaUtils;


/**
 * @author avd
 */
public class PaImageFileFilter implements FileFilter {
	
	 public boolean accept(File file) {
			  
		 String name = file.getName(); 
		
		 return  PaUtils.get().isImageOrVideoFileStr(name);
	 }
}
