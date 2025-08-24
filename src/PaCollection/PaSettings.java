package pacollection;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paforms.PaExtEditorTable;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * Container for saving and loading settings of the application. Saved and loaded info can have various purpose: for example, recent
 * files informatio also is saved here.
 */
public class PaSettings {

	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SAVE_EVENT, this, "saveSettings");
		
		//to give a last chance to save something
		PaEventDispatcher.get().addConnect(PaEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT, this, "saveSettingsIfNeed");
	}
	
	public static String LOCALE_DATE_DEFAULT_CAPTION ="localeDefault";
	
	public static final String ALBUM_ELEMENT = "AlbmS";
	
	public static final String ID_LAST_ATTR = "idLast";
	
	public static final String NEXT_ID_ATTR = "nextId_photos";
	
	public static final String SUBJECTS_ELEMENT = "Subjects";

	private int m_initSortingIndex = 1; //initial sorting index
	
	private String _standartRezervPlase;
	
	private int _photoScale;
	
	private int _columnsAmount;
	
	private int _orientation;
	
	private boolean _alVisible;
	
	private boolean _phVisible;
	
	private boolean _teVisible;
	
	private boolean _viVisible;
	
	private boolean m_logEnabled;
	
	private boolean m_customIconsEnabled;
	
	private boolean m_saving_has_been_required = false;
	
	private int m_specIsonsSize = 200;
	
	int m_minTimerDelay = 1;
	
	int m_maxTimerDelay = 10;
	
	int m_currentTimerDelay = 3;

	private Dimension m_instrWinInitSize = new Dimension(900,700);
	
	private float m_zoomStep = 0.05f;
	
	private Color m_instrumentsColor = Color.GREEN;	
	
	private Color m_redEyeColor = Color.black; //initial color for correcting Red Eye effect
	
	private int m_widthMarker = 20; //size of marker for crop frame
	
	private int m_contrastStep = 5; 
	
	private Color m_selectColor = Color.BLUE;	
	
	private boolean createImagesCopiesFlag = true;
	
	private String language = new String("ru");
	
	private String country = new String("RU");
	
	//format will be loaded from the settings file
	private String guiDateFormat = new String(DATE_FORMAT);
	
	private int m_maxLogFileLength = 10;//in bytes
	
	/**
	 * fixed size of preview area in instruments dialog
	 */
	private Dimension m_prevAreaSize = new Dimension(500,1000);
	
	/**
	 * fixed height of histogram panel in instruments dialog
	 */
	private int m_histPanelHeight = 200;
	
	/**
	 * type of double click image reaction
	 * 0 - Edit; 1- Properties; 2 - Instruments
	 */
	private int m_dblClickreactionType = 0;
	
	/*
	 * 1st bit - show name of the image in the albom
	 * 2nd bit - show name of the image file
	 * 3d bit - show the date of the image 
	 * 4 bit - show the id of the image
	 */
	private short m_bitMaskImCap = 0x0f;//4 bits

	
	private ArrayList< String[] > rowData = new ArrayList<String []>();
	
	//container for ids of info messages, which have been marked as "don't show any more"
	private ArrayList<Integer> m_info_not_shown = new ArrayList<Integer>();
	
	HashMap<Integer,Integer> m_hiddenDialogsChoice = new HashMap<Integer,Integer>();	
	
	private final String SETT_ELEMENT = "Settings";
	
	private final String IMAGE_FILES_PLACE_ATTR = "placeOfPhotoFiles";
	
	private final String VIEW_SCALE_ATTR = "viewScale";
	
	private final String COLUMN_AMOUNT_ATTR =  "columnAmount";
	
	private final String ORIENTATION_ATTR =  "orientation";
	
	private final String ALL_VISIBLE_ATTR = "alVisible";
	
	private final String PH_VISIBLE_ATTR = "phVisible";
	
	private final String TE_VISIBLE_ATTR = "teVisible";
	
	private final String VI_VISIBLE_ATTR = "viVisible";
	
	private final String LANGUAGE_ATTR = "language";
	
	private final String COUNTRY_ATTR = "country";
	
	private final String IMAGES_ACC_COPIES_ATTR = "specImagesCreation";
	
	private final String INITIAL_SORT_INDEX_ATTR = "initSortIndex";
	
	private final String EXT_EDITORS_ELEMENT = "ExternalEditors";
	
	private final String EXT_EDITOR_ELEMENT = "ExternalEditor";
	
	private final String EXT_MENU_NAME_ATTR = "menuName";
	
	private final String EXT_PATH_ATTR = "pathCommandName";
	
	private final String EXT_KEYS_ATTR = "keys";
	
	private final String INF_NOT_SHOW = "InfoMessagesNottoShow";
	
	private final String INF_NOT_SHOW_ID = "Infmess";
	
	private final String INF_NOT_SHOW_ID_ATTR = "id";
	
	private final String GUI_DATE_FORMAT_ATTR ="guiDateFormat";
	
	private final String ZOOM_STEP_ATTR = "zoomStep";
	
	private final String  INSTR_WIN_INIT_HEIGHT_ATTR = "instrWindowInitHeight";
	
	private final String  INSTR_WIN_INIT_WIDTH_ATTR = "instrWindowInitWidth";
	
	private final String INSTR_COLOR_RED_ATTR = "instrRed";
	
	private final String INSTR_COLOR_GREEN_ATTR = "instrGreen";
	
	private final String INSTR_COLOR_BLUE_ATTR = "instrBlue";
	
	private final String SELECT_COLOR_RED_ATTR = "selectRed";
	
	private final String SELECT_COLOR_GREEN_ATTR = "selectGreen";
	
	private final String SELECT_COLOR_BLUE_ATTR = "selectBlue";
	
	private final String INSTR_MARKER_WIDTH_ATTR = "markerWidth";
	
	private final String INSTR_RE_COLOR_RED_ATTR = "instrReRed";
	
	private final String INSTR_RE_COLOR_GREEN_ATTR = "instrReGreen";
	
	private final String INSTR_RE_COLOR_BLUE_ATTR = "instrReBlue";
	
	private final String INSTR_CONTRAST_STEP_ATTR = "contrastStep";
	
	private final String LOG_ENABLED_ATTR = "logEnabled";
	
	private final String CUSTOM_ICONS_ATTR = "customIcons";
	
	private final String MAX_SIZE_LOG_FILE_ATTR = "logMaxSize";//megabytes
	
	private final String BOOST_ICONS_SIZE_ATTR = "boostIconsSize";
	
	private final String PREV_AREA_SIZE_ATTR = "previewAreaSize";
	
	private final String HISTOGRAM_PANEL_SIZE_ATTR = "histHeight";
	
	private final String DBL_CLICK_TYPE_ATTR = "dblClikType";
	
	private final String SHOW_INFO_IMAGE_ATTR = "showInfoImage";
	
	private final String INF_DIALOG_CHOICE = "Dialogs";
	
	private final String INF_DIALOG_CHOICE_ELEMENT = "DialogParameters";
	
	private final String INF_DIALOG_CHOICE_ELEMENT_ID_ATTR = "id";
	
	private final String INF_DIALOG_CHOICE_ELEMENT_CODE_ATTR = "code";
	
	
	public PaSettings () {

		_standartRezervPlase = "";
		
		_photoScale = 0;
		
		_columnsAmount = 0;
		
		_orientation = 0;
		
		_alVisible = true;
		
		_phVisible = true;
		
		_teVisible = true;
		
		_viVisible = true;
		
		m_customIconsEnabled = true;
		
	}
	
	public String getStandardFolderPlace() {
		
		return _standartRezervPlase;
	}

	public void setStandardFolderPlace(String standartRezervPlase) {
		
		_standartRezervPlase = standartRezervPlase;
	}

	public int getPhotoScale() {
		
		return _photoScale;
	}

	public void setPhotoScale(int photoScale) {
		
		_photoScale = photoScale;
	}

	public int getColumnsAmount() {
		
		return _columnsAmount;
	}

	public void setColumnsAmount(int columnsAmount) {
		
		_columnsAmount = columnsAmount;
	}
	
	
	public int getOrientation() {
		
		return _orientation;
	}

	public void setOrientation(int orientation) {
		
		_orientation = orientation;
	}
	
	public boolean is_alVisible() {
		
		return _alVisible;
	}

	public void set_alVisible(boolean alVisible) {
		
		_alVisible = alVisible;
	}

	public boolean is_phVisible() {
		
		return _phVisible;
	}

	public void set_phVisible(boolean phVisible) {
		
		_phVisible = phVisible;
	}
	/**
	 * 
	 * @return the flag of visibility for subjects form
	 */
	public boolean isSubjectsVisible() {
		
		return _teVisible;
	}

	public void setSubjectsVisible(boolean teVisible) {
		
		_teVisible = teVisible;
	}

	public boolean is_viVisible() {
		
		return _viVisible;
	}

	public void set_viVisible(boolean viVisible) {
		
		_viVisible = viVisible;
	}
	
	public void setSelectColor(Color c) {
		
		m_selectColor = c;
	}
	
	public Color getSelectColor() {
		
		return m_selectColor;
	}

	
	
	public boolean getCreateImagesCopiesFlag() {
		
		return createImagesCopiesFlag;
	}
	
	public void setCreateImagesCopiesFlag(boolean flag) {
		
		createImagesCopiesFlag = flag;
	}
	
	public String getLanguage() {
		
		return language ;
	}
	
	public void setLanguage(String l) {
		
		language = l;
	}
	
	public String getCountry() {
		
		return country ;
	}
	
	public void setCountry(String l) {
		
		country = l;
	}
	
	public void setGuiDateFormat(String format) {
		
		if ( format != null ) {
			
			guiDateFormat = format;
		}
		
	}
	
	/**
     * <p>Returns the size of boost images. If the size is higher, the quality is better, but the 
     * performance is worth and vice versa</p>
     */
	public int getSpecIconsSize() { return m_specIsonsSize; }
	
	/**
     * <p>Sets the size of boost images. If the size is higher, the quality is better, but the 
     * performance is worth</p>
     * @param s - one dimension size of boost images
     */
	public void setSpecIconsSize(int s) { m_specIsonsSize = s;}
	
	public String getGuiDateFormat() {
		
		return guiDateFormat;
	}
	
	public boolean firstInit() {
		
		return true;
	}
	
	public ArrayList< String[] >  getExtEditorsList() {
		
		return rowData;
	}
	
	
	public void load_Parameters (String configFile) {
	
		rowData.clear();
		
		InputStream	in = null;
		
		try 
		{
			
			environmentCheck( configFile);
	
			in = new FileInputStream(configFile);	
		
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
		
			
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT) {
					
					if (reader.getLocalName().equals(SETT_ELEMENT)) {
						
						setStandardFolderPlace(reader.getAttributeValue(null, IMAGE_FILES_PLACE_ATTR));
						
						setPhotoScale(Integer.parseInt(reader.getAttributeValue(null,  VIEW_SCALE_ATTR)));
						
						setColumnsAmount(Integer.parseInt(reader.getAttributeValue(null, COLUMN_AMOUNT_ATTR)));
						
						setOrientation(Integer.parseInt(reader.getAttributeValue(null, ORIENTATION_ATTR)));
						
						set_alVisible(Boolean.parseBoolean(reader.getAttributeValue(null, ALL_VISIBLE_ATTR)));
						
						set_phVisible(Boolean.parseBoolean(reader.getAttributeValue(null, PH_VISIBLE_ATTR)));
						
						setSubjectsVisible(Boolean.parseBoolean(reader.getAttributeValue(null, TE_VISIBLE_ATTR)));
						
						set_viVisible(Boolean.parseBoolean(reader.getAttributeValue(null, VI_VISIBLE_ATTR)));
						
						setCustomIconsEnabled(Boolean.parseBoolean(reader.getAttributeValue(null, CUSTOM_ICONS_ATTR)));
						
						setLogEnabled(Boolean.parseBoolean(reader.getAttributeValue(null, LOG_ENABLED_ATTR)));
												
						setInitialSorting(Integer.parseInt(reader.getAttributeValue(null, INITIAL_SORT_INDEX_ATTR)));
						
						setInitialSorting(Integer.parseInt(reader.getAttributeValue(null, INITIAL_SORT_INDEX_ATTR)));
						
						setGuiDateFormat(reader.getAttributeValue(null, GUI_DATE_FORMAT_ATTR));
						
						setCreateImagesCopiesFlag( Boolean.parseBoolean(reader.getAttributeValue(null, IMAGES_ACC_COPIES_ATTR)) );
						
						language= reader.getAttributeValue(null, LANGUAGE_ATTR);
						
						country= reader.getAttributeValue(null, COUNTRY_ATTR);
						
						try {
							
							int s = Integer.parseInt(reader.getAttributeValue(null, PREV_AREA_SIZE_ATTR));
							
							m_prevAreaSize = new Dimension(s,1000);
						}
						catch( NumberFormatException e) {
							
							m_prevAreaSize.width = 500;
							
							m_prevAreaSize.height = 1000;
						}
						
						try {
							
							int s = Integer.parseInt(reader.getAttributeValue(null,HISTOGRAM_PANEL_SIZE_ATTR));
							
							m_histPanelHeight = s;
						}
						catch( NumberFormatException e) {
							
							m_histPanelHeight = 200;
						}
						
						try {
							
							short s =Short.parseShort(reader.getAttributeValue(null, SHOW_INFO_IMAGE_ATTR));
							
							setInfoBitmask(s);
						}
						catch( NumberFormatException e) {
							
							setInfoBitmask((short)1); //show only name
						}
						
						
						try {
							
							m_instrWinInitSize = new Dimension( Integer.parseInt(reader.getAttributeValue(null, INSTR_WIN_INIT_WIDTH_ATTR)),
								 Integer.parseInt(reader.getAttributeValue(null, INSTR_WIN_INIT_HEIGHT_ATTR)));
						}
						catch( NumberFormatException e) {
							
							 m_instrWinInitSize.width = 800;
							 
							 m_instrWinInitSize.height = 800;
						}
						
						try {
							m_maxLogFileLength = Integer.parseInt(reader.getAttributeValue(null, MAX_SIZE_LOG_FILE_ATTR));
						}
						catch( NumberFormatException e) {
							
							m_maxLogFileLength = 1;
						}
						
						try {
							m_specIsonsSize = Integer.parseInt(reader.getAttributeValue(null, BOOST_ICONS_SIZE_ATTR));
						}
						catch( NumberFormatException e) {
							
							m_specIsonsSize = 300;
						}
						
						try {
							m_dblClickreactionType = Integer.parseInt(reader.getAttributeValue(null, DBL_CLICK_TYPE_ATTR));
						}
						catch( NumberFormatException e) {
							
							m_dblClickreactionType = 0;
						}
						
						try {
							String sT= reader.getAttributeValue(null, ZOOM_STEP_ATTR);
							
							if ( sT == null ) { 
								
								m_zoomStep = 0.01f;  
							}
							else {
								
								m_zoomStep = Float.parseFloat(sT);
								
							}
						}
						catch (NumberFormatException e )  {
							
							m_zoomStep = 0.01f;  
							
						}
						
						//instruments color loading
						loadInstrumentsColor(reader);
						
						loadInstrumentReColor(reader);	
						
						loadInstrumentMarkerSize(reader);
						
						loadSelectColor(reader);
						
						loadContrastStep(reader);
														
					}
					
					if (reader.getLocalName().equals(EXT_EDITOR_ELEMENT)) {
						
					    String[] ar = new String[ PaExtEditorTable.COLUMNS ];
					    
					    ar[0]= reader.getAttributeValue(null, EXT_MENU_NAME_ATTR);
					    
					    ar[1]= reader.getAttributeValue(null, EXT_PATH_ATTR);
					    
					    ar[2]= reader.getAttributeValue(null, EXT_KEYS_ATTR);
					    
						rowData.add(ar);
					
					}
						
				}
			}	
			
			reader.close(); 
		
		}
		catch (XMLStreamException e) {
		 
		 writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
		}
		catch (IOException  e) {
			
			writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
		}
		finally {
			
			try {
				
				in.close();
				
			} 
			catch (IOException e) {}
		}
	}

	private void loadInstrumentsColor(XMLStreamReader reader) {
		
		try {
			
			int red = Integer.parseInt(reader.getAttributeValue(null, INSTR_COLOR_RED_ATTR));
			
			int green = Integer.parseInt(reader.getAttributeValue(null, INSTR_COLOR_GREEN_ATTR));
			
			int blue = Integer.parseInt(reader.getAttributeValue(null, INSTR_COLOR_BLUE_ATTR));
			
			setInstrumentsColor(new Color(red,green,blue));
			
		}
		catch(NumberFormatException e) {
			
			setSelectColor(Color.GREEN);
		}		
	}
	
	private void loadSelectColor(XMLStreamReader reader) {
		
		try {
			
			int red = Integer.parseInt(reader.getAttributeValue(null, SELECT_COLOR_RED_ATTR));
			
			int green = Integer.parseInt(reader.getAttributeValue(null, SELECT_COLOR_GREEN_ATTR));
			
			int blue = Integer.parseInt(reader.getAttributeValue(null, SELECT_COLOR_BLUE_ATTR));
			
			setSelectColor(new Color(red,green,blue));
		}
		catch(NumberFormatException e) {
			
			setSelectColor(Color.BLUE);
		}
		
	}
	
	private void loadInstrumentReColor(XMLStreamReader reader) {
		
		try {
			
			int red = Integer.parseInt(reader.getAttributeValue(null, INSTR_RE_COLOR_RED_ATTR));
			
			int green = Integer.parseInt(reader.getAttributeValue(null, INSTR_RE_COLOR_GREEN_ATTR));
			
			int blue = Integer.parseInt(reader.getAttributeValue(null, INSTR_RE_COLOR_BLUE_ATTR));
			
			this.setRedEyeColor(new Color(red,green,blue));
		}
		catch(NumberFormatException e) {
			
			setSelectColor(Color.black);
		}
		
	}
	
	private void loadInstrumentMarkerSize(XMLStreamReader reader) {
		
		try {
			
		 m_widthMarker = Integer.parseInt(reader.getAttributeValue(null, INSTR_MARKER_WIDTH_ATTR));
		 
		}
		catch( NumberFormatException e) {
			
			 m_widthMarker = 20;
		}			
	}
	
	private void loadContrastStep(XMLStreamReader reader) {
		
		try {
			
			m_contrastStep = Integer.parseInt(reader.getAttributeValue(null, INSTR_CONTRAST_STEP_ATTR));
		}
		catch( NumberFormatException e) {
			
			m_contrastStep = 5;
		}		
	}
	//
	/**
	 *  gets object for localization
	 * @param resName - resource name
	 * @return the resource for language
	 */
	public ResourceBundle getResourceBundle( String resName)
	{
		Locale currentLocale;
		
	   if ( language.isEmpty() && country.isEmpty() )	{
		   
		   currentLocale = Locale.getDefault(); 
	   }
	   else {
		   
		   currentLocale =  new Locale(language, country); 
	   }

      //for normal work the file with specific locale should exist
       return ResourceBundle.getBundle( "PaTranslation."+resName, currentLocale);
	}

	public  void saveSettings (PaEvent eventSave)  {
		
		if ( eventSave.getEventType() != PaEventDispatcher.SAVE_EVENT ) { return; }
		
		try {		
			
			saveXMLdata();
			
		} catch (FileNotFoundException e) {
	
			 writeLog("FileNotFoundException  : " + NEXT_ROW, e, true, false, true);
			 
		} catch (XMLStreamException e) {
	
			 writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
		}
	}
	
	public  void saveSettingsIfNeed(PaEvent ev)  {
		
		if (m_saving_has_been_required) { 
			
			
			if ( ev.getEventType() != PaEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT ) { return; }
			
			try {		
				
				saveXMLdata();
				
			} catch (FileNotFoundException e) {
		
				 writeLog("FileNotFoundException  : " + NEXT_ROW, e, true, false, true);
				 
			} catch (XMLStreamException e) {
		
				 writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
			}
		}
	}
	
	public void setReqSavingFlag() {
		
		m_saving_has_been_required = true;
	}

	// save xml data
	public  void saveXMLdata() throws FileNotFoundException, XMLStreamException {
		
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		
		//  "UTF-8" must be set here
		FileOutputStream fSt = new FileOutputStream(PaUtils.get().
				getXMLPath().concat(PaUtils.get().getSettingsXMLName()));
		
		XMLStreamWriter writer = factory.createXMLStreamWriter(fSt, "UTF-8");
		
		try {
		
			writer.writeStartDocument("UTF-8", "1.0");
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeStartElement(SETT_ELEMENT);
			
			writer.writeAttribute(IMAGE_FILES_PLACE_ATTR, getStandardFolderPlace());
			
			writer.writeAttribute(VIEW_SCALE_ATTR, Integer.toString(getPhotoScale()));
			
			writer.writeAttribute(COLUMN_AMOUNT_ATTR, Integer.toString(getColumnsAmount()));
			
			writer.writeAttribute(ORIENTATION_ATTR, Integer.toString(getOrientation()));
			
			writer.writeAttribute(ALL_VISIBLE_ATTR, Boolean.toString(is_alVisible()));
			
			writer.writeAttribute(PH_VISIBLE_ATTR, Boolean.toString(is_phVisible()));
			
			writer.writeAttribute(TE_VISIBLE_ATTR, Boolean.toString(isSubjectsVisible()));
			
			writer.writeAttribute(VI_VISIBLE_ATTR, Boolean.toString(is_viVisible()));
			
			writer.writeAttribute(CUSTOM_ICONS_ATTR, Boolean.toString(isCustomIconsEnabled()));
	
			writer.writeAttribute(LOG_ENABLED_ATTR, Boolean.toString(isLogEnabled()));
			
			writer.writeAttribute(IMAGES_ACC_COPIES_ATTR, Boolean.toString(this.getCreateImagesCopiesFlag()));
			
			writer.writeAttribute(LANGUAGE_ATTR, language);
			
			writer.writeAttribute(COUNTRY_ATTR, country);
			
			writer.writeAttribute(INITIAL_SORT_INDEX_ATTR, Integer.toString(getInitialSortingIndex()));
					
			writer.writeAttribute(GUI_DATE_FORMAT_ATTR, getGuiDateFormat() );
			    	
			writer.writeAttribute(ZOOM_STEP_ATTR, Float.toString( m_zoomStep ));
			
			writer.writeAttribute(INSTR_WIN_INIT_WIDTH_ATTR, Integer.toString( m_instrWinInitSize.width ));
			
			writer.writeAttribute(INSTR_WIN_INIT_HEIGHT_ATTR, Integer.toString(  m_instrWinInitSize.height ));
			
			writer.writeAttribute(SHOW_INFO_IMAGE_ATTR, Short.toString(m_bitMaskImCap));
			
			writeInstrumentsColor(writer);
			
			writeInstrumentReColor(writer);
			
			writeSelectColor(writer);
			
			writer.writeAttribute(INSTR_MARKER_WIDTH_ATTR, Integer.toString(  m_widthMarker ));
			
			writer.writeAttribute(INSTR_CONTRAST_STEP_ATTR, Integer.toString( m_contrastStep ));
			
			writer.writeAttribute(MAX_SIZE_LOG_FILE_ATTR, Integer.toString( m_maxLogFileLength )); 
			
			writer.writeAttribute(PREV_AREA_SIZE_ATTR, Integer.toString( m_prevAreaSize.width));
			
			writer.writeAttribute(HISTOGRAM_PANEL_SIZE_ATTR, Integer.toString( m_histPanelHeight));
			
			writer.writeAttribute(BOOST_ICONS_SIZE_ATTR, Integer.toString( m_specIsonsSize ));
		
			writer.writeAttribute(DBL_CLICK_TYPE_ATTR, Integer.toString(m_dblClickreactionType));
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeDTD(ADD_TAB);
			
			writer.writeStartElement(EXT_EDITORS_ELEMENT);
			
			for (int i=0; i< rowData.size(); i++) {
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(EXT_EDITOR_ELEMENT);
				
				writer.writeAttribute(EXT_MENU_NAME_ATTR , rowData.get(i)[0]);
				
				writer.writeAttribute(EXT_PATH_ATTR , rowData.get(i)[1]);
				
				writer.writeAttribute(EXT_KEYS_ATTR , rowData.get(i)[2]);
				
				writer.writeEndElement();		
				
			}
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			writer.writeDTD(NEXT_ROW);
			
			//list of messages which are no visible any more
			writer.writeStartElement(INF_NOT_SHOW);
			
			for (int i = 0; i< m_info_not_shown.size(); i++) {
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(INF_NOT_SHOW_ID);
				
				writer.writeAttribute(INF_NOT_SHOW_ID_ATTR ,  Integer.toString(m_info_not_shown.get(i)));
	
				writer.writeEndElement();		
			}
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			
			writer.writeDTD(NEXT_ROW);
			
			
			//list of messages which user choises
			writer.writeStartElement(INF_DIALOG_CHOICE);
			
			for (Integer key : m_hiddenDialogsChoice.keySet()) {
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(INF_DIALOG_CHOICE_ELEMENT);
				
				writer.writeAttribute(INF_DIALOG_CHOICE_ELEMENT_ID_ATTR,  Integer.toString(key));
				
				writer.writeAttribute( INF_DIALOG_CHOICE_ELEMENT_CODE_ATTR,  Integer.toString(m_hiddenDialogsChoice.get(key)));
	
				writer.writeEndElement();		
			}
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			writer.writeEndDocument();
		}
		finally {
			
			writer.close();
			
			if(fSt != null) {
				
				try {
					
					fSt.close();
				} 
				catch (IOException e) {}
			}
		}
	}

	private void writeInstrumentsColor(	XMLStreamWriter writer) throws XMLStreamException  {
				
		writer.writeAttribute(INSTR_COLOR_RED_ATTR, Integer.toString(getInstrumentsColor().getRed()));
		
		writer.writeAttribute(INSTR_COLOR_BLUE_ATTR, Integer.toString(getInstrumentsColor().getBlue()));
		
		writer.writeAttribute(INSTR_COLOR_GREEN_ATTR, Integer.toString(getInstrumentsColor().getGreen()));
		
	}
	
	private void writeSelectColor(	XMLStreamWriter writer) throws XMLStreamException  {
				
		writer.writeAttribute(SELECT_COLOR_RED_ATTR, Integer.toString(getSelectColor().getRed()));
		
		writer.writeAttribute(SELECT_COLOR_BLUE_ATTR, Integer.toString(getSelectColor().getBlue()));
		
		writer.writeAttribute(SELECT_COLOR_GREEN_ATTR, Integer.toString(getSelectColor().getGreen()));
		
	}
	
	private void writeInstrumentReColor(	XMLStreamWriter writer) throws XMLStreamException  {
				
		writer.writeAttribute(INSTR_RE_COLOR_RED_ATTR, Integer.toString( getRedEyeColor().getRed()));
		
		writer.writeAttribute(INSTR_RE_COLOR_BLUE_ATTR, Integer.toString(getRedEyeColor().getBlue()));
		
		writer.writeAttribute(INSTR_RE_COLOR_GREEN_ATTR, Integer.toString(getRedEyeColor().getGreen()));
		
	}
	//functions for system initialization
	private void createAppDirs() 
	{		
		    File dir=new File(PaUtils.get().getXMLPath ());
		    
		    if(! dir.exists()){
		
		        dir.mkdirs();
		    }
		    dir=new File(PaUtils.get().getPhotoPath ());
		    
		    if(! dir.exists()){
				
		        dir.mkdirs();
		    }
		    dir=new File(PaUtils.get().getPathToAlbumsIcons() );
		    
		    if(! dir.exists()){
				
		        dir.mkdirs();
		    }		
	}
	
	private boolean createConfigFile(String configFile) {
	
		String sExtEditors= null;
		
		Locale currentLocale = Locale.getDefault();
		
		
		if ( OS_TYPE == PaUtils.OSType.WIN ) {
			
			sExtEditors = 	"<"+EXT_EDITORS_ELEMENT + ">"+
			"<"+EXT_EDITOR_ELEMENT+" "+EXT_MENU_NAME_ATTR+"='Paint editor'"+" "+ EXT_PATH_ATTR+"='C:\\Windows\\System32\\mspaint.exe'"
					+" "+EXT_KEYS_ATTR+"=''  ></"+EXT_EDITOR_ELEMENT+">"+
			"<"+EXT_EDITOR_ELEMENT+" "+EXT_MENU_NAME_ATTR+"='GraphicsGale'"+" "+ EXT_PATH_ATTR+"='C:\\Program Files (x86)\\GraphicsGale FreeEdition\\Gale.exe'"
					+" "+EXT_KEYS_ATTR+"=''  ></"+EXT_EDITOR_ELEMENT+">"+
			"</"+EXT_EDITORS_ELEMENT + ">";
			
		}
		if ( OS_TYPE == PaUtils.OSType.LINUX ) {
			
			sExtEditors = 	"<"+EXT_EDITORS_ELEMENT + ">"+
			"<"+EXT_EDITOR_ELEMENT+" "+EXT_MENU_NAME_ATTR+"='Gimp'"+" "+ EXT_PATH_ATTR+"='/usr/bin/gimp'"
					+" "+EXT_KEYS_ATTR+"=''  ></"+EXT_EDITOR_ELEMENT+">"+
		
			"</"+EXT_EDITORS_ELEMENT + ">";
		}
		
		String inf_messages = "<" + INF_NOT_SHOW + ">" + "</" + INF_NOT_SHOW + ">";
		
		String inf_messages_2 = "<" + INF_DIALOG_CHOICE + ">" + "</" + INF_DIALOG_CHOICE + ">";
		
		String s = "<?xml version='1.0' encoding='UTF-8'?>" + "<"+SETT_ELEMENT+" "+IMAGE_FILES_PLACE_ATTR+"='"+ 
				PaUtils.get().getPhotoPath()+"' "+VIEW_SCALE_ATTR+"='240' "+COLUMN_AMOUNT_ATTR+
				"='3' "+ORIENTATION_ATTR+"='2' "+ALL_VISIBLE_ATTR+"='true'" + " " + INITIAL_SORT_INDEX_ATTR + "='1'"+
				" "+PH_VISIBLE_ATTR+"='true' "+TE_VISIBLE_ATTR+"='true' "+VI_VISIBLE_ATTR+"='true' "+LANGUAGE_ATTR+" ='"
				+currentLocale.getLanguage()+"' " + 
				COUNTRY_ATTR +" = '"+currentLocale.getCountry()+"' "+ CUSTOM_ICONS_ATTR +"='true' "+
				MAX_SIZE_LOG_FILE_ATTR +"='1' "+BOOST_ICONS_SIZE_ATTR+"='300' "+
				IMAGES_ACC_COPIES_ATTR+"='true' "+ GUI_DATE_FORMAT_ATTR+"='"+LOCALE_DATE_DEFAULT_CAPTION+ "' "+ZOOM_STEP_ATTR+"='0.10' "+
				INSTR_WIN_INIT_HEIGHT_ATTR +"='600' " + INSTR_WIN_INIT_WIDTH_ATTR +"='800' "+
				PREV_AREA_SIZE_ATTR + "='500' " + HISTOGRAM_PANEL_SIZE_ATTR + "='200' " +
				INSTR_COLOR_RED_ATTR +"='0' "+INSTR_COLOR_BLUE_ATTR +"='0' "+INSTR_COLOR_GREEN_ATTR +"='255' "+
				SELECT_COLOR_RED_ATTR +"='0' "+SELECT_COLOR_BLUE_ATTR +"='255' "+SELECT_COLOR_GREEN_ATTR +"='0' "+
				INSTR_RE_COLOR_RED_ATTR +"='69' "+INSTR_RE_COLOR_BLUE_ATTR +"='58' "+INSTR_RE_COLOR_GREEN_ATTR +"='104' "+
				DBL_CLICK_TYPE_ATTR + "='0' " +
				SHOW_INFO_IMAGE_ATTR + "='1' " +
				INSTR_MARKER_WIDTH_ATTR+"='20' "+ INSTR_CONTRAST_STEP_ATTR +"='5' "+LOG_ENABLED_ATTR +"='false' "+" >" + sExtEditors + inf_messages +
				inf_messages_2 + "</"+SETT_ELEMENT+">";
		
		
		byte[] b = s.getBytes();
		
		try {
			
			FileOutputStream out = new FileOutputStream(configFile);
			
			out.write(b);
			
			out.close();
			
		} catch (FileNotFoundException e) {
			
			 writeLog("FileNotFoundException  : can't create config file " + NEXT_ROW, e, true, false, true);
			 
			// no possibility to start application
			return false;
		}
		catch (IOException e) {
			
			 writeLog("IOException  : can't create config file " + NEXT_ROW, e, true, false, true);
			 
			// no possibility to start application
			 return false;
		}
		
		return true;
	} //TODO
	private boolean createAlbumFile() {
		
		String s = "<?xml version='1.0' encoding='UTF-8'?><"+ALBUM_ELEMENT+" "+ID_LAST_ATTR+"='1' "+NEXT_ID_ATTR+"='1'> </"+ALBUM_ELEMENT+">";
		
		byte[] b = s.getBytes();
		
		try {
			
			FileOutputStream out = new FileOutputStream( PaUtils.get().getAlbumsXMLFullName ());
			
			out.write(b);
			
			out.close();
			
		} catch (FileNotFoundException e) {
			
			// TODO no possibility to start application
			 writeLog("FileNotFoundException  : can't create albom file " + NEXT_ROW, e, true, false, true);
			 
			 return false;
			 
		}
		catch (IOException e) {
			
			// TODO no possibility to start application
			writeLog("IOException  : can't create albom file " + NEXT_ROW, e, true, false, true);
			
			return false;
		}
		
		return true;
	}
	private boolean createSubjectFile() {
		
		String s = "<?xml version='1.0' encoding='UTF-8'?><"+SUBJECTS_ELEMENT+" "+ID_LAST_ATTR+"='0'> </"+SUBJECTS_ELEMENT+">";
		
		byte[] b = s.getBytes();
		
		try {
			
			FileOutputStream out = new FileOutputStream( PaUtils.get().getSubjectsXMLFullName());
			
			out.write(b);
			
			out.close();
			
		} catch (FileNotFoundException e) {
			
			// no possibility to start application
			 writeLog("FileNotFoundException  : can't create subjects file " + NEXT_ROW, e, true, false, true);
			 
			return false;
		}
		catch (IOException e) {
			
			// no possibility to start application
			writeLog("IOException  : can't create subjects file " + NEXT_ROW, e, true, false, true);
			
			return false;
		}
		
		return true;
	}
	
	private void createDefaultAlbumIcon() throws IOException {
				
		String oldName = concatPathName(PaUtils.get().getIconsPath(), 
				PaUtils.get().getDefaultAlbumsIconName());
		
		String newName = concatPathName(PaUtils.get().getPathToAlbumsIcons(), 
				PaUtils.get().getDefaultAlbumsIconName());
		
		
		File f = new File(newName );
		
		if (  f.exists() )  return;
		
		FileChannel	source = null;
		
		FileChannel destination = null;
		
		FileInputStream fileInputStream = null;
		
		FileOutputStream fileOutputStream = null;
		
		try {
	
			fileInputStream = new FileInputStream(oldName);
			
			source = fileInputStream.getChannel();
		    
			fileOutputStream = new FileOutputStream(newName);
			
			destination = fileOutputStream.getChannel();
			
			destination.transferFrom(source, 0, source.size());
		    
		}
		catch (FileNotFoundException e1) {
			
			// not critical exception
			writeLog("FileNotFoundException : can't create albom default icon " + NEXT_ROW, e1, true, false, true);
		}
		finally {
			
			source.close();
			
			destination.close();
			
			fileInputStream.close();
			
			fileOutputStream.close();
			
		}
		
	}

	private void environmentCheck(String configFile) 
	{
		createAppDirs();
		
		File f = new File(configFile);
		
		if ( ! f.exists() ) { 
			
			if (! createConfigFile(configFile) ) System.exit(0);
			
		}
		
		f = new File( PaUtils.get().getAlbumsXMLFullName() );
		
		if ( ! f.exists() ) { 
			
			if (! createAlbumFile() ) System.exit(0);	
		}
		
		f = new File(PaUtils.get().getSubjectsXMLFullName() );
		
		if ( ! f.exists() ) {
			
			if ( ! createSubjectFile() ) System.exit(0);
				
		}
		
		try {
			createDefaultAlbumIcon();
		} 
		catch (IOException e) {
			
			// not critical exception
			writeLog("IOException : can't create albom default icon " + NEXT_ROW, e, true, false, true);
		}
	}
	
	
	public int getMinSliderTimerDelay() {  return  m_minTimerDelay;  } 
	
	public void setMinSliderTimerDelay( int i) { m_minTimerDelay = i;  }
	
	public int getMaxSliderTimerDelay() { return  m_maxTimerDelay; } 

	public void setMaxSliderTimerDelay( int i) {  m_maxTimerDelay = i; }

	public int getMCurrentSliderTimerDelay() { return  m_currentTimerDelay; } 

	public void setCurrentSliderTimerDelay( int i) { m_currentTimerDelay = i; }
	
	
	public Dimension getInstrumentsWinInitSize() { return m_instrWinInitSize; }
	
	public  float getZoomStep() { return m_zoomStep; }
	
	public void setInstrumentsWinInitSize(Dimension d) {   m_instrWinInitSize=d; }
	
	public  void setZoomStep(float s) { m_zoomStep = s; }
	
	
	public Color getInstrumentsColor() { 
		
		return m_instrumentsColor; 
		}
	public void setInstrumentsColor(Color c) { 
		
		m_instrumentsColor = c; 
	}
	
	public void setInstrumentMarkerSize( Integer w ) {
		
		m_widthMarker= w;
	}
	public int getInstrumentMarkerSize(  ) {
		
		return m_widthMarker;
	}
	
	public Color getRedEyeColor() { 
		
		return m_redEyeColor; 
		}
	public void setRedEyeColor(Color c) { 
		
		 m_redEyeColor = c; 
		 
	}
	
	public int getContrastStep() {
		
		return m_contrastStep;
	}
	
	public void setLogEnabled(boolean flag) { 
		
		m_logEnabled = flag;
	}
	
	public boolean isLogEnabled() { 
		
		return m_logEnabled; 
	}
	
	
	public void setCustomIconsEnabled(boolean flag) { 
		
		m_customIconsEnabled = flag;
	}
	
	public boolean isCustomIconsEnabled() { 
		
		return 	m_customIconsEnabled; 
	}
	
	public void setContrastStep(int s) {
		
		m_contrastStep = s;
	}
	
	 /**
     * <p>Remark: this is not a CURRENT sorting index. The current sorting index is in PaUtils</p>
     * @return the initial sorting index from the settings 
     */
	public int getInitialSortingIndex() {
		
		return m_initSortingIndex;
	}
	
	 /**
     * <p>Sets the sorting index which will be default after loading of application</p>
     * @param index - sorting index to save into settings 
     */
	public void setInitialSorting(int index) {
		
		m_initSortingIndex = index;
	}
	
	/**
     * <p>Sets the maximum size of log file in bytes</p>
     * @param sz - maximum size of the log file in bytes 
     */
	public void setMaxLogFileLength(int sz)
	{
		m_maxLogFileLength = sz;
	}
	
	/**
     * <p>Gets the maximum size of log file in bytes</p>
     * @return  the maximum size of log file in bytes
     */
	public int getMaxLogFileLength()
	{
		return m_maxLogFileLength;
	}
	
	/**
	 * 
	 * @return - fixed size of preview area in the instruments window
	 */
	public Dimension getPrevAreaFixedSize() {
		
		return new Dimension(m_prevAreaSize);
	}
	
	/**
	 * 
	 * @param d - fixed size of preview area in the instruments window
	 */
	public  void setPrevAreaFixedSize(Dimension d) {
		
		m_prevAreaSize =d;
	}
	
	/**
	 * 
	 * @param d - fixed height of histogram panel in the instruments window
	 */
	public  void setHistoPanelHeight(int d) {
		
		m_histPanelHeight = d;
	}
	
	public int getHistoPanelHeight() {
		
		return m_histPanelHeight;
	}
	
	/**
	 * 
	 * @param index - type of reaction for double click - can be 0,1,2
	 */
	public  void setDblClickReactionType(int index) {
		
		m_dblClickreactionType = index;
	}
	
	/**
	 * 
	 * @return the type of reaction for mouse double click on image
	 */
	public  int getDblClickReactionType() {
		
		return m_dblClickreactionType;
	}
	/**
	 * Sets the info bitmask which determines the info which is shown in view image forms:
	 * 1 bit - name is visible; 2 bit - name of file is visible; 3 bit - date is visible;
	 * 4 bit - id is visible
	 * @param mask
	 */
	public void setInfoBitmask(short mask) {
		
		if(mask > 0x0f) { m_bitMaskImCap = 1; }
		
		m_bitMaskImCap = mask;
		
	}
	
	public short getInfoBitmask() {
		
		return m_bitMaskImCap;
		
	}
	
	public ArrayList<Integer> getNonVisibleInfoMessages(){
		
		return m_info_not_shown;
	}
	
	public HashMap<Integer,Integer> getNonVisibleInfoMessagesCh(){
		
		return  m_hiddenDialogsChoice;
	}
	
	public void clearHiddenDialogLists() {
		
		m_hiddenDialogsChoice.clear();
		
		m_info_not_shown.clear();
		
		setReqSavingFlag();
	}
}
