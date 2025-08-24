package paimage;

import java.io.File;
import java.io.FileFilter;

import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 */
public class PaImageFileFilter implements FileFilter {
	
	 public boolean accept(File file) {
			  
		 String name = file.getName(); 
		
		 return  PaUtils.get().isImageOrVideoFileStr(name);
	 }
}
