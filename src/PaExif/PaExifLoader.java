package paexif;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;

import paenums.PaPair;
import paglobal.PaLog;
import paglobal.PaUtils;

/**
 * <p>Class for reading the exif metadata</p>
 * @author Andrii Dashkov
 *
 */
public class PaExifLoader {
	
    enum TextEncoding
    {
        NoEncoding,
        
        AsciiEncoding,
        
        JisEncoding,
        
        UnicodeEncoding,
        
        UndefinedEncoding
    };
    
    enum PrivateTag
    {
        ExifPointer(0x8769),
        
        GpsInfoPointer(0x8825),
        
        InteroperPointer(0xA005),
        
        JpegIntFormat(0x0201),
        
        JpegIntFormatLength(0x0202);
        
    	public int value(){ return m_prv; }

    	private final int m_prv;
    	
    	PrivateTag(int i) {
    		
    		m_prv = i;	
    	}
    };
	
	private PaByteOrder m_byteOrder;
	
	PaExifElement  m_thumbnailXResolution;
	 
	PaExifElement m_thumbnailYResolution;
	 
	PaExifElement m_thumbnailResolutionUnit;
	 
	PaExifElement m_thumbnailOrientation;
	
    private Map<PaImageExf, PaExifElement> m_imageElements = new HashMap<PaImageExf, PaExifElement>();
    
    private Map<PaExtendedExf, PaExifElement> m_extendedElements = new HashMap<PaExtendedExf, PaExifElement>();
    
    private Map<PaGpsExf, PaExifElement> m_gpsElements = new HashMap<PaGpsExf, PaExifElement>();

	public PaExifLoader() {
		
	}
	
	/**
	 * Loads exif data from the file
	 * @param fileName - file to load exif data
	 * @return
	 */
	public boolean loadFile(String fileName) {
		
		File f= new File( fileName);
		
		if ( f.exists() ) {
			
			FileImageInputStream in = null;
			
			PaByteArrayStream inByte = null;
			
			try {
				try {				
					
					in = new FileImageInputStream(f);
					
					inByte = extractExif(in);
					
					 if(inByte == null) { 
						 
						String s = "Can't read exif data from the file: "+fileName;
						
						PaLog.writeLogOnly(s, null );	
						
						PaLog.writeInfoOnly(PaUtils.getMessagesStrs("cantReadExifInfoMessage"));
						
						return false; 
						 
					 }
					 
					 loadExifElements(inByte);
					 
					 return true;
				} 
				finally {	
					
					if(in != null) in.close();	
					
					if(inByte != null) inByte.close();
				}
			}
			catch (IOException e) {
				
				String s = "Can't read exif data from the file: "+fileName;
				
				PaLog.writeLog(s, null, true, true, true );			
				
				PaLog.writeInfoOnly(PaUtils.getMessagesStrs("cantReadExifInfoMessage"));
		
				return false;
			}							
		}
		return false;
		
	}
	
	public PaExifElement getExifElement(PaImageExf el) {
		
		return m_imageElements.get(el);
	}
	
	public PaExifElement getExifElement(PaGpsExf el) {
		
		return m_gpsElements.get(el);
	}
	
	public PaExifElement getExifElement(PaExtendedExf el) {
		
		return m_extendedElements.get(el);
	}
	
