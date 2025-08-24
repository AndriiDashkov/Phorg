
package pacollection;

import static paglobal.PaUtils.*;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;
import javax.swing.ImageIcon;

import paglobal.PaUtils;


/**
 * The main container class to hold the properties of a single image
 * @author Andrii Dashkov
 *
 */
public class PaImage implements Comparable<PaImage> {
	
	private int m_id;	
	
	private String m_name;	
	
	private String m_path;		
	
	private ArrayList<Integer> m_keywords;
	
	private Date m_date;		
	
	private int m_width;	
	
	private int m_height;	
	
	private int m_scale;
	
	private ImageIcon m_photoImageIcon;
	
	private boolean m_visible;
	
	private boolean m_printed;
	
	private boolean m_bookmarked;
	
	private boolean m_link;
	
	private String m_comments;
	
	private int m_sortid;
	
	private boolean m_sliderVisible;
	
	/**
	 * This id's are only for auxiliary purpose; never rely on it's value
	 * They usually are used to transfer additional data to the filter view
	 */
	private int m_auxiliaryId = -1;
	
	private int m_albumId = -1;
	

	public PaImage() {
		
		m_id = PaMainConainer.set_nextIdPlus();
		
		m_sortid = m_id+PaUtils.get().getCustomSortIdStep();
		
		m_name = null;
		
		m_path = null;
		
		m_keywords = new ArrayList<Integer>();
		
		m_date = new Date();
		
		m_width = 0;
		
		m_height = 0;
		
		m_scale = 0;
		
		m_photoImageIcon = null;
		
		m_visible = true;
		
		m_printed = false;
		
		m_bookmarked = false;	
		
		m_comments = new String();
		
		m_link = true;
		
		m_sliderVisible = true;
	}
	/**
	 * Copy constructor
	 * @param p other image object
	 */
	public PaImage(PaImage p) {
		
		m_id = PaMainConainer.set_nextIdPlus();
		
		m_sortid = m_id+PaUtils.get().getCustomSortIdStep();
		
		m_name = p.getName();
		
		m_path = p.getFullPath();
		
		m_keywords = new ArrayList<Integer>();
		
		Iterator<Integer> it = p.getSubjectsList().iterator();
		
		while (it.hasNext()) {
			
			m_keywords.add(it.next());
		}
			
		m_date = p.getDate();
		
		m_width = p.getWidth();
		
		m_height = p.getHeight();
		
		m_scale = p.get_scale();
		
		m_photoImageIcon = p.getImageIcon();
		
		m_visible = p.isVisible();
		
		m_printed = p.isPrinted();
		
		m_bookmarked = p.isBookmarked();
		
		m_comments = p.getComments();
		
		m_link = true;
		
		m_sliderVisible = true;
	}
	
	public PaImage(String name, String path,
			ArrayList<Integer> keywords, Date date) { 
		this();
	
		m_name = name;
		
		m_path = path;
		
		m_keywords = keywords;
		
		m_date = date;
		
		ImageIcon icon = new ImageIcon(m_path);
		
		m_width = icon.getIconWidth();
		
		m_height = icon.getIconHeight();

	}

	public PaImage (int id, String name, String path,
			ArrayList<Integer> keywords, Date date) {
		
		this();
		
		m_id = id;
		
		m_name = name;
		
		m_path = path;
		
		m_keywords = keywords;
		
		m_date = date;
		
		ImageIcon icon = new ImageIcon(m_path);
		
		m_photoImageIcon =null;
		
		m_width = icon.getIconWidth();
		
		m_height = icon.getIconHeight();

	}

	public PaImage (int id, String name, String path,
			ArrayList<Integer> keywords, Date date, int width, int height) {
		
		this();
		
		m_id = id;
		
		m_name = name;
		
		m_path = path;
		
		m_keywords = keywords;
		
		m_date = date;
		
		m_width = width;
		
		m_height = height;

	}

	public boolean isLink() {
		
		return m_link;
	}
	
	public void setLink(boolean flag) {
		
		this.m_link = flag;
	}
	
	
	public boolean isPrinted() {
		
		return m_printed;
	}
	
	public void setPrinted(boolean flag) {
		
		this.m_printed = flag;
	}
	
	public boolean isBookmarked() {
		
		return m_bookmarked;
	}
	
	public void setBookmarked(boolean flag) {
		
		this.m_bookmarked = flag;
	}
	
	public String getComments() {
		
		return m_comments;
	}
	
	public void setComments( String comments ) {
		
		this.m_comments = comments;
	}
	
	
	
	public boolean isVisible() {
		
		return m_visible;
	}

	public void setVisible(boolean _visible) {
		
		this.m_visible = _visible;
	}
	
	public int get_scale() {
		
		return m_scale;
	}

	public void set_scale(int _scale) {
		
		this.m_scale = _scale;
	}
	
	public void setImageIcon(Image photoImage) {
		
		this.m_photoImageIcon = new ImageIcon(photoImage);
	}
	
	
	public void set_imageNull () {
		
		m_photoImageIcon = null;
	}

	public ImageIcon getImageIcon() {
		
		return m_photoImageIcon;
	}
	
	public PaSubject get_tem (int Id) {
		
		PaSubjectContainer temContainer = PaUtils.get().getSubjectsContainer();
		
		Iterator<PaSubject> iter = temContainer.get_tems().iterator();
		
		PaSubject x;
		
		while (iter.hasNext()) {
			
			x = iter.next();
			
			if (x.getId() == Id) {
				
				return x;
			}
		}
		
		return null;
	}
	
