package PaExif;

import java.io.ByteArrayInputStream;


public class PaByteArrayStream extends ByteArrayInputStream {
	
	
	private PaByteOrder m_byteOrder;

	public PaByteArrayStream(byte[] arg0) {
		
		super(arg0);
		
	}

	public PaByteArrayStream(byte[] arg0, int arg1, int arg2) {
		
		super(arg0, arg1, arg2);
		
	}
	
	public void setByteOrder(PaByteOrder byteOrder ) {
		
		m_byteOrder = byteOrder;
	}
	
	public void seek(long offset) {
		
		pos = (int) offset;
				
	}
	
	public long getStreamPosition() { return (long)pos; }
	
	
	public int readUnsignedByte() {
		
		int v = this.read();
		
		return v; 	
	}
	
	public int readUnsigned16() {//2 bytes
		
		int v0 =  readUnsignedByte();
		
		int v1 =  readUnsignedByte();
		
		if(m_byteOrder == PaByteOrder.BigEndian) {	
			
			return (int) ((v0 <<8) | (v1)); //TODO проверить варианты для BigEndian
		}
		
		return (int) ((v0) | (v1 <<8) );
		
	}
	
	public long readUnsigned32() {//2 bytes
		
		long v0 =  readUnsignedByte();
		
		long v1 =  readUnsignedByte();
		
		long v2 =  readUnsignedByte();
		
		long v3 =  readUnsignedByte();
		
		if(m_byteOrder == PaByteOrder.BigEndian) {	
			
			return (long) ((v0 << 24 ) | (v1 << 16) | (v2 << 8) | (v3 << 0)); //TODO check BigEndian?
		}
		
		return (long) ((v3 << 24 ) | (v2 << 16) | (v1 << 8) | (v0 << 0));
		
	}
	
	
	
	public String readString(int bytes)
	{		
		int off = 0;
		
		byte[] b = new byte[bytes];
		
		this.read(b, off, bytes);
		
		return new String(b);			
	}

}
