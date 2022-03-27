package PaExif;

public enum PaImageExf {
	
	Width(0x0100), 
	Length(0x0101),
	BitsPerSample(0x0102),
	Compression(0x0103),  
	PhotometricInterpretation(0x0106),
	Orientation(0x0112),  
	SamplesPerPixel(0x0115),
	Planar(0x011C),
	YCbCrSubSampling(0x0212),
	ResX(0x011A),
	ResY(0x011B),
	ResUnit(0x0128),
	StripOffsets(0x0111),
	RowsPerStrip(0x0116),
	StripByteCounts(0x0117),
	TransferFunction(0x012D),
	WhitePoint(0x013E),
	PrimaryChromaciticies(0x013F),
	YCbCrCoefficients(0x0211),
	ReferenceBlackWhite(0x0214),
	DateTime(0x0132),  
	ImageDesc(0x010E),
	Make(0x010F),
	Model(0x0110),
	Software(0x0131),
	Artist(0x013B), 
	Copyright(0x8298),
	ExifPointer(0x8769), 
	GpsInfoPointer(0x8825);
	
	public int value(){ return m_image; }

	private final int m_image;
	
	PaImageExf(int i) {
		
		m_image = i;	
	}
	
	public static PaImageExf fromInt(int i) {
		
		switch(i) {
		
		case 0x0100: return   PaImageExf.Width;
		case 0x0101: return PaImageExf.Length;
		case 0x0102: return PaImageExf.BitsPerSample;
		case 0x0103: return PaImageExf.Compression;
		case 0x0106: return PaImageExf.PhotometricInterpretation;
		case 0x0112: return PaImageExf.Orientation;
		case 0x0115: return PaImageExf.SamplesPerPixel;
		case 0x011C: return PaImageExf.Planar;
		case 0x0212: return PaImageExf.YCbCrSubSampling;
		case 0x011A: return PaImageExf.ResX;
		case 0x011B: return PaImageExf.ResY;
		case 0x0128: return PaImageExf.ResUnit;
		case 0x0111: return PaImageExf.StripOffsets;
		case 0x0116: return PaImageExf.RowsPerStrip;
		case 0x0117: return PaImageExf.StripByteCounts;
		case 0x012D: return PaImageExf.TransferFunction;
		case 0x013E: return PaImageExf.WhitePoint;
		case 0x013F: return PaImageExf.PrimaryChromaciticies;
		case 0x0211: return PaImageExf.YCbCrCoefficients;
		case 0x0214: return PaImageExf.ReferenceBlackWhite;
		case 0x0132: return PaImageExf.DateTime;
		case 0x010E: return PaImageExf.ImageDesc;
		case 0x010F: return PaImageExf.Make;
		case 0x0110: return PaImageExf.Model;
		case 0x0131: return PaImageExf.Software;
		case 0x013B: return PaImageExf.Artist;
		case 0x8298: return PaImageExf.Copyright;
		case 0x8769: return PaImageExf.ExifPointer;
		case 0x8825: return PaImageExf.GpsInfoPointer;
		default:
		}
		return null;
	}

};
