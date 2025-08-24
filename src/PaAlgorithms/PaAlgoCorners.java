/**
 * 
 */
package paalgorithms;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import paenums.PaTriple;

/**
 * The class for implementation of the Harris/Stephens filter
 * @author Andrii Dashkov
 *
 */
public class PaAlgoCorners {

	/**
	 * 
	 */
	public PaAlgoCorners() {

	}
	
	
	/**
	 * Gets the value of gaussian squared mask, the gaussian is centered to center of the square, the value of mask is equal to 1.0 in the center.
	 * @param w mask width
	 * @param h mask height
	 * @param sigma - the variance
	 */
	static private double[][] gaussMask(double sigma, int w, int h) {
		
		double[][] mask = new double[h][w];
		
		int x_center = w/2;
		
		int y_center = h/2;
		
		for ( int y = 0; y < h; y++) {
			
			for ( int x = 0; x < w; x++) {
		
				int y_relative = y - y_center;
				
				int x_relative = x - x_center;
				
				 mask[y][x] =  Math.exp(-(x_relative*x_relative + y_relative*y_relative)/(2*sigma));
		
			}
		}
		
		return mask;
	}
	
	/**
	 * Shapes the squared mask 'mask' to have only elements, which are inside the circle with radius min_dimension/2
	 * The mask is assumed to be squared , w_mask == h_mask, but rectangular mask also can be processed; in this case the circle
	 * is shaped into minimum dimension.
	 * @param mask - mask itself
	 * @param w_mask - mask width
	 * @param h_mask - mask height
	 */
	static private ArrayList<PaTriple<Integer,Integer, Double>> shapeMaskToCircle(double[][] mask, int w_mask, int h_mask) {
		
		ArrayList<PaTriple<Integer,Integer, Double>> entries = new ArrayList<>();
		
		int  r_limit = w_mask <= h_mask ? w_mask/2 : h_mask/2;
		
		
		for ( int y = 0; y < h_mask; y++) {
			
			for ( int x = 0; x < w_mask; x++) {
				
				int r = (int) Math.sqrt(x*x + y*y);
				
				if (r <= r_limit) {
					
					entries.add(new  PaTriple<Integer, Integer, Double>(x, y, mask[y][x]));
				}
			}
			
		}
		
		
		return entries;
		
	}
	
