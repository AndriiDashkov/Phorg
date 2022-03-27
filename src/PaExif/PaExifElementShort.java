package PaExif;

import static PaGlobal.PaUtils.getGuiStrs;
import java.util.Vector;

public class PaExifElementShort extends PaExifElement {


	private Vector<Integer> m_short_vector;

	@SuppressWarnings("unchecked")
	public PaExifElementShort(Vector<Integer> v) {
		
		super(PaElementType.Short, v.size());
		
		 m_short_vector = (Vector<Integer>) v.clone();
	}
	
	public Integer toShort() {
		
		if(m_short_vector == null || m_short_vector.isEmpty()) return null;
		
		return m_short_vector.get(0);
	}
	
	public String toString(int numberOfComponents) 
	{
		String s = new String();
		
		for(int i=0; i <  numberOfComponents; ++i) {
			
			s = m_short_vector.get(i).toString() + " ";
		}
		
		return s;
	}
	
	public String toString() 
	{
		if(m_short_vector == null || m_short_vector.isEmpty()) return getGuiStrs("noDataString");
		
		return m_short_vector.get(0).toString();
		
	}
	
	public Vector<Integer> toInt(int numberOfComponents) 
	{
		if(m_short_vector == null || m_short_vector.isEmpty()) return null;
		
		Vector<Integer> v = new Vector<Integer>(numberOfComponents);
		
		for(int i=0; i <  numberOfComponents; ++i) {
			
			v.add(m_short_vector.get(i));
		}
		
		return v;
	}
}
