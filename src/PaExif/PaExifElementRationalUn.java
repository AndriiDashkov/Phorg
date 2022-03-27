package PaExif;

import static PaGlobal.PaUtils.getGuiStrs;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Vector;
import PaEnums.PaPair;
import PaGlobal.PaLog;

public class PaExifElementRationalUn extends PaExifElement 
{

	private Vector<PaPair<Long, Long>> m_pair_vector;

	@SuppressWarnings("unchecked")
	public PaExifElementRationalUn(Vector<PaPair<Long, Long>> v) 
	{
		super(PaElementType.SignedLong, v.size());
		
		m_pair_vector = (Vector<PaPair<Long, Long>>) v.clone();
	}

	
	public String toString() 
	{
		if(m_pair_vector.isEmpty()) return getGuiStrs("noDataString");
		
		return toDouble().toString();		
	}
	
	public Long toLong() 
	{
		if(m_pair_vector.isEmpty()) return null;
		
		PaPair<Long, Long> firstPair = m_pair_vector.get(0);
		
		return firstPair.first();		
	}

	
	public BigDecimal toDouble() 
	{
		if(m_pair_vector.isEmpty()) return null;
		
		PaPair<Long, Long> firstPair = m_pair_vector.get(0);
	
		if(firstPair.second().doubleValue() != 0) {
			
			BigDecimal bd =	new  BigDecimal(firstPair.first().doubleValue()/firstPair.second().doubleValue());
			
			return bd.round(new MathContext(5));
		}
		else {
			
			return null;
		}
	 	
	}
	
	public Vector<Long> getLongValues(int numberOfPairs) 
	{
		if(m_pair_vector.isEmpty() || numberOfPairs > m_pair_vector.size()) {
			
			PaLog.writeLogOnly("Desirable number of elements is too big for PaExifElementRationalUn", null );
			
			return null;
		}
		
		Vector<Long> v = new Vector<Long>();
		
		PaPair<Long, Long> p = null;
		
		for(int i=0; i <  numberOfPairs; ++i) {
			
			p = m_pair_vector.get(i);
			
			v.add(p.first());
			
			v.add(p.second());
		}
		
		return v;		
	}
}
