
package PaLong;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import PaEditor.PaInstrumentsWindow;
import PaGlobal.PaUtils;


/**
 * <p>Class PaBlurOperation uses SwingWorker to start a long task into background. 
 * Here is the operation of blur image; it uses the space filtration by mask which size can be changed
 * by user</p>
 *  @author avd
 */
 public class  PaBlurOperation extends SwingWorker<Void, Void> implements PropertyChangeListener 
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
	 
	 boolean m_avBoundary; //boundary average flag
	 
	 boolean m_dontChangeViewImage = false;

	 /**
	 * <p>Constructor has parameters to start add images operation properly</p>
	 * @param progressMonitor - progress monitor
	 * @param level - level of blur - 5 -99 % (mask size)
	 * @param im - image for blur, can be subimage of fullIm
	 * @param fullIm - full image; if the fullImage  is null then the we consider the im parameter
	 * to be a fullImage and do operation for all image, not for subimage
	 * @param imRec - rectangle of subimage  inside full image (fullIm)
	 * @param parent - parent instruments window
	 * @param dontChangeViewImage -if true then the result image will not be change in the preview area ,
	 * and the result of operation will be accessible via link to m_image only. 
	 * FUTURE IMPLEMENTATION - the average function doesn't work in right way !!!!!!
	 */
	 public  PaBlurOperation(ProgressMonitor progressMonitor,double level,
			  BufferedImage im, BufferedImage fullIm,Rectangle imRec, boolean averageBoundary,
			  PaInstrumentsWindow parent,boolean dontChangeViewImage) 
	 {
		 
		 m_progressMonitor = progressMonitor;
		 
		 addPropertyChangeListener(this);	
		 
		 m_level = (int)level;
		 
		 m_image = im;
		 
		 m_fullImage = fullIm;
		 
		 m_rec = imRec;
		 
		 m_parent = parent;
		 
		 m_avBoundary = averageBoundary;
		 
		 m_dontChangeViewImage = dontChangeViewImage;
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
			
		    //mask width
			final int wMask = m_level/2;
			
			final int wMask2 = m_level*m_level;//number of pixels in the mask
		    
	        final int wMax = m_image.getWidth();
	        
	        final int hMax = m_image.getHeight();
	        
			WritableRaster wr= m_image.getRaster();

		    final int wMaxNew = wMax+wMask *2;
		    
		    final int hMaxNew = hMax+wMask *2;
		        
	    	int[][][] imData = new int[4][hMaxNew][wMaxNew];
		
			DataBuffer buf = m_image.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = m_image.getAlphaRaster() != null;
			
			//read data in array imData; data can be different types of buffer
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
				int[] data = ((DataBufferInt) buf).getData();
				
				readIntData(data,imData, wMax, hMax, wMask);
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) m_image.getRaster().getDataBuffer()).getData();
				
				readByteData(data,imData, hasAlphaChannel, wMax, hMax, wMask);
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
				
				return null;
			}
			
			dCounter = 10.0;
			
			setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
			//fill boundary around image - the width of boundary is half of mask width
	        for(int j = 0; j < hMaxNew; ++j) {
	        	
	        	for(int i = 0; i < wMask; ++i) {
	        		
	        		 int index = i+wMask;
	        		 
	        	   	  imData[3][j][i] = imData[3][j][index]; // alpha
	        	   	  
	        	   	  imData[2][j][i] = imData[2][j][index];  // blue
	        	   	  
	        	   	  imData[1][j][i] = imData[1][j][index];  // green
	        	   	  
	        	   	  imData[0][j][i] = imData[0][j][index];  // red
	        	   	  
	        	   	  index = i + wMax + wMask;
	        	   	  
	         	   	  int index1 = i - wMask + wMaxNew - 1; 
	         	   	  
	         	   	  imData[3][j][index] = imData[3][j][index1]; // alpha
	         	   	  
	        	   	  imData[2][j][index] = imData[2][j][index1];  // blue
	        	   	  
	        	   	  imData[1][j][index] = imData[1][j][index1];  // green
	        	   	  
	        	   	  imData[0][j][index] = imData[0][j][index1];  // red
	        	}
	        }
	        
	        for(int i = 0; i < wMaxNew; ++i) {
	        	
	        	for(int j = 0; j < wMask; ++j) {
	        		
	        		  int index = j + wMask;
	        		  
	        	   	  imData[3][j][i] = imData[3][index][i]; // alpha
	        	   	  
	        	   	  imData[2][j][i] = imData[2][index][i];  // blue
	        	   	  
	        	   	  imData[1][j][i] = imData[1][index][i];  // green
	        	   	  
	        	   	  imData[0][j][i] = imData[0][index][i];  // red
	        	   	  
	         	   	  index = j + hMax  + wMask;
	         	   	  
	         	   	  int index1 = j + hMaxNew - 1 - wMask;
	         	   	  
	         	   	  imData[3][index][i] = imData[3][index1][i]; // alpha
	         	   	  
	        	   	  imData[2][index][i] = imData[2][index1][i];  // blue
	        	   	  
	        	   	  imData[1][index][i] = imData[1][index1][i];  // green
	        	   	  
	        	   	  imData[0][index][i] = imData[0][index1][i];  // red
	        	}
	        }
	        
			dCounter = 20.0;  setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
	        int red = 0; 
	        
	        int green = 0;
	        
	        int blue = 0; 
	        
	        int alpha = 0;
	        
	        double h = 58.0/(wMax * hMax);
	        
	        //mask processing
	    	for ( int x = wMask; x < wMaxNew - wMask; ++x ) {
	    		
				for(int y = wMask; y < hMaxNew - wMask; ++y) {
					
					red = 0; 
					
					green = 0; 
					
					blue = 0; 
					
					alpha = 0;
					
					dCounter += h;
					
					setProgress((int)dCounter);
					
					if(this.isCancelled()) { return null; }
					
					//go over the all mask square
					for(int x1 = x - wMask; x1 <= x + wMask; ++x1 ) {
						
						for(int y1 = y - wMask; y1 <= y + wMask; ++y1 ) {
							
							red += imData[0][y1][x1];
							
							green += imData[1][y1][x1];
							
							blue += imData[2][y1][x1];
							
							alpha += imData[3][y1][x1];
						}
					}	
							
					red /= wMask2;
					
					green /= wMask2;
					
					blue /= wMask2;
					
					alpha /= wMask2;
					
					red = red > 255 ? 255 : (red < 0 ? 0 : red);
					
					green = green > 255 ? 255 : (green < 0 ? 0 : green);
					
					blue = blue > 255 ? 255 : (blue < 0 ? 0 : blue);
	
					 if (hasAlphaChannel) {
						 
						 int[] rgb = {red,green,blue,alpha};
						 
						 wr.setPixel(x - wMask, y - wMask,  rgb);
					 }
					 else {
						 
						 int[] rgb = {red,green,blue};
						 
						 wr.setPixel(x - wMask, y - wMask,  rgb);
					 }
				}
	    	}
	    	
	    	dCounter = 90.0;
	    	
			setProgress((int)dCounter);
			
			if(this.isCancelled()) { return null; }
			
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
		
    	 if(m_image != null && !m_dontChangeViewImage) {	
    		 
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
	
	
	void readByteData(byte[] data, int[][][] imData, boolean hasAlphaChannel, int wMax, int hMax, int wMask) {
	
		//read data from image raster
	    if (hasAlphaChannel) {
	    	
	       final int pixelLength = 4;
	       
	       for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
	          
	          imData[3][row+wMask][col+wMask] = ((int) data[pixel+3]); // alpha
	          
	          imData[2][row+wMask][col+wMask] = ((int) (data[pixel]& 0x00ff)); // blue
	          
	          imData[1][row+wMask][col+wMask] = ((int) (data[pixel + 1]& 0x00ff)); // green
	          
	          imData[0][row+wMask][col+wMask] = ((int) (data[pixel+2] & 0x00ff)); // red
	
	          col++;
	          
	          if (col == wMax) {
	        	  
	             col = 0;
	             
	             row++;
	             
	             if((row) >= hMax) { break;}
	          }
	       }
	    } else {
	    	
	       int pixelLength = 3;
	       
	       for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
	    	   
	    	  int rowIndex = row + wMask;
	    	  
	    	  int colIndex = col + wMask;
	    	  
	    	  imData[3][rowIndex][colIndex ] = 255; // alpha
	    	  
	          imData[2][rowIndex][colIndex ] = ((int) (data[pixel] & 0x00ff)); // blue
	          
	          imData[1][rowIndex][colIndex ] = ((int) (data[pixel + 1] & 0x00ff)); // green
	          
	          imData[0][rowIndex][colIndex ] = ((int) (data[pixel+2] & 0x00ff)); // red
	                  
	          col++;
	          
	          if (col == wMax) {
	        	  
	             col = 0;
	             
	             row++;
	             
	             if((row) >= hMax) { break;}
	          }
	       }
	    }
	
	}
	
	void readIntData(int[] data, int[][][] imData, int wMax, int hMax, int wMask) {
	
	       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
	          
	    	   int rgb = data[pixel];
	    	   
	          imData[3][row+wMask][col+wMask] = (rgb >>> 24); // alpha
	          
	          imData[2][row+wMask][col+wMask] = (rgb & 0x000000ff); // blue
	          
	          imData[1][row+wMask][col+wMask] = ((rgb & 0x0000ff00) >> 8); // green
	          
	          imData[0][row+wMask][col+wMask] = ((rgb & 0x00ff0000) >> 16); // red
	          
	          col++;
	          
	          if (col == wMax) {
	        	  
	             col = 0;
	             
	             row++;
	             
	             if((row) >= hMax) { break;}
	          }
	       }
	}
	
}
