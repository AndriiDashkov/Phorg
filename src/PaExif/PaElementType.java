package paexif;

public enum PaElementType
{
    Byte(1),
    
    Ascii(2),
    
    Short(3),
    
    Long(4),
    
    Rational(5),
    
    Undefined(7),
    
    SignedLong(9),
    
    SignedRational(10),
    
    Unknown(100);
    
   	public int value(){ return _type; }

	private final short _type;
	
	PaElementType(int i) {
		
		_type = (short) i;	
	}
	 
	public static PaElementType fromInt(int i)
	{
		switch(i) {
		
			case 1: return PaElementType.Byte;
			
			case 2: return PaElementType.Ascii;
			
			case 3: return PaElementType.Short;
			
			case 4: return PaElementType.Long;
			
			case 5: return PaElementType.Rational;
			
			case 7: return PaElementType.Undefined;
			
			case 9: return PaElementType.SignedLong;
			
			case 10: return PaElementType.SignedRational;
			
			default: return PaElementType.Unknown;
		}
	}
	
};