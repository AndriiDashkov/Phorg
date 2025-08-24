/**
 * 
 */
package palong;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import pacollection.PaAlbum;
import pacollection.PaAlbumContainer;
import pacollection.PaImage;
import pacollection.PaImageContainer;
import pacollection.PaMainConainer;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;


/**
 * <p>Class PaAddAlbumRecursiveTask uses SwingWorker to start a long task into background. 
 * Here is the operation of recursive creation of albums according to folders structure and adding images</p>
 * @author Andrii Dashkov
 */
public class PaAddAlbumRecursiveTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

		 ProgressMonitor m_progressMonitor;
		 
		 /**<p>folder to load images from</p>*/
		 File  m_dir;
		 
		 /**<p>id of the album where to add images</p>*/
		 int m_albumId;
		 
		 /**<p>the mark of the copy operation: if it is true then image files are copied</p>*/
		 boolean m_isCopy;
		 
		 /**<p>string with errors text</p>*/
		 String m_errorStr;
		 
		 /**<p>counter of errors</p>*/
		 int m_counter = 0; 
		 
		 /**<p>counter of images which were added</p>*/
		 int m_ImageCounter = 0; //
	
		 /**
		 * <p>Constructor has parameters to start  operation properly</p>
		 * @param progressMonitor - progress monitor
		 * @param sourcePath - folder to load images from
		 * @param albomId - id of album where to add images
		 * @param isCopy - flag to control type of add operation; if it is true then image file are copied physically
		 */
		 public PaAddAlbumRecursiveTask(ProgressMonitor progressMonitor, String sourcePath, 
				 int albumId,boolean isCopy) 
		 {

			 m_progressMonitor = progressMonitor;
			 
			 m_dir = new File(sourcePath);
			 
			 m_albumId = albumId;
			 
			 m_isCopy = isCopy;
			 
			 m_errorStr = new String();  
			 
			 addPropertyChangeListener(this);	
		 }
		 
        @Override
        public Void doInBackground() 
        {

        	try {
	        	if (m_dir.exists() && m_dir.isDirectory()) {
	            	
	            	int id = m_albumId;
	            	
	            	PaUtils.get().setCursor(Cursor.DEFAULT_CURSOR,Cursor.WAIT_CURSOR);
	            	
	            	setProgress(1);
	            	
	        	    String[] children = m_dir.list();
	        	   
	        	    if(children.length != 0) {
	        	    	
	        	    	double step = 100.0/(double)children.length;
	        	    	
	        	    	double h = 1.0;
	        	    	
		        	    for (int i=0; i<children.length; i++) {
		        	    	
		        	    	if(h >= 100) { h = 99; } //if we here get 100 % we will jump to finally block after next call of setProgress
		        	    	
							setProgress((int)h);
							
							h += step; 
		        	    	
	        		    	File f = new File(m_dir, children[i]);
	        		    	
	        		    	if (f.isDirectory()) { 
	        		    
	        		    		createAlbumForDir(f,id);
	        		    	}
	        		    	else {
	        		    		
	        		    		if(PaUtils.get().isImageOrVideoFileStr(f.getName())) {
	        		    	
	        		    			addImageToAlbum(f,id);
	        		    		}
	        		    		else {
	        		    			
	        		    			writeLog("Add images operation: not image file - " + f.getAbsolutePath(),
	        		    					null, true, false, true);
	        		    		}
	        		    	}   		    								
							if(this.isCancelled()) { break; }
		        	    }
	        	    }       	    
	        	}
	        	else {
	        		
            		writeLog("Add images operation: not folder or doesn't exist - " + m_dir.getAbsolutePath(),
            				null, true, false, true);
	        	}
        	}
    		finally {    
    			
    			setProgress(100);
    		}
			return null;
        }
        
		 /**
		 * <p>Creates album for folder; considers all inner folders as included albums</p>
		 * @param dir - parent folder to create album
		 * @param parentId - id of parent album for this newly created album
		 */
        public void createAlbumForDir(File dir, int parentId)
        {
        	if (!dir.exists() || !dir.isDirectory()) {
        		
        		writeLog("Add images operation: not folder or doesn't exist - " + dir.getAbsolutePath() + NEXT_ROW, null, true, false, true);
        		
        		return ;
        	}
        	
        	int id = parentId;
        	
            String[] children = dir.list();
            
            if(children.length == 0)  { return; } 
            
            boolean newAlbumCreated = false;
            
            if(PaUtils.get().isImagePresentInside(dir, false)) {
        	
	        	String folderName = dir.getName();
	        	    	
	        	PaAlbum album = new PaAlbum (folderName, "", 
	        			PaUtils.get().getSettings().getStandardFolderPlace(), 
	        			new Date(), folderName);
	        	
	        	album.setParentId(parentId);
	        	
	        	//the control of name duplications
	        	int counter = 1;
	        	
	        	while(PaUtils.get().getAlbumContainer().addAlbum(album) == -1) {
	        		
	        		String name = folderName + "+"+counter;
	        		
	        		album.setName(name);
	        		
	        		album.setFolderName(name);
	        		
	        		++counter;
	
	        	}
	        	
	        	newAlbumCreated = true;

	        	id = album.getId(); //we create new album, we should change id
        	
            }
        	
        	setProgress(1);
        	
    	    double step = 1;
    	    
    	    if(children.length != 0 ) { step = 100.0/((double)children.length); }
    	    
    	    double h = 1.0;
    	    
    	    for (int i=0; i<children.length; ++i) {
    	    	
    	
    	    	if(h >= 100.0) { h = 99.00; } //if we here get 100 % we will jump to finally block after next call of setProgress
    	    	
				setProgress((int)h);
				
				h += step; 
    	    		
		    	File f = new File(dir, children[i]);
		    	
		    	if (f.isDirectory()) { 
		    		
		    		createAlbumForDir(f,id); 
		    	}
		    	else {
		    		
		    		if(newAlbumCreated && PaUtils.get().isImageOrVideoFileStr(f.getName())) {
		    			
		    			addImageToAlbum(f,id);
		    		}
		    		else {
		    			
		    			writeLog("Add images operation: not image file - " + f.getAbsolutePath(), null, true, false, true);
		    		}
		    	}
    	    }	
    	}
        
        /**
         * Removes all empty albums form the main container which are children of the album with id = parentAlbumId
         * @param parentAlbumId - id of album
         */
        @SuppressWarnings("unused")
		private void cleanEmptyAlbums(int parentAlbumId) {
        	
        	 PaAlbumContainer cont = PaUtils.get().getAlbumContainer();
        	 
        	 PaMainConainer contMain = PaUtils.get().getMainContainer();
        	 
        	 ArrayList<Integer>  list = cont.getAllChildAlbumsIds(parentAlbumId);
        	 
        	 if(list.isEmpty()) { return; }
    
    		 ListIterator<Integer> it = list.listIterator();
    		 
    		 while(it.hasNext()) {
    			 
    			 int id = it.next();
    			 
    			 PaImageContainer c = contMain.getContainer(id);
    			 
    			 if(c != null && c.isEmpty()) {
    				 
    				 cont.removeAlbum(id);
    			 }
    		 }      	
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() 
        {	  			
         	if(!m_errorStr.isEmpty()) {
         		
        		String s = "";
        		
        		if(m_counter >= 6){ //if the list is too long, only first 6 items
        			
        			String[] sArray = m_errorStr.split(NEXT_ROW,6);
        			
        			String str1 = sArray[0] + NEXT_ROW + sArray[1] + NEXT_ROW +
        					sArray[2] + NEXT_ROW + sArray[3] + NEXT_ROW +
        					sArray[4] + NEXT_ROW + sArray[5] + NEXT_ROW +
        					"....." + NEXT_ROW;
        			
        			s = getMessagesStrs("errorWhileCopyOperation")+ NEXT_ROW  
            			    + str1 + getMessagesStrs("messagePossibleReasonNoAccess") + NEXT_ROW
            			    + getMessagesStrs("fullListSecExpCanBeFound");
        			
        		}
        		else {
        			
        			s = getMessagesStrs("errorWhileCopyOperation")+ NEXT_ROW  
        			    + m_errorStr + getMessagesStrs("messagePossibleReasonNoAccess");
        		}
        		
        		writeLog("SecurityException object's list (copy images operation):  " + 
        				NEXT_ROW + m_errorStr, null, true, false, true);
        		
        		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),s,
        			    getMessagesStrs("messageErrorCaption"),
        			    JOptionPane.ERROR_MESSAGE);
        	} 	
         	
        	PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
        	
        	PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT));
        	
			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
			
			PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);   	
    		writeLog(getMessagesStrs("numberOfLoadedImagesCaption")+" "+m_ImageCounter, null, true, true, false);
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
		 /**
		 * <p>Add image in file f to album with id</p>
		 * @param f - file to add to album
		 * @param id - album's id (where to add)
		 */
		private void addImageToAlbum(File f,int id)
		{
			m_ImageCounter++;
			
			String sourcePath = f.toPath().getParent().toString();
			
			String standardPath = PaUtils.get().getAlbumContainer().getAlbum(id).getFullStandardPath();

    		if ( m_isCopy ) {
    			
    			Date d = new Date();
    			
    			//only second variant - copy files to standard album folder
    			//(for recursive albums creation we can't save all file in one target folder, so the option
    			// 'save to special folder ' has no sence)
				PaImage photo = new PaImage("photo" + PaMainConainer.get_nextId(), 
						f.getAbsolutePath(), new ArrayList<Integer>(), d);
				
				PaUtils.get().getMainContainer().getContainer(id).addPhoto(photo);
			
				if(PaUtils.isSamePathes(sourcePath,standardPath)) { 
					
					//files are in the standard folder already - so they are not links
					photo.setLink(false); 	
					
				} else {
					
					photo.setLink(false);
					
					String oldPath =  photo.getFullPath();
					
					try {
						
						photo.changePath(standardPath);
				
					}
					catch(IOException | SecurityException e ) {
						
						writeLog("IOException | SecurityException : " + oldPath + NEXT_ROW, e, true, false, true);
						
						m_errorStr += oldPath + NEXT_ROW; //collects all error pathes
						
						m_counter++;
						
				  		photo.setLink(true);
				  		
				  		photo.setFullPath(oldPath);
					}
				}	
    		}
    		else {	//first variant - only links	
    			
    			Date d = new Date();

				PaImage photo = new PaImage("photo" + PaMainConainer.get_nextId(), 
						f.getAbsolutePath(), new ArrayList<Integer>(), d);
				
				if(PaUtils.isSamePathes(sourcePath,standardPath)) { 
					
					photo.setLink(false); 
				}
				
				PaUtils.get().getMainContainer().getContainer(id).addPhoto(photo);
    		}
		}	
}
		