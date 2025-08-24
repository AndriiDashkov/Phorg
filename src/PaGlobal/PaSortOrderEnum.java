
package paglobal;

import static paglobal.PaUtils.*;

/**
 * <p>This enum represents the sorting order of images in albom.</p>
 * @value DATE_ORDER - sorting order by date of image 
 * @value ID_ORDER - native order by unique id of an image (this is also insertion order of images)
 * @value CUSTOM_ORDER - custom sorting order, in this order an user can reorder images in desirable way using drag and drop
 * @value BOOOMAKED_ORDER - in this sorting order the images which are bookmarked are always in the head
 * @value PRINTED_ORDER - in this sorting order the images which were printed are always in the head
 * @author Andrii Dashkov
 *
 */
public enum PaSortOrderEnum {
	
	DATE_ORDER,
	ID_ORDER,
	CUSTOM_ORDER,
	PRINTED_ORDER,
	BOOOMAKED_ORDER,
	LINKS_ORDER,
	ST_FOLDER_ORDER,
	NO_ORDER;
	
	public static int toInt(PaSortOrderEnum v)
	{
		switch(v){
			case DATE_ORDER: 		return 0;
			case ID_ORDER:			return 1;
			case CUSTOM_ORDER:		return 2;
			case PRINTED_ORDER:		return 3;
			case BOOOMAKED_ORDER:	return 4;
			case LINKS_ORDER:       return 5;
			case ST_FOLDER_ORDER:   return 6;
			case NO_ORDER:
			default: 				return -1;
		}
		
	}
	
	public static PaSortOrderEnum fromInt(int  v)
	{
		switch(v){
			case 0:  return DATE_ORDER;
			case 1:  return ID_ORDER;
			case 2:  return CUSTOM_ORDER;
			case 3:  return PRINTED_ORDER;
			case 4:  return BOOOMAKED_ORDER;
			case 5:  return LINKS_ORDER;
			case 6:  return ST_FOLDER_ORDER;
			default: return NO_ORDER;
		}
		
	}
	

	/**
	 * @return converts enum index to string 
	 * @param enum index
	 */
	public static String toString(PaSortOrderEnum v) {
		
		switch(v) {
			case DATE_ORDER:      return getGuiStrs("dateSortString");
			case ID_ORDER:        return getGuiStrs("idSortString");
			case CUSTOM_ORDER:    return getGuiStrs("customSortString");			
			case PRINTED_ORDER:	  return getGuiStrs("printedSortString");
			case BOOOMAKED_ORDER: return getGuiStrs("bookmarkedSortString");
			case LINKS_ORDER:     return getGuiStrs("linkSortString");
			case ST_FOLDER_ORDER: return getGuiStrs("standardSortString");
			default: 			  return "";
				
		}
		
	}
	/**
	 * 
	 * @param index - index to check
	 * @return true if the index is a custom order sort
	 */
	public static boolean isCustomOrder(int index) {
		
		return fromInt(index) == CUSTOM_ORDER;
		
	}
}
