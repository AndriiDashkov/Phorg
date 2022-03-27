/**
 * 
 */
package PaEditor;

/**
 * @author Andrey Dashkov
 * The enum which represents the names of instrument. It is used 
 * for save current parameters of instruments outside the Editor
 */
public enum PaEnumInstrumentNames {

	INST_CROP_WITH_SAVE_BLUR,
	INST_BLUR,
	INST_COLOR_BALANCE,
	INST_CONTRAST,
	INST_CROP,
	INST_FFT,
	INST_GAMMA,
	INST_HORIZON,
	INST_HSI,
	INST_LINEAR,
	INST_RED_EYE,
	INST_SAVE,
	INST_SHARP,
	INST_RESIZE,
	INST_ZOOM,
	INST_ZOOM_RECT,
	INST_SHIFT,
	INST_TURN,
	INST_SMOOTH,
	INST_BINARY,
	MASK,
	EROSION_DILAT,
	INST_ROI_CROP,
	INST_ROI_CHANGE,
	INST_ROI_LOAD,
	NONE;
	
	public static PaEnumInstrumentNames[] toArray()
	{
		PaEnumInstrumentNames[] ar = new PaEnumInstrumentNames[26];
		
		ar[0] =	INST_CROP_WITH_SAVE_BLUR;
		ar[1] = INST_BLUR;
		ar[2] = INST_COLOR_BALANCE;
		ar[3] = INST_CONTRAST;
		ar[4] = INST_CROP;
		ar[5] = INST_FFT;
		ar[6] = INST_GAMMA;
		ar[7] = INST_HORIZON;
		ar[8] = INST_HSI;
		ar[9] = INST_LINEAR;
		ar[10] = INST_RED_EYE;
		ar[11] = INST_SAVE;
		ar[12] = INST_SHARP;
		ar[13] = INST_RESIZE;
		ar[14] = INST_ZOOM;
		ar[15] = INST_ZOOM_RECT;
		ar[16] = INST_SHIFT;
		ar[17] = INST_TURN;
		ar[18] = INST_SMOOTH;
		ar[19] = INST_BINARY;
		ar[20] = MASK;
		ar[21] = NONE;
		ar[22] = EROSION_DILAT;
		ar[23] = INST_ROI_CHANGE;
		ar[24] = INST_ROI_CROP;
		ar[25] = INST_ROI_LOAD;
		
		return ar;
		
	}
}
