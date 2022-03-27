package PaExif;

public class PaExifElementUndef extends PaExifElement {

	private byte[] m_bytes;
	
	public PaExifElementUndef(byte[] b) {
		
		super(PaElementType.Undefined, b.length+1);
		
		m_bytes = b;
	}

	public PaExifElementUndef(char[] m_offsetAscii, int m_count) {
		
		super(PaElementType.Undefined, m_count);
		
		m_bytes = new byte[m_offsetAscii.length];
		
		for(int i=0; i <  m_offsetAscii.length; ++i) {
			
			m_bytes[i] =  (byte)m_offsetAscii[i];
			
		}
	}
}
