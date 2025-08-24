package paimage;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.concatPathName;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import paalgorithms.PaAlgoTransform;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 *
 */
public class PaViewRunnable implements Runnable {
	
	private PaViewPhotosForm viewPhotoForm;
	
	private PaViewPanel component;
	
	public PaViewRunnable (PaViewPhotosForm viewPhotoForm, PaViewPanel component) {
		
		this.component = component;
		
		this.viewPhotoForm = viewPhotoForm;
	}

	@Override
	public void run() {

		try {
			
			if (viewPhotoForm.getImage().getImageIcon() == null || viewPhotoForm.getImage().get_scale() != 
							PaUtils.get().getSettings().getPhotoScale()) {
				
				Image ic = null;
				
				if ( PaUtils.get().getSettings().getCreateImagesCopiesFlag() ) {
					
					String fileName = Integer.toString(viewPhotoForm.getImage().getId());
					
					String fullPath = concatPathName( PaUtils.get().getPathToBoostIcons(component.getPhotoContainer()),
								fileName+".jpeg");
					
					File outFile = new File(fullPath);
					
					try {
						
					if ( outFile.exists() ) {
									
						ic = ImageIO.read(outFile);
				
					}
					else {
						
						outFile = new File(viewPhotoForm.getImage().getFullPath());
						
						ic = ImageIO.read(outFile);
						}
					}
					catch (IIOException eI) {
						
						ic = null;
					}
					catch (IllegalArgumentException eI2) {
						
						ic = null;
					}
					
				}
				else {
					File oFile = new File(viewPhotoForm.getImage().getFullPath());
					
					ic = ImageIO.read(oFile);
				}
				
				if(ic == null) {
					
					ic = PaUtils.get().getNonValidImage();
					
					writeLog(getMessagesStrs("cantReadTheImageFile") + viewPhotoForm.getImage().getFullPath() + NEXT_ROW,
							null, true, true, false);
				}
			
				viewPhotoForm.getImage().setImageIcon(PaAlgoTransform.getScaledImage(ic,viewPhotoForm.m_widthIcon,
						viewPhotoForm.m_heightIcon) );
				
			
				viewPhotoForm.getIconLabel().setIcon(viewPhotoForm.getImage().getImageIcon());	
				
				viewPhotoForm.getImage().set_scale(PaUtils.get().getSettings().getPhotoScale());
				
			} else {
				
				viewPhotoForm.getIconLabel().setIcon(viewPhotoForm.getImage().getImageIcon());
			}
		} 
		catch (IOException e) {
			
			writeLog("IOException :  can't read image file" + NEXT_ROW, e, true, false, true);
		}
	}

}
