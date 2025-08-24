
package paexif;

import static paglobal.PaUtils.*;

import java.math.BigDecimal;
import java.util.Vector;

/**
 * @author Andrii Dashkov
 *
 */
public class PaExifElement {

	public PaElementType  m_type;
	
	public int m_i;

	public PaExifElement(PaElementType b, int i) {

			m_type = b;
			
			m_i = i;
	}
	
	public PaExifElement() {

		m_type = PaElementType.Byte;
		
		m_i = 0;
	}
	
	public int count() { return m_i; }
	
	public Long toLong()
	{
		return null;
	}

	
	public Integer toShort() {
		
		return null;
	}
	
	public String toString() {
		
		return new String(getGuiStrs("noDataString"));
	}
	
	public BigDecimal toDouble() 
	{
		return null;
	}
	
	public Vector<Long> getLongValues(int numberOfElements) 
	{
		return null;
	}
	
	public Vector<Integer> toInt(int numberOfComponents) { return null; } 
	
}


