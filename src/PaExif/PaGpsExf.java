package PaExif;

public enum PaGpsExf {
	
    		VersionId         ( 0x0000),
            LatitudeRef       ( 0x0001),
            Latitude          ( 0x0002),
            LongitudeRef      ( 0x0003),
            Longitude         ( 0x0004),
            AltitudeRef       ( 0x0005),
            Altitude          ( 0x0006),
            TimeStamp         ( 0x0007),
            Satellites        ( 0x0008),
            Status            ( 0x0009),
            MeasureMode       ( 0x000A),
            Dop               ( 0x000B),
            SpeedRef          ( 0x000C),
            Speed             ( 0x000D),
            TrackRef          ( 0x000E),
            Track             ( 0x000F),
            ImageDirectionRef ( 0x0010),
            ImageDirection    ( 0x0011),
            MapDatum          ( 0x0012),
            DestLatitudeRef   ( 0x0013),
            DestLatitude      ( 0x0014),
            DestLongitudeRef  ( 0x0015),
            DestLongitude     ( 0x0016),
            DestBearingRef    ( 0x0017),
            DestBearing       ( 0x0018),
            DestDistanceRef   ( 0x0019),
            DestDistance      ( 0x001A),
            ProcessingMethod  ( 0x001B),
            AreaInformation   ( 0x001C),
            DateStamp         ( 0x001D),
            Differential      ( 0x001E);
            
	    	public int value(){ return _gps; }

	    	private final int _gps;
	    	
	    	PaGpsExf(int i) {
	    		
	    		_gps = i;	
	    	}
	    	
	    	public static PaGpsExf fromInt(int i) 
	    	{
	    		switch(i) {
	    		
					case 0x0000: return VersionId  ;
					case 0x0001: return LatitudeRef;
					case 0x0002: return Latitude;
					case 0x0003: return LongitudeRef ;
					case 0x0004: return Longitude;
					case 0x0005: return  AltitudeRef;
					case 0x0006: return Altitude ;
					case 0x0007: return TimeStamp ;
					case 0x0008: return Satellites;
					case 0x0009: return Status;
					case 0x000A: return MeasureMode;
					case 0x000B: return Dop;
					case 0x000C: return SpeedRef;
					case 0x000D: return Speed;
					case 0x000E: return TrackRef;
					case 0x000F: return Track;
					case 0x0010: return ImageDirectionRef;
					case 0x0011: return ImageDirection;
					case 0x0012: return MapDatum;
					case 0x0013: return DestLatitudeRef;
					case 0x0014: return DestLatitude;
					case 0x0015: return  DestLongitudeRef;
					case 0x0016: return DestLongitude;
					case 0x0017: return  DestBearingRef;
					case 0x0018: return DestBearing;
					case 0x0019: return  DestDistanceRef;
					case 0x001A: return DestDistance;
					case 0x001B: return ProcessingMethod;
					case 0x001C: return  AreaInformation;
					case 0x001D: return  DateStamp;
					case 0x001E: return Differential ;
					default:
	    		}
	    	
	    		return null;
	    	}

}
