
package PaAlgorithms;

import PaEditor.PaComplexValue;
import PaEditor.PaHeapArray;

/**
 * @author Andrey Dashkov
 *
 */
public class PaAlgoFFT {

	/**
	 * 
	 */
	public PaAlgoFFT() {

	}


	/**
	 * This FFT implementation works with 2D data, that's why the input must be padded with zeros, the padding must align the data
	 * with size equal the size of 2 - for nTx,nTy
	 * @param rl - real part of image data
	 * @param im - imaginary part of image data 
	 * @param direct - true for direct Furie transition and false for backward Furie transition
	 * @param nTx - x direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
	 * @param nTY - y direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
	 * @param FuvRl - resulting real part of Furies transition
	 * @param FuvIm  - resulting imaging part of Furies transition
	 */
	@Deprecated
	static public  void getFFT(boolean direct, double[][] rl,double[][] im,int nTx,int nTy,
			double[][] FuvRl,double[][] FuvIm)
	{
		
		double[] yRl = new double[nTy];
		
		double[] yIm  = new double[nTy];
		
		double[][] FxvRl = new double[nTy][nTx];//real part
		
		double[][] FxvIm = new double[nTy][nTx];//imaginary part 
		
		//Firstly, the FFT for columns
		//temporary results are in  FxvRl
		for (int x = 0; x < nTx; ++x) {
			
			double[] rlY = new double[nTy];
			
			double[] imY = new double[nTy];
			
			for(int i = 0; i < nTy; ++i) {
				
				rlY[i] = rl[i][x];
				
				imY[i] = im[i][x];
			}
			
			//xFFT(direct, rlY,imY,nTy,yRl,yIm) ;
			
			for(int v = 0; v < nTy; ++v)  {
				if(direct) {
					FxvRl[v][x] = yRl[v]/nTy;
					FxvIm[v][x] = yIm[v]/nTy;
				}
				else {
					FxvRl[v][x] = yRl[v];
					FxvIm[v][x] = yIm[v];
				}
			}
		}
	
		//now FFT for rows
		for ( int v = 0; v < nTy; ++v) {
		
			double[] rlX = new double[nTx];
			
			double[] imX = new double[nTx];
			
			for(int l = 0; l < nTx; ++l) {
				
				rlX[l] = FxvRl[v][l];
				
				imX[l] = FxvIm[v][l];
			}
			
			//xFFT(direct,rlX,imX,nTx,yRl, yIm) ;
			
			for(int u = 0; u < nTx; ++u)  {
				
				if(direct) {
					
					FuvRl[v][u] = yRl[u]/(nTx);
					
					FuvIm[v][u] = yIm[u]/(nTx);
				}
				else {
					
					FuvRl[v][u] = yRl[u];
					
					FuvIm[v][u] = yIm[u];
				}
			}			
		}	
	}


