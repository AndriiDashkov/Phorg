
package paeditor;

/**
 * @author Andrii Dashkov
 * <p>Class for manipulation with complex values</p>
 */
public class PaComplexValue {

	
	private double m_rl = 0;
	
	private double m_im = 0;
	/**
	 * 
	 */
	public PaComplexValue(double rl, double im) {
		m_rl = rl;
		m_im = im;
	}
	
	public static PaComplexValue add(PaComplexValue f, PaComplexValue s) {
		
		return new PaComplexValue(f.m_rl + s.m_rl, f.m_im - s.m_im);
	}
	
	public static PaComplexValue sub(PaComplexValue f, PaComplexValue s) {
		
		return new PaComplexValue(f.m_rl - s.m_rl, f.m_im - s.m_im);
	} 
	
	public static PaComplexValue mul(PaComplexValue f, PaComplexValue s) {
		
		return new PaComplexValue(f.m_rl*s.m_rl -f.m_im*s.m_im, f.m_im*s.m_rl + f.m_rl*s.m_im);
	}
	
	public static double[]  mul(double v1r, double v1i, double v2r, double v2i) {
		
		double[] i =new double[2];
		
		i[0] = v1r*v2r -v1i*v2i;
		
		i[1] = v1i*v2r + v1r*v2i;
		
		return i;
	} 
	
	
}
