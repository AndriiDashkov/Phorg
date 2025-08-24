package pacollection;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
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
import paglobal.PaUtils;

/**
 * Container for album's collection
 * @author Andrii Dashkov
 * @version
 */
public class PaAlbumContainer {


	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SAVE_EVENT, this, "saveAllAlbums");
	}
	
	/**
	 * enum for control problems while add album operation
	 * @author Andrii Dashkov
	 *
	 */
	private enum ValidationControl {
		
		WRONG_NAME, //the name already exists
		
		WRONG_PATH, //the path already exists
		
		WRONG_ID, //duplicate id
		
		WRONG_EXIST_STAND_OPATH, //standard path already exists
		
		WRONG_XML_PATH, //wrong path to xml file
		
		DATA_OK //ok
	};
	
	private final String ALB_ELEMENT = "albm";
	
	private final String ALB_NAME_ATTR = "name";
	
	private final String ALB_ID_ATTR = "id";	
	
	private final String ALB_PARENT_ID_ATTR = "parentId";
	
	private final String ALB_DATE_ATTR = "date";
	
	private final String ALB_PATH_ATTR = "path";
	
	private final String ALB_COMMENT_ATTR = "coment";
	
	private final String ALB_FOLDER_ATTR = "folder";
	
	private ArrayList<PaAlbum> m_albums;

	
	public PaAlbumContainer () {
		
		m_albums = new ArrayList<PaAlbum>();
	
	}
	
	public PaAlbumContainer (ArrayList<PaAlbum> alb) {
		
		m_albums = alb;

	}
	
	/**
	 * 
	 * @return the link to container with albums
	 */
    public ArrayList<PaAlbum> getAlbums() {
    	
		return m_albums;
	}
     
	/**
	 * deletes the album from container
	 * @param pa - album to delete
	 * @return true in any case
	 */
 	private boolean remove(PaAlbum pa) {
 		
 			m_albums.remove(pa);
 
 			return true; 
 	}
 	
 	/**
 	 * Returns null if an album with id doesn't exist
 	 * @param Id - id of the album
 	 * @return the album object
 	 */
 	public PaAlbum getAlbum (int Id) {
 		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if (x.getId() == Id)
 				
 			return x;
 		}
 		
		return null;
 	}
 	
 	/**
 	 * 
 	 * @param name - name of the album
 	 * @return the id of the found album with the name 
 	 */
 	public int getAlbumId (String name) {
 		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if (x.getName().equals(name) ) {
 				
 				return x.getId();
 			}
 			
 		}
 		
		return -1;
 	}
 
 	/**
 	 * 
 	 * @param id - container (album id)
 	 * @return the image container
 	 */
 	public PaImageContainer getPhotoContainer(int id)
 	{
 		return  PaUtils.get().getMainContainer().getContainer(id);
 	}
 	
 	/**
 	 * Creates the standard folder for the album
 	 * @param albom - album to create a standard folder for it
 	 * @return true if only the new creation is successful
 	 */
 	public boolean createAlbumStandardFolder(PaAlbum albom)
 	{
 	    File dir=new File(albom.getFullStandardPath());
 	    
	    if(! dir.exists()){
	    	
	        return dir.mkdirs();	
	        
	    }
	    
	    return false;

 	}
 	
 	/**
 	 * Creates the standard folder for the album with id
 	 * @param id - id of the album to create a standard folder for it
 	 * @return true if the operation is successful
 	 */
 	public boolean createAlbumStandardFolder(int id)
 	{
 		PaAlbum al = getAlbum(id);
 		
 		if(al == null) return false;
 		
 		return createAlbumStandardFolder(al);
 	}
 	
 	/**
 	 * 
 	 * @param alb  album object to add
 	 * @return true if the operation successful
 	 */
 	public boolean add_album (PaAlbum alb)
 	{
 		 if (addAlbum (alb) != -1 ) return true;
 		 
 		 return false;
 		
 	}
 	 
 	/**
 	 * Adds new album to container, makes all folders and xml files for album 
 	 * @param al - new album to add
 	 * @return new id in the case of success, or -1 in the case of fail
 	 */
 	public int addAlbum (PaAlbum al)
 	{
		int Id = PaAlbum.getNextIdAndIterate();
		
 		al.setId(Id);
 	
 		//validation - change id to the moment then validation is ok; this intensive
 		//check will prevent the id duplication
 		int counterName = 1;
 		
 		int counterFolder = 1;
 		
 		ValidationControl flagControl;
 		
 		String startName = al.getName();
 		
 		String startFolderName = al.getFolderName();
 		
 		while( (flagControl = validateNewData(al)) != ValidationControl.DATA_OK) {
 			
 			switch(flagControl) {
 			
 				case WRONG_XML_PATH :
 					
	 			case WRONG_ID : {
	 				
	 				Id = PaAlbum.getNextIdAndIterate();
	 				
	 	 			al.setId(Id);
	 	 			
	 	 			break;
	 			}
	 			case WRONG_NAME : {
	 				
	 	 			al.setName(startName + "+" + counterName);
	 	 			
	 	 			++counterName;
	 	 			
	 	 			break;
	 			}
	 			case WRONG_EXIST_STAND_OPATH: 
	 				
	 			case WRONG_PATH : {
	 				
	 				al.setFolderName(startFolderName + "+" + counterFolder);
	 				
	 	 			++counterFolder;
	 	 			
	 	 			break;
	 			}
	 			
	 			default: { 
	 				break; 
	 			}
 			
 			};
 		}
 		
 		if ( ! createAlbumStandardFolder(al) ) {
 			
 			writeLog(getMessagesStrs("impossibleToCreateSavingFolderMessage") + " : "
						+ al.getFullStandardPath()+ NEXT_ROW, null, true, true, false);

 			return -1;
 		}
 		
 
 		m_albums.add(al);

 		PaUtils.get().getMainContainer().add_photoContainer(new PaImageContainer(Id));
 		
 		createAlbumFileXML(al);
 	 		
 		return Id;
 	}
 	
 	/**
 	 * Creates the XML file for savin album information on the disk
 	 * @param al - album class to save in XML
 	 * @return true if everything is ok
 	 */
 	public boolean createAlbumFileXML (PaAlbum al) {

 		int Id = al.getId();
 		
 		PaImageContainer _ppc = new PaImageContainer(Id);

 		try {
 			
			_ppc.saveImages(Id);
			
		} catch (FileNotFoundException e) {
						
			writeLog("FileNotFoundException : " + NEXT_ROW, e, true, false, true);
		
		} catch (XMLStreamException e) {
			
			writeLog("XMLStreamException : " + NEXT_ROW, e, true, false, true);
		}
 		
 		return true;
 	}
 	/**
 	 * Checks the all possible duplication of new album al and albums in this container
 	 * @param al - new album to insert
 	 * @return
 	 */
	private ValidationControl validateNewData(PaAlbum al) {
		
		Iterator<PaAlbum> iter = m_albums.iterator();
		
 		PaAlbum x;
 		
 		//check the existence of such full standard path for another album and id duplication
		while (iter.hasNext ()) {
			
 			x = iter.next();
 			
 			if (x.getName().equals(al.getName()) ){
 				
 				writeLog("Add album opeation: the name already exists for new id = " 
 						+ x.getId()+ NEXT_ROW, null, true, false, true);
 				
 				return ValidationControl.WRONG_NAME;
 			}	
 			if(x.getFullStandardPath().equals(al.getFullStandardPath()) ){
 				
	 				writeLog("Add album opeation: the standard folder already exists for new id = " 
	 						+ x.getId()+ NEXT_ROW, null, true, false, true);
	 				
	 				return ValidationControl.WRONG_PATH;
 			}
 			if(x.getId() == al.getId()) {
 				
 				writeLog("Add album opeation: the such id already exists  = " 
 						+ x.getId()+ NEXT_ROW, null, true, false, true);
 				
 				return ValidationControl.WRONG_ID;
 			}
 			
 		}
		
		//check physical existence of xml file for album
		String str = PaUtils.get().getAlbumXMLFilePath(al.getId());
		
		if(Files.exists(Paths.get(str))) { 
			
			writeLog("Add album opeation: the xml file already exists for new id = " 
						+ al.getId()+ NEXT_ROW, null, true, false, true);
	
			return ValidationControl.WRONG_XML_PATH;
		}
		//check physical existence of a standard folder
		if(Files.exists(Paths.get(al.getFullStandardPath()))) { 
			
			writeLog("Add album opeation: the standard folder already exists for new id = " 
						+ al.getId()+ NEXT_ROW, null, true, false, true);
	
			return ValidationControl.WRONG_EXIST_STAND_OPATH;
		}
		return ValidationControl.DATA_OK;
	}
		

 	/**
 	 *  Edits the album object with id = al.id; all information will be copied form al
 	 * @param al - album object which is created with new, has the id which exists and new information
 	 * @return true if the operation is succeed
 	 */
  	public boolean editAlbum (PaAlbum al) {

  		Iterator<PaAlbum> iter1 = m_albums.iterator();
  		
  		PaAlbum y = null;
  		
  		while (iter1.hasNext()) {
  			
  			y = iter1.next();  	
  			
  			if (y.getId() == al.getId()) {
  				
  				y.setName(al.getName());
  				
  				y.setComent(al.getComment());
  				
  				y.setPath(al.getStandardPath());
  				
  				y.setDate(al.getDate());
  				
  				y.setFolderName(al.getFolderName());
  				
  				//the parent was changed - then we should reparent childs if it is needed
  				if(al.getParentId() != y.getParentId()) {
  					
  					reparentChildAlbums(y.getId(), al.getParentId());
  				}
  				
  				y.setParentId(al.getParentId());
  				
				PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT) );
				
				PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(event);
				
 				return true;
 			}
  		}
		return false; 		
  	}
 	
	/**
	 * <p>Deletes the album with id, deletes all boost icons also.Doesn't touch the images on the disk at all.</p>
	 * @param id - id of the album which has to be deleted
	 * @return true if the operation is successful
	 */
	public boolean removeAlbum(int id) 
	{
		boolean flag = remove(id);
		
		if(flag) {
		
			//refresh event
			PaEventDispatcher.get().fireCustomEvent(  new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT) );
			
			PaEventDispatcher.get().fireCustomEvent(  new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );
			
			PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
			
			PaEventDispatcher.get().fireCustomEvent(event);
		}
		
		return flag;
	}
	
	
	/**
	 * <p>Deletes the album with id, deletes all boost icons also.
	 * Doesn't touch the images on the disk at all.</p>
	 * @param id - id of the album which has to be deleted
	 * @return true if the operation is successful
	 */
	public boolean remove(int id) 
	{
		PaAlbum al = getAlbum(id);
		
		if(al == null) {
			
			writeLog(getMessagesStrs("noAlbomWithId")+ id, null, true, true, true);
			
			return false;
		}
		
		int parentId = al.getParentId();
			
		PaUtils.get().getMainContainer().removeContainer(id);
		
		remove(al);
		
		changeParentIds(id, parentId);
		
		return true;
	}


	public int size() {
		
		return m_albums.size();
	}
	
	public boolean isEmpty() {
		
		return m_albums.isEmpty();
	}
	

	public  void saveAllAlbums (PaEvent event)  {
		
		if ( event.getEventType() != PaEventDispatcher.SAVE_EVENT ) { return; }
		
		try {		
			
//			setConfigAlboms(PaUtils.getInstance().getAlbomsXMLPath().concat(PaUtils.getInstance().getAlbomsXMLName()));
			saveAlbums();
	
		} catch (FileNotFoundException e) {
	
			writeLog("FileNotFoundException : " + NEXT_ROW, e, true, false, true);
			
		} catch (XMLStreamException e) {
	
			writeLog("XMLStreamException : " + NEXT_ROW, e, true, false, true);
		}
	}

	
	/**
	 * Saves all albums info in xml file
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public  void saveAlbums () throws FileNotFoundException, XMLStreamException {
		
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		
		// "UTF-8" parameter is important here
		FileOutputStream fSt = new FileOutputStream(PaUtils.get().getAlbumsXMLFullName());
		
		XMLStreamWriter writer = factory.createXMLStreamWriter(fSt, "UTF-8");
		try {
	
			writer.writeStartDocument("UTF-8", "1.0");
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeStartElement(PaSettings.ALBUM_ELEMENT);
	
			int _value = PaAlbum.getNextId(); 
		
			String value = Integer.toString(_value);
			
			writer.writeAttribute(PaSettings.ID_LAST_ATTR, value);
			
			int _nextId_photos = PaMainConainer.get_nextId();
			
			String nextId_photos = Integer.toString(_nextId_photos);
			
			writer.writeAttribute(PaSettings.NEXT_ID_ATTR, nextId_photos);
	
			for(PaAlbum x : m_albums ) {
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(ALB_ELEMENT);
				
				writer.writeAttribute(ALB_NAME_ATTR, x.getName());
				
				writer.writeAttribute(ALB_ID_ATTR, Integer.toString(x.getId()));	
				
				writer.writeAttribute(ALB_PARENT_ID_ATTR, Integer.toString(x.getParentId()));	
				
				writer.writeAttribute(ALB_DATE_ATTR, dateToString(x.getDate(),DATE_FORMAT));
				
				writer.writeAttribute(ALB_PATH_ATTR, x.getStandardPath());
				
				writer.writeAttribute(ALB_COMMENT_ATTR, x.getComment());
				
				writer.writeAttribute(ALB_FOLDER_ATTR, x.getFolderName());
				
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
					
				} catch (IOException e) {
					
				}
			}
		}
	}
	

	/**
	 * Loads albums from special xml file
	 * @param configFile - xml file with data about albums
	 */
	public void  loadAlbums(String configFile) {
		

		Integer ALBOM_id;
		
		Integer parent_id;	
		
		String ALBOM_name = "";	
		
		String ALBOM_path = "";
		
		String ALBOM_coment = "";
		
		String ALBOM_folder = "";
		
		Date ALBOM_date = null;
		
		InputStream in = null;
								
		try {		
		
			in = new BufferedInputStream(new FileInputStream(configFile));	
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
			
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT) {
					
					if (reader.getLocalName().equals(PaSettings.ALBUM_ELEMENT)) {
						
						PaAlbum.setInitialNextId(Integer.parseInt(reader.getAttributeValue(null, PaSettings.ID_LAST_ATTR)));
						
						PaMainConainer.set_load_nextId(Integer.parseInt(reader.getAttributeValue(null, PaSettings.NEXT_ID_ATTR)));
					}
					if (reader.getLocalName().equals(ALB_ELEMENT)) {
						
						ALBOM_id = Integer.parseInt(reader.getAttributeValue(null, ALB_ID_ATTR));
						
						try {
							parent_id = Integer.parseInt(reader.getAttributeValue(null, ALB_PARENT_ID_ATTR));
						}
						catch(NumberFormatException e) {
							
							parent_id = PaUtils.ALBUM_TOP_PARENT_ID;
						}
						//if(parent_id == null) parent_id = PaUtils.ALBUM_TOP_PARENT_ID;
						
						ALBOM_name = reader.getAttributeValue(null, ALB_NAME_ATTR);
												
						ALBOM_date = stringToDate(reader.getAttributeValue(null, ALB_DATE_ATTR),DATE_FORMAT);
						
						if ( ALBOM_date == null ) { ALBOM_date = new Date(); }
											
						ALBOM_path = reader.getAttributeValue(null, ALB_PATH_ATTR);
						
						ALBOM_coment = reader.getAttributeValue(null, ALB_COMMENT_ATTR);
						
						ALBOM_folder = reader.getAttributeValue(null, ALB_FOLDER_ATTR);
						
						PaAlbum al = new PaAlbum(ALBOM_name, ALBOM_coment, ALBOM_path, ALBOM_date,  ALBOM_folder, ALBOM_id );
						
						al.setParentId(parent_id);
						
						m_albums.add(al);
						
					}
					
							}
			}	

			reader.close();
			
			in.close();
			
		} catch (FileNotFoundException e) {
			
			writeLog("FileNotFoundException : " + configFile + NEXT_ROW, e, true, false, true);
			
		} catch (XMLStreamException e) {
			
			writeLog("XMLStreamException : " + configFile + NEXT_ROW, e, true, false, true);
		}
		catch (IOException  e) {
			
			writeLog("IOException  : " + configFile + NEXT_ROW, e, true, false, true);
		}
		finally {
			
			if(in != null)
				
				try {
					
					in.close();
					
				} catch (IOException e) {}
			}
	}

	public boolean addAll(Collection<? extends PaAlbum> col) {
		
		return m_albums.addAll(col);
	}

	public boolean removeAll(Collection<?> col) {
		
		return m_albums.removeAll(col);
	}

	public Iterator<PaAlbum> iterator() {
		
		return m_albums.iterator();
	}
	
	
	public boolean albumNameUniqueValidator(String name,int id) {
		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if ( x.getName().equals(name) && id != x.getId() ) {
 				
 				return false;
 			}
 		}
		
		return true;
	}
	
	public boolean albumPathUniqueValidator( String pathName, int id) {
		
		Iterator<PaAlbum> iter = m_albums.iterator();
		
 		PaAlbum x;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if ( x.getFullStandardPath().equals(pathName) && id != x.getId() ) {
 				
 				return false;
 			}
 		}
		return true;
	}
	
	
	/**
	 * <p>Finds all child albums for album with id</p>
	 * @param listIds - list of ids of album which are for merging
	 * @param resultId - id of target album
	 * @param copyFlag - true if all merged images must be save to the standard path of target album 
	 */
	public void mergeAlbums(ArrayList<Integer> listIds, Integer resultId, boolean copyFlag)
	{
		
 		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
				getMessagesStrs("mergeAlbomsLongTaskCaption"),
				getMessagesStrs("mergeImagesOperationNote"), 0, 100);
		
		progressMonitor.setMillisToDecideToPopup(0);
		
		progressMonitor.setMillisToPopup(0);
		
		PaUtils.get().setCursor(Cursor.DEFAULT_CURSOR,Cursor.WAIT_CURSOR);
				
		PaMergeLongTask ts = new PaMergeLongTask(progressMonitor,
				listIds, resultId, copyFlag);
		
		ts.execute();
		
	}
	
	/**
	 * <p>Finds all child albums for albom with id</p>
	 * @param id - parent's id
	 * @return list of all album's ids which are the childs of parent album
	 */
	public ArrayList<Integer> findChildsForParentId(int id)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if ( x.getParentId() == id ) {	
 				
 				list.add(x.getId());
 			}
 		}
 		
		return list;		
	}
	
	/**
	 * <p>Counts all child albums for album with id</p>
	 * @param id - parent's album id
	 * @return amount of child's albums for album with id
	 */
	public int getChildsCount(int id)
	{
		int count = 0;
		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if ( x.getParentId() == id ) {	
 				
 				++count;
 			}
 		}
 		
		return count;	
	}
	
	/**
	 * <p>Returns array of ids (only first level of parent-child)</p>
	 * @param id - parent's album id
	 * @return array of childs albums ids
	 */
	public int[] getChildAlbumsIds(int id)
	{
		int count = getChildsCount(id);
		
		int[] a = new int[count];
		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		int i=0;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();	
 			
 			if ( x.getParentId() == id ) {		
 				
 				a[i] = x.getId();
 				
 				++i;
 			}		
 		}
 		
		return a;	
	}
	/**
	 * Check the relation parent - child; the relation is checked recursively for whole deep of the tree
	 * @param albumId - id of an album to find children in
	 * @param id - id of an album to check into children's list
	 * @return - true if the album with is is a child of an album with albumId
	 */
	public boolean isChild(int albumId, int id) {
		
		 ArrayList<Integer> childs = getAllChildAlbumsIds(albumId);
		 
		 for(Integer id1 : childs) {
			 
			 if(id1.intValue() == id) { return true; }
		 }
		 return false;
	}
	
	/**
	 * <p>Returns full array of ids (all levels)</p>
	 * @param id - parent's album id
	 * @return array of childs albums ids
	 */
	public ArrayList<Integer> getAllChildAlbumsIds(int id)
	{
		ArrayList<Integer> a = new ArrayList<Integer>();
		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 
 		while (iter.hasNext ()) {
 			
 			x = iter.next();	
 			
 			if ( x.getParentId() == id ) {	
 				
 				int idA = x.getId();
 				
 				a.add(idA);
 				
				a.addAll(getAllChildAlbumsIds(idA));				
 			}		
 		}
		return a;	
	}
	/**
	 * When we change the parent of an album to other album which is a child of the first one,
	 * we must re-parent the first level of children in order to avoid problems
	 * Only the first level of children is under operation. After this operation the album with
	 * albumId has no children and can be easily inserted as a child in any place.
	 * If there is a situation when we insert in other branch, then there is no need to do re-parent operation.
	 * @param albumId - id of an album which is going to change parent, but still in the old state
	 * @param newParentId - new parent is for the album with albumId
	 * @return - true if the children have been re-parented
	 */
	public boolean reparentChildAlbums(int albumId, int newParentId) {
		
		if(isChild(albumId, newParentId)) {//is newParentId a child of albumId?
			
			int oldParentId = getAlbum(albumId).getParentId();
			
			changeParentIds(albumId, oldParentId);
			
			return true;
		}
		return false;
		
	}
	
	
	/**
	 * <p>Returns array of pathes of child albums</p>
	 * @param id - parent's album id
	 * @return array of child albums pathes
	 */
	public String[] getChildAlbumsPathes(int id)
	{
		int count = getChildsCount(id);
		
		String[] a = new String[count];
		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		int i=0;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();	
 			
 			if ( x.getParentId() == id ) {	
 				
 				a[i] = x.getFullStandardPath();
 				
 				++i;
 			}		
 		}
		return a;	
	}
	/**
	 * <p>Change parent id for all albums where the parent id == sourceId</p>
	 * @param sourceId - id which will be found into container and changed to targetId 
	 */
	public void changeParentIds(int sourceId, int targetId)
	{
		int count = 0;
		
 		Iterator<PaAlbum> iter = m_albums.iterator();
 		
 		PaAlbum x;
 		
 		while (iter.hasNext ()) {
 			
 			x = iter.next();
 			
 			if ( x.getParentId() == sourceId ) {	
 				
 				x.setParentId(targetId);
 				
 				++count;
 			}
 		}	
 		writeLog(getMessagesStrs("numberOfReparentedAlboms") + " " + count, null, true, true, true);
	}
	
	/**
	 * <p>Class PaMergeLongTask uses SwingWorker to start a long task into background. 
	 * Here is the operation of albums merging</p>
	 */