	/**
	 * Find corners and edges according to Harris/Stephens filter, based on Hessian matrix
	 * @param im - input image, it will be modified by this function
	 * @param mask_sigma - variance for the gaussian mask
	 * @param w_mask - the width of the mask, must be even, usually the squared mask is used
	 * @param h_mask - the height of the mask, must be even, usually the squared mask is used
	 * @param sensitivity - the coefficient in the Harris/Stephens formula : R = Det - (sensitivity)*Tr*Tr 
	 * @param threshold - the threshold of detection 'th' is calculated as th = threshold*max(R)
	 * @param showCorners - true to find corners
	 * @param showEdges - true to find edges
	 * @return the image where corners are marked by red color, and edges by green color
	 */
	static public BufferedImage findCornersEdges(BufferedImage im, double mask_sigma, int w_mask, int h_mask, double sensitivity, 
			double threshold,  boolean showCorners, boolean showEdges) {
		
		//convert to grayscale
		BufferedImage gr_image =  (BufferedImage) PaAlgoConvert.toGrayscale(im);
		
		double[][] sq_mask = gaussMask(mask_sigma, w_mask, h_mask);
		
		
		ArrayList<PaTriple<Integer,Integer, Double>> cl_mask_list = shapeMaskToCircle(sq_mask, w_mask, h_mask);
		
		int w = gr_image.getWidth();  
		
		int h =  gr_image.getHeight();
		
		int grad_length = 3;
		
		int[] I_mask_x = new int[grad_length ];
		
		int[] I_mask_y = new int[grad_length ];
		
		int w_mask_2 =  w_mask/2;
		 
		int h_mask_2 =  h_mask/2;
		 
		int[][] Ix = new int[h][w];
		
		int[][] Iy = new int[h][w];
		
		//find derivatives Ix, Iy for every pixel
		 
		for ( int y = 0; y < h; y++) {
			
			for ( int x = 0; x < w; x++) {
				
				int x_ = x;
				
				int x__ = x;
				
				int y_ = y;
				
				int y__ = y;

				int I_sum_x = 0;
				
				int I_sum_y = 0;
				
				for(int i = grad_length/2 - 1, j = grad_length/2 + 1; i >= 0; --i, ++j) {
					
					x_ =  x_ - 1;
					
					if(x_ < 0 ) {//boundary control
						x_ += 1;
					}
					
					I_mask_x[i] = (int) ((gr_image.getRGB(x_, y)  & 0xff)*(-1.0));//mult_;
					
					I_sum_x += I_mask_x[i];
					
					x__ =  x__ + 1;
					
					if(x__ >= w ) {//boundary control
						x__ -= 1;
					}
					
					I_mask_x[j] = (int) ((gr_image.getRGB(x__, y)  & 0xff));//mult__;
					
					I_sum_x += I_mask_x[j];
					
					y_ =  y_ - 1;
					
					if(y_ < 0 ) {//boundary control
						y_ += 1;
					}
					
					I_mask_y[i] = (int) ((gr_image.getRGB(x, y_)  & 0xff)*(-1.0));//mult_;
					
					I_sum_y += I_mask_y[i];
					
					y__ =  y__ + 1;
					
					if(y__ >= h ) {//boundary control
						y__ -= 1;
					}
					
					I_mask_y[j] = (int) ((gr_image.getRGB(x, y__)  & 0xff));//mult__;
					
					I_sum_y += I_mask_y[j];
					
					// mult_ -= -1;
					 
					// mult__ += 1;
					
				}
				
				Ix[y][x] = I_sum_x;
				
				Iy[y][x] = I_sum_y;
				
				
			}
		}
		
		//using the mask
		
		double[][] res = new double[h][w];
		
		ArrayList<PaTriple<Integer, Integer, Double>> corners_raw_list = new ArrayList<PaTriple<Integer, Integer, Double>>();
		
		ArrayList<PaTriple<Integer, Integer, Double>> edges_raw_list = new ArrayList<PaTriple<Integer, Integer, Double>>();
		
		double maxR = -1.0;
		
		for ( int y = 0; y < h; y++) {
			
			for ( int x = 0; x < w; x++) {
				
				double Ix2 = 0;
				
				double Iy2 = 0;
				
				double Ixy = 0;
				
				for(PaTriple<Integer,Integer, Double> p : cl_mask_list) {
					
					int x_ = x + p.first() - w_mask_2 ;
					
					int y_ = y + p.second() - h_mask_2 ;
					
					if(x_ < 0 || y_ < 0 ||  x_ >= w || y_ >= h) {//image boundaries control
						continue;
					}
					
					double Ix_mult = Ix[y_][x_]*p.third();
					
					Ix2 +=  Ix[y_][x_] * Ix_mult;
				
					Iy2 +=  Iy[y_][x_] * Iy[y_][x_]*p.third();
				
					Ixy +=  Ix_mult * Iy[y_][x_];
					
				}
				
				double R = (Ix2*Iy2 - Ixy*Ixy - sensitivity*(Ix2 + Iy2)*(Ix2 + Iy2))/( cl_mask_list.size()*cl_mask_list.size());
				
				if(Math.abs(R) > maxR) {
					
					maxR = Math.abs(R);
				}
				
				res[y][x] = R;
			
			}
		}
		
		double th = threshold * maxR;
		
		
		for ( int y = 0; y < h; y++) {
			
			for ( int x = 0; x < w; x++) {
				
				
				if(th < res[y][x]) {
					
					corners_raw_list.add(new PaTriple<Integer, Integer, Double>(x,y, res[y][x]));
				}
				else if(-th > res[y][x]){
					
					edges_raw_list.add(new PaTriple<Integer, Integer, Double>(x,y, res[y][x]));
				}
				
			}
		}

		if (showEdges) {
			
			ArrayList<PaTriple<Integer,Integer, Double>> edges_list = noneMaxSuppresion(edges_raw_list, w_mask);
			
			for(PaTriple<Integer, Integer, Double> p: edges_list) {
				
				drawEdge(im,  p);

			}
		}
		
		if (showCorners) {
			
			ArrayList<PaTriple<Integer,Integer, Double>> corners_list = noneMaxSuppresion(corners_raw_list, w_mask);
		
			for(PaTriple<Integer, Integer, Double> p: corners_list) {
				
				drawCorner(im,  p);
			
			}
		}
		
		return im;
		
	}
	
