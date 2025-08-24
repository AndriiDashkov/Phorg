/**
 * 
 */
package palong;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import pacollection.PaImage;
import pacollection.PaImageContainer;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 *
 */

	/**
	 * <p>Class PaMoveLongTask uses SwingWorker to start a long task into background. 
	 * Here is the operation of coping images from current album to other album</p>
	 */
public class PaCopyToAlbumTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

		 ProgressMonitor m_progressMonitor;
		
		 int m_counter = 0;
		 
		 boolean m_copyFlag;
		 
		 Integer m_resultAlbomId;
		 
		 ArrayList<PaImage> m_list;
		 
		 String m_targetAlbomName;
		
		 /**
		 * <p>Constructor has parameters to start  operation properly</p>
		 * @param progressMonitor - progress monitor
		 * @param list - list of images object that should be copied
		 * @param copyFlag - true in the case when we want to copy images file to standart folder 
		 * @param albomName - target albom's name
		 * @param resultAlbomId - target albom's id
		 */
		 public PaCopyToAlbumTask(ProgressMonitor progressMonitor, 
				 Integer resultAlbomId, ArrayList<PaImage> list, boolean copyFlag,String albomName) 
		 {

			 m_progressMonitor = progressMonitor;
			 
			 m_resultAlbomId = resultAlbomId;
			 
			 m_list = list;
			 
			 m_copyFlag = copyFlag;
			 
			 m_targetAlbomName = albomName;
			 
			 addPropertyChangeListener(this);	
		 }
		 
        @Override
        public Void doInBackground() 
        {	
    
        	double h = 0.0;
        	
        	PaImageContainer cont = PaUtils.get().getMainContainer().getContainer(m_resultAlbomId);
        	
    		PaImage x=null;
    		
    		Iterator<PaImage> it = m_list.iterator();
    		
    		double step = 100.0/(double)m_list.size();
    		
    		try {
	    		while (it.hasNext()) {
	    			
	    			x = it.next();
	    			
	    			PaImage xNew = new PaImage(x);
	    			
	    			//checks name and generate new if the name exists
	    			xNew.setName(cont.getUniquePhotoName(xNew.getName()));
	    			
	    			
	    			if(m_copyFlag) {
	    				
	    				if(cont.add_Image(xNew, true)) { m_counter++;};
	    			}
	    			else {
	    				
	    				boolean flag = true;
	    				
		    			if(x.isLink()) { flag = false; }
		    			
		    			if(cont.add_Image(xNew, flag)) { m_counter++;};
	    			}    			

	    		  	if(h >= 100) { h = 99; } //if we here get 100 % we will jump to finally block after next call of setProgress
	    		  	
					setProgress((int)h);
					
					h += step; 			
					
					if(this.isCancelled()) { break; }
	    		
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
    		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );
    		
    		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
    		
    		writeLog(getMessagesStrs("movedImagesInOtherAlbom")+" " + m_targetAlbomName+" : "+m_counter, null,true,true,true);
        }
        

		@Override
		public void propertyChange(PropertyChangeEvent evt) 
		{
			 if ("progress" == evt.getPropertyName()) {
				 
			 	int p = (Integer) evt.getNewValue();
			 	
                m_progressMonitor.setProgress(p);
                
                String message =
                        String.format(getMessagesStrs("copyInAlbomOperationNote")+" %d%% \n", p);
                
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