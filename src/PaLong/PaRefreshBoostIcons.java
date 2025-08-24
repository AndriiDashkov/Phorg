
package palong;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import pacollection.PaImage;
import paglobal.PaUtils;



/**
 * <p>This class  uses SwingWorker to start a long task into background. 
 * Here is the operation of refresh of boost icons in album</p>
 * @author Andrii Dashkov
 */
 public class PaRefreshBoostIcons extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor;
	 
	 int counter;
	 
	 int m_currentCursorType;

	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 */
	 public PaRefreshBoostIcons(ProgressMonitor progressMonitor) 
	 
	 {
		 m_progressMonitor = progressMonitor;
		 
		 addPropertyChangeListener(this);	
	 }
    @Override
    public Void doInBackground() 
    {
		counter = 1;
		
		m_currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
		
		Iterator<PaImage> it = PaUtils.get().getMainContainer().getCurrentContainer().iterator();
		
		int lng = PaUtils.get().getMainContainer().getCurrentContainer().getSize();
		
		try{
			
			while (it.hasNext()) {		
				
				PaUtils.get().getViewPanel().refreshBoostIcon(it.next().getId());
				
				if(this.isCancelled()) { break; }
				
				int pr = counter*99/lng;
				
				if(pr < 1) pr = 1;
				
				if(pr >= 100) pr = 99;
				
				setProgress(pr);	 
				
				++counter;
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
    
		PaUtils.get().resetCursor(m_currentCursorType);   
		
		writeLog(getMessagesStrs("numberOfRefreshedIconCaption")+" "+(counter-1), null, true, true, false);
    }
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		 if ("progress" == evt.getPropertyName()) {
			 
		 	int p = (Integer) evt.getNewValue();
		 	
            m_progressMonitor.setProgress(p);
            
            String message =
                    String.format(getMessagesStrs("refreshedImagesOperationNote")+" %d%% \n", p);
            
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