	static public ArrayList<PaTriple<Integer,Integer, Double>> noneMaxSuppresion( ArrayList<PaTriple<Integer,Integer, Double>> raw_list, int maskSize) {
		
		ArrayList<PaTriple<Integer,Integer, Double>> res_list = new ArrayList<PaTriple<Integer,Integer, Double>>();
		
		ArrayList<PaTriple<Integer,Integer, Double>> tmp_list = new ArrayList<PaTriple<Integer,Integer, Double>>();
		
		int maskSize_2 = maskSize/2;
		
		for(int i = 0; i < raw_list.size(); ++i) {
			
			
			PaTriple<Integer,Integer, Double> p =  raw_list.get(i);
			
			tmp_list.add(p);
			
			int x = p.first();
			int y = p.second();

			for(int j =  i + 1; j < raw_list.size(); ++j) {
				
				PaTriple<Integer,Integer, Double> p1 =  raw_list.get(j);
				
				int x1 = p1.first();
				
				int y1 = p1.second();
				
				if( (Math.abs(y - y1) <= maskSize_2) && (Math.abs(x - x1) <=  maskSize_2)) {
					
					tmp_list.add(p1);
				}
					
			}
			
			//find maximum
			PaTriple<Integer,Integer, Double> p_max = tmp_list.get(0);
			
			double double_max = p_max.third();
			
			for(int j =  0; j < tmp_list.size(); ++j) {
				
				double v = tmp_list.get(j).third();
				
				if (v > double_max) {
					
					 p_max = tmp_list.get(j);
					 
					 double_max = v;
				}
			
			}
			//add maximum
			res_list.add(p_max);
			 //delete from raw_list
			 
			raw_list.removeAll(tmp_list);

			tmp_list.clear();
		}
		
		
		return res_list;
	
	}
	
	
	public static void drawCorner(BufferedImage im,  PaTriple<Integer, Integer, Double> p) {
		
		
		int w = im.getWidth();
		
		//int h = im.getHeight();
		
		im.setRGB(p.first(), p.second(), Color.red.getRGB());
		
		if( (p.first()-1) >= 0 ) {
			
			if( (p.second() -1) >= 0 ) {
				
				im.setRGB(p.first()-1, p.second() -1, Color.red.getRGB());
			}
			
			if( (p.second() -2) >= 0 ) {
				
				im.setRGB(p.first()- 1, p.second() - 2, Color.red.getRGB());
			}
			
			if( (p.second() -3) >= 0 ) {
				
				im.setRGB(p.first()- 1, p.second() - 3, Color.red.getRGB());
			}
			
		}
		
		if( (p.second() -1) >= 0 ) {
		
			im.setRGB(p.first(), p.second() -1, Color.red.getRGB());
			
			if( (p.first()+1) < w ) {
			
				im.setRGB(p.first() + 1, p.second() -1, Color.red.getRGB());
			}
		
		}
		
		if( (p.second() -2) >= 0) {
			
			if ( (p.first()-2) >= 0) {
		
				im.setRGB(p.first()-2, p.second() -2, Color.red.getRGB());
			}
			
			if ( (p.first()+2) < w) {
				
				im.setRGB(p.first()+ 2, p.second() - 2, Color.red.getRGB());
			}
			
			if ( (p.first()+1) < w) {
				
				im.setRGB(p.first()+ 1, p.second() - 2, Color.red.getRGB());
			}
			
			im.setRGB(p.first(), p.second() - 2, Color.red.getRGB());
		
		}
		
		if( (p.second() -3) >= 0) {
			
			if ( (p.first()-2) >= 0) {
				
				im.setRGB(p.first()-2, p.second() -3, Color.red.getRGB());
			}
			
			if ( (p.first()+2) < w) {
			
				im.setRGB(p.first()+ 2, p.second() - 3, Color.red.getRGB());
			}
			
			if ( (p.first()+1) < w) {
			
				im.setRGB(p.first()+ 1, p.second() - 3, Color.red.getRGB());
			}
			
			im.setRGB(p.first(), p.second() - 3, Color.red.getRGB());
		}
		
	}
		