	public boolean loadExifElements(PaByteArrayStream in) 
	{	
		 clearAllMaps();
		
		 long startPos;
		 
		try {
			startPos = in.getStreamPosition();
	
		 String s = in.readString(2);
		 
		 if(s.equals("II") ) {
			 
			 m_byteOrder =  PaByteOrder.LittleEndian;
			 
			 
		 } else if(s.equals("MM")) {
			 
			 m_byteOrder = PaByteOrder.BigEndian;
		 }
		 else return false;
		 
		 in.setByteOrder(m_byteOrder);

		 long id = in.readUnsigned16();		 
		 
		 long offset = in.readUnsigned32(); //4 bytes reading
		
		    if (id != 0x002A) {
		    	
		        return false;
		    }

		    in.seek(startPos + offset);
		    
		    ArrayList<PaExifHeader> headers = new ArrayList<PaExifHeader>();
		    
		    readHeaders(headers,in);
		    	   
		    offset = in.readUnsigned32(); //4 bytes reading

		    fillMapImageExf(m_imageElements,in,startPos, headers);
    
		    PaExifElement exfP = m_imageElements.remove(PaImageExf.ExifPointer);
		    
		    PaExifElement gpsP = m_imageElements.remove(PaImageExf.GpsInfoPointer);
		    
		    if(exfP != null) {
		    	
		    	fillMapExtendedExf(m_extendedElements,in,startPos, exfP );
		    }
		    else {
		    	
				PaLog.writeLogOnly("Can't do remove operation for ExifPointer ", null );	
		    }
		    
		    if(gpsP != null) { 
		    	
		    	fillMapGpsExf(m_gpsElements,in,startPos, gpsP );
		    }
		    else { PaLog.writeLogOnly("Can't do remove operation for GpsPointer ", null );	 }

		    m_extendedElements.remove(PaExtendedExf.fromInt(PrivateTag.InteroperPointer.value()));
		    
		    if (offset != 0) {
		    
		        in.seek(startPos + offset);
		        
		        Map<Integer, PaExifElement> thumbnailElements = new HashMap<Integer, PaExifElement>();
		        
		        ArrayList<PaExifHeader> headersT = new ArrayList<PaExifHeader>();
		        
			    readHeaders(headersT,in);
			    
		        fillMapThumbnail(thumbnailElements,in,startPos,headersT);

		        PaExifElement jpegOffset = thumbnailElements.get(PrivateTag.JpegIntFormat.value());
		        
		        PaExifElement jpegLength = thumbnailElements.get(PrivateTag.JpegIntFormatLength.value());
		        
		        if(jpegOffset == null || jpegLength == null ) {
		        	
		        	PaLog.writeLogOnly("Can't find offset or length element in exif data", null );
		        	
		        	return false;
		        }

		        if (jpegOffset.m_type == PaElementType.Long && jpegOffset.m_i == 1 
		            && jpegLength.m_type == PaElementType.Long && jpegLength.m_i == 1)
		        {
		         
		            in.seek(startPos + jpegOffset.toLong());

		            m_thumbnailXResolution = thumbnailElements.get(PaImageExf.ResX.value());
		            
		            m_thumbnailYResolution = thumbnailElements.get(PaImageExf.ResY.value());
		            
		            m_thumbnailResolutionUnit = thumbnailElements.get(PaImageExf.ResUnit.value());
		            
		            m_thumbnailOrientation = thumbnailElements.get(PaImageExf.Orientation.value());
		        }
		    }
		    
		} catch (IOException e) {
					
			e.printStackTrace();
			
			String s = "Can't read exif info from the input stream  ";
			
			PaLog.writeLogOnly(s, null );	
			
			return false;
		}
		    return true;
		
		
	}
	
	 private void clearAllMaps()
	 {
		 
		m_imageElements.clear();
		
		m_extendedElements.clear();
		
		m_gpsElements.clear(); 
	 }
	 
	 private void readHeaders(ArrayList<PaExifHeader> headers,PaByteArrayStream in) throws IOException 
	 {
		headers.clear();
		 
		long count=0;
	
		count = in.readUnsigned16();
	
		 for(short i = 0 ; i < count; ++i)
		 {
			 PaExifHeader h = new PaExifHeader();	
			 
			 readHeader(h,in);			 
			 
			 headers.add(h);			 
		 }

	 }
	 
