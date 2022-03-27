
package PaLong;


import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import PaAlgorithms.PaAlgoFFT;
import PaAlgorithms.PaAlgoUtils;
import PaEditor.PaComplexValue;
import PaEditor.PaHeapArray;
import PaEditor.PaInstrumentsWindow;
import PaGlobal.PaUtils;


/**
 * <p>Class PaBlurImageOperation uses SwingWorker to start a long task into background. 
 * Here is the operation of blur image</p>
 * @author avd
 */
 public class PaBlurImageOperation extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor;
	 
	 int counter;
	 
	 int m_currentCursorType;
	 /**
	  * Input data: velocity in x direction, velocity in y direction, exposure time
	  */
	 private double m_xVelocity,m_yVelocity,m_expTime;
	 
	 BufferedImage m_image = null;
	 
	 String m_message;
	 
	 PaInstrumentsWindow m_parent;

	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 */
	 public PaBlurImageOperation(ProgressMonitor progressMonitor,double xVelocity,
			 double yVelocity, double expTime, BufferedImage im,PaInstrumentsWindow parent) 
	 {
		 m_progressMonitor = progressMonitor;
		 
		 addPropertyChangeListener(this);	
		 
		 m_xVelocity = xVelocity;
		 
		 m_yVelocity =  yVelocity;
		 
		 m_expTime =  expTime;
		 
		 m_image = im;
		 
		 m_parent = parent;
	 }
	 
	 public BufferedImage getImage() { return  m_image; }
	 
    @Override
    public Void doInBackground() 
    {
    	counter = 1;
    	
    	double dCounter = 0.0;
		
		m_currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
			
		int xN = m_image.getWidth();  
		
		int yN =  m_image.getHeight();
		
		int[]  t1 = PaAlgoUtils.getPowerOf2_MaxValue(2*xN,2*yN);
		
		int nT2 = t1[1];
		
		int n2 = t1[0];
	
		m_message = getMessagesStrs("memoryPreparationDataBlurOp");
		
		dCounter = 1.0;
		
		int pr = (int)dCounter;
		
		setProgress(pr);
		
		PaHeapArray<Double> hfRl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> hfIm = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imDataIm = new PaHeapArray<Double>(nT2,nT2);
		
		//separate array for every component
		PaHeapArray<Double> imDataRlRed = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imDataRlGreen = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imDataRlBlue = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imDataRlAlpha = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imageRlRed = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imageRlGreen = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imageRlBlue = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imageRl;
		
		PaHeapArray<Double> imageIm = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imDataRl;
		
		PaHeapArray<Double> FuvRl = new PaHeapArray<Double>(nT2,nT2);//- for FFT results
		
		PaHeapArray<Double> FuvIm = new PaHeapArray<Double>(nT2,nT2);////- for FFT results
		
    	
			try{
	
				BufferedImage targetImage = new BufferedImage(xN,yN,BufferedImage.TYPE_INT_RGB);
				
				m_message = getMessagesStrs("getCenteredDataBlurOp");
				
				dCounter = 2.0;
				
				pr = (int)dCounter;
				
				setProgress(pr);	    			  
			
				PaHeapArray<Double> ffRl = new PaHeapArray<Double>(nT2,nT2);//tmp arrays
				
				PaHeapArray<Double> ffIm = new PaHeapArray<Double>(nT2,nT2);
				
				for ( int i = 0; i < nT2; ++i) {
					
					for ( int j = 0; j < nT2; ++j) {

							double[] t = blurFilterFunction(i,j,m_xVelocity,m_yVelocity,m_expTime); 
							
							hfRl.set(j,i,t[0]);//*k;
							
							hfIm.set(j,i,t[1]);//*k;	
							
							imDataIm.set(j,i,0.0);
						
					}
				}
				
				dCounter += 3.0;
				
				pr = (int)dCounter;
		
				m_message = getMessagesStrs("fastFTransCaption");
				
				setProgress(pr);
				
				if(this.isCancelled()) { 
					
					return null; 
				}
			
				PaAlgoFFT.getFFT1(false,hfRl,hfIm,nT2,nT2,n2,ffRl,ffIm); //back FFT
				
				for ( int i = 0; i < nT2; ++i) {
					
					for ( int j = 0; j < nT2; ++j) {
						
						 if(i >= xN || j >= yN) {
							 
							 ffRl.set(j,i,0.0);
							 
							 ffIm.set(j,i,0.0);
							 
							 imDataRlRed.set(j,i,0.0);
							 
							 imDataRlGreen.set(j,i,0.0);
							 
							 imDataRlBlue.set(j,i,0.0);
							 
						 }
						 else{
							 
							 double k = Math.pow(-1.0, (i+j)); 
							 
							 ffRl.set(j,i,k*ffRl.get(j,i));
							 
							 ffIm.set(j,i,k*ffIm.get(j,i));
							 
							int rgb = m_image.getRGB(i, j);		
							
							imDataRlRed.set(j,i,((rgb & 0xff0000) >> 16)*k);
							
							imDataRlGreen.set(j,i,((rgb & 0xff00) >> 8)*k);
							
							imDataRlBlue.set(j,i,(rgb & 0xff)*k);
							
							imDataRlAlpha.set(j,i,(double)(rgb >>> 24));
						 }
						
					}
				}

				PaAlgoFFT.getFFT1(true,ffRl,ffIm,nT2,nT2,n2,hfRl,hfIm);

				ffRl.free();
				
				ffIm.free();
				
				if(this.isCancelled()) { 
					
					return null; 
				}
				
				double[] max = new double[3];
				
				double[] min = new double[3];
					
				for(int color = 0; color < 3; ++color ) {
					
					dCounter += 5.0;
					
					pr = (int)dCounter;
			
					m_message = getMessagesStrs("fastFTransCaption");
					
					setProgress(pr);
					
					if(this.isCancelled()) { 
						return null; 
					}
				
					//preparation of centred data
					switch(color) {
					
						default :
							
						case 0 : { imDataRl = imDataRlRed; 	 imageRl = imageRlRed; break; }
						
						case 1 : { imDataRl = imDataRlGreen; imageRl = imageRlGreen; break; }
						
						case 2 : { imDataRl = imDataRlBlue;  imageRl = imageRlBlue; break; }
					}
			
					//direct FFT ; FuvRl - real part, FuvIm - imaginary part
				
					PaAlgoFFT.getFFT1(true,imDataRl,imDataIm, nT2,nT2,n2,FuvRl, FuvIm); 
				
					double h = 10.0/(nT2*nT2);
					
					m_message = getMessagesStrs("fitrationBlurCaption");
					
					//apply the filter
					for ( int x = 0; x < nT2; ++x) {
						
						for ( int y = 0; y < nT2; ++y) {
							
							dCounter += h;
							
							pr = (int)dCounter;
			
							setProgress(pr);
							
							if(this.isCancelled()) { 
								
								return null; 
							}
								
							double[] t = PaComplexValue.mul(hfRl.get(y,x),hfIm.get(y,x),
									FuvRl.get(y,x),FuvIm.get(y,x));
		
							FuvRl.set(y,x,t[0]);
							
							FuvIm.set(y,x,t[1]);  
							
						}			
					}
					//back to spatial domain
					PaAlgoFFT.getFFT1(false,FuvRl,FuvIm,nT2,nT2,n2,imageRl, imageIm);
				
					h = 10.0/(xN*yN);
					
					m_message = getMessagesStrs("getCenteredDataBlurOp");
					
					for ( int x = 0; x < xN; ++x) {
						
						for ( int y = 0; y < yN; ++y) {
							
							dCounter += h;
							
							pr = (int)dCounter;
							
							setProgress(pr);
							
							if(this.isCancelled()) { 
								
								return null; 
							}
				
							double d  = imageRl.get(y,x)*Math.pow(-1.0, (x+y));//back centring
							 
							if(d > max[color]) max[color] = d;
							
							if(d < min[color]) min[color] = d;
							
							imageRl.set(y,x,d);
				
						}
					}
				}
				
				//stretch the brightness range of RGB for the limits 0 ... 255
				double h = 25.0/(xN*yN);
				
				m_message = getMessagesStrs("restoreBrightRangeBlurOp");
				
				for ( int x = 0; x < xN; ++x) {
					
					for ( int y = 0; y < yN; ++y) {
						
						dCounter += h;
						
						pr = (int)dCounter;
						
						if(pr > 99) pr = 99;
						
						setProgress(pr);
						
						if(this.isCancelled()) { 
							
							return null; 
						}
						
						//stretch the brightness range
						int r = (int)((imageRlRed.get(y,x)-min[0])*255.0/(max[0]-min[0]));
						
						int g = (int)((imageRlGreen.get(y,x)-min[1])*255.0/(max[1]-min[1]));
						
						int b = (int)((imageRlBlue.get(y,x)-min[2])*255.0/(max[2]-min[2]));
		
						r = r > 255 ? 255 : (r < 0 ? 0 : r);
						
						g = g > 255 ? 255 : (g < 0 ? 0 : g);
						
						b = r > 255 ? 255 : (b < 0 ? 0 : b);
						
						Color newColor =  new Color(r,g,b,imDataRlAlpha.get(y, x).intValue());
				
						targetImage.setRGB(x, y, newColor.getRGB());	
					}
				}
				m_image = targetImage;
		    	    	
			}
			catch(OutOfMemoryError e) {
					
				JOptionPane.showMessageDialog(
					    null,
					    getMessagesStrs("outOfMemoryMessage"),
					    getMessagesStrs("messageErrorCaption"),
					    JOptionPane.OK_OPTION);
				
				writeLog("Not enough memory: can't get result image for blur instrument" + NEXT_ROW +
						e.getMessage(), null,
						true, false, false );
			}
			finally {	
				
				hfRl.free();
				
				hfIm.free();
				
				imDataIm.free();
			
				imDataRlRed.free();
				
				imDataRlGreen.free();
				
				imDataRlBlue.free();
				
				imDataRlAlpha.free();
				
				imageRlRed.free();
				
				imageRlGreen.free();
				
				imageRlBlue.free();
				
				imageIm.free();
				
				FuvRl.free();
				
				FuvIm.free();
				
				PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);  
				
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
			 
			m_parent.setResultView(m_image, getGuiStrs("blurInstrumentName"));
		 }
		 else {
			 
			writeLog("Instruments window: can't get result image for blur instrument", null,
				true, false, false );
		 }
    
		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);   
		
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
            
            String message =
                    String.format(m_message+" %d%% \n", p);
            
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
	 * <p>The idea of this filter is to correct the blur effect; the user have to gess about 
	 * a,b,expTime parameters on its own</p>
	 * @param u - filter variable in x direction
	 * @param v - filter variable in y direction
	 * @param a - velocity of blur in x direction
	 * @param b - velocity of blur in y direction
	 * @param expTime - time of exposition
	 * @return the H filter which can be used in frequency data only (to the results of FFT )
	 */
	public  double[] blurFilterFunction(int u, int v,double a, double b, double expTime)
	{
		double[] res = new double[2];
		
		double c1 = Math.PI*(u*a+v*b);
		
		if(c1 == 0.0) { res[0] = 0.0; res[1] = 0.0;  return res;}
		
		double cSin = Math.sin(c1);
		
		double c2 = expTime*cSin/c1;
		
		res[0]= c2*Math.cos(c1);
		
		res[1]= -c2*cSin;
		
		
		return res;
	}
	
}