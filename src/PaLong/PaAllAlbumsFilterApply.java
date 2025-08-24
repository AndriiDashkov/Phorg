
package palong;

import static paglobal.PaLog.*;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import pacollection.PaFilterInfo;
import pacollection.PaImageContainer;
import pacollection.PaSubject;
import paglobal.PaLog;
import paglobal.PaUtils;
import paimage.PaViewFiltr;



/**
 * <p>Class PaMoveLongTask uses SwingWorker to start a long task into background. 
 * Here is the operation of coping images from current album to other album</p>
 * @author Andrii Dashkov
 */
public class PaAllAlbumsFilterApply extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

		 ProgressMonitor m_progressMonitor;
		
		 int m_counter = 0;
		 
		 PaImageContainer m_filterContainer;
		 
		 ArrayList<PaSubject> m_list; 
		 
		 Date m_from;
		 
		 Date m_to; 

		 PaFilterInfo m_filter;
		 
		/**
		 * End message is shown in the very end of process
		 */
		 String  m_endMessage = null;
		 
		 /**
		 * <p>Constructor has parameters to start  operation properly</p>
		 * @param progressMonitor - progress monitor
		 * @param filter - filter object to use in procedure
		 */
		 public PaAllAlbumsFilterApply(ProgressMonitor progressMonitor, PaFilterInfo filter,
				 String endMessage) 
		 {

			 m_progressMonitor = progressMonitor;	
			 
			 m_list = filter.getSubjectsList(); 
			 
			 m_from = filter.getDateFrom();
			 
			 m_to = filter.getDateTo(); 
			 
			 m_filter = filter;

			 m_endMessage = endMessage;
			 
			 addPropertyChangeListener(this);	
			 
		 }
		 
        @Override
        public Void doInBackground() 
        {	
        	double h = 0.0;
        	
    		double step = 100.0/(double)PaUtils.get().getMainContainer().get_size();
    		
    		m_filterContainer = new PaImageContainer();
    		
    		try {

    			for (PaImageContainer photoContainer : PaUtils.get().getMainContainer().getList()) {
    				
    				photoContainer.applyFilterToNewContainer(m_filterContainer, m_filter);
    				
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
    		PaViewFiltr viewFilter = new PaViewFiltr(PaUtils.get().getMainWindow(), m_filterContainer,
    				m_filterContainer.getSize());
    		
    		 if(m_endMessage == null) {
    			 
    			 PaLog.writeLogOnly("PaPhotoTabModel.activateFilter for all alboms size = " +
    				m_filterContainer.size() + NEXT_ROW + m_filter.toString(), null);
    			 
    		 }
    		 else {
    			 
    			 writeLog(m_endMessage, null,true, true, false);
    		 }
    		 
    		 viewFilter.setVisible(true);
        	
        }
        
		@Override
		public void propertyChange(PropertyChangeEvent evt) 
		{
			 if ("progress" == evt.getPropertyName()) {
				 
			 	int p = (Integer) evt.getNewValue();
			 	
                m_progressMonitor.setProgress(p);
                
                String message =
                        String.format(getMessagesStrs("filterFromAllAlbomsOperationNote")+" %d%% \n", p);
                
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