
package PaAlgorithms;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import PaEditor.PaComplexValue;
import PaEditor.PaHeapArray;

/**
 * @author avd
 *
 */
public class PaFftAlgorithms {
	
	
	private double cosAr[] = {

			1.0,// k = 1 z = 0
			-1.0,// k = 2 z = 1
			6.123233995736766E-17,// k = 4 z = 2
			0.7071067811865476,// k = 8 z = 3
			0.9238795325112867,// k = 16 z = 4
			0.9807852804032304,// k = 32 z = 5
			0.9951847266721969,// k = 64 z = 6
			0.9987954562051724,// k = 128 z = 7
			0.9996988186962042,// k = 256 z = 8
			0.9999247018391445,// k = 512 z = 9
			0.9999811752826011,// k = 1024 z = 10
			0.9999952938095762,// k = 2048 z = 11
			0.9999988234517019,// k = 4096 z = 12
			0.9999997058628822,// k = 8192 z = 13
			0.9999999264657179,// k = 16384 z = 14
			0.9999999816164293,// k = 32768 z = 15
			0.9999999954041073// k = 65536 z = 16
				
				
		};
		
		private double sinAr[] = {
				0.0,// k = 1 z = 0sin
				0.0,// k = 2 z = 1sin
				1.0,// k = 4 z = 2sin
				0.7071067811865475,// k = 8 z = 3sin
				0.3826834323650898,// k = 16 z = 4sin
				0.19509032201612825,// k = 32 z = 5sin
				0.0980171403295606,// k = 64 z = 6sin
				0.049067674327418015,// k = 128 z = 7sin
				0.024541228522912288,// k = 256 z = 8sin
				0.012271538285719925,// k = 512 z = 9sin
				0.006135884649154475,// k = 1024 z = 10sin
				0.003067956762965976,// k = 2048 z = 11sin
				0.0015339801862847655,// k = 4096 z = 12sin
				7.669903187427045E-4,// k = 8192 z = 13sin
				3.8349518757139556E-4,// k = 16384 z = 14sin
				1.917475973107033E-4,// k = 32768 z = 15sin
				9.587379909597734E-5,// k = 65536 z = 16sin
		
		};
	
	
	
	
	static short mapBits[]= {
	    0x00, 0x80, 0x40, 0xC0, 0x20, 0xA0, 0x60, 0xE0,
	    0x10, 0x90, 0x50, 0xD0, 0x30, 0xB0, 0x70, 0xF0,
	    0x08, 0x88, 0x48, 0xC8, 0x28, 0xA8, 0x68, 0xE8,
	    0x18, 0x98, 0x58, 0xD8, 0x38, 0xB8, 0x78, 0xF8,
	    0x04, 0x84, 0x44, 0xC4, 0x24, 0xA4, 0x64, 0xE4,
	    0x14, 0x94, 0x54, 0xD4, 0x34, 0xB4, 0x74, 0xF4,
	    0x0C, 0x8C, 0x4C, 0xCC, 0x2C, 0xAC, 0x6C, 0xEC,
	    0x1C, 0x9C, 0x5C, 0xDC, 0x3C, 0xBC, 0x7C, 0xFC,
	    0x02, 0x82, 0x42, 0xC2, 0x22, 0xA2, 0x62, 0xE2,
	    0x12, 0x92, 0x52, 0xD2, 0x32, 0xB2, 0x72, 0xF2,
	    0x0A, 0x8A, 0x4A, 0xCA, 0x2A, 0xAA, 0x6A, 0xEA,
	    0x1A, 0x9A, 0x5A, 0xDA, 0x3A, 0xBA, 0x7A, 0xFA,
	    0x06, 0x86, 0x46, 0xC6, 0x26, 0xA6, 0x66, 0xE6,
	    0x16, 0x96, 0x56, 0xD6, 0x36, 0xB6, 0x76, 0xF6,
	    0x0E, 0x8E, 0x4E, 0xCE, 0x2E, 0xAE, 0x6E, 0xEE,
	    0x1E, 0x9E, 0x5E, 0xDE, 0x3E, 0xBE, 0x7E, 0xFE,
	    0x01, 0x81, 0x41, 0xC1, 0x21, 0xA1, 0x61, 0xE1,
	    0x11, 0x91, 0x51, 0xD1, 0x31, 0xB1, 0x71, 0xF1,
	    0x09, 0x89, 0x49, 0xC9, 0x29, 0xA9, 0x69, 0xE9,
	    0x19, 0x99, 0x59, 0xD9, 0x39, 0xB9, 0x79, 0xF9,
	    0x05, 0x85, 0x45, 0xC5, 0x25, 0xA5, 0x65, 0xE5,
	    0x15, 0x95, 0x55, 0xD5, 0x35, 0xB5, 0x75, 0xF5,
	    0x0D, 0x8D, 0x4D, 0xCD, 0x2D, 0xAD, 0x6D, 0xED,
	    0x1D, 0x9D, 0x5D, 0xDD, 0x3D, 0xBD, 0x7D, 0xFD,
	    0x03, 0x83, 0x43, 0xC3, 0x23, 0xA3, 0x63, 0xE3,
	    0x13, 0x93, 0x53, 0xD3, 0x33, 0xB3, 0x73, 0xF3,
	    0x0B, 0x8B, 0x4B, 0xCB, 0x2B, 0xAB, 0x6B, 0xEB,
	    0x1B, 0x9B, 0x5B, 0xDB, 0x3B, 0xBB, 0x7B, 0xFB,
	    0x07, 0x87, 0x47, 0xC7, 0x27, 0xA7, 0x67, 0xE7,
	    0x17, 0x97, 0x57, 0xD7, 0x37, 0xB7, 0x77, 0xF7,
	    0x0F, 0x8F, 0x4F, 0xCF, 0x2F, 0xAF, 0x6F, 0xEF,
	    0x1F, 0x9F, 0x5F, 0xDF, 0x3F, 0xBF, 0x7F, 0xFF,
	};
	
	
	
