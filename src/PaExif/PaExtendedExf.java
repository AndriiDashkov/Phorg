package PaExif;

public enum PaExtendedExf {
	
	   			ExifVersion              (0x9000),
		        FlashPixVersion          ( 0xA000),
		        ColorSpace               ( 0xA001),
		        ComponentsConfiguration  ( 0x9101),
		        CompressedBitsPerPixel   ( 0x9102),
		        PixelXDimension          ( 0xA002),
		        PixelYDimension          ( 0xA003),
		        MakerNote                ( 0x927C),
		        UserComment              ( 0x9286),
		        RelatedSoundFile         ( 0xA004),
		        DateTimeOriginal         ( 0x9003),
		        DateTimeDigitized        ( 0x9004),
		        SubSecTime               ( 0x9290),
		        SubSecTimeOriginal       ( 0x9291),
		        SubSecTimeDigitized      ( 0x9292),
		        ImageUniqueId            ( 0xA420),

		        ExposureTime             ( 0x829A),
		        FNumber                  ( 0x829D),
		        ExposureProgram          ( 0x8822),
		        SpectralSensitivity      ( 0x8824),
		        ISOSpeedRatings          ( 0x8827),
		        Oecf                     ( 0x8828),
		        ShutterSpeedValue        ( 0x9201),
		        ApertureValue            ( 0x9202),
		        BrightnessValue          ( 0x9203),
		        ExposureBiasValue        ( 0x9204),
		        MaxApertureValue         ( 0x9205),
		        SubjectDistance          ( 0x9206),
		        MeteringMode             ( 0x9207),
		        LightSource              ( 0x9208),
		        Flash                    ( 0x9209),
		        FocalLength              ( 0x920A),
		        SubjectArea              ( 0x9214),
		        FlashEnergy              ( 0xA20B),
		        SpatialFrequencyResponse ( 0xA20C),
		        FocalPlaneXResolution    ( 0xA20E),
		        FocalPlaneYResolution    ( 0xA20F),
		        FocalPlaneResolutionUnit ( 0xA210),
		        SubjectLocation          ( 0xA214),
		        ExposureIndex            ( 0xA215),
		        SensingMethod            ( 0xA217),
		        FileSource               ( 0xA300),
		        SceneType                ( 0xA301),
		        CfaPattern               ( 0xA302),
		        CustomRendered           ( 0xA401),
		        ExposureMode             ( 0xA402),
		        WhiteBalance             ( 0xA403),
		        DigitalZoomRatio         ( 0xA404),
		        FocalLengthIn35mmFilm    ( 0xA405),
		        SceneCaptureType         ( 0xA406),
		        GainControl              ( 0xA407),
		        Contrast                 ( 0xA408),
		        Saturation               ( 0xA409),
		        Sharpness                ( 0xA40A),
		        DeviceSettingDescription ( 0xA40B),
		        SubjectDistanceRange     ( 0x40C);
		        
		    	public int value(){ return _ext; }

		    	private final int _ext;
		    	
		    	 PaExtendedExf(int i) {
		    		_ext = i;	
		    	}
		    	 
	    		public static PaExtendedExf fromInt(int i) {
	    			
	    			switch(i) {
	    			
						case 0x9000: return ExifVersion; 
						case 0xA000: return FlashPixVersion; 
						case 0xA001: return ColorSpace ;  
						case 0x9101: return ComponentsConfiguration;
						case 0x9102: return CompressedBitsPerPixel;
						case 0xA002: return PixelXDimension; 
						case 0xA003: return PixelYDimension;  
						case 0x927C: return MakerNote;    
						case 0x9286: return UserComment;  
						case 0xA004: return RelatedSoundFile;   
						case 0x9003: return DateTimeOriginal; 
						case 0x9004: return DateTimeDigitized;
						case 0x9290: return SubSecTime;
						case 0x9291: return SubSecTimeOriginal;
						case 0x9292: return SubSecTimeDigitized;
						case 0xA420: return ImageUniqueId;
						case 0x829A:	return ExposureTime;
						case 0x829D:	return  FNumber;
						case 0x8822:	return ExposureProgram;
						case 0x8824:	return SpectralSensitivity;
						case 0x8827:	return  ISOSpeedRatings;
						case 0x8828:	return Oecf;
						case 0x9201:	return ShutterSpeedValue;
						case 0x9202:	return ApertureValue;
						case 0x9203:	return BrightnessValue;
						case 0x9204:	return ExposureBiasValue;
						case 0x9205:	return  MaxApertureValue;
						case 0x9206:	return SubjectDistance ;
						case 0x9207:	return MeteringMode;
						case 0x9208:	return  LightSource;
						case 0x9209:	return  Flash ;
						case 0x920A:	return  FocalLength;
						case 0x9214:	return SubjectArea;
						case 0xA20B:	return FlashEnergy;
						case 0xA20C:	return SpatialFrequencyResponse;
						case 0xA20E:	return FocalPlaneXResolution;
						case 0xA20F:	return FocalPlaneYResolution;
						case 0xA210:	return FocalPlaneResolutionUnit;
						case 0xA214:	return SubjectLocation;
						case 0xA215:	return ExposureIndex;
						case 0xA217:	return SensingMethod;
						case 0xA300:	return FileSource;
						case 0xA301:	return  SceneType  ;
						case 0xA302:	return CfaPattern;
						case 0xA401:	return CustomRendered;
						case 0xA402:	return  ExposureMode;
						case 0xA403:	return WhiteBalance;
						case 0xA404:	return DigitalZoomRatio;
						case 0xA405:	return FocalLengthIn35mmFilm;
						case 0xA406:	return SceneCaptureType ;
						case 0xA407:	return GainControl;
						case 0xA408:	return Contrast;
						case 0xA409:	return Saturation;
						case 0xA40A:	return   Sharpness;
						case 0xA40B:	return  DeviceSettingDescription ;
						case 0x40C:	return   SubjectDistanceRange;
						
	    			default:
	    			}
	    			
	    			return null;
	    		}

}
