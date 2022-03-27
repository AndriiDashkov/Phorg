/**
 * 
 */
package PaLong;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.concatPathName;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import PaCollection.PaMainConainer;
import PaCollection.PaImage;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;

/**
 * <p>Class LongTaskAddImages uses SwingWorker to start a long task into background. 
 * Here is the operation of group adding of images in album</p>
 *  @author avd
 */
 public class PaAddImagesToAlbumTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor;
	 
	 String m_sourcePath;
	 
	 String m_targetPath;
	 
	 File[] m_files;
	 
	 boolean m_copyFlag;
	 
	 int counter;
	 
	 int m_currentCursorType;
	 
	 int m_albumId;

	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param sourcePath - full path to folder where are images to load
	 * @param targetPath - full path to the folder where album is; in the case of copyFlag files will be loaded to it
	 * @param files - names of files to load
	 * @param copyFlag - flag to indicate that user has chosen to copy images physically in the target folder
	 */
	 public PaAddImagesToAlbumTask(ProgressMonitor progressMonitor, String sourcePath, String targetPath,
			 File[] files, boolean copyFlag, int albomId) 
	 {
		 m_progressMonitor = progressMonitor;
		 
		 m_sourcePath=sourcePath;
		 
		 m_targetPath = targetPath;
		 
		 m_files = files;
		 
		 m_copyFlag = copyFlag;
		 
		 m_albumId = albomId;
		 
		 addPropertyChangeListener(this);	
	 }
	 
    @Override
    public Void doInBackground() 
    {
    
		counter = 1;
		
		int lng = m_files.length;
		
		m_currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
		
		String standartPath = PaUtils.get().getAlbumContainer().getAlbum(m_albumId).getFullStandardPath();
		
		setProgress(1);	    			  
	
		try {		
    		if ( m_copyFlag ) {
    			
    			Date d = new Date();
    			
    			if ( ! m_targetPath.isEmpty() ) { //third variant - copy files to choosed folder
    				
    				for(File f : m_files) {
    					
    					PaImage photo = new PaImage("photo" + PaMainConainer.get_nextId(), concatPathName(m_sourcePath, f.getName()), new ArrayList<Integer>(), d);
    					
    					if(PaUtils.isSamePathes(m_sourcePath,m_targetPath)) {//copy no sense - files are already there
    						
    						if(PaUtils.isSamePathes(standartPath,m_targetPath)) { 
    							
    							//files in the standard folder - so they are not links
    							photo.setLink(false); 
							}
    						else { photo.setLink(true);  }
    						
    						PaUtils.get().getMainContainer().getContainer(m_albumId).addPhoto(photo);
    						
    					}
    					else {
    						
    						PaUtils.get().getMainContainer().getContainer(m_albumId).addCopyImage (photo, m_targetPath);
    					}
    			
    					if(this.isCancelled()) { break; }
    					
    					int pr = counter*99/lng;
    					
    					if(pr < 1) pr = 1;
    					
    					if(pr >= 100) pr = 99;
    					
    					setProgress(pr);	    
    					
    					++counter;
    				}
    				if(!this.isCancelled()) setProgress(100);    				
    			}
    			else { //second variant - copy files to standard album folder
    				for(File f : m_files) {
    					
    					PaImage photo = new PaImage("photo" + PaMainConainer.get_nextId(), concatPathName(m_sourcePath, f.getName()), new ArrayList<Integer>(), d);
    					
    					if(PaUtils.isSamePathes(m_sourcePath,standartPath)) { 
    						
    						//files are in the standard folder already - so they are not links
							photo.setLink(false); 
							
							PaUtils.get().getMainContainer().getContainer(m_albumId).addPhoto ( photo); 
							
						} else {
							
							PaUtils.get().getMainContainer().getContainer(m_albumId).add_Image(photo, true);
							
						}
    					
    					if(this.isCancelled()) { break; }
    					
    					int pr = counter*99/lng;
    					
    					if(pr < 1) pr = 1;
    					
    					if(pr >= 100) pr = 99;
    					
    					setProgress(pr);
    					
    					++counter;
    				}
    				
    				if(!this.isCancelled()) setProgress(100);  				
    			}
    		}
    		else {	//first variant - only links	
    			
    			Date d = new Date();
    			
    			for(File f : m_files) {
    				
    				PaImage photo = new PaImage("photo" + PaMainConainer.get_nextId(), concatPathName(m_sourcePath, f.getName()), new ArrayList<Integer>(), d);
    				
    				if(PaUtils.isSamePathes(m_sourcePath,standartPath)) { 
    					
    					photo.setLink(false); 
    				}
    				
    				PaUtils.get().getMainContainer().getContainer(m_albumId).addPhoto(photo);
    				
    				if(this.isCancelled()) { break; }
    				
    				int pr = counter*99/lng;
					
    				if(pr < 1) pr = 1;
					
    				if(pr >= 100) pr = 99;
					
    				setProgress(pr);
    				
    				++counter;
    			}
    			
    			if(!this.isCancelled()) setProgress(100);
    		}
		}
		finally {		
			
			setProgress(100);
		}
		return null;
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() 
    {
    	PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
    	
		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
		
		PaUtils.get().resetCursor(m_currentCursorType);   		
		
		writeLog(getMessagesStrs("numberOfLoadedImagesCaption")+" "+counter, null, true, true, false);
    }
    

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		 if ("progress" == evt.getPropertyName()) {
			 
		 	int p = (Integer) evt.getNewValue();
		 	
            m_progressMonitor.setProgress(p);
            
            String message =
                    String.format(getMessagesStrs("loadedImagesOperationNote")+" %d%% \n", p);
            
            m_progressMonitor.setNote(message);
      
            if (m_progressMonitor.isCanceled() || this.isDone()) {
            	
                Toolkit.getDefaultToolkit().beep();
                
                if (m_progressMonitor.isCanceled()) {
                	
                    this.cancel(true);
    	
                } 
            }
        }			
	}
}
 
