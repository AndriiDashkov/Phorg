
package PaLong;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import PaGlobal.PaUtils;
import PaROI.PaRoiSaveRoiDialog;


/**
 * <p>Class LongTaskAddImages uses SwingWorker to start a long task into background. 
 * Here is the operation of positive ROI nd negative samples generatio</p>
 * @author Andrey Dashkov
 */
 public class PaRoiGenerateSaveTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor = null;
	 
	 PaRoiSaveRoiDialog m_roiSaveDialog = null;
	 
	 int m_currentCursorType;


	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param roiSaveDialog - dialog with ROI save operation settings

	 */
	 public PaRoiGenerateSaveTask(ProgressMonitor progressMonitor, PaRoiSaveRoiDialog roiSaveDialog) 
	 {
		 m_progressMonitor = progressMonitor;
	
		 m_roiSaveDialog = roiSaveDialog;
		 
		 addPropertyChangeListener(this);	
	 }
	 
	 public void setCurrentProgress(int i) {
		 
		 setProgress(i);
		 
	 }
	 
    @Override
    public Void doInBackground() 
    {
    

		m_currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);

		
		setProgress(1);	    			  
	
		
		try {		
			
			m_roiSaveDialog.startCutting();
    	
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
		
		writeLog(getMessagesStrs("roSaveOperatioDoneCaption")+" ", null, true, true, false);
    }
    

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		 if ("progress" == evt.getPropertyName()) {
			 
		 	int p = (Integer) evt.getNewValue();
		 	
            m_progressMonitor.setProgress(p);
            
            String message =
                    String.format(getMessagesStrs("roiSavedOperationNote")+" %d%% \n", p);
            
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