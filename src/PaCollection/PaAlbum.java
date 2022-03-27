package PaCollection;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import PaGlobal.PaUtils;
import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.*;
import PaImage.PaBufImageBuilder;

/**
 * The main class to represent an album
 * @author avd
 *
 */
public class PaAlbum implements Comparable<PaAlbum>{
	
	private static int nextID_albom = 1; // Indices in PaAlbomsTabModel  will start with 1.
	
	private int m_id;
	
	private String m_name;
	
	private String m_coment;
	
	private String m_path;
	
	private Date m_date = null;
	
	private String m_folderName;
	
	private int m_parent_id;
	
	private ImageIcon _icon = new ImageIcon();
	
	public PaAlbum() {
		
		m_name = null;
		
		m_coment = null;
		
		m_path = null;
		
		m_date = new Date();
		
		m_parent_id = PaUtils.ALBUM_TOP_PARENT_ID;
		
		initAlbumIcon();
	}
	public PaAlbum (String name_alb, String coment_alb, 
				String path_alb, Date date_alb, String folderName) {
		
		m_name = name_alb;
		
		m_coment = coment_alb;
		
		m_path = path_alb;
		
		m_date = date_alb;
		
		m_folderName = folderName;
		
		m_parent_id = PaUtils.ALBUM_TOP_PARENT_ID;
		 
		initAlbumIcon();
	}
	public PaAlbum (String name_alb, String coment_alb, 
			String path_alb, Date date_alb,String folderName, int id_alb ) {		
		
		m_name = name_alb;
		
		m_coment = coment_alb;
		
		m_path = path_alb;
		
		m_date = date_alb;
		
		m_id = id_alb;
		
		m_folderName = folderName;
		
		 m_parent_id = PaUtils.ALBUM_TOP_PARENT_ID;
		
		initAlbumIcon();
	}

	/**
	 * 
	 * @return the next value of next album id and iterates it
	 */
	public static int getNextIdAndIterate () {
		return nextID_albom++;
	}
	/**
	 * Never use this function to get next id , then + 1 and create NEW album. Use for this task
	 * the function getNextIdAndIterate(). Function getNextId() can be used for information and saving
	 * purpose only.
	 * @return the current value of next id
	 */
	public static int getNextId() {
		
		return nextID_albom;
	}
	
	/**
	 * This function is used for initialization of nextID_albom from xml file only
	 * @param lastId - id value
	 */
	public static void setInitialNextId (int lastId) {
		
		nextID_albom = lastId;
	}
	
	public int getId() {
		
		return m_id;
	}
	
	public void setId(int id) {
		
		m_id = id;
	}
	
	public String getName() {
		
		return m_name;
	}
	
	public String getComment() {
		
		return m_coment;
	}
	
	/**
	 * 
	 * @return the full path to the standard folder of this album
	 */
	public String getFullStandardPath() {
		
		return m_path +m_folderName;
	}
	
	public String getStandardPath() {
		
		return m_path;
	}
	
	
	public  ImageIcon  getIcon() {
		
		return _icon;
	}
	
	public String getDateAsString (String format) {
				
		return dateToString(m_date, format);
		
	}
	
	public Date getDate () {
		
		return m_date;
	}
	

	public void setName(String name_albom) {
		
		m_name = name_albom;
	}
	
	public void setComent(String coment_albom) {
		
		m_coment = coment_albom;
	}
	
	public void setPath(String path_albom) {
		
		m_path = path_albom;
	}
	
	public void setDate(Date date_albom) {
		
		m_date = date_albom;
	}
	
	
	public void setFolderName(String name) {
		
		m_folderName = name;
	}
	
	/**
	 * Sets the standard folder name for current id
	 */
	public void setFolderNameForId() {
		
		m_folderName = PaUtils.getNewAlbumFolderName(m_id);
	}
	
	public String getFolderName() {
		
		return m_folderName;
	}

	public int compareTo(PaAlbum pa_alb) {
		
		return m_name.compareToIgnoreCase(pa_alb.m_name);
	}
	
	
	public void setRootPathAndFolder (String fullPath) 
	{	
		
		String rootPath= PaUtils.get().getPathFromString(fullPath) ;
		
		String folderName = PaUtils.getFileNameFromString(fullPath );
		
		
		m_path = rootPath;
		
		m_folderName = folderName;
		
	}
	
	
	private void initAlbumIcon() {
		
				
		String fullPath = concatPathName(PaUtils.get().getPathToAlbumsIcons(), 
				getIconFileName());
					
		File f = new File(fullPath);
		
		if ( f.exists() ) {
			
			_icon = new ImageIcon(fullPath);
		}
		else {
			
			String defaultPath = concatPathName(PaUtils.get().getIconsPath () , 
					PaUtils.get().getDefaultAlbomsIconName());
			
			File f1 = new File( defaultPath  );
			
			if ( f1.exists() ) {
				
				_icon = new ImageIcon( defaultPath );
			}
		}
	}
	
	
	public void setAlbomIcon(String path)  {
		
		BufferedImage im = null;
		
		try {
			
			im = ImageIO.read(new File(path));
	
			Image newImage = im.getScaledInstance(30, 30, 0) ;
		
			String fullPath = concatPathName(PaUtils.get().getPathToAlbumsIcons(), 
				getIconFileName());
		
			PaBufImageBuilder b = new PaBufImageBuilder();
			
			BufferedImage bImage = b.bufferImage( newImage );
		
			ImageIO.write(bImage, "png",new File(fullPath));
			
		} catch (IOException e) {
			
			writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
		}
		
	
		initAlbumIcon();
		
	}
	
	public String getIconFileName() {
		
		return "paalbomicon" +  m_id +".png";
	}

	public boolean isImageFileExists(String fileName) {
		
		File f =new File(concatPathName(getFullStandardPath (),fileName));
		
		return f.exists();
		
	}
	
	
	 public int getParentId() { return m_parent_id; }
	 
	 public void setParentId(int i) { m_parent_id = i; }
		

}