	 private void readHeader(PaExifHeader h,PaByteArrayStream in) throws IOException 
	 {
		 
		    
		h.m_tag = (int)in.readUnsigned16();
		
		h.m_type = (int)in.readUnsigned16();
		
		h.m_count = (int) in.readUnsigned32(); //4 bytes reading
		
		short[] b = new short[4];
		
		for(int i=0; i < 4; ++i) { b[i] = (short)in.readUnsignedByte(); }
		
		h.setOffSetBytes(b,m_byteOrder);
 
	 }
	 


	 
	 public void fillMapThumbnail(Map<Integer, PaExifElement> map,
			 PaByteArrayStream in, long startPos, ArrayList<PaExifHeader> headers) throws IOException
		{
		 	map.clear();
		 	
		    for(PaExifHeader header: headers) {
		    	
				PaExifElement el = getElementFromHeader(in,startPos,header);	
				
				map.put(header.m_tag,el); 
						
	    	}		    
		}
	 
	 
	 
	 public void fillMapExtendedExf(Map<PaExtendedExf, PaExifElement> map,
			 PaByteArrayStream in, long startPos, ArrayList<PaExifHeader> headers) throws IOException
		{
		 	map.clear();
		 	
		    for(PaExifHeader header: headers) {
		    	
				PaExifElement el = getElementFromHeader(in,startPos,header);
				
				PaExtendedExf exf  = PaExtendedExf.fromInt(header.m_tag);
				
				if(exf != null) {
					
					map.put(exf,el); 
				}
				else {   
					
					PaLog.writeLogOnly("Can't find the exif extended tag for value "+header.m_tag, null);	    			
				}
	    	}		    
		}
	 
	 
	 public void fillMapExtendedExf(Map<PaExtendedExf, PaExifElement> map,
			 PaByteArrayStream in, long startPos, PaExifElement el) throws IOException
		{
		 
	     map.clear();
	     
	     if (el.m_type == PaElementType.Long && el.m_i == 1) {
	     
	         in.seek(startPos +  el.toLong());
	   
	         ArrayList<PaExifHeader> headers = new ArrayList<PaExifHeader>();
	         
	         readHeaders(headers,in);
	         
	         fillMapExtendedExf( map,in, startPos, headers);
	         
	         return;         
	    
	     } 

	 }
	 
	 public void fillMapImageExf(Map<PaImageExf, PaExifElement> map,
			 PaByteArrayStream in, long startPos, ArrayList<PaExifHeader> headers) throws IOException
		{
		 	map.clear();
		 	
		    for(PaExifHeader header: headers) {
		    	
		    	PaImageExf exf = PaImageExf.fromInt(header.m_tag);
				    	
				PaExifElement el = getElementFromHeader(in,startPos,header);
					
				if(exf != null) {
					
					map.put(exf,el); 
					
				} else { 
					
					PaLog.writeLogOnly("Can't find the exif image tag for value "+header.m_tag, null );	    			
				}
	    	}
		    
		}
	 
	 public void fillMapGpsExf(Map<PaGpsExf, PaExifElement> map,
			 PaByteArrayStream in, long startPos, ArrayList<PaExifHeader> headers) throws IOException
		{
		 	map.clear();
		 	
		    for(PaExifHeader header: headers) {
		    	
				PaExifElement el = getElementFromHeader(in,startPos,header);
				
				PaGpsExf exf = PaGpsExf.fromInt(header.m_tag);
				
				if(exf != null){
					
					map.put(exf,el);
				}
				else {    
					
					PaLog.writeLogOnly( "Can't find the exif image gps for value "+header.m_tag, null );	    			
				}
	    	}
		    
		}
	 
	 
	 public void fillMapGpsExf(Map<PaGpsExf, PaExifElement> map,
			 PaByteArrayStream in, long startPos, PaExifElement el) throws IOException
	{
		 
	     map.clear();
	     
	     if (el.m_type == PaElementType.Long && el.m_i == 1) {
	           	
	         in.seek(startPos +  el.toLong());
	     
	         ArrayList<PaExifHeader> headers = new ArrayList<PaExifHeader>();
	         
	         readHeaders(headers,in);
	         
	         fillMapGpsExf( map,in, startPos, headers);
	         
	         return;         
	       
	     } 
	 
	 }
	 
	 