	public int getId() {
		
		return m_id;
	}
	
	public int getSortId() {
		
		return m_sortid;
	}
	
	public void setSortId(int id) {
		
		m_sortid = id;
	}
	
	public String getName() {
		
		return m_name;
	}

	public void setName(String name) {
		
		m_name = name;
	}

	public String getFullPath() {
		
		return m_path;
	}

	public void setFullPath(String path) {
		
		m_path = path;
	}

	
	public ArrayList<Integer> getSubjectsList() {
		
		return m_keywords;
	}

	public void setSubjectsList(ArrayList<Integer> keywords) {
		
		m_keywords = keywords;
	}

	public Date getDate() {

		return m_date;
	}
	
	public String getDateAsString (String format) {
		
			
		 return dateToString(m_date,format);
	
	}

	public void setDate(Date date) {
		
		m_date = date;
	}
	
	public int getWidth() {
		
		return m_width;
	}

	public void setWidth(int width) {
		
		m_width = width;
	}
	
	public int getHeight() {
		
		return m_height;
	}

	public void setHeight(int height) {
		
		m_height = height;
	}

	public int getKey(int i) {
		
		return (Integer) m_keywords.get(i);
	}
	

	
	/**
	 * <p>Insert subject id (key) to this image</p>
	 * @param idSub - id of the subject to be inserted 
	 */
	public boolean addKey(int idSub) {
		
		//control if the key is in the container already
		if(m_keywords.indexOf(idSub) == -1) {  
			
			m_keywords.add(idSub);  
			
			return true;
			
		} else {
			
			return false;
		}
	}

	public int findKey(int number) {
		
		Integer nm = number;
		
		return m_keywords.indexOf(nm);
	}
	

	public boolean removeKey(int number) {
		
		Integer nm = number;
		
		int rk = m_keywords.indexOf(nm);
		
		if(rk != -1) {
			
			m_keywords.remove(rk);
			
			return true;
			
		} else {
			
			return false;
		}
	}	


	public int size_keywords() {
		
		return m_keywords.size();
	}

	static <T, V extends T> boolean is_keywords(T x, V[] y) {
		
		for (int i=0; i < y.length; i++)
			
			if (x.equals(y[i])) {
				
				return true;
			}
		
		return false;
	}

	public int compareTo(PaImage photo) {

		return m_name.compareToIgnoreCase(photo.m_name);
	}
	
	/**
	 * <p>Changes the path for this image, makes copy of the file into new destination.</p>
	 * @param newPath - new full path 
	 */
	public void changePath(String newPath) throws IOException,SecurityException
	{
			String fileName = PaUtils.getFileNameFromString(m_path); 
		
			String oldPathFullname = new String(m_path);
			
			fileName = checkUniqueFileName(newPath, fileName);
			
			m_path = concatPathName(newPath, fileName); 
			
			FileChannel source = null;
			
			FileChannel destination = null;
			
			FileInputStream fileInputStream = null;
			
			FileOutputStream fileOutputStream = null;
			
			try {
				
				fileInputStream = new FileInputStream(oldPathFullname);
				
				source = fileInputStream.getChannel();
				
				fileOutputStream = new FileOutputStream(m_path);
				
				destination = fileOutputStream.getChannel();
				
				destination.transferFrom(source, 0, source.size());
			}
			finally {	
				
				if(source != null){
					
					source.close();
					
				}
				if(destination != null) {
					
					destination.close();
				}
				
				fileInputStream.close();
				
				fileOutputStream.close();
				
			}						
	}
	

	/**
	 * <p>Changes the path for this image, makes copy of the file into new destination, but don't make changes if 
	 * the image is not in the standard folder</p>
	 * @param newPath - new full path 
	 * @param oldAlbomStandartPath - old standard path for the albom
	 */
	public void changePathWithOldPathCheck(String newPath,String oldAlbomStandartPath) throws IOException,SecurityException
	{
			
		String oldPhotoPath = PaUtils.get().getPathFromString( m_path);
			
		if ( isSamePathes(oldPhotoPath, oldAlbomStandartPath ) ) {
			
			changePath(newPath);			
		}				
	}
	
	public PaImage cloneData() {
		
		PaImage p = new PaImage(this);
		
		p.m_id = m_id;
		
		p.m_sortid = m_sortid;
		
		p.m_name = new String(m_name);
		
		p.m_path = new String(m_path);
		
		p.m_date = new Date(m_date.getTime());
		
		if(m_photoImageIcon != null) {
			
			p.setImageIcon(m_photoImageIcon.getImage());
		}

		p.m_comments = new String(m_comments);
		
		p.m_link = m_link;
		
		return p;
	}
	/**
	 * 
	 * @return the true if this image should be visible in slider
	 */
	public boolean isVisibleInSlider() { return m_sliderVisible; }
	
	/**
	 * 
	 * @param flag - flag of visibility in slider window
	 */
	public void setVisibleInSlider(boolean flag) {	m_sliderVisible = flag; }
	
	/**
	 * Sets the auxiliary id; never rely on this id; it's temporary, only to remember real id in some cases 
	 * @param id - id number for auxiliary purpose
	 */
	public void setAuxiliaryId(int id) { m_auxiliaryId = id; }
	
	public int getAuxiliaryId() { return m_auxiliaryId; }
	
	public void setAuxiliaryAlbumId(int id) { m_albumId = id; }
	
	public int getAuxiliaryAlbumId() { return m_albumId; }

}
	