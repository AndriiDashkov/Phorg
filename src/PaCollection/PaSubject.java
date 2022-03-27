package PaCollection;
/** 
 * This class is a representative of single subject
 * Subject can be insert into image properties; the link is made through the  id
 * @author avd
 */
public class PaSubject implements Comparable<PaSubject> {

	private static int nextID_tem = 0; 
	
	private int _id_tem;
	
	private String _name_tem;	
	

	public PaSubject () {
		
		_name_tem = null;
	}
	
	public PaSubject (String name_tem) {
		
		_name_tem = name_tem;
	}
	
	public PaSubject (int id_tem, String name_tem) {
		
		_id_tem = id_tem;
		
		_name_tem = name_tem;
	}
	
	/**
	 * @param t - subject object for copy constructor
	 *<p>Copy constructor</p>
	 */
	public PaSubject(PaSubject t) {
		
		_id_tem = t.getId();
		
		_name_tem = t.getName();
	}

	public void set_id_tem () {
		
		nextID_tem++;
		
		_id_tem = nextID_tem;		
	}
	
	public static int get_nextId () {
		
		return nextID_tem;
	}
	
	public static void set_load_nextId (int lastId) {
		
		nextID_tem = lastId;
	}
	
	public int getId() {
		
		return _id_tem;
	}
	
	public String getName() {
		
		return _name_tem;
	}
	
	public void setName(String name_tem) {
		
		_name_tem = name_tem;
	}
	

	public int compareTo(PaSubject tema) {
		
		return _name_tem.compareToIgnoreCase(tema.getName());
	}

}