	double[][] testAr = {
		{0,0,0,0,0,0,0,0,0,0,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{1,1,1,1,1,1,1,1,1,1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
		{2,2,2,2,2,2,2,2,2,2,2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{3,3,3,3,3,3,3,3,3,3,3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
		{4,4,4,4,4,4,4,4,4,4, 4,4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
		{5,5,5,5,5,5,5,5,5,5, 5,5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
		{6,6,6,6,6,6,6,6,6,6, 6,6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6},
		{7,7,7,7,7,7,7,7,7,7, 7,7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{8,8,8,8,8,8,8,8,8,8, 8,8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
		{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
		{10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10},
		{11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11},
		{12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12},
		{13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13},
		{14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14},
		{15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15},
		{16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16},
		{17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17},
		{18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18},
		{19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19},
		{20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20},
		{21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21},
		{22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22},
		{23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23,23},
		{24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24},
		{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25},
		{26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26},
		{27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27,27},
		{28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28},
		{29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29},
		{30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30},
		{31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31}
	};

	double[][] testAr2 = {
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31},	
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31}
		};
	
		/**
		 * Reflects the value from range min - max to a new range minNew - maxNew
		 * @param minNew - min point of new range
		 * @param maxNew - max point of new range
		 * @param min - min point of old range
		 * @param max - max point of old range
		 * @param value - value to reflect
		 * @return new value in the range  minNew - maxNew
		 */
		public double getInNewRange(double maxNew, double minNew, double max, double min, double value) {
			
			double a = (maxNew-minNew)/(max-min);
			
			double b = maxNew - a*max;
			
			return a*value +b;
		}
	
	
	public BufferedImage performBlur(BufferedImage im, double xVel,double yVel,double expTime) {
		
		int xN = im.getWidth();  
		
		int yN = im.getHeight();
		
		int[] a  = getPowerOf2_MaxValue(xN,yN); //expand with zeros up to the size the power of 2
		
		int nT2 = a[1];
		
		int n2 = a[0]; //the power of 2
		
		int shift = 32 - n2; // auxiliary bits shift
		
		//RGB data, rearranged along x
		
		PaHeapArray<Double> im_Alpha  = new PaHeapArray<Double>(nT2,nT2);

		PaHeapArray<Double> imRed_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imRed_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imGreen_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imGreen_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imBlue_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> imBlue_Im = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> flData_Rl = new PaHeapArray<Double>(nT2,nT2);
		
		PaHeapArray<Double> flData_Im = new PaHeapArray<Double>(nT2,nT2);
	

		int x, J;

		for(int y = 0; y < nT2; y++) {

	    	int rgb = im.getRGB(0, y);
	    	
	    	double red = 0.0;
	    	
			double green = 0.0;
			
			double blue = 0.0;
			
			int alpha = 0;
	    	
	    	if(y < yN) {	   
	    		
		    	red = ((rgb & 0xff0000) >> 16);
		    	
				green = (rgb & 0xff00) >> 8;
				
				blue = rgb & 0xff;
	    	}
				
			if( (y)%2 != 0)  {
				
				imRed_Rl.set(y,0,-red); //centring (-1)**(x+y)
				
				imGreen_Rl.set(y,0,-green);
				
				imBlue_Rl.set(y,0,-blue);
			}
			else { 
				
	  			imRed_Rl.set(y,0,red); //centring (-1)**(x+y)
	  			
				imGreen_Rl.set(y,0,green);
				
				imBlue_Rl.set(y,0,blue);
			}
			
			imRed_Im.set(y,0,0.0);   imGreen_Im.set(y,0,0.0);   imBlue_Im.set(y,0,0.0);  im_Alpha.set(y,0,0.0);
		
			for(x = 1; x < nT2 - 1; x++)
			{
			   //4 bytes of x value are reflected to the reverse array mapBits 
			   J = (mapBits[(int)(x & 0xff000000) >> 24]) | (mapBits[(int)(x & 0x00ff0000) >> 16] << 8) | 
					   (mapBits[(int)(x & 0x0000ff00)>>8] << 16) | (mapBits[(int)x & 0x000000ff] << 24);

			    J = J >>> shift; //important to perform unsigned shift
					   
			    if (x < J)
			    {
			    	
			    	red = 0.0;
			    	
					green = 0.0;
					
					blue = 0.0;
					
					alpha = 0;
			    	
			    	
			    	if(x < xN && y < yN) {
			    		
			    		rgb = im.getRGB(x, y);
			    		
				    	red = ((rgb & 0xff0000) >> 16);
				    	
						green = (rgb & 0xff00) >> 8;
			    
						blue = rgb & 0xff;
						
						alpha = rgb >>> 24;
			    	}
			    	
			    	im_Alpha.set(y,0,(double)alpha);
						
		    		if( (x+y)%2 != 0)  {
		    			
		    			imRed_Rl.set(y,J,-red); //centring (-1)**(x+y)
		    			
		    			imGreen_Rl.set(y,J,-green);
		    			
		    			imBlue_Rl.set(y,J,-blue);
		    		}
		    		else { 
		    			
		      			imRed_Rl.set(y,J,red); //centring (-1)**(x+y)
		      			
		    			imGreen_Rl.set(y,J,green);
		    			
		    			imBlue_Rl.set(y,J,blue);
		    		}
		    		
		    		
		    	 	red = 0.0;
		    	 	
					green = 0.0;
					
					blue = 0.0;
			    	
			    	if(J < xN && y < yN) {
			    		
			    		rgb = im.getRGB(J, y);
			    		
				    	red = ((rgb & 0xff0000) >> 16);
				    	
						green = (rgb & 0xff00) >> 8;
						
						blue = rgb & 0xff;
			    	}    
		    		if( (J+y)%2 != 0)  { 
		    			
		    			imRed_Rl.set(y,x,-red); //centring (-1)**(x+y)
		    			
		    			imGreen_Rl.set(y,x,-green);
		    			
		    			imBlue_Rl.set(y,x,-blue);
		    		}
		    		else {
		    			
		    			imRed_Rl.set(y,x,red); //centring (-1)**(x+y)
		    			
		    			imGreen_Rl.set(y,x,green);
		    			
		    			imBlue_Rl.set(y,x,blue);
		    		}
		    		
	    			imRed_Im.set(y,x,0.0); imGreen_Im.set(y,x,0.0);    imBlue_Im.set(y,x,0.0);
	    			
			    	
					double[] t0 = blurFilterFunction(x,y,xVel,yVel,expTime); 
					
					double[] t1 = blurFilterFunction(J,y,xVel,yVel,expTime);
					
					flData_Rl.set(y,x,t1[0]);//*k;
					
					flData_Im.set(y,x,t1[1]);//*k;
					
					flData_Rl.set(y,J,t0[0]);//*k;
					
					flData_Im.set(y,J,t0[1]);//*k;
	
			    }
			}
			

	  		imRed_Rl.set(y,nT2 - 1,0.0);   imGreen_Rl.set(y,nT2 - 1,0.0);   imBlue_Rl.set(y,nT2 - 1,0.0);
	  		
			imRed_Im.set(y,nT2 - 1,0.0);   imGreen_Im.set(y,nT2 - 1,0.0);   imBlue_Im.set(y,nT2 - 1,0.0);
		
			
			double[] t0 = blurFilterFunction(nT2 - 1,y,xVel,yVel,expTime); 
			
			flData_Rl.set(y,x,t0[0]);//*k;
			
			flData_Im.set(y,x,t0[1]);//*k;
		}
		 //here we got the ready data for first FFT, color data have been reaaranged long x
		//lists are used for processing some arrays 
		ArrayList<PaHeapArray<Double>> l_Rl = new ArrayList<PaHeapArray<Double>>();
		
		ArrayList<PaHeapArray<Double>> l_Im = new ArrayList<PaHeapArray<Double>>();
		
		
		//set direct or backward FFT in lists l_Rl, l_Im
		ArrayList<Integer> directionMap = new ArrayList<Integer>(); 
		
		l_Rl.add(imRed_Rl);   l_Im.add(imRed_Im);	directionMap.add(0);	
		
		l_Rl.add(imGreen_Rl); l_Im.add(imGreen_Im); directionMap.add(0);	
		
		l_Rl.add(imBlue_Rl);  l_Im.add(imBlue_Im);	directionMap.add(0);	
		
		//initial filter was in frequency area, to  return it - backward FFT
		l_Rl.add(flData_Rl);  l_Im.add(flData_Im);  directionMap.add(1); 
		
	
		//data in flData_Rl flData_Im arein space domain now; here - centring and zero padding 
		for ( int i = 0; i < nT2; ++i) {
			
			for ( int j = 0; j < nT2; ++j) {
				
				 if(i >= xN || j >= yN) {
					 
					 flData_Im.set(j,i,0.0);
					 
					 flData_Im.set(j,i,0.0);
				 }
				 else{
					 
					 if((i+j)%2 != 0) {
						 
						 flData_Im.set(j,i,-flData_Rl.get(j,i));
						 
						 flData_Im.set(j,i,-flData_Im.get(j,i));
					 } 			
				 }
				
			}
		}
		//*** now rearange x and back into frequency domain flData_Rl flData_Im
		l_Rl.clear(); 		  l_Im.clear();  directionMap.clear();
		
		l_Rl.add(flData_Rl);  l_Im.add(flData_Im);   directionMap.add(0); //direct FFT


		//****use filter  - multiplication data in freq domain and filter data
		for ( int x1 = 0; x1 < nT2; ++x1) {
			
			for ( int y1 = 0; y1 < nT2; ++y1) {
	
				double[] t = PaComplexValue.mul(imRed_Rl.get(y1,x1),imRed_Im.get(y1,x1),
						flData_Rl.get(y1,x1),flData_Im.get(y1,x1));

				imRed_Rl.set(y1,x1,t[0]);
				
				imRed_Im.set(y1,x1,t[1]);
				
				t = PaComplexValue.mul(imGreen_Rl.get(y1,x1),imGreen_Im.get(y1,x1),
						flData_Rl.get(y1,x1),flData_Im.get(y1,x1));

				imGreen_Rl.set(y1,x1,t[0]);
				
				imGreen_Im.set(y1,x1,t[1]);
				
				t = PaComplexValue.mul(imBlue_Rl.get(y1,x1),imBlue_Im.get(y1,x1),
						flData_Rl.get(y1,x1),flData_Im.get(y1,x1));

				imBlue_Rl.set(y1,x1,t[0]);
				
				imBlue_Im.set(y1,x1,t[1]);
				
			}			
		}
		
		//***rearange x and back into space domain of color data
		l_Rl.clear(); 		  l_Im.clear();  directionMap.clear();
		
		l_Rl.add(imRed_Rl);   l_Im.add(imRed_Im);	directionMap.add(1);	
		
		l_Rl.add(imGreen_Rl); l_Im.add(imGreen_Im); directionMap.add(1);	
		
		l_Rl.add(imBlue_Rl);  l_Im.add(imBlue_Im);	directionMap.add(1);
			
		
		//post processing - centring and brightness restoration
		BufferedImage targetImage = new BufferedImage(xN,yN,BufferedImage.TYPE_INT_RGB);
		
		double max[] = {0.0,0.0,0.0}; // 0 - red, 1- green, 2- blue
		
		double min[] = {0.0,0.0,0.0}; // 0 - red, 1- green, 2- blue
		
		for ( int x1 = 0; x1 < xN; ++x1) {
			
			for ( int y1 = 0; y1 < yN; ++y1) {
				
				double r  = imRed_Rl.get(y1,x1);
				
				double g  = imGreen_Rl.get(y1,x1);
				
				double b  = imBlue_Rl.get(y1,x1);
				
				if((x1+y1)%2 !=0 ) {
					
					r  = -r;//centring back
					
					g  = -g;//centring back
					
					b  = -b;//centring back
					
					imRed_Rl.set(y1,x1,r);
					
					imGreen_Rl.set(y1,x1,g);
					
					imBlue_Rl.set(y1,x1,b);
				 
				}
				
				if(r > max[0]) max[0] = r;
				
				if(r < min[0]) min[0] = r;
				
				if(g > max[1]) max[1] = g;
				
				if(g < min[1]) min[1] = g;
				
				if(b > max[2]) max[2] = b;
				
				if(b < min[2]) min[2] = b;					
			}
		}
		
		
		for ( int x1 = 0; x1 < xN; ++x1) {
			
			for ( int y1 = 0; y1 < yN; ++y1) {
	
				//stretching the brightness range
				int r = (int)((imRed_Rl.get(y1,x1)-min[0])*255.0/(max[0]-min[0]));
				
				int g = (int)((imGreen_Rl.get(y1,x1)-min[1])*255.0/(max[1]-min[1]));
				
				int b = (int)((imBlue_Rl.get(y1,x1)-min[2])*255.0/(max[2]-min[2]));

				r = r > 255 ? 255 : (r < 0 ? 0 : r);
				
				g = g > 255 ? 255 : (g < 0 ? 0 : g);
				
				b = r > 255 ? 255 : (b < 0 ? 0 : b);
				
				Color newColor =  new Color(r,g,b,im_Alpha.get(y1, x1).intValue());
		
				targetImage.setRGB(x1, y1, newColor.getRGB());	
			}
		}
		//memory free
		im_Alpha.free();   imRed_Rl.free();  imRed_Im.free();
		
		imGreen_Rl.free(); imGreen_Im.free();
		
		imBlue_Rl.free();  imBlue_Im.free();
		
		flData_Rl.free();  flData_Im.free();
		
		return targetImage;
		
	}


	/**
	 * 
	 */
	public PaFftAlgorithms() {

	}
		/**
		 * Makes fft for two dimension data; results are saved into arrays with data; initial data are lost after this operation
		 * @param l_Rl - list of array with real part of data; all list must be ready for x operation - the first reverse of data (in x direction)
		 *  must be done before call this function
		 * @param l_Im - list of arrays with imagine part of data
		 * @param  fftDirectionMap - list of FFT directions for every data in l_Im, l_Rl; 0 - direct FFT, 1 - backward FFT
		 * @param nT2 - the size of the data (PaHeapArray inside lists must be the same nT2 x nT2 size), all arrays in the list must be the same size
		 * @param n2 - the power of 2 for nT2 : nT2 = 2**n2
		 */
		public void fft2D(double[][] l_Rl, double[][] l_Im, 
				boolean direction, int nT2, int n2) {
		
			double pi2 = 2 * 3.1415926535897932384626433832795;
	
			int m, m_2, j, mpNd2, k, p;
			
 			double v0R ;
 			
			double v0I ;
			
			double vR = 1;
			
			double vI = 0;
			
 			double[] t = new double[2];
 			
 			//rearrange along x
			dataReverse(l_Rl, l_Im, nT2, n2, true);
			
			for(int y = 0; y < nT2; y++) {
			
				for(m = 2, m_2 = 1, p = 1; m <= nT2; ++p, m_2 = m, m += m)
				{				
				    //double an = pi2/m;
				    if(direction) {
				    	
				    	v0R = cosAr[p];
				    	
				    	v0I = -sinAr[p];
				    	
				    } else {
				    	
				     	v0R = cosAr[p];
				     	
					    v0I = sinAr[p];
				    }
				
					vR = 1;
					
					vI = 0;
					
				    for(j = 0; j <= m_2-1; j++)
				    {
				         for(k = j; k < nT2; k += m)
				         {
				             mpNd2 = k + m_2;
			
				 			t = PaComplexValue.mul(vR,vI,l_Rl[y][mpNd2],l_Im[y][mpNd2]);
				 		
				             //Temp = W * x[mpNd2];
				             l_Rl[y][mpNd2] =  l_Rl[y][k] - t[0];
				             
				             l_Im[y][mpNd2] =  l_Im[y][k] - t[1];//0-t[1]
				             
				             //x[mpNd2] = x[m] - Temp;
				             l_Rl[y][k] =  l_Rl[y][k] + t[0];
				             
				             l_Im[y][k] =  l_Im[y][k] + t[1]; //0+t[1]
				             //x[m] = x[m] + Temp;
				             
				             
				         }
				         
				         t = PaComplexValue.mul(vR,vI,v0R,v0I);
				         
			             vR = t[0]; vI = t[1];
				         
				    }
				}
			}
			
			//перестановка по y
			dataReverse(l_Rl, l_Im, nT2, n2, false);

			//прогонка по y 
		
			for(int y = 0; y < nT2; y++) {
				
				for(m = 2, m_2 = 1; m <= nT2; m_2 = m, m += m){
					
				    double an = pi2/m;
				   
				    if(direction) {
				    	
				    	v0R = Math.cos(an);
				    	
				    	v0I = -Math.sin(an);
				    }
				    else {
				        v0R = Math.cos(an);
				        
					    v0I = Math.sin(an);
				    }
			
				    vR = 1;
				    
				    vI = 0;
				
				    for(j = 0; j <= m_2-1; j++)
				    {						         
				         for(k = j; k < nT2; k += m)
				         {
				             mpNd2 = k + m_2;
				       
				 			 t = PaComplexValue.mul(vR,vI,l_Rl[mpNd2][y],l_Im[mpNd2][y]);
				 		
				             //Temp = W * x[mpNd2];
				             l_Rl[mpNd2][y] =  l_Rl[k][y] - t[0];
				             
				             l_Im[mpNd2][y] =  l_Im[k][y] - t[1];//0-t[1]
				             
				             //x[mpNd2] = x[m] - Temp;
				             l_Rl[k][y] =  l_Rl[k][y] + t[0];
				             
				             l_Im[k][y] =  l_Im[k][y] + t[1]; //0+t[1]
				             //x[m] = x[m] + Temp;
				             
				         }
				         t = PaComplexValue.mul(vR,vI,v0R,v0I);
				         
			             vR = t[0]; vI = t[1];
			             
				    }
				} 
			}
		}
		
		/**
		 * Gets the closest number which is a power of 2 and > then max dimension xN,yN
		 * @param xN - x dimension
		 * @param yN - y dimension  
		 * @return the array [0] - power of 2; [1] - max value over Xn,yN
		 */
		public  int[] getPowerOf2_MaxValue(int xN, int yN) {
				
			int[] a = new int[2];
			
			int max = yN;
			
			if( xN > yN) max =xN;
			
			int nT = 4 ;
			
			int n = 2;
			
			while(nT < max) {
				
				nT = nT*2;
				
				++n;
			}
			
			a[0] = n;
			
			a[1] = nT;
			
			return a;
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
		/**
		 * Performs data exchange as a stage of FFT iterative algorithm
		 * @param l_Rl - list with real part data; l_Rl and l_Im must be synchronized - l_Rl[i] and l_Im[i] must have the physical data for one specific object
		 * @param l_Im - list with imagine part data
		 * @param nT2 - size of data, only equal data available nT2 x nT2; nT2 must be power of 2
		 * @param n2 - power of 2 for nT2; nT2 = 2**n2
		 * @param xRev - true for x direction exchange, false for y direction exchange
		 */
		private void dataReverse(double[][] l_Rl, 
				double[][] l_Im, int nT2, int n2, boolean xRev) {
		
			int  J;
			
			int shift = 32- n2;
			
		   	double dataM_Rl0; 
		   	
 			double dataM_Im0; 
 			
 	       	double dataM_Rl1; 
 	       	
 			double dataM_Im1; 
			
    	    //x direction ; take into account that arrays PaHeapArray has different x,y notation - y - first; x - second argument - in contrary with
    	    //BufferedImage class - in it there is a standard order - x,y
			if(xRev) { 
				
				for(int y = 0; y < nT2; y++) {
					
					for(int x = 1; x < nT2 - 1; x++)
					{
						   //4 bytes of x value are reflected to the reverse array mapBits 
						J = (mapBits[(int)(x & 0xff000000) >> 24]) | (mapBits[(int)(x & 0x00ff0000) >> 16] << 8) | 
								(mapBits[(int)(x & 0x0000ff00)>>8] << 16) | (mapBits[(int)x & 0x000000ff] << 24);
		
						J = J >>> shift; //important to perform unsigned shift
					    if (x < J)
					    {	 
			              	 dataM_Rl0 = l_Rl[y][J]; 
			              	 
				 			 dataM_Im0 = l_Im[y][J]; 
				 			 
				 	       	 dataM_Rl1 = l_Rl[y][x]; 
				 	       	 
				 			 dataM_Im1 = l_Im[y][x]; 
				 			 
				 	    	 l_Rl[y][x] = dataM_Rl0; 
				 	    	 
				 			 l_Im[y][x] = dataM_Im0; 
				 			 
				 			 l_Rl[y][J] = dataM_Rl1; 
				 			 
				 			 l_Im[y][J] = dataM_Im1; 
					    }
					}
				}
			}
			else { //y direction
				for(int x = 0; x < nT2; x++) {
					
					for(int y = 1; y < nT2 - 1; y++)
					{
						   //4 bytes of x value are reflected to the reverse array mapBits 
						J = (mapBits[(int)(y & 0xff000000) >>> 24]) | (mapBits[(int)(y & 0x00ff0000) >> 16] << 8) | 
								(mapBits[(int)(y & 0x0000ff00)>>8] << 16) | (mapBits[(int)y & 0x000000ff] << 24);
		
						J = J >>> shift; //important to perform unsigned shift
					    if (y < J)
					    {
			              	 dataM_Rl0 = l_Rl[J][x]; //red
			              	 
				 			 dataM_Im0 = l_Im[J][x]; //red
				 			 
				 	       	 dataM_Rl1 = l_Rl[y][x]; //red
				 	       	 
				 			 dataM_Im1 = l_Im[y][x]; //red
				 			 
				 	    	 l_Rl[y][x] = dataM_Rl0; 
				 	    	 
				 			 l_Im[y][x] = dataM_Im0; 
				 			 
				 			 l_Rl[J][x] = dataM_Rl1 ; 
				 			 
				 			 l_Im[J][x] = dataM_Im1; 
					    }
					}
				}
			}
		}
		

		/**
		 * @param rl - real part of image data
		 * @param im - imaginary part of image data 
		 * @param direct - true for direct Furie transition and false for backward Furie transition
		 * @param nTx - x direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
		 * @param nTY - y direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
		 * @param FuvRl - resulting real part of Furies transition
		 * @param FuvIm  - resulting imaging part of Furies transition
		 */
		public  void getFFT(boolean direct, PaHeapArray<Double> rl, PaHeapArray<Double> im,
				int nTx,int nTy,PaHeapArray<Double> FuvRl,PaHeapArray<Double> FuvIm, 
				double[] exp_rl,double[] exp_im)
		{
			
			double[] yRl = new double[nTy];
			
			double[] yIm  = new double[nTy];
			
			PaHeapArray<Double> FxvRl = new PaHeapArray<Double>(nTy,nTx);//real part
			
			PaHeapArray<Double> FxvIm = new PaHeapArray<Double>(nTy,nTx);//imaginary part
			
			//firstly FFT for columns

			for (int x = 0; x < nTx; ++x) {
				

				xFFT(direct, false,x, rl,im,nTy,0,1,yRl,yIm, exp_rl,exp_im) ;
		
				
				for(int v = 0; v < nTy; ++v)  {
					
					if(direct) {
						
						FxvRl.set(v,x,yRl[v]/nTy);
						
						FxvIm.set(v,x,yIm[v]/nTy);
					}
					else {
						
						FxvRl.set(v,x,yRl[v]);
						
						FxvIm.set(v,x,yIm[v]);
					}
				}
			
			}

			//now FFT for rows
			for ( int v = 0; v < nTy; ++v) {
				
				xFFT(direct,true,v,rl,im,nTx,0,1,yRl, yIm,exp_rl,exp_im) ;
				
				for(int u = 0; u < nTx; ++u)  {
					
					if(direct) {
						
						FuvRl.set(v,u,yRl[u]/(nTx));
						
						FuvIm.set(v,u,yIm[u]/(nTx));
					}
					else {
						
						FuvRl.set(v,u,yRl[u]);
						
						FuvIm.set(v,u,yIm[u]);
					}
				}			
			}
			
			FxvRl.free();
			
			FxvIm.free();
		}
		
		
		public  void xFFT(boolean direct, boolean xDirection,int xy, PaHeapArray<Double> aRl,PaHeapArray<Double> aIm, 
				int n, int firstIndex, int step, double[] FxvRl, double[] FxvIm, double[] exp_rl,double[] exp_im) {
			
	
				if (n == 1) { 
	
				if(xDirection) {
					
					FxvRl[0] = aRl.get(xy,firstIndex);//red
					
					FxvIm[0] = aIm.get(xy, firstIndex);
					
				}
				else {
					
					FxvRl[0] = aRl.get(firstIndex,xy);//red
					
					FxvIm[0] = aIm.get(firstIndex,xy);
				}
				
				return;
			}
	
			double v0I;
			
			if(direct) v0I = -exp_im[n];
			
			else v0I = exp_im[n];
			
			double v0R = exp_rl[n];
			
			
			double vR = 1;
			
			double vI = 0.0;
			
			int nNext = n/2;
			
			double[] y0R = new double[nNext]; double[] y0I = new double[nNext];
			
			double[] y1R = new double[nNext]; double[] y1I = new double[nNext];

			xFFT(direct,xDirection,xy,aRl,aIm,nNext,firstIndex,step*2,y0R,y0I,exp_rl,exp_im);

			xFFT(direct,xDirection,xy,aRl,aIm,nNext,firstIndex+step, step*2,y1R,y1I,exp_rl,exp_im);

			
			for(int k = 0 ; k < nNext; ++k ) {
				
					double[] t = PaComplexValue.mul(vR,vI,y1R[k],y1I[k]);
					
					FxvRl[k] = y0R[k] + t[0]; 
					
					FxvIm[k] = y0I[k] + t[1];
			
					FxvRl[k+n/2] = y0R[k] - t[0]; 
					
					FxvIm[k+n/2] = y0I[k] - t[1];	
					
					double[] t1 = PaComplexValue.mul(vR,vI,v0R,v0I);
					
					vR = t1[0];
					
					vI = t1[1];
			}	
		}

		
		/**
		 * 
		 * @param im - source image
		 * @param level
		 * @return
		 */
		public BufferedImage getBlur(BufferedImage im, int level) {
			

			int im_width = im.getWidth();  
			
			int im_height =  im.getHeight();
			
			int min1 = im_width;
			
			if(im_height < im_width) min1 = im_height;
			
			if(level > 99) level = 99;
			
			int radius = (int)(min1*(100-level)/100.0)/2;
			
		
			//multiplication on 2 is because we need to avoid of edge filter effects while FFT
			int d2data[] = getPowerOf2_MaxValue(2*im_width,2*im_height); //zero padding up to power of 2
		
			int nT2 = d2data[1];
			
			@SuppressWarnings("unused")
			int n2 = d2data[0];
			
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
			
			int deltaX = (nT2 - im_width)/2;
			
			if(deltaX > 0) --deltaX;
			
			int deltaY = (nT2 - im_height)/2;
			
			if(deltaY > 0) --deltaY;
			

			//long time_op  = System.currentTimeMillis();
			
			int rgb_data = im.getRGB(2, 2);
			
			double r_data = (double)((rgb_data & 0x00ff0000) >> 16);
			
			double g_data = (double)((rgb_data & 0x0000ff00) >> 8);
			
			double b_data = (double)(rgb_data & 0x000000ff);
			
			double[] maxData = {r_data,g_data,b_data};
			
			double[] minData = {r_data,g_data,b_data};

			int x1 = 0; 
			
			int y1 = 0;
			
			int nT2_1 = nT2+1;
			
			double pi2 = 2.0*Math.PI;
			
			double[] expN_Rl = new double[nT2_1];
			
			double[] expN_Im = new double[nT2_1];

			for ( int i = 0; i < nT2; ++i) {
				
				int z = i + 1;
				
				if(z <= nT2_1 && z%2 == 0) {       //[2] -2,[4] - 4, [8] - 8, [16] - 16, [32] - 32
					
					double an =	pi2/z; 
					
					expN_Rl[z] = Math.cos(an);
					
					expN_Im[z] = Math.sin(an);
				}
		
				for ( int j = 0; j < nT2; ++j) {
					
					 int i_j = i + j;
					 
					 x1 = i - deltaX;
					 
					 y1 = j - deltaY;
					 
					 Red_Im.set(j,i,0.0);
					 
					 Green_Im.set(j,i,0.0);
					 
					 Blue_Im.set(j,i,0.0);
					 				
					 if(x1 < 0 || y1 < 0 || x1 >= im_width || y1 >= im_height) {	
						 
						 Red_Rl.set(j,i,0.0);
						 
						 Green_Rl.set(j,i,0.0);
						 
						 Blue_Rl.set(j,i,0.0);
					 }
					 else{	
						 
						 int rgb = 0;
						 
							 rgb = im.getRGB(x1, y1);
					
							double rD = (double)((rgb & 0x00ff0000) >> 16);
							
							double gD = (double)((rgb & 0x0000ff00) >> 8);
							
							double bD = (double)(rgb & 0x000000ff);
							
						Alpha_Rl.set(y1,x1,(double)(rgb >>> 24));
				
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
			
			//debugImage1(Blue_Rl, null, nT2, nT2, "D:\\bbbbb1.jpg");
			
			getFFT(true,Red_Rl,Red_Im,nT2,nT2,Red_Rl2,Red_Im2,expN_Rl,expN_Im);
			
			getFFT(true,Green_Rl,Green_Im,nT2,nT2,Green_Rl2,Green_Im2,expN_Rl,expN_Im);
			
			getFFT(true,Blue_Rl,Blue_Im,nT2,nT2,Blue_Rl2,Blue_Im2,expN_Rl,expN_Im);
			
			//debugImage1(Blue_Rl2,Blue_Im2, nT2, nT2, "D:\\aaSpectr.jpg");
			
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
					
					 Red_Rl2.set(j,i,0.0);
					 
					 Green_Rl2.set(j,i,0.0);
					 
					 Blue_Rl2.set(j,i,0.0);
					
					 Red_Im2.set(j,i,0.0);
					 
					 Green_Im2.set(j,i,0.0);
					 
					 Blue_Im2.set(j,i,0.0);
				}
			}
	
			for ( int j = deltaY1; j < dY1; ++j) {
				
				for ( int i = 0; i < deltaX1; ++i) {
			
					Red_Rl2.set(j,i,0.0); Green_Rl2.set(j,i,0.0);Blue_Rl2.set(j,i,0.0);
					
					Red_Im2.set(j,i,0.0);Green_Im2.set(j,i,0.0);Blue_Im2.set(j,i,0.0);
				}
				
				for ( int i = dX1; i < nT2; ++i) {	
					
					 Red_Rl2.set(j,i,0.0);Green_Rl2.set(j,i,0.0);Blue_Rl2.set(j,i,0.0);
					 
					 Red_Im2.set(j,i,0.0);Green_Im2.set(j,i,0.0);Blue_Im2.set(j,i,0.0);
				}
			}
			
			int cX = nT2/2;
			
			Point c = new Point(cX,cX);
			
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
			
			//debugImage1(Blue_Rl2,Blue_Im2, nT2, nT2, "D:\\ccSpectr.jpg");

			getFFT(false,Red_Rl2,Red_Im2,nT2,nT2,Red_Rl,Red_Im,expN_Rl,expN_Im);
			
			getFFT(false,Green_Rl2,Green_Im2,nT2,nT2,Green_Rl,Green_Im,expN_Rl,expN_Im);
			
			getFFT(false,Blue_Rl2,Blue_Im2,nT2,nT2,Blue_Rl,Blue_Im,expN_Rl,expN_Im);
			

			double red = Red_Rl.get(deltaY+1,deltaX+1);
			
			double green = Green_Rl.get(deltaY+1,deltaX+1);
			
			double blue = Blue_Rl.get(deltaY+1,deltaX+1);
			
			if((deltaY+1 + deltaX+1)%2 != 0) {
				
				red = -red;
				
				green = - green;
				
				blue = - blue;
			}
			
			double[] max = {red,green,blue};
			
			double[] min = {red,green,blue};
			

			for ( int x = 0; x < im_width; ++x) {
				
				for ( int y = 0; y < im_height; ++y) {
					
					int j = y + deltaY;
					
					int i = x + deltaX;
					
					int i_j = i+j;
					//red =0; green = 0; blue = 0;
					
			
					red = Red_Rl.get(j,i);
					
					green = Green_Rl.get(j,i);
					
					blue = Blue_Rl.get(j,i);
				
					
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
			
			double allMin = min[0];
			
			if(min[0] < min[1] && allMin < min[2]) allMin = min[0];
			
			if(min[1] < min[0] && min[1] < min[2]) allMin = min[1];
			
			if(min[2] < min[0] && min[2] < min[1]) allMin = min[2];
			
			min[0] -= allMin; min[1] -= allMin; min[2] -= allMin;
			
			max[0] -= allMin;  max[1] -= allMin;  max[2] -= allMin;
			
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
			
			int j;
			
			int i;
			
			for ( int x = 0; x < im_width; ++x) {
				
				for ( int y = 0; y < im_height; ++y) {
					
					j = y + deltaY;
					
					i = x + deltaX;
					
					r = (int)(aRed*Red_Rl.get(j,i).intValue() + bRed);
					
					g = (int)(aGreen*Green_Rl.get(j,i).intValue()+bGreen);
					
					b = (int)(aBlue*Blue_Rl.get(j,i).intValue()+bBlue);
					
					int rgb = r << 16 | g << 8 | b |  Alpha_Rl.get(y, x).intValue() << 24;
			
					targetImage.setRGB(x, y, rgb/*newColor.getRGB()*/);	
				}
			}

			return targetImage;
		}
		
		
		
		public  void readData( BufferedImage im,double[][] a_rl, double[][] a_im, double[][] alpha, int color) {
			
			if(im == null) { return; }
			
			int xN = im.getWidth();  
			
			int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			
		       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
			          	    	
			    	int rgb = data[pixel];
			    	
			    	alpha[row][col] = (rgb >>> 24);
			    	
			    	a_im [row][col] = 0.0;
			    	
			    	 switch(color) {
			    	 
			    	 	case 0: {
			    	 		
			    	 		a_rl[row][col] = ((rgb & 0x00ff0000) >> 16);
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		a_rl[row][col] = ((rgb & 0x0000ff00) >> 8);
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 		a_rl[row][col] = (rgb & 0x000000ff);
			    	 		
			    	 		break;
			    	 	}
			    	 
			    	 } 
			    	 
			        col++;
			        
			        if (col == xN) {
			        	
			             col = 0;
			             
			             row++;
			             
			             if((row) >= yN) { break;}
			        }
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			      
		        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
          			          
		        	a_im [row][col] = 0.0;
		        	
					switch(color) {
					
			    	 	case 0: {
			    	 		
			    	 		a_rl[row][col] = ((data[pixel+2] & 0x00ff));
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		a_rl[row][col] = ((data[pixel + 1]& 0x00ff));
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 		a_rl[row][col] = ((data[pixel]& 0x00ff));
			    	 		
			    	 		break;
			    	 	}
					}
					
					if(hasAlphaChannel) {
						
						alpha[row][col] = ((data[pixel+3] & 0x00ff));
					}
							                  
		            col++;
		            
		            if (col == xN) {
		            	
		              col = 0;
		              
		              row++;
		              
		              if((row) >= yN) { break;}
		            }
		       }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
			}
	}
		/**
		 *  Doesn't care about alpha channel!
		 * @param im
		 * @param a_rl
		 * @param a_im
		 * @param color
		 * @param minMax
		 */
public  void readCenteredData( BufferedImage im, double[][] a_rl, double[][] a_im, int color, double[] minMax) {
			
			if(im == null) { return; }
			
			int xN = im.getWidth();  
			
			int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			
		       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
			       
			    	int rgb = data[pixel];
			    	
			    	a_im [row][col] = 0.0;
			    	
			    	 switch(color) {
			    	 
			    	 	case 0: {
			    	 		
			    	 		a_rl[row][col] = ((rgb & 0x00ff0000) >> 16);
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		a_rl[row][col] = ((rgb & 0x0000ff00) >> 8);
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 		a_rl[row][col] = (rgb & 0x000000ff);
			    	 		
			    	 		break;
			    	 	}
			    	 
			    	 } 
			    	 
			    	 if(minMax[0] > a_rl[row][col]) { minMax[0] = a_rl[row][col]; }
			    	 
			    	 if(minMax[1] < a_rl[row][col]) { minMax[1] = a_rl[row][col]; }
			    	 
			    	 //centering
			    	if((row+col)%2 != 0) {
			    		
			    		a_rl[row][col] = - a_rl[row][col];
			    	}
			    	
			        col++;
			        
			        if (col == xN) {
			        	
			             col = 0;
			             
			             row++;
			             
			             if((row) >= yN) { break;}
			        }
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			      
		        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
          			          
		        	a_im [row][col] = 0.0;
		        	
					switch(color) {
					
			    	 	case 0: {
			    	 		
			    	 		a_rl[row][col] = ((data[pixel+2] & 0x00ff));
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		a_rl[row][col] = ((data[pixel + 1]& 0x00ff));
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 		a_rl[row][col] = ((data[pixel]& 0x00ff));
			    	 		
			    	 		break;
			    	 	}
					}
					
			    	 if(minMax[0] > a_rl[row][col]) { minMax[0] = a_rl[row][col]; }
			    	 
			    	 if(minMax[1] < a_rl[row][col]) { minMax[1] = a_rl[row][col]; }
					
					 //centering
			    	if((row+col)%2 != 0) {
			    		
			    		a_rl[row][col] = - a_rl[row][col];
			    	}
							                  
		            col++;
		            
		            if (col == xN) {
		            	
		              col = 0;
		              
		              row++;
		              
		              if((row) >= yN) { break;}
		            }
		       }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
			}
	}
		
		public  void writeData( BufferedImage im,double[][] data_rl, double[][] alpha,int color) {
			
		
			int xN = im.getWidth();  
			
			int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			   
		       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
			          	   
		    	    int c = (int)(data_rl[row][col]);
		
					c  = c  < 0 ? 0 : (c  > 255 ? 255 : c );
					
					data[pixel] |= ((int)(alpha[row][col])) << 24;
					
					switch(color) {
			    	 	case 0: {
			    	 		
			    	 	
			    	 		data[pixel] |= (c << 16) ;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 	
			    	 		data[pixel] |= c << 8;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 	
			    	 		
			    	 		data[pixel] |= c ;
			    	 		break;
			    	 	}
					}
					
			        col++;
			        
			        if (col == xN) {
			        	
			             col = 0;
			             
			             row++;
			             
			             if((row) >= yN) { break;}
			        }
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			    
		        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
        
		    	    int c = (int)(data_rl[row][col]);
		    		
					c  = c  < 0 ? 0 : (c  > 255 ? 255 : c );
					
					if (hasAlphaChannel) { data[pixel+3] = ((byte)alpha[row][col]); }
		    					
					switch(color) {
					
			    	 	case 0: {
			    	 		
			    	 		data[pixel+2] = (byte)c;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		data[pixel + 1] = (byte)c;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 		data[pixel] = (byte)c ;
			    	 		
			    	 		break;
			    	 	}
					}
			          		                  
		            col++;
		            
		            if (col == xN) {
		            	
		              col = 0;
		              
		              row++;
		              
		              if((row) >= yN) { break;}
		            }
		       }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
				return;
			}
			
		return;
	}
		
		/**
		 * Doesn't care about alpha channel!!
		 * @param im
		 * @param data_rl
		 * @param color
		 */
		public  void writeCenteredData( BufferedImage im,double[][] data_rl, int color) {
			
			
			int xN = im.getWidth();  
			
			int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			   
		       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
			          	   
		    	    int c = (int)(data_rl[row][col]);
		    	    
		    		if((row+col)%2 != 0) {
		    			
		    			c = -c;
		    		}
		
					c  = c  < 0 ? 0 : (c  > 255 ? 255 : c );
					
					switch(color) {
			    	 	case 0: {
			    	 
			    	 		data[pixel] |= (c << 16) ;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		data[pixel] |= c << 8;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    
			    	 		data[pixel] |= c ;
			    	 		
			    	 		break;
			    	 	}
					}
					
			        col++;
			        
			        if (col == xN) {
			        	
			             col = 0;
			             
			             row++;
			             
			             if((row) >= yN) { break;}
			        }
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			    
		        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
        
		    	    int c = (int)(data_rl[row][col]);
		    	    

		    		if((row+col)%2 != 0) {
		    			c = -c;
		    		}
		    		
					c  = c  < 0 ? 0 : (c  > 255 ? 255 : c );
				
					switch(color) {
					
			    	 	case 0: {
			    	 		
			    	 		data[pixel+2] = (byte)c;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		data[pixel + 1] = (byte)c;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 		data[pixel] = (byte)c ;
			    	 		
			    	 		break;
			    	 	}
					}
			          		                  
		            col++;
		            
		            if (col == xN) {
		            	
		              col = 0;
		              
		              row++;
		              
		              if((row) >= yN) { break;}
		            }
		       }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
				return;
			}
			
		return;
	}
		
		
		public  void writeNormalizedData( BufferedImage im,double[][] data_rl, double[][] alpha,int color, double a, double b) {
			
			
			int xN = im.getWidth();  
			
			int yN =  im.getHeight();
						
			DataBuffer buf = im.getRaster().getDataBuffer();
			
			final boolean hasAlphaChannel = im.getAlphaRaster() != null;
			
			if(buf.getDataType() == DataBuffer.TYPE_INT) {
				
			   int[] data = ((DataBufferInt) buf).getData();
			   
		       for (int pixel = 0, row = 0, col = 0; pixel < data.length; ++pixel) {
			          	   
	    		
		    		int c = (int)(a*data_rl[row][col]+b); //normalization
		
					c  = c  < 0 ? 0 : (c  > 255 ? 255 : c );
					
					data[pixel] |= ((int)(alpha[row][col])) << 24;
					
					switch(color) {
					
			    	 	case 0: {
			    	 		
			    	 		data[pixel] |= (c << 16) ;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			  
			    	 		data[pixel] |= c << 8;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 	
			    	 		data[pixel] |= c ;
			    	 		
			    	 		break;
			    	 	}
					}
					
			        col++;
			        
			        if (col == xN) {
			        	
			             col = 0;
			             
			             row++;
			             
			             if((row) >= yN) { break;}
			        }
		       }
			}
			else if(buf.getDataType() == DataBuffer.TYPE_BYTE) {
				
				byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
				
				int pixelLength = 3;
				
			    if (hasAlphaChannel) {
			    	
			       pixelLength = 4;
			    } 
			    
		        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
        
		    		int c = (int)(a*data_rl[row][col]+b); //normalization
		    		
					c  = c  < 0 ? 0 : (c  > 255 ? 255 : c );
					
					if (hasAlphaChannel) { data[pixel+3] = ((byte)alpha[row][col]); }
		    					
					switch(color) {
					
			    	 	case 0: {
			    	 		
			    	 		data[pixel+2] = (byte)c;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 1: {
			    	 		
			    	 		data[pixel + 1] = (byte)c;
			    	 		
			    	 		break;
			    	 	}
			    	 	case 2: {
			    	 		
			    	 		data[pixel] = (byte)c ;
			    	 		
			    	 		break;
			    	 	}
					}
			          		                  
		            col++;
		            
		            if (col == xN) {
		            	
		              col = 0;
		              
		              row++;
		              
		              if((row) >= yN) { break;}
		            }
		       }
			}
			else {
				
				writeLog("Unknow  format of raster data buffer: " + buf.getDataType()+ NEXT_ROW 
						, null, true, true, true );
				return;
			}
			
		return;
	}
		
	
		private void fft(double[] l_Rl, double[] l_Im, boolean direction, int nT2, int n2) {
		
			double pi2 = 2 * 3.1415926535897932384626433832795;
			
	
			int m, m_2, j, mpNd2, k;
				 
 			double v0R ;
 			
			double v0I ;
			
			double vR = 1;
			
			double vI = 0;
			
 			double[] t = new double[2];
 			
 			//rearrange along x
			dataReverse1(l_Rl, l_Im, nT2, n2);
			
			for(m = 2, m_2 = 1; m <= nT2; m_2 = m, m += m)
			{				
				    double an = pi2/m;
				    
				    if(direction) {
				    	
				    	v0R = Math.cos(an);
				    	
				    	v0I = -Math.sin(an);
				    	
				    } else {
				    	
				     	v0R = Math.cos(an);
				     	
					    v0I = Math.sin(an);
				    }
				
					vR = 1;
					
					vI = 0;
					
				    for(j = 0; j <= m_2-1; j++)
				    {
				         for(k = j; k < nT2; k += m)
				         {
				        	 
				             mpNd2 = k + m_2;
			
				 			 t = PaComplexValue.mul(vR,vI,l_Rl[mpNd2],l_Im[mpNd2]);
				 	
				             l_Rl[mpNd2] =  l_Rl[k] - t[0];
				             
				             l_Im[mpNd2] =  l_Im[k] - t[1];
				             
				             l_Rl[k] =  l_Rl[k] + t[0];
				             
				             l_Im[k] =  l_Im[k] + t[1]; 
	
				         }
				         t = PaComplexValue.mul(vR,vI,v0R,v0I);
				         
			             vR = t[0]; vI = t[1];
				         
				   }
			}
		}
		
		private void dataReverse1(double[] l_Rl, double[] l_Im, int nT2, int n2) {
		
			int  J;
			
			int shift = 32 - n2;
			
		   	double dataM_Rl0; 
		   	
 			double dataM_Im0; 
 			
 	       	double dataM_Rl1; 
 	       	
 			double dataM_Im1; 
			
    
					
			for(int x = 1; x < nT2 - 1; x++)
			{
				   //4 bytes of x value are reflected to the reverse array mapBits 
				J = (mapBits[(int)(x & 0xff000000) >>> 24]) | (mapBits[(int)(x & 0x00ff0000) >> 16] << 8) | 
						(mapBits[(int)(x & 0x0000ff00)>>8] << 16) | (mapBits[(int)x & 0x000000ff] << 24);

				J = J >>> shift; //important to perform unsigned shift
						
			    if (x < J)
			    {	 
	              	 dataM_Rl0 = l_Rl[J]; 
	              	 
		 			 dataM_Im0 = l_Im[J]; 
		 			 
		 	       	 dataM_Rl1 = l_Rl[x]; 
		 	       	 
		 			 dataM_Im1 = l_Im[x]; 
		 			 
		 	    	 l_Rl[x] = dataM_Rl0; 
		 	    	 
		 			 l_Im[x] = dataM_Im0; 
		 			 
		 			 l_Rl[J] = dataM_Rl1; 
		 			 
		 			 l_Im[J] = dataM_Im1; 
			    }
			}
		}
			
		
		
	public void initData(double[][] d_rl, double[][] d_im, int xN, int yN, int N2) {
		
		for(int i= 0; i < N2; ++i) {
			
			for(int j = 0; j < N2; ++j) {
				
				d_im[j][i] = 0.0;
				
				if(i >= xN || j >= yN) {
					
					d_rl[j][i] = 0.0;
				}
				
			}
		}
		
	}
	
	public void subData(double[][] d_rl, double[][] d_im, int n2, int sN) {
		
		for(int i= 0; i < n2; ++i) {
			
			for(int j = 0; j < n2; ++j) {
				
				d_im[j][i] /= sN;
				
				d_rl[j][i] /= sN;
			}
		}
		
	}
	
	public void subFilterData(double[][] d_rl, double[][] d_im, int n2, int sN) {
		
		Point c = new Point(n2/2, n2/2);
		
		for(int i= 0; i < n2; ++i) {
			
			for(int j = 0; j < n2; ++j) {
				
				double d = c.distance(j, i);
				
				double h = 1- Math.exp(-d*d/(2*60*60));
				
				d_im[j][i] = (d_im[j][i]*h + d_im[j][i])/sN;
				
				d_rl[j][i] =(d_rl[j][i]*h + d_rl[j][i])/sN;
			
			}
		}
	}
		

	public void centerData(double[][] d_rl, double[][] d_im, int xN, int yN) {
		
		for(int i= 0; i < xN; ++i) {
			
			for(int j = 0; j < yN; ++j) {
				
				if((i+j)%2 != 0) {
					
					d_im[j][i] = - d_im[j][i];
					
					d_rl[j][i] = - d_rl[j][i];
				}
			}
		}
		
	}
		
		public BufferedImage test(BufferedImage im) {
			
			int xN = im.getWidth();
			
			int yN = im.getHeight();
			
			int[] d = getPowerOf2_MaxValue(xN, yN);
			
			int p2 = d[0];
			
			int N2 = d[1];
			
			BufferedImage resIm = new BufferedImage(xN, yN, BufferedImage.TYPE_INT_RGB);
			
			double[][] d_rl = new double[N2][N2];
			
			double[][] d_im = new double[N2][N2];

			for(int color = 0; color < 3; ++color) {
			
				double[] minMax = { 0,0};
				
				readCenteredData(im,d_rl,d_im, color, minMax); //red
				
				fft2D(d_rl, d_im, true, N2, p2);
				
				subFilterData(d_rl, d_im, N2, N2*N2);
				
				fft2D(d_rl, d_im, false, N2, p2);
				
				writeCenteredData(resIm,d_rl,color); 
			}
			
			return resIm;
			
		}
		
		
		
		public BufferedImage test12(BufferedImage im) {
			
			int xN = im.getWidth();
			
			int yN = im.getHeight();
			
			int[] d = getPowerOf2_MaxValue(xN, yN);
			
			int p2 = d[0];
			int N2 = d[1];
			
			BufferedImage resIm = new BufferedImage(xN, yN, BufferedImage.TYPE_INT_RGB);
			
			double[][] d_rl = new double[N2][N2];
			
			double[][] d_im = new double[N2][N2];
			
			double[][] alpha = new double[N2][N2];
			
			
			for(int color = 0; color < 3; ++color) {
			
				double[] minMaxData = {0,0};
				
				readCenteredData(im,d_rl,d_im, color, minMaxData); //red
				
				fft2D(d_rl, d_im, true, N2, p2);
				
				subFilterData(d_rl, d_im, N2, N2*N2);
				
				fft2D(d_rl, d_im, false, N2, p2);
				
				double[] minMax = {0,0};
				
				getMaxMinCentered(d_rl, xN, yN, minMax);
				
				double a = (minMaxData[1]-minMaxData[0])/(minMax[1]-minMax[0]);
				
				double b = minMaxData[1] - a*minMax[0];
				
				writeNormalizedData(resIm,d_rl,alpha,color,a,b); 
			}
			
			return resIm;
			
		}
		
		
		private void getMaxMinCentered(double[][] d, int xN, int yN, double[] minMax) {	
			
			for(int x = 0; x < xN; ++x) {
				
				for(int y=0; y < yN; ++y) {
					
					
					if((x+y)%2 != 0) {
						
						d[y][x] = - d[y][x];
		    		}
					
					if(minMax[0] > d[y][x]) { minMax[0] = d[y][x]; }
					
					if(minMax[1] < d[y][x]) { minMax[1] = d[y][x]; }
				}
			}
		}
		
		
	public void test2() {
			
		int N2 =32;
		
		int p2 =5;
		

		double[][] d_im = new double[N2][N2];

				
		fft2D(testAr2, d_im, true, N2, p2);
				
		subData(testAr2, d_im, N2, N2*N2);
				
		fft2D(testAr2, d_im, false, N2, p2);
 	
	}
	
	
	public void test3() {
		
		double[] ar = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
		
		double[] ar_im = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		
		fft(ar,ar_im,true,32,5);
		
		for(int i=0; i < 32; ++i) {
			
			ar[i] /= 32.0;
			
			ar_im[i] /= 32.0;
		}
		
		fft(ar,ar_im,false,32,5);
		
		for(int i=0; i < 32; ++i) {
			
			System.out.print(ar[i]+" ");
			
			if(i%8 == 0) System.out.print("\n");
		}
	}	
}
