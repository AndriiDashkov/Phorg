package PaGlobal;


import static PaGlobal.PaLog.writeLog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import PaActions.PaActionExtStart;
import PaCollection.PaAlbumContainer;
import PaCollection.PaMainConainer;
import PaCollection.PaRecentData;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaCollection.PaSelectedImages;
import PaCollection.PaSettings;
import PaCollection.PaSubjectContainer;
import PaDialogs.PaSpecialDialog;
import PaDialogs.PaSpecialDialog.DialogType;
import PaExif.PaByteOrder;
import PaForms.PaAlbumsTreeForm;
import PaForms.PaImagePopupMenu;
import PaForms.PaImageTable;
import PaForms.PaSubjectsForm;
import PaImage.PaViewPanel;
import PaLong.PaDirDeleteLongTask;


/**
 * 
 * @author avd
 *
 */
public class PaUtils {
	
	private static PaUtils _instance;//image height
	
	private static String DIR_SEP;
	
	public static final int VERT_STRUT = 12;
	
	public static final int HOR_STRUT = 6;
	
	final static String roiFileDelimeter1 = ";";
	
	final static String roiFileDelimeter2 = ",";

	private JComboBox<String>  m_sortCombo = null;


	/**
	 * enum for switch between types of operation systems
	 * @author avd
	 *
	 */
	public static enum OSType {
		
		 WIN,
		 
		 LINUX
	}
	
	public static final OSType OS_TYPE = OSType.WIN;
	
	/**
	 * Sets differences between OS implementations
	 */
	private void initOSDependedConsts() {
		
		switch( OS_TYPE ) {
		
			default: {}
			
			case WIN: {		
				
					DIR_SEP = "\\";
					
			    	break;
			}
		
			case LINUX : {
				
					DIR_SEP = "/";
					
					break;
			}
		};
	}
	
    //DATE_FORMAT for inner keeping of dates 
	public static final String DATE_FORMAT = "dd.MM.yyyy";
	
	//GUI_DATE_FORMAT - for outer UI presentation of dates
	public static String GUI_DATE_FORMAT = "dd.MM.yyyy";
	
	public final static String NEXT_ROW = "\n";
	
	public final static String ADD_TAB = "\t";
	
	public final static String NAME_EXT_SEP = ".";
	
	//public static String DIR_SEP2 = "/";
	
	public final static String NEW_FILE_EXT = "+1";
	
	public final static String MAIN_APP_PREF = "Phorg";
	
	public final static String ALBUM_FOLDER_NAME_PREF = "album";
	
	private final String USER_DATA_DIR_PREF = ".data";
	
	private ArrayList<PaImage> copyBuffer = new ArrayList<PaImage>();
	
	private String[] m_imageExtensions = {
			"jpeg", "jpg" ,"gif", "bmp", "wbmp", "png"	
	};
	private String[] m_videoExtensions = {
			"webm","mkv","flv",
			"vob","ogv","ogg",
		 	"drc","gifv",
		 	"avi","mov",
		 	"qt","wmv",
		 	"yuv","rm",
		 	"rmvb","asf",
		 	"mp4","m4p",
			"m4v",
		 	"mpg", "mp2", "mpeg", "mpe", "mpv",
		 	"mpg", "mpeg", "m2v",
		 	"svi",
		 	"3gp","3g2",
		 	"mxf","nsv",
		 	"flv", "f4v", "f4p" ,"f4a", "f4b"
	};
	
//	private static final Object lock = new Object();
//	private static boolean isInitialized;
		
/**
 * main container for albums
 */
	private PaAlbumContainer m_mainAlbumContainer;		
	/**
	 * main container for subjects
	 */
	private PaSubjectContainer m_mainSubjectContainer;			
	/**
	 * main container for images
	 */
	private PaMainConainer m_mainImagesContainer;	
	/**
	 * container for selections
	 */
	private PaSelectedImages m_selectedImages;				

	private PaAlbumsTreeForm m_albumsForm;
	
	private PaSubjectsForm m_subjectsForm;
	
	private PaImageTable m_imagesTableForm;
	/**
	 * main view panel with images
	 */
	private PaViewPanel m_viewPanel;						
	
	private PaSettings _settings;
	
	private PaRecentData _recents;	
	
	private JTextField m_mainLabel;						
					
	private JFrame _mainWindow;		
	
	private PaImagePopupMenu _popupPhoto;
	
	private PaTokenizer _tokenizer;
	
	private String _workDir;
	
	private String _homeDir;
	
	private static ResourceBundle _menusStringSource;
	
	private static ResourceBundle _guiStringSource;
	
	private static ResourceBundle _messagesStringSource;
	
	private Integer [] imageSizes = { 120, 150, 180, 
			210, 240, 270, 300, 350, 400, 500, 600, 800 };
	
	private Integer [] numberImageColumns = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
	
	public static final int ALBUM_TOP_PARENT_ID = 0;
	