	public PaExifElement getElementFromHeader(PaByteArrayStream in, long startPos, PaExifHeader header) throws IOException
	 {
	
		PaElementType type = PaElementType.fromInt(header.m_type);
		
		if(type == PaElementType.Unknown) {
			
			PaLog.writeLogOnly( "Can't find the exif header type for value "+header.m_type, null );	
			
			return null;
		}
	
	   switch (type) {
	   
	     	case Byte : 
		     {
	          
	             Vector<Integer> vec = new Vector<Integer>(header.m_count);
	
	             if (header.m_count > 4) {
	            	 
	            	 in.seek(startPos + header.getOffSet());
	            
	                 for (int i = 0; i < header.m_count; i++) {
	                  
	                	 vec.add(i,in.readUnsignedByte());
	                 }
	                 
	             } else {
	            	 
	                 for( int i = 0; i < header.m_count; i++ ) {
	                   
	                	 vec.add(i,(int)header.getOffSetByte(i));
	                 }
	             }
	         
	             return new PaExifElementByte(vec);
		     }
	     	case Undefined:
		     {
		         if (header.m_count > 4) {
		           
		             in.seek(startPos + header.getOffSet());
		             
		             byte[] b = new byte[header.m_count];
		             
		             in.read(b, 0, header.m_count);
		             
		             return new PaExifElementUndef(b);
		         
		         } else {
		           
		             return new PaExifElementUndef(header.getOffSetAsciis(), header.m_count);
		         }
		     }
		    case Ascii:
		    {
		         if (header.m_count > 4) {
		            
		             in.seek(startPos + header.getOffSet());
		            
		             String s = in.readString(header.m_count);
		             
		             return new PaExifElementAscii(s);
		             
		         } else {
		      
		             return new PaExifElementAscii(new String(header.getOffSetAsciis()));
		         }
		    }
		    case Short:
	         {
	      
             	Vector<Integer>  vecS = new Vector<Integer>(header.m_count);
	             
	             if (header.m_count > 2) {
	                
	                 in.seek(startPos + header.getOffSet());
	                 
	                 for (int i = 0; i < header.m_count; i++) {
	                    
	                 	vecS.add(i,in.readUnsigned16());
	                 }
	                 
	             } else {
	            	 
	                 for (int i = 0; i < header.m_count; i++) {
	                 
	                	 vecS.add(i,header.getOffSetShort(i));
	                 }

	             }
	             
	             return new PaExifElementShort(vecS);
	         }   
		     case Long:
	         {
	      
	             Vector<Long> vecI = new Vector<Long>(header.m_count);
	             
	             if (header.m_count > 1) {
	          
	                 in.seek(startPos + header.getOffSet());
	                 for (int i = 0; i < header.m_count; i++) {
	                  
	                 	vecI.add(i,in.readUnsigned32());
	                 }
	             } else if(header.m_count == 1) {
	               
	                 vecI.add(0,header.getOffSet());
	             }
	     
	             return new PaExifElementInt(vecI);
	         }
		     case SignedLong:
		         {
	
		             Vector<Long> vecI = new Vector<Long>(header.m_count);
		             
		             if (header.m_count > 1) {
		       
		                 in.seek(startPos + header.getOffSet());
		                 
		                 for (int i = 0; i < header.m_count; i++) {
		               
		                	vecI.add(i,in.readUnsigned32());
		                 }
		             } else if (header.m_count == 1) {
		          
		                 vecI.add(0,header.getOffSet());
		             }
		      
		             return new PaExifElementInt(vecI);
		         }
		     case Rational:
		         {
		      
		             Vector<PaPair<Long, Long>> vec = new  Vector<PaPair<Long, Long>>(header.m_count);
		             
		             in.seek(startPos + header.getOffSet());
		             
		             for (int i = 0; i < header.m_count; i++) {
		             
		             	long first = in.readUnsigned32();
		             	
		             	long second = in.readUnsigned32();
		             	
		             	PaPair<Long, Long> p = new PaPair<Long, Long>(first,second);
		             	
		             	vec.add(i,p);
		             }

		             return new PaExifElementRationalUn(vec); 
		         }
		     case SignedRational:
		         {
		        
		        	 Vector<PaPair<Long, Long>> vec = new  Vector<PaPair<Long, Long>>(header.m_count);
		
		        	 in.seek(startPos + header.getOffSet());
		        	 
		             for(int i = 0; i < header.m_count; i++){
		            
		             	long first = in.readUnsigned32();
		             	
		             	long second = in.readUnsigned32();
		             	
		             	PaPair<Long, Long> p = new PaPair<Long, Long>(first,second);
		             	
		             	vec.add(i,p);
		             }
		             
		             return new PaExifElementRationalUn(vec); 
		         }
		     default:
		 
				String s = "Can't determine the exif header type for value "+header.m_type;
				
				PaLog.writeLogOnly(s, null );	
				
				return new PaExifElement();
		     }		 
	 }
	
	
   /**
    * Extracts the exif data
    * @param in
    * @return the stream of byte array with exif data or null
    * @throws IOException
    */
	public PaByteArrayStream extractExif( ImageInputStream in ) throws IOException 
	{
		in.setByteOrder(ByteOrder.BIG_ENDIAN);

		int in1 = in.read();
		
		int in2 = in.read();
		
		 int c1 = 0xFF;
		 
		 int c2 = 0xD8;
		
		 if( !(in1==c1 && in2==c2)) { return null; }

		try {
			
			in1 = in.read();
			
			in2 = in.read();
			
			c2 = 0xE1;
			
			while( !(in1==c1 && in2==c2) )
		    {
		        int length = toUnsigned16(in.readShort());
	
		        in.seek(in.getStreamPosition()+length-2);
		        
		        in1 = in.read();
		        
				in2 = in.read();
		        
		    }
		} catch (EOFException ex) {
			
			return null;
		}

        int length = toUnsigned16(in.readShort());
	           
        if(!readBytesInString(in,4).equals("Exif")) return null;

	    in.readShort();

		 IIOByteBuffer buf1 = new IIOByteBuffer(null, 0, 0); 

		 in.readBytes(buf1,  length - 8);
		 
		 return new PaByteArrayStream(buf1.getData());
	    
	}
	
		
	private String readBytesInString(ImageInputStream in, int b) throws IOException
	{
		 IIOByteBuffer buf = new IIOByteBuffer(null, 0, 0); 

		 in.readBytes(buf, b);
		 
		 return new String(buf.getData());		
	}
	

	
	