	public static void drawEdge(BufferedImage im,  PaTriple<Integer, Integer, Double> p) {
		
		int w = im.getWidth();
		
		int h = im.getHeight();
		
		im.setRGB(p.first(), p.second(), Color.green.getRGB());
		
		if((p.first()-1) >= 0) {
		
			im.setRGB(p.first() - 1, p.second(), Color.green.getRGB());
		}
		
		if( (p.first()+1) < w) {
			
			im.setRGB(p.first() + 1, p.second(), Color.green.getRGB());
		}
		
		if( (p.second()-1) >= 0) {
			
			im.setRGB(p.first(), p.second()-1, Color.green.getRGB());
			
		}
		if( (p.second() + 1) < h) {
			
			im.setRGB(p.first(), p.second()+1, Color.green.getRGB());
		}
	
	}

	
	
	//debug function
	/*static private void getStatisitic(double[][] res, int w, int h ) {
		
		double av_plus = 0.0;
		
		int av_plus_counter =0;
		
		double av_minus = 0.0;
		
		int av_minus_counter =0;
		
		int av_zero_counter = 0;
		
		double d_min = 99999999999999.0;
		
		double d_max = - 9999999999999.0;
		
		for ( int y = 0; y < h; y++) {
			
			for ( int x = 0; x < w; x++) {
				
				if(res[y][x] < 0.0) { av_minus += res[y][x]; av_minus_counter++; }
				
				if(res[y][x] > 0.0) { av_plus += res[y][x]; av_plus_counter++; }
				
				if(res[y][x] == 0.0) { av_zero_counter++; }
				
				
				if(res[y][x] > d_max) { d_max = res[y][x]; }
				
				if(res[y][x] < d_min) { d_min = res[y][x]; }
				
	
				
			}
		}
		
		av_plus /= av_plus_counter;
		
		av_minus /= av_minus_counter;
		
		System.out.println("Mean poaitive = " + av_plus);
		
		System.out.println("Mean negative = " +av_minus);
		
		System.out.println("Zero counter = " + av_zero_counter);
		
		System.out.println("Min = " + d_min);
		System.out.println("Max = " + d_max);
		
		int num_intervals = 20;
		
		double step = (d_max - d_min)/num_intervals;
		
		System.out.println("Step = " + step);
		
		int[] intervals_data = new int[num_intervals];
		
		
		for ( int y = 0; y < h; y++) {
			
			for ( int x = 0; x < w; x++) {
				
				for(int i = 0; i < num_intervals; ++i) {
				
					
					double bt = d_min + i* step;
					
					double up = bt +  step;
					
					if(res[y][x] < up && res[y][x] >= bt ) { intervals_data[i] += 1; break; }
					
				}
					
			}
		}
		
		for(int i = 0; i < num_intervals; ++i) {
			
			System.out.print(intervals_data[i]);
			System.out.print(" bottom : " + (i*step + d_min ));
			System.out.println(" top : " + ((i + 1)*step + d_min ));
			
		}
		
	
	}*/
	
}




