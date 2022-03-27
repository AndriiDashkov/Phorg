
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
 * <p>Class PaSharpOperation uses SwingWorker to start a long task into background. 
 * Here is the operation of sharpness increase; it uses the frequency filtration using DFT
 * This operation also can be used for selection of image's contours - see appropriative flag in the constructor</p>\
 * @author avd
 */
 public class PaSharpOperation extends SwingWorker<Void, Void> implements PropertyChangeListener 
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
	  * the operation will get the contours of image if this flag is true
	  */
	 boolean m_contour; 
	 
	 /**
	  * Maximum and minimum values of the slider which is on the special panel; we use it when recalculate input range before
	  * fft algorithm; the range for average variant is too big for frequency method
	  */
	 int m_maxSlider;
	 
	 int m_minSlider;

	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param level - level of sharpness increase - the slider values will be recalculated
	 * @param im - image for operation, can be subimage of fullIm
	 * @param fullIm - full image; if the fullImage  is null then the we consider the im parameter
	 * to be a fullImage and do operation for all image, not for subimage
	 * @param imRec - rectangle of subimage  inside full image (fullIm); can be null
	 * @param parent - parent instruments window
	 * 
	 */
	 public  PaSharpOperation(ProgressMonitor progressMonitor,double level,int maxSlider, int minSlider,
			  BufferedImage im, BufferedImage fullIm,Rectangle imRec, boolean contourFlag,
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
		 
		 m_contour = contourFlag;
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
			
			dCounter = 1.0;      
			
			setProgress((int)dCounter);	
			
			if(this.isCancelled()) { return null; }
			
			int xN = m_image.getWidth();
			
			int yN = m_image.getHeight();
			
			int minSize = xN;
			
			if(xN > yN)  { minSize =yN; }
			
			PaFftAlgorithms al = new PaFftAlgorithms();
			
			//coefficient 0.2 has been selected in experimental way
			double maxNew = minSize*0.2;
			
			double level = al.getInNewRange(maxNew,minSize*0.02, m_maxSlider, m_minSlider, m_level);
			
			//reverse of visible sense of increase and decrease of sharpness ()
			//sharpness is bigger for small values of level
			double D0 = maxNew - level;
			
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
				
				al.fft2D(d_rl, d_im, true, N2, p2);//the sum/N2*N2 will be performed later 
				
				dCounter += 10.0;   
				setProgress((int)dCounter); 
				
				if(this.isCancelled()) { return null; }
				
				if(!m_contour) {
					
					//hi frequency filtration and then we add result to image itself
					//part d_rl[j][i]*h in the sum is a contour
					//here we also perform the /N2*N2 (part of DFT formula)
					for(int i= 0; i < N2; ++i) {
						
						for(int j = 0; j < N2; ++j) {
							
							double dist = c.distance(j, i);
							
							double h = 1.0 - Math.exp(-dist*dist/(2*D02));
							
							d_im[j][i] = (d_im[j][i]*h + d_im[j][i])/sN;
							
							d_rl[j][i] = (d_rl[j][i]*h + d_rl[j][i])/sN;
														
						}
					}
				}
				else {
					for(int i= 0; i < N2; ++i) {
						
						for(int j = 0; j < N2; ++j) {
							
							double dist = c.distance(j, i);
							
							double h = 1.0 - Math.exp(-dist*dist/(2*D02));
							
							d_im[j][i] = (d_im[j][i]*h)/sN;
							
							d_rl[j][i] = (d_rl[j][i]*h)/sN;
														
						}
					}
					
				}
				
				al.fft2D(d_rl, d_im, false, N2, p2);
				
				//brightness must be corrected in the case of contour operation
				if(m_contour) {
			
					for(int i= 0; i < xN; ++i) {
						
						for(int j = 0; j < yN; ++j) {
							
							if(d_rl[j][i] > 1) d_rl[j][i] *= 10;						
						}
					}
				}
				
				dCounter += 10.0; setProgress((int)dCounter);  if(this.isCancelled()) { return null; }
				
				al.writeCenteredData(m_image,d_rl,color); 
			}
				
	    	//m_image = targetImage;
	    	//the case of subimage - we must integrate it in the full image
	    	if(m_fullImage != null) {

				for(int x1 = 0; x1 < m_image.getWidth(); ++x1) {
					
					for(int y1 = 0; y1 < m_image.getHeight(); ++y1) {
						
						m_fullImage.setRGB(x1+ m_rec.x, y1+ m_rec.y, m_image.getRGB(x1, y1));
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
			
			writeLog("Not enough memory: can't get result image for sharpness increase instrument" + NEXT_ROW +
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
			 
			m_parent.setResultView(m_image, getGuiStrs("sharpInstrumentName"));
		 }
		 else {
			 
			writeLog("Instruments window: can't get result image for sharpness increase instrument", null,
				true, false, false );
		 }
    
		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);  
		
		m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		writeLog("Instruments window: sharpness instrument operation finished.", null,
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
