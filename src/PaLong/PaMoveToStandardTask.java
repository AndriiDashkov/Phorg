
package palong;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.concatPathName;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import pacollection.PaImage;
import pacollection.PaImageContainer;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paglobal.PaUtils;



/**
 * <p>Class PaMoveToStandardTask uses SwingWorker to start a long task into background. 
 * Here is the operation of moving link images to standard location for whole album </p>
 * @author Andrii Dashkov
 */
public class PaMoveToStandardTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

		 ProgressMonitor m_progressMonitor;
		
		 int m_counter = 0;
		 
		 Integer m_albumId;
		 
		 ArrayList<String> m_errorList = new ArrayList<String>(); 
		 
		 ArrayList<PaImage> m_list = null;
		
		 /**
		 * <p>Constructor has parameters to start  operation properly</p>
		 * @param alId - target album's id
		 * @param list - list of images for which we wants to do the reallocation into the standard folder; if list == null, then the operation
		 * will move all images in the album with id; if list != null then only images in the list will be moved
		 */
		 public PaMoveToStandardTask(ProgressMonitor progressMonitor, Integer alId, ArrayList<PaImage> list) 
		 {
			 m_progressMonitor = progressMonitor;
			 
			 m_albumId = alId;
			 
			 m_list = list;
			 
			 addPropertyChangeListener(this);	
		 }
		 
        @Override
        public Void doInBackground() 
        {	
    
			double h = 0.0;
			
			String standardPath = PaUtils.get().getAlbumContainer().getAlbum(m_albumId).getFullStandardPath();
			
			PaImageContainer c =PaUtils.get().getMainContainer().getContainer(m_albumId);
			
			Iterator<PaImage> iter;
			
			if(m_list == null) { //all images in the album will be moved
				
				iter = c.getList().iterator();
			}
			else { //only images in the list
				
				iter = m_list.iterator();
			}
			
			double step = 100.0/(double)c.getSize();	
			
			try {		
					PaImage x;
					
					while (iter.hasNext ()) {
						
						x = iter.next();
						
						String fileName = PaUtils.getFileNameFromString(x.getFullPath()); 
						
						String newPath = concatPathName(standardPath, fileName);
						
						PaUtils.get();
						
						if(x.isLink() && !PaUtils.isSamePathes(newPath, x.getFullPath())) {
							
							try {
								
								x.changePath(standardPath);
								
								x.setLink(false);
								
								++m_counter;
								
							} catch (SecurityException | IOException e) {
				    							
								m_errorList.add(x.getFullPath());
								
								writeLog("Error while moving to standard folder for file " + 
									x.getName()+" id = " + x.getId() + NEXT_ROW+
									" path = " + x.getFullPath(),e,true,false,true);
							}
				
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
        	
			if(m_errorList.isEmpty()) {
				
				writeLog(getMessagesStrs("numberOfMovedToStandard") + " " + m_counter,null,true,true,true);	
			}
			else {
				
				String str = getMessagesStrs("errorMovedToStandard")+NEXT_ROW;
				
				for(String s: m_errorList){
					
					str += s + NEXT_ROW;				
				}
				
		  		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
	    				str,getMessagesStrs("messageErrorCaption"),
	    			    JOptionPane.ERROR_MESSAGE);
			}
			
			if(PaUtils.get().getMainContainer().isCurrent(m_albumId)){
				
				PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );
			}
			
    		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
    		
        }

		@Override
		public void propertyChange(PropertyChangeEvent evt) 
		{
			 if ("progress" == evt.getPropertyName()) {
				 
			 	int p = (Integer) evt.getNewValue();
			 	
                m_progressMonitor.setProgress(p);
                
                String message =
                        String.format(getMessagesStrs("moveToStandardOperationNote")+" %d%% \n", p);
                
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
