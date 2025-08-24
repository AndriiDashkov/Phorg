
package paalgorithms;

/**
 * Filters, which are based on gradient operations
 * @author Andrii Dashkov
 *
 */
public class PaAlgoGradient {

	/**
	 * 
	 */
	public PaAlgoGradient() {
	}

	static public int[] sobelFilter(int[][][] colors, int level){
		
		int[] cl = new int[3];
		
		for(int i = 0; i < 3; ++i ) {
			
			int s1 = colors[i][2][0]+2*colors[i][2][1] +colors[i][2][2] - colors[i][0][0]
					-2*colors[i][0][1]-colors[i][0][2];
			
			if(s1 < 0) s1 = -s1;
			
			int s2 = colors[i][0][2]+2*colors[i][1][2] +colors[i][2][2] - colors[i][0][0]
					-2*colors[i][1][0]-colors[i][2][0];
			
			if(s2 < 0) s2 = -s2;
			
			cl[i] = s1 + s2;
			
			//the control of sharpness threshold
			//for some pixels the Laplas operator can be very big: for example the black pixel inside white neighbors
			//it's impossible to see visually. This is workaround for such a case.
			// for bigger values of level the sharpness is more intensive

			if(cl[i] < -level ) { cl[i] = -level; }
			
			if(cl[i] > level ) { cl[i] = level; }
			
		}
		
		return cl;
	}

	/**
	 * <p>Returns the Laplas operator value. Second index in colors is a row, or 'y' according to image; third index is a column, or 'x'
	 * according to image; first index is a color component - red, green or blue; laplasian performes a separate calculation for all
	 * color components</p>
	 * @param colors - three planes - red,green, blue every plane - 3x3 pixels; colors is a matrix with info about image
	 * laplas operator will be calculated for this value using mask - lapMask
	 * @param lapMask - mask 3 x 3 which determines the form of laplasian
	 * @param - level - the threshold level of sharpness
	 * @return three value of red,green, blue for pixel in 1,1 of colors
	 */
	static public int[] laplasFunction(int[][][] colors, int lapMask[][], int level ){
		
		int[] cl = new int[3];
		
		for(int i = 0; i < 3; ++i ) {
	
			cl[i] = colors[i][0][0]*lapMask[0][0] +
					colors[i][0][1]*lapMask[0][1]+
					colors[i][0][2]*lapMask[0][2]+
					colors[i][1][0]*lapMask[1][0]+
					colors[i][1][1]*lapMask[1][1]+
					colors[i][1][2]*lapMask[1][2]+
					colors[i][2][0]*lapMask[2][0]+
					colors[i][2][1]*lapMask[2][1]+
					colors[i][2][2]*lapMask[2][2];
			
			//the control of sharpness threshold
			//the case when the laplasian value spikes, because for example black pixel surronded white pixels
			//this case is almost invisible for the eye, but can lead to bad values of laplasian
	
			if(cl[i] < -level ) { cl[i] = -level; }
			
			if(cl[i] > level ) { cl[i] = level; }
	
		}
		return cl;
	}

}
