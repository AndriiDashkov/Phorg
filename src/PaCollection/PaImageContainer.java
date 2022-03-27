package PaCollection;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import PaAlgorithms.PaAlgoTransform;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventInt;
import PaGlobal.PaSortOrderEnum;
import PaGlobal.PaUtils;
import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.*;
import PaImage.PaImageFileFilter;
import PaImage.PaViewPhotosForm;
import PaLong.PaAddAlbumRecursiveTask;
import PaLong.PaAddImagesToAlbumTask;


/**
 * Main container to hold and control image objects PaImage
 * @author Andrey Dashkov
 *
 */
public class PaImageContainer implements Cloneable{
	/**
	 *  main container to hold images
	 */
	private ArrayList<PaImage> m_images; 
	/**
	 *  unique id of the container; this is also the id of the unique album
	 */
	private Integer m_id = -1;	// Id - of album 
		
	private String _configPhoto;
	
	private PaFilterInfo m_filter = null; //to reset the filter in container set it to null
	
	
	private final static String ATR_NAME = "name";
	
	private final static String ATR_ID = "id";
	
	private final static String ATR_SORT_ID = "sortid";
	
	private final static String ATR_PATH = "path";
	
	private final static String ATR_WIDTH = "width";
	
	private final static String ATR_HEIGHT = "height";
	
	private final static String ATR_DATE = "date";
	
	private final static String ATR_BOOKMARKED = "bookmarked";
	
	private final static String ATR_PRINTED = "printed";
	
	private final static String ATR_COMMENTS  = "comments";
	
	private final static String ATR_KEY  = "key";
	
	private final static String ELM_SUBJECT  = "theme";
	
	private final static String ELM_PHOTO  = "photo";
	
	private final static String ELM_PHOTOALBOMS = "PhAlbmS";
	
	private final static String ATR_IDALBOM  = "id_Albm";
	
	private final static String ATR_LINK = "link";
	
	private final static String VISIBLE_IN_SLIDER = "slVis";
	
	
	public PaImageContainer () {
		
		m_images = new ArrayList<PaImage>();
	}
	
	public PaImageContainer(ArrayList<PaImage> photos) {
		
		m_images = photos;
	}
	
	public PaImageContainer(int id_alboms) {
		
		m_images = new ArrayList<PaImage>();
		
		m_id = id_alboms;
	}
	
	public boolean isEmpty() 
	{
		return m_images.isEmpty();		
	}
	
	@SuppressWarnings("unchecked")
	public PaImageContainer clone() throws CloneNotSupportedException {
		
		PaImageContainer cloneContainer = (PaImageContainer) super.clone(); 
		
		cloneContainer.m_images = (ArrayList<PaImage>) m_images.clone();
		
		return cloneContainer;
	}
	/**
	 * 
	 * @return the link to the inner container 
	 */
	public ArrayList<PaImage> getList() {
		
		return m_images;
	}

	public int getId() {
		
		return m_id;
	}
	
	public int getSize() {
		
		return  m_images.size();
	}
	/**
	 * 
	 * @return  the album object for this image container
	 */
	public PaAlbum getAlbum() {
		
		return PaUtils.get().getAlbumContainer().getAlbum( getId() );
	}
	
	
	public String getConfigPhoto() {
		
		return _configPhoto;
	}
	public void setConfigAlbom(String configPhoto) {
		
		_configPhoto = configPhoto;
	}

	/**
	 * 
	 * @param pp - image object to remove
	 * @return true in any case
	 */
	public boolean removeImage(PaImage pp) {
		
			m_images.remove(pp);
			
			return true; 
	}
	
	/**
	 * Adds new image object. Usualy it is used without refresh of PaViewPanel
	 * @param pp - new image object to add
	 * @return
	 */
	public boolean addPhoto (PaImage pp) {
			
		if (! addImage(pp,false) ) return false;
		
		createSpecialLoadImage (pp);
		
		return true;
	}

	/**
	 * Adds image to the container (base addition function)
	 * @param pp image to add
	 * @param duplicateAllowed - true if the duplication is allowed - used for filtration only
	 * @return
	 */
	public boolean addImage (PaImage pp, boolean duplicateAllowed) {
		
		Iterator<PaImage> iter = m_images.iterator();
		
		PaImage x;
		
		if(!duplicateAllowed) {
			
			while (iter.hasNext ()) {
				
				x = iter.next();
				
				if (x.getName().equals(pp.getName()) || x.getFullPath().equals(pp.getFullPath())) {
					
					return false;
				}
			}
		}
		
		m_images.add(pp);	
		
		return true;
	}

	/**
	 * 
	 * @param pp - new image object to add in the container
	 * @return
	 */
	public boolean add_Image (PaImage pp) {
		

		if ( ! addPhoto(pp) ) { return false; }
				
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
		
		PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
		
		PaEventDispatcher.get().fireCustomEvent(event);
		
		return true;
	}
	
	/**
	 * Adds image to the container
	 * @param pp - new image object to add
	 * @param copyFlag - if true, then the coping in standard folder takes place
	 * @return
	 */
	public boolean add_Image (PaImage pp, boolean copyFlag) {
		
		if ( copyFlag ) {		
			
			pp.setLink(false);
			
			return addCopyImage (pp, this.getAlbum().getFullStandardPath());							
		}
		else {
			
			pp.setLink(true);
			
			return addPhoto(pp);
		}	
	}
	
	/**
	 * Adds new image object with coping of the file in new place
	 * @param pp - new image object to add
	 * @param newPath - new path for image file
	 * @return
	 */
	public boolean addCopyImage (PaImage pp, String newPath) 
	{
			
			String oldPath =  pp.getFullPath();

			try {
			 
				pp.changePath(newPath);
			
			}
			catch(IOException | SecurityException e ) {
				
				writeLog("IOException | SecurityException : " + NEXT_ROW, e, true, false, true);
				
		  		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(), //"Сбой операции копирования: "
		  				getMessagesStrs("errorWhileCopyOperation") + NEXT_ROW + 
		  		         getMessagesStrs("pathPartOfMessage")+newPath +getMessagesStrs("filePartOfMessage")+pp.getName(),
	    			    getMessagesStrs("messageErrorCaption"),
	    			    JOptionPane.ERROR_MESSAGE);
		  		
		  		pp.setLink(true);
		  		
		  		pp.setFullPath(oldPath);
		  	
		  		
			}
		
