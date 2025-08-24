package paexif;

import static paglobal.PaUtils.getGuiStrs;

import java.util.Vector;

public class PaExifElementByte extends PaExifElement {
	
	private Vector<Integer> m_byte_vector;

	@SuppressWarnings("unchecked")
	public PaExifElementByte(Vector<Integer> v) {
		
		super(PaElementType.Byte, v.size());
		
		m_byte_vector = (Vector<Integer>) v.clone();
	}
	
	public String toString()
	{
		if( m_byte_vector == null ||  m_byte_vector.isEmpty()) { 
			
			return getGuiStrs("noDataString"); 
		}
		
		return m_byte_vector.get(0).toString(); 
		
	}

}