	/**
	 * This FFT implementation works with 2D data, that's why the input must be padded with zeros, the padding must align the data
	 * with size equal the size of 2 - for nTx,nTy
	 * @param rl - real part of image data
	 * @param im - imaginary part of image data 
	 * @param direct - true for direct Furie transition and false for backward Furie transition
	 * @param nTx - x direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
	 * @param nTY - y direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
	 * @param n2 - the power of 2 for the input data (auxiliary input not to calculate inside)
	 * @param FuvRl - resulting real part of Furies transition
	 * @param FuvIm  - resulting imaging part of Furies transition
	 */
	static public  void getFFT1(boolean direct, PaHeapArray<Double> rl, PaHeapArray<Double> im,
			int nTx,int nTy,int n2,PaHeapArray<Double> FuvRl,PaHeapArray<Double> FuvIm)
	{
		double[] yRl = new double[nTy];
		
		double[] yIm  = new double[nTy];
		
		PaHeapArray<Double> FxvRl = new PaHeapArray<Double>(nTy,nTx);//real part
		
		PaHeapArray<Double> FxvIm = new PaHeapArray<Double>(nTy,nTx);//imaginary part
		
		//firstly FFT for columns
		//temporary results are in FxvRl
		double[] rlY = new double[nTy];
		
		double[] imY = new double[nTy];
		
		for (int x = 0; x < nTx; ++x) {
			
			for(int i = 0; i < nTy; ++i) {
				
				rlY[i] = rl.get(i,x);
				
				imY[i] = im.get(i,x);
			}
			
			xFFT(direct, rlY,imY,nTy,n2,yRl,yIm) ;
			
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
	
		//now FFT for rows of temporary results
		for ( int v = 0; v < nTy; ++v) {

			double[] rlX = new double[nTx];
			
			double[] imX = new double[nTx];
			
			for(int l = 0; l < nTx; ++l) {
				
				rlX[l] = FxvRl.get(v,l);
				
				imX[l] = FxvIm.get(v,l);
			}
			
			xFFT(direct,rlX,imX,nTx,n2,yRl, yIm) ;
			
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


	/**
	 * This FFT implementation works with 2D data, that's why the input must be padded with zeros, the padding must align the data
	 * with size equal the size of 2 - for nTx,nTy
	 * @param rl - real part of image data
	 * @param im - imaginary part of image data 
	 * @param direct - true for direct Furie transition and false for backward Furie transition
	 * @param nTx - x direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
	 * @param nTY - y direction max points; this parameter should be prepared with get2maxNumber(sourceImage) in order to receive the power of 2 max number
	 * @param FuvRl - resulting real part of Furies transition
	 * @param FuvIm  - resulting imaging part of Furies transition
	 */
	static public  void getFFT3D(boolean direct, HeapArray3D<Double> rl, HeapArray3D<Double> im,
			int nTx,int nTy,HeapArray3D<Double> FuvRl,HeapArray3D<Double> FuvIm)
	{
		
		double[][] yRl = new double[3][nTy];
		
		double[][] yIm  = new double[3][nTy];
		
		HeapArray3D<Double> FxvRl = new HeapArray3D<Double>(3,nTy,nTx);//real part
		
		HeapArray3D<Double> FxvIm = new HeapArray3D<Double>(3,nTy,nTx);//imaginary part
		
		//Firstly, FFT for rows
		//temporary results are â FxvRl
		
		for (int x = 0; x < nTx; ++x) {
			
			double[][] rlY = new double[3][nTy];
			
			double[][] imY = new double[3][nTy];
			
			for(int i = 0; i < nTy; ++i) {
				
				for(int z = 0; z < 3; ++z) {
					
					rlY[z][i] = rl.get(z,i,x);
					
					imY[z][i] = im.get(z,i,x);
				}
			}
			
			xFFT3D(direct,rlY,imY,nTy,yRl,yIm) ;
			
			for(int v = 0; v < nTy; ++v)  {
				
				if(direct) {
					
					for(int z = 0; z < 3; ++z) {
						
						FxvRl.set(z,v,x,yRl[z][v]/nTy);
						
						FxvIm.set(z,v,x,yIm[z][v]/nTy);
					}
				}
				else {
					
					for(int z = 0; z < 3; ++z) {
						
						FxvRl.set(z,v,x,yRl[z][v]);
						
						FxvIm.set(z,v,x,yIm[z][v]);
					}
				}
			}
		
		}
	
		//now FFT for rows
		for ( int v = 0; v < nTy; ++v) {

			double[][] rlX = new double[3][nTx];
			
			double[][] imX = new double[3][nTx];
			
			for(int l = 0; l < nTx; ++l) {
				
				for(int z = 0; z < 3; ++z) {
					
					rlX[z][l] = FxvRl.get(z,v,l);
					
					imX[z][l] = FxvIm.get(z,v,l);
				}
			}
			
			xFFT3D(direct,rlX,imX,nTx,yRl,yIm) ;
			
			for(int u = 0; u < nTx; ++u)  {
				
				if(direct) {
					
					for(int z = 0; z < 3; ++z) {
						
						FuvRl.set(z,v,u,yRl[z][u]/(nTx));
						
						FuvIm.set(z,v,u,yIm[z][u]/(nTx));
					}
				}
				else {
					
					for(int z = 0; z < 3; ++z) {
						
						FuvRl.set(z,v,u,yRl[z][u]);
						
						FuvIm.set(z,v,u,yIm[z][u]);
					}
				}
			}			
		}
		
		FxvRl.free();
		
		FxvIm.free();
	}


	/**
	 * This FFT implementation works with 1D data
	 * @param aRl - real part of the input data
	 * @param aIm - imaginary of the input data
	 * @param n - number of points for FFT, the power of 2 max number
	 * @param n2 - the power of 2 for n
	 * @param FxvRl - resulting real part of Furies transition
	 * @param FxvIm  - resulting imaging part of Furies transition
	 */
	static public  void xFFT(boolean direct,double[] aRl,double[] aIm, 
			int n,int n2, double[] FxvRl, double[] FxvIm) {
		
		if (aRl.length == 1) { 
			
			FxvRl[0] = aRl[0];//red
			
			FxvIm[0] = aIm[0];
			
			
			return;
		}

		double v0I;
		
		if(direct) { 
			
			v0I =  -PaAlgoConsts.sinAr[n2]; 
			
			}//-exp_im[n];
		else { 
			
			v0I = PaAlgoConsts.sinAr[n2];
		}
		
		double v0R = PaAlgoConsts.cosAr[n2];
		
		
		double vR = 1;
		
		double vI = 0.0;
		
		int nNext = n/2;
		
		int nNext2 = n2 - 1;
		
		double[] y0R = new double[nNext]; double[] y0I = new double[nNext];
		
		double[] y1R = new double[nNext]; double[] y1I = new double[nNext];
		
		double[] a0R = new double[nNext]; double[] a0I = new double[nNext];
		
		double[] a1R = new double[nNext]; double[] a1I = new double[nNext];
		
		for(int i = 0; i < nNext; ++i) {
			
			a1R[i] = aRl[2*i+1];
			
			a0R[i] = aRl[2*i];
			
			a1I[i] = aIm[2*i+1];
			
			a0I[i] = aIm[2*i];
		}
		
		xFFT(direct,a0R,a0I,nNext,nNext2,y0R,y0I);
	
		xFFT(direct,a1R,a1I,nNext,nNext2,y1R,y1I);
		
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
	 * This FFT implementation works with 2D data
	 * @param aRl - real part of the input data
	 * @param aIm - imaginary of the input data
	 * @param n - number of points for FFT, the power of 2 max number
	 * @param FxvRl - resulting real part of Furies transition
	 * @param FxvIm  - resulting imaging part of Furies transition
	 */
	static public  void xFFT3D(boolean direct,double[][] aRl,double[][] aIm, 
			int n, double[][] FxvRl, double[][] FxvIm) {
		
		if (aRl[0].length == 1) { 
			
			for(int z =0; z < 3; ++z) {
				
				FxvRl[z][0] = aRl[z][0];//red
				
				FxvIm[z][0] = aIm[z][0];
			}
			
			return;
		}
		
		double cF = 1.0;
		
		if(direct) cF = -1.0;
		
		double an =	2.0*Math.PI/n;
	
		double v0R = Math.cos(an);
		
		double v0I = cF*Math.sin(an);
		
		double vR = 1;
		
		double vI = 0.0;
		
		int nNext = n/2;
		
		double[][] y0R = new double[3][nNext]; double[][] y0I = new double[3][nNext];
		
		double[][] y1R = new double[3][nNext]; double[][] y1I = new double[3][nNext];
		
		double[][] a0R = new double[3][nNext]; double[][] a0I = new double[3][nNext];
		
		double[][] a1R = new double[3][nNext]; double[][] a1I = new double[3][nNext];
		
		for(int i = 0; i < nNext; ++i) {
			
			for(int z =0; z < 3; ++z) {
				
				a1R[z][i] = aRl[z][2*i+1];
				
				a0R[z][i] = aRl[z][2*i];
			
				a1I[z][i] = aIm[z][2*i+1];
				
				a0I[z][i] = aIm[z][2*i];
			}
		}
		
		xFFT3D(direct,a0R,a0I,nNext,y0R,y0I);
	
		xFFT3D(direct,a1R,a1I,nNext,y1R,y1I);
		
		for(int k = 0 ; k < nNext; ++k ) {
			
			for(int z =0; z < 3; ++z) {
				
				double[] t = PaComplexValue.mul(vR,vI,y1R[z][k],y1I[z][k]);
				
				FxvRl[z][k] = y0R[z][k] + t[0]; 
				
				FxvIm[z][k] = y0I[z][k] + t[1];
		
				FxvRl[z][k+n/2] = y0R[z][k] - t[0]; 
				
				FxvIm[z][k+n/2] = y0I[z][k] - t[1];	
				
				double[] t1 = PaComplexValue.mul(vR,vI,v0R,v0I);
				
				vR = t1[0];
				
				vI = t1[1];
			}
		}	
	}

}
