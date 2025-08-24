package paexif;

import static paglobal.PaUtils.getGuiStrs;

import java.util.Vector;

import paglobal.PaLog;

public class PaExifElementInt extends PaExifElement {


	private Vector<Long> m_int_vector;

	@SuppressWarnings("unchecked")
	public PaExifElementInt(Vector<Long> v) {
		
		super(PaElementType.Long, v.size());
		
		 m_int_vector = (Vector<Long>) v.clone();
	}
	
	public Long toLong()
	{		
		if(m_int_vector.isEmpty()) return null;
		
		return m_int_vector.elementAt(0);
	}

	
	public String toString()
	{		
		if(m_int_vector.isEmpty()) return getGuiStrs("noDataString");
		
		return toLong().toString();
	}
	
	public Vector<Long> getLongValues(int numberOfElements) 
	{
		if( m_int_vector.isEmpty() || numberOfElements > m_int_vector.size()) {
			
			PaLog.writeLogOnly("Desirable number of elements is too big for PaExifElementInt", null );
			
			return null;
		}
		
		Vector<Long> v = new Vector<Long>();
		
		for(int i=0; i <  numberOfElements; ++i) {
			
			v.add(m_int_vector.get(i));
		}
		
		return v;		
	}
}
