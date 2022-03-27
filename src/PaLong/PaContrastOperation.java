
package PaLong;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import PaAlgorithms.PaFftAlgorithms;
import PaEditor.PaInstrumentsWindow;
import PaGlobal.PaUtils;



/**
 * <p>Class PaContrastOperation uses SwingWorker to start a long task into background. 
 * Here is the operation of contrast change; it uses the frequency filtration using DFT</p>
 * @author avd
 */
 public class PaContrastOperation extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor;
	 
	 int counter;
	 
	 int m_currentCursorType;
	 
	 /**
	  * level of blur - in reality this is a size of mask (3x3,5x5,55x55 etc)
	  */
	 //private int m_level;
	 BufferedImage m_image = null;
	 
	 String m_message;
	 
	 PaInstrumentsWindow m_parent;
	 
	 /**
	  * the operation will get the contours of image if this flag is true
	  */
	 boolean m_contour; 
	 
	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param level - level of sharpness increase - the slider values will be recalculated
	 * @param im - image for operation
	 * @param parent - parent instruments window
	 * 
	 */
	 public PaContrastOperation(ProgressMonitor progressMonitor,double level,BufferedImage im,
			 PaInstrumentsWindow parent) 
	 {
		 m_progressMonitor = progressMonitor;
		 
		 addPropertyChangeListener(this);	
		 
		 m_image = im;
		 
		 m_parent = parent;

	 }
	 
	 public BufferedImage getImage() { return  m_image; }
	 
    @Override
    public Void doInBackground() 
    {
    	
    	double dCounter = 0.0;
		
		m_currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			
			m_message = getMessagesStrs("operationInProgressMessage");
			
			dCounter = 1.0;      setProgress((int)dCounter);	     if(this.isCancelled()) { return null; }
			
			int xN = m_image.getWidth();
			
			int yN = m_image.getHeight();
			
			int minSize = xN;
			
			if(xN > yN)  { minSize =yN; }
			
			double D02 = 0.2 * minSize;
			
			PaFftAlgorithms al = new PaFftAlgorithms();

			int[] d = al.getPowerOf2_MaxValue(xN, yN);
			
			int p2 = d[0];
			
			int N2 = d[1];
			
			double[][] d_rl = new double[N2][N2];
			
			double[][] d_im = new double[N2][N2];
			
			Point c = new Point(N2/2, N2/2);//center
			
			double sN = N2*N2;
			 
			//we use the same BufferedImage for read and write, so the alpha channel is going automatically (it sn't processed inside algorithm)
			for(int color = 0; color < 3; ++color) {
				
				dCounter += 10.0; setProgress((int)dCounter); if(this.isCancelled()) { return null; }
			
				double[] minMax = { 0,0};
				
				al.readCenteredData(m_image,d_rl,d_im,color, minMax); //red
				
				al.fft2D(d_rl, d_im, true, N2, p2);//the sum/N2*N2 will be performed later 
				
				dCounter += 10.0;   setProgress((int)dCounter);  if(this.isCancelled()) { return null; }
				
		
				//hi frequency filtration and then we add result to image itself
				//part d_rl[j][i]*h in the sum is a contour
				//here we also perform the /N2*N2 (part of DFT formula)
				for(int i= 0; i < N2; ++i) {
					
					for(int j = 0; j < N2; ++j) {
						
						double dist = c.distance(j, i);
						
						double h = (0.8-0.5)*(1.0 - Math.exp(-5*dist*dist/(1*D02)))+0.5;	
				
						d_im[j][i] = (d_im[j][i]*h)/sN;
						
						d_rl[j][i] = (d_rl[j][i]*h)/sN;;							
					}
				}
				
				
				al.fft2D(d_rl, d_im, false, N2, p2);
				
				dCounter += 10.0; setProgress((int)dCounter);  if(this.isCancelled()) { return null; }
				
				al.writeCenteredData(m_image,d_rl,color); 
			}	
		}
		catch(ArrayIndexOutOfBoundsException ex) {//the case MUST be investigated !!!!!!!
			writeLog("Unexpectable problem: difference in buffer and indexed data " + NEXT_ROW +
					ex.getMessage(), ex, true, true, true );
		}
		catch(OutOfMemoryError ex) {
			
			JOptionPane.showMessageDialog(
				    null,
				    getMessagesStrs("outOfMemoryMessage"),
				    getMessagesStrs("messageCaption"),
				    JOptionPane.OK_OPTION);
			
			writeLog("Not enough memory: can't get result image for contrast instrument" + NEXT_ROW +
					ex.getMessage(),null,true, false, false );
		}
		finally {	
			
			PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);  
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
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
    	
		 if(m_image != null) {	
			 
			m_parent.setResultView(m_image, getGuiStrs("contrastInstrumentName"));
		 }
		 else {
			 
			writeLog("Instruments window: can't get result image for contrast instrument", null,
				true, false, false );
		 }
    
		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);  
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		writeLog("Instruments window: contrast instrument operation finished.", null,
				true, false, false );
		
		writeLog(getMessagesStrs("sharpInstrumentFinishedCaption"), null, true, true, false);
    }
    


	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		 if ("progress" == evt.getPropertyName()) {
			 
		 	int p = (Integer) evt.getNewValue();
		 	
            m_progressMonitor.setProgress(p);
            
            String message = String.format(m_message+" %d%% \n", p);
            
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
