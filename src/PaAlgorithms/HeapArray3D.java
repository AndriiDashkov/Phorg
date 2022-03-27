
package PaAlgorithms;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import java.lang.reflect.Field;

/**
 * @author Andrey Dashkov
 *
 */


/**
 * This class can allocated memory beyond the scope of JVM procces. It is used for
 * creation of an extremely large arrays . This array is 2 dimensional and can set and get the values
 * of short,float and double type. The generic can work with three types only - Double,Float and Short
 * @author avd
 *
 */
public  class HeapArray3D<T  extends Number> {
	/**
	 * Initial adress of the allocated memory 
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
	
	int m_z;
	/**
	 * set to true if the memory has not been allocated
	 */
	boolean m_notAllocated = false;
	
	private int BYTE_SIZE = 2;//the size of sort type

	private T m_type = null;//just for type detection
	

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
	public HeapArray3D(int z, int row, int col) {
		
		if(m_type instanceof Short)  { BYTE_SIZE = 2; }
		
		else if(m_type instanceof Short) {BYTE_SIZE = 4;}//float
		
		else {BYTE_SIZE = 8;}//double
		
		m_rows = row;
		
		m_columns = col;
		
		m_z = z;
		
		m_address = getUnsafe().allocateMemory(z*row*col*BYTE_SIZE);
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
	public void init(T initVal) {
		
		if(!isNullAllocated()) {
			
			for(int z = 0; z < m_z; ++z) 
				
			for(int i = 0; i < m_rows; ++i) {
				
				for(int j = 0; j < m_columns; ++j) {
					
					set(z, i, j, initVal);
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
	public void set(int z, int row, int col, T val) throws IndexOutOfBoundsException {
		
		if(!rangeControl(z, row, col)) {
			
			throw new IndexOutOfBoundsException();
		}
		
		if(m_type instanceof Short) {
			
			getUnsafe().putShort(m_address + (z*m_columns*m_rows + row*m_columns+col) * BYTE_SIZE, val.shortValue());
		}
		else 
		if(m_type instanceof Float) {
			
			getUnsafe().putFloat(m_address + (z*m_columns*m_rows+row*m_columns+col) * BYTE_SIZE, val.floatValue());
		}
		else {
			getUnsafe().putDouble(m_address + (z*m_columns*m_rows + row*m_columns+col) * BYTE_SIZE, val.doubleValue());
		} 
	}
	
	/**
	 * Getter 
	 * @param row  - row index
	 * @param col - column index
	 * @return
	 */
	@SuppressWarnings({ "restriction", "unchecked" })
	public T get(int z, int row, int col) throws IndexOutOfBoundsException {
		
		if(!rangeControl(z, row, col)) {
			
			throw new IndexOutOfBoundsException();
		}
		
		if(m_type instanceof Short) {
			
			return (T)new Short(getUnsafe().getShort(m_address + (z*m_columns*m_rows + row*m_columns+col) * BYTE_SIZE));
		}
		else if(m_type instanceof Float) {
			
			return (T)new Float(getUnsafe().getFloat(m_address + (z*m_columns*m_rows + row*m_columns+col) * BYTE_SIZE));
			
		} else {
			
			return (T)new Double(getUnsafe().getDouble(m_address + (z*m_columns*m_rows + row*m_columns+col) * BYTE_SIZE));
		} 
	}
	
	/**
	 * Controls the input indices
	 * @param row - input index of row
	 * @param col - input index of column
	 * @return false if the one if idiocies is beyond limits
	 */
	private boolean rangeControl(int z, int row, int col) {
		
		if(row >= m_rows || col >= m_columns || z >= m_z) return false;
		
		return true;
	}
	
	/**
	 * Frees the allocated memory
	 */
	@SuppressWarnings("restriction")
	public void free() {
		
		getUnsafe().freeMemory(m_address);
	}
	
	public int getWidth() { return m_columns;}
	
	public int getHeight() { return m_rows;}
}