	public static final int ALBUM_TREE_PARENT_ROOT_ID = -1;
	
	/**
	 * special boost image to show instead video files
	 */
	BufferedImage m_videoImage = null;
	
	BufferedImage m_nonValidImage = null;
	
	/**
	 * 
	 * @return the system depended separator for path
	 */
    public static String getSeparator() {
    	
    	return  DIR_SEP;
    }
	
	
	public void setWorkDir(String dirName) {
		
		_workDir = dirName;
	}
	
	
	public String getWorkDir() {
		
		return  _workDir;
	}
	
	public void setHomeDir(String dirName) {
		
		_homeDir = dirName;
	}

	/**
	 * 
	 * @return the link to the main album container
	 */
	public  PaAlbumContainer getAlbumContainer () {
		
		return  m_mainAlbumContainer;
	}
	
	public  PaSubjectContainer getSubjectsContainer () {
		
		return  m_mainSubjectContainer;
	}
	/**
	 * Main container contains the all other containers with image objects
	 * @return the main container over all containers
	 */
	public PaMainConainer getMainContainer () {
		
		return m_mainImagesContainer;
	}
	
	public PaSelectedImages getSelectedImages() {
		
		return m_selectedImages;
	}

	public PaAlbumsTreeForm get_albomsForm () {
		
		return m_albumsForm;
	}
	/**
	 * 
	 * @return the link to the table (form) of subjects
	 */
	public PaSubjectsForm getSubjectsForm() {
		
		return m_subjectsForm;
	}
	
	public PaViewPanel getViewPanel () {
		
		return m_viewPanel;
	}
	
	public PaImageTable get_photosForm() {
		
		return m_imagesTableForm;
	}
	
	//END	
	private PaUtils() {
				
		initOSDependedConsts();
	};
	
	public static PaUtils get() {
		
		if (_instance == null) {
			
			_instance = new PaUtils();	
		}
		return _instance;
	}
	
	public JTextField getMainLabel() {
		
		return m_mainLabel;
	}
	public void setMainLabel(String str) {
		
		m_mainLabel.setText(str);
	}
	

	
	public PaSettings getSettings () {
		
		return _settings;
	}
	
	public PaRecentData getRecents () {
		
		return _recents;
	}
		
	/**
	 * 
	 * @return full path to the icons folder
	 */
    public String getIconsPath() {
    	
    	return new String (_workDir+DIR_SEP +"Icons"+DIR_SEP +"actions"+DIR_SEP );
    }
    /**
     * 
     * @param jfrm - new main window object
     */
    public void setMainWindow (JFrame jfrm) {
    	
    	_mainWindow = jfrm;
    }
    
    /**
     * 
     * @return the main window
     */
    public JFrame getMainWindow () {
    	
    	return _mainWindow;
    }
    
    public PaImagePopupMenu get_popUp () {
    	
    	return _popupPhoto;
    }
    
    public PaTokenizer get_tokenizer () {
    	
    	return _tokenizer;
    }
        
    public String getAppPath () {
    	
    	return new String (_homeDir+DIR_SEP+"Documents"+DIR_SEP+MAIN_APP_PREF+DIR_SEP);
    }
    
    public String getXMLPath () {
    	
    	return new String ( getAppPath ()+USER_DATA_DIR_PREF+DIR_SEP+"xml"+DIR_SEP);
    }
    
    public String getPhotoPath () {
    	
    	return new String (getAppPath ()+"photos"+DIR_SEP);
    }
    
    public String getAlbomsXMLName () {
    	
    	return new String ("_albumSet.xml");
    }
    
    public String getAlbomsXMLFullName () {
    	
    	return new String ( getXMLPath () +getAlbomsXMLName ());
    }

    public String getTemsXMLName () {
    	
    	return new String ("_subjectSet.xml");
    }
    
    public String getSubjectsXMLFullName () {
    	
    	return new String ( getXMLPath () + getTemsXMLName());
    }
    
    public String getSettingsXMLName () {
    	
    	return new String ("_settings.xml");
    }
    
    public String getRecentsXMLName () {
    	
    	return new String ("_recents.xml");
    }
    
    /**
     * Path to XML data
     */
    public String getPhotosXMLPath () {
    	
    	return new String (getXMLPath () +"_albm_");
    }
    /**
     * 
     * @param id - albom's id
     * @return the full path to the albom's xml file
     */
    public String getAlbomXMLFilePath(int id) {
    	
    	return new String (getPhotosXMLPath() + id + ".xml");
    }
    
    public String getPathToAlbumsIcons()
    {
 	   
 	   return getAppPath ()+USER_DATA_DIR_PREF+DIR_SEP+"icons"+DIR_SEP;
    }
    
