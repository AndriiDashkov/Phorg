package PaExif;

public class PaExifElementAscii extends PaExifElement {

	private String m_ascci;
	
	public PaExifElementAscii(String s) {
		
		super(PaElementType.Ascii, s.length()+1);
		
		m_ascci = s;
	}
	
	public String toString() {
		
		return m_ascci;
	}

}
