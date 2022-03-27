
package PaLong;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
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
 * <p>Class PaBlurOperation uses SwingWorker to start a long task into background. 
 * Here is the operation of blur image; it uses the space filtration by mask which size can be changed
 * by user</p>
 * @author avd
 */
 public class  PaBlurHiFOperation extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor;
			 
	 int counter;
	 
	 int m_currentCursorType;
	 
	 /**
	  * level of blur - in reality this is a size of mask (3x3,5x5,55x55 etc)
	  */
	 private int m_level;
	 
	 BufferedImage m_image = null;
	 
	 BufferedImage m_fullImage = null;
	 
	 Rectangle m_rec = null;
	 
	 String m_message;
	 
	 PaInstrumentsWindow m_parent;

	 /**
	  * target rectangle (an area that has been selected by user)
	  */
	 Rectangle m_targetRec;
	 
	 /**
	  * Maximum and minimum values of the slider which is on the special panel; we use it when recalculate input range before
	  * fft algorithm; the range for average variant is too big for frequency method
	  */
	 int m_maxSlider;
	 
	 int m_minSlider;

	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param level - level of blur - 5 -99 % (mask size)
	 * @param im - image for blur, can be subimage of fullIm
	 * @param fullIm - full image; if the fullImage  is null then the we consider the im parameter
	 * to be a fullImage and do operation for all image, not for subimage
	 * @param imRec - rectangle of subimage  inside full image (fullIm); here it is bigger then
	 * targetRec because of compensation of black boundary after DFT
	 * @param targetRec - rectangle which was selected by user
	 * @param parent - parent instruments window
	 * 
	 * FUTURE IMPLEMENTATION - the average function doesn't work in right way !!!!!!
	 */
	 public  PaBlurHiFOperation(ProgressMonitor progressMonitor,double level,int maxSlider, int minSlider,
			  BufferedImage im, BufferedImage fullIm,Rectangle imRec,Rectangle targetRec,
			  PaInstrumentsWindow parent) 
	 {
		 m_progressMonitor = progressMonitor;
		 
		 addPropertyChangeListener(this);	
		 
		 m_level = (int)level;
		 
		 m_image = im;
		 
		 m_fullImage = fullIm;
		 
		 m_rec = imRec;
		 
		 m_parent = parent;
		 
		 m_maxSlider = maxSlider; 
		 
		 m_minSlider = minSlider;
		 
		 m_targetRec = targetRec;
	 }
	 
	 public BufferedImage getImage() { return  m_image; }
	 
    @Override
    public Void doInBackground() 
    {
    	//counter = 1;
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
			
			PaFftAlgorithms al = new PaFftAlgorithms();
			
			double level = al.getInNewRange(m_maxSlider, m_maxSlider*0.9, m_maxSlider, m_minSlider, m_level);
			
			double D0 = minSize * (1.0 - level/100.0);
			
			double D02 = D0 * D0;
			
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
				
				al.fft2D(d_rl, d_im, true, N2, p2);
				
				dCounter += 10.0;   setProgress((int)dCounter);  if(this.isCancelled()) {  return null; }
				
				//low frequency filtration
				for(int i= 0; i < N2; ++i) {
					
					for(int j = 0; j < N2; ++j) {
						
						double dist = c.distance(j, i);
						
						double h = Math.exp(-dist*dist/(2*D02));
						
						d_im[j][i] = d_im[j][i]*h/sN;
						
						d_rl[j][i] = d_rl[j][i]*h/sN;
														
					}
				}
				
				al.fft2D(d_rl, d_im, false, N2, p2);
				
				dCounter += 10.0; setProgress((int)dCounter);  if(this.isCancelled()) { return null; }
				
				al.writeCenteredData(m_image,d_rl,color); 
			}
				
	    	//the case of subimage - we must integrate it in the full image
	    	if(m_fullImage != null) {
	    		
	    		//these calculation is to intercept the boundary cases
	    		int deltaX = m_targetRec.x - m_rec.x;
	    		
	    		int deltaY = m_targetRec.y - m_rec.y;

				for(int x1 = 0; x1 < m_targetRec.width; ++x1) {
					
					for(int y1 = 0; y1 < m_targetRec.height; ++y1) {
						
						m_fullImage.setRGB(x1+ m_targetRec.x, y1+ m_targetRec.y, 
								m_image.getRGB(x1+deltaX, y1+deltaY));
					}
				}	
				
				m_image = m_fullImage;
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
			
			writeLog("Not enough memory: can't get result image for blur instrument" + NEXT_ROW +
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
    	 if(this.isCancelled() && m_fullImage != null) { m_image = m_fullImage; }//fix of the bug: select area and cancel operation - part of the image
  
		 if(m_image != null) {	
			 
			m_parent.setResultView(m_image, getGuiStrs("blurInstrumentName"));
		 }
		 else {
			 
			writeLog("Instruments window: can't get result image for blur instrument", null,
				true, false, false );
		 }
    
		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);   
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		writeLog("Instruments window: blur instrument operation finished.", null,
				true, false, false );
		
		writeLog(getMessagesStrs("blurInstrumentFinishedCaption"), null, true, true, false);
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