		return addPhoto(pp);
	}

	/**
	 * <p>Adds pp object to the container, creates boost image</p>
	 * @param pp - image object to insert 
	 */
	public void addPaPhoto (PaImage pp) 
	{			
		//unique name control
		pp.setName(getUniquePhotoName(pp.getName()) );
		
		m_images.add(pp);

		createSpecialLoadImage (pp);
	}
	

	/**
	 * <p>Deletes all boost images for this container/album.</p>
	 * @param flag - true if you want delete not only images, but parent folder also 
	 */
	public void deleteSpecialLoadImages(boolean flag) 
	{
		File f = new File(PaUtils.get().getPathToBoostIcons(this));
		
		if(!f.exists()) return;
		
		if(flag) {
			
			PaUtils.get().deleteNotEmptyDir(f);
			
		}
		else {		
			
			PaUtils.get().deleteDirChilds(f);
		}
	}
	
	
	/**
	 * <p>Creates a boost image for the image pp.</p>
	 * @param pp - image object 
	 */
	public void createSpecialLoadImage(PaImage pp) {
		
		
		if ( PaUtils.get().getSettings().getCreateImagesCopiesFlag() ) {
		
		 int width= PaUtils.get().getSettings().getSpecIconsSize();
		 
		 int height = width;
		 
		 BufferedImage srcImg = null;
		
		 int currentCursorType = PaUtils.get().getCurrentCursor();
		 
		 PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
		 
		 BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		 
		 try {
			 
				 File fl = new File(pp.getFullPath());
				 
				 if(PaUtils.get().isImageFile(fl)) {
					 
					 try {
						 
						 srcImg = ImageIO.read(fl);
					 }
					 catch (IIOException | IllegalArgumentException eI1) {
						 
						 srcImg = null;
					 }
	
	
					 if(srcImg == null) {
						 
						 srcImg = PaUtils.get().getNonValidImage();
						 
						 writeLog(getMessagesStrs("cantReadTheImageFile") + fl.getAbsolutePath() + NEXT_ROW,
									null, true, true, false);
					 }
						 
					 float aspectRatio =  (float) srcImg.getWidth() / (float) srcImg.getHeight();
					 
					 if ( aspectRatio > 1.0 ) {
						 
						 height = (int) ( ( (float) width ) / aspectRatio );
					 }
					 else {
						 
						 width = (int) ( ( (float) height ) * aspectRatio );
					 }
					
					img = (BufferedImage) PaAlgoTransform.getScaledImage (srcImg, width, height);
					
				 }
				 else if(PaUtils.get().isVideoFile(fl.getName())) { 
					 
					  //believe that this is video file
					 img = PaUtils.get().specialImageForVideo();
				 }
				 
				 String fileName = new String( new Integer(pp.getId()).toString());
					
				 File outFile = new File( concatPathName( PaUtils.get().getPathToBoostIcons(this), fileName+".jpeg") );
						
				 ImageIO.write(img, "jpeg", outFile);
		    
			 } catch (IOException | IllegalArgumentException e) {
				 
				 writeLog(getMessagesStrs("cantCreateBoostImageFile") + NEXT_ROW, null, true, true, false);
			 }
			 finally {
				 
				 PaUtils.get().resetCursor(currentCursorType);
			 }
		 
		}
		
	}
	
	/**
	 * All images from the list are inserted as links
	 * @param list - list of images to insert in this container
	 */
	public void copyAllImagesAsLinks(ArrayList<PaImage> list) 
	{
		
		for(PaImage pp : list) {
			
			PaImage pNew = new PaImage(pp);
			
			addPaPhoto (pNew);
			
		}
		
	}
	
	/**
	 * All images from the list are inserted with coping into standard folder
	 * @param list - list of images to insert in this container
	 */
	public void copyImagesInStandFolder(ArrayList<PaImage> list) 
	{
		
		for(PaImage pp : list) {
			
			PaImage pNew = new PaImage(pp);
			
			pNew.setLink(false);
			
			addCopyImage (pNew, getAlbum().getFullStandardPath()); 

		}	
	}
	
	/**
	 * Copies images with differentiation: if the image is in standard folder in it's source album
	 * then the image is copied into standard folder of this container; so is for linked images
	 * @param list - mixed list where the images can be links and can be in standard folder
	 */
	public void copyImagesWithControl(ArrayList<PaImage> list) 
	{
		ArrayList<PaImage> listLinks = new ArrayList<PaImage>();
		
		ArrayList<PaImage> listSt = new ArrayList<PaImage>();
		
		for(PaImage pp : list) {
			
			if(pp.isLink()) {
				
				listLinks.add(pp);
			}
			else{
				
				listSt.add(pp);
			}
		}
		
		copyImagesInStandFolder(listSt); 
		
		copyAllImagesAsLinks(listLinks);
	}
	

	/**
	 * Deletes the special boost image which is used for acceleration of loading process
	 * @param Id - id of the image
	 */
	public void deleteSpecialLoadImage (int Id) {
		
		
		String fileName = new String( new Integer(Id).toString());
		
		File outFile = new File( concatPathName( PaUtils.get().getPathToBoostIcons(this), fileName+".jpeg") );
		
		
		if ( outFile.exists() ) {
			
			outFile.delete();
		}

	}
	
	/**
	 * Creates new versions of boost images for all images in the album
	 */
	public void refreshAllBoostIcons ( ) {

			Iterator<PaImage> it = m_images.iterator();
			
			while (it.hasNext()) {		
				
				PaUtils.get().getViewPanel().refreshBoostIcon(it.next().getId());
			}
	
			
	}
	
	/**
	 * Validates the duplication of the name and if such name exists, then generates new unique name
	 * @param nameToCheck - image name to check for existence
	 * @param id - id to check
	 * @return new name or old name if it is unique
	 */
	public String getUniquePhotoName(String nameToCheck, int id) {
		
		String name = nameToCheck;
		
		Iterator<PaImage> iter = m_images.iterator();
		
        PaImage p= null;
        
		while (iter.hasNext ()) {
			
		    p = iter.next();
		    
			if ( p.getName().equals(name) && id != p.getId() ) {
				
				name += NEW_FILE_EXT;
				
				name = getUniquePhotoName( new String(name), id );
				
				break;
				
			}
		}

		return name; 
	}
	/**
	 * Generates unique name in this container
	 * @param nameToCheck - image name to check (name in the album, not file name)
	 * @return unique new name or old name
	 */
	public String getUniquePhotoName(String nameToCheck ) {
		
		return getUniquePhotoName(nameToCheck, -1);
	}
	
	public boolean isUniquePhotoPath( String fullPath, int idN)  {
		
		Iterator<PaImage> iter = m_images.iterator();
		
        PaImage p = null;
        
		while (iter.hasNext ()) {
			
		    p = iter.next();
		    
			if ( isSamePathes(p.getFullPath(), fullPath ) && ( p.getId() != idN )  ) {
				
				return false;
				
			}
		}

		return true; 
	}
	


	
	/**
	 * <p>Loads group of images from the source folder and adds them to the this albom</p>
	 * @param sourcePath - source folder with images
	 * @param targetPath - target folder (if the user has chosen the option 'copy to other folder'); if it is empty
	 * then the other option will be chosen
	 * @param copyFlag - if images must be physically copied into standard or chosen folder
	 * @param isRecur - if the source folder should be scanned in recursive way to find image files
	 * @return true if the operation was successful
	 */
	public boolean loadImagesGroup (String sourcePath, String targetPath, boolean copyFlag,boolean isRecur) {
				
		File dir = new File(sourcePath);
		
		if(!dir.exists()) {
			
			writeLog("Add group of images - folder doesn't exist : "+ sourcePath, null, true, false, true);
			
			JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
    				getMessagesStrs("choosenFolderDoesNotExist"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			
			return false;
		}
		
		PaUtils.get().setCursor(Cursor.DEFAULT_CURSOR,Cursor.WAIT_CURSOR);
		
		int imagesAmount = PaUtils.get().countImageFiles2(dir,isRecur);
		
	    PaUtils.get().setMainLabel(getMessagesStrs("loadImagesOperationCaption")+" "+imagesAmount);
	    
		PaUtils.get().setCursor(Cursor.DEFAULT_CURSOR,Cursor.DEFAULT_CURSOR);
		
		
		if (imagesAmount == 0 ) {
			
	 		PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR); 
	 		
	 		PaUtils.get().setMainLabel("");
	 		
			writeLog("Add group of images - folder doesn't have images : "+ sourcePath, null, true, false, true);
	   		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
    				getMessagesStrs("choosenFolderHasNoItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
	  
			return false;
		}
		
		if(!targetPath.isEmpty()) {
			
			File dir1 = new File(targetPath);
			
			if( !dir1.exists()) {
				
				writeLog( getMessagesStrs("targetFolderDoesNotExist") + " : "+ targetPath, null, true, true, true);
			
				int n = JOptionPane.showConfirmDialog(
					    PaUtils.get().getMainWindow(),
					    getMessagesStrs("targetFolderDoesNotExist") + " : "+ targetPath + NEXT_ROW +
	    				getMessagesStrs("createTargetFolderQuestion"),
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.YES_NO_OPTION);
				
				if ( n != JOptionPane.YES_OPTION) { return false; }
				
				try {
					
					dir1.mkdirs();
				}
				catch(SecurityException ex) {
					
					writeLog(getMessagesStrs("cantCreateTargetFolder") + " : "+ targetPath, ex, true, true, true);
			   		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
		    				getMessagesStrs("cantCreatetargetFolder") + " : "+ targetPath,
		    			    getMessagesStrs("messageInfoCaption"),
		    			    JOptionPane.INFORMATION_MESSAGE);
			   		
					return false;
				}
			}
		}
		
		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
				getMessagesStrs("loadImagesOperationCaption")+" "+imagesAmount,
				getMessagesStrs("loadedImagesOperationNote"), 0, 100);
		
		progressMonitor.setMillisToDecideToPopup(0);
		
		progressMonitor.setMillisToPopup(0);
		
		if(isRecur) {
			
			PaAddAlbumRecursiveTask ts = new PaAddAlbumRecursiveTask(progressMonitor, sourcePath, 
					 m_id,copyFlag);
			
			ts.execute();
		} 
		else {		
			
			File[] files = dir.listFiles(new PaImageFileFilter());
			
			//add images operation is potentially long, we start it using the SwingWorker 
			PaAddImagesToAlbumTask ts = new PaAddImagesToAlbumTask(progressMonitor, sourcePath, 
					targetPath, files, copyFlag, m_id);
			
			ts.execute();
		}
		return true;
	}
	
	/**
	 * 
	 * @param id - id of image object
	 * @return the image object for id
	 */
	public PaImage getImage( int id) {
		
		Iterator<PaImage> it = m_images.iterator();
		
		PaImage y = null;
		
		while (it.hasNext ()) {
			
			y = it.next();
			
			if (y.getId() == id) {
				
				return y; 
			}
		}
		return null;
	}
	
	public PaImage getPhoto( String  name) {
		
		Iterator<PaImage> it = m_images.iterator();
		
		PaImage y = null;
		
		while (it.hasNext ()) {
			
			y = it.next();
			
			if (y.getName().equals(name) ) {
				
				return y; 
			}
		}
		
		return null;
	}
	
	/**
	 * <p>Edits image object which has the same id as pp</p>
	 * @param pp - image which contains the info  for edit operation
	 * @param idForEdit - id of image which must be edited
	 */
	public boolean editImage (PaImage pp, int idForEdit) 
	{
		PaImage y =  getImage(idForEdit);	
		
		if ( y != null ) {
		
			y.setName(pp.getName());
			
			y.setDate(pp.getDate());
			
			y.setFullPath(pp.getFullPath());
			
			y.setSubjectsList(pp.getSubjectsList());
			
			y.set_imageNull();
			
			y.setWidth(new ImageIcon(y.getFullPath()).getIconWidth());
			
			y.setHeight(new ImageIcon(y.getFullPath()).getIconHeight());
			
			y.setBookmarked(pp.isBookmarked());
			
			y.setPrinted(pp.isPrinted());
			
			y.setComments(pp.getComments());
			
			y.setVisibleInSlider(pp.isVisibleInSlider());
			
			return true;
		}
		else {	
			
			writeLog("Can't find photo object in the container for edit operation: id = "+ pp.getId(), null, true, false, true);
			
			return false;
		}
	}	
	
	/**
	 * Removes image from this container(album)
	 * @param id - id of an image
	 * @return true if the operation was succesful
	 */
	public boolean removePhoto(int id) 
	{
		PaImage x = null;
		
		Iterator<PaImage> itr = m_images.iterator();
		
		while (itr.hasNext()) {
			
			x = itr.next();
			
			if (x.getId() == id) {
				
				itr.remove();
				
				return true;
			}
		} 
		return false;
	}
	
	/**
	 * Removes images from the album
	 * @param viewPhotos - list of view forms with images to delete
	 * @param deleteFromDisk - true if the image file should be deleted from disk
	 * @return
	 */
	public int removeImagesFromForms (ArrayList<PaViewPhotosForm> viewPhotos, boolean deleteFromDisk) {
		
		if ( viewPhotos.size() == 0 ) return 0;

		ArrayList<PaImage> list = new ArrayList<PaImage>();
		
		for (PaViewPhotosForm viewPhotoForm : viewPhotos) {
			
			list.add(viewPhotoForm.getImage());
		}
		
		return removeImages (list, deleteFromDisk);

	}
	
	/**
	 * Removes images from the album
	 * @param list - list of images to delete
	 * @param deleteFromDisk - true if the image file should be deleted from disk
	 * @return number od deleted images
	 */
	public int removeImages (ArrayList<PaImage> list, boolean deleteFromDisk) {
		
		if ( list.size() == 0 ) return 0;
		
		int counter =0;

		for (PaImage pp : list) {
			
			int id = pp.getId();
			
			removePhoto(id);
			
			counter++;
			
			//delete image
			if ( deleteFromDisk ) {
	
				PaUtils.get().deleteNotEmptyDir(  new File(pp.getFullPath()) );
				
			}
			//delete the boost icon
			deleteSpecialLoadImage (id);
		}

		PaEvent e = new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT);
		
		e.setEndMessage(getMessagesStrs("deletedImagesMessage")+" " + counter);
		
		PaEventDispatcher.get().fireCustomEvent(e);
		
		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
		
		return counter;
	}
	
	/**
	 * Deletes the images which are located into path
	 * @param path - path where the images should be deleted
	 * @param deleteFromDisk - true if the image file should be deleted from disk
	 * @return
	 */
	public int removeAllImagesForPath(String path, boolean deleteFromDisk) {
		
		ArrayList<PaImage> list = new ArrayList<PaImage>();
		
		PaImage x = null;
		
		Iterator<PaImage> it = m_images.iterator();
		
		while (it.hasNext()) {
			
			x=it.next();
			
			if ( isSamePathes(x.getFullPath(), path ) ) {
				
				list.add(x);
	
			}
		}
		
		return removeImages(list, deleteFromDisk);
	}
	
	/**
	 * Checks the existence of the image with id in this album
	 * @param id - album id for check
	 * @return true if an image with id exists in this album
	 */
	public boolean findId(int id) {
		
		PaImage x = null;
		
		Iterator<PaImage> it = m_images.iterator();
		
		while (it.hasNext()) {
			
			x=it.next();
			
			if ( x.getId() == id  )  {
				
				return true;
	
			}
		}
		
		return false;
	}
	/**
	 * @param id - id of the image
	 * return array index of an image with id
	 * 
	 */
	public int findIndex(int id) 
	{
		int sz = m_images.size();
		
		for(int i=0; i < sz; ++i) {
			
			if(m_images.get(i).getId() == id) return i;	
		}		
		
		
		return -1;
	}
	
	/**
	 * <p>Gets the current filter object for this container</p>
	 */	
	public PaFilterInfo getFilter() { return m_filter; }
	
	/**
	 * <p>Resets the current filter object for this container
	 * This means that all images will be visible after refresh
	 * Also disables the filter icon</p>
	 */

	public void resetFilter() { 
		
		m_filter = null; 
		
		PaUtils.get().getViewPanel().setFilterIconEnable(false);	
	}
	/**
	 * <p>Sets the current filter object for this container</p>
	 */
	public void setFilter(PaFilterInfo f) { 
		
		m_filter = f; 
	}
	/**
	 * <p>Filters current container and makes the images which are not suitable for
	 *  filter to be hidden</p>
	 */	
	public void applyCurrentFilter() {
		
		if(m_filter == null) { //filter reset, all images are visible
			
			Iterator<PaImage> itr = m_images.iterator();
			
			while (itr.hasNext()) {
				
				itr.next().setVisible(true);
			}
			
			PaUtils.get().getViewPanel().setFilterIconEnable(false);
			
			return;
		}

		Date date_from = m_filter.getDateFrom();
		
		Date date_to = m_filter.getDateTo();

		
		if ( date_from == null || date_to == null ) {
			
			writeLog( "Can't do filtering (applyCurrentFilter) for id =  " + 
			m_id + " because of invalid date", null, true, false, true);
		
			return;
		}
		
		PaImage x = null;
		
		Iterator<PaImage> itr = m_images.iterator();
		
		while (itr.hasNext()) {
			
			x=itr.next();
			
			x.setVisible(useFilterForImage(x, m_filter));
		}
		
		PaUtils.get().getViewPanel().setFilterIconEnable(true);
	}

	/**
	 * @param newContainer - new container for all filtered images
	 * @param fl - filter object with filtering criteria
	 * <p>Filters current container and add the copy of filtered images into new container
	 * Does not change the visibility of images in the current container</p>
	 * 
	 */	
	public void applyFilterToNewContainer(PaImageContainer newContainer, PaFilterInfo fl)
	{
		Date date_from = fl.getDateFrom();
		
		Date date_to = fl.getDateTo();
	
		if ( date_from == null || date_to == null ) {
			
			writeLog( "Can't do filtering (applyFilterToNewContainer) to other container for id =  " + 
			m_id + " because of invalid date", null, true, false, true);
			
			return;
		}
		PaImage x = null;
		
		//PaImageContainer _filterContainer = filterContainer;
		Iterator<PaImage> itr = m_images.iterator();
		
		while (itr.hasNext()) {
			
			x=itr.next();
						
			if ( useFilterForImage(x, fl) ) {	
				
				PaImage img = new PaImage(x);//this creates image with new id
				//remember the real previous id : the filter view has the edit operation,
				//we must remember the real id in the album in the case of edit image operation 
				//in filter view
				
				img.setAuxiliaryId(x.getId());
				
				img.setAuxiliaryAlbumId(m_id);//must have source album id in the filter view
				
				img.setLink(x.isLink()); //we need this to detect the previous status of image in source album
				
				newContainer.addImage(img,true); //duplication is possible
			}	
		}			
	}
	
	/**
	 * Check the properties of image x  for filter fl
	 * @param x - image to check
	 * @param fl - filter info to check
	 * @return true if x is suitable for filter fl
	 */
	private boolean useFilterForImage(PaImage x, PaFilterInfo fl) {
		
		ArrayList<PaSubject> subjectKeys = fl.getSubjectsList();
		
		Date date_from = fl.getDateFrom();
		
		Date date_to = fl.getDateTo();
		
		boolean ingnoreSubjects = fl.isSubjectsIgnored();
		
		boolean dateResult = fl.isDatesIgnored();
		
		if(!dateResult) {
			
			dateResult = ( x.getDate().getTime() >= date_from.getTime() ) 
					
				&& ( x.getDate().getTime() <= date_to.getTime() );
		}
		
		if ( !dateResult )  { return false; }
			
		boolean boolmarkResult = fl.isBookmarkedIgnored();
		
		if(!boolmarkResult) 
			
		if(fl.isBookmarkActiveSelected()) { boolmarkResult = x.isBookmarked();}
		
			else { boolmarkResult = !x.isBookmarked();}
		
		if(!boolmarkResult) { return false;}
				
		boolean printedResult = fl.isPrintedIgnored();
		
		if(!printedResult) 
		{	
			if(fl.isPrintedActiveSelected()) { 
				
				printedResult = x.isPrinted();
			}
			else { 
				printedResult = !x.isPrinted();
			}
		}
		
		if(!printedResult) { return false; }
		
		boolean linksResult = fl.isLinksIgnored();
		
		if(!linksResult) {
			
			if(fl.isLinksActiveSelected()) { 
				
				linksResult = x.isLink();
			}
			else { 
				
				linksResult = !x.isLink();
			}
		}
		
		if(!linksResult) { return false; }
			
		boolean kewordResult = false;
		
		if (ingnoreSubjects || subjectKeys == null ) { 
			
			kewordResult = true;
		}	
		else {					
			if (x.getSubjectsList().isEmpty()) {
				
				if(subjectKeys.isEmpty()) {
					
					kewordResult = true;
				}
				else {
					kewordResult = false;
				}
			}
			else {		
				
				if(subjectKeys.isEmpty()) {
					
					kewordResult = false;
				}
				else {
					for (PaSubject key : subjectKeys) {	
						
						if (x.findKey(key.getId()) != -1) { 
							
							kewordResult = true;
							
							break;
						}
					}	
				}
			}
		}	
		
		return kewordResult;
	}
	

	/**
	 * 
	 * @return the size of container
	 */
	public int size() {
		
		return m_images.size();
	}
	
	public void removeAllPhotos() {

		m_images.clear();

	}
	
	/**
	 * Refreshes selected container for view panel in the case when this container has the same id
	 */
	public void refreshSelectedContrainer() 
	{
		if (this.getId() == PaUtils.get().getMainContainer().getCurrentContainer().getId() ) {
			
			try {
				
				PaUtils.get().getMainContainer().setCurrentLoadedContainer(this);
			}
			catch ( CloneNotSupportedException exp ) {
				
			}			
		}	
	}

	/**
	 * <p>Deletes all images from the container</p>
	 * @return true if the operation is successful
	 */
	public boolean clearImages() 
	{
		removeAllPhotos();
		
		deleteSpecialLoadImages(false);
		
		//loaded albom control
		if(PaUtils.get().getMainContainer().isCurrent(getId())) {

			//if the album and this container is current, then we should refresh the view panel
			refreshSelectedContrainer();
			
			PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );
		}
	
		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
		
		return true;
	}
	
	/**
	 * 
	 * @return the iterator for images in this container
	 */
	public Iterator<PaImage> iterator() {
		
		return m_images.iterator();
	}	
	
	/**
	 * Parses and saves images data to xml file
	 * @param id_albom - id of saving albom
	 * @param path_name - path to save xml data
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public void saveImages (int id_albom) throws FileNotFoundException, XMLStreamException {

		//String str = path_name + id_albom + ".xml";
		
		String str = PaUtils.get().getAlbomXMLFilePath(id_albom);
		
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		
		// "UTF-8" is important
		FileOutputStream fStream = new FileOutputStream(str);
		
		XMLStreamWriter writer = factory.createXMLStreamWriter(fStream, "UTF-8");
		try {
		
		writer.writeStartDocument("UTF-8", "1.0");
		
		writer.writeDTD(NEXT_ROW);
		
		writer.writeStartElement(ELM_PHOTOALBOMS);
		
		writer.writeAttribute(ATR_IDALBOM , Integer.toString( id_albom));
		
		//create nodes
		for(PaImage me : m_images ) {
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeDTD(ADD_TAB);
			
			writer.writeStartElement(ELM_PHOTO);
			
			writer.writeAttribute(ATR_NAME, me.getName());
			
			writer.writeAttribute(ATR_DATE, dateToString(me.getDate(),DATE_FORMAT));
			
			writer.writeAttribute(ATR_PATH, me.getFullPath());
			
			writer.writeAttribute(ATR_ID , Integer.toString(me.getId()));
			
			writer.writeAttribute(ATR_SORT_ID , Integer.toString(me.getSortId()));
			
			writer.writeAttribute(ATR_BOOKMARKED, Boolean.toString( me.isBookmarked() ));
			
			writer.writeAttribute(ATR_PRINTED , Boolean.toString( me.isPrinted() ));
			
			writer.writeAttribute(ATR_LINK , Boolean.toString( me.isLink() ));
			
			writer.writeAttribute(VISIBLE_IN_SLIDER , Boolean.toString( me.isVisibleInSlider() ));
			
			writer.writeAttribute(ATR_COMMENTS, me.getComments());
			
			writer.writeAttribute(ATR_WIDTH, Integer.toString(me.getWidth()));
			
			writer.writeAttribute(ATR_HEIGHT , Integer.toString(me.getHeight()));
			
			for (int i=0; i<me.getSubjectsList().size(); i++) {
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(ELM_SUBJECT);
				
				writer.writeAttribute(ATR_KEY , Integer.toString(me.getKey(i)));
				
				writer.writeEndElement();		
			}
			
			writer.writeDTD(NEXT_ROW);	
			
			writer.writeDTD(ADD_TAB);
			
			writer.writeEndElement();
		}	
		
		writer.writeDTD(NEXT_ROW);
		
		writer.writeEndElement();
		
		writer.writeEndDocument();
		}
		finally {
			
			writer.close();
			
			if(fStream != null){
				
				try {
					
					fStream.close();
					
				} catch (IOException e) {}
			}
		}
	}

	/**
	 * Loads and parses the all image data in the albom container
	 * @param configPhoto - path to file with XML data
	 * @throws IOException
	 */
	public void  loadImages (String configPhoto) throws IOException {

		String name = "";
		
		String p_path = "";
		
		Integer id;
		
		String sortid;
		
		Integer width;
		
		Integer height;
		
		Integer KEY; 
		
		Boolean bookmarked = false;
		
		Boolean printed = false;
		
		Boolean link = true;
		
		Boolean sliderVisible = true;
		
		String comments = new String();
		
		Date p_date = null;
		
		InputStream in = null;
			
		try {
			//input stream on the base of config file
			in = new FileInputStream(configPhoto);	
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
			
			// Stax reading
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT) {
					
					if (reader.getLocalName().equals(ELM_PHOTOALBOMS)) {
						
						m_id = Integer.parseInt(reader.getAttributeValue(null, ATR_IDALBOM ));
					}
					
					
					if (reader.getLocalName().equals(ELM_PHOTO)) {
						
						name = reader.getAttributeValue(null, ATR_NAME);
						
						id = Integer.parseInt(reader.getAttributeValue(null, ATR_ID ));	
						
						sortid = reader.getAttributeValue(null, ATR_SORT_ID );
						
						p_path = reader.getAttributeValue(null, ATR_PATH);
						
						width = Integer.parseInt(reader.getAttributeValue(null, ATR_WIDTH));
						
						height = Integer.parseInt(reader.getAttributeValue(null, ATR_HEIGHT ));
						
						
						bookmarked = Boolean.parseBoolean(reader.getAttributeValue(null, ATR_BOOKMARKED));
						
						printed = Boolean.parseBoolean(reader.getAttributeValue(null, ATR_PRINTED ));
						
						comments = reader.getAttributeValue(null, ATR_COMMENTS);
						
						link = Boolean.parseBoolean(reader.getAttributeValue(null, ATR_LINK ));
						
						String sV = reader.getAttributeValue(null, VISIBLE_IN_SLIDER );
						
						if(sV != null) {			
							
							sliderVisible = Boolean.parseBoolean(sV);
						} 
								
						p_date = stringToDate(reader.getAttributeValue(null, ATR_DATE),DATE_FORMAT);
						
						if (p_date  == null) p_date = new Date();
						
						ArrayList<Integer> keywords = new ArrayList<Integer>();
						
						while (reader.hasNext()) {
							
							int sevent = reader.next();
							
							if (sevent == XMLStreamConstants.START_ELEMENT) {
								
								if (reader.getLocalName().equals(ELM_SUBJECT)) {
									
									KEY = Integer.parseInt(reader.getAttributeValue(null, ATR_KEY ));
									
									keywords.add(KEY);
								}
							}
							if (sevent == XMLStreamConstants.END_ELEMENT) {
								
								if (reader.getLocalName().equals(ELM_PHOTO)) {
									
									PaImage photo = new PaImage(id, name, p_path, keywords, 
											p_date, width, height);
									
									photo.setBookmarked(bookmarked);
									
									photo.setPrinted(printed);
									
									photo.setComments(comments);
									
									photo.setLink(link);
									
									photo.setVisibleInSlider(sliderVisible);
									
									if(sortid != null) {
										
										photo.setSortId(Integer.parseInt(sortid));
									}
									
									m_images.add(photo);
									
									break;
								}
							}
						}
					}					
				}
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			
			writeLog("FileNotFoundException  : " + configPhoto + NEXT_ROW, e, true, false, true);
			
		} catch (XMLStreamException e) {
			
			writeLog("XMLStreamException  : " + configPhoto + NEXT_ROW, e, true, false, true);
		}
		finally {
			
			in.close();
		}
	}
	

	// [0] - printed
	//[1] - boormarked
	public int[] getPrintedPhotos() {
		
		int[] ar = new int[3];

		PaImage x = null;
		
		Iterator<PaImage> it = m_images.iterator();
		
		while (it.hasNext()) {
			
			x=it.next();
			
			ar[2]++;
			
		    if ( x.isPrinted() ) {   ++ar[0]; }
		    
		    if ( x.isBookmarked() ) {  ++ar[1]; }
		
		}
		
		return ar;
	}
	/**
	 * 
	 * @return the number of images in the standard folder
	 */
	public int getStandNumber() {
		
		PaImage x = null;
		
		int counter = 0;
		
		Iterator<PaImage> it = m_images.iterator();
		
		while (it.hasNext()) {
			
			x=it.next();
			
		    if ( !x.isLink() ) {  ++counter; }
		}
		
		return counter;
	}
	
	/**
	 * Inserts the images from buffer to this container
	 * @param copyBuffer - the link to the copy/paste buffer which holds the images
	 */
	public void pasteInBuffer(ArrayList<PaImage > copyBuffer) {
		
		if ( copyBuffer.size() != 0  ) {
			
			Iterator<PaImage> it = copyBuffer.iterator();
			
			PaImage x = null;
			
			while(it.hasNext() ) {
				
				x = it.next();
				
				PaImage p = new PaImage( x );
				
				p.setName( getUniquePhotoName(p.getName(), -1) );
				
				if(x.isLink()) {
					
					add_Image (p);
				}
				else {//if no link we should copy the file from one standard folder to other - this prevents inconsistency when the source album will be deleted
					
					String standardPath = PaUtils.get().getAlbumContainer().
							getAlbum(m_id).getFullStandardPath();
				
					String fileName = PaUtils.getFileNameFromString(x.getFullPath()); 
					
					String newPath = concatPathName(standardPath, fileName);
					
					if(!PaUtils.isSamePathes(newPath, x.getFullPath())) {
						
							try {
								
								p.changePath(standardPath);
								
								p.setLink(false);
								
								add_Image(p);
								
							} catch (SecurityException | IOException e) {
								
								writeLog("Error while moving to standard folder for file " + 
										x.getName()+" id = " + x.getId() + NEXT_ROW+
										" path = " + x.getFullPath(),e,true,false,true);
								
								String str = getMessagesStrs("errorMovedToStandard");
								
						  		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
					    				str,getMessagesStrs("messageErrorCaption"),
					    			    JOptionPane.ERROR_MESSAGE);
							}
					}
				}
			
			}		
		}
	}
	
	/**
	 * comparator for custom user's sorting order - it is using the special sort id
	 * 
	 */
	private class PaCustomComparator implements Comparator<PaImage> {
		
	    public int compare(PaImage p1, PaImage p2) {
	    	
	        return p1.getSortId() - p2.getSortId();
	    }
	}
	/**
	 * comparator for printed flag sorting order 
	 * 
	 */
	private class PaPrintedComparator implements Comparator<PaImage> {
		
	    public int compare(PaImage p1, PaImage p2) {
	    	
	    	if(p1.isPrinted() && !p2.isPrinted()) { return -1000000; }
	    	
	    	if(p2.isPrinted() && !p1.isPrinted()) { return 1000000; }
	    	
	    	if((p1.isPrinted() && p2.isPrinted()) || (!p1.isPrinted() && !p2.isPrinted())) { return p1.getId() - p2.getId(); }
	    	
	        return -1;//doesn't matter - no posibility to get here
	    }
	}
	/**
	 * <p>comparator for bookmarked flag sorting order </p>
	 * 
	 */
	private class PaBookmarkedComparator implements Comparator<PaImage> {
		
	    public int compare(PaImage p1, PaImage p2) {
	    	
	    	if(p1.isBookmarked() && !p2.isBookmarked()) { return -1000000; }
	    	
	    	if(p2.isBookmarked() && !p1.isBookmarked()) { return 1000000; }
	    	
	    	if((p1.isBookmarked() && p2.isBookmarked()) || 
	    			(!p1.isBookmarked() && !p2.isBookmarked())) {
	    		
	    		return p1.getId() - p2.getId(); 
	    	}
	    	
	        return -1;//doesn't matter - no posibility to get here
	    }
	}
	/**
	 * <p>comparator for native sorting order - it is using the original id while insertion</p>
	 * 
	 */
	private class PaNativeComparator implements Comparator<PaImage> {
		
	    public int compare(PaImage p1, PaImage p2) {
	    	
	        return p1.getId() - p2.getId();
	    }
	}
	/**
	 * <p>comparator for date sorting order - it is using the date for sorting</p>
	 * 
	 */
	private class PaDateComparator implements Comparator<PaImage> {
		
	    public int compare(PaImage p1, PaImage p2) {
	    	
	        return p1.getDate().compareTo(p2.getDate());
	    }
	}
	/**
	 * <p>comparator for link sorting order - it is using the link mark for sorting</p>
	 * 
	 */
	private class PaLinksComparator implements Comparator<PaImage> {
		
	    public int compare(PaImage p1, PaImage p2) {
	    	
	    	if(p1.isLink() && !p2.isLink()) { return -1000000; }
	    	
	    	if(p2.isLink() && !p1.isLink()) { return 1000000; }
	    	
	    	if((p1.isLink() && p2.isLink()) || (!p1.isLink() && !p2.isLink())) { 
	    		
	    		return p1.getId() - p2.getId(); 
	    	}
	        return -1;//doesn't matter - no possibility to get here
	    }
	}
	/**
	 * <p>comparator for not link sorting order - it is using the not link mark for sorting</p>
	 * 
	 */
	private class PaStFolderComparator implements Comparator<PaImage> {
		
	    public int compare(PaImage p1, PaImage p2) {
	    	
	    	if(!p1.isLink() && p2.isLink()) { return -1000000; }
	    	
	    	if(!p2.isLink() && p1.isLink()) { return 1000000; }
	    	
	    	if((p1.isLink() && p2.isLink()) || (!p1.isLink() && !p2.isLink())) { 
	    		
	    		return p1.getId() - p2.getId();
	    	}
	    	
	        return -1;//doesn't matter - no possibility to get here
	    }
	}
	/**
	 * @param sortOrder - sorting order enum,this order must be the same as in the PaDeskTopPane.initSortCombo()
	 * 
	 */
	public void setSortOrder(PaSortOrderEnum sortOrder) 
	{
		switch(sortOrder) {
			
			case DATE_ORDER: {		
				
				Collections.sort(m_images, new PaDateComparator());
				
				return;
			}
			case ID_ORDER: {		
				
				Collections.sort(m_images, new PaNativeComparator());
				
				return;
			}
			case CUSTOM_ORDER: {		
				
				Collections.sort(m_images, new PaCustomComparator());
				
				return;
			}
			case PRINTED_ORDER: {		
				
				Collections.sort(m_images, new PaPrintedComparator());
				
				return;
			}
			case BOOOMAKED_ORDER: {		
				
				Collections.sort(m_images, new PaBookmarkedComparator());
				
				return;
			}
			case LINKS_ORDER: {	
				
				Collections.sort(m_images, new PaLinksComparator());
				
				return;
			}
			case ST_FOLDER_ORDER: {		
				
				Collections.sort(m_images, new PaStFolderComparator());
				
				return;
			}
			default: {		
				
				writeLog( "The sort index is incorrect :", null, true, false, true);
			}	
		};		
		
		writeLog( "The sort index is " + sortOrder, null, true, false, true);
	}
	
	/**
	 * <p>The function insert the image with insId before or after image with baseId. Insertion does the reordering of sorting id's order, not natural id</p>
	 * @param insId - image with id will be inserted
	 * @param baseId - base image id (relatively to it the insertion will be performed)
	 * @param beforeFlag - flag of position - before or after baseId the insertion will be performed
	 */
	public void insertCustomSortId(int insId, int baseId, boolean beforeFlag) 
	{
		int beforeSortId;
		
		int afterSortId;
		
		PaImage base = getImage(baseId);
	
		if(base == null) {
			
			writeLog( "Null pointer for id =" + baseId, null, true, false, true);
			
			return;
		}
		
		if (beforeFlag) {		
			
			afterSortId  = base.getSortId();
			
			int indexBefore = findIndex(baseId)-1;
			
			if(indexBefore < 0) { //catches the case of insertion in the very begining
				
				beforeSortId = 0;
			}
			else {
				
				beforeSortId = m_images.get(indexBefore ).getSortId();
			}				
		}
		else {
			
			beforeSortId = base.getSortId();
			
			int indexAfter = findIndex(baseId)+1;
			
			if(indexAfter >= m_images.size()) { 
				//catches the case of insertion in the very end
				afterSortId = beforeSortId + PaUtils.get().getCustomSortIdStep()*2;
				
			}
			else {
				
				afterSortId = m_images.get(indexAfter).getSortId();
			}
			
		}
	
		//here we try to insert an image (sort id of image) between two other images (their sort ids)
		if((afterSortId - beforeSortId) >= 3) {
			
			getImage(insId).setSortId((afterSortId + beforeSortId)/2);
		}
		else {	//too small sort id gap between two images - we should reorder the sort ids till the end of arraylist
			if(beforeSortId == 0 ) {
				
				//reordering form the first element
				reOrderSortIds(0, PaUtils.get().getCustomSortIdStep(),insId); 
				
			} else {
				
				reOrderSortIds(findIndex(beforeSortId)+1, beforeSortId,insId);
			}
			insertCustomSortId(insId, beforeSortId, false);
		}	
	}
	/**
	 * @param indexStart - start index to reorder
	 * @param prevValueSortId - sort id of previous element in array
	 * @param exclusiveID - id of element (not sort id) which should be excluded from reordering (this is element to insert)
	 * 
	 */
	private void reOrderSortIds(int indexStart, int prevValueSortId, int exclusiveID)
	{
		for(int i=indexStart; i < m_images.size(); ++i ) {
			
			if( m_images.get(i).getId() != exclusiveID) {
				
				 prevValueSortId += PaUtils.get().getCustomSortIdStep();
				 
				 m_images.get(i).setSortId(prevValueSortId);			
			}
		}				
	}
		
	/**
	 * 
	 * @param p - image to be replaced
	 * 
	 */
	public void replacePhoto(PaImage p)
	{
		int index = findIndex(p.getId());
		
		PaImage ph = p.cloneData();
		
		m_images.set(index, ph);
	}
	 
	/**
	* <p>Returns the Set with names of used subjects in this container (albom)</p>
	*/
	 public Set<String> getUsedSubjectsList()
	 {		 
		Set<String> s = new HashSet<String>(); 
		
		PaSubjectContainer tCont = PaUtils.get().getSubjectsContainer();
		 
		Iterator<PaImage> iter = m_images.iterator();
		
		PaImage x;
		
		while (iter.hasNext ()) {
			
			x = iter.next();
			
			ArrayList<Integer> list = x.getSubjectsList();
			
			for(Integer id: list){
				
				s.add(tCont.getTem(id).getName());
			}		
		}		
		return s;
	 }
	 
	/**
	* <p>Moves all links to standard folder. Does the physical coping of files in standard folder</p>
	*/
	 public void moveLinksToStandardFolder() 
	 {
		 	String standardPath = PaUtils.get().getAlbumContainer().getAlbum(m_id).getStandardPath();
		 	
			Iterator<PaImage> iter = m_images.iterator();
			
			PaImage x;
			
			int counter = 0;
			
			ArrayList<String> errorList = new ArrayList<String>(); 
			
			while (iter.hasNext ()) {
				
				x = iter.next();
				
				String fileName = PaUtils.getFileNameFromString(x.getFullPath()); 
				
				String newPath = concatPathName(standardPath, fileName);
				
				PaUtils.get();
				
				if(x.isLink() && !PaUtils.isSamePathes(newPath, x.getFullPath())) {
					
					try {
						
						x.changePath(standardPath);
						
						x.setLink(false);
						
						++counter;
						
					} catch (SecurityException | IOException e) {
						
							writeLog("Error while moving to standard folder for file " + 
								x.getName()+" id = " + x.getId() + NEXT_ROW+
								" path = " + x.getFullPath(), e, true, false, true);
						
							errorList.add(x.getFullPath());
					}
					
				}
			} 
			if(errorList.isEmpty()) {
				
				writeLog(getMessagesStrs("numberOfMovedToStandard") + " " + counter,null,true,true,true);	
			}
			else {
				
				String str = getMessagesStrs("errorMovedToStandard")+NEXT_ROW;
				
				for(String s: errorList){
					
					str += s + NEXT_ROW;				
				}
				
		  		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
	    				str,getMessagesStrs("messageErrorCaption"),
	    			    JOptionPane.ERROR_MESSAGE);
			}
	 }
}
