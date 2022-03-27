/**
 * 
 */
package PaLong;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.concatPathName;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import static PaGlobal.PaUtils.NEXT_ROW;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaDialogs.PaAlbumProblemsDialog;
import PaGlobal.PaUtils;


/**
 * <p>Class PaAlbomImageControl uses SwingWorker to start a long task into background. 
 * Here is the operation of albom's check (image and boost file existence, so on)
 * All results are saved in special table</p>
 *  @author avd
 */
 public class PaAlbumImageControl extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor;
	 
	 /**
	  * Problems counter
	  */
	 int m_counter;
	 
	 int m_currentCursorType;
	 
	 int m_albumId;
	 
	 int m_prCounter = 0;
	 
	 String[][] m_data = null;
	 
	 String m_albumName = null;
	 
	 /**
	  * table for results view
	  */
	 JTable m_table;
	 
	 /**
	  * a list for saving results of the check
	  */
	 ArrayList<Info> m_list = new ArrayList<Info>();

	 /**
	 * <p>Constructor has parameters to start the operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param id - id of the album which is chosen for check operation
	 * @param albumName - album name (it is used just for information)
	 */
	 public PaAlbumImageControl(ProgressMonitor progressMonitor, int id, String albumName) 
	 {
		 m_progressMonitor = progressMonitor;
		 
		 m_albumId  = id;
		 
		 m_albumName = albumName;
		 
		 addPropertyChangeListener(this);	
	 }
	 
    @Override
    public Void doInBackground() 
    {
		m_counter = 1;
		
		m_currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
		
		PaImageContainer cont = PaUtils.get().getMainContainer().getContainer(m_albumId);
		
		Iterator<PaImage> it = cont.iterator();
		
		int lng = cont.getSize();
		
		String noBaseFileMessage = getMessagesStrs("noBaseFileNotification");
		
		String noBoostFileMessage = getMessagesStrs("noBoostFileNotification");
		
		String boostBaseDifferenceMessage = getMessagesStrs("boostBaseFileDifNotification");
		
		String cantDetMessage = getMessagesStrs("exeptionWhilCheckNotification");
		
		try{
			while (it.hasNext()) { 
				
				PaImage im = it.next(); 
				
				String sPath = im.getFullPath();
				
				String sName = im.getName();
				
				if(this.isCancelled()) { break; }
				
				int pr = m_counter*99/lng;
				
				if(pr < 1) pr = 1;
				
				if(pr >= 100) pr = 99;
				
				setProgress(pr);	  
				
				++m_counter;
				
				String fullBoostImagePath = concatPathName( PaUtils.get().getPathToBoostIcons(cont),
						new Integer(im.getId()).toString()+".jpeg");
				
				File f = new File(sPath);
				
				if(!f.isFile()) {//control of existence of the image file
					
					m_list.add(new Info(sName,sPath,noBaseFileMessage));
					
					writeLog("The base image file doesn't exist : " + sName + " path :" + sPath, 
							null, true, true, false);
				}
				else {
					
					File fBoost = new File(fullBoostImagePath);
					
					if(!fBoost.isFile()) {//control of existence of the boost of image file
						
						m_list.add(new Info(sName,sPath,noBoostFileMessage));
						
						writeLog("The boost image file doesn't exist : " + sName + " path :" +
								fullBoostImagePath, null, true, true, false);
					}
					else {
						
						try {
							
							BasicFileAttributes attrs;
							
								attrs = Files.readAttributes(f.toPath(), 
										BasicFileAttributes.class);
							
							FileTime dateBaseFile = attrs.lastModifiedTime();
							
							attrs = Files.readAttributes(fBoost.toPath(), 
									BasicFileAttributes.class);
							
							FileTime dateBoost = attrs.lastModifiedTime();
							
							//control of the difference between modification date of image and boosy files
							//if the last modification date of the image file is late of the modification of
							//the boost file then the situation is possible when boost icon dosn't have right image 
							if(dateBoost.compareTo(dateBaseFile) < 0) {
								
								m_list.add(new Info(sName,sPath,boostBaseDifferenceMessage));
								
								writeLog("Difference between dates of image and boost files; base image : " + 
										sName + " path :" + sPath + "modified : " + dateBaseFile.toString() + NEXT_ROW 
										+ "boost image :" + fullBoostImagePath + "modified : " + dateBoost.toString(), 
										null, true, true, false);
								
							}
						} catch (IOException e) {
							
							m_list.add(new Info(sName,sPath,cantDetMessage));
							
							writeLog("IOException: "+ cantDetMessage+" image : "+ sName + " path :" 
									+ sPath, null, true, true, false);
						}
					}
				}
			}
		}
		finally {	
			
			setProgress(100);
		}
		return null;
    }
    
    /**
     * Gets the array which is the data for result table
     * @return  the array for table initiation: the table has 3 columns - name, path and problem's description
     */
    private String[][] getArray() {
    	
    	if(m_list.isEmpty()) return null;
    	
    	m_prCounter = m_list.size();
    	
    	String[][] s = new String[m_list.size()][3];
    	
    	int i = 0;
    	
    	for(Info in : m_list) {
    		
    		s[i][0] = in.name;
    		
    		s[i][1] = in.path;
    		
    		s[i][2] = in.problem;
    		
    		++i;
    	}
    	return s;
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() 
    {
    	PaUtils.get().resetCursor(m_currentCursorType);
    	
    	m_data = getArray();
    	
    	if(m_data == null) {
    		
    		JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("noProblemsInAlbomMessage"),
				    getMessagesStrs("messageInfoCaption"),
				    JOptionPane.INFORMATION_MESSAGE);
    	}
    	else {
    		
    		PaUtils.get().resetCursor(m_currentCursorType);   		
    		
    		String[] cNames = {getGuiStrs("nameImageColumnInfName"),
    				getGuiStrs("pathImageColumnInfName"),getGuiStrs("problemImageColumnInfName")};
    		
    		m_table = new JTable(m_data,cNames);
    		
    		PaAlbumProblemsDialog d = new PaAlbumProblemsDialog(null,m_table,m_albumName,m_prCounter);
    		
    		d.setVisible(true);
    	}
    
    }
    

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		 if ("progress" == evt.getPropertyName()) {
			 
		 	int p = (Integer) evt.getNewValue();
		 	
            m_progressMonitor.setProgress(p);
            
            String message =
                    String.format(getMessagesStrs("checkAlbomOperationNote")+" %d%% \n", p);
            
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
	 * Holder for row of the info table
	 * @author avd
	 *
	 */
	private class Info {
		
		public String name;
		
		public String path;
		
		public String problem;
		
		public Info(String nm, String pth, String pr) {
			
			name = nm;
			
			path = pth;
			
			problem = pr;
		}
	}
}