	public int toUnsigned16(short i) { 
		
		if(i<0) {
			
			return (1<<16)+i;
		}
		
		return i;
	}
	 
	
		/**
	    * Extracts the exif data from the very begining including the start bytes
	    * @param in
	    * @return the stream of byte array with exif data or null
	    * @throws IOException
	    */
		public boolean  extractFullExif(  File f,ByteArrayOutputStream buf) throws IOException 
		{
			
			FileImageInputStream  in = new FileImageInputStream(f);	
			
			try {
				
				in.setByteOrder(ByteOrder.BIG_ENDIAN);
				
				if ( !f.exists() ) { in.close(); return false; }
				
				int in1 = in.read();
				
				int in2 = in.read();
				
				int c1 = 0xFF;
				 
				int c2 = 0xD8;
				
				if( !(in1==c1 && in2==c2)) { in.close(); return false; }
	
				try {
					
					in1 = in.read();
					
					in2 = in.read();
					
					c2 = 0xE1;
					
					while( !(in1==c1 && in2==c2) )
				    {
				        int length = toUnsigned16(in.readShort());
			
				        in.seek(in.getStreamPosition()+length-2);
				        
				        in1 = in.read();
				        
						in2 = in.read();
				        
				    }
				} catch (EOFException ex) {
					
					in.close();
					
					return false;
				}
	
				long pos0 = in.getStreamPosition();
				
		        int length = toUnsigned16(in.readShort());
			           
		        if(!readBytesInString(in,4).equals("Exif")) { in.close(); return false; }
	
			    in.readShort();
			    
			    in.seek(0);
			  
				IIOByteBuffer buf1 = new IIOByteBuffer(null, 0, 0); 
				 
				in.readBytes(buf1,  length  + (int)pos0 );
				 
				buf.write(buf1.getData());
			 
			}
			finally {
				
				in.close();
			 
			}
			 
			return true;
		    
		}
		
