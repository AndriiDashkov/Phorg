
package palong;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import paalgorithms.PaAlgoFFT;
import paalgorithms.PaAlgoUtils;
import paeditor.PaHeapArray;
import paeditor.PaInstrumentsWindow;
import paglobal.PaUtils;



/**
 * <p>Class PaShiftOperation uses SwingWorker to start a long task into background. 
 * Here is the operation of  image shift</p>
 * @author Andrii Dashkov
 */
 public class  PaShiftOperation extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

	 ProgressMonitor m_progressMonitor;
	 
	 int counter;
	 
	 int m_currentCursorType;
	 /**
	  * Input data: velosity in x direction, velocity in y direction, exposure time
	  */
	 private double m_level;
	 
	 BufferedImage m_image = null;
	 
	 BufferedImage m_fullImage = null;
	 
	 Rectangle m_rec = null;
	 
	 String m_message;
	 
	 PaInstrumentsWindow m_parent;

	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param level - level of blur - 5 -99 %
	 * @param im - image for blur, can be subimage of fullIm
	 * @param fullIm - full image
	 * @param imRec - rectangle of subimage im inside full image fullIm
	 * @param parent - parent instruments window
	 */
	 public  PaShiftOperation(ProgressMonitor progressMonitor,double level,
			  BufferedImage im, BufferedImage fullIm,Rectangle imRec, PaInstrumentsWindow parent) 
	 {
		 m_progressMonitor = progressMonitor;
		 
		 addPropertyChangeListener(this);	
		 
		 m_level = level;
		 
		 m_image = im;
		 
		 m_fullImage = fullIm;
		 
		 m_rec = imRec;
		 
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
		
		m_message = getMessagesStrs("memoryPreparationDataBlurOp");
		
		dCounter = 1.0;
		
		int pr = (int)dCounter;
		
		setProgress(pr);
		
		int im_width =  m_image.getWidth();  
		
		int im_height =   m_image.getHeight();
		
		int min1 = im_width;
		
		if(im_height < im_width) min1 = im_height;
		
		if(m_level > 99) m_level = 99;
		
		int radius = (int)(min1*(100-m_level)/100.0)/2;
		
		//multiplication on 2 is because we need to avoid of edge filter effects while FFT
		
		int[]  t = PaAlgoUtils.getPowerOf2_MaxValue(2*im_width,2*im_height);
		
		int nT2 = t[1];
		
		int n2 = t[0];
	
		PaHeapArray<Double> Red_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Alpha_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Rl2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Red_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Green_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> Blue_Im2 = new PaHeapArray<Double>(nT2,nT2);
		
		int rgb_data = m_image.getRGB(2, 2);
		
		double r_data = (double)((rgb_data & 0x00ff0000) >> 16);
		
		double g_data = (double)((rgb_data & 0x0000ff00) >> 8);
		
		double b_data = (double)(rgb_data & 0x000000ff);
		
		double[] maxData = {r_data,g_data,b_data};
		
		double[] minData = {r_data,g_data,b_data};


		try {
			
			m_message = getMessagesStrs("getCenteredDataBlurOp");
			
			dCounter = 2.0;
			
			pr = (int)dCounter;
			
			setProgress(pr);	 
			
			double h = 10.0/(nT2);
			
			for ( int i = 0; i < nT2; ++i) {
				
				dCounter += h;   setProgress((int)dCounter);
				
				if(this.isCancelled()) { return null; }
				
				for ( int j = 0; j < nT2; ++j) {
					
					 int i_j = i + j;
					 	 
					 Red_Im.set(j,i,0.0);
					 
					 Green_Im.set(j,i,0.0);
					 
					 Blue_Im.set(j,i,0.0);
					 				
					 if(i >= im_width || j >= im_height) {	
						 
						 Red_Rl.set(j,i,0.0);   Green_Rl.set(j,i,0.0);  Blue_Rl.set(j,i,0.0);
					 }
					 else{	
						 
						 int rgb = 0;
	
						 rgb = m_image.getRGB(i, j);
			
						 double rD = (double)((rgb & 0x00ff0000) >> 16);
						 
						 double gD = (double)((rgb & 0x0000ff00) >> 8);
						 
						 double bD = (double)(rgb & 0x000000ff);
						 
						 Alpha_Rl.set(j,i,(double)(rgb >>> 24));
				
						if(i_j%2 != 0 && i_j != 0) {			
							
							Red_Rl.set(j,i,-rD);  Green_Rl.set(j,i,-gD); Blue_Rl.set(j,i,-bD);
						}
						else {	
		
							Red_Rl.set(j,i,rD);  Green_Rl.set(j,i,gD);  Blue_Rl.set(j,i,bD);
						}
						
						if(rD > maxData[0]) maxData[0] = rD;
						
						if(rD < minData[0]) minData[0] = rD;
						
						if(gD > maxData[1]) maxData[1] = gD;
						
						if(gD < minData[1]) minData[1] = gD;
						
						if(bD > maxData[2]) maxData[2] = bD;
						
						if(bD < minData[2]) minData[2] = bD;
						
						
					 }
					
				}
			}
			
			dCounter = 14.0;  
			
			m_message = getMessagesStrs("fastFTransCaption");
			
			setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
			PaAlgoFFT.getFFT1(true,Red_Rl,Red_Im,nT2,nT2,n2,Red_Rl2,Red_Im2);
			
			dCounter = 25.0;  setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
			PaAlgoFFT.getFFT1(true,Green_Rl,Green_Im,nT2,nT2,n2,Green_Rl2,Green_Im2);
			
			dCounter = 32.0;  setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
			PaAlgoFFT.getFFT1(true,Blue_Rl,Blue_Im,nT2,nT2,n2,Blue_Rl2,Blue_Im2);
			
			dCounter = 38.0;   pr = (int)dCounter;
			
			m_message = getMessagesStrs("fitrationBlurCaption");
			
			setProgress(pr);
			
			if(this.isCancelled()) { return null; }
				
			int deltaX1 = (nT2 - radius*2)/2;
			
			int deltaY1 = (nT2 - radius*2)/2;
			
			int dX1 = deltaX1  + radius*2;
			
			int dY1 = deltaY1 + radius*2;
			
			
			for ( int i = 0; i < nT2; ++i) {
				
				for ( int j = 0; j < deltaY1; ++j) {
					
					 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
					 
					 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
					 
				}
				for ( int j = dY1; j < nT2; ++j) {
					
					 Red_Rl2.set(j,i,0.0);   Green_Rl2.set(j,i,0.0);  Blue_Rl2.set(j,i,0.0);
					 
					 Red_Im2.set(j,i,0.0);   Green_Im2.set(j,i,0.0);  Blue_Im2.set(j,i,0.0);
				}
			}
	
			for ( int j = deltaY1; j < dY1; ++j) {
				
			for ( int i = 0; i < deltaX1; ++i) {
			
					Red_Rl2.set(j,i,0.0);  Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
					
					Red_Im2.set(j,i,0.0);  Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
				}
				for ( int i = dX1; i < nT2; ++i) {	
					
					 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
					 
					 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);
				}
			}
			
	
			Point c = new Point(nT2/2,nT2/2);
			
			Point p;
	
			for ( int i = deltaY1; i < dX1; ++i) {
				
				for ( int j = deltaY1; j < dY1; ++j) {
					
					p = new Point(i,j);
					
					double r  = c.distance(p);
					
					if(r > radius) {
						
						 Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0); Blue_Rl2.set(j,i,0.0);
						 
						 Red_Im2.set(j,i,0.0); Green_Im2.set(j,i,0.0); Blue_Im2.set(j,i,0.0);	
					}
				}
			}
			
			dCounter = 50.0;  
			
			m_message = getMessagesStrs("fastFTransCaption");
			
			setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
			PaAlgoFFT.getFFT1(false,Red_Rl2,Red_Im2,nT2,nT2,n2,Red_Rl,Red_Im);
			
			dCounter = 59.0;  setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
			PaAlgoFFT.getFFT1(false,Green_Rl2,Green_Im2,nT2,nT2,n2,Green_Rl,Green_Im);
			
			dCounter = 68.0;  setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
			PaAlgoFFT.getFFT1(false,Blue_Rl2,Blue_Im2,nT2,nT2,n2,Blue_Rl,Blue_Im);
			
			dCounter = 75.0;   pr = (int)dCounter;
			
			m_message = getMessagesStrs("getCenteredDataBlurOp");
			
			setProgress(pr);
			
			if(this.isCancelled()) { return null; }
			
			//initial data for finding min and max
			double red = Red_Rl.get(1,1);
			
			double green = Green_Rl.get(1,1);
			
			double blue = Blue_Rl.get(1,1);
		
			double[] max = {red,green,blue};
			
			double[] min = {red,green,blue};
			//*************************************
			
			//get back centered data and find max and min values for all color components
			for ( int i = 0; i < im_width; ++i) {
				
				for ( int j = 0; j < im_height; ++j) {
					
					int i_j = i+j;
	
					red = Red_Rl.get(j,i);
					
					green = Green_Rl.get(j,i);
					
					blue = Blue_Rl.get(j,i);
		
					//get back the centered data
					if((i_j)%2 != 0 && i_j != 0) {
						
						red = -red;
						
						green = - green;
						
						blue = - blue;
						
						 Red_Rl.set(j,i,red);
						 
						 Green_Rl.set(j,i,green);
						 
						 Blue_Rl.set(j,i,blue);
					}
					
					if(red > max[0]) max[0] = red;
					
					if(red < min[0]) min[0] = red;
					
					if(green > max[1]) max[1] = green;
					
					if(green < min[1]) min[1] = green;
					
					if(blue > max[2]) max[2] = blue;
					
					if(blue < min[2]) min[2] = blue;
				}
			}
			//coefficients to convert initial data range to tne end data range		
			double aRed = (maxData[0]-minData[0])/(max[0]-min[0]);
			
			double aGreen = (maxData[1]-minData[1])/(max[1]-min[1]);
			
			double aBlue = (maxData[2]-minData[2])/(max[2]-min[2]);
			
			double bRed = maxData[0] - aRed*max[0];
			
			double bGreen = maxData[1] - aGreen*max[1];
			
			double bBlue = maxData[2] - aBlue*max[2];
			
			
			BufferedImage targetImage = new BufferedImage(im_width,im_height,BufferedImage.TYPE_INT_RGB);
			
			int r = 0;
			
			int g = 0;
			
			int b = 0;

			
			dCounter = 90.0;   pr = (int)dCounter;
			
			m_message = getMessagesStrs("restoreBrightRangeBlurOp");
			
			setProgress(pr);
			
			if(this.isCancelled()) { return null; }
	
			for ( int i = 0; i < im_width; ++i) {
				
				for ( int j = 0; j < im_height; ++j) {
					
	
					r = (int)(aRed*Red_Rl.get(j,i).intValue() + bRed);
					
					g = (int)(aGreen*Green_Rl.get(j,i).intValue()+bGreen);
					
					b = (int)(aBlue*Blue_Rl.get(j,i).intValue()+bBlue);
	
					r = r > 255 ? 255 : (r < 0 ? 0 : r);
					
					g = g > 255 ? 255 : (g < 0 ? 0 : g);
					
					b = r > 255 ? 255 : (b < 0 ? 0 : b);
					
					int rgb = r << 16 | g << 8 | b |  Alpha_Rl.get(j, i).intValue() << 24;
							
					targetImage.setRGB(i, j, rgb);	
				}
			}

			if(m_fullImage != null) {
				
				BufferedImage newImage = PaUtils.deepCopy(m_fullImage );
				
				for(int x1 = 0; x1 < targetImage.getWidth(); ++x1) {
					
					for(int y1 = 0; y1 < targetImage.getHeight(); ++y1) {
						
						newImage.setRGB(x1+ m_rec.x, y1+ m_rec.y, targetImage.getRGB(x1, y1));
					}
				}	
				
				m_image = newImage;
			}
			else {
				m_image = targetImage;
			}
		
	    }
		finally {	
			
			Red_Rl.free();
			
			Green_Rl.free();
			
			Blue_Rl.free();
			
			Alpha_Rl.free();
			
			Red_Rl2.free();
			
			Green_Rl2.free();
			
			Blue_Rl2.free();
			
			Red_Im.free();
			
			Green_Im.free();
			
			Blue_Im.free();
			
			Red_Im2.free();
			
			Green_Im2.free();
			
			Blue_Im2.free();
			
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