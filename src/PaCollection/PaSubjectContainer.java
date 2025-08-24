package pacollection;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paevents.PaEventInt;
import paevents.PaEventSubjectRefreshNewPhotoDialog;
import paglobal.PaUtils;
/**
 * 
 * @author Andrii Dashkov
 *
 */
public class PaSubjectContainer {

	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SAVE_EVENT, this, "saveSubjectsEvent");
	}
	
	private final String SUB_ELEMENT =  "subject";
	
	private final String SUB_NAME_ATTR =  "name";
	
	private final String SUB_ID_ATTR = "id";

	private ArrayList<PaSubject> _subjects;
	
	private TreeSet<PaSubject> _subjects_sort;
	
	private String _configTems;
	

	public PaSubjectContainer () {
		
		_subjects = new ArrayList<PaSubject>();
		
		_subjects_sort = new TreeSet<PaSubject>(_subjects);
	}
	
	public PaSubjectContainer (ArrayList<PaSubject> subjects) {
		
		_subjects = subjects;
		
		_subjects_sort.addAll(_subjects);
	}
	

	public boolean addAll(Collection<? extends PaSubject> col) {
		
		return _subjects.addAll(col);
	}
	
    /**
     * 
     * @return the link to the sort collection
     */
    public TreeSet<PaSubject> get_tems_sort() {
    	
		return _subjects_sort;
	}

	/**
	 * 
	 * @return the link to the collection
	 */
	public ArrayList<PaSubject> get_tems () {
		
		return _subjects;
	}
	
	/**
	 * @param  id
	 */
	public PaSubject getTem(int id) {
		
  		Iterator<PaSubject> iter = _subjects.iterator();
  		
  		PaSubject x = null;
  		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if ( x.getId() == id ) {
 				
 				return x;
 			}
 		}
		
 		return null;
	}
	
	/**
	 * 
	 * @param subject
	 * @return the id of the subject
	 */
	public int get_id_tema (PaSubject subject) {
		
		return subject.getId();
	}
	
	/**
	 * 
	 * @param configTem
	 */
  	public void setConfigTems(String configTem) {
  		
 		_configTems = configTem;
 	}
  	
  	/**
  	 * Checks the existence of the subject with name  
  	 * @param sub - sub with name to control
  	 * @return
  	 */
  	public boolean nameExist(PaSubject sub) {
  		
  		Iterator<PaSubject> iter = _subjects.iterator();
  		
		PaSubject x = null;
		
		while (iter.hasNext ()) {
			
			x = iter.next();
			
			if (x.getName().equals(sub.getName())) { 
				return true; 
			}
		}
		
		return false;
  	}
  	
  	/**
  	 * 
  	 * @param sub new subject to add
  	 * @return true if the operation is ok
  	 */
  	public boolean addSubject (PaSubject sub) {

 		if(nameExist(sub)) { return false; }
 		
 		sub.set_id_tem();
 		
 		_subjects.add(sub);
 		
 		_subjects_sort.add(sub);
 		
		PaEventInt _evenNewTema = new PaEventSubjectRefreshNewPhotoDialog(sub);
		
		PaEventDispatcher.get().fireCustomEvent(_evenNewTema);
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SUBJECT_REFRESH_EVENT) );
		
		PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
		
		PaEventDispatcher.get().fireCustomEvent(event);
			
 		return true;
  	}
  	
 /**
  * Edits subject 
  * @param subject
  * @return true if the operation is successful
  */
   	public boolean edit_tems (PaSubject subject) {
   		
   		Iterator<PaSubject> iter = _subjects.iterator();
   		
   		PaSubject x = null;

   		// unique name check
   		while (iter.hasNext ()) {
   			
   			x = iter.next();
   			
   			if (x.getName().equals(subject.getName()) & x.getId() != subject.getId()) {
   				
   				return false;
   			}
   		}
   		Iterator<PaSubject> iter1 = _subjects.iterator();
   		
   		PaSubject y = null;
   		
   		while (iter1.hasNext()) {
   			
   			y = iter1.next();  	
   			
   			if (y.getId() == subject.getId()) {
   				
   				y.setName(subject.getName());
   				
   				_subjects_sort.clear();
   				
   				_subjects_sort.addAll(_subjects);
   				
   				PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
   				
   				PaEventDispatcher.get().fireCustomEvent(event);
   				
  				return true;
  			}
   		}
 		return false; 		
   	}

   /**
    * Removes subject
    * @param subject
    * @return true in any case
    */
 	public boolean remove_tem(PaSubject subject) {
 		
 			_subjects.remove(subject);
 			
 			return true; 
 	}

 	/**
 	 * Removes subject with id
 	 * @param nm
 	 * @return
 	 */
 	public boolean removeSubject(int nm) {

 		PaSubject x = null;
 		
 		Iterator<PaSubject> itr = _subjects.iterator();
 		
 		while (itr.hasNext()) {
 			
 			x=itr.next();
 			
 			int id = x.getId();
 			
 			if (id == nm) {		
 				
 				itr.remove();
 				
   				_subjects_sort.clear();
   				
   				_subjects_sort.addAll(_subjects);
   				
   				PaEventInt event_1 = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
   				
   				PaEventDispatcher.get().fireCustomEvent(event_1);
   				
   				PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SUBJECT_REFRESH_EVENT) );
   				
 				return true;
 			}
 		}
 		
 		return false;
 	}
 	
	/**
	 * Container size
	 * @return number of subjects
	 */
	public int size() {
		
		return _subjects.size();
	}
	
	/**
	 * Saves subjects using the event
	 * @param eventSave
	 */
	public void saveSubjectsEvent (PaEvent eventSave) {
		
		if ( eventSave.getEventType() != PaEventDispatcher.SAVE_EVENT ) { 
			return; 
		}
		
		try {
			
			setConfigTems(PaUtils.get().getXMLPath().concat(PaUtils.get().getTemsXMLName()));
			
			save_tems();

		} catch (FileNotFoundException e) {

			writeLog("FileNotFoundException :  " + NEXT_ROW, e, true, false, true);
			
		} catch (XMLStreamException e) {

			writeLog("XMLStreamException :  " + NEXT_ROW, e, true, false, true);
		}
	}
	

 	/**
 	 * Saves subjects
 	 * @throws FileNotFoundException
 	 * @throws XMLStreamException
 	 */
	public void save_tems () throws FileNotFoundException, XMLStreamException {
		
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		
		//UTF-8 link is critical here
		FileOutputStream fSt = new FileOutputStream(_configTems);
		
		XMLStreamWriter writer = factory.createXMLStreamWriter(fSt, "UTF-8");
		
		try {
			//create header
			writer.writeStartDocument("UTF-8", "1.0");
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeStartElement(PaSettings.SUBJECTS_ELEMENT);
			
			int _value = PaSubject.get_nextId();
			
			String value = Integer.toString(_value);
			
			writer.writeAttribute(PaSettings.ID_LAST_ATTR, value);
	
			// save nodes
			for(PaSubject x : _subjects ) {
				
				String _word = x.getName();
				
				Integer _id = x.getId();
				
				String _idst = Integer.toString(_id);
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(SUB_ELEMENT);
				
				writer.writeAttribute(SUB_NAME_ATTR , _word);
				
				writer.writeAttribute(SUB_ID_ATTR, _idst);
				
				writer.writeEndElement();
				
		
			}
				
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			writer.writeEndDocument();
		}
		finally {
			
			writer.close();
			
			if(fSt != null) {
				
				try {
					
					fSt.close();
					
				} catch (IOException e) {}
			}
		}
	}


	/**
	 * Loads subjects from xml file configFile
	 * @param configFile - xml file with subjects
	 */
	public void  loadSubjects(String configFile) {

		InputStream in = null;
		
		try {
			// input stream
			in = new FileInputStream(configFile);	
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);

			// read document
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT) {
					
					if (reader.getLocalName().equals(PaSettings.SUBJECTS_ELEMENT)) {
						
						PaSubject.set_load_nextId(Integer.parseInt(reader.getAttributeValue(null, PaSettings.ID_LAST_ATTR)));

					}
					if (reader.getLocalName().equals(SUB_ELEMENT)) {
						
						String _word = reader.getAttributeValue(null, SUB_NAME_ATTR);
						
						Integer _id = Integer.parseInt(reader.getAttributeValue(null, SUB_ID_ATTR));
						
						_subjects.add(new PaSubject(_id, _word));
					}
				}
			}
			_subjects_sort.addAll(_subjects);
			
			reader.close();
		
		} catch (FileNotFoundException e) {
			
			writeLog("FileNotFoundException :  " + configFile + NEXT_ROW, e, true, false, true);
			
		} catch (XMLStreamException e) {
			
			writeLog("XMLStreamException :  " + configFile + NEXT_ROW, e, true, false, true);
		}
		finally {
			
			try {
				
				in.close();
				
			} catch (IOException e) {
				
				writeLog("IOException :  " + configFile + NEXT_ROW, e, true, false, true);
			}
		}
	}
}