		/**
		 * Reads the image data (without JFIF header  ) to buf; This function have to work with JFIF files only
		 * @param f 
		 * @param buf
		 * @return true if the reading was successful
		 * @throws IOException
		 */
		public boolean  extractImageJFIF(  File f,ByteArrayOutputStream buf) throws IOException 
		{
			
			FileImageInputStream  in = new FileImageInputStream(f);	
			
			try {
				in.setByteOrder(ByteOrder.BIG_ENDIAN);
				
				if ( !f.exists() ) { in.close(); return false; }
				
				int in1 = in.read();
				
				int in2 = in.read();
				
				int c1 = 0xFF;
				
				int c2 = 0xD8;
				
				if( !(in1==c1 && in2==c2)) { in.close(); return false; }
	
				try {
					
					in1 = in.read();
					
					in2 = in.read();
					
					c2 = 0xE0;
					
					while( !(in1==c1 && in2==c2) )
				    {
				        int length = toUnsigned16(in.readShort());
			
				        in.seek(in.getStreamPosition()+length-2);
				        
				        in1 = in.read();
				        
						in2 = in.read();
				        
				    }
				} catch (EOFException ex) {
					
					in.close();
					
					return false;
				}
	
				long pos0 = in.getStreamPosition();
				
		        int length = toUnsigned16(in.readShort());
			           
		        if(!readBytesInString(in,4).equals("JFIF")) { in.close(); return false; }
		    
			    in.seek(pos0+length);
			    
			    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	
				while(true) {
					
					int c3 = in.read();
					
					byteArray.write(c3);
					
					if(c3 == 0xFF) {
						
						int c4 = in.read();
						
						byteArray.write(c4);
						
						if(c4 == 0xD9) {
							
							if(in.read() == -1) {
								
								break; 
							}
							
							in.seek(in.getStreamPosition()-1);
						}
					}	
				}
				
				buf.write(byteArray.toByteArray());
			}
			finally {
				
				if(in != null) {
					
					in.close();
				}
			}
			return true;
		    
		}
		
		
	
	/**
	 * Inserts the exif data from file s to the image data in the file t.
	 * @param t - source file with image data
	 * @param s - source file with Exif data
	 * @param newFileFullName - new file full name for merge
	 * @return
	 */
	public boolean getMergeExifBlock(File t, File s, String newFileFullName) {
		
		if ( !t.exists() ) { return false; }
		
		FileImageInputStream in = null;
		
		FileOutputStream outputStream = null;
		
		ByteArrayOutputStream byteArray = null;
		
		try {
				 
				byteArray = new ByteArrayOutputStream();
			
				if(!extractFullExif(s, byteArray)) { return false; }
				
				if(! extractImageJFIF(t, byteArray)) { return false; }
				
					outputStream = new FileOutputStream (newFileFullName); 
					
					byteArray.writeTo(outputStream);
					
					outputStream.flush();
					
					outputStream.close();
					
					return true;
						
		}
		catch (IOException e) {
			
			String s1 = "Can't read data from the file: " + t.getAbsolutePath();
			
			PaLog.writeLog(s1, null, true, true, true );	
			
			return false;
		}
		finally {				
			try {
				
				byteArray.close();
				
				if(outputStream != null) {
					
					outputStream.close();
				}
				
				if(in != null){
					
					in.close();
				}
			} catch (IOException e) {
				
			}							
		}
	}
}
