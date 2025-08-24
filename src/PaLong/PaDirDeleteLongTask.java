
package palong;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import paglobal.PaUtils;



/**
 * @author Andrii Dashkov
 *
 */

	/**
	 * <p>Class PaDirDeleteLongTask uses SwingWorker to start a long task into background. 
	 * Here is the operation of folder deleting with all subfolders</p>
	 */
public class PaDirDeleteLongTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

		 ProgressMonitor m_progressMonitor;
		 
		 File  m_dir;
		 
		 int m_counter = 0;
		 
		 boolean  m_rootFlag;
		
		 /**
		 * <p>Constructor has parameters to start  operation properly</p>
		 * @param progressMonitor - progress monitor
		 * @param dir - folder to delete
		 * @param deleteRootFlag - determines to delete root folder dir or not
		 */
		 public PaDirDeleteLongTask(ProgressMonitor progressMonitor, File dir, boolean deleteRootFlag) 
		 {

			 m_progressMonitor = progressMonitor;
			 
			 m_dir = dir;
			 
			 m_rootFlag = deleteRootFlag;
			 
			 addPropertyChangeListener(this);	
		 }
		 
        @Override
        public Void doInBackground() 
        {	
    
        	String path=new String();
        	
        	String str = "";
        	
        	int m_counter  = 0;
        	
        	double h = 0.0;
        	
        	if (m_dir.isDirectory()) {
        		
        		try {
        			
	    	    	String[] children = m_dir.list();
	    	    	
	    	    	if(children.length != 0) {
	    	    		
		    	    	double step = 100.0/(double)children.length;
		    	    	
		    	    	for (int i=0; i < children.length; i++) {
		    	    		
		    		    	File f = new File(m_dir, children[i]);
		    		    	
		    		    	str += deleteNotEmptyDir(f);
		    		    	
		    		    	if(h >= 100) { h = 99; } //if we here get 100 % we will jump to finally block after next call of setProgress
		    		    	
							setProgress((int)h);
							
							h += step; 			
							
							if(this.isCancelled()) { break; }		    		    	
		    	    	}
	    	    	}
	    	    	if(m_rootFlag) {
	    	    		
		    	    	try {
		    	    		
		    		    	path = m_dir.getPath();
		    		    	
		    		    	Files.delete(m_dir.toPath());
		    		    	
		    	    	}
		    		    catch(IOException | SecurityException exp) {
		    		    	
		    		    	++m_counter;
		    		    	
				    		str += path + PaUtils.NEXT_ROW;
		    		    }
	    	    	}
        		}
        		finally {     		
        			
        			setProgress(100);
        		}
        	} 
        	else {
        		if(m_rootFlag) {
        			
	        		try {
	        			
	    	    		path = m_dir.getPath();
	    	    		
	    	    		m_dir.delete();
	    	    		
	        		}
	        		catch(SecurityException exp) {
	        		
	        			++m_counter;
	        			
	        			str += path + PaUtils.NEXT_ROW;
	        		}
        		}
        		setProgress(100);
        	}
        	if(m_counter != 0) {
        		
        		String s = "";
        		
        		if(m_counter >= 6){ //if the list is too long, only first 6 items
        			
        			String[] sArray = str.split(PaUtils.NEXT_ROW,6);
        			
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
        			    + str + getMessagesStrs("messagePossibleReasonNoAccess");
        		}
        		
        		writeLog("SecurityException object's list (folder delete operation):  " + 
        				PaUtils.NEXT_ROW + str, null, true, false, true);
        		
        		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),s,
        			    getMessagesStrs("messageErrorCaption"),
        			    JOptionPane.ERROR_MESSAGE);
        	}

			return null;
        }
        
		 /**
		 * <p>Base delete operation</p>
		 * @return string with security exceptions if they occur
		 * @param dir - folder to delete
		 */
        public String deleteNotEmptyDir(File dir)
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
        	
    		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);    		
        }
        

		@Override
		public void propertyChange(PropertyChangeEvent evt) 
		{
			 if ("progress" == evt.getPropertyName()) {
				 
			 	int p = (Integer) evt.getNewValue();
			 	
                m_progressMonitor.setProgress(p);
                
                String message =
                        String.format(getMessagesStrs("deleteFilesOperationNote")+" %d%% \n", p);
                
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
