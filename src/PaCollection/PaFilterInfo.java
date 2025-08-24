/**
 * 
 */
package pacollection;

import static paglobal.PaUtils.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Andrii Dashkov
 *<p>The class is intend for saving filter information and it is used in PaImageContainer</p>
 */
public class PaFilterInfo {
	
	ArrayList<PaSubject> m_list = null;
	
	Date m_dateFrom = null;
	
	Date m_dateTo = null;
	
	//ignoring flags
	boolean m_ignoreSubjects = true;
	
	boolean m_ignoreBookmark = true;
	
	boolean m_ignorePrinted = true;
	
	boolean m_ignoreDates = false;
	
	boolean m_ignoreLinks = true;
	
	boolean m_ignoreSlide = true;
	
	boolean m_linksActive = true;
	
	boolean m_bookmarkActive = true;
	
	boolean m_printedActive = true;
	/**
	 * <p>No setters for this class - setting of members only using constructor<p>
	 * @param list - list of subjects which are in the filter
	 */
	public PaFilterInfo(ArrayList<PaSubject> list, Date from, Date to, boolean ignoreSubjects,
			boolean ignoreBook, boolean ignorePrint, boolean ignoreDate, boolean actBook,
			boolean actPrint,boolean ignLinks, boolean actLink) 
	{
		m_ignoreSubjects = ignoreSubjects;
		
		m_ignoreBookmark = ignoreBook;
		
		m_ignorePrinted = ignorePrint;
		
		m_ignoreDates = ignoreDate;
		
		m_bookmarkActive = actBook;
		
		m_printedActive = actPrint;
		
		m_ignoreLinks = ignLinks;
		
		m_linksActive = actLink;
		
		if( list != null ){
			
			m_list = new ArrayList<PaSubject>();
			
			for(PaSubject t: list) {
				
				PaSubject tNew = new PaSubject(t);
				
				m_list.add(tNew);
				
			}
		}
		
		if(from != null && to != null) {	
			
			m_dateFrom = from;
			
			m_dateTo = to;
		}
	}
	
	/**
	 * Copy constructor
	 * @param rhs
	 */
	public PaFilterInfo(PaFilterInfo rhs) 
	{
		this(rhs.getSubjectsList(),rhs.m_dateFrom,rhs.m_dateTo, rhs.m_ignoreSubjects,
				rhs.m_ignoreBookmark, rhs.m_ignorePrinted, rhs.m_ignoreDates, rhs.m_bookmarkActive,
				rhs.m_printedActive, rhs.m_ignoreLinks, rhs.m_linksActive);

	}
	
	public PaFilterInfo() 
	{
		m_dateTo = new Date();
		
		Calendar c = Calendar.getInstance();
		
		c.set(1900,0,1);
		
		m_dateFrom = c.getTime();
		
		m_list = new ArrayList<PaSubject>();

	}
	/**
	 * @return start date, the value must be checked for null
	 */
	public Date getDateFrom() { return m_dateFrom; }
	
	/**
	 * @return end date, the value must be checked for null
	 */
	public Date getDateTo() { return m_dateTo; }
	
	/**
	 * @return list of subjects in the filter; the value must be checked for null
	 */
	public ArrayList<PaSubject> getSubjectsList() { return  m_list; }
	
	/**
	 * @return true if subjects filter condition must be not used for filtering
	 */
	public boolean isSubjectsIgnored() { return  m_ignoreSubjects; }
	
	public boolean isBookmarkedIgnored() { return  m_ignoreBookmark; }
	
	public boolean isPrintedIgnored() { return  m_ignorePrinted; }
	
	public boolean isDatesIgnored() { return  m_ignoreDates; }

	public boolean isBookmarkActiveSelected() { return  m_bookmarkActive; }
	
	public boolean isPrintedActiveSelected() { return  m_printedActive; }
	
	public boolean isLinksIgnored() { return  m_ignoreLinks; }
	
	public boolean isLinksActiveSelected() { return  m_linksActive; }
	/**
	 * @return data for filter in the string form
	 */
	public String toString() {
		
		String s = " Filter info: date from = " + " " + dateToString(m_dateFrom,DATE_FORMAT) + 
				" date to = " + dateToString(m_dateTo,DATE_FORMAT) + NEXT_ROW + "Subjects :"+ NEXT_ROW;
		
		for(PaSubject t: m_list) {
			
			 s += t.getName()+ NEXT_ROW;
		}
		
		s += "is subject ignored : " + m_ignoreSubjects;
		
		return s;
	}
	/**
	 * 
	 * @return true if all parameters are ignored; (no sence to use filter in this case)
	 */
	public boolean isAllIgnored() {
		
		return 	m_ignoreSubjects &&
				m_ignoreBookmark &&
				m_ignorePrinted &&
				m_ignoreDates &&
				m_ignoreLinks &&
				m_ignoreSlide;
		
	}

}