    /**
     * 
     * @param cont
     * @return the path to the boost icons for the container cont
     */
    public String getPathToBoostIcons(PaImageContainer cont)
    {
    	String path = concatPathName(getPathToAlbumsIcons(), Integer.toString(cont.getAlbum().getId()));
    	
    	File f = new File(path);
    	
    	if ( ! f.exists() ) { 
    		
    		 f.mkdirs();
    	}
    	return path;
    }
    
    /**
     * 
     * @return the color which is used for selection in the main view panel
     */
    public Color getSelectionColor() {
    	
    	return this.getSettings().getSelectColor();
    }
    
    public String getDefaultAlbomsIconName()
    {   
 	   return "paalbomicon.png";
    }
    
    
    /**
     * The function which initializes all main containers of the application
     */
	public void initVariable () 
	{
		
		m_mainAlbumContainer = new  PaAlbumContainer();
		
		m_mainSubjectContainer = new PaSubjectContainer();
		
		m_mainImagesContainer = new PaMainConainer();
		
		m_selectedImages = new PaSelectedImages();
		
		_settings = new PaSettings();
		
		_recents = new PaRecentData();
		
		m_mainLabel = new JTextField(50);
		
	}
	
	public void initForms () {

		_tokenizer = new PaTokenizer();
		
		m_albumsForm = new PaAlbumsTreeForm();
		
		m_subjectsForm = new PaSubjectsForm();
		
		m_viewPanel = new PaViewPanel();	
			
		_popupPhoto = new PaImagePopupMenu();
		
		_popupPhoto.addMouseAdapter(m_viewPanel.get_Panel());
		
		m_imagesTableForm = new PaImageTable(m_viewPanel);	
	}
    
	/**
	 * Initiates the all main containers of the application
	 * @throws IOException
	 */
    public void initContainers () throws IOException  {
    	
    		
		_settings.load_Parameters(getXMLPath().concat(getSettingsXMLName()));
		
		_recents.load_Parameters(getXMLPath().concat(getRecentsXMLName()));
		
		_menusStringSource = _settings.getResourceBundle("MenusStringSource");
		
		_guiStringSource = _settings.getResourceBundle("GuiStringSource");
		
		_messagesStringSource = _settings.getResourceBundle("MessagesSource");
		
		m_mainAlbumContainer.loadAlbums(getXMLPath().concat(getAlbomsXMLName()));
		
		m_mainSubjectContainer.loadSubjects(getXMLPath().concat(getTemsXMLName()));
		
		m_mainImagesContainer.loadMainContainer(getAlbumContainer());
		
		GUI_DATE_FORMAT = _settings.getGuiDateFormat();
		
		if ( GUI_DATE_FORMAT.equals(PaSettings.LOCALE_DATE_DEFAULT_CAPTION) ) {
			
			GUI_DATE_FORMAT = new SimpleDateFormat().toPattern().substring(0, 8);
		
		}
    }
    
    
    public static String getFileNameFromString(String fullPath ) {
    	
    	String fileName =new String();
    	
    	if (fullPath.lastIndexOf(DIR_SEP) != -1 )  {
    		
    		fileName = fullPath.substring(fullPath.lastIndexOf(DIR_SEP)+1) ;
    	}

    	return fileName;
    
    }
    
    public String getPathFromString(String fullPath ) 
    {
    	
    	String path = new String();
    	
    	if (fullPath.lastIndexOf(DIR_SEP) != -1 )  {
    		
    		path = fullPath.substring(0,fullPath.lastIndexOf(DIR_SEP)+1) ;
    	}

	
    	return path;
    
    }
    
    public static boolean isSamePathes(String p1, String p2 ) 
    {
    	
    	Path path1 = Paths.get(p1);
    	
    	Path path2 = Paths.get(p2);
    	
    	if(path1 == null || path2 == null) {
    	
    	}
    	   	
    	int result = path1.compareTo(path2);
    	
    	if ( result == 0 ) return true;
    	
    	return false;
   
    
    }
        
    public static String  checkFilePermisions(String checkPath,boolean checkReadable, boolean checkWritable) {
    	
    	Path p;
    	
    	try { 
    		
			p=FileSystems.getDefault().getPath( checkPath );
		}
		catch( InvalidPathException exp) {
			
			 return getMessagesStrs("notCorrectPathValueMessage");  
		
		}
    	
 
    	Path parentPath = p;
   
    	do {
    		
    		if ( Files.exists(parentPath) ) {
    			
    			if (  checkReadable && !Files.isReadable(parentPath) ) {
    				
    				 return getMessagesStrs("notReadableOperationMessage"); 
    			}
    			
    			if (  checkWritable && !Files.isWritable(parentPath)  ) {
    				
   				 return getMessagesStrs("notWritableOperationMessage");
    			}

    			break;
    		}
    		
    		if (parentPath == p.getRoot() ) { break;}
    		
    		parentPath = parentPath.getParent();
    		
    		if ( parentPath == null ) {
    			
    			return getMessagesStrs("notValidPathMessage");
    		}
    		
    	} while(true);
    	
    	

		return new String();
    	
    }
    