private class PaMergeLongTask extends SwingWorker<Void, Void> implements PropertyChangeListener 
 {

		 ProgressMonitor m_progressMonitor;
		 
		 ArrayList<Integer> m_listIds;
		 
		 boolean  m_copyFlag;
		 
		 Integer m_resultAlbumId;
		 
		 int m_counter;
		 
		 int m_currentCursorType;
		 
		 /**
		 * <p>Constructor has parameters to start  operation properly</p>
		 * @param progressMonitor - progress monitor
		 * @param listIds - list of albums ids which should be merged
		 * @param copyFlag - determines to save files into standard folder or not
		 * @param resultId - target albom's id
		 */
		 public PaMergeLongTask(ProgressMonitor progressMonitor, 
				 ArrayList<Integer> listIds, Integer resultId, boolean copyFlag) 
		 {

			 m_progressMonitor = progressMonitor;
			 
			 m_listIds = listIds;
			 
			 m_copyFlag = copyFlag;
			 
			 m_resultAlbumId = resultId;
			 
			 addPropertyChangeListener(this);	
		 }
		 
        @Override
        public Void doInBackground() 
        {	
        	if(m_listIds == null || m_listIds.isEmpty()) return null;
        	
    		double step = 100.0/(m_listIds.size()-1);//-1 because minus the target album
    		
    		double h = 0;
    		
    		m_counter =0;
    		
    		Integer albomId = null;
    		
    		m_currentCursorType = PaUtils.get().getCurrentCursor();
    		
    		PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
    		
    		Iterator<Integer> itr = m_listIds.iterator();
    		try {
	    		while (itr.hasNext()) {
	    			
	    			 albomId = itr.next();
	    			 
	    			 if ( ! m_resultAlbumId.equals(albomId) ) {
	    				 
	    					String newPath = getAlbum(m_resultAlbumId).getFullStandardPath();
	    					
	    					PaImageContainer res  = PaUtils.get().getMainContainer().getContainer(m_resultAlbumId);
	    					
	    					PaImageContainer al= PaUtils.get().getMainContainer().getContainer(albomId);
	    					
	    					ArrayList<PaImage> photoCont = al.getList();
	    					
	    					if(photoCont.isEmpty()) continue;
	    					
	    					Iterator<PaImage> it = photoCont.iterator();
	    					
	    					double step1 = step/photoCont.size();	
	    					
	    			 		while (it.hasNext ()) {	
	    			 			
	    			 			PaImage oldIm = it.next();
	    			 			
	    			 			PaImage newPhoto = new PaImage(oldIm);
	    			 			
	    			
	    			 			if(h >= 100) { h = 99.0; }
	    			 			
	    			 			setProgress((int)h);
	    			 			
	    			 			h += step1;
	    			 			
	    			 			if(this.isCancelled()) { break; }
	    			 			
	    			 			if ( m_copyFlag || !oldIm.isLink()) { //images will be copied physically in the standard folder
	    			 				
	    			 				try {
	    			 					
	    								newPhoto.changePath(newPath);
	    								
	    								newPhoto.setLink(false);
	    								
	    								res.addPaPhoto(newPhoto);
	    								
			    			 			m_counter++;
	    							}
	    							catch (IOException | SecurityException exp) {
	    								
	    						  		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(), //"Copy operation failed "
	    						  				getMessagesStrs("errorWhileCopyOperation") + NEXT_ROW + 
	    						  		         getMessagesStrs("pathPartOfMessage")+" " +newPath +
	    						  		         " "+ getMessagesStrs("filePartOfMessage")+" "+newPhoto.getName(),
	    					    			    getMessagesStrs("messageErrorCaption"),
	    					    			    JOptionPane.ERROR_MESSAGE);
	    							}	 				
	    			 			}
	    			 			else {
	    			 				
		    			 			res.addPaPhoto(newPhoto);
		    			 			
		    			 			m_counter++;
	    			 			}
	    			 		}
	    			 }
	    		}			 
    		}
    		finally {
    			
	    		setProgress(100); 	
    		}
			return null;
        }
        	
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() 
        {
    		if(PaUtils.get().getMainContainer().isCurrent(m_resultAlbumId)){
    			
    			PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
    		}
    		
    		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
    		
    		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);   
    		
    		writeLog(getMessagesStrs("numberOfMergedImages")+" "+m_counter, null, true, true, false);
        }
        
      	/* (non-Javadoc)
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) 
		{
			 if ("progress" == evt.getPropertyName()) {
				 
			 	int p = (Integer) evt.getNewValue();
			 	
                m_progressMonitor.setProgress(p);
                
                String message =
                        String.format(getMessagesStrs("mergeImagesOperationNote")+" %d%% \n", p);
                
                m_progressMonitor.setNote(message);
                
                if (m_progressMonitor.isCanceled() || this.isDone()) {
                	
                    Toolkit.getDefaultToolkit().beep();
                    
                    if (m_progressMonitor.isCanceled()) {
                    	
                        this.cancel(true);
                    } 
                }
            }			
		}
 }

/**
 * Checks the existence of a standard folder for the album with id
 * @param id - album's id
 * @return true if the standard folder is ok
 */
public boolean isStandardFolderExist(int id) {
	
	PaAlbum al = getAlbum(id);
	
	if(al != null) {
		
		return Files.exists(Paths.get(al.getFullStandardPath()));	
	}
	return false;
}
	 

	
}
