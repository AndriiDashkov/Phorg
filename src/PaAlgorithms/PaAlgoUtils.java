
package paalgorithms;

/**
 * @author Andrii Dashkov
 * 
 * Class for auxiliary functions.
 *
 */
public class PaAlgoUtils {

	/**
	 * 
	 */
	public PaAlgoUtils() {
	}

	@SuppressWarnings("unused")
	static public int[] getMaxMin(int[] m) {
		
		int[] minMax = {0,0};
		
		for(int i=0; i < m.length; ++i ) {
			
			if(m[i] > minMax[1] ) minMax[1] = m[i];
			
			if(m[i] < minMax[0]) minMax[0] = m[i];
		}	
		
		return minMax;
	}

	/**
	 * <p>Power function s = a0*(value+a1)**gamma
	 * It is used to create two-part function for two different range -  0 ..127 and
	 * 127 .. 255</p>
	 * @param value - vlaue to calculate
	 * @param a  - coefficients for 'first' part of range - < 127
	 * @param b - coefficients for 'second' part of range - > 127
	 * @param gamma - power coefficient - must be understood as for first part of range
	 * @param maxGamma - the upper limit of gamma; we use it to transform ranges 0...1 <-> 1..maxGamma while
	 * we construct the second function in second part of range 0..255
	 * @return the value after power function
	 */
	static public  double gamma2PartFunction(double value,double[] a, double[] b, double gamma, 
			double maxGamma) {
		
		double gamma_1 =1;
		
		//we create function from two parts - that is why gamma for other part should be opposite
		if (gamma == 1.0 ) { 
			
			gamma_1 = gamma;
		} 
		else if (gamma > 1 ) //{ gamma_1 = 1.0/(maxGamma-1.0)*(gamma-1.0); } 
		{ 
			gamma_1 = 1.0/gamma; 
		} 
		else { 
			gamma_1 = ((maxGamma-1.0))*gamma+1; 
		}
		
		if(value < 128) {
			
			return a[0]*Math.pow((value+a[1]),gamma);
			
		} 
		//return b[0]*Math.pow((value+b[1]),(gamma_1));
		
		return 127.0+Math.pow((value+127.0),(gamma_1));
	
	}

	/**
	 * <p>Powere function s = a0*r**gamma</p>
	 * @param value - source value
	 * @param a0 - coefficient
	 * @param gamma  - power coefficient
	 * @return
	 */
	static public  double gammaFunction(double r,double a0, double gamma) {
		
			return a0*Math.pow(r,gamma);
	}

	/**
	 * Gets the closest number which is a power of 2 and > then max dimension if source image
	 * @param sourceImage
	 * @return power of 2
	 */
	static public  int get2CloseNumber(int xN, int yN){
					
		int max = yN;
		
		if( xN > yN) max =xN;
		
		int nT ;
		
		int t = 4 ;
		
		do {
			
			nT = t;
			
			t = nT*2;
			
		}while(t < max);
		
		return nT;
	}

	/**
	 * Gets the closest number which is a power of 2 and > then max dimension if source image
	 * @param sourceImage
	 * @return power of 2
	 */
	static public  int get2maxNumber(int xN, int yN){
			
		int max = yN;
		
		if( xN > yN) max =xN;
		
		int nT = 4 ;
		
		while(nT < max) {
			
			nT = nT*2;	
			
		}
		
		return nT;
	}

	static public  int getRandomValue(int max) {
		
		double v = Math.random();
		
		return (int)(max*v);
	
	}

	/**
	 * Generates random values in the range with 0 value in the center of range
	 * The function is not very clever, but it is enough for us.
	 * @param min - must be < 0
	 * @param max - must be > 0
	 * @return
	 */
	static public  int getRandomValue(int min, int max) {
		
		double v = Math.random();
		
		if(v < 0.5) {
			
			return (int)((min/0.5)*v);
			
		}
		else {
			
			return (int)((max/0.5) * v);
		}
		
	}

	/**
	 * Gets the closest number which is a power of 2 and > then max dimension xN,yN
	 * @param xN - x dimension
	 * @param yN - y dimension  
	 * @return the array [0] - power of 2; [1] - max value over Xn,yN
	 */
	static public  int[] getPowerOf2_MaxValue(int xN, int yN) {
			
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
	 * Calculates the dispersion
	 * @param av - mean values for 3 channels
	 * @param data - data to calculate dispersion for
	 * @param xN - horizontal dimension
	 * @param yN - vertical dimension
	 * @return the square root of variance, separated for 3 channels
	 */
	static public  double[] getDisper(double av[], HeapArray3D<Double> data, int xN, int yN) {
	
		double[] br = { 0.0,0.0,0.0}; //0 - red, 1- green, 2  blue
		
		double n = xN*yN;
		
		for ( int x = 0; x < xN; ++x) {
			
			for ( int y = 0; y < yN; ++y) {
				
				double d = data.get(0,x, y)-av[0];
				
				br[0] += d*d;//red
				
				d = data.get(1,x, y)-av[1];
				
				br[1] += d*d;//green
				
				d = data.get(2,x, y)-av[2];
				
				br[2] += d*d;//blue
			}
		}
		
		br[0] = Math.sqrt(br[0]/n);//red
		
		br[1] = Math.sqrt(br[1]/n);//green
		
		br[2] = Math.sqrt(br[2]/n);
		
		return br;
		
	}
	
	
	
	

}
