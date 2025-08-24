
package pacollection;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paglobal.PaUtils;


/**
 * @author Andrii Dashkov
 * Container for saving and loading the information about recently used files and data
 */
public class PaRecentData {

	{
		//PaEventDispatcher.get().addConnect(PaEventDispatcher.SAVE_EVENT, this, "saveSettings");
		
		//to give a last chance to save something
		PaEventDispatcher.get().addConnect(PaEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT, this, "saveSettingsIfNeed");
		

	}
	
	public static String LOCALE_DATE_DEFAULT_CAPTION ="localeDefault";
	
	public static final String ALBUM_ELEMENT = "AlbmS";
	
	public static final String ID_LAST_ATTR = "idLast";
	
	public static final String NEXT_ID_ATTR = "nextId_photos";
	
	public static final String SUBJECTS_ELEMENT = "Subjects";

	private boolean m_saving_has_been_required = false;
	
	//container for ids of info messages, which have been marked as "don't show any more"
	private ArrayList<String> m_recent_rois = new ArrayList<String>();
	
	private ArrayList<String> m_recent_pix_saves = new ArrayList<String>();
	
	private final String SETT_ELEMENT = "Recents";
	
	private final String ROI_RECENTS_ELEMENT = "RoiRecents";
	
	private final String ROI_FILE_ELEMENT = "RoiFile";
	
	private final String ROI_FILE_PATH_ATTR = "RoiPath";
	
	private final String PIX_RECENTS_ELEMENT = "PixRecents";
	
	private final String PIX_FILE_ELEMENT = "PixFile";
	
	private final String PIX_FILE_PATH_ATTR = "PixPath";

	public PaRecentData() {
		
		//these lists must always have the first empty item
		m_recent_rois.add("");
		
		m_recent_pix_saves.add("");
	}
	
	public void  addToRecentRoiList(String p) {
		
		if(m_recent_rois.size() >= 15) {
			
			//the 0  item is empty, it must no be deleted
			m_recent_rois.remove(1);
		}
		
		if(!m_recent_rois.contains(p)) {
		
			m_recent_rois.add(p);
			
			setReqSavingFlag();
		}
	}
	
	public void  addtoRecentPixList(String p) {
		
		if(m_recent_pix_saves.size() >= 15) {
				
			//the 0  item is empty, it must no be deleted
			m_recent_pix_saves.remove(1);
		}
		
		if(!m_recent_pix_saves.contains(p)) {
			
			m_recent_pix_saves.add(p);
			
			setReqSavingFlag();
		}
	}
	
	public ArrayList<String> getRecentRoiList() {
		
		return m_recent_rois;
	}
	
	public ArrayList<String>  getRecentPixList() {
		
		return m_recent_pix_saves;
	}
	
	
	public void load_Parameters (String configFile) {
	
		m_recent_rois.clear();
		
		m_recent_pix_saves.clear();
		
		m_recent_rois.add("");
		
		m_recent_pix_saves.add("");
		
		InputStream	in = null;
		
		try 
		{
			
			if ( ! Files.exists(Paths.get(configFile) )) { 
				
				if (! createConfigFile(configFile) ) {
					
					//not critical, we can go further
					return;
					
				}
			}
	
			in = new FileInputStream(configFile);	
		
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
		
			
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT) {
					
					if (reader.getLocalName().equals(SETT_ELEMENT)) {
						
					}
					
					if (reader.getLocalName().equals(ROI_FILE_ELEMENT)) {
						
					    String a = reader.getAttributeValue(null, ROI_FILE_PATH_ATTR);
					    
					    m_recent_rois.add(a);
					
					}
					
					if (reader.getLocalName().equals(PIX_FILE_ELEMENT)) {
						
					    String a = reader.getAttributeValue(null, PIX_FILE_PATH_ATTR);
					    
					    m_recent_pix_saves.add(a);
					
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

	public  void saveRecents (PaEvent eventSave)  {
		
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
				getXMLPath().concat(PaUtils.get().getRecentsXMLName()));
		
		XMLStreamWriter writer = factory.createXMLStreamWriter(fSt, "UTF-8");
		
		try {
		
			writer.writeStartDocument("UTF-8", "1.0");
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeStartElement(SETT_ELEMENT);
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeDTD(ADD_TAB);
			
			writer.writeStartElement(ROI_RECENTS_ELEMENT);
			
			//the 0 index is for empty item, no need to save it
			for (int i = 1; i< m_recent_rois.size(); i++) {
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(ROI_FILE_ELEMENT);
				
				writer.writeAttribute(ROI_FILE_PATH_ATTR, m_recent_rois.get(i));
				
				writer.writeEndElement();		
				
			}
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeStartElement(PIX_RECENTS_ELEMENT);
			
			for (int i=0; i <  m_recent_pix_saves.size(); i++) {
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(PIX_FILE_ELEMENT);
				
				writer.writeAttribute(PIX_FILE_PATH_ATTR,  m_recent_pix_saves.get(i));
				
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
	
	private boolean createConfigFile(String configFile) {
	
		
	
		String roiFiles = 	"<" + ROI_RECENTS_ELEMENT + ">"+
				"<" + ROI_FILE_ELEMENT + " ></" + ROI_FILE_ELEMENT + ">" +
				"</" + ROI_RECENTS_ELEMENT + ">";
		
		String pixFiles = 	"<" + PIX_RECENTS_ELEMENT + ">"+
				"<" + PIX_FILE_ELEMENT + " ></" + PIX_FILE_ELEMENT + ">" +
				"</" + PIX_RECENTS_ELEMENT + ">";
		

		
		String s = "<?xml version='1.0' encoding='UTF-8'?>" + "<" + SETT_ELEMENT + ">"
				+ roiFiles  + pixFiles +  "</"+SETT_ELEMENT+">";
		
		
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
	} 
}

