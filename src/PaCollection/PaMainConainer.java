package PaCollection;

import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;

/**
 * The class represents the main container, which contains all albums
 * @author avd
 *
 */
public class PaMainConainer {
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SAVE_EVENT, this, "saveMainContainer");
	}
	
	private ArrayList<PaImageContainer> m_mainContainer;
	
	private static int next_IdPhoto = 0;
	
	/**
	 * id of current loaded album
	 */
	private int m_currentId = -1;
	

	public PaMainConainer() {
		
		m_mainContainer = new ArrayList<PaImageContainer> ();
		

	}


	public static int get_nextId () {
		
		return next_IdPhoto;
	}
	
	public static int set_nextIdPlus () {
		
		return next_IdPhoto++;
	}
	
	//setting lastId while loading XML file
	public static void set_load_nextId (int lastId) {
		
		next_IdPhoto = lastId;
	}
	
	
	//return the reference for the main application container
	public ArrayList<PaImageContainer> getList() {
		
		return m_mainContainer;
	}
	

	public boolean add_photoContainer (PaImageContainer photoContainer) {
		
		m_mainContainer.add(photoContainer);
		
		return true;
	}

	/**
	 * Removes container
	 * @param Id_albom - id of albom to delete
	 */
	public void removeContainer (int Id_albom) /* throws CloneNotSupportedException*/
	{
		boolean flag = isCurrent(Id_albom);
		
		PaImageContainer photoContainer = getContainer(Id_albom);
		
		if(photoContainer != null) {
			
				String s = PaUtils.get().getAlbomXMLFilePath(Id_albom);
		
				try {
					if (!Files.deleteIfExists(Paths.get(s))) {
						
						writeLog("IOException: Can't delete xml file "+ s + NEXT_ROW 
								+"while removing photocontainer operation", null, true, false, false) ;
						
					}
					
				} catch (IOException e) {
					
					writeLog("IOException: Can't delete xml file "+ s + NEXT_ROW 
							+"while removing photocontainer operation", null, true, false, false) ;
				}	
				
				photoContainer.deleteSpecialLoadImages(true);
				
				m_mainContainer.remove(photoContainer);
				
				if(flag) {//if albom is loaded we should skip the current container
					
					m_currentId = -1;
				}
		
		}

	}
	
	/**
	 * Loads all albums in the main container
	 * @param albomContainer - alboms container
	 */
	public void loadMainContainer (PaAlbumContainer albomContainer) {

		PaImageContainer photoContainer;
		
		Iterator<PaAlbum> iter = albomContainer.iterator();
		
		PaAlbum x;

 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			int albomId = x.getId();
 			
 			m_mainContainer.add(photoContainer = new PaImageContainer(albomId));
 			
 			String configPhoto = PaUtils.get().getPhotosXMLPath() + albomId + ".xml";
 			
 			try {
 				
				photoContainer.loadImages(configPhoto);
				
			} catch (IOException e) {
				
				writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
			}

 		}			
 	}
	

	public void saveMainContainer (PaEvent eventSave) {
		
		if ( eventSave.getEventType() != PaEventDispatcher.SAVE_EVENT ) { return; }
		
		int currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
		
		try {
		
			PaImageContainer x = null;
			
			Iterator<PaImageContainer> iter = m_mainContainer.iterator();		

			while (iter.hasNext ()) {
				
				x = iter.next();
				
				int albomId = x.getId();
					
				x.saveImages(albomId);
				
			}
			
		} catch (FileNotFoundException e) {

			writeLog("FileNotFoundException  : " + NEXT_ROW, e, true, false, true);
			
		} catch (XMLStreamException e) {

			writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
			
		}
		finally {
			
			PaUtils.get().resetCursor(currentCursorType);	
		}
		
	}
	
	public int get_size () {
		
		return m_mainContainer.size();
	}
	
	/**
	 * 
	 * @param Id - of album or container
	 * @return the container according to Id
	 */
	public PaImageContainer getContainer (int Id) {
		
		PaImageContainer x;
		
		Iterator<PaImageContainer> iter = m_mainContainer.iterator();
		
		while (iter.hasNext()) {
			
			x = iter.next();
			
			if (x.getId() == Id) {
				
				return x; 
			}
		}
		
		return null;
	}
	

	
	public int getIndex(PaImageContainer c) {
		
		return m_mainContainer.indexOf(c);
		
	}
	
	public PaImageContainer getPhotoContainerForPhoto (int id) {
		
		PaImageContainer x;
		
		Iterator<PaImageContainer> iter = m_mainContainer.iterator();
		
		while (iter.hasNext()) {
			
			x = iter.next();

			if ( x.findId(id) ) {
				
				return x; 
			}
		}
		
		return null;
	}
	
	
	public void setCurrentLoadedContainer (PaImageContainer photoContainer) throws CloneNotSupportedException {

		if(photoContainer == null ) {
			
			m_currentId = -1; 
		}
		else {
			
			m_currentId = photoContainer.getId();
		}
			
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );
	}
	/**
	 * 	
	 * @return the current (loaded into view panel) container or null
	 */
	public PaImageContainer getCurrentContainer() 
	{
		//return m_selected;
		if(m_currentId == -1 ) return null;

		return getContainer (m_currentId);
		
	}
	
	/**
	 * 	
	 * @return the current (loaded into view panel) container id 
	 */
	public int getCurrentContainerId() 
	{
		return m_currentId;	
	}

	
	/**
	 * 	
	 * Removes all inclusion of the images with definite path from all albums
	 * @param list - the list for images fro removing
	 * @param deleteImageFromDisk if true, deletes the images from the disk
	 */
	public int removeImageFromAllContainers ( ArrayList<PaImage> list, boolean deleteImageFromDisk )  
	{
		if ( list.size() == 0 ) return 0;
		
		int counter = 0;
		
		Iterator<PaImage> it = list.iterator();
		
		while (it.hasNext()) {
		
			String path = it.next().getFullPath();
			
			Iterator<PaImageContainer> iter = m_mainContainer.iterator();
			
			while (iter.hasNext()) {
				
				counter += iter.next().removeAllImagesForPath(path, deleteImageFromDisk);
				
			}
		
		}
		
		return counter;
	}
	
	/**
	 * <p>Clears containers with ids</p>
	 * @param ids - array list of ids of containers to clear
	 */
	public void clearContainers(ArrayList<Integer> ids) 
	{	
		if(ids.isEmpty()) return;
		
		for(int i: ids) {
			
			PaImageContainer c = getContainer(i);
			
			if(c != null ) {
				
				c.removeAllPhotos();
				
				c.deleteSpecialLoadImages(false);
				
				if(isCurrent(i)) {
					
					c.refreshSelectedContrainer();
					
					PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );			
				}	
			}
		}
		
		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
	}
	
	/**
	 * <p>Checks if id is current container id</p>
	 * @param idAlbum - id to check
	 */
	public boolean isCurrent(int idAlbum)
	{		
		if(m_currentId != -1  && idAlbum == m_currentId) { return true; }
		
		return false;
	}
	
	/**
	 * <p>Checks if there is id of loaded album among the list</p>
	 * @param idAlboms - list of ids
	 */
	public boolean hasCurrent(ArrayList<Integer> idsAlboms)
	{
		if(m_currentId == -1) { return false; }
		
	
		for(int id: idsAlboms) {
			
			if(id == m_currentId) { return true; }	
			
		}
		
		return false;
	}
	
	/**
	 * <p>removes album files which are in the standard folder (operation 'remove albom')</p>
	 * @param idAlbom - id's of album to be removed
	 * @param newPath - new albom's location
	 * @param oldPath - old albom's location
	 * @param oldFolderName - name of old folder name (need for boost icons moving)
	 */
	public void reallocImages(int idAlbom, String newPath, String oldFolderName,String oldPath,boolean deleteOldStandardFolder)
	{		
		String name = new String();
		
		File f = new File(newPath);
		
		if ( f != null && ! f.exists() ) {
			
			try {
				
				name = newPath;
				
				f.mkdirs();
				
			}
			catch(SecurityException exp  ) {
				
		    	Object[] options = {getMessagesStrs("skipButtonCaption"),
		    			getMessagesStrs("abortButtonCaption")};
		    	
		    	JOptionPane.showOptionDialog(PaUtils.get().getMainWindow(),  
		    			getMessagesStrs("newFolderCreationError")
    					+ NEXT_ROW + name, getMessagesStrs("messageErrorCaption"),
    					JOptionPane.YES_NO_CANCEL_OPTION,
    					JOptionPane.ERROR_MESSAGE,
    					null,options,options[0]);
		    	
		
		    	return;
			}
		}
			
 		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
				getMessagesStrs("reallocImagesLongTaskCaption"),
				getMessagesStrs("reallocImagesLongTask"), 0, 100);
		
		progressMonitor.setMillisToDecideToPopup(0);
		
		progressMonitor.setMillisToPopup(0);
		
		PaUtils.get().setCursor(Cursor.DEFAULT_CURSOR,Cursor.WAIT_CURSOR);
		

		PaChangePathLongTask ts = new PaChangePathLongTask(progressMonitor, idAlbom,newPath,
				oldFolderName,oldPath,deleteOldStandardFolder);
		
		ts.execute();

	}
	
	/**
	 * <p>Class PaChangePathLongTask uses SwingWorker to start a long task into background. 
	 * Here is the operation of folder reallocation</p>
	 */
	class PaChangePathLongTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
	 {

			 ProgressMonitor m_progressMonitor;
			 
			 int  m_id;
			 
			 String m_newPath;
			 
			 String m_oldPath;
			 
			 int m_counter = 0; //counter of error messages
			 
			 boolean m_deleteOldFolder;
			 
			 String m_str = ""; //string for error messages collection
			 
			 String m_oldBoostIconFolderName = null;
			 
			 boolean m_hasInnerFolders = false;
			 /**
			 * <p>Constructor has parameters to start  operation properly</p>
			 * @param progressMonitor - progress monitor
			 * @param id - albom's id which has to be moved
			 * @param newPath - new albom's path
			 * @param oldPath - old albom's path
			 * @param m_oldFolderName - is used for the boost icons moving
			 */
			 public PaChangePathLongTask(ProgressMonitor progressMonitor, int id, String newPath,
					 String oldFolderName, String oldPath, boolean deleteOldFolder) 
			 {
				 m_progressMonitor = progressMonitor;
				 
				 m_id = id;
				 
				 m_newPath = newPath;
				 
				 m_oldPath = oldPath;
				 
				 m_oldBoostIconFolderName = oldFolderName;
				 
				 m_deleteOldFolder = deleteOldFolder;
				 
				 addPropertyChangeListener(this);	
			 }
			 
	        @Override
	        public Void doInBackground() 
	        {	
	        	
	        	String name;
	        	
	        	PaImage x = null;
	        	
	        	double h = 0.0;
	        	
	        	double step = 0.0;
	        	
	        	int counter =0;
	        	
	        	int maxProgressValue = 100;
	        	
	        	if(m_deleteOldFolder) { maxProgressValue = 50;}
	        	
	        	PaImageContainer p = null;
	        	
	        	try {
	        		
	        		p = getContainer(m_id);
	        		
	        		if(p == null) {
	        			
	        			writeLog(getMessagesStrs("noContainerForRemovOperation"),null,true,true,true);
	        			
	        			writeLogOnly("Change path operation error: image container is null for id = : " + m_id, null);
	        			
	        			return null;
	        		}
	        		if(p.getSize() == 0) {
	        			
	        			writeLog(getMessagesStrs("noImagesForRemovOperation"),null,true,true,true);
	        			
	        			writeLogOnly("Change path operation error: image container is  empty  id = : " + m_id, null);
	        			
	        			return null;
	        		}
	        		        		
		        	Iterator<PaImage> itr = p.iterator();
		        	
		        	step = (double)maxProgressValue/(double)p.getSize();
		        	
		        	while (itr.hasNext()) {
		        		
		        		x=itr.next();
		        		
		        		name = x.getFullPath();
		        		
		        		try {
		        			
		        			x.changePathWithOldPathCheck(m_newPath,m_oldPath);
		        			
		    		    	if(h >= maxProgressValue) { h = maxProgressValue-1; } //if we here get 100 % we will jump to finally block after next call of setProgress
		    		    	
		    		    	if(h < 1) h = 1;
		    		    	
							setProgress((int)h);
							
							h += step; 		
							
							if(this.isCancelled()) { return null; }	
							
		        		} catch (IOException | SecurityException exp) {
		        			
		        			writeLogOnly("Removing operation of albom with id = "+m_id+" failed", exp);
		        			
		        	    	Object[] options1 = {getMessagesStrs("skipButtonCaption"),
		        	    			
		                    getMessagesStrs("abortButtonCaption")};
		        	    	
		        	    	int n1 = JOptionPane.showOptionDialog(PaUtils.get().getMainWindow(),    
		        	    			getMessagesStrs("copyFileErrorMessage")
		        					+ NEXT_ROW + name, getMessagesStrs("messageErrorCaption"),
		        					JOptionPane.YES_NO_CANCEL_OPTION,
		        					JOptionPane.ERROR_MESSAGE,
		        					null,options1,options1[0]);
		        	    	
		        	    	if ( n1 == 1) {
		        	    		
		        	    		return null; 
		        	    		
		        	    	}
		        			
		        		}
		        	
		        		++counter;
		        	} 	
		        	
		        	if(m_deleteOldFolder){
		  	        
		        		String path=new String();
		        		
		            	File dir = new File(m_oldPath);
		            	
		            	if (dir.isDirectory()) {
		            		
			    	    	String[] children = dir.list();
			    	    	
			    	    	if(children.length != 0) {
			    	    		
				    	    	step = 50.0/(double)children.length;
				    	    	
				    	    	for (int i=0; i < children.length; i++) {
				    	    		
				    		    	File f = new File(dir, children[i]);
				    		    	
				    		    	if(f.exists() && !f.isDirectory()) {
				    		    		
					    		    	m_str += deleteNotEmptyDir(f);
					    		    	
					    		    	if(h >= 100) { h = 99; } //if we here get 100 % we will jump to finally block after next call of setProgress
					    		    	
										setProgress((int)h);
										
										h += step; 			
										
										if(this.isCancelled()) { break; }	
				    		    	}
				    		    	else if(f.isDirectory()){
				    		    		
				    		    		m_hasInnerFolders = true;
				    		    	}
				    	    	}
			    	    	} 	   
		            	}
		              
		        		try {
		        			if(!m_hasInnerFolders) {
		        				
			    	        	path = dir.getPath();
			    	        	
			    	    		Files.delete(dir.toPath());
		        			}
		        			else {
		        				
		        				writeLog(getMessagesStrs("standFolderHasInnerFolder"),null,true,true,false);
		        			}
		    	    	
		        		}
		        		catch(SecurityException | IOException exp) {
		        			
		        			++m_counter;
		        			
		        			m_str += path + PaUtils.NEXT_ROW;
		        		}
		        	}
	        	}
	        	finally {
	        		
	        		writeLog(getMessagesStrs("filesWereRemovedWhileOperation")+" "+counter,null,true,true,true);
	        		writeLogOnly("Removing operation of albom with id = "+m_id+" has been finished, " + counter+" files were removed", null);
	        		
	            	setProgress(100);
	        	}
	        	
				return null;
	        }
	        
	
			 /**
			 * <p>Base delete operation</p>
			 * @return string with security exceptions if they occur
			 * @param dir - folder to delete
			 */
		    private String deleteNotEmptyDir(File dir)
		    {
		    	String path=new String();
		    	
		    	String str = "";
		   	
		    	if (dir.isDirectory()) {
		    		
			    	String[] children = dir.list();
			    	
			    	for (int i=0; i<children.length; i++) {
			    		
				    	File f = new File(dir, children[i]);
				    	
				    	str += deleteNotEmptyDir(f);				    	
			    	}
			    	try {
			    		
				    	path = dir.getPath();
				    	
				    	Files.delete(dir.toPath());
			    	}
				    catch(IOException | SecurityException exp) {
				    	
				    	++m_counter;
				    	
			    		str += path + PaUtils.NEXT_ROW;
				    }
		    	} 
		    	else {
		    		
		    		try {
		    			
			    		path = dir.getPath();
			    		
			    		Files.delete(dir.toPath());
		    		}
		    		catch(IOException | SecurityException exp) {
		    			
		    			++m_counter;
		    			
						str += path + PaUtils.NEXT_ROW;
		    		}
		    	}
		    	return str;
			}
			
	 
	        /*
	         * Executed in event dispatching thread
	         */
	        @Override
	        public void done() 
	        {
	        	
	        	if(isCurrent(m_id)) {
	        		
	        		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );
	        	}
	        	
	        	PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SAVE_EVENT) );
	        	
	    		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR); 
	    		
            	//here all the error print
            	if(m_counter != 0) {
            		
            		String s = "";
            		
            		if(m_counter >= 6){ //if the list is too long, only first 6 items
            			
            			String[] sArray = m_str.split(PaUtils.NEXT_ROW,6);
            			
            			String str1 = sArray[0] + PaUtils.NEXT_ROW + sArray[1] + PaUtils.NEXT_ROW +
            					sArray[2] + PaUtils.NEXT_ROW + sArray[3] + PaUtils.NEXT_ROW +
            					sArray[4] + PaUtils.NEXT_ROW + sArray[5] + PaUtils.NEXT_ROW +
            					"....." + PaUtils.NEXT_ROW;
            			
            			s = getMessagesStrs("messageDeleteOperationImpossible")+ PaUtils.NEXT_ROW  
                			    + str1 + getMessagesStrs("messagePossibleReasonNoAccess") + PaUtils.NEXT_ROW
                			    + getMessagesStrs("fullListSecExpCanBeFound");
            		}
            		else {
            			
            			s = getMessagesStrs("messageDeleteOperationImpossible")+ PaUtils.NEXT_ROW  
            			    + m_str + getMessagesStrs("messagePossibleReasonNoAccess");
            		}
            		
            		writeLog("SecurityException object's list (folder delete operation):  " + 
            				PaUtils.NEXT_ROW + m_str, null, true, false, true);
            		
            		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),s,
            			    getMessagesStrs("messageErrorCaption"),
            			    JOptionPane.ERROR_MESSAGE);
            		
            	}
            	if(m_hasInnerFolders) {
            		
        			JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),getMessagesStrs("standFolderHasInnerFolder"),
            			    getMessagesStrs("messageInfoCaption"),
            			    JOptionPane.INFORMATION_MESSAGE);
        		}
	        }
	        

			@Override
			public void propertyChange(PropertyChangeEvent evt) 
			{
				 if ("progress" == evt.getPropertyName()) {
					 
				 	int p = (Integer) evt.getNewValue();
				 	
	                m_progressMonitor.setProgress(p);
	                
	                String message =
	                        String.format(getMessagesStrs("reallocImagesLongTask")+" %d%% \n", p);
	                
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
}
