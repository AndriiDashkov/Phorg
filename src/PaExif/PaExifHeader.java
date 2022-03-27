package PaExif;


public class PaExifHeader
{
    public int m_tag; 
    
    public int m_type; 
    
    public int m_count; 

    private long m_offset; 
    
    private short[] m_offsetBytes= new short[4];
    
    private char[] m_offsetAscii = new char[4]; 
    
    private int[] m_offsetShorts = new int[2];
    
    public long getOffSet() { return  m_offset; } 
    
    public short getOffSetByte(int index) { return  m_offsetBytes[index]; }
    
    public short[] getOffSetBytes() { return  m_offsetBytes; }
    
    public int getOffSetShort(int index) { return  m_offsetShorts[index]; }
    
    public int[] getOffSetShorts() { return  m_offsetShorts; }
    
    public char getOffSetAscii(int index) { return   m_offsetAscii[index]; }
    
    public char[] getOffSetAsciis() { return   m_offsetAscii; }
    

    public void setOffSetBytes(short[] b,PaByteOrder byteOrder) 
    {
    	
    	byte[] by = new byte[4];
    	
    	for(int i=0; i < 4; ++i) {	by[i] = (byte) b[i]; }	
    	
    	String s = new String(by);
    	
    	int sizeS = s.length(); 
    
    	
    	for(int i=0; i < 4; ++i){
    		
    		m_offsetBytes[i] = b[i]; 
    		
    		if(sizeS <= i) {
    			
    			m_offsetAscii[i] = '\u0000';
    			
    		} else {
    			
    			m_offsetAscii[i] = s.charAt(i);
    		}
    	}
    
    	
    	if(byteOrder == PaByteOrder.BigEndian) {	
    		
    		m_offsetShorts[0] = (int) (( ((int)b[0]) <<8) | ((int)b[1])); 
    		
    		m_offsetShorts[1] = (int) (( ((int)b[2]) <<8) | ((int)b[3]));
    		
		} else {
			
	    	m_offsetShorts[0] = (int) (((int)b[0]) | (((int)b[1]) <<8) );
	    	
	    	m_offsetShorts[1] = (int) (((int)b[2]) | (((int)b[3]) <<8) );
		}
    	
    	
		if(byteOrder == PaByteOrder.BigEndian) {	
			
			m_offset = (long) ((b[0] << 24 ) | (b[1] << 16) | (b[2] << 8) | (b[3] << 0)); 
		}
		else {
			
			m_offset = ((b[3] << 24 ) | (b[2] << 16) | (b[1] << 8) | (b[0] << 0));
		}
    	
    }

};