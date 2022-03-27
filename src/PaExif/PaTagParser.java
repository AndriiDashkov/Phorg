
package PaExif;

import static PaGlobal.PaUtils.getGuiStrs;
import java.math.BigDecimal;
import java.util.Vector;
import PaGlobal.PaLog;

/**
 * @author avd
 * parser is used to convert a tag information into human form strings
 */
public class PaTagParser {

	public PaTagParser() {
		
	}
	
	
	
	/**
	 * @return  orientation string for tag element in the human form
	 */
	public static String getOrientation(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Orientation tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 1: return getGuiStrs("exifOrientationNormal");	
			
			case 2: return getGuiStrs("exifOrientationV180");
			
			case 3: return getGuiStrs("exifOrientationVH180");
			
			case 4: return getGuiStrs("exifOrientationH180");
			
			case 5: return getGuiStrs("exifOrientationL90H180");
			
			case 6: return getGuiStrs("exifOrientationR90");
			
			case 7: return getGuiStrs("exifOrientationR90H180");
			
			case 8: return getGuiStrs("exifOrientationL90");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  string for tag element in the human form
	 */
	public static String getString(PaExifElement el,String tagName) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for "+tagName+" tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		return el.toString();
	}
	
	/**
	 * @return   string for tag element in the human form with units
	 */
	public static String getStringUnit(PaExifElement el,String tagName, String units) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for "+tagName+" tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		return el.toString()+ " " + units;
	}

	/**
	 * @return  camera model string for tag element in the human form
	 */
	public static String getCompression(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Compression tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		int index = el.toShort();
		
		switch(index){	
		
			case 1: return getGuiStrs("exifUncompressed");	
			
			case 2: return getGuiStrs("exifJPEGcompression");
			
			default:return getGuiStrs("noDataString");
		}
	}
	

	/**
	 * @return  bitspersample string for tag element in the human form
	 */
	public static String getBitsPerSample(PaExifElement el) 
	{	
		if(el == null || !(el instanceof PaExifElementShort) ) {
			
			PaLog.writeLogOnly("Exif element for BitsPerSample tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		PaExifElementShort el1 = (PaExifElementShort) el;
		
		return el1.toString(3);
	}

	
	/**
	 * @return  image width or length string for tag element in the human form
	 */
	public static String getImageWidthOrLength(PaExifElement el) 
	{	
		if(el == null || !((el instanceof PaExifElementShort) || (el instanceof PaExifElementInt)) ) {
			
			PaLog.writeLogOnly("Exif element for Image Length/Width tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		if(el instanceof PaExifElementShort) {
			
			PaExifElementShort el1 = (PaExifElementShort) el;
			
			return el1.toShort().toString();
		}
		if(el instanceof PaExifElementInt) {
			
			PaExifElementInt el1 = (PaExifElementInt) el;
			
			return el1.toLong().toString();
		}
		
		return getGuiStrs("noDataString");
	}


	/**
	 * @return  camera model string for tag element in the human form
	 */
	public static String getLongString(PaExifElement el,int after,String tagName) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for "+tagName+" tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		Long l = el.toLong();
		
		if( l == null ) {
			
			PaLog.writeLogOnly("Exif element for "+tagName+" tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		
		if(after == -1) return l.toString();

		 return l.toString();
	}
	
	
	/**
	 * @return  double string for tag element in the human form
	 */
	public static String getDoubleString(PaExifElement el,int after,String tagName) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for "+tagName+" tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		BigDecimal l = el.toDouble();
		
		if( l == null ) {
			
			PaLog.writeLogOnly("Exif element for "+tagName+" tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		String s = l.toString();
		
		if(after == -1) return s;	
		
		int index = s.indexOf(".");
		
		 return s.substring(0,index+after);
	}
	
	
	/**
	 * @return  reference black white string for tag element in the human form
	 */
	public static String getReferenceBlackWhite(PaExifElement el) 
	{	
		if(el == null || !(el instanceof PaExifElementRationalUn)) {
			
			PaLog.writeLogOnly("Exif element for ReferenceBlackWhite tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		PaExifElementRationalUn el1 = (PaExifElementRationalUn) el;
		
		Vector<Long> vec = el1.getLongValues(3);
		
		if( vec == null ) {
			
			PaLog.writeLogOnly("Exif element for ReferenceBlackWhite tag has no an information", null );
			
			return getGuiStrs("noDataString");
		}
		String s = new String("[");
		
		for(int i=0; i < vec.size(); ++i) {	
			
			s = s + " " + vec.get(i) +"," ;
		}
		s= s.substring(0, s.length()-2);
		
		s= s + "]";
		
		return s;
	}
	
	
	/**
	 * @return  exposure program string for tag element in the human form
	 */
	public static String getExposureProgram(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Exposure Program tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 1: return getGuiStrs("exifExpProgManual");	
			
			case 2: return getGuiStrs("exifExpProgNormal");
			
			case 3: return getGuiStrs("exifExpProgAperture");
			
			case 4: return getGuiStrs("exifExpProgShutter");
			
			case 5: return getGuiStrs("exifExpProgCreative");
			
			case 6: return getGuiStrs("exifExpProgAction");
			
			case 7: return getGuiStrs("exifExpProgPortrait");
			
			case 8: return getGuiStrs("exifExpProgLandscape");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  exposure mode string for tag element in the human form
	 */
	public static String getExposureMode(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Exposure Mode tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifExpAuto");
			
			case 1: return getGuiStrs("exifExpModeManual");
			
			case 2: return getGuiStrs("exifExpModeAutoB");	
			
			default:return getGuiStrs("noDataString");
		}
	}

	
	/**
	 * @return  resolution unit string for tag element in the human form
	 */
	public static String getResUnit(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Resolution Unit tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		int index = el.toShort();
		
		switch(index){	
		
			case 1: return getGuiStrs("exifExpNoResUnit");	
			
			case 2: return getGuiStrs("exifExpInchUnit");
			
			case 3: return getGuiStrs("exifExpCentimeterUnit");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  light source string for tag element in the human form
	 */
	public static String getLightSource(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Light Source tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifExpAuto");	
			
			case 1: return getGuiStrs("exifExpLightSourceDayLight");
			
			case 2: return getGuiStrs("exifExpLightSourceFlu");
			
			case 3: return getGuiStrs("exifExpLightSourceTun");
			
			case 4: return getGuiStrs("exifExpLightSourceFlash");
			
			case 9: return getGuiStrs("exifExpLightSourceFineWeather");	
			
			case 10: return getGuiStrs("exifExpLightSourceCloudyWeather");
			
			case 11: return getGuiStrs("exifExpLightSourceShade");
			
			case 12: return getGuiStrs("exifExpLightSourceDaylightF");
			
			case 13: return getGuiStrs("exifExpLightSourceDWF");
			
			case 14: return getGuiStrs("exifExpLightSourceCWF");
			
			case 15: return getGuiStrs("exifExpLightSourceWF");
			
			case 17: return getGuiStrs("exifExpLightSourceSLA");
			
			case 18: return getGuiStrs("exifExpLightSourceSLB");
			
			case 19: return getGuiStrs("exifExpLightSourceSLC");
			
			case 20: return getGuiStrs("exifExpLightSourceD55");
			
			case 21: return getGuiStrs("exifExpLightSourceD65");
			
			case 22: return getGuiStrs("exifExpLightSourceD75");
			
			case 23: return getGuiStrs("exifExpLightSourceD50");
			
			case 24: return getGuiStrs("exifExpLightISOstudioTun");
			
			case 255: return getGuiStrs("exifExpLightSourceOther");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  screen capture type for tag element in the human form
	 */
	public static String getSceneCaptureType(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for screen capture type tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifExpSCTstandard");	
			
			case 1: return getGuiStrs("exifExpSCTlandscape");
			
			case 2: return getGuiStrs("exifExpSCTportrait");
			
			case 3: return getGuiStrs("exifExpSCTnightscene");
			
			default:return getGuiStrs("noDataString");
		}
	}

	
	/**
	 * @return  digital zoom ration tag element in the human form
	 */
	public static String getDigitalZoomRation(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for digital zoom ratio tag does not exist", null );
			
			return getGuiStrs("exifNotUsed");
			
		}
		
		BigDecimal z = el.toDouble();
		
		if(z == null || z.doubleValue() == 0.0 ) { return getGuiStrs("exifNotUsed"); }
		
		return z.toString();
	}
	
	
	/**
	 * @return  photometric interpretation  tag element in the human form
	 */
	public static String getPhotometricInter(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for photometric interpretation tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 2: return getGuiStrs("exifPhInterpRGB");	
			
			case 6: return getGuiStrs("exifPhInterpYCbCr");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  planar or chunky format  tag element in the human form
	 */
	public static String getPlanar(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for planar format tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 1: return getGuiStrs("exifPlanar");	
			
			case 2: return getGuiStrs("exifChunky");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	
	/**
	 * @return  YCbCr subsampling format  tag element in the human form
	 */
	public static String getYCbCrSubsampling(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for YCbCr subsampling tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		Vector<Integer> v = el.toInt(2);
		
		if(v == null ) {
			
			PaLog.writeLogOnly("Exif element for YCbCr subsampling tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		if(v.get(0) == 2 && v.get(1) == 1) {	
			
			return "YCbCr4:2:2";
		}
		if(v.get(0) == 2 && v.get(1) == 2) {	
			
			return "YCbCr4:2:0";
		}
		
		return getGuiStrs("noDataString");
	}
	
	/**
	 * @return  color space tag element in the human form
	 */
	public static String getColorSpace(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for  color space tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		int i = el.toShort();
		
		if( i == 1 ) return "sRGB";
		
		return getGuiStrs("exifColoSpUncalibrated");
	}
	
	/**
	 * @return  sensing method format  tag element in the human form
	 */
	public static String getSensingMethod(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for sensing method tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 1: return getGuiStrs("exifSMNotDefined");	
			
			case 2: return getGuiStrs("exif1ChipCl");
			
			case 3: return getGuiStrs("exif2ChipCl");	
			
			case 4: return getGuiStrs("exif3ChipCl");
			
			case 5: return getGuiStrs("exifClSeqArea");	
			
			case 7: return getGuiStrs("exifTrilinear");
			
			case 8: return getGuiStrs("exifClSeqLinear");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  white balance format  tag element in the human form
	 */
	public static String getWhiteBalance(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for white balance tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifExpAuto");	
			
			case 1: return getGuiStrs("exifManual");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return subject area format  tag element in the human form
	 */
	public static String getSubjectArea(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for subject area tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}

		int count = el.count();
		
		Vector<Integer> v = el.toInt(count);
		
		if(v == null ) {
			
			PaLog.writeLogOnly("Exif element for subject area tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		switch(count){	
		
			case 2: {
				
				String s = new String(" x = ");
				
				s = getGuiStrs("exifPoint") + s + v.get(0)+" y = "+ v.get(1);
				
				return s;
						
			}
			case 3: {
				String s = new String(" x = ");
				
				s = getGuiStrs("exifCircle") + s + v.get(0)+" y = "+ v.get(1)+" d = " + v.get(2);
				
				return s;
			}
			case 4: {
				String s = new String(" x = ");
				
				s = getGuiStrs("exifRectangle") + s + v.get(0)+" y = "+ v.get(1)+" w = " + v.get(2)+" h = " +v.get(3);
				
				return s;
			}
			
			default:return getGuiStrs("noDataString");
		}
	}
	/**
	 * @return  distance to the subject format  tag element in the human form
	 */
	public static String getSubDistanceRange(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for distance to the subject tag does not exist", null );
			
			return getGuiStrs("noDataString");
			
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifSMNotDefined");	
			
			case 1: return getGuiStrs("exifMacro");
			
			case 2: return getGuiStrs("exifCloseView");
			
			case 3: return getGuiStrs("exifDistantView");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  Sharpness tag element in the human form
	 */
	public static String getSharpness(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Sharpness tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifSharpnessNormal");	
			
			case 1: return getGuiStrs("exifSharpnessSoft");
			
			case 2: return getGuiStrs("exifSharpnessHard");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	
	/**
	 * @return  Saturation  tag element in the human form
	 */
	public static String getSaturation(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for Saturation tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifSaturationNormal");	
			
			case 1: return getGuiStrs("exifSaturationLow");
			
			case 2: return getGuiStrs("exifSaturationHigh");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	/**
	 * @return  Contrast  tag element in the human form
	 */
	public static String getContrast(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for contrast tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifContrastNormal");	
			
			case 1: return getGuiStrs("exifContrastSoft");
			
			case 2: return getGuiStrs("exifContrastHard");
			
			default:return getGuiStrs("noDataString");
		}
	}
	
	
	/**
	 * @return  metering mode  tag element in the human form
	 */
	public static String getMeteringMode(PaExifElement el) 
	{	
		if(el == null ) {
			
			PaLog.writeLogOnly("Exif element for metering mode tag does not exist", null );
			
			return getGuiStrs("noDataString");
		}
		
		int index = el.toShort();
		
		switch(index){	
		
			case 0: return getGuiStrs("exifMMUnknown");	
			
			case 1: return getGuiStrs("exifMMAverage");
			
			case 2: return getGuiStrs("exifMMCenterWA");
			
			case 3: return getGuiStrs("exifMMSpot");
			
			case 4: return getGuiStrs("exifMMMultiSpot");
			
			case 5: return getGuiStrs("exifMMPattern");
			
			case 6: return getGuiStrs("exifMMPartial");
			
			default:return getGuiStrs("noDataString");
		}
	}
}