	/**
	 * <p>Deletes not empty dir. Because the operation potentially is long, we use progress monitor</p>
	 */
    public PaDirDeleteLongTask deleteNotEmptyDir(File dir)
    {	
		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
				getMessagesStrs("deleteFilesOperationCaption"),
				getMessagesStrs("deleteFilesOperationNote"), 0, 100);
		
		progressMonitor.setMillisToDecideToPopup(0);
		
		progressMonitor.setMillisToPopup(0);

		//add images operation is potentially long, we start it using the SwingWorker 
		PaDirDeleteLongTask ts = new PaDirDeleteLongTask(progressMonitor,dir,true);
		
		ts.execute(); 

		return ts;
 	}
	/**
	 * <p>Delete all subfolders, AND the root folder or file. Reloaded function.</p>
	 * @param path to folder or file to be deleted
	 * @param yesMessage - message for log in the case of operation success
	 * @param noMessage - message for log in the case of operation fail
	 */
    public void deleteNotEmptyDir(String path,String yesMessage,String notMessage)
    {	
    	File fF = new File(path);
    	
		if( fF != null && fF.exists() ) {
			
			deleteNotEmptyDir(fF);
			
			if(yesMessage != null) {
				
				writeLog(yesMessage, null, true, true, true);
			}
		}
		else {					
			
			if(notMessage != null) {
				
				writeLog(notMessage, null, true, true, true);
			}
		} 
    
    }
    

	/**
	 * <p>Delete all subfolders, but NOT the root folder or file. Reloaded function.</p>
	 * @param dir t- folder or file to be deleted
	 */
    public void deleteDirChilds(File dir)
    { 
    	if (dir.isDirectory()) 
    	{	
    		
    		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
    				getMessagesStrs("deleteFilesOperationCaption")+NEXT_ROW
    				+dir.getPath(),getMessagesStrs("deleteFilesOperationNote"), 0, 100);
    		
    		progressMonitor.setMillisToDecideToPopup(0);
    		
    		progressMonitor.setMillisToPopup(0);
    		
    		//add images operation is potentially long, we start it using the SwingWorker 
    		PaDirDeleteLongTask ts = new PaDirDeleteLongTask(progressMonitor,dir,false);//false - not delete root folder
    		
    		ts.execute();
    		
    	} 
	}
    

	/**
	 * <p>Delete all subfolders, but NOT the root folder or file. Reloaded function.</p>
	 * @param path to folder or file to be deleted
	 * @param yesMessage - message for log in the case of operation success
	 * @param noMessage - message for log in the case of operation fail
	 */
    public void deleteDirChilds(String path,String yesMessage,String notMessage)
    { 
    	File fF = new File(path);
    	
		if( fF != null && fF.exists() ) {
			
			deleteDirChilds(fF);
			
			writeLog(yesMessage, null, true, true, true);
		}
		else {		
			
			writeLog(notMessage, null, true, true, true);
		}      
    }
    
	public static void copyFile(String sourceFullPath, String targetFullPath) throws IOException, FileNotFoundException {
		
		File f= new File( sourceFullPath );
		
		if ( f.exists() ) {
			
			String newFullPath = concatPathName(targetFullPath, checkUniqueFileName(targetFullPath, f.getName()) );
	
			FileChannel source = null;
			
			FileChannel destination = null;
			
			FileInputStream fileInputStream = null;
			
			FileOutputStream fileOutputStream = null;
			
			try {
				
				fileInputStream = new FileInputStream(sourceFullPath);
				
				source = fileInputStream.getChannel();
				
				fileOutputStream = new FileOutputStream(newFullPath);
				
				destination = fileOutputStream.getChannel();
				
				destination.transferFrom(source, 0, source.size());
				
			} 
			finally {
	
				if(source != null) { source.close(); }
				
				if(destination != null) { destination.close(); }
				
				if(fileInputStream != null) {  fileInputStream.close(); }
				
				if(fileOutputStream != null) {  fileOutputStream.close(); } 
								
			}
						
		}
	}
    /**
     * Creates path + name; the func controls the end of the path variable
     * @param path
     * @param name
     * @return concatenated path
     */
    public static String concatPathName(String path, String name) 
    {
    	
     	if ( path.endsWith(DIR_SEP) /*|| path.endsWith(DIR_SEP2) */ ) {
    		
    		return path+name;
    	}
     	

     	
     	return path+DIR_SEP+name;
     
    }
    /**
     * 
     * @param fullFileName - full name of the file
     * @return two strings first [0] - file name without extension, second [1] = extension of the file
     */
    public static String[] getNameAndExtension(String fullFileName)
    {
    	String[] sA = new String[2];
    	
    	 int index =fullFileName.lastIndexOf(NAME_EXT_SEP);
    	 
    	 if ( index == -1 ) {
    		 
    		 sA[0] = fullFileName;
    		 
    		 sA[1] = new String();
    	 }
    	 else {
    		 
    		sA[0] =  fullFileName.substring(0, index);
    		
    		sA[1] = fullFileName.substring(index+1);
    	 }
    	
    	 return sA;
    }

    
  
	public static String checkUniqueFileName(String checkPath, String fileNameToCheck )
	{
		String 	path = concatPathName(checkPath, fileNameToCheck ); 
		
		File f = new File(path);
		
		if ( f != null && f.exists() ) {
			
			String[] sArray = getNameAndExtension(fileNameToCheck);
			
			String newName = new String();
			
			if ( sArray[1].isEmpty() ) {
				
				newName = sArray[0]+NEW_FILE_EXT;
			}
			else {
				
				newName  = sArray[0]+NEW_FILE_EXT+NAME_EXT_SEP+sArray[1];
			}
			fileNameToCheck = checkUniqueFileName(checkPath, newName);
		}
		
		return  fileNameToCheck;
	}
	

   public static String getMenusStrs(String name) {
	   	   
	  return  _menusStringSource.getString(name);
   }
   
   public static String getGuiStrs(String name) {
	   	   
	  return  _guiStringSource.getString(name);
   }
   
   
   public static String getMessagesStrs(String name) {
	   	   
	  return  _messagesStringSource.getString(name);
   }
	
  public static String dateToString(Date dat, String format ) {
	  
	if ( dat == null || format.isEmpty() ) { return "00-00-00"; }
	
	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
	
	dateFormat.setLenient(false);
	
	return dateFormat.format( dat );
	
  }
  
  public static Date stringToDate(String dat,String format) {
	  
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		
		dateFormat.setLenient(false);
		
		try {
			
			Date d = dateFormat.parse( dat );
			
			return  d;
			
		} catch (ParseException e) {
			
			writeLog("ParseException :  " + NEXT_ROW, e, true, false, true);
			
			return null;
		}
  }

  public JMenu createExternalEditorMenu() {
	  
	  JMenu submenu = new JMenu(getMenusStrs("openImageInExtMenuName"));
	  
	  ArrayList<String []> list= _settings.getExtEditorsList();
	  
	  for( short i=0; i < list.size(); i++ ) {
		  
		  submenu.add( new PaActionExtStart(list.get(i)[1],list.get(i)[0],list.get(i)[2]) );
	  }
	  		
	  return submenu;
  }
  
  public ArrayList<PaImage> getCopyPhotoBuffer() {
	  
	  return copyBuffer;
  }
  
  public Integer [] getImageSizes() {
	  
	  return imageSizes;
  }
  
	public Integer[] getNumberImageColumns() {
		
		
		return numberImageColumns;
	}
    
	/**
	 * 
	 * @param sourceImage source image
	 * @param cursorParent - previous cursor; the operation cam be long and we can inherit the cursor from previous steps
	 * @return the new copy of sourceImage
	 */
	public static Image copyImage(BufferedImage sourceImage, Component cursorParent) {
		
		 if (cursorParent != null ) cursorParent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		 try {
			 
			int wMax = sourceImage.getWidth();   int hMax =  sourceImage.getHeight();
			
			BufferedImage targetImage = new BufferedImage(   wMax,  hMax,  BufferedImage.TYPE_INT_RGB);
	
			for ( int i = 0; i < wMax; i++) 
				
				for ( int j = 0 ; j < hMax; j++) 
					
					targetImage.setRGB(i, j,  sourceImage.getRGB(i, j));
								
			return   targetImage;	
		 }
		 finally {
			 
			 if (cursorParent != null ) cursorParent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			 
		 }
	}
	/**
	 * 
	 * @param sImage - source image
	 * @return the new deep copy of source image
	 */
	public static BufferedImage deepCopy(BufferedImage sImage) {
		
	    ColorModel cm = sImage.getColorModel();
	    
	    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	    
	    WritableRaster raster = sImage.copyData(sImage.getRaster().createCompatibleWritableRaster());
	    
	    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	//getSubimage

	/**
	 * @return full path to the log file
	 */
	public String getLogFullPath() {
			
		return getWorkDir() + DIR_SEP + "log_pa.txt";
	}


	/**
	 * @return step for new custom sort id
	 * the idea: in case of reodering(insertion) of custom positions it is useful to have space between sibling id's
	 */
	public int getCustomSortIdStep()
	{
		
		return 10;	
	}
	
	
	/**
	 * @return converts sequence of 2 bytes into short value
	 */
	public static short fromByteToShort(byte[] b, PaByteOrder byteOrder) {

		if(byteOrder == PaByteOrder.BigEndian) {
			
			return (short) ((b[0] <<8) | (b[1])); 
		}
		
		return (short) ((b[0]) | (b[1] <<8) );
	}
	
	/**
	 * @return converts sequence of 4 bytes into short value
	 */
	public static int fromByteToInt(byte[] b, PaByteOrder byteOrder) {
		

		if(byteOrder == PaByteOrder.BigEndian) {	
			
			return (int) ((b[0] << 24 ) | (b[1] << 16) | (b[2] << 8) | (b[3] << 0)); //TODO проверить варианты для BigEndian
		}
		return (int) ((b[3] << 24 ) | (b[2] << 16) | (b[1] << 8) | (b[0] << 0));
		
	}
	


	/**
	 * @return  combo box - this is the single main combo box, the direct link to it is useful
	 */
	public JComboBox<String> getSortCombo(){  return m_sortCombo; }
	/**
	 * <p>Sets the link to the main sort combo box</p>
	 * @param combo - main sorting combo box. The direct link to it is very convinient 
	 */
	public void setSortCombo(JComboBox<String> combo) { m_sortCombo = combo;	}


	/**
	 * @return true if the application is in debug view 
	 */
	public boolean isDebugView() {
		
		return false;
	}
	
	/**
	 * <p>Sets the wait cursor globally, controls previous value of cursor</p>
	 */
	public void setCursor(int currentCursorType, int typeToSet) {
		
		 if(currentCursorType != typeToSet) {
			 
			 getMainWindow().setCursor(Cursor.getPredefinedCursor(typeToSet));
		 }	
	}
	
	/**
	 * <p>Resets the wait cursor to default cursor globaly, controls previous value of the cursor</p>
	 */
	public void resetCursor(int previousCursorType)
	{			 
		getMainWindow().setCursor(Cursor.getPredefinedCursor(previousCursorType));
	}
	
	public int getCurrentCursor() {
		 
		 return getMainWindow().getCursor().getType();

	}
	
	/**
	 * 
	 * @param c - component to set fixed size
	 */
	public void setFixedSizeBehavior(JComponent c)
	{
		Dimension size = c.getPreferredSize();
		
		size.width = c.getMaximumSize().width;
		
		c.setMaximumSize(size);
	}
	
	/**
	 * @return the base font which is used in all GUI forms and windows
	 */
	public Font getBaseFont()
	{		
		return new Font(Font.DIALOG, Font.PLAIN , 12);
	}
	
	public Font getLargeFont()
	{		
		return new Font(Font.SANS_SERIF, Font.PLAIN , 14);
	}
	
	public static String getRoiDelimeter1()
	{		
		return roiFileDelimeter1;
	}
	
	public static String getRoiDelimeter2()
	{		
		return roiFileDelimeter2;
	}
	
	
	/**
	 * @return the name of the visible root node in the albom's tree
	 */
	public static String getAlbomsRootName()
	{
		return getGuiStrs("albomsRootName");
	}
	
	/**
	 * @return the path to the introduction xml file which is used for JTree object in user manual dialog
	 */
	public String getFullPathToHelpFile(){ 
		
		String s = getPathToHelpFiles()+"contents.txt";
		
		return s;
	}
	/**
	 * @return the full path to the folder with help files
	 */
	public String getPathToHelpFiles(){ 
		
		String p = getWorkDir() + DIR_SEP + "Help"+DIR_SEP+_settings.getLanguage()+DIR_SEP;
		
		return  p;
	}
	/**
	 * @param f - file to contr
	 * @return true if the f is an image file
	 */	
	public boolean isImageFile(File f) {
		
		if(f == null || !f.exists() || f.isDirectory()) return false;	
		
		String fileName = getFileNameFromString(f.getAbsolutePath());
		
		int i = fileName.lastIndexOf('.');
		
		if (i > 0) {
			
		    String extension = fileName.substring(i+1).toLowerCase();
		    
		    return isImageExtension(extension);
		}
		
		return false;
	}
	
	/**
	 * @param f - file to contr
	 * @return true if the f is an image file
	 */	
	public boolean isImageFileStr(String fileName) {
			
		int i = fileName.lastIndexOf('.');
		
		if (i > 0) {
			
		    String extension = fileName.substring(i+1).toLowerCase();
		    
		    return isImageExtension(extension);
		    
		}
		
		return false;
	}
	
	/**
	 * @param f - file to contr
	 * @return true if the f is an image file
	 */	
	public boolean isImageOrVideoFileStr(String fileName) {
			
		int i = fileName.lastIndexOf('.');
		
	
		if (i > 0) {
			
		    String extension = fileName.substring(i+1).toLowerCase();
		    
		    return isImageExtension(extension) || isVideoExtension(extension);
		}
		
		return false;
	}
	/**
	 * @param f - file to contr
	 * @return true if the f is an image file
	 */	
	public boolean isVideoFile(String fileName) {
			
		int i = fileName.lastIndexOf('.');
	
		if (i > 0) {
			
		    String extension = fileName.substring(i+1).toLowerCase();
		    
		    return isVideoExtension(extension);
		    
		}
		
		return false;
	}
	
	/**
	 * performs recursive seach if the flag isRecursive == true
	 * @param isRecursive - recursive search flag
	 * @return amount of image files in dir; 
	 */	
	public int countImageFiles(File dir, boolean isRecursive) {
		
		int counter = 0;
		
		File[] files = dir.listFiles();
		
		for(File f: files) {
			
			try {
		
				if(f.isDirectory()) {
					
					if( isRecursive &&
						!f.getName().matches(".*RECYCLE.*BIN.*") &&
						!f.getName().matches(".*System Volume Information.*"))
					{ 
						counter += countImageFiles(f,isRecursive);
					
					}
				}
				else {
					
					if(isImageFile(f)) counter++;
				}
			}
			catch(NullPointerException ex) {
				
				writeLog("NullPointerException : Can't count folder :" + f.getAbsolutePath(),
        				ex, true, false, true);
			}
		}
		
		return counter;
	}
	
	
	/**
	 * performs recursive seach if the flag isRecursive == true
	 * @param isRecursive - recursive search flag
	 * @return amount of image files in dir; 
	 */	
	public int countImageFiles2(File dir, boolean isRecursive) {
		
		int counter = 0;
		
		String[] files = dir.list();
		
		for(String f: files) {
			try {
		
				if(isImageOrVideoFileStr(f) ) {
					
					counter++;
				}
				else if(isRecursive){
					
					File d= new File(dir.getAbsolutePath()+DIR_SEP+f);
					
					if(d.isDirectory()   &&
							!f.matches(".*RECYCLE.*BIN.*") &&
							!f.matches(".*System Volume Information.*"))
						{ 
							counter += countImageFiles(d,isRecursive);
					
						}
				}
			}
			catch(NullPointerException ex) {
				
				writeLog("NullPointerException : Can't count folder :" + f,
        				ex, true, false, true);
			}
		}
		
		return counter;
	}
	
	
	/**
	 * performs recursive search if the flag isRecursive == true
	 * returns immediately after first found image
	 * @param isRecursive - recursive search flag
	 * @return true if at least one image in the folder's structure
	 */	
	public boolean isImagePresentInside(File dir, boolean isRecursive) {

		String[] files = dir.list();
		
		for(String f: files) {
			
			try {
				
				if(isImageOrVideoFileStr(f)) { 
					
					return true; 
				}
				else if( isRecursive ) {
					
					File d= new File(dir.getAbsolutePath()+DIR_SEP+f);
					
					if(d.isDirectory() &&
						!f.matches(".*RECYCLE.*BIN.*") &&
						!f.matches(".*System Volume Information.*"))
					{ 
						
						return  isImagePresentInside(d,isRecursive);
					}
				}
		
			}
			catch(NullPointerException ex) {
				
				writeLog("NullPointerException : Can't count folder :" + f,
        				ex, true, false, true);
			}
		}
		
		return false;
	}
	

		/**
		 * <p>Calculates the dimension which is required to fit image into
		 * sizes wd,ht with saving of proportion between image's height and width</p>
		 * @param img - image
		 * @param wd width to fit
		 * @param ht - height to fit
		 * @return new dimension which always is inside a rectangle wd x ht
		 */
		public Dimension getWidthHeight(BufferedImage img, int wd, int ht) {
			
			int width  = wd;
			
			int height = ht;
			
			 float aspectRatio =  (float) img.getWidth() / (float) img.getHeight();
			 
			 if ( aspectRatio > 1.0 ) {
				 
				 int h = (int) ( ( (float) width ) / aspectRatio );
				 
				 if ( h > height ) {
					 
					 width = (int) ( ( (float) height ) * aspectRatio );
				 }
				 else {			
					 
					 height =h;
				 }
			 }
			 else {
				 
				 int w = (int) ( ( (float) height ) * aspectRatio );
				 
				 if ( w > width ) {
					 
					 height = (int) ( ( (float) width ) / aspectRatio );
				 }
				 else {
					 
					 width =w;
				 }
			 }
			 
			 return new Dimension(width,height);
			
		}
		/**
		 * Generates new folder name for id ; this function is here just for unification
		 * @param id - id of album
		 * @return new album folder name
		 */
		static public String getNewAlbumFolderName(int id) {
			
			 return new String(ALBUM_FOLDER_NAME_PREF + id);
			
		}
		
		/**
		 * 
		 * @param i
		 * @return
		 * @throws IOException
		 */
		public byte[] intToBytes(int i[])  {
			
		    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		    
		    ObjectOutput out;
		    
			try {
				
				out = new ObjectOutputStream(byteStream);
			

			    for(int k = i.length-1; k >=0;  --k) {
			    	
			    	out.writeInt(i[k]);
			    }
			    
			    out.close();
			    
			    byte[] bytes = byteStream.toByteArray();
			    
			    byteStream.close();
			    
			    return bytes;
			    
			} catch (IOException e) {
				
				return null;
			}
		    
		}
		
		
		
		/**
		 * This function invokes dialog which warns user about long operation;
		 * take into account that class  PaSpecialDialog has the check box
		 * "don't show me any more" and can't detect that this check box has been
		 * activated before
		 * @param w - width of the current image in instruments window 
		 * @param h - height of the current image in instruments window 
		 * @param parent - parent of the dialog which will be shown
		 * @return the answer on the question about long operation
		 */
		static public int askAboutLongOperation(int w, int h, JFrame parent) {
			 if(w > 699 || h > 699) {
					
				 PaSpecialDialog dialog = new PaSpecialDialog(parent, DialogType.YES_NO_OPTION, getMessagesStrs("messageAnswerCaption"), 
						 getMessagesStrs("potentialLongOperationMessage"),true, 
						 getGuiStrs("longOperationCheckBoxTooltip"), 0);
				 
				 dialog.setVisible(true);
				 
				 int n =  dialog.getCloseFlag();
				 
				 if( n == JOptionPane.NO_OPTION) {
					 
					 writeLog("User: refuse from a long operation", null,true, false, false );
				 }
				 else {
					 
					 writeLog("User: confirmation of  long operation", null,true, false, false ); 
				 }
				
				return n;
				
			 }
			 return JOptionPane.YES_OPTION;
		}
		/**
		 * Sets the font for all components inside the component "comp"
		 * @param comp - top of the tree
		 * @param f - font to set
		 */
		public static void setComponentsFont ( Component comp, Font f )
		{
		    comp.setFont(f);
		    
		    if (comp instanceof Container)
		    {
		        for (Component child : ( (Container)comp ).getComponents () )
		        {
		        	setComponentsFont( child, f );
		        }
		    }
		}
		/**
		 * !!!! the using of this function is only for case when image with id is in CURRENT CONTAINER
		 * @param id - id of image
		 * @return the full path to the boost image with id
		 */
		public String getFullPathToBoostImage(int id) {
			
			PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
			
			String fileName = new String( new Integer(id).toString());
			
			return  concatPathName( PaUtils.get().getPathToBoostIcons(cont), fileName+".jpeg");
			
		}
		
		/**
		 * 
		 * @return a special boost image for video files
		 */
		public BufferedImage specialImageForVideo() {
			
			if(m_videoImage == null) {
				
				try {
					
					m_videoImage = ImageIO.read(new File(getIconsPath() + "vboost.png"));
					
				} catch (IOException e) {
					
					writeLog("Can't read vboost.png file", null,true, false, false );
					
					m_videoImage = new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
				}
			}
			return m_videoImage;
			
		}
		/**
		 * 
		 * @return a special boost image for nonreadable image files
		 */
		public BufferedImage getNonValidImage() {
			
			if(m_nonValidImage == null) {
				
				try {
					
					m_nonValidImage = ImageIO.read(new File(getIconsPath() + "nonvalid.png"));
					
				} catch (IOException e) {
					
					writeLog("Can't read vboost.png file", null,true, false, false );
					
					m_nonValidImage = new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
				}
			}
			return m_nonValidImage;
			
		}
		
		/**
		 * 
		 * @param ext - file extension
		 * @return return true if extension is one of video extension
		 */
		public boolean isVideoExtension(String ext) {
			
			for(String s : m_videoExtensions) {
				
				if(ext.equals(s)) { return true; }
			}
			
			return false;
			
		}
		/**
		 * 
		 * @param ext - file extension
		 * @return return true if extension is one of image extension
		 */
		public boolean isImageExtension(String ext) {
			
			for(String s : m_imageExtensions) {
				
				if(ext.equals(s)) { return true; }
			}
			
			return false;
			
		}
		/**
		 * Returns the boost image for the image im or null
		 * @param im - image object 
		 * @param cont - container where the image object is
		 * @return  the boost image for the image im or null
		 */
		public Image getBoostImageForId(PaImage im, PaImageContainer cont) {
			
			Image ic = null;
			
			String fileName = Integer.toString(im.getId());
			
			String fullPath = concatPathName( PaUtils.get().getPathToBoostIcons(cont), fileName+".jpeg");
			
			try {
			
				File outFile = new File(fullPath);
				
				if ( outFile.exists() ) {
								
					ic = ImageIO.read(outFile);
			
				}
				else {
					
					outFile = new File(im.getFullPath());
					
					ic = ImageIO.read(outFile);
				
				}
				
				return ic;
			
			} catch (IOException e) {
				
				writeLog("IOException :  can't read image file" + NEXT_ROW, e, true, false, true);
				
				return null;			
			}	
			
		}
		
		
		public static boolean isFileExists(String filePathString) {
			
			if(filePathString == null) return false; 
			
			Path path = Paths.get(filePathString);

			if (Files.exists(path)) {
				
			   return true;
			}

			return false;
			
		}	
}
