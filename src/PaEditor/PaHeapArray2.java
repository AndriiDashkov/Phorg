
package PaEditor;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import java.lang.reflect.Field;


/**
 * This class can allocated memory beyond the scope of JVM proccess. It is used for
 * creation of an extremly large arrays . This array is 2 dimensional and can set and get the values
 * of short,float and double type. The generic can work with three types only - Double,Float and Short
 * @author avd
 *
 */
public  class PaHeapArray2{
	
	/**
	 * Initial address of the allocated memory 
	 */
	private long m_address;
	/**
	 * Number of rows
	 */
	int m_rows;
	
	/**
	 * Number of columns
	 */
	int m_columns;
	
	/**
	 * set to true if the memory has not been allocated
	 */
	boolean m_notAllocated = false;
	
	private int BYTE_SIZE = 2;//the size of sort type

	/**
	 * 
	 * @return the Unsafe object which can allocated memory on the heap 
	 */
	@SuppressWarnings("restriction")
	private sun.misc.Unsafe getUnsafe() {
		
		try {
			
			Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			
			f.setAccessible(true);
			
			return (sun.misc.Unsafe) f.get(null);
			
		} catch (Exception e) {
			
			writeLog("Unsafe allocation:  error" + NEXT_ROW, e, true, false, true);
			
			m_notAllocated = true;
			
			return null;
		}
	}

	/**
	 * Constructor
	 * @param row - number of rows in the array
	 * @param col - number of columns in the array
	 */
	@SuppressWarnings("restriction")
	public PaHeapArray2(int row, int col) {
		
		BYTE_SIZE = 8;//double
		
		m_address = getUnsafe().allocateMemory(row*col* BYTE_SIZE);
	}
	
	/**
	 * 
	 * @return true if the manual allocation of the memory was not succesful
	 */
	public boolean isNullAllocated() { return m_notAllocated; }
	
	/**
	 * Inits the array
	 * @param initVal - value to set for all arrays's elements
	 */
	//@SuppressWarnings("unused")
	public void init(double initVal) {
		
		if(!isNullAllocated()) {
			
			for(int i = 0; i < m_rows; ++i) {
				
				for(int j = 0; j < m_columns; ++j) {
					
					set(i, j, initVal);
				}
			}
		}
	}
	
	/**
	 * Sets the value for specific element with indicies row,column
	 * @param row  - row index
	 * @param col - column index
	 * @param val - value to set
	 */
	@SuppressWarnings("restriction")
	public void set(int row, int col, double val) {
		
		getUnsafe().putDouble(m_address + (row*m_columns+col) * BYTE_SIZE, val);
		 
	}
	
	/**
	 * Getter 
	 * @param row  - row index
	 * @param col - column index
	 * @return
	 */
	@SuppressWarnings({ "restriction" })
	public double get(int row, int col){
		
	return getUnsafe().getDouble(m_address + (row*m_columns+col) * BYTE_SIZE);
		 
	}
	
	/**
	 * Controls the input indicies
	 * @param row - input index of row
	 * @param col - input index of column
	 * @return false if the one if idicies is beyond limits
	 */
	@SuppressWarnings("unused")
	private boolean rangeControl(int row, int col) {
		
		if(row >= m_rows || col >= m_columns) return false;
		
		return true;
	}
	
	/**
	 * Frees the allocated memory
	 */
	@SuppressWarnings("restriction")
	public void free() {
		
		getUnsafe().freeMemory(m_address);
	}
}